package com.example.young.talk2;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ClientActivity extends AppCompatActivity {
    private EditText mEtContent;
    private ConnManager manager;

    private PushReceiver mPushReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        manager = ConnManager.getInstance();
       // mEtContent = (EditText)findViewById(R.id.test_tv);
        Intent startIntent = new Intent(this, ConnService.class);
        startService(startIntent);
        initReceiver();
    }

    private void initReceiver() {
        mPushReceiver = new PushReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(PushReceiver.ACTION.equals(intent.getAction())) {
                    String response = intent.getStringExtra(PushReceiver.DATA);
                    Toast.makeText(ClientActivity.this, response, Toast.LENGTH_SHORT).show();
                }
            }
        };

        IntentFilter filter = new IntentFilter(PushReceiver.ACTION);
        registerReceiver(mPushReceiver, filter);
    }

    public void clickMessage(View view) {
        if(mEtContent.getText() != null) {
            manager.sendMessage(mEtContent.getText().toString());
        }
    }

    public void clickDisconnect(View view) {
        manager.disconnect();
    }

    @Override
    protected void onDestroy() {
        manager.disconnect();
        unregisterReceiver(mPushReceiver);
        super .onDestroy();
    }
}
