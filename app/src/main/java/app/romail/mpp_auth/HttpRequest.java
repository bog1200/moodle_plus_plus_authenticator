package app.romail.mpp_auth;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.auth0.android.jwt.JWT;


public class HttpRequest {

    private static JWT accessToken;

    public static Long getAccountFromToken(){
        String subject = accessToken.getSubject();
        return Long.parseLong(subject);
    }
    public static JSONObject GetRequest(String url) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<JSONObject> result =  executor.submit(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + accessToken.toString())
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    String responseString = response.body().string();
                    if (responseString.isEmpty()) {
                        return new JSONObject();
                    }
                    return new JSONObject(responseString);
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

    public static boolean IdAuthRequest(String country, String pin) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> result =  executor.submit(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                okhttp3.RequestBody body = okhttp3.RequestBody.create(new JSONObject()
                        .put("country", country)
                        .put("pin", pin)
                        .toString(), okhttp3.MediaType.parse("application/json"));
                Request request = new Request.Builder()
                        .url("https://test-mpp.romail.app:8080/api/v1/account/idLogin")
                        .post(body)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    String responseString = response.body().string();
                    if (responseString.isEmpty()) {
                        return false;
                    }
                    JSONObject key =  new JSONObject(responseString);
                    accessToken = new JWT(key.getString("accessToken"));
                    return true;
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

    public static JSONObject PostRequest(String url, JSONObject data){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<JSONObject> result =  executor.submit(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                okhttp3.RequestBody body = okhttp3.RequestBody.create(data.toString(), okhttp3.MediaType.parse("application/json"));
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + accessToken.toString())
                        .post(body)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    String responseString = response.body().string();
                    if (responseString.isEmpty()) {
                        return new JSONObject();
                    }
                    return new JSONObject(responseString);
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

