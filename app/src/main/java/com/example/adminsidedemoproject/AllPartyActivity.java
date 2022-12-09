package com.example.adminsidedemoproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adminsidedemoproject.Adapter.PartyNamesAdapter;
import com.example.adminsidedemoproject.Adapter.PartyStockInOutAdapter;
import com.example.adminsidedemoproject.Adapter.ProductItemAdapter;
import com.example.adminsidedemoproject.Model.PartyNamesModel;
import com.example.adminsidedemoproject.Model.ProductsModel;
import com.example.adminsidedemoproject.Model.StockInOutModel;
import com.example.adminsidedemoproject.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class AllPartyActivity extends AppCompatActivity {
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
Button all_party_add_party_btn,addPartyButton;
RecyclerView  allEntryEv;
ArrayList<PartyNamesModel> partyNamesModel,searchModel;
PartyNamesAdapter partyNamesAdapter;
TextView to_pay,to_receive;
CardView search_cardView_party;
RelativeLayout tap_to_search_party;
ImageButton upcoming_img_arrow1_party,close_dialogue_top_addparty_button;
    Toolbar toolbar;
    EditText partyNameEt,partyMobEt;
    private Dialog partyDialogue;
    private DatabaseReference myref,mref2;
EditText search_card_ed;
    int toreceive,topay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_party);
        loadpurchasesales();
        setPartyDialog();
        toolbar=findViewById(R.id.party);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);
        myref=  FirebaseDatabase.getInstance().getReference().child("ProductCategoriesAndUnits").child("PartyDetails");

        if(myref!=null) {
            loadData();
        }else
        {
           // Toast.makeText(this, "not exist", Toast.LENGTH_SHORT).show();
        }
        allEntryEv=findViewById(R.id.all_entry_rv);
        partyNamesModel=new ArrayList<>();
        searchModel=new ArrayList<>();

        partyNamesAdapter=new PartyNamesAdapter(this,partyNamesModel);
        LinearLayoutManager linearLayout=new LinearLayoutManager(this);
        allEntryEv.setLayoutManager(linearLayout);
        allEntryEv.setAdapter(partyNamesAdapter);

        to_pay=findViewById(R.id.to_pay);
        to_receive=findViewById(R.id.to_receive);
        search_card_ed=findViewById(R.id.search_card_ed);

        //search_cardView_party= findViewById(R.id.search_cardView_party);
        all_party_add_party_btn=findViewById(R.id.all_party_add_party_btn);
        all_party_add_party_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partyDialogue.show();
            }
        });
        tap_to_search_party= findViewById(R.id.tap_to_search_party);
        upcoming_img_arrow1_party=findViewById(R.id.upcoming_img_arrow1_party);
        tap_to_search_party.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(search_card_ed.getVisibility()==View.GONE)
                {
                    search_card_ed.setVisibility(View.VISIBLE);
                    upcoming_img_arrow1_party.setImageResource(R.drawable.ic_arrow_up_24);

                }
                else
                {
                    if(search_card_ed.getVisibility()==View.VISIBLE)
                    {
                        search_card_ed.setVisibility(View.GONE);
                        upcoming_img_arrow1_party.setImageResource(R.drawable.ic_arrow_down_24);

                    }
                }
            }
        });


        search_card_ed.addTextChangedListener(new TextWatcher() {
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
                    partyNamesAdapter=new PartyNamesAdapter(AllPartyActivity.this,partyNamesModel);
                    allEntryEv.setAdapter(partyNamesAdapter);
                    partyNamesAdapter.notifyDataSetChanged();
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

    public void loadpurchasesales()
    {
        mref2= FirebaseDatabase.getInstance().getReference().child("StockInOut");
        if(mref2!=null)
        {
            mref2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for(DataSnapshot ds:snapshot.getChildren())
                    {
                      for(DataSnapshot ds2:ds.getChildren())
                      {
                          String StockType=ds2.child("StockType").getValue().toString();
                          String StockTotalAmount=ds2.child("StockDueAmount").getValue().toString();
                          int StockTotalamt=Integer.parseInt(StockTotalAmount);

                          if(StockType.equals("OUT"))
                          {
                              toreceive=toreceive+StockTotalamt;
                              // stock_total_purchase.setText(totalpurchase);
                              //Log.d("passes","true"+totalpurchase);

                          }
                          else if(StockType.equals("IN"))
                          {
                              topay=topay+StockTotalamt;
                              //stock_total_sales.setText(totalsales);
                              //Log.d("passes","true"+totalsales);

                          }

                      }
                        to_receive.setText(String.valueOf(toreceive));
                        to_pay.setText(String.valueOf(topay));
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
    public void loadData(){
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
                            String partyName=ds.child("partyName").getValue().toString();;
                            partyNamesModel.add(new PartyNamesModel(partyName));
                        }
                        partyNamesAdapter.notifyDataSetChanged();
                    }
                    else{
                       // Toast.makeText(AllPartyActivity.this, "not exist", Toast.LENGTH_SHORT).show();
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
    private void setPartyDialog() {
        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat FromToDateFormate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        partyDialogue =new Dialog(this);
        partyDialogue.setContentView(R.layout.add_party_dialogue);
        partyDialogue.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_box));
        partyDialogue.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        partyDialogue.setCancelable(true);


        partyNameEt=partyDialogue.findViewById(R.id.party_name_et);
        partyMobEt=partyDialogue.findViewById(R.id.party_mob_et);
        addPartyButton=partyDialogue.findViewById(R.id.add_party_button);
        close_dialogue_top_addparty_button=partyDialogue.findViewById(R.id.close_dialogue_top_addparty_button);
        close_dialogue_top_addparty_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partyDialogue.dismiss();
            }
        });
        HashMap<String,Object> map =new HashMap<>();

        addPartyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(partyNameEt.getText().toString().matches("") )
                {
                    Log.d("name1",partyNameEt.getText().toString()+partyMobEt.getText().toString());
                    partyMobEt.setError("Required");

                }
                if(partyMobEt.getText().toString().matches(""))
                {
                    partyNameEt.setError("Required");
                    Log.d("name2",partyNameEt.getText().toString()+partyMobEt.getText().toString());

                }
                if(!partyNameEt.getText().toString().isEmpty() && !partyMobEt.getText().toString().isEmpty())
                {
                    Log.d("name3",partyNameEt.getText().toString()+partyMobEt.getText().toString());
                    DatabaseReference data=FirebaseDatabase.getInstance().getReference();
                    map.put("partyName",partyNameEt.getText().toString());
                    map.put("partyMobile",partyMobEt.getText().toString());
                    data.child("ProductCategoriesAndUnits").child("PartyDetails").child(partyNameEt.getText().toString()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(AllPartyActivity.this, "Sucessfully Party Added", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }
    private void Filter(String text)
    {
        for(PartyNamesModel ps: partyNamesModel)
        {
            if(ps.getPartyName().equals(text))
            {
                searchModel.add(ps);
            }
        }
        partyNamesAdapter=new PartyNamesAdapter(AllPartyActivity.this,searchModel);
        allEntryEv.setAdapter(partyNamesAdapter);
        partyNamesAdapter.notifyDataSetChanged();
    }

  /*  public void loadData()
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
                        proditemAdapter.UpdateAdapter(getContext(),productsModels);
                    }
                    else{
                        Toast.makeText(getContext(), "not exist", Toast.LENGTH_SHORT).show();
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        else
        {
            Toast.makeText(this, "not exist", Toast.LENGTH_SHORT).show();

        }
    }*/
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