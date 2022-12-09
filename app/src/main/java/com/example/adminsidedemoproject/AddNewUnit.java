package com.example.adminsidedemoproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.adminsidedemoproject.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddNewUnit extends AppCompatActivity {
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();

    Button addUnitButton;
    EditText unitName;
    Toolbar toolbar;
    private DatabaseReference unitRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_unit);
        addUnitButton=findViewById(R.id.add_unit_button);
        unitName=findViewById(R.id.unit_name_et);
        toolbar=findViewById(R.id.manage_product_unit);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);
        unitRef=  FirebaseDatabase.getInstance().getReference();
        addUnitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(unitName.getText().toString().isEmpty())
                {
                    unitName.setError("Required");
                }
                else
                {
                    HashMap<String,Object> checkinHashMap=new HashMap<>();
                    checkinHashMap.put("unitName",unitName.getText().toString().trim());
                    unitRef.child("ProductCategoriesAndUnits").child("ProductUnits").child(unitName.getText().toString()).updateChildren(checkinHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(AddNewUnit.this, "Category updated successfuly..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

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