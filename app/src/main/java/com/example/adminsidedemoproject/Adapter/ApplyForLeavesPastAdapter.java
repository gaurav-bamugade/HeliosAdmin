package com.example.adminsidedemoproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.example.adminsidedemoproject.LeaveDetailsActivity;
import com.example.adminsidedemoproject.Model.LeavesModelClass;
import com.example.adminsidedemoproject.R;

import java.util.ArrayList;

public class ApplyForLeavesPastAdapter extends RecyclerView.Adapter<ApplyForLeavesPastAdapter.ViewHolder> {

    Context context;
    ArrayList<LeavesModelClass> leaveModel;

    public ApplyForLeavesPastAdapter(Context context, ArrayList<LeavesModelClass> leaveModel) {
        this.context = context;
        this.leaveModel = leaveModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.leaves_request_row_layout,parent,false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeavesModelClass lmc=leaveModel.get(position);
        holder.leaveType.setText(lmc.getLeave_type()+" "+lmc.getNo_of_days()+"-Days");
        holder.leaveStatus.setText(lmc.getStatus());
        holder.leaveTime.setText(lmc.getFrom()+"  to  "+lmc.getTo());
        Resources res=context.getResources() ; //resource handle
        holder.leaveStatusColor.setBackgroundColor(res.getColor(R.color.green));
        holder.leave_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, LeaveDetailsActivity.class);
                i.putExtra("apply_for_leave_id",lmc.getApply_for_leave_id());
                i.putExtra("from",lmc.getFrom());
                i.putExtra("leave_type",lmc.getLeave_type());
                i.putExtra("no_of_days",lmc.getNo_of_days());
                i.putExtra("reason",lmc.getReason());
                i.putExtra("remark",lmc.getRemark());
                i.putExtra("status",lmc.getStatus());
                i.putExtra("to",lmc.getTo());
                i.putExtra("user_uid",lmc.getUser_uid());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return leaveModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView leaveType,leaveTime,leaveStatus;
        RelativeLayout leaveStatusColor;
        CardView leave_card;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            leaveType=itemView.findViewById(R.id.leave_type_text);
            leaveTime=itemView.findViewById(R.id.leave_time_text);
            leaveStatus=itemView.findViewById(R.id.leave_type_pending);
            leaveStatusColor=itemView.findViewById(R.id.leave_status_color);
            leave_card=itemView.findViewById(R.id.leave_card);


        }
    }
}
