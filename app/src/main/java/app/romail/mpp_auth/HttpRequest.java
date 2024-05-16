package app.romail.mpp_auth;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.auth0.android.jwt.JWT;


public class HttpRequest {

    private static final String API_URL = "https://mpp.romail.app/api/v1";


    public static Long getAccountFromToken(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MPP_AUTH", MODE_PRIVATE);
        JWT accessToken = new JWT(sharedPreferences.getString("accessToken", ""));
        String subject = accessToken.getSubject();
        return Long.parseLong(subject);
    }
    public static JSONObject GetRequest(Context context, String url) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MPP_AUTH", MODE_PRIVATE);
        JWT accessToken = new JWT(sharedPreferences.getString("accessToken", ""));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<JSONObject> result =  executor.submit(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(API_URL+url)
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

    public static JSONArray GetRequestArray(Context context, String url) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MPP_AUTH", MODE_PRIVATE);
        JWT accessToken = new JWT(sharedPreferences.getString("accessToken", ""));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<JSONArray> result =  executor.submit(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(API_URL+url)
                        .addHeader("Authorization", "Bearer " + accessToken.toString())
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    String responseString = response.body().string();
                    if (responseString.isEmpty()) {
                        return new JSONArray();
                    }
                    return new JSONArray(responseString);
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

    public static boolean IdAuthRequest(Context context, String country, String pin) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> result =  executor.submit(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                okhttp3.RequestBody body = okhttp3.RequestBody.create(new JSONObject()
                        .put("country", country)
                        .put("pin", pin)
                        .toString(), okhttp3.MediaType.parse("application/json"));
                Request request = new Request.Builder()
                        .url(API_URL+"/account/idLogin")
                        .post(body)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    String responseString = response.body().string();
                    if (responseString.isEmpty()) {
                        return false;
                    }
                    JSONObject key =  new JSONObject(responseString);
                    SharedPreferences sharedPreferences = context.getSharedPreferences("MPP_AUTH", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("accessToken", key.getString("accessToken"));
                    editor.putString("refreshToken", key.getString("refreshToken"));
                    editor.apply();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });

        try {
            Boolean success = result.get();
            if (success) {
                return true;
            }
            return false;


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static boolean refreshToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MPP_AUTH", MODE_PRIVATE);
        JWT refreshToken = new JWT(sharedPreferences.getString("refreshToken", ""));
        if (refreshToken.isExpired(0)) {
            return false;
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> result = executor.submit(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                okhttp3.RequestBody body = okhttp3.RequestBody.create(new JSONObject()
                        .toString(), okhttp3.MediaType.parse("application/json"));
                Request request = new Request.Builder()
                        .url(API_URL+"/account/refresh")
                        .addHeader("Authorization", "Bearer " + refreshToken.toString())
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    String responseString = response.body().string();
                    if (responseString.isEmpty()) {
                        return false;
                    }
                    JSONObject key = new JSONObject(responseString);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("accessToken", key.getString("accessToken"));
                    editor.apply();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });

        try {
            Boolean success = result.get();
            if (success) {
                return true;
            }
            return false;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static JSONObject PostRequest(Context context,String url, JSONObject data){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MPP_AUTH", MODE_PRIVATE);
        JWT accessToken = new JWT(sharedPreferences.getString("accessToken", ""));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<JSONObject> result =  executor.submit(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                okhttp3.RequestBody body = okhttp3.RequestBody.create(data.toString(), okhttp3.MediaType.parse("application/json"));
                Request request = new Request.Builder()
                        .url(API_URL+url)
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

