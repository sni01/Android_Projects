package edu.stevens.cs522.cloudchatapp.requests;

import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import edu.stevens.cs522.cloudchatapp.responses.Response;
import edu.stevens.cs522.cloudchatapp.responses.StreamingResponse;


/**
 * Created by nisha0634 on 3/14/15.
 */
public class RestMethod {
    private static String feed = "http://host-155-246-163-26.dhcp.stevens-tech.edu:8080/chat";
    //change the feed to the same URL as on your terminal, this is important!
    //private static String feed = "http://shas-mbp.home:8080/chat";

    public RestMethod(String feed){
        this.feed = feed;
    }

    public RestMethod(){}

    public Response perform(RegisterRequest request){
        try{
            String charset = "UTF-8";
            String query = String.format("?username=%s&regid=%s", URLEncoder.encode(request.clientName, charset),URLEncoder.encode(request.registerationID.toString(), charset));

            URL url = new URL(feed + query);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setInstanceFollowRedirects(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //set headers
            Map<String, String> headers = request.getRequestHeaders();
            for(String header : headers.keySet()){
                httpURLConnection.addRequestProperty(header, headers.get(header));
            }

            InputStream is = new BufferedInputStream(httpURLConnection.getInputStream());
            InputStreamReader reader = new InputStreamReader(is);
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(reader);
            String read = br.readLine();
            while(read != null) {
                //System.out.println(read);

                sb.append(read);
                read =br.readLine();
            }

            StringReader stringReader = new StringReader(sb.toString());
            JsonReader jsonReader = new JsonReader(stringReader);
            return request.getResponse(httpURLConnection, jsonReader);
        }
        catch (MalformedURLException e){
            Log.e("MAL-FORMATTED URL: ", e.getMessage());
            return null;
        }
        catch (IOException e){
            Log.e("IO/EXCEPTION: ", e.getMessage());
            return null;
        }
    }

    public Response perform(SendMessageRequest request){
        try{
            String charset = "UTF-8";
            String query = String.format("/%s?regid=%s", URLEncoder.encode(request.clientID+"", charset),URLEncoder.encode(request.registerationID.toString(), charset));
            URL url = new URL(feed+query);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setInstanceFollowRedirects(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");

            //set headers
            Map<String, String> headers = request.getRequestHeaders();
            for(String header : headers.keySet()){
                httpURLConnection.addRequestProperty(header, headers.get(header));
            }


            OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream());

            JsonWriter writer = new JsonWriter(out);
            writer.beginObject();
            writer.name(SendMessageRequest.CHATROOM);
            writer.value(request.chatRoomId);
            writer.name(SendMessageRequest.TIMESTAMP);
            writer.value(request.timeStamp);
            writer.name(SendMessageRequest.TEXT);
            writer.value(request.messageContent);
            writer.endObject();
            writer.flush();
            writer.close();

            int httpStatus = httpURLConnection.getResponseCode();
            Log.i("RECEIVER SERVICE :", "HTTPURLCONNECTION STATUS" + httpStatus);

            InputStream is = new BufferedInputStream(httpURLConnection.getInputStream());
            InputStreamReader reader = new InputStreamReader(is);
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(reader);
            String read = br.readLine();
            while(read != null) {
                //System.out.println(read);
                sb.append(read);
                read = br.readLine();
            }

            StringReader stringReader = new StringReader(sb.toString());
            JsonReader jsonReader = new JsonReader(stringReader);
            return request.getResponse(httpURLConnection, jsonReader);
        }
        catch (MalformedURLException e){
            Log.e("MAL-FORMATTED URL: ", e.getMessage());
            return null;
        }
        catch (IOException e){
            Log.e("IO/EXCEPTION: ", e.getMessage());
            return null;
        }
    }

    public StreamingResponse perform(SynchronizeRequest request, StreamingOutput out){
        try{
            String charset = "UTF-8";
            String query = String.format("/%s?regid=%s&seqnum=%s", URLEncoder.encode(request.clientID+"", charset),URLEncoder.encode(request.registerationID.toString(), charset),URLEncoder.encode(request.most_sequence_number + "", charset));
            URL url = new URL(feed+query);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setInstanceFollowRedirects(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");

            //set headers
            Map<String, String> headers = request.getRequestHeaders();
            for(String header : headers.keySet()){
                httpURLConnection.addRequestProperty(header, headers.get(header));
            }

            //write to connection
            out.writeToConn(httpURLConnection);

            //return StreamResponse with connection, StreamResponse is taking care of disconnect httpURLConnection.
            return new StreamingResponse(httpURLConnection); //return StreamingResponse to requestProcessor to handle it.
        }
        catch (MalformedURLException e){
            Log.e("MAL-FORMATTED URL: ", e.getMessage());
            return null;
        }
        catch (IOException e){
            Log.e("IO/EXCEPTION: ", e.getMessage());
            return null;
        }
    }
}
