package com.example.javaapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatWin extends AppCompatActivity {

    String receiveImg, receiveUid, receiveName, senderUid;
    CircleImageView profile;
    TextView receiverName;
    CardView sendbtn;
    EditText textMsg;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;

    public static String senderImg;
    public static String receiverImage;

    String senderRoom, receiverRoom;
    RecyclerView messageAdapter;
    ArrayList<MsgModelClass> messagesArrayList;
    MsgsAdaptor msgsAdaptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_win);

        Log.d("chatWin", "onCreate called");
        Toast.makeText(this, "Activity Created", Toast.LENGTH_SHORT).show();

        Log.d("chatWin", "setContentView called");

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        receiveName = getIntent().getStringExtra("receiveNameField");
        receiveImg = getIntent().getStringExtra("receiveImgField");
        receiveUid = getIntent().getStringExtra("receiveUidField");

        messagesArrayList = new ArrayList<>();

        sendbtn = findViewById(R.id.sendBtn);
        textMsg = findViewById(R.id.textMsg);
        receiverName = findViewById(R.id.receiverName);
        profile = findViewById(R.id.chatProfileImage);

        messageAdapter = findViewById(R.id.msgAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        messageAdapter.setLayoutManager(linearLayoutManager);

        msgsAdaptor = new MsgsAdaptor(chatWin.this, messagesArrayList);
        messageAdapter.setAdapter(msgsAdaptor);

        Picasso.get().load(receiveImg).into(profile);
        receiverName.setText("" + receiveName);

        senderUid = firebaseAuth.getUid();
        senderRoom = senderUid + receiveUid;
        receiverRoom = receiveUid + senderUid;

        DatabaseReference reference = database.getReference().child("user").child(firebaseAuth.getUid());
        DatabaseReference chatReference = database.getReference().child("chats").child(senderRoom).child("messages");

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    MsgModelClass messages = dataSnapshot.getValue(MsgModelClass.class);
                    messagesArrayList.add(messages);
                }
                msgsAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImg = snapshot.child("profilePic").getValue().toString();
                receiverImage = receiveImg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = textMsg.getText().toString();
                if(message.isEmpty()) {
                    Toast.makeText( chatWin.this, "Enter the msg first", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(chatWin.this, message, Toast.LENGTH_SHORT).show();
                textMsg.setText("");
                Date date = new Date();
                MsgModelClass messages = new MsgModelClass(message, senderUid, date.getTime());
                database = FirebaseDatabase.getInstance();
                database.getReference().child("chats").child(senderRoom).child("messages").push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        database.getReference().child("chats").child(receiverRoom).child("messages").push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                    }
                });
            }
        });
    }
}