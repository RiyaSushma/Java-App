package com.example.javaapp;

import static com.example.javaapp.chatWin.receiverImage;
import static com.example.javaapp.chatWin.senderImg;

import android.view.LayoutInflater;
import android.view.View;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MsgsAdaptor extends RecyclerView.Adapter {

    Context context;
    ArrayList<MsgModelClass> messagesAdaptorArrayList;
    int ITEM_SEND = 1;
    int ITEM_RECEIVE = 2;
    FirebaseAuth firebaseAuth;

    public MsgsAdaptor(Context context, ArrayList<MsgModelClass> messagesAdaptorArrayList) {
        this.context = context;
        this.messagesAdaptorArrayList = messagesAdaptorArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SEND) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new senderviewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout, parent, false);
            return new receiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MsgModelClass messages = messagesAdaptorArrayList.get(position);
        if(holder.getClass() == senderviewHolder.class) {
            senderviewHolder viewHolder = (senderviewHolder) holder;
            viewHolder.msgtxt.setText(messages.getMessage());
            Picasso.get().load(senderImg).into(viewHolder.circleImageView);
        }
        else {
            receiverViewHolder viewHolder = (receiverViewHolder) holder;
            viewHolder.msgtxt.setText(messages.getMessage());
            Picasso.get().load(receiverImage).into(viewHolder.circleImageView);
        }
    }

    @Override
    public int getItemCount() {
        return messagesAdaptorArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        MsgModelClass messages = messagesAdaptorArrayList.get(position);
        if(firebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderId())) {
            return ITEM_SEND;
        }
        else {
            return ITEM_RECEIVE;
        }
    }

    class senderviewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView msgtxt;

        public senderviewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.chatSenderProfileImage);
            msgtxt = itemView.findViewById(R.id.senderTextSet);
        }
    }
    class receiverViewHolder extends  RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView msgtxt;

        public receiverViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.chatReceiverProfileImage);
            msgtxt = itemView.findViewById(R.id.receiverTextSet);
        }
    }
}
