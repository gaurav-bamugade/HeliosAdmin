package com.example.adminsidedemoproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.adminsidedemoproject.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class AddStockActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
NetworkChangeListener networkChangeListener=new NetworkChangeListener();
Button addPartyBtn,addPartyButton,addStockToCurrentStock;
private Dialog partyDialogue;
EditText partyNameEt,partyMobEt;
private DatabaseReference partyRef,availableRef;
Spinner addPartySpinner;
ArrayList<String> partyList;
ArrayAdapter<String>  partyAdapter;
TextView stock_add_currently_available,addStockTotalAmount,addStockDueAmount;
ImageButton selectAddStockDateImgBtn,addStockTimeImgBtn,close_dialogue_top_addparty_button;
EditText purchaseNumberEt,stockQuantityToAdd,stockAddPurchasePrice,addStockRemark,receivedAmount;
String partySelectedName;
LinearLayout totalAmountLinearLayout;
TextView  addStockDateTx,addStockTimeTx,partyName_Tx,purchase_p_number;
int Chour,Cminute,day,month,year,totalAmount=0,dueAmount=0;
String prodId,ProdNm,ProdUnit;
int dbUpdateStockAvailable,dbUpdateStockIn;
TextView quantity_to_add_unittype,add_stock_purchase_unit_type;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock);
        initialize();
        setPartyDialog();
        Intent i=getIntent();
        toolbar=findViewById(R.id.add_stock);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);
        prodId=i.getStringExtra("product_id");
        ProdNm=i.getStringExtra("product_name");
        ProdUnit=i.getStringExtra("product_unit");
        partyRef= FirebaseDatabase.getInstance().getReference().child("ProductCategoriesAndUnits").child("PartyDetails");
        availableRef=FirebaseDatabase.getInstance().getReference().child("AllProducts").child(prodId);
       if(partyRef!=null) {
           loadParty();
        }else
        {
           // Toast.makeText(this, "not exist", Toast.LENGTH_SHORT).show();
        }

        if(availableRef!=null) {
            loadAvailableStock();
        }else
        {
           // Toast.makeText(this, "not exist", Toast.LENGTH_SHORT).show();
        }

        //addPartySpinner.getSelectedItem().toString()

        partyList=new ArrayList<>();
        partyAdapter=new ArrayAdapter<>(this, R.layout.style_spinner,partyList);
        addPartySpinner.setAdapter(partyAdapter);

    }
    public void loadAvailableStock(){
        availableRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String availablestock=snapshot.child("AvailableStock").getValue().toString();
                String unitType=snapshot.child("UnitType").getValue().toString();
                stock_add_currently_available.setText(availablestock+" "+unitType);
                quantity_to_add_unittype.setText(unitType);
                add_stock_purchase_unit_type.setText(unitType);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void initialize(){
        Calendar calendar= Calendar.getInstance();
        addPartyBtn=findViewById(R.id.add_party_btn);
        partyRef= FirebaseDatabase.getInstance().getReference();
        addPartySpinner=findViewById(R.id.add_stock_party);
        addPartySpinner.setOnItemSelectedListener(this);
        stock_add_currently_available=findViewById(R.id.stock_add_currently_available);

        addStockDateTx=findViewById(R.id.add_stock_date_tx);
        addStockTimeTx=findViewById(R.id.add_stock_time_tx);

        selectAddStockDateImgBtn=findViewById(R.id.select_add_stock_date_imgBtn);
        addStockTimeImgBtn=findViewById(R.id.add_stock_time_imgBtn);
///////spinner

        purchaseNumberEt=findViewById(R.id.purchase_number_et);

        stockQuantityToAdd=findViewById(R.id.stock_quantity_to_add);
        stockAddPurchasePrice=findViewById(R.id.stock_add_purchase_price);

        addStockRemark=findViewById(R.id.add_stock_remark);
        totalAmountLinearLayout=findViewById(R.id.total_amount_linear_layout);


        addStockTotalAmount=findViewById(R.id.add_stock_total_amount);
        addStockDueAmount=findViewById(R.id.add_stock_due_amount);

        addStockToCurrentStock=findViewById(R.id.add_stock_to_current_stock);
        receivedAmount=findViewById(R.id.received_amount);
        partyName_Tx=findViewById(R.id.partyName_Tx);
        purchase_p_number=findViewById(R.id.purchase_p_number);
        addPartyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partyDialogue.show();
            }
        });

        quantity_to_add_unittype=findViewById(R.id.quantity_to_add_unittype);
        add_stock_purchase_unit_type=findViewById(R.id.add_stock_purchase_unit_type);

////start views
        purchaseNumberEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                purchase_p_number.setText( "P"+purchaseNumberEt.getText().toString());
            }
        });

        stockAddPurchasePrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    totalAmount=Integer.parseInt(stockQuantityToAdd.getText().toString())
                            *  Integer.parseInt(stockAddPurchasePrice.getText().toString());
                   // Toast.makeText(AddStockActivity.this, ""+totalAmount, Toast.LENGTH_SHORT).show();
                    addStockTotalAmount.setText(String.valueOf(totalAmount));
                    receivedAmount.setText(String.valueOf(totalAmount));
                }
                catch (Exception e)
                {

                }
            }
        });

        stockQuantityToAdd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    totalAmount=Integer.parseInt(stockQuantityToAdd.getText().toString())
                            *  Integer.parseInt(stockAddPurchasePrice.getText().toString());
                   // Toast.makeText(AddStockActivity.this, ""+totalAmount, Toast.LENGTH_SHORT).show();
                    addStockTotalAmount.setText(String.valueOf(totalAmount));
                    receivedAmount.setText(String.valueOf(totalAmount));
                }
                catch (Exception e)
                {

                }
            }
        });

        receivedAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                    String getTotal=addStockTotalAmount.getText().toString();
                    if(!receivedAmount.getText().toString().isEmpty())
                    {
                        dueAmount=Integer.parseInt(addStockTotalAmount.getText().toString())-Integer.parseInt(receivedAmount.getText().toString());
                        addStockDueAmount.setText(String.valueOf(dueAmount));
                        // Toast.makeText(AddStockActivity.this, "getTotal"+dueAmount, Toast.LENGTH_SHORT).show();
                        Log.d("checkdueamout","true");
                    }
                    else
                    {
                        Log.d("checkdueamout","false");
                    }

            }
        });

        selectAddStockDateImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year=calendar.get(Calendar.YEAR);
                month=calendar.get(Calendar.MONTH);
                day=calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog=new DatePickerDialog(AddStockActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        addStockDateTx.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                datePickerDialog.setCancelable(true);
                datePickerDialog.show();
            }
        });

        addStockTimeImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog=new TimePickerDialog(AddStockActivity.this
                        , new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Chour=hourOfDay;
                        Cminute=minute;

                        Calendar calendar1=Calendar.getInstance();
                        calendar1.set(0,0,0,Chour,Cminute);
                        addStockTimeTx.setText(DateFormat.format("hh mm aa",calendar1));
                    }
                },12,0,false
                );
                timePickerDialog.updateTime(Chour,Cminute);
                timePickerDialog.show();
            }
        });
        addStockToCurrentStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(AddStockActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                //addStockTotalAmount.getText().toString().isEmpty() &&
                //partyName_Tx.getText().toString().isEmpty()&&
                //addStockDueAmount.getText().toString().isEmpty()
                //receivedAmount.getText().toString().isEmpty()

                if(addStockDateTx.getText().toString().isEmpty())
                {
                    addStockDateTx.setError("Required");
                    return;
                }
                if(addStockTimeTx.getText().toString().isEmpty())
                {
                    addStockTimeTx.setError("Required");
                    return;
                }
                if(purchaseNumberEt.getText().toString().isEmpty())
                {
                    purchaseNumberEt.setError("Required");
                    return;
                }
                if(stockQuantityToAdd.getText().toString().isEmpty() )
                {
                    stockQuantityToAdd.setError("Required");
                    return;
                }
                if(stockAddPurchasePrice.getText().toString().isEmpty())
                {
                    stockAddPurchasePrice.setError("Required");
                    return;
                }
                if(partyName_Tx.getText().toString().isEmpty())
                {
                    partyName_Tx.setError("Required");
                    return;
                }
                if(receivedAmount.getText().toString().isEmpty())
                {
                    receivedAmount.setError("Required");
                }
                else
                {
                    updateData();
                    Log.d("chhh","true");
                }


            }
        });

    }
    public void updateData(){

        String addStockDate=addStockDateTx.getText().toString().trim();
        String addStockTime=addStockTimeTx.getText().toString().trim();
        String addStockPurchaseNum=purchaseNumberEt.getText().toString().trim();
        String addStockPartyNm= partyName_Tx.getText().toString().trim();
        String addStockQuantity=  stockQuantityToAdd.getText().toString().trim();
        String addStockPurchasePr=stockAddPurchasePrice.getText().toString().trim();
        String addStockTotAmt= addStockTotalAmount.getText().toString().trim();
        String addStockReceivedAmt= receivedAmount.getText().toString().trim();
        String addStockDueAmt=addStockDueAmount.getText().toString().trim();
        String addStockToRemark=addStockRemark.getText().toString().trim();
        SimpleDateFormat twelehourformat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
        String currenttwelehrformat= twelehourformat.format(new Date());

        if(TextUtils.isEmpty(addStockDate)
                && TextUtils.isEmpty(addStockTime)
                &&TextUtils.isEmpty(addStockPurchaseNum)
                &&TextUtils.isEmpty(addStockPartyNm)
                && TextUtils.isEmpty(addStockQuantity)
                &&TextUtils.isEmpty(addStockPurchasePr)
                &&TextUtils.isEmpty(addStockTotAmt)
                &&TextUtils.isEmpty(addStockReceivedAmt)
                &&TextUtils.isEmpty(addStockDueAmt))
        {

            Toast.makeText( AddStockActivity.this,"Please Enter Required Credentials",Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            if(!addStockDate.isEmpty() && !addStockTime.isEmpty()
                  && !addStockPartyNm.isEmpty()
                    && !addStockQuantity.isEmpty() && !addStockPurchasePr.isEmpty()
                    && !addStockTotAmt.isEmpty() && !addStockReceivedAmt.isEmpty() && !addStockDueAmt.isEmpty() )
            {
                String id= UUID.randomUUID().toString();
                HashMap<String,Object> map =new HashMap<>();
                map.put("ProdId",prodId);
                map.put("StockDate",""+addStockDate);
                map.put("StockTime",""+addStockTime);
                map.put("StockPurchaseSalesNumber",""+"P"+addStockPurchaseNum);
                map.put("StockPartyName",""+addStockPartyNm);
                map.put("StockQuantity",""+addStockQuantity);
                map.put("StockPurchaseSalesPrice",""+addStockPurchasePr);
                map.put("StockTotalAmount",""+addStockTotAmt);
                map.put("StockAmountReceivedPaid",""+addStockReceivedAmt);
                map.put("StockDueAmount",""+addStockDueAmt);
                map.put("StockRemark",""+addStockToRemark);
                map.put("StockId",id);
                map.put("StockType","IN");
                map.put("StockItemName",ProdNm);
                map.put("StockUnitType",ProdUnit);

                FirebaseDatabase data=FirebaseDatabase.getInstance();
                data.getReference().child("StockInOut").child(prodId).child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseDatabase allProdList=FirebaseDatabase.getInstance();
                            allProdList.getReference().child("AllProducts").child(prodId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String AvailableStk=snapshot.child("AvailableStock").getValue().toString();
                                    String StockIn=snapshot.child("StockIn").getValue().toString();
                                    String category=snapshot.child("Category").getValue().toString();
                                    int availableStock=Integer.parseInt(AvailableStk);
                                    int stockIn=Integer.parseInt(StockIn);
                                    dbUpdateStockAvailable=availableStock+Integer.parseInt(addStockQuantity);
                                    dbUpdateStockIn=stockIn+Integer.parseInt(addStockQuantity);

                                        ///allprodlistMap
                                        HashMap<String,Object> allprodlistMap =new HashMap<>();
                                        allprodlistMap.put("AvailableStock",String.valueOf(dbUpdateStockAvailable));
                                        allprodlistMap.put("StockIn",String.valueOf(dbUpdateStockIn));
                                        allprodlistMap.put("UpdateTime",currenttwelehrformat);
                                        ///categorywiselistMap
                                       /* HashMap<String,Object> categorywiselistMap =new HashMap<>();
                                        categorywiselistMap.put("AvailableStock",String.valueOf(dbUpdateStockAvailable));
                                        categorywiselistMap.put("StockIn",String.valueOf(dbUpdateStockIn));
                                        categorywiselistMap.put("UpdateTime",currenttwelehrformat);
*/

                                       // Toast.makeText(AddStockActivity.this,"Product Uploaded Sucessfull..",Toast.LENGTH_SHORT).show();
                                        FirebaseDatabase allProdListInside=FirebaseDatabase.getInstance();
                                        allProdListInside.getReference().child("AllProducts").child(prodId).updateChildren(allprodlistMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                //Toast.makeText(AddStockActivity.this,"Stock updated successfuly",Toast.LENGTH_SHORT).show();
                                                FirebaseDatabase CategoryWiseList=FirebaseDatabase.getInstance();
                                                CategoryWiseList.getReference().child("categoryWiseProducts").child(category).child(prodId).updateChildren(allprodlistMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        FirebaseDatabase partyAddList=FirebaseDatabase.getInstance();
                                                        partyAddList.getReference().child("ProductCategoriesAndUnits").child("PartyDetails")
                                                                .child(partyName_Tx.getText().toString()).child("StockInOut").child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Toast.makeText(AddStockActivity.this, "updatedSuccessfully", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });


                                                    }
                                                });
                                            }
                                        });



                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                        else
                        {
                            Toast.makeText(AddStockActivity.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
                        }
                        //loading.dismiss();
                    }
                });
            }

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
                            Toast.makeText(AddStockActivity.this, "Sucessfully Party Added", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }
    public void loadParty(){

        if(partyRef!=null)
        {
            partyRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
                {
                    if(snapshot.exists())
                    {
                       // Toast.makeText(AddStockActivity.this," exist",Toast.LENGTH_SHORT).show();

                    for(DataSnapshot ds:snapshot.getChildren())
                    {

                        String name=ds.child("partyName").getValue().toString();
                        partyList.add((name));
                        partyAdapter.notifyDataSetChanged();
                    }
                    }
                    else
                    {
                        Toast.makeText(AddStockActivity.this,"not exist",Toast.LENGTH_SHORT).show();
                    }

                }
                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
     /*   String spinnerString=parent.getItemAtPosition(position).toString();
        partySelectedName=spinnerString.toString();
        Toast.makeText(this, ""+spinnerString, Toast.LENGTH_SHORT).show();
       ;*/

        String text = parent.getItemAtPosition(position).toString();
        //Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
        partyName_Tx.setText(text);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i=new Intent(this,MainActivity.class);
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




/*if(addStockDateTx.getText().toString().isEmpty()&&
                addStockTimeTx.getText().toString().isEmpty() &&
                purchaseNumberEt.getText().toString().isEmpty() &&
                 stockQuantityToAdd.getText().toString().isEmpty() &&
                stockAddPurchasePrice.getText().toString().isEmpty())
                {
                    Toast.makeText(AddStockActivity.this,"Please Enter required credentials"+partyName_Tx.getText().toString().isEmpty(),Toast.LENGTH_SHORT).show();
                    Log.d("chhh",String.valueOf(addStockDateTx.getText().toString().isEmpty())+
                            addStockTimeTx.getText().toString().isEmpty() +
                            purchaseNumberEt.getText().toString().isEmpty() +
                            stockQuantityToAdd.getText().toString().isEmpty() +
                            stockAddPurchasePrice.getText().toString().isEmpty());

                }
             else
             {

                 updateData();
             }*/