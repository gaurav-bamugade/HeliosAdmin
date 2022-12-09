package com.example.adminsidedemoproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminsidedemoproject.Model.PartyNamesModel;
import com.example.adminsidedemoproject.PartyEntryDetails;
import com.example.adminsidedemoproject.R;

import java.util.ArrayList;

public class PartyNamesAdapter extends RecyclerView.Adapter<PartyNamesAdapter.ViewHolder> {
    Context context;
    ArrayList<PartyNamesModel> partyNamesModels;

    public PartyNamesAdapter(Context context, ArrayList<PartyNamesModel> partyNamesModels) {
        this.context = context;
        this.partyNamesModels = partyNamesModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.party_names_row_layout,parent,false);
        return new  ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PartyNamesModel ps= partyNamesModels.get(position);
        holder.partyName.setText(ps.getPartyName());
        holder.partyNameCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, PartyEntryDetails.class);
                i.putExtra("party_name",ps.getPartyName());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return partyNamesModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView partyName;
        CardView partyNameCard;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            partyName=itemView.findViewById(R.id.party_name);
            partyNameCard=itemView.findViewById(R.id.party_name_card);
        }
    }
}
