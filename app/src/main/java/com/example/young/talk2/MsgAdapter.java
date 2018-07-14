package com.example.young.talk2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by young on 18-5-1.
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private List<Msg> mMsgList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftName;
        TextView rightName;
        TextView leftMsg;
        TextView rightMsg;
        ImageView rightIcon;
        ImageView leftIcon;

        public ViewHolder(View view) {
            super(view);
            leftLayout = (LinearLayout)view.findViewById(R.id.left_layout_outer);
            rightLayout = (LinearLayout)view.findViewById(R.id.right_layout_outer);
            leftMsg = (TextView)view.findViewById(R.id.left_msg);
            rightMsg = (TextView)view.findViewById(R.id.right_msg);
            rightIcon = (ImageView)view.findViewById(R.id.icon_right);
            leftIcon = (ImageView)view.findViewById(R.id.icon_left);
            leftName = (TextView)view.findViewById(R.id.name_left);
            rightName = (TextView)view.findViewById(R.id.name_right);
        }
    }

    public MsgAdapter(List<Msg> msgList) {
        mMsgList = msgList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Msg msg = mMsgList.get(position);
        if(msg.getType() == Msg.TYPE_RECEIVED) {
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
            holder.leftIcon.setImageResource(msg.getIconId());
            holder.leftName.setText(msg.getName());
        } else if(msg.getType() == Msg.TYPE_SEND) {
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());
            holder.rightIcon.setImageResource(msg.getIconId());
            holder.rightName.setText(msg.getName());
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }

    public void addItem(Msg msg) {
        mMsgList.add(msg);
        notifyItemInserted(mMsgList.size() - 1);
    }
}
