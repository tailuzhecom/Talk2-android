package com.example.young.talk2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by young on 18-5-22.
 */

public class FindFragment extends Fragment {
    private Button express_btn;
    private ConnManager connManager;
    NoteAdapter adapter;

    FindBrocastReceiver findBrocastReceiver;

    private List<NoteItem> mNoteList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.find_fragment, container, false);
        initFriends();
        connManager = ConnManager.getInstance();
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.find_recyclerview);
        express_btn = (Button)view.findViewById(R.id.express_find);

        express_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ExpressActivity.class);
                startActivity(intent);
            }
        });

        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NoteAdapter(mNoteList);
        recyclerView.setAdapter(adapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Type", "GetExpression");
                    jsonObject.put("From", ConnManager.getUsername());
                    connManager.sendMessage(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        findBrocastReceiver = new FindBrocastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("talk2.GetExpression");
        getActivity().getApplication().registerReceiver(findBrocastReceiver, intentFilter);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getApplication().unregisterReceiver(findBrocastReceiver);
    }

    private void initFriends() {
//        for(int i = 0; i < 3; i++) {
//            NoteItem apple = new NoteItem("Apple", R.drawable.apple, "愤怒会使刀刃变得迟钝！");
//            mNoteList.add(apple);
//            NoteItem banana = new NoteItem("Banana", R.drawable.banana, "人类好无聊啊，放假前还要考试。");
//            mNoteList.add(banana);
//            NoteItem orange = new NoteItem("Orange", R.drawable.orange, "人如果不付出牺牲就无法得到任何东西，如果要得到什么，就要付出同等的代价，这就是等价交换的原则。");
//            mNoteList.add(orange);
//        }
    }

    private class FindBrocastReceiver extends BroadcastReceiver {
        private static final String TAG = "FindBrocastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("broadcast");
            Log.d(TAG, "Chat Message: " + msg);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(msg);
                String user = (String)jsonObject.get("User");
                String content = (String)jsonObject.get("Content");
                String expressionId = (String)jsonObject.get("ExpressionId");

                if(!adapter.existItem(expressionId)) {
                    adapter.addItem(new NoteItem(user, new IconMap().getUserIcon(user), content, expressionId));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
