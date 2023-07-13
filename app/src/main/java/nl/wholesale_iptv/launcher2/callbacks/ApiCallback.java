package nl.wholesale_iptv.launcher2.callbacks;

import nl.wholesale_iptv.launcher2.models.ApiResponse;

public interface ApiCallback {
    void onResponse(ApiResponse response);
}
