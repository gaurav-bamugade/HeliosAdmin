package com.example.adminsidedemoproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminsidedemoproject.Model.PartyNamesModel;
import com.example.adminsidedemoproject.Model.ProductsModel;
import com.example.adminsidedemoproject.R;

import java.util.ArrayList;

public class LowStockQuantityAdapter extends RecyclerView.Adapter<LowStockQuantityAdapter.ViewHolder> {
    Context context;
    ArrayList<ProductsModel> productsModels;

    public LowStockQuantityAdapter(Context context, ArrayList<ProductsModel> productsModels) {
        this.context = context;
        this.productsModels = productsModels;
    }

    @NonNull
    @Override
    public LowStockQuantityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.low_stock_quantity_layout,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LowStockQuantityAdapter.ViewHolder holder, int position) {
        ProductsModel ps= productsModels.get(position);
        holder.item_name_low_stock.setText(ps.getProductName());
        holder.available_low_number.setText(ps.getAvailableStock());
        holder.unit_type.setText(ps.getUnitType());
    }

    @Override
    public int getItemCount() {
        return productsModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
       TextView item_name_low_stock,available_low_number,unit_type;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name_low_stock=itemView.findViewById(R.id.item_name_low_stock);
            available_low_number=itemView.findViewById(R.id.available_low_number);
            unit_type=itemView.findViewById(R.id.unit_type);

        }
    }
}
