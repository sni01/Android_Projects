package edu.stevens.cs522.cloudchatapp.responses;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nisha0634 on 3/14/15.
 */
public class Response {
    private Map<String, String> results;

    public Response(JsonReader jsonReader){
        results = new HashMap<String, String>();
        try{
            jsonReader.beginObject();
            while(jsonReader.peek() != JsonToken.END_OBJECT){
                String label = jsonReader.nextName();
                results.put(label, jsonReader.nextString());
            }
            jsonReader.endObject();
        }
        catch (IOException e){
            Log.i("RESPONSE :", e.getMessage());
        }
    }

    public Map<String, String> getResult(){
        return this.results;
    }
}
