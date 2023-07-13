package nl.wholesale_iptv.launcher2.models;

import androidx.annotation.Nullable;
import androidx.work.Data;

import org.json.JSONObject;

public class ApiResponse {
    public boolean success;
    @Nullable
    public ApiError error;
    @Nullable
    public JSONObject res;

    public ApiResponse(boolean success, @Nullable ApiError error, @Nullable JSONObject res) {
        this.success = success;
        this.error = error;
        this.res = res;
    }

    public Data toData() {
        Data.Builder builder = new Data.Builder()
            .putBoolean("success", success);
        if(success && res != null)
            builder.putString("res", res.toString());
        if(!success && error != null)
            builder
                .putInt("error_code", error.code)
                .putString("error_message", error.message);
        return builder.build();
    }

    public static ApiResponse unknownError() {
        return new ApiResponse(false, new ApiError(500, "Unknown error"), null);
    }

    public static ApiResponse fromException(Exception e) {
        return new ApiResponse(
            false,
            new ApiError(
                500,
                e.getMessage()
            ),
            null
        );
    }

    public static class ApiError {
        public int code;
        public String message;

        public ApiError(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
