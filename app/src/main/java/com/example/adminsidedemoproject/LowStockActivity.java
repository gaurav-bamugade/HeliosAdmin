package com.example.adminsidedemoproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.adminsidedemoproject.Adapter.LowStockQuantityAdapter;
import com.example.adminsidedemoproject.Adapter.ProductItemAdapter;
import com.example.adminsidedemoproject.Model.ProductsModel;
import com.example.adminsidedemoproject.Utility.NetworkChangeListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LowStockActivity extends AppCompatActivity {
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();

    RecyclerView low_stock_quantity;
ArrayList<ProductsModel> productsModels,searchModel;
LowStockQuantityAdapter lowStockQuantityAdapter;
private DatabaseReference myref;
LinearLayout show_not_found_low_stock;
    Toolbar toolbar;
    ArrayList<Integer> check;
    int stock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_low_stock);
        toolbar=findViewById(R.id.low_stock_quantitiy_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        check=new ArrayList<>();
        show_not_found_low_stock=findViewById(R.id.show_not_found_low_stock);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);
        low_stock_quantity=findViewById(R.id.low_stock_quantity);
        myref=  FirebaseDatabase.getInstance().getReference().child("AllProducts");
        if(myref!=null) {
            getData();

        }else
        {
            //Toast.makeText(this, "not exist", Toast.LENGTH_SHORT).show();
        }
        productsModels=new ArrayList<>();
        RecyclerView.LayoutManager lm=new LinearLayoutManager(this);
        low_stock_quantity.setLayoutManager(lm);
        lowStockQuantityAdapter=new LowStockQuantityAdapter(this,productsModels);
        low_stock_quantity.setAdapter(lowStockQuantityAdapter);
    }


    private void getData()
    {
        //myref.child("AllProducts");
        if(myref!=null)
        {
            myref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.exists())
                    {
                        productsModels.clear();
                        for(DataSnapshot ds:snapshot.getChildren())
                        {
                            String AvailableStock=ds.child("AvailableStock").getValue().toString();;
                            String Category=ds.child("Category").getValue().toString();
                            String LowQuantity =ds.child("LowQuantity").getValue().toString();
                            String MrpPrice =ds.child("MrpPrice").getValue().toString();
                            String ProdId =ds.child("ProdId").getValue().toString();
                            String ProdImage=ds.child("ProdImage").getValue().toString();
                            String ProductDescription=ds.child("ProductDescription").getValue().toString();;
                            String ProductName=ds.child("ProductName").getValue().toString();
                            String ProductPurchasePrice =ds.child("ProductPurchasePrice").getValue().toString();
                            String SellPrice=ds.child("SellPrice").getValue().toString();
                            String Timestamp=ds.child("Timestamp").getValue().toString();
                            String UnitType=ds.child("UnitType").getValue().toString();
                            String UpdateTime=ds.child("UpdateTime").getValue().toString();
                            String stockIn=ds.child("StockIn").getValue().toString();
                            String stockOut=ds.child("StockOut").getValue().toString();
                            String UploadTime=ds.child("UploadTime").getValue().toString();
                            String OpeningStock=ds.child("OpeningStock").getValue().toString();
                            int lowstockquantity=Integer.parseInt(LowQuantity);
                            int availableStock=Integer.parseInt(AvailableStock);
                            if( lowstockquantity>=availableStock)
                            {
                                Log.d("lowstock",String.valueOf(lowstockquantity==availableStock));
                                productsModels.add(new ProductsModel(AvailableStock,Category
                                        ,LowQuantity,MrpPrice,ProdId,ProdImage,ProductDescription,ProductName
                                        ,ProductPurchasePrice,SellPrice,Timestamp,UnitType,
                                        UpdateTime,UploadTime,stockIn,stockOut,OpeningStock));
                                stock=stock+1;
                            }
                            else
                            {
                                stock=stock+0;
                            }


                        }
                        if(stock==0)
                        {
                            show_not_found_low_stock.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            show_not_found_low_stock.setVisibility(View.INVISIBLE);
                        }
                        Log.d("stocklow",String.valueOf(stock));
                        lowStockQuantityAdapter.notifyDataSetChanged();
                    }
                    else{
                       // Toast.makeText(LowStockActivity.this, "not exist", Toast.LENGTH_SHORT).show();

                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        else
        {
           // Toast.makeText(LowStockActivity.this, "not exist", Toast.LENGTH_SHORT).show();

        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i=new Intent(this,StockManageActivity.class);
                startActivity(i);
                return true;

        }
        return super.onOptionsItemSelected(item);
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