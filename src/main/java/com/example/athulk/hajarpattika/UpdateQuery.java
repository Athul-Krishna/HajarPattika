package com.example.athulk.hajarpattika;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import static com.example.athulk.hajarpattika.data.SubjectProvider.LOG_TAG;

public class UpdateQuery {

    private UpdateQuery(){}

    public static UpdateInfo fetchData(String requestUrl){
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try{
            jsonResponse = makeHttpRequest(url);
        }catch(IOException e){
            Log.e(LOG_TAG, "Error making request");
        }
        if(jsonResponse.equals("Site error")){
            UpdateInfo updateInfo = new UpdateInfo(0,"Error retrieving news from the server");
            return updateInfo;
        }
        UpdateInfo updateInfo = extractFeatureFromJson(jsonResponse);
        return updateInfo;
    }

    private static URL createUrl(String stringUrl){
        URL url = null;
        try{
            url = new URL(stringUrl);
        }catch (MalformedURLException e){
            Log.e(LOG_TAG, "Error making url.");
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = "";
        if(url == null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if(urlConnection.getResponseCode()==200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else{
                Log.e(LOG_TAG, "Error response code: "+urlConnection.getResponseCode());
                return "Site error";
            }
        }catch (IOException e){
            Log.e(LOG_TAG,"Error getting response");
        }finally {
            if(inputStream!=null){
                inputStream.close();
            }
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream!=null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line!=null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static UpdateInfo extractFeatureFromJson(String versionJson){
        double minVer = 0;
        String updateNews = null;
        if(TextUtils.isEmpty(versionJson)){
            return null;
        }
        try{
            JSONObject baseObject = new JSONObject(versionJson);
            JSONObject ecbObject = baseObject.getJSONObject("ecb");
            minVer = ecbObject.getDouble("version");
            updateNews = ecbObject.getString("news");
        }catch(JSONException e){
            Log.e(LOG_TAG,"Error");
        }
        UpdateInfo info = new UpdateInfo(minVer, updateNews);
        return info;
    }
}
