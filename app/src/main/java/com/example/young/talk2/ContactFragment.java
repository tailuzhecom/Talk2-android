package com.example.young.talk2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by young on 18-5-21.
 */

public class ContactFragment extends Fragment {
    RecyclerView recyclerView;
    ContactAdapter adapter;
    ConnManager connManager;
    private ContactReceiver contactReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.contact_fragment, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.contact_recyclerview);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        contactReceiver = new ContactReceiver();
        IntentFilter intentFilter = new IntentFilter("talk2.AddFriend");
        getActivity().getApplication().registerReceiver(contactReceiver, intentFilter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter=  new ContactAdapter(mFriendList);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

        connManager = connManager.getInstance();
        //请求获得好友列表
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Type", "GetFriendsList");
                    jsonObject.put("From", ConnManager.getUsername());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                connManager.sendMessage(jsonObject.toString());
            }
        }).start();
        return view;
    }

    @Override
    public void onDestroy() {
        super .onDestroy();
        getActivity().getApplication().unregisterReceiver(contactReceiver);
    }

    private List<ContactItem> mFriendList = new ArrayList<>();

    public void updateContact(String friend_list) {   //friendlist : "curry&kd&LBJ"
        Log.d("ContactFragment: ", "updateContact");
        String[] contact_name_list = friend_list.split("&");
        mFriendList.clear();
        for(String contact : contact_name_list) {
            Log.d(TAG, "contact name: " + contact);
            mFriendList.add(new ContactItem(contact, new IconMap().getUserIcon(contact)));
        }
        adapter.notifyDataSetChanged();
    }

    public void addItem(ContactItem contactItem) {
        adapter.add(contactItem);
    }

    private class ContactReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("broadcast");
            Log.d(TAG, "Chat Message: " + msg);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(msg);
                String Type = (String)jsonObject.get("Type");

                if(Type.equals("AddFriend")) {   //获取好友列表
                    String new_friend = (String)jsonObject.get("Content");
                    addItem(new ContactItem(new_friend, new IconMap().getUserIcon(new_friend)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
