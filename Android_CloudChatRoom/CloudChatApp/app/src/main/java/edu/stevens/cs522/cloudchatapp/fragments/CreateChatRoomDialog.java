package edu.stevens.cs522.cloudchatapp.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import edu.stevens.cs522.cloudchatapp.R;
import edu.stevens.cs522.cloudchatapp.factory.ISetChatRoomListener;

/**
 * Created by nisha0634 on 4/26/15.
 */
public class CreateChatRoomDialog extends DialogFragment {
    public static final String TAG = "create new chat room dialog tag";

    private ISetChatRoomListener iSetChatRoomListener;

    private static Activity context;
    private static EditText editText;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        if(!(activity instanceof ISetChatRoomListener)){
            throw new IllegalArgumentException("Activity must implement ISetChatRoomListener");
        }
        iSetChatRoomListener = (ISetChatRoomListener)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //get layout for this fragment, it is a layout with a listView
        View layout = inflater.inflate(R.layout.setup_chatroom_dialog, container, false);
        Button setupButton = (Button)layout.findViewById(R.id.setup_chatRoom_dialog_setup_button);
        Button cancelButton = (Button)layout.findViewById(R.id.setup_chatRoom_dialog_cancel_button);
        editText = (EditText)layout.findViewById(R.id.setup_chatRoom_dialog_edit_text);
        setupButton.setOnClickListener(setupListener);
        cancelButton.setOnClickListener(cancelListener);
        return layout;
    }


    public void launch(Activity context, String tag){
        this.context = context;
        CreateChatRoomDialog createChatRoomDialog = new CreateChatRoomDialog();
        Bundle bundle = new Bundle();
        createChatRoomDialog.setArguments(bundle);
        createChatRoomDialog.show(context.getFragmentManager(), tag);
    }

    /*
     * on click listener
     */
    View.OnClickListener setupListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //call iSendMessageListener
            String newRoomName = editText.getText().toString();
            iSetChatRoomListener.setChatRoom(newRoomName);
            dismiss();
        }
    };

    View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //destroy this dialog
            dismiss();
        }
    };
}
