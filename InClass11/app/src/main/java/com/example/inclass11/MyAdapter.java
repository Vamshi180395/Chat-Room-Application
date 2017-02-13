package com.example.inclass11;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Rama Vamshi Krishna on 10/19/2016.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Messages> msgslist;
    List<Messages> completelist;
    private Context mContext;
    Uri imageuri,imageuri1;


    static ChatRoomActivity activity;
    public MyAdapter(Context context, List<Messages> itemslist, ChatRoomActivity activity) {
        msgslist = itemslist;
        mContext = context;
        this.activity= activity;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.row_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder viewHolder, final int position) {
        Date date1 = null;
        Messages msg = msgslist.get(position);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ImageView cimg=viewHolder.cimage;
        ImageView dimg = viewHolder.dimage;
        ImageView msgimg = viewHolder.msgomg;
        TextView textViewtemp = viewHolder.txtmsg;
        TextView sendername = viewHolder.sendernamee;
        LinearLayout layouutt=viewHolder.layout;
        textViewtemp.setText(msg.message);
           imageuri= Uri.parse("android.resource://com.example.inclass11/drawable/comment");
        cimg.setImageURI(imageuri);
        imageuri1= Uri.parse("android.resource://com.example.inclass11/drawable/delete");
        dimg.setImageURI(imageuri1);
        sendername.setText(msg.userfullname);
        if(msg.userid.equals(user.getUid())){
            dimg.setVisibility(View.VISIBLE);
        }
        if(!msg.userid.equals(user.getUid())){
            dimg.setVisibility(View.INVISIBLE);
        }
        if(msg.comments!=null && msg.comments.size()!=0){


            for(int i = 0; i < msg.comments.size(); i++){
                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                try {
                    date1=format.parse(msg.comments.get(i).commentedtime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                PrettyTime p = new PrettyTime();
                final LinearLayout ll = new LinearLayout(activity);
                ll.setOrientation(LinearLayout.VERTICAL);
                TextView iv =new TextView(activity);
                TextView ts =new TextView(activity);
                final LinearLayout l2 = new LinearLayout(activity);
                l2.setOrientation(LinearLayout.HORIZONTAL);
                TextView tv=new TextView(activity);
                tv.setText(msg.comments.get(i).commentorname);
                iv.setTextSize(18);
                iv.setTextColor(Color.DKGRAY);
                iv.setText(msg.comments.get(i).comment);
                ts.setText("        "+p.format(date1));
                l2.addView(tv);
                l2.addView(ts);
                ll.addView(iv);
                ll.addView(l2);
                layouutt.addView(ll);

            }
        }
        cimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

activity.commentMessage(position);

            }
        });
        dimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.deleteMessage(position);
            }
        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return msgslist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtmsg,sendernamee;
        public EditText commentt;
        public ImageView dimage,cimage,msgomg;
        MaintainData2 mdata2;
        LinearLayout layout;
        public ViewHolder(View itemView) {
            super(itemView);
            txtmsg = (TextView) itemView.findViewById(R.id.txtnames);
            dimage = (ImageView) itemView.findViewById(R.id.deleteimage);
            cimage = (ImageView) itemView.findViewById(R.id.commentimage);
            sendernamee=(TextView) itemView.findViewById(R.id.sendername);
            layout=(LinearLayout) itemView.findViewById(R.id.lauput);
        }
        public interface MaintainData2{
            public void deleteMessage(int position);
            public void commentMessage(int position);
        }

    }



}

