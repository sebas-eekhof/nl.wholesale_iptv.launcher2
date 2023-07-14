package nl.wholesale_iptv.launcher2.helpers;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import nl.wholesale_iptv.launcher2.callbacks.ApiCallback;
import nl.wholesale_iptv.launcher2.enums.RequestMethod;
import nl.wholesale_iptv.launcher2.models.ApiRequest;
import nl.wholesale_iptv.launcher2.models.ApiResponse;
import nl.wholesale_iptv.launcher2.services.FirebaseMessagingService;

public class ApiHelper {
    private final Context context;

    public ApiHelper(Context context) {
        this.context = context;
    }

    public ApiResponse probe() {
        NetworkHelper networkHelper = new NetworkHelper(context);
        JSONObject body = new JSONObject();
        try {
            body.put("mac_secondary", networkHelper.getMACSecondary(null));
            body.put("ethernet_connected", networkHelper.isEthernetConnected());
            body.put("wifi_connected", networkHelper.isWifiConnected());
            body.put("fcm_token", FirebaseMessagingService.getToken(context));
        } catch(JSONException e) {
            e.printStackTrace();
        }

        ApiRequest request = new ApiRequest("/device/probe")
            .setBody(body)
            .setMethod(RequestMethod.POST);
        return request(request);
    }
    
    public ApiResponse getAppVersions() {
        ApiRequest request = new ApiRequest("/apps/versions");
        return request(request);
    }

    public void getAppVersionsAsync(ApiCallback callback) {
        ApiRequest request = new ApiRequest("/apps/versions");
        requestAsync(request, callback);
    }

    public void requestAsync(ApiRequest request, ApiCallback callback) {
        Thread thread = new Thread(() -> {
            ApiResponse response = request(request);
            callback.onResponse(response);
        });
        thread.start();
    }

    public ApiResponse request(ApiRequest request) {
        ApiHelper.disableSSLCertificateChecking();
        ApiResponse apiResponse;

        try {
            DeviceHelper deviceHelper = new DeviceHelper(context);
            NetworkHelper networkHelper = new NetworkHelper(context);

            String mac = networkHelper.getMAC(null);
            String serial = deviceHelper.getSerial();

            URL url = new URL("https://android-l.wholesale-iptv.nl" + request.getPath());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(request.getMethod().toString());
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");
            conn.setRequestProperty("x-mac", mac);
            conn.setRequestProperty("x-serial", serial);
            for(String key: request.getHeaders().keySet())
                conn.setRequestProperty(key, request.getHeaders().get(key));

            conn.connect();

            if(request.getMethod() != RequestMethod.GET && request.getBody() != null) {
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(request.getBody().toString());
                os.flush();
                os.close();
            }

            int status = conn.getResponseCode();

            if(status == 200 || status == 201) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = br.readLine()) != null)
                    sb.append(line);
                br.close();
                String res = sb.toString();
                try {
                    JSONObject data = new JSONObject(res);
                    if(data.has("success") && data.has("error") && data.has("data")) {
                        apiResponse = new ApiResponse(
                            data.getBoolean("success"),
                            data.getBoolean("success") ? null : new ApiResponse.ApiError(
                                data.getJSONObject("error").getInt("code"),
                                data.getJSONObject("error").getString("message")
                            ),
                            data.getBoolean("success") ? (data.isNull("data") ? new JSONObject() : data.getJSONObject("data")) : null
                        );
                    } else
                        apiResponse = ApiResponse.unknownError();
                } catch(JSONException e) {
                    e.printStackTrace();
                    apiResponse = ApiResponse.fromException(e);
                }
            } else
                apiResponse = new ApiResponse(
                    false,
                    new ApiResponse.ApiError(
                        status,
                        conn.getResponseMessage()
                    ),
                    null
                );

            conn.disconnect();
        } catch(IOException e) {
            e.printStackTrace();
            apiResponse = ApiResponse.fromException(e);
        }
        return apiResponse;
    }

    private static void disableSSLCertificateChecking() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        } };

        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
