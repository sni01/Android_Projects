package edu.stevens.cs522.cloudchatapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.UUID;

import edu.stevens.cs522.cloudchatapp.R;

/**
 * Created by nisha0634 on 3/11/15.
 */
public class PreferenceActivity extends Activity implements View.OnClickListener{

    //preference variable
    public static final String MY_PREFS = "shared preferences";
    public static final String CLIENT_NAME = "client name";
    public static final String CLIENT_ID = "client identifier";
    public static final String SERVER_ADDRESS = "server address";
    public static final String LEAST_SIGNIFICANT_BITS = "least significant bits";
    public static final String MOST_SIGNIFICANT_BITS = "most significant bits";
    public static final String CHAT_ROOM_NAME = "chat room name";
    public static final String SEQUENCE_NUMBER = "most recently message sequence number";
    public static final String LATITUDE = "client's latitude";
    public static final String LONGITUDE = "client's longitude";
    public static final String SENDER = "sender";

    //UI variables
    private EditText client_name_EditText;
    private EditText server_address_EditText;
    private Button set_preference_Button;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.preference_activity_layout);

        //init UI variables
        client_name_EditText = (EditText)findViewById(R.id.preference_client_name);
        server_address_EditText = (EditText)findViewById(R.id.preference_server_address);
        set_preference_Button = (Button)findViewById(R.id.preference_set_button);

        //set button listener
        set_preference_Button.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.preference_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.preference_client_activity){
            Intent intent = new Intent(this, Client.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    //implement click listener
    @Override
    public void onClick(View v){
        //read from Edit View
        String client_name = client_name_EditText.getText().toString();
        String server_address = server_address_EditText.getText().toString();
        //get preference
        int mode = Activity.MODE_PRIVATE;
        SharedPreferences preferences = getSharedPreferences(MY_PREFS, mode);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CLIENT_NAME, client_name);
        editor.putLong(CLIENT_ID, 0);
        editor.putString(SERVER_ADDRESS, server_address);
        editor.putString(CHAT_ROOM_NAME, "_DEFAULT");
        editor.putLong(SEQUENCE_NUMBER, 0); //sequence record starts with 0
        editor.putString(LATITUDE, "0");
        editor.putString(LONGITUDE, "0");

        UUID registerationID = UUID.randomUUID();
        long least = registerationID.getLeastSignificantBits();
        long most = registerationID.getMostSignificantBits();
        editor.putLong(LEAST_SIGNIFICANT_BITS, least);
        editor.putLong(MOST_SIGNIFICANT_BITS, most);
        //commit
        editor.commit();
    }
}
