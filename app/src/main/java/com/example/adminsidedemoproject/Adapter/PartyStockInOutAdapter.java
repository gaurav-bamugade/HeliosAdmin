package com.example.adminsidedemoproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminsidedemoproject.Model.StockInOutModel;
import com.example.adminsidedemoproject.R;

import java.util.ArrayList;

public class PartyStockInOutAdapter extends RecyclerView.Adapter<PartyStockInOutAdapter.ViewHolder> {

    Context context;
    ArrayList<StockInOutModel> stockInOutModels;

    public PartyStockInOutAdapter(Context context, ArrayList<StockInOutModel> stockInOutModels) {
        this.context = context;
        this.stockInOutModels = stockInOutModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.party_entry_row_layout,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StockInOutModel stk=stockInOutModels.get(position);
        holder.party_rc_partyname.setText(stk.getStockPartyName());
        holder.party_rc_updateTime.setText(stk.getStockDate()+" "+stk.getStockTime());
        holder.party_rc_purchase_sales.setText(stk.getStockPurchaseSalesNumber());
        holder.party_rc_pay_receive_amount.setText(stk.getStockDueAmount());
        holder.party_rc_itemname.setText(stk.getStockItemName());
        holder.party_rc_quantity.setText(stk.getStockQuantity());
        holder.party_rc_quantity_unittype.setText(stk.getStockUnitType());
        holder.party_rc_purchase_sales_amount.setText(stk.getStockPurchaseSalesPrice());
        holder.party_rc_totalAmount.setText(stk.getStockTotalAmount());
        holder.totalM.setText(stk.getStockTotalAmount());
        if(stk.getStockType().equals("IN"))
        {
            holder.party_rc_pay_receive.setText("TO PAY");
        }
        else if(stk.getStockType().equals("OUT"))
        {
            holder.party_rc_pay_receive.setText("TO RECEIVE");
        }


    }

    @Override
    public int getItemCount() {
        return stockInOutModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView party_rc_partyname,party_rc_updateTime,party_rc_purchase_sales,
                party_rc_pay_receive,party_rc_pay_receive_amount,party_rc_itemname,
                party_rc_quantity,party_rc_quantity_unittype,party_rc_purchase_sales_amount,
                party_rc_totalAmount,totalM;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            party_rc_partyname=itemView.findViewById(R.id.party_rc_partyname);
            party_rc_updateTime=itemView.findViewById(R.id.party_rc_updateTime);
            party_rc_purchase_sales=itemView.findViewById(R.id.party_rc_purchase_sales);
            party_rc_pay_receive=itemView.findViewById(R.id.party_rc_pay_receive);
            party_rc_pay_receive_amount=itemView.findViewById(R.id.party_rc_pay_receive_amount);
            party_rc_quantity=itemView.findViewById(R.id.party_rc_quantity);
            party_rc_quantity_unittype=itemView.findViewById(R.id.party_rc_quantity_unittype);
            party_rc_itemname=itemView.findViewById(R.id.party_rc_itemname);
            party_rc_purchase_sales_amount=itemView.findViewById(R.id.party_rc_purchase_sales_amount);
            party_rc_totalAmount=itemView.findViewById(R.id.party_rc_totalAmount);
            totalM=itemView.findViewById(R.id.totalM);




        }
    }
}
