package com.example.young.talk2;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class ConnService extends Service implements ConnManager.ConnectionListener {
    private static final String TAG = "ConnService";
    private ConnManager mConnManager;
    private String username;

    DatabaseHelper databaseHelper;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 连接服务
     */


    @Override
    public void onCreate() {
        super.onCreate();
        databaseHelper = new DatabaseHelper(this, "Talk2v3.db", null, 2);

        databaseHelper.getWritableDatabase();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mConnManager = ConnManager.getInstance();
                mConnManager.setConnectionListener(ConnService.this);
                mConnManager.connect();
                ReadThread readThread = new ReadThread(mConnManager.getSocket());
                readThread.run();
            }
        }).start();

    }

    @Override
    public void pushData(String data) {
        Intent intent = new Intent();
        intent.setAction(PushReceiver.ACTION);
        intent.putExtra(PushReceiver.DATA, data);
        sendBroadcast(intent);
    }


    //监听线程
    private class ReadThread extends Thread {
        private Socket rSocket;
        private boolean isStart = false;

        public ReadThread(Socket socket) {
            isStart = true;
            rSocket = socket;
        }

        @Override
        public void run() {
            Log.d(TAG, "监听线程启动.");
            super.run();
            String line = "";
            if (rSocket != null) {
                while (!rSocket.isClosed() && !rSocket.isInputShutdown()) {
                    Log.d(TAG, "监听socket运行正常.");
                    try {
                        InputStream is = rSocket.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader br = new BufferedReader(isr);

                        while ((line = br.readLine()) != null) {
                            Log.d(TAG, "监听线程Message: " + line);
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(line);
                                String Type = (String) jsonObject.get("Type");


                                if (Type.equals("205")) { //获取好友列表
                                    String friend_list = (String) jsonObject.get("FriendsList");
                                    JSONObject broadcastJson = new JSONObject();
                                    broadcastJson.put("Type", "GetFriendsList");
                                    broadcastJson.put("Content", friend_list);
                                    Intent intent = new Intent();
                                    intent.setAction("talk2.GetFriendsList");
                                    intent.putExtra("broadcast", broadcastJson.toString());
                                    sendBroadcast(intent);

                                } else if (Type.equals("203")) {  //接收好友发送的信息
                                    String friend_name = (String) jsonObject.get("From");
                                    String content = (String) jsonObject.get("Content");
                                    JSONObject broadcastJson = new JSONObject();
                                    broadcastJson.put("Type", "FriendMessage");
                                    broadcastJson.put("From", friend_name);
                                    broadcastJson.put("Content", content);
                                    Intent intent = new Intent();
                                    intent.setAction("talk2.FriendMessage");
                                    intent.putExtra("broadcast", broadcastJson.toString());
                                    sendBroadcast(intent);

                                    // 更新本地数据库，插入聊天记录
                                    {
                                        SQLiteDatabase db = databaseHelper.getWritableDatabase();
                                        ContentValues values = new ContentValues();
                                        values.put("from_", friend_name);
                                        values.put("to_", ConnManager.getUsername());
                                        values.put("content", content);
                                        db.insert("chat_record", null, values);
                                    }


                                    //更新消息列表界面
                                    {
                                        SQLiteDatabase db = databaseHelper.getWritableDatabase();
                                        ContentValues values = new ContentValues();
                                        int flag = 0;
                                        Cursor cursor = db.query("talking", null, null, null, null, null, null);
                                        if (cursor.moveToFirst()) {
                                            do {
                                                String from = cursor.getString(cursor.getColumnIndex("friend")); //消息发送方
                                                if (from.equals(friend_name)) {
                                                    flag = 1;
                                                    break;
                                                }

                                            } while (cursor.moveToNext());
                                        }
                                        cursor.close();
                                        values.clear();
                                        values.put("friend", friend_name);
                                        values.put("msg", content);

                                        if (flag == 1) {
                                            db.update("talking", values, "friend = ?", new String[]{friend_name});
                                        } else {
                                            db.insert("talking", null, values);
                                        }
                                    }

                                } else if (Type.equals("206")) {  //添加好友成功
                                    String content = (String) jsonObject.get("Content");
                                    JSONObject broadcastJson = new JSONObject();
                                    broadcastJson.put("Type", "AddFriend");
                                    broadcastJson.put("Content", content);
                                    Intent intent = new Intent();
                                    intent.setAction("talk2.AddFriend");
                                    intent.putExtra("broadcast", broadcastJson.toString());
                                    sendBroadcast(intent);

                                } else if (Type.equals("208")) { //获取好友动态
                                    Log.d(TAG, "获取好友动态");
                                    String user = (String) jsonObject.get("User");
                                    String content = (String) jsonObject.get("Content");
                                    String expressionId = (String) jsonObject.get("ExpressionId");

                                    JSONObject broadcastJson = new JSONObject();
                                    broadcastJson.put("Type", "GetExpression");
                                    broadcastJson.put("User", user);
                                    broadcastJson.put("Content", content);
                                    broadcastJson.put("ExpressionId", expressionId);
                                    Intent intent = new Intent();
                                    intent.setAction("talk2.GetExpression");
                                    intent.putExtra("broadcast", broadcastJson.toString());
                                    sendBroadcast(intent);

                                } else if(Type.equals("201")) { //登录成功
                                    JSONObject broadcastJson = new JSONObject();
                                    broadcastJson.put("Type", "LoginSuccess");
                                    Intent intent = new Intent();
                                    intent.setAction("talk2.Login");
                                    intent.putExtra("broadcast", broadcastJson.toString());
                                    sendBroadcast(intent);

                                } else if(Type.equals("401")) { //登录失败
                                    JSONObject broadcastJson = new JSONObject();
                                    broadcastJson.put("Type", "LoginFailed");
                                    Intent intent = new Intent();
                                    intent.setAction("talk2.Login");
                                    intent.putExtra("broadcast", broadcastJson.toString());
                                    sendBroadcast(intent);
                                } else if(Type.equals("210")) {
                                    Intent intent = new Intent();
                                    intent.setAction("talk2.PersonInfo");
                                    intent.putExtra("broadcast", line);
                                    sendBroadcast(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }

}
