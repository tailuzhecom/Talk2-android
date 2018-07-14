package com.example.young.talk2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class ExpressActivity extends AppCompatActivity {
    ConnManager connManager;
    private final String TAG = "EpressActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express);

        connManager = ConnManager.getInstance();

        final EditText editText = (EditText)findViewById(R.id.edit_express);

        Button btn_confirm = (Button)findViewById(R.id.confirm_express);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Type", "Express");
                    jsonObject.put("From", ConnManager.getUsername());
                    jsonObject.put("Note", editText.getText().toString());
                    Log.d(TAG, "json:" + jsonObject.toString());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            connManager.sendMessage(jsonObject.toString());
                        }
                    }).start();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                finish();
            }
        });

        Button btn_cancel = (Button)findViewById(R.id.cancel_express);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
