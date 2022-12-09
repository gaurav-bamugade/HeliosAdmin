package com.example.adminsidedemoproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.adminsidedemoproject.Adapter.PartyNamesAdapter;
import com.example.adminsidedemoproject.Adapter.PartyStockInOutAdapter;
import com.example.adminsidedemoproject.Model.PartyNamesModel;
import com.example.adminsidedemoproject.Model.StockInOutModel;
import com.example.adminsidedemoproject.Utility.NetworkChangeListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PartyEntryDetails extends AppCompatActivity {
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();

    RecyclerView  allEntryEv;
    ArrayList<StockInOutModel> stockInOutModels;
    PartyStockInOutAdapter partyStockInOutAdapter;
    private DatabaseReference myref;
    String partyName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_entry_details);
        Intent i=getIntent();
        partyName=i.getStringExtra("party_name");
        allEntryEv=findViewById(R.id.party_entry_rc);
        myref=  FirebaseDatabase.getInstance().getReference().child("ProductCategoriesAndUnits").child("PartyDetails").child(partyName).child("StockInOut");
        if(myref!=null) {
            loadData();
        }else
        {
           // Toast.makeText(this, "not exist", Toast.LENGTH_SHORT).show();
        }


        stockInOutModels=new ArrayList<>();
        partyStockInOutAdapter=new PartyStockInOutAdapter(this,stockInOutModels);
        LinearLayoutManager linearLayout=new LinearLayoutManager(this);
        allEntryEv.setLayoutManager(linearLayout);
        allEntryEv.setAdapter(partyStockInOutAdapter);

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
                        partyStockInOutAdapter.notifyDataSetChanged();

                    }
                    else{
                      //  Toast.makeText(PartyEntryDetails.this, "not exist", Toast.LENGTH_SHORT).show();
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        else
        {
           // Toast.makeText(this, "not exist", Toast.LENGTH_SHORT).show();

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