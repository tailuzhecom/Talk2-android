package com.example.young.talk2;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by young on 18-5-24.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    private Context context;
    private List<NoteItem> mNoteItems;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View noteView;
        ImageView friendImage;
        TextView friendName;
        TextView note;

        public ViewHolder(View view) {
            super(view);
            noteView = view;
            friendImage = (ImageView) view.findViewById(R.id.imgHead);
            friendName = (TextView) view.findViewById(R.id.tvName);
            note = (TextView) view.findViewById(R.id.tvContent);
        }
    }

    public NoteAdapter(List<NoteItem> noteItems) {
        mNoteItems = noteItems;
    }

    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saying_item, parent, false);
        final NoteAdapter.ViewHolder holder = new NoteAdapter.ViewHolder(view);
        context = parent.getContext();
//        holder.friendView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position = holder.getAdapterPosition();
//                NoteItem friend = mNoteItems.get(position);
//                Intent intent = new Intent(context, ChatActivity.class);
//                intent.putExtra("friend_name", friend.getName());
//                context.startActivity(intent);
//            }
//        });

        return holder;
    }

    @Override
    public void onBindViewHolder(NoteAdapter.ViewHolder holder, int position) {
        NoteItem friend = mNoteItems.get(position);
        holder.friendImage.setImageResource(friend.getImageId());
        holder.friendName.setText(friend.getName());
        holder.note.setText(friend.getNote());
    }

    @Override
    public int getItemCount() {
        return mNoteItems.size();
    }

    public void addItem(NoteItem noteItem) {
        mNoteItems.add(noteItem);
        notifyItemInserted(mNoteItems.size());
    }

    public boolean existItem(String expressionId) {
        for(NoteItem item : mNoteItems) {
            Log.d(TAG, "existItem: " + item.getExpressionId());
            if(item.getExpressionId().equals(expressionId))
                return true;
        }
        return false;
    }
}