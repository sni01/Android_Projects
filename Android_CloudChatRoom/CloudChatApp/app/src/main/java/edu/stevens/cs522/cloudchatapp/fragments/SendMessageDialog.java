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
import edu.stevens.cs522.cloudchatapp.factory.ISendMessageListener;

/**
 * Created by nisha0634 on 4/26/15.
 */
public class SendMessageDialog extends DialogFragment {

    public static final String TAG = "send message dialog tag";

    private ISendMessageListener iSendMessageListener;

    private static Activity context;

    private static EditText editText;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        if(!(activity instanceof ISendMessageListener)){
            throw new IllegalArgumentException("Activity must implement ISendMessageListener");
        }
        iSendMessageListener = (ISendMessageListener)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //get layout for this fragment, it is a layout with a listView
        View layout = inflater.inflate(R.layout.send_message_dialog, container, false);
        Button sendButton = (Button)layout.findViewById(R.id.send_message_dialog_send_button);
        Button cancelButton = (Button)layout.findViewById(R.id.send_message_dialog_cancel_button);
        editText = (EditText)layout.findViewById(R.id.send_message_dialog_edit_text);
        sendButton.setOnClickListener(sendListener);
        cancelButton.setOnClickListener(cancelListener);
        return layout;
    }


    public void launch(Activity context, String tag){
        this.context = context;
        SendMessageDialog sendMessageDialog = new SendMessageDialog();
        Bundle bundle = new Bundle();
        sendMessageDialog.setArguments(bundle);
        sendMessageDialog.show(context.getFragmentManager(), tag);
    }

    /*
     * on click listener
     */
    View.OnClickListener sendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //call iSendMessageListener
            String content = editText.getText().toString();
            iSendMessageListener.acknowledge(content);
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
