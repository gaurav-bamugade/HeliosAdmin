package com.example.adminsidedemoproject.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminsidedemoproject.EditProdDetails;
import com.example.adminsidedemoproject.Model.ProductsModel;

import com.example.adminsidedemoproject.ProdDetailsActivity;
import com.example.adminsidedemoproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.List;

public class ProductItemAdapter extends RecyclerView.Adapter<ProductItemAdapter.Viewholder> {

    Context context;
    List<ProductsModel> productsModels;
/*    FirebaseDatabase dat=FirebaseDatabase.getInstance();
    DatabaseReference mr=dat.getReference();*/
DatabaseReference stockInOutRef,allProductRef,productCategoryWiseRef;


    public ProductItemAdapter(Context context, List<ProductsModel> productsModels) {
        this.context = context;
        this.productsModels = productsModels;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.stock_item_card_row_layout,parent,false);
        return new Viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        /*holder.prodQty.setText(prodm.get(position).getProductQty());*/

        ProductsModel prodMod=productsModels.get(position);
        stockInOutRef= FirebaseDatabase.getInstance().getReference();
        allProductRef= FirebaseDatabase.getInstance().getReference();
        productCategoryWiseRef= FirebaseDatabase.getInstance().getReference();

         Picasso.get().load(prodMod.getProdImage()).into(holder.product_rc_im);
         holder.product_rc_name.setText(prodMod.getProductName());
         holder.product_rc_update_time.setText(prodMod.getUpdateTime());
         holder.product_rc_stock_in.setText(prodMod.getStockIn());
         holder.product_rc_stock_out.setText(prodMod.getStockOut());
         holder.product_rc_stock_availableStock.setText(prodMod.getAvailableStock());
         holder.prod_item_card.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent i=new Intent(context,ProdDetailsActivity.class);
                 i.putExtra("product_id",prodMod.getProdId());
                 i.putExtra("product_name",prodMod.getProductName());
                 i.putExtra("product_unit",prodMod.getUnitType());
                 i.putExtra("product_stock_opening",prodMod.getOpeningStock());
                 i.putExtra("product_stock_In",prodMod.getStockIn());
                 i.putExtra("product_stock_out",prodMod.getStockOut());
                 i.putExtra("product_stock_available",prodMod.getAvailableStock());
                 i.putExtra("product_stock_image",prodMod.getProdImage());
                 context.startActivity(i);
             }
         });
        holder.product_rc_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(context);
                alertDialog2.setTitle("Please Choose the Option ?");
                alertDialog2.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i=new Intent(context, EditProdDetails.class);
                        i.putExtra("AvailableStock",prodMod.getAvailableStock());
                        i.putExtra("Category",prodMod.getCategory());
                        i.putExtra("LowQuantity",prodMod.getLowQuantity());
                        i.putExtra("MrpPrice",prodMod.getMrpPrice());
                        i.putExtra("ProdId",prodMod.getProdId());
                        i.putExtra("ProdImage",prodMod.getProdImage());
                        i.putExtra("ProductDescription",prodMod.getProductDescription());
                        i.putExtra("ProductName",prodMod.getProductName());
                        i.putExtra("ProductPurchasePrice",prodMod.getProductPurchasePrice());
                        i.putExtra("SellPrice",prodMod.getSellPrice());
                        i.putExtra("Timestamp",prodMod.getTimestamp());
                        i.putExtra("UnitType",prodMod.getUnitType());
                        i.putExtra("UpdateTime",prodMod.getUpdateTime());
                        i.putExtra("UploadTime",prodMod.getUploadTime());
                        i.putExtra("StockIn",prodMod.getStockIn());
                        i.putExtra("StockOut",prodMod.getStockOut());
                        i.putExtra("OpeningStock",prodMod.getOpeningStock());
                        context.startActivity(i);
                    }
                });
                alertDialog2.setNeutralButton("Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                allProductRef.child("AllProducts").child(prodMod.getProdId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        stockInOutRef.child("StockInOut").child(prodMod.getProdId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                productCategoryWiseRef.child("ProductCategoriesAndUnits").child("PartyDetails")
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                for(DataSnapshot ds:snapshot.getChildren())
                                                                {
                                                                    String partyname=ds.child("partyName").getValue().toString();
                                                                    productCategoryWiseRef.child("ProductCategoriesAndUnits").child("PartyDetails").child(partyname).child("StockInOut")
                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                    for(DataSnapshot ds3:snapshot.getChildren())
                                                                                    {
                                                                                        String prodId=ds3.child("ProdId").getValue().toString();
                                                                                        String stockid=ds3.child("StockId").getValue().toString();
                                                                                        if(prodId.equals(prodId))
                                                                                        {
                                                                                            productCategoryWiseRef.child("ProductCategoriesAndUnits").child("PartyDetails").child(partyname).child("StockInOut")
                                                                                                    .child(stockid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            Toast.makeText(context, "Successfully removed", Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                }
                                                                            });
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                            }
                                        });
                                    }
                                });
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
        });
        holder.product_rc_start_stock.setText(prodMod.getOpeningStock()+" "+prodMod.getUnitType());

    }

    @Override
    public int getItemCount() {
        return productsModels.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ImageView product_rc_im,product_rc_edit;
        TextView product_rc_name,product_rc_update_time,product_rc_stock_in,product_rc_stock_out
                ,product_rc_stock_availableStock,product_rc_start_stock;
        CardView prod_item_card;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
        /*    prodQty =itemView.findViewById(R.id.prod_qty);*/
            product_rc_im =itemView.findViewById(R.id.product_rc_im);
            product_rc_edit =itemView.findViewById(R.id.product_rc_edit);
            product_rc_name=itemView.findViewById(R.id.product_rc_name);
            product_rc_update_time=itemView.findViewById(R.id.product_rc_update_time);
            product_rc_stock_in=itemView.findViewById(R.id.product_rc_stock_in);
            product_rc_stock_out=itemView.findViewById(R.id.product_rc_stock_out);
            product_rc_stock_availableStock=itemView.findViewById(R.id.product_rc_stock_availableStock);
            prod_item_card=itemView.findViewById(R.id.prod_item_card);
            product_rc_start_stock=itemView.findViewById(R.id.product_rc_start_stock);

        }
    /*    private void setData(int post)
        {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(itemView.getContext(), ProdDetailsActivity.class);
                    i.putExtra("prodid",prodm.get(post).getProductId());
                    itemView.getContext().startActivity(i);

                }
            });


        }*/
    }
    public void UpdateAdapter(Context context, List<ProductsModel> productsModels){
        this.context = context;
        this.productsModels = productsModels;
        notifyDataSetChanged();
    }
}










  /*    editOpts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(context,R.style.Theme_AppCompat_Light_Dialog)
                            .setTitle("Products")
                            .setMessage("Please choose and option!!")
                            .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i=new Intent(itemView.getContext(), editProdDetailsActivity.class);
                                    i.putExtra("prodid",prodm.get(post).getProductId());
                                    itemView.getContext().startActivity(i);
                                }
                            })
                            .setNeutralButton("Remove", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mr.child("products").child(prodm.get(post).getProductId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(context,"Removed Product Successfully", Toast.LENGTH_SHORT).show();
                                                notifyDataSetChanged();
                                            }
                                            else
                                            {
                                                Toast.makeText(context,"Failed to delete", Toast.LENGTH_SHORT).show();

                                            }

                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Cancel",null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();




                }
            });*/
