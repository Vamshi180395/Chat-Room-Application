package com.example.inclass11;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

public class ChatRoomActivity extends AppCompatActivity implements MyAdapter.ViewHolder.MaintainData2 {
    private static final  int GALLERY_CAPTURE = 100;
    int commentedposition=0;
    ImageView uploadimage;
    static final int REQ_ADD=1;
    final static String VALUE_KEY="message";
    FirebaseStorage storage=FirebaseStorage.getInstance();
String userid,username;
    TextView displayname,messagebox;
    DatabaseReference mDatabase,childObj;
    String message;
    Uri selectedImg;
    String imageuri=null;
    Messages deletedmessage,messagetocomment;
    private FirebaseAuth mAuth;
    String comment;
    ArrayList<Messages>  allmmsgslist=new ArrayList<Messages>();
    ArrayList<Messages> allmsgslist=new ArrayList<Messages>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        findViewByIDs();
        if (getIntent().getExtras() != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            childObj = mDatabase.child("users").child(user.getUid()).child("fullname");
            childObj.addValueEventListener(new ValueEventListener() {
                                               @Override
                                               public void onDataChange(DataSnapshot dataSnapshot) {
                                                    username = dataSnapshot.getValue(String.class);
                                                   displayname.setText(username);
                                               }

                                               @Override
                                               public void onCancelled(DatabaseError databaseError) {

                                               }
                                           });
            userid =  getIntent().getExtras().getString("UserID");
            mDatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference aexpref1= mDatabase.child("messages");
            aexpref1.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                    Messages msg = dataSnapshot.getValue(Messages.class);
                    allmsgslist.add(msg);
                    setRecycleView();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Messages msg = dataSnapshot.getValue(Messages.class);
                    allmsgslist.remove(msg);
                    setRecycleView();
                    Toast.makeText(ChatRoomActivity.this,"Message Deleted",Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
            setRecycleView();
        }
        uploadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_CAPTURE);
            }
        });
    }

    private void setRecycleView() {
        allmmsgslist.clear();
        allmmsgslist.addAll(allmsgslist);
        if(allmmsgslist.size()!=0){
            RecyclerView downrecycleview=(RecyclerView) findViewById(R.id.down_recycler_view);
            MyAdapter adapter = new MyAdapter(this, allmmsgslist,ChatRoomActivity.this);
            downrecycleview.setAdapter(adapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            layoutManager.setSmoothScrollbarEnabled(true);
            downrecycleview.setLayoutManager(layoutManager);

        }
    }

    private void findViewByIDs() {
        displayname=(TextView) findViewById(R.id.username);
        messagebox=(TextView) findViewById(R.id.messagetosend);
        uploadimage=(ImageView) findViewById(R.id.imageView2);

    }

    public void sendMessage(View view) {
        message = messagebox.getText().toString();
        if (message.length() == 0 ) {
            if(message.length() == 0){
                setResult(RESULT_CANCELED);
                Toast.makeText(this, "Please enter a message to proceed further", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference aexpref=mDatabase.child("messages").push();
            String msgkey= aexpref.getKey();
            if(imageuri==null) {

                Messages msg = new Messages(msgkey, message,userid, "",username,null);
                aexpref.setValue(msg);
            }
            else{
                Messages msg = new Messages(msgkey, message,userid, imageuri,username,null);
                aexpref.setValue(msg);
            }

            setRecycleView();
        }

    }

    public void logOut(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            Intent i=new Intent(ChatRoomActivity.this,LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            // No user is signed in
        }

    }

    @Override
    public void deleteMessage(int position) {
        deletedmessage=allmsgslist.get(position);
        allmsgslist.remove(deletedmessage);
        String keyforexpensetobedeleted  =deletedmessage.msgkey;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference aexpref=mDatabase.child("messages").child(keyforexpensetobedeleted);
        aexpref.removeValue();
        setRecycleView();
    }

    @Override
    public void commentMessage(int position) {
        commentedposition=position;
        Intent in=new Intent(ChatRoomActivity.this,CommentActivity.class);
        startActivityForResult(in,REQ_ADD);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==REQ_ADD && resultCode==RESULT_OK) {
            this.comment = data.getExtras().getString(VALUE_KEY);
            messagetocomment=allmsgslist.get(commentedposition);
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
            String date= sdf.format(cal.getTime()).toString();
            if(messagetocomment.comments==null) {
                ArrayList<Comment> comments = new ArrayList<Comment>();
                comments.add(new Comment(username, this.comment,date));
                messagetocomment.comments = comments;
            }
            else{
                messagetocomment.comments.add(new Comment(username, this.comment, date));
            }
            String keyforexpensetobedeleted  =messagetocomment.msgkey;
            mDatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference aexpref=mDatabase.child("messages").child(keyforexpensetobedeleted);
            aexpref.setValue(messagetocomment);
            allmsgslist.set(commentedposition,messagetocomment);
            setRecycleView();
        }

       else if(resultCode == RESULT_OK && requestCode==GALLERY_CAPTURE){
            selectedImg = data.getData();

            uploadimage.setDrawingCacheEnabled(true);
            uploadimage.buildDrawingCache();
            Bitmap bitmap = uploadimage.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data1 = baos.toByteArray();
            String path = "myImage/"+ UUID.randomUUID()+".png";
            StorageReference myImageRef = storage.getReference(path);
            UploadTask uploadTask = myImageRef.putBytes(data1);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference aexpref=mDatabase.child("messages").push();
                    String msgkey= aexpref.getKey();
                    Messages msg = new Messages(msgkey, " ",userid,downloadUrl.toString(),username,null);
                    aexpref.setValue(msg);
                }
            });

        }else if(resultCode == RESULT_CANCELED){

        }
    }
}
