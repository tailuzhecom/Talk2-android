package com.example.young.talk2;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by young on 18-5-22.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private Context context;
    private List<ContactItem> mfriendItems;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View friendView;
        ImageView friendImage;
        TextView friendName;

        public ViewHolder(View view) {
            super(view);
            friendView = view;
            friendImage = (ImageView)view.findViewById(R.id.contact_image);
            friendName = (TextView)view.findViewById(R.id.contact_name);
        }
    }

    public ContactAdapter(List<ContactItem> friendItems) {
        mfriendItems = friendItems;
    }

    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        final ContactAdapter.ViewHolder holder = new ContactAdapter.ViewHolder(view);
        context = parent.getContext();
        holder.friendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                ContactItem friend = mfriendItems.get(position);
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("friend_name", friend.getName());
                context.startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ContactAdapter.ViewHolder holder, int position) {
        ContactItem friend = mfriendItems.get(position);
        holder.friendImage.setImageResource(friend.getImageId());
        holder.friendName.setText(friend.getName());
    }

    @Override
    public int getItemCount() {
        return mfriendItems.size();
    }

    public void remove(int position) {
        mfriendItems.remove(position);
        notifyItemRemoved(position);
    }

    public void add(ContactItem contactItem) {
        mfriendItems.add(contactItem);
        notifyItemInserted(getItemCount() - 1);
    }
}
