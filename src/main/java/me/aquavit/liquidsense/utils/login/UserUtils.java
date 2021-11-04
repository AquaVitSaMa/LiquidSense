package me.aquavit.liquidsense.utils.login;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public final class UserUtils {

    public static boolean isValidTokenOffline(String token) {
        return token.length() >= 32;
    }

    public static boolean isValidToken(String token) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        BasicHeader[] headers = new BasicHeader[]{new BasicHeader("Content-Type", "application/json")};

        HttpPost request = new HttpPost("https://authserver.mojang.com/validate");
        request.setHeaders(headers);

        JSONObject body = new JSONObject();
        body.put("accessToken", token);
        request.setEntity(new StringEntity(body.toString()));

        CloseableHttpResponse response = client.execute(request);

        return response.getStatusLine().getStatusCode() != 204;
    }

    public static String getUsername(String uuid) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet("https://api.mojang.com/user/profiles/" + uuid + "/names");
        CloseableHttpResponse response = client.execute(request);

        if (response.getStatusLine().getStatusCode() != 200) {
            return null;
        }

        String username;
        try {
            JSONArray names = new JSONArray(EntityUtils.toString(response.getEntity()));

            username = new JSONObject(names.get(names.length() - 1).toString()).getString("name");
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }

        return username;
    }

    public static String getUUID(String username) {
        try {
            // Make a http connection to Mojang API and ask for UUID of username
            URLConnection uRLConnection = new URL("https://api.mojang.com/users/profiles/minecraft/" + username).openConnection();

            HttpsURLConnection httpConnection = (HttpsURLConnection)uRLConnection;
            httpConnection.setConnectTimeout(2000);
            httpConnection.setReadTimeout(2000);
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            HttpURLConnection.setFollowRedirects(true);
            httpConnection.setDoOutput(true);

            if (httpConnection.getResponseCode() != 200) {
                return "";
            }

            // Read response content and get id from json
            InputStreamReader it = new InputStreamReader(httpConnection.getInputStream());{
                JsonElement jsonElement = new JsonParser().parse(it);
                if (jsonElement.isJsonObject()) {
                    return jsonElement.getAsJsonObject().get("id").getAsString();
                }
            }

        } catch(Throwable ignored) {
        }

        return "";
    }


}
