package com.example.young.talk2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by young on 18-5-22.
 */

public class MyProfileFragment extends Fragment {
    ConnManager connManager;
    TextView name_tv;
    TextView phone_tv;
    TextView email_tv;
    TextView location_tv;
    TextView sex_tv;
    TextView note_tv;

    ProfileBrocastReceiver profileBrocastReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.myprofile_fragment, container, false);
        TextView username_textview = (TextView)view.findViewById(R.id.name_myprofile);
        connManager = ConnManager.getInstance();

        username_textview.setText("用户名：" + ConnManager.getUsername());

        ImageView icon_myprofile = (ImageView)view.findViewById(R.id.icon_myprofile);
        icon_myprofile.setImageResource(new IconMap().getUserIcon(ConnManager.getUsername()));

        Button send_btn = (Button)view.findViewById(R.id.send_msg_myprofile);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("friend_name", ConnManager.getUsername());
                startActivity(intent);
            }
        });

        name_tv = (TextView)view.findViewById(R.id.name_myprofile);
        phone_tv = (TextView)view.findViewById(R.id.phone_number_myprofile);
        email_tv = (TextView)view.findViewById(R.id.email_myprofile);
        location_tv = (TextView)view.findViewById(R.id.location_myprofile);
        sex_tv = (TextView)view.findViewById(R.id.sex_myprofile);
        note_tv = (TextView)view.findViewById(R.id.saying_myprofile);

        profileBrocastReceiver = new ProfileBrocastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("talk2.PersonInfo");
        getActivity().getApplication().registerReceiver(profileBrocastReceiver, intentFilter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Type", "GetInfo");
                    jsonObject.put("From", ConnManager.getUsername());
                    connManager.sendMessage(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getApplication().unregisterReceiver(profileBrocastReceiver);
    }

    private class ProfileBrocastReceiver extends BroadcastReceiver {
        private final String TAG = "MyProfileFragment";

        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("broadcast");
            Log.d(TAG, "Chat Message: " + msg);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(msg);
                String username = (String)jsonObject.get("User");
                String phone = (String)jsonObject.get("Phone");
                String email = (String)jsonObject.get("Email");
                String location = (String)jsonObject.get("Location");
                String note = (String)jsonObject.get("Note");
                String sex = (String)jsonObject.get("Sex");

                name_tv.setText("用户名：" + username);
                phone_tv.setText("电话号码：" + phone);
                email_tv.setText("邮箱：" + email);
                location_tv.setText("地区：" + location);
                sex_tv.setText("性别：" + sex);
                note_tv.setText("个人签名：" + note);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
