package com.example.young.talk2;

import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptGroup;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by young on 18-5-22.
 */

public class ConnManager {
    private static final int STATE_FROM_SERVER_OK = 0;
    private static String dsName = "192.168.43.92";
    private static int dstPort = 10021;
    private static Socket socket;
    private static String username;
    private static ConnManager instance;

    public static ConnManager getInstance() {
        if (instance == null) {
            synchronized (ConnManager.class) {
                if (instance == null) {
                    instance = new ConnManager();
                }
            }
        }
        return instance;
    }

    /**
     * 连接
     *
     * @return
     */
    public boolean connect(final Handler handler) {

        if (socket == null || socket.isClosed()) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        socket = new Socket(dsName, dstPort);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        throw new RuntimeException("连接错误: " + e.getMessage());
                    }

                    try {
                        // 输入流，为了获取客户端发送的数据
                        InputStream is = socket.getInputStream();
                        byte[] buffer = new byte[1024];
                        int len = -1;
                        while ((len = is.read(buffer)) != -1) {
                            final String result = new String(buffer, 0, len);

                            Message msg = Message.obtain();
                            msg.obj = result;
                            msg.what = STATE_FROM_SERVER_OK;
                            handler.sendMessage(msg);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("getInputStream错误: " + e.getMessage());
                    }

                }
            }).start();
        }

        return true;
    }

    /**
     * 连接
     *
     * @return
     */
    public void connect() {
        if (socket == null || socket.isClosed()) {

            try {
                socket = new Socket(dsName, dstPort);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException("连接错误: " + e.getMessage());
            }

        }

    }

    public Socket getSocket() {
        return socket;
    }

    /**
     * 发送信息
     *
     * @param content
     */
    public void sendMessage(String content) {
        OutputStream os = null;
        try {
            if (socket != null) {
                os = socket.getOutputStream();
                os.write(content.getBytes());
                os.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException("发送失败:" + e.getMessage());
        }
    }

    /**
     * 发送信息
     *
     * @param auth
     */
    public void sendAuth(String auth) {
        OutputStream os = null;
        try {
            if (socket != null) {
                os = socket.getOutputStream();
                os.write(auth.getBytes());
                os.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException("发送失败:" + e.getMessage());
        }
    }

    /**
     * 关闭连接
     */
    public void disconnect() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException("关闭异常:" + e.getMessage());
            }
            socket = null;
        }
    }

    public  interface  ConnectionListener{
        void pushData(String str);
    }

    private ConnectionListener mListener;
    public void setConnectionListener(ConnectionListener listener){
        this.mListener = listener;
    }


    public static void setUsername(String username_) {
        username = username_;
    }

    public static String getUsername() {
        return username;
    }



}
