package com.example.adminsidedemoproject.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminsidedemoproject.Model.StockInOutModel;
import com.example.adminsidedemoproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProdDetailsEntryAdapter extends RecyclerView.Adapter<ProdDetailsEntryAdapter.ViewHolder> {
    Context context;
    ArrayList<StockInOutModel> stockInOutModels;

    private DatabaseReference myref;
    public ProdDetailsEntryAdapter(Context context, ArrayList<StockInOutModel> stockInOutModels) {
        this.context = context;
        this.stockInOutModels = stockInOutModels;
    }

    @NonNull
    @Override
    public ProdDetailsEntryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.all_entries_for_product_row_layout,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdDetailsEntryAdapter.ViewHolder holder, int position) {
        StockInOutModel si=stockInOutModels.get(position);
        Resources res = context.getResources();
        holder.entry_party_name.setText(si.getStockPartyName());
        holder.entry_party_update_time.setText(si.getStockDate()+","+si.getStockTime());
        holder.entry_stock_in_out_number.setText(si.getStockQuantity());
        holder.entry_stock_in_outunit.setText(si.getStockUnitType());

        holder.entry_party_availableStock_unitype.setText(si.getStockUnitType());
        holder.entry_type_totalamt.setText(si.getStockTotalAmount());


        if(si.getStockType().equals("IN"))
        {
            holder.stock_type_in_out.setText("Stock"+" "+si.getStockType());
            holder.stock_type_in_out.setTextColor(res.getColor(R.color.red));
        }
        else if(si.getStockType().equals("OUT"))
        {
            holder.stock_type_in_out.setText("Stock"+" "+si.getStockType());
            holder.stock_type_in_out.setTextColor(res.getColor(R.color.greendark));
        }
        checkRemainingStock(si.getProdId(),holder);

    }

    @Override
    public int getItemCount() {
        return stockInOutModels.size();
    }
    public void checkRemainingStock(String prodId, ViewHolder holder)
    {
        myref=  FirebaseDatabase.getInstance().getReference().child("AllProducts").child(prodId);
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String stockavailable=snapshot.child("AvailableStock").getValue().toString();
                holder.entry_available_stock_num.setText(stockavailable);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void update(ArrayList<StockInOutModel> stockInOutModels)
    {
        this.stockInOutModels = stockInOutModels;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView entry_party_name,entry_party_update_time,entry_stock_in_out_number,entry_stock_in_outunit,entry_available_stock_num,
        entry_party_availableStock_unitype,entry_type_totalamt,stock_type_in_out;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            entry_party_name=itemView.findViewById(R.id.entry_party_name);
            entry_party_update_time=itemView.findViewById(R.id.entry_party_update_time);
            entry_stock_in_out_number=itemView.findViewById(R.id.entry_stock_in_out_number);
            entry_stock_in_outunit=itemView.findViewById(R.id.entry_stock_in_outunit);
            entry_available_stock_num=itemView.findViewById(R.id.entry_available_stock_num);
            entry_party_availableStock_unitype=itemView.findViewById(R.id.entry_party_availableStock_unitype);
            entry_type_totalamt=itemView.findViewById(R.id.entry_type_totalamt);
            stock_type_in_out=itemView.findViewById(R.id.stock_type_in_out);

        }
    }

}
