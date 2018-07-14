package com.example.young.talk2;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by young on 18-5-1.
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private Context context;
    private List<FriendItem> mfriendItems;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View friendView;
        ImageView friendImage;
        TextView friendName;
        TextView note;

        public ViewHolder(View view) {
            super(view);
            friendView = view;
            friendImage = (ImageView)view.findViewById(R.id.friend_image);
            friendName = (TextView)view.findViewById(R.id.friend_name);
            note = (TextView)view.findViewById(R.id.note);
        }
    }

    public FriendAdapter(List<FriendItem> friendItems) {
        mfriendItems = friendItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        context = parent.getContext();
        holder.friendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                FriendItem friend = mfriendItems.get(position);
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("friend_name", friend.getName());
                context.startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FriendItem friend = mfriendItems.get(position);
        holder.friendImage.setImageResource(friend.getImageId());
        holder.friendName.setText(friend.getName());
        holder.note.setText(friend.getNote());
    }

    @Override
    public int getItemCount() {
        return mfriendItems.size();
    }

    public void addItem(FriendItem friendItem) {
        mfriendItems.add(friendItem);
        notifyItemInserted(mfriendItems.size() - 1);
    }

    public void clearItems() {
        mfriendItems.clear();
        notifyDataSetChanged();
    }
}
