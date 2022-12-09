package com.example.adminsidedemoproject;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminsidedemoproject.Adapter.PartyStockInOutAdapter;
import com.example.adminsidedemoproject.Adapter.ProdDetailsEntryAdapter;
import com.example.adminsidedemoproject.Model.StockInOutModel;
import com.example.adminsidedemoproject.Utility.NetworkChangeListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ProdDetailsActivity extends AppCompatActivity {
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();

    private ImageView prodImg;
    private TextView stock_opening_tx,stock_total_purchase,stock_total_sales;
    private DatabaseReference myref,calculatetotalpurchasesells;
    private String pid;
    Button addStockInButton,stockOutButton;
    String prodId,prodNm,product_unit,product_stock_opening,product_stock_In,product_stock_out,product_stock_available,product_stock_image;
    TextView opening_stock_number,product_details_unit_type,details_stock_in_number,details_stock_in_unit_type,
            stock_out_number,stock_out_unittype,remaining_stock,remaining_stock_unitytype;
    RecyclerView allEntryEv;
    ArrayList<StockInOutModel> stockInOutModels;
    ProdDetailsEntryAdapter prodDetailsEntryAdapter;
    String partyName;
    int totalpurchase,totalsales;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        initialize();
        /*prodName=findViewById(R.id.details_prod_name);
        prodprice=findViewById(R.id.details_prod_price);
        prodcategory=findViewById(R.id.details_prod_category);
        proddesc=findViewById(R.id.details_prod_desc);*/
       // myref=  FirebaseDatabase.getInstance().getReference();

        allEntryEv=findViewById(R.id.entries_rc);

         Intent i=getIntent();
        prodId=i.getStringExtra("product_id");
        prodNm=i.getStringExtra("product_name");
        product_unit=i.getStringExtra("product_unit");
        product_stock_opening=i.getStringExtra("product_stock_opening");
        product_stock_In=i.getStringExtra("product_stock_In");
        product_stock_out=i.getStringExtra("product_stock_out");
        product_stock_available=i.getStringExtra("product_stock_available");
        product_stock_image=i.getStringExtra("product_stock_image");
        stock_total_purchase=findViewById(R.id.stock_total_purchase);
        stock_total_sales=findViewById(R.id.stock_total_sales);

        myref=  FirebaseDatabase.getInstance().getReference().child("StockInOut").child(prodId);
        if(myref!=null) {
            loadData();
            loadpurchasesales();

        }else
        {
          //  Toast.makeText(this, "not exist", Toast.LENGTH_SHORT).show();
        }


        Picasso.get().load(product_stock_image).into(prodImg);
        opening_stock_number.setText(product_stock_opening);
        details_stock_in_number.setText(product_stock_In);
        remaining_stock_unitytype.setText(product_unit);
        product_details_unit_type.setText(product_unit);
        details_stock_in_unit_type.setText(product_unit);
        stock_out_unittype.setText(product_unit);
        stock_out_number.setText(product_stock_out);
        remaining_stock.setText(product_stock_available);


        stockInOutModels=new ArrayList<>();
        prodDetailsEntryAdapter=new ProdDetailsEntryAdapter(this,stockInOutModels);
        LinearLayoutManager linearLayout=new LinearLayoutManager(this);
        allEntryEv.setLayoutManager(linearLayout);
        allEntryEv.setAdapter(prodDetailsEntryAdapter);
    }
    public void initialize(){
        addStockInButton=findViewById(R.id.add_stock_in_button);
        prodImg=findViewById(R.id.product_detail_image);
        stockOutButton=findViewById(R.id.stock_out_button);
        opening_stock_number=findViewById(R.id.opening_stock_number);
        product_details_unit_type=findViewById(R.id.product_details_unit_type);
        details_stock_in_number=findViewById(R.id.details_stock_in_number);
        details_stock_in_unit_type=findViewById(R.id.details_stock_in_unit_type);
        stock_out_number=findViewById(R.id.stock_out_number);
        stock_out_unittype=findViewById(R.id.stock_out_unittype);
        remaining_stock=findViewById(R.id.remaining_stock);
        remaining_stock_unitytype=findViewById(R.id.remaining_stock_unitytype);

        addStockInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ProdDetailsActivity.this,AddStockActivity.class);
                i.putExtra("product_id",prodId);
                i.putExtra("product_name",prodNm);
                i.putExtra("product_unit",product_unit);
                startActivity(i);
            }
        });
        stockOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ProdDetailsActivity.this,OutStockActivity.class);
                i.putExtra("product_id",prodId);
                i.putExtra("product_name",prodNm);
                i.putExtra("product_unit",product_unit);
                startActivity(i);
            }
        });
    }

    public void loadpurchasesales()
    {
        if(myref!=null)
        {
            myref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for(DataSnapshot ds:snapshot.getChildren())
                    {
                        String StockType=ds.child("StockType").getValue().toString();
                        String StockTotalAmount=ds.child("StockTotalAmount").getValue().toString();
                        int StockTotalamt=Integer.parseInt(StockTotalAmount);

                        if(StockType.equals("IN"))
                        {
                            totalpurchase=totalpurchase+StockTotalamt;
                           // stock_total_purchase.setText(totalpurchase);
                            Log.d("passes","true"+totalpurchase);

                        }
                        else if(StockType.equals("OUT"))
                        {
                            totalsales=totalpurchase+StockTotalamt;
                            //stock_total_sales.setText(totalsales);
                            //Log.d("passes","true"+totalsales);

                        }

                    }
                    stock_total_purchase.setText(String.valueOf(totalpurchase));
                    stock_total_sales.setText(String.valueOf(totalsales));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void loadData()
    {
        if(myref!=null)
        {
            myref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.exists())
                    {
                        for(DataSnapshot ds:snapshot.getChildren())
                        {
                            String ProdId=ds.child("ProdId").getValue().toString();;
                            String StockDate=ds.child("StockDate").getValue().toString();
                            String StockTime =ds.child("StockTime").getValue().toString();
                            String StockPurchaseSalesNumber =ds.child("StockPurchaseSalesNumber").getValue().toString();
                            String StockPartyName =ds.child("StockPartyName").getValue().toString();
                            String StockQuantity=ds.child("StockQuantity").getValue().toString();
                            String StockPurchaseSalesPrice=ds.child("StockPurchaseSalesPrice").getValue().toString();;
                            String StockTotalAmount=ds.child("StockTotalAmount").getValue().toString();
                            String StockAmountReceivedPaid =ds.child("StockAmountReceivedPaid").getValue().toString();
                            String StockDueAmount=ds.child("StockDueAmount").getValue().toString();
                            String StockRemark=ds.child("StockRemark").getValue().toString();
                            String StockId=ds.child("StockId").getValue().toString();
                            String StockType=ds.child("StockType").getValue().toString();
                            String StockItemName=ds.child("StockItemName").getValue().toString();
                            String StockUnitType=ds.child("StockUnitType").getValue().toString();


                            stockInOutModels.add(new StockInOutModel(ProdId,StockDate,StockTime,StockPurchaseSalesNumber,
                                    StockPartyName,StockQuantity,StockPurchaseSalesPrice,StockTotalAmount,StockAmountReceivedPaid,
                                    StockDueAmount,StockRemark,StockId,StockType,StockItemName,StockUnitType));
                        }
                        prodDetailsEntryAdapter.notifyDataSetChanged();

                    }
                    else{
                       // Toast.makeText(ProdDetailsActivity.this, "not exist", Toast.LENGTH_SHORT).show();
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        else
        {
            //Toast.makeText(this, "not exist", Toast.LENGTH_SHORT).show();

        }
    }
    @Override
    protected void onStart() {
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}





























 /*       myref.child("products").child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot ds) {

                //String prodid=ds.child("prod_id").getValue().toString();;
                String prodDesc=ds.child("product_description").getValue().toString();
                String prodNames=ds.child("product_name").getValue().toString();
                String prodPrice=ds.child("product_price").getValue().toString();
                //String stockCount=ds.child("stock_count").getValue().toString();
                String prodCate=ds.child("category").getValue().toString();
                String prodimg=ds.child("product_image_url").getValue().toString();
                Picasso.get().load(prodimg).placeholder(R.drawable.adminicon).into(prodImg);
                prodName.setText(prodNames);
                prodprice.setText(prodPrice);
                prodcategory.setText(prodCate);
                proddesc.setText(prodDesc);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });*/