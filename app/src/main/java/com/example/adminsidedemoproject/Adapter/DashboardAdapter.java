package com.example.adminsidedemoproject.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminsidedemoproject.AdminLoginActivity;
import com.example.adminsidedemoproject.Model.DashboardModel;
import com.example.adminsidedemoproject.R;
import com.example.adminsidedemoproject.ShowCalendar;
import com.example.adminsidedemoproject.ShowLeavesActivity;
import com.example.adminsidedemoproject.StockManageActivity;
import com.example.adminsidedemoproject.UserManageActivity;
import com.example.adminsidedemoproject.UserProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashViewHolder> {

    Context context;
    ArrayList<DashboardModel> dbm;
    private String currentuserid;
    private FirebaseAuth fireAuth;
    private DatabaseReference fireref;

/*
    public void setDbm(List<DashboardModel> dbm) {
        this.dbm = dbm;
    }*/
private FirebaseAuth useraut;
    public DashboardAdapter(Context context, ArrayList<DashboardModel> dbm) {
        this.context = context;
        this.dbm = dbm;
        fireAuth= FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public DashViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_recyclerview_item_layout,parent,false);

        return new DashViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DashViewHolder holder, int position) {
       DashboardModel bm=dbm.get(position);
        holder.option_name.setText(bm.getName());

        fireAuth=FirebaseAuth.getInstance();

        Picasso.get().load(bm.getIcon()).into(holder.option_Icon);
        holder.select_option_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             /*  if(bm.getName().equals("Attendance"))
                {
                    Intent i=new Intent(context, showCalendar.class);
                    context.startActivity(i);
                }
                if(bm.getName().equals("Apply For Leave"))
                {
                    Intent i= new Intent(context, ShowLeavesActivity.class);
                    context.startActivity(i);
                }
                if(bm.getName().equals("Approvals"))
                {
                    Intent i= new Intent(context, ShowLeavesActivity.class);
                    context.startActivity(i);
                }*/
                if(bm.getName().equals("Manage Stock"))
                {
                    Intent i= new Intent(context, StockManageActivity.class);
                    context.startActivity(i);
                }
                if(bm.getName().equals("Account Details"))
                {
                    Intent i= new Intent(context, UserProfileActivity.class);
                    context.startActivity(i);
                }
                if(bm.getName().equals("Apply For Leave"))
                {
                    Intent i= new Intent(context, ShowLeavesActivity.class);
                    context.startActivity(i);
                }
                if(bm.getName().equals("Attendance"))
                {
                    Intent i=new Intent(context, ShowCalendar.class);
                    context.startActivity(i);
                }
                if(bm.getName().equals("Generate-ID"))
                {
                    Intent i= new Intent(context, UserManageActivity.class);
                    context.startActivity(i);
                }
                if(bm.getName().equals("Logout"))
                {
                    AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(context);
                    alertDialog2.setTitle("Do you want to Logout?");
                    alertDialog2.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            updateuserstatus("offline");
                            fireAuth.signOut();

                            //Toast.makeText(MainActivity.this,"Successfully Logged out",Toast.LENGTH_SHORT).show();
                            sendUserToLoginActivity();

                        }
                    });
                    alertDialog2.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    alertDialog2.show();
                }
            }
        });

    }
    private void sendUserToLoginActivity() {
        Intent intent = new Intent(context, AdminLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        ((Activity)context).finish();;
    }
    @Override
    public int getItemCount() {
        if (dbm != null) {
            return dbm.size();
        }
        else {
            return 0;
        }


    }

    public class DashViewHolder extends RecyclerView.ViewHolder{
        TextView notification_badge,option_name;
        ImageView option_Icon;
        CardView select_option_cardview;
        public DashViewHolder(@NonNull View itemView) {
            super(itemView);
            notification_badge=itemView.findViewById(R.id.option_notification_badge);
            option_Icon=itemView.findViewById(R.id.option_icon);
            option_name=itemView.findViewById(R.id.option_name);
            select_option_cardview=itemView.findViewById(R.id.select_option_cardview);
        }
    }
    private void updateuserstatus(String  st)
    {
        fireref= FirebaseDatabase.getInstance().getReference();
        String saveCurrentTime,saveCurrentDate;
        Calendar calen=Calendar.getInstance();
        SimpleDateFormat currentDate= new SimpleDateFormat("MMM, dd, yyyy");
        saveCurrentDate=currentDate.format(calen.getTime());

        SimpleDateFormat CurrentTime= new SimpleDateFormat("hh: mm: a");
        saveCurrentTime=CurrentTime.format(calen.getTime());
        HashMap<String, Object> onlineState = new HashMap<>();
        onlineState.put("time",saveCurrentTime);
        onlineState.put("Date",saveCurrentDate);
        onlineState.put("state",st);
        fireAuth= FirebaseAuth.getInstance();
        //currentuserid=fireauth.getCurrentUser().getUid();
        FirebaseUser mFirebaseUser = fireAuth.getCurrentUser();
        if(mFirebaseUser != null) {
            currentuserid = mFirebaseUser.getUid(); //Do what you need to do with the id
            fireref.child("ExistingUsers").child(currentuserid).child("userstate")
                    .updateChildren(onlineState);
        }
        //Log.d("checkid",fireAuth.getCurrentUser().getUid());

    }
}
