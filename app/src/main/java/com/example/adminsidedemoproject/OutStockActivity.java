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

public class OutStockActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();

    Button addPartyBtn,outPartyBtn,OutStockToCurrentStock;
    private Dialog partyDialogue;
    EditText partyNameEt,partyMobEt;
    private DatabaseReference partyRef,availableRef,checkIfStockLessThenQuantity;
    Spinner outPartySpinner;
    ArrayList<String> partyList;
    ArrayAdapter<String> partyAdapter;
    TextView outstockCurrentlyAvailable,OutStockTotalAmount,OutStockDueAmount;
    ImageButton selectOutStockDateImgBtn,OutStockTimeImgBtn,close_dialogue_top_addparty_button;
    EditText OutSalesNumberEt,stockQuantityToOut,stockOutSellPrice,OutStockRemark,OutreceivedAmount;
    String partySelectedName;
    LinearLayout totalAmountOutLinearLayout;
    TextView  OutStockDateTx,OutStockTimeTx,OutpartyName_Tx,sell_s_number;
    int Chour,Cminute,day,month,year,totalAmount=0,dueAmount=0;
    String prodId,prodNm,ProdUnit;
    int dbUpdateStockAvailable,dbUpdateStockIn;
    TextView quantity_out_unit_type,stock_out_selling_price_unit_type;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_stock);
        initialize();
        setPartyDialog();
        Intent i=getIntent();
        toolbar=findViewById(R.id.out_stock);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);

        prodId=i.getStringExtra("product_id");
        prodNm=i.getStringExtra("product_name");
        ProdUnit=i.getStringExtra("product_unit");
        partyRef= FirebaseDatabase.getInstance().getReference().child("ProductCategoriesAndUnits").child("PartyDetails");
        availableRef=FirebaseDatabase.getInstance().getReference().child("AllProducts").child(prodId);
        if(partyRef!=null) {
            loadParty();
        }else
        {
          //  Toast.makeText(this, "not exist", Toast.LENGTH_SHORT).show();
        }

        if(availableRef!=null) {
            loadAvailableStock();
        }else
        {
          //  Toast.makeText(this, "not exist", Toast.LENGTH_SHORT).show();
        }
        //addPartySpinner.getSelectedItem().toString()

        partyList=new ArrayList<>();
        partyAdapter=new ArrayAdapter<>(this, R.layout.style_spinner,partyList);
        outPartySpinner.setAdapter(partyAdapter);
    }

    public void initialize(){
        Calendar calendar= Calendar.getInstance();
        outPartyBtn=findViewById(R.id.out_party_btn);
        partyRef= FirebaseDatabase.getInstance().getReference();
        outPartySpinner=findViewById(R.id.out_stock_party);
        outPartySpinner.setOnItemSelectedListener(this);
        outstockCurrentlyAvailable=findViewById(R.id.out_stock_currently_available);

        OutStockDateTx=findViewById(R.id.out_stock_date_tx);
        OutStockTimeTx=findViewById(R.id.out_stock_time_tx);

        selectOutStockDateImgBtn=findViewById(R.id.select_out_stock_date_imgBtn);
        OutStockTimeImgBtn=findViewById(R.id.out_stock_time_imgBtn);
///////spinner

        OutSalesNumberEt=findViewById(R.id.out_sales_number_et);

        stockQuantityToOut=findViewById(R.id.stock_quantity_to_out);
        stockOutSellPrice=findViewById(R.id.stock_out_sell_price);

        OutStockRemark=findViewById(R.id.out_stock_remark);
        totalAmountOutLinearLayout=findViewById(R.id.total_amount_out_linear_layout);


        OutStockTotalAmount=findViewById(R.id.out_stock_total_amount);
        OutStockDueAmount=findViewById(R.id.out_stock_due_amount);

        OutStockToCurrentStock=findViewById(R.id.out_stock_to_current_stock);
        OutreceivedAmount=findViewById(R.id.out_received_amount);
        OutpartyName_Tx=findViewById(R.id.out_partyName_Tx);
        sell_s_number=findViewById(R.id.sell_s_number);
        outPartyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partyDialogue.show();
            }
        });

        stock_out_selling_price_unit_type=findViewById(R.id.stock_out_selling_price_unit_type);
        quantity_out_unit_type=findViewById(R.id.quantity_out_unit_type);
////start views
        OutSalesNumberEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sell_s_number.setText( "S"+OutSalesNumberEt.getText().toString());
            }
        });

        stockOutSellPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    totalAmount=Integer.parseInt(stockQuantityToOut.getText().toString())
                            *  Integer.parseInt(stockOutSellPrice.getText().toString());
                    // Toast.makeText(AddStockActivity.this, ""+totalAmount, Toast.LENGTH_SHORT).show();
                    OutStockTotalAmount.setText(String.valueOf(totalAmount));
                    OutreceivedAmount.setText(String.valueOf(totalAmount));
                }
                catch (Exception e)
                {

                }
            }
        });

        stockQuantityToOut.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    totalAmount=Integer.parseInt(stockQuantityToOut.getText().toString())
                            *  Integer.parseInt(stockOutSellPrice.getText().toString());
                    // Toast.makeText(AddStockActivity.this, ""+totalAmount, Toast.LENGTH_SHORT).show();
                    OutStockTotalAmount.setText(String.valueOf(totalAmount));
                    OutreceivedAmount.setText(String.valueOf(totalAmount));
                }
                catch (Exception e)
                {

                }
            }
        });

        OutreceivedAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String getTotal=OutStockTotalAmount.getText().toString();
                if(!OutreceivedAmount.getText().toString().isEmpty())
                {
                    dueAmount=Integer.parseInt(OutStockTotalAmount.getText().toString())-Integer.parseInt(OutreceivedAmount.getText().toString());
                    OutStockDueAmount.setText(String.valueOf(dueAmount));
                    // Toast.makeText(AddStockActivity.this, "getTotal"+dueAmount, Toast.LENGTH_SHORT).show();
                    Log.d("checkdueamout","true");
                }
                else
                {
                    Log.d("checkdueamout","false");
                }

            }
        });

        selectOutStockDateImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year=calendar.get(Calendar.YEAR);
                month=calendar.get(Calendar.MONTH);
                day=calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog=new DatePickerDialog(OutStockActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        OutStockDateTx.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                datePickerDialog.setCancelable(true);
                datePickerDialog.show();
            }
        });

        OutStockTimeImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog=new TimePickerDialog(OutStockActivity.this
                        , new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Chour=hourOfDay;
                        Cminute=minute;

                        Calendar calendar1=Calendar.getInstance();
                        calendar1.set(0,0,0,Chour,Cminute);
                        OutStockTimeTx.setText(DateFormat.format("hh mm aa",calendar1));
                    }
                },12,0,false
                );
                timePickerDialog.updateTime(Chour,Cminute);
                timePickerDialog.show();
            }
        });
        OutStockToCurrentStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(OutStockActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                //addStockTotalAmount.getText().toString().isEmpty() &&
                //partyName_Tx.getText().toString().isEmpty()&&
                //addStockDueAmount.getText().toString().isEmpty()
                //receivedAmount.getText().toString().isEmpty()

                if(OutStockDateTx.getText().toString().isEmpty())
                {
                    OutStockDateTx.setError("Required");
                    return;
                }
                if(OutStockTimeTx.getText().toString().isEmpty())
                {
                    OutStockTimeTx.setError("Required");
                    return;
                }
                if(OutSalesNumberEt.getText().toString().isEmpty())
                {
                    OutSalesNumberEt.setError("Required");
                    return;
                }
                if(stockQuantityToOut.getText().toString().isEmpty() )
                {
                    stockQuantityToOut.setError("Required");
                    return;
                }
                if(stockOutSellPrice.getText().toString().isEmpty())
                {
                    stockOutSellPrice.setError("Required");
                    return;
                }
                if(OutpartyName_Tx.getText().toString().isEmpty())
                {
                    OutpartyName_Tx.setError("Required");
                    return;
                }
                if(OutreceivedAmount.getText().toString().isEmpty())
                {
                    OutreceivedAmount.setError("Required");
                }
                else
                {
                    checkIfStockLessThenQuantity=FirebaseDatabase.getInstance().getReference().child("AllProducts").child(prodId);
                    checkIfStockLessThenQuantity.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String availablestock=snapshot.child("AvailableStock").getValue().toString();
                            int  quant=Integer.parseInt(stockQuantityToOut.getText().toString());
                            int  available=Integer.parseInt(availablestock);
                            if(quant>available)
                            {
                                Toast.makeText(OutStockActivity.this, "Insufficient Stock", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                updateData();
                                Log.d("chhh","true");
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }


            }
        });

    }
    public void updateData(){

        String outStockDate=OutStockDateTx.getText().toString().trim();
        String outStockTime=OutStockTimeTx.getText().toString().trim();
        String outStockPurchaseNum=OutSalesNumberEt.getText().toString().trim();
        String outStockPartyNm= OutpartyName_Tx.getText().toString().trim();
        String outStockQuantity=  stockQuantityToOut.getText().toString().trim();
        String outStockPurchasePr=stockOutSellPrice.getText().toString().trim();
        String outStockTotAmt= OutStockTotalAmount.getText().toString().trim();
        String outStockReceivedAmt= OutreceivedAmount.getText().toString().trim();
        String outStockDueAmt=OutStockDueAmount.getText().toString().trim();
        String outStockToRemark=OutStockRemark.getText().toString().trim();
        SimpleDateFormat twelehourformat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
        String currenttwelehrformat= twelehourformat.format(new Date());

        if(TextUtils.isEmpty(outStockDate)
                && TextUtils.isEmpty(outStockTime)
                &&TextUtils.isEmpty(outStockPurchaseNum)
                &&TextUtils.isEmpty(outStockPartyNm)
                && TextUtils.isEmpty(outStockQuantity)
                &&TextUtils.isEmpty(outStockPurchasePr)
                &&TextUtils.isEmpty(outStockTotAmt)
                &&TextUtils.isEmpty(outStockReceivedAmt)
                &&TextUtils.isEmpty(outStockDueAmt))
        {

            Toast.makeText( OutStockActivity.this,"Please Enter Required Credentials",Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            if(!outStockDate.isEmpty() && !outStockTime.isEmpty()
                    && !outStockPartyNm.isEmpty()
                    && !outStockQuantity.isEmpty() && !outStockPurchasePr.isEmpty()
                    && !outStockTotAmt.isEmpty() && !outStockReceivedAmt.isEmpty() && !outStockDueAmt.isEmpty() )
            {
                String id= UUID.randomUUID().toString();
                HashMap<String,Object> map =new HashMap<>();
                map.put("ProdId",prodId);
                map.put("StockDate",""+outStockDate);
                map.put("StockTime",""+outStockTime);
                map.put("StockPurchaseSalesNumber",""+"S"+outStockPurchaseNum);
                map.put("StockPartyName",""+outStockPartyNm);
                map.put("StockQuantity",""+outStockQuantity);
                map.put("StockPurchaseSalesPrice",""+outStockPurchasePr);
                map.put("StockTotalAmount",""+outStockTotAmt);
                map.put("StockAmountReceivedPaid",""+outStockReceivedAmt);
                map.put("StockDueAmount",""+outStockDueAmt);
                map.put("StockRemark",""+outStockToRemark);
                map.put("StockId",id);
                map.put("StockType","OUT");
                map.put("StockItemName",prodNm);
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
                                    String StockOt=snapshot.child("StockOut").getValue().toString();
                                    String category=snapshot.child("Category").getValue().toString();
                                    int availableStock=Integer.parseInt(AvailableStk);
                                    int stockOut=Integer.parseInt(StockOt);
                                    dbUpdateStockAvailable=availableStock-Integer.parseInt(outStockQuantity);
                                    dbUpdateStockIn=stockOut+Integer.parseInt(outStockQuantity);

                                    ///allprodlistMap
                                    HashMap<String,Object> allprodlistMap =new HashMap<>();
                                    allprodlistMap.put("AvailableStock",String.valueOf(dbUpdateStockAvailable));
                                    allprodlistMap.put("StockOut",String.valueOf(dbUpdateStockIn));
                                    allprodlistMap.put("UpdateTime",currenttwelehrformat);
                                    ///categorywiselistMap
                                       /* HashMap<String,Object> categorywiselistMap =new HashMap<>();
                                        categorywiselistMap.put("AvailableStock",String.valueOf(dbUpdateStockAvailable));
                                        categorywiselistMap.put("StockIn",String.valueOf(dbUpdateStockIn));
                                        categorywiselistMap.put("UpdateTime",currenttwelehrformat);
*/

                                 //   Toast.makeText(OutStockActivity.this,"Product Uploaded Sucessfull..",Toast.LENGTH_SHORT).show();
                                    FirebaseDatabase allProdListInside=FirebaseDatabase.getInstance();
                                    allProdListInside.getReference().child("AllProducts").child(prodId).updateChildren(allprodlistMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                           // Toast.makeText(OutStockActivity.this,"Stock updated successfuly",Toast.LENGTH_SHORT).show();
                                            FirebaseDatabase CategoryWiseList=FirebaseDatabase.getInstance();
                                            CategoryWiseList.getReference().child("categoryWiseProducts").child(category).child(prodId).updateChildren(allprodlistMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    FirebaseDatabase partyAddList=FirebaseDatabase.getInstance();
                                                    partyAddList.getReference().child("ProductCategoriesAndUnits").child("PartyDetails")
                                                            .child(OutpartyName_Tx.getText().toString()).child("StockInOut").child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    Toast.makeText(OutStockActivity.this, "updatedSuccessfully", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(OutStockActivity.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
                        }
                        //loading.dismiss();
                    }
                });
            }

        }
    }
    public void loadAvailableStock(){
        availableRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String availablestock=snapshot.child("AvailableStock").getValue().toString();
                String unitType=snapshot.child("UnitType").getValue().toString();
                outstockCurrentlyAvailable.setText(availablestock+" "+unitType);
                stock_out_selling_price_unit_type.setText(unitType);;
                quantity_out_unit_type.setText(unitType);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        addPartyBtn=partyDialogue.findViewById(R.id.add_party_button);
        close_dialogue_top_addparty_button=partyDialogue.findViewById(R.id.close_dialogue_top_addparty_button);
        close_dialogue_top_addparty_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partyDialogue.dismiss();
            }
        });
        HashMap<String,Object> map =new HashMap<>();

        addPartyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(partyNameEt.getText().toString().isEmpty())
                {
                    Log.d("name1",partyNameEt.getText().toString()+partyMobEt.getText().toString());
                    partyMobEt.setError("Required");
                    return;

                }
                if(partyMobEt.getText().toString().isEmpty())
                {
                    partyNameEt.setError("Required");
                    Log.d("name2",partyNameEt.getText().toString()+partyMobEt.getText().toString());
                    return;
                }
                else
                {
                    Log.d("name3",partyNameEt.getText().toString()+partyMobEt.getText().toString());
                    DatabaseReference data=FirebaseDatabase.getInstance().getReference();
                    map.put("partyName",partyNameEt.getText().toString());
                    map.put("partyMobile",partyMobEt.getText().toString());
                    data.child("ProductCategoriesAndUnits").child("PartyDetails").child(partyNameEt.getText().toString()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(OutStockActivity.this, "Sucessfully Party Added", Toast.LENGTH_SHORT).show();
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
                       // Toast.makeText(OutStockActivity.this,"not exist",Toast.LENGTH_SHORT).show();
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
        OutpartyName_Tx.setText(text);

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