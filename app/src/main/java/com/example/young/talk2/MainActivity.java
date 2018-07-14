package com.example.young.talk2;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private TextView mTextMessage;
    private List<FriendItem> mFriendList = new ArrayList<>();
    private MsgFragment msgFragment;
    private ContactFragment contactFragment;
    private FindFragment findFragment;
    private MyProfileFragment myProfileFragment;

    private ConnManager connManager;
    private MainUIBrocastReceiver mainUIBrocastReceiver;

    private AlertDialog.Builder builder;
    private EditText addEdit;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_msg:
                    Log.d(TAG, "onNavigationItemSelected: " + "contact fragment clicked!");
                    initMsgFragment();
                    return true;
                case R.id.navigation_contact:
                    Log.d(TAG, "onNavigationItemSelected: " + "contact fragment clicked!");
                    initContactFragment();
                    return true;
                case R.id.navigation_find:

                    initFindFragment();
                    return true;
                case R.id.navigation_me:
                    initMyProfile();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        initMsgFragment();
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        //添加好友
        builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入要添加好友的用户名");
        addEdit = new EditText(this);
        builder.setView(addEdit);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "输入框: " + addEdit.getText().toString());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("Type", "AddFriend");
                            jsonObject.put("From", ConnManager.getUsername());
                            jsonObject.put("To", addEdit.getText().toString());
                            connManager.sendMessage(jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "输入框: 点击了取消");
            }
        });
        ImageView addView = (ImageView)findViewById(R.id.add_imageView);
        final AlertDialog dialog = builder.create();
        addView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEdit.setText("");
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });
        Log.d(TAG, "onCreate: ");
        connManager = ConnManager.getInstance();
        mainUIBrocastReceiver = new MainUIBrocastReceiver();
        IntentFilter intentFilter = new IntentFilter("talk2.GetFriendsList");
        intentFilter.addAction("talk2.FriendMessage");
        registerReceiver(mainUIBrocastReceiver, intentFilter);

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mainUIBrocastReceiver);
        super.onDestroy();
    }


    private void initMsgFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(msgFragment == null) {
            msgFragment = new MsgFragment();
            transaction.add(R.id.msg_framelayout, msgFragment);
        }
        hideFragment(transaction);
        transaction.show(msgFragment);

        transaction.replace(R.id.msg_framelayout, msgFragment);
        transaction.commit();
    }

    private void initContactFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(contactFragment == null) {
            contactFragment = new ContactFragment();
        }
        transaction.replace(R.id.msg_framelayout, contactFragment);

        transaction.commit();
    }

    private void initFindFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(findFragment == null) {
            findFragment = new FindFragment();
        }
        transaction.replace(R.id.msg_framelayout, findFragment);

        transaction.commit();
    }

    private void initMyProfile() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(myProfileFragment == null) {
            myProfileFragment = new MyProfileFragment();
        }
        transaction.replace(R.id.msg_framelayout, myProfileFragment);

        transaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction) {
        if(msgFragment != null) {
            transaction.hide(msgFragment);
        }
    }

    private class MainUIBrocastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("broadcast");
            Log.d(TAG, "Chat Message: " + msg);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(msg);
                String Type = (String)jsonObject.get("Type");

                if(Type.equals("GetFriendsList")) {   //获取好友列表
                    String friend_list = (String)jsonObject.get("Content");
                    Log.d(TAG, "friend_list: " + friend_list);
                    contactFragment.updateContact(friend_list);

                } else if(Type.equals("FriendMessage")) {
                    String content = (String)jsonObject.get("content");
                    Log.d(TAG, "FriendMessage: " + content);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
