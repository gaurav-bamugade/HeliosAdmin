package com.example.adminsidedemoproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class AllProductActivity extends AppCompatActivity {
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();

    EditText search_prod;
    RecyclerView allProdRc;
    List<ProductsModel> productsModels,searchModel;
    ProductItemAdapter proditemAdapter;
    Button addStockItem,add_gategory_button;
    private DatabaseReference myref;
    TextView in_selling_price,in_purchase_price;
    int sellingPrice,purchasaePrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_product);
        search_prod=findViewById(R.id.search_prod);
        allProdRc=findViewById(R.id.all_prod_rc);
        addStockItem=findViewById(R.id.all_add_stock_item);
        add_gategory_button=findViewById(R.id.all_add_gategory_button);
        myref=  FirebaseDatabase.getInstance().getReference().child("AllProducts");
        if(myref!=null) {
            getData();

        }else
        {
           // Toast.makeText(AllProductActivity.this, "not exist", Toast.LENGTH_SHORT).show();
        }


        productsModels=new ArrayList<>();
        searchModel=new ArrayList<>();
        RecyclerView.LayoutManager lm=new LinearLayoutManager(AllProductActivity.this);
        allProdRc.setLayoutManager(lm);
        proditemAdapter=new ProductItemAdapter(AllProductActivity.this,productsModels);
        allProdRc.setAdapter(proditemAdapter);
        in_selling_price= findViewById(R.id.in_selling_price);
        in_purchase_price= findViewById(R.id.in_purchase_price);
        add_gategory_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(AllProductActivity.this,AddCategoryForProduct.class);
                startActivity(i);
            }
        });
        addStockItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(AllProductActivity.this,NewProductUploadActivity.class);
                startActivity(i);
            }
        });
        search_prod.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                   /* proditemAdapter=new ProductItemAdapter(AllProductActivity.this,productsModels);
                    allProdRc.setAdapter(proditemAdapter);
                    proditemAdapter.notifyDataSetChanged();*/

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchModel.clear();
                if(s.toString().isEmpty())
                {
                    proditemAdapter=new ProductItemAdapter(AllProductActivity.this,productsModels);
                    allProdRc.setAdapter(proditemAdapter);
                    proditemAdapter.notifyDataSetChanged();
                }
                else
                {
                    Filter(s.toString());
                }
            }
            @Override
            public void afterTextChanged(Editable s)
            {
               /* searchModel.clear();
                if(s.toString().isEmpty())
                {
                    proditemAdapter=new ProductItemAdapter(AllProductActivity.this,productsModels);
                    allProdRc.setAdapter(proditemAdapter);
                    proditemAdapter.notifyDataSetChanged();
                }
                else
                {
                    Filter(s.toString());
                }*/
            }

        });
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
                            productsModels.add(new ProductsModel(AvailableStock,Category
                                    ,LowQuantity,MrpPrice,ProdId,ProdImage,ProductDescription,ProductName
                                    ,ProductPurchasePrice,SellPrice,Timestamp,UnitType,UpdateTime,UploadTime,stockIn,stockOut,OpeningStock));
                        }
                        proditemAdapter.notifyDataSetChanged();
                        proditemAdapter.UpdateAdapter(AllProductActivity.this,productsModels);
                    }
                    else{
                       // Toast.makeText(AllProductActivity.this, "not exist", Toast.LENGTH_SHORT).show();
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        else
        {
           // Toast.makeText(AllProductActivity.this, "not exist", Toast.LENGTH_SHORT).show();

        }


    }
    private void Filter(String text)
    {
        for(ProductsModel ps: productsModels)
        {
            if(ps.getProductName().equals(text))
            {
                searchModel.add(ps);
            }
        }
        proditemAdapter=new ProductItemAdapter(this,searchModel);
        allProdRc.setAdapter(proditemAdapter);
        proditemAdapter.notifyDataSetChanged();
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