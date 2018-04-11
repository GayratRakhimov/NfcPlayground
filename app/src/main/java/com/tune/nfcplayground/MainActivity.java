package com.tune.nfcplayground;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements
        NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

    private NfcAdapter mNfcAdapter;

    EditText amount;
    Button send;
    TextView result;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amount = (EditText) findViewById(R.id.amount);
        send = (Button) findViewById(R.id.send);
        result = (TextView) findViewById(R.id.result);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

    }

    private void sendMessage() {
        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            result.setText("NFC не доступно");
        } else {
            // Register callback to set NDEF message. Setting
            // this makes NFC data push active while the Activity
            // is in the foreground.
            mNfcAdapter.setNdefPushMessageCallback(this, this);
            // Register callback for message-sent success
            mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check to see if a Beam launched this Activity
        if (NfcAdapter.ACTION_NDEF_DISCOVERED
                .equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type
        result.setText(new String(
                msg.getRecords()[0].getPayload()));
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = amount.getText().toString();
        NdefMessage msg = new NdefMessage(NdefRecord.createMime(
                "application/com.tune.nfcplayground.beamtext",
                text.getBytes()),
                NdefRecord
                        .createApplicationRecord("com.tune.nfcplayground"));
        return msg;
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        //This callback happens on a binder thread, don't update
        // the UI directly from this method.
        result.setText(amount.getText().toString()+" o\'tdi");
    }

}
