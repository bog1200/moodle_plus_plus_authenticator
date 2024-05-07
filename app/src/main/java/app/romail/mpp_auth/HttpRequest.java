package app.romail.mpp_auth;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpRequest {
    public static JSONObject getRequest(String url) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<JSONObject> result =  executor.submit(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    assert response.body() != null;
                    return new JSONObject(response.body().string());
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });

        try {
            return result.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

