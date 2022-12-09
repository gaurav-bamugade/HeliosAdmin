package com.example.adminsidedemoproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.adminsidedemoproject.Utility.NetworkChangeListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
    private String currentuserid;
    private FirebaseAuth fireauth;
    TextView tx, tx2;
    private FirebaseAuth useraut;
    BottomNavigationView navigationView;
    Button testing;
    private DatabaseReference fireref;
    private Toolbar toolbar;
    MenuItem menuItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationView = findViewById(R.id.Main_bottom_nav);
        toolbar=findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        fireauth= FirebaseAuth.getInstance();

        useraut=FirebaseAuth.getInstance();
        fireref= FirebaseDatabase.getInstance().getReference();
       /* testing=findViewById(R.id.testing);
        testing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,AllPartyActivity.class);
                startActivity(i);
            }
        });*/
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProductFragment()).commit();
        toolbar.setTitleTextColor(getColor(R.color.white));
        navigationView.setSelectedItemId(R.id.stock);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment frag = null;
                switch (item.getItemId()) {
                    /*case R.id.Home:
                        frag=new AdminHomeFragment();
                        break;*/
                    case R.id.stock:
                        frag = new ProductFragment();
                        break;

                  case R.id.dashboard:
                        frag=new DashboardFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container, frag).commit();
                return true;
            }
        });

    }
    private void sendUserToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, AdminLoginActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
   @Override
    protected void onStart() {
       IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
       registerReceiver(networkChangeListener,filter);
        super.onStart();
        FirebaseUser currentusers = useraut.getCurrentUser();
        if (currentusers == null) {
            sendUserToLoginActivity();
        }
        else
        {
            updateuserstatus("online");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_notification,menu);
        menuItem=menu.findItem(R.id.chat_notf);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.chat_notf:
                Intent intent=new Intent(MainActivity.this,ChatTabsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser  currentusers=useraut.getCurrentUser();
        if(currentusers!=null){
            updateuserstatus("offline");
        }
    }
    private void updateuserstatus(String  st)
    {

        String saveCurrentTime,saveCurrentDate;
        Calendar calen=Calendar.getInstance();
        SimpleDateFormat currentDate= new SimpleDateFormat("MMM, dd, yyyy");
        saveCurrentDate=currentDate.format(calen.getTime());

        SimpleDateFormat CurrentTime= new SimpleDateFormat("hh: mm: a");
        saveCurrentTime=CurrentTime.format(calen.getTime());
        HashMap<String, Object> onlineState = new HashMap<>();
        onlineState.put("time",saveCurrentTime);
        onlineState.put("Date",saveCurrentDate);
        onlineState.put("state",st);

        currentuserid=fireauth.getCurrentUser().getUid();

        fireref.child("ExistingUsers").child(currentuserid).child("userstate")
                .updateChildren(onlineState);
    }
}