package com.example.adminsidedemoproject;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.adminsidedemoproject.Utility.NetworkChangeListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileDetailsActivity extends AppCompatActivity {
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();

    CircleImageView user_profile_details_image,user_profile_details_edit_setting;
TextView user_profile_details_name,user_profile_details_role,user_profile_details_employee_id,
        user_profile_details_email,user_profile_details_phone,user_profile_details_radioGrp,user_profile_details_date_of_birth;
CardView user_profile_details_change_password;
private DatabaseReference load_profile_database_ref;
String   currentUserID;
    ImageButton back_btn_profile_details;
    LinearLayout change_pass;
    String gender="";
    RadioButton radioM,radioF;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_details);

        user_profile_details_edit_setting=findViewById(R.id.user_profile_details_edit_setting);
        user_profile_details_change_password=findViewById(R.id.user_profile_details_change_password);
        back_btn_profile_details=findViewById(R.id.back_btn_profile_details);
        user_profile_details_image=findViewById(R.id.user_profile_image);
        user_profile_details_name=findViewById(R.id.user_profile_details_name);
        user_profile_details_role=findViewById(R.id.user_profile_details_role);
        user_profile_details_employee_id=findViewById(R.id.user_profile_details_employee_id);
        user_profile_details_email=findViewById(R.id.user_profile_details_email);
        user_profile_details_phone=findViewById(R.id.user_profile_details_phone);
        user_profile_details_date_of_birth=findViewById(R.id.user_profile_details_date_of_birth);
        user_profile_details_change_password=findViewById(R.id.user_profile_details_change_password);
        radioGroup=findViewById(R.id.user_profile_details_radioGrp);
        radioM=findViewById(R.id.user_profile_details_radioM);
        radioF=findViewById(R.id.user_profile_details_radioF);

        change_pass=findViewById(R.id.change_pass);

        change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String empid=user_profile_details_employee_id.getText().toString().trim();
                sendUserToUpdateActivity(empid);

            }
        });
        back_btn_profile_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(UserProfileDetailsActivity.this,UserProfileActivity.class);
                startActivity(i);
            }
        });

        user_profile_details_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(UserProfileDetailsActivity.this,EditProfileActivity.class);
                startActivity(i);
            }
        });
       user_profile_details_edit_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(UserProfileDetailsActivity.this,EditProfileActivity.class);

                startActivity(i);
            }
        });
    /*    user_profile_details_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(users_profile.this,user_profile_details.class);
                startActivity(i);
            }
        });*/

        loadData();
    }
    private void loadData(){
            currentUserID= FirebaseAuth.getInstance().getCurrentUser().getUid();
            load_profile_database_ref= FirebaseDatabase.getInstance().getReference();
            load_profile_database_ref.child("Admin").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String UserRegEmail=  snapshot.child("AdminEmail").getValue().toString();
                    String userRole=snapshot.child("AdminRole").getValue().toString();
                    String employeeId=snapshot.child("AdminId").getValue().toString();
                    String userName=snapshot.child("AdminName").getValue().toString();
                    String userPass=snapshot.child("AdminPass").getValue().toString();
                    String userProfileImage=snapshot.child("AdminImage").getValue().toString();
                    String userPhone=snapshot.child("AdminPhone").getValue().toString();
                    String userDateOfBirth=snapshot.child("AdminDob").getValue().toString();

                    user_profile_details_name.setText( userName);
                    user_profile_details_email.setText(UserRegEmail);
                    user_profile_details_role.setText(userRole);
                    user_profile_details_employee_id.setText(employeeId);
                    String userGender=snapshot.child("AdminGender").getValue().toString().trim();
                    if(userGender.equals(""))
                    {
                        Log.d("helloradio","true");
                    }
                    else if(userGender.equals("Male"))
                    {
                        radioM.setChecked(true);

                    }
                    else if(userGender.equals("Female"))
                    {
                        radioF.setChecked(true);
                    }
                    if(userPhone.equals("") )
                    {
                        //Toast.makeText(UserProfileDetailsActivity.this, "Please set your profile picture", Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        user_profile_details_phone.setText(userPhone);
                    }
                    if(userDateOfBirth.equals(""))
                    {
                      //  Toast.makeText(UserProfileDetailsActivity.this, "Please set your date of birth", Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        user_profile_details_date_of_birth.setText(userDateOfBirth);
                    }

                    if(userProfileImage.equals("") )
                    {
                       // Toast.makeText(UserProfileDetailsActivity.this, "Please set your profile picture", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Picasso.get().load(userProfileImage).placeholder(R.drawable.profile).into(user_profile_details_image);
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

    }
    private void sendUserToUpdateActivity(String empiId) {
        Intent mainintent= new Intent(UserProfileDetailsActivity.this,UpdatePasswordActivity.class);
        mainintent.putExtra("empId",empiId);
        //mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(UserProfileDetailsActivity.this,UserProfileActivity.class);
        startActivity(i);
        finish();
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