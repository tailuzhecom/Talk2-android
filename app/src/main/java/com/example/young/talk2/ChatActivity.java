package com.example.young.talk2;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {
    private String friend_name;
    private final String TAG = "ChatActivity";
    private List<Msg> msgList = new ArrayList<>();
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private ImageView emoji_view;
    ConnManager connManager;

    DatabaseHelper databaseHelper;

    ChatBrocastReceiver chatBrocastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initMsgs();
        connManager = ConnManager.getInstance();
        Intent intent = getIntent();
        friend_name = intent.getStringExtra("friend_name");
        TextView chat_textView = (TextView)findViewById(R.id.chat_textView);
        chat_textView.setText(friend_name);
        inputText = (EditText)findViewById(R.id.input_text);
        send = (Button)findViewById(R.id.send);
        msgRecyclerView = (RecyclerView)findViewById(R.id.msg_recycler_view);
        emoji_view = (ImageView)findViewById(R.id.emoji_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);

        //返回按钮
        ImageView back_chat = (ImageView)findViewById(R.id.back_chat);
        back_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Log.d("ChatActivity", "onCreate: ");

        databaseHelper = new DatabaseHelper(this, "Talk2v3.db", null, 2);  //获取数据库,更新聊天记录
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query("chat_record", null, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                String from = cursor.getString(cursor.getColumnIndex("from_")); //消息发送方
                String to = cursor.getString(cursor.getColumnIndex("to_"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                Log.d(TAG, "Query: from: " + "from");
                Log.d(TAG, "to: " + to);
                Log.d(TAG, "content: " + content);
                if(from.equals(ConnManager.getUsername()) && to.equals(friend_name)) {
                    adapter.addItem(new Msg(content, Msg.TYPE_SEND, new IconMap().getUserIcon(from), from));
                } else if(from.equals(friend_name) && to.equals(ConnManager.getUsername())){
                    adapter.addItem(new Msg(content, Msg.TYPE_RECEIVED, new IconMap().getUserIcon(from), from));
                }

            } while (cursor.moveToNext());
        }
        cursor.close();

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Type", "GetOfflineMessage");
                    jsonObject.put("From", ConnManager.getUsername());
                    jsonObject.put("To", friend_name);
                    jsonObject.put("Content", "");
                    connManager.sendMessage(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();

        //发送按钮
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String content = inputText.getText().toString();
                if(!"".equals(content)) {
                    //发送消息
                    //json format: {'from': myname, 'to': friendname, 'content':msg}
                    final JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("Type", "SendMessage");
                        jsonObject.put("From", ConnManager.getUsername());
                        jsonObject.put("To", friend_name);
                        jsonObject.put("Content", content);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //在chat_record中插入发送的消息
                    SQLiteDatabase db = databaseHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("from_", ConnManager.getUsername());
                    values.put("to_", friend_name);
                    values.put("content", content);
                    db.insert("chat_record", null, values);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            connManager.sendMessage(jsonObject.toString());
                        }
                    }).start();
                    Log.d(TAG, "Send message: " + jsonObject.toString());
                    adapter.addItem(new Msg(content, Msg.TYPE_SEND, new IconMap().getUserIcon(ConnManager.getUsername()), ConnManager.getUsername()));
                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                    //更新ui
                    inputText.setText("");
                }
            }
        });


        emoji_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: emoji view");
            }
        });

        chatBrocastReceiver = new ChatBrocastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("talk2.FriendMessage");
        registerReceiver(chatBrocastReceiver, intentFilter);

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(chatBrocastReceiver);
        super .onDestroy();
    }

    private void initMsgs() {
//        Msg msg1 = new Msg("Hello guy", Msg.TYPE_RECEIVED, R.drawable.orange, "kd");
//        msgList.add(msg1);
//        Msg msg2 = new Msg("Hello who is that", Msg.TYPE_SEND, R.drawable.apple, "ssc");
//        msgList.add(msg2);
//        Msg msg3 = new Msg("This is Tom. Nice to meet you.", Msg.TYPE_RECEIVED, R.drawable.orange, "kd");
//        msgList.add(msg3);
    }

    public int getItemCount() {
        return msgList.size();
    }

    private class ChatBrocastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("broadcast");
            Log.d(TAG, "Chat Message: " + msg);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(msg);
                String from_name = (String)jsonObject.get("From");
                String content = (String)jsonObject.get("Content");
                Log.d(TAG, from_name + " " + content);
                if(from_name.equals(friend_name)) {
                    adapter.addItem(new Msg(content, Msg.TYPE_RECEIVED, new IconMap().getUserIcon(friend_name), friend_name));
                    msgRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    Log.d(TAG, "from_name equals friend_name,用户正在与该来信用户聊天");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}