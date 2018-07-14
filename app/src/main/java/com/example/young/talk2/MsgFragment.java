package com.example.young.talk2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by young on 18-5-21.
 */

//Fragment1
public class MsgFragment extends Fragment {
    private static final String TAG = "MsgFragment";
    private List<FriendItem> mFriendList = new ArrayList<>();
    MsgAdapter adapter;
    DatabaseHelper databaseHelper;
    ConnManager connManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.msg_fragment, container, false);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.friend_recyclerview);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        FriendAdapter adapter=  new FriendAdapter(mFriendList);
        recyclerView.setAdapter(adapter);
        connManager = ConnManager.getInstance();

        adapter.clearItems();
        Log.d(TAG, "onCreateView: ");
        databaseHelper = new DatabaseHelper(getActivity(), "Talk2v3.db", null, 2);  //获取数据库,更新消息列表
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query("talking", null, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                String friend = cursor.getString(cursor.getColumnIndex("friend")); //消息发送方
                String content = cursor.getString(cursor.getColumnIndex("msg"));
                Log.d(TAG, "Query: friend: " + friend);
                Log.d(TAG, "content: " + content);

                adapter.addItem(new FriendItem(friend, new IconMap().getUserIcon(friend), content));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return view;
    }

}
