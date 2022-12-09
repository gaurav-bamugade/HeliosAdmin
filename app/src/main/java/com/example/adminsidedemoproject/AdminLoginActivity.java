package com.example.adminsidedemoproject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.adminsidedemoproject.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AdminLoginActivity extends AppCompatActivity {
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();

    Button login;
EditText emailId,passwordID;
private FirebaseUser currentuser;
private DatabaseReference firedb;
    private FirebaseAuth usrauth;
    private Dialog categoryDialog;
    private Button addbtn;
    private EditText categoryDesc;
    private ProgressDialog LoadingBar;
    private String currentUserID;

    private DatabaseReference copy_from_admin_node,check_account_email_valid_from_list
            ,check_if_id_already_exist,save_data_exist;
    String adminId="pPGiTg6yesWzSC10Ou7ORf8AwN52";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        login=findViewById(R.id.loginSub);
        emailId=findViewById(R.id.EmailId);
        passwordID=findViewById(R.id.PasswordId);
        usrauth=FirebaseAuth.getInstance();
        currentuser=FirebaseAuth.getInstance().getCurrentUser();

        LoadingBar=new ProgressDialog(this);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailId.getText()==null || emailId.getText().toString().isEmpty() )
                {
                    emailId.setError("Required");
                    return;
                }
                if(passwordID.getText()==null || passwordID.getText().toString().isEmpty())
                {
                    passwordID.setError("Required");
                }
                else
                {
                    loginNew();
                  //  Toast.makeText(AdminLoginActivity.this,"clicked",Toast.LENGTH_LONG).show();

                }

            }
        });
    }

    private void loginNew(){
        LoadingBar.setTitle("Logging In..");
        LoadingBar.setMessage("Please wait,Creating Account...");
        LoadingBar.setCanceledOnTouchOutside(false);
        LoadingBar.show();
        String EdUserEmail=emailId.getText().toString();
        String EdUserPass=passwordID.getText().toString();
        if(TextUtils.isEmpty(EdUserEmail) && TextUtils.isEmpty(EdUserPass)){
            Toast.makeText(AdminLoginActivity.this,"Please Enter the Required Credentials",Toast.LENGTH_LONG).show();
        }
        if(!EdUserEmail.isEmpty() && !EdUserPass.isEmpty())
        {
            check_account_email_valid_from_list= FirebaseDatabase.getInstance().getReference();
            check_account_email_valid_from_list.child("Admin").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot ds) {
                    String adminEmail=ds.child("AdminEmail").getValue().toString();
                    String adminPass=ds.child("AdminPass").getValue().toString();
                    String adminID=ds.child("AdminUid").getValue().toString();
                    if(adminId.equals(adminID))
                    {
                        if(EdUserEmail.equals(adminEmail) && EdUserPass.equals(adminPass))
                        {
                            signInFirstTimeToaccount(EdUserEmail,EdUserPass);

                        }
                        else
                        {
                            Toast.makeText(AdminLoginActivity.this,"Please Enter Correct Credentials..",Toast.LENGTH_SHORT).show();
                            LoadingBar.dismiss();
                        }
                    }
                    else
                    {
                        Toast.makeText(AdminLoginActivity.this,"Incorrect",Toast.LENGTH_SHORT).show();
                        LoadingBar.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                   // Toast.makeText(AdminLoginActivity.this,""+error,Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

 /* private void loginuser()
    {
        String UserEmail=emailId.getText().toString();
        String UserPass=passwordID.getText().toString();
        if(TextUtils.isEmpty(UserEmail) && TextUtils.isEmpty(UserPass)){
            Toast.makeText(LoginActivity.this,"Please Enter the Required Credentials",Toast.LENGTH_LONG).show();
        }
        if(!UserEmail.isEmpty() && !UserPass.isEmpty())
        {
            check_if_id_already_exist=FirebaseDatabase.getInstance().getReference();
            check_if_id_already_exist.child("existing_users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    String UserRegEmail=ds.child("user_email").getValue().toString().trim();
                    String userPass=ds.child("user_pass").getValue().toString().trim();
                    if(UserRegEmail.equals(UserEmail) && userPass.equals(UserPass))
                    {
                        Toast.makeText(LoginActivity.this,"ID already Exists ....",Toast.LENGTH_SHORT).show();
                        signInExistingUsers(UserEmail,UserPass);
                    }
                   else
                    {
                            check_account_email_valid_from_list= FirebaseDatabase.getInstance().getReference();
                            check_account_email_valid_from_list.child("users_created_id").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot dsa:snapshot.getChildren())
                                    {
                                        String userEmail=dsa.child("user_email").getValue().toString();
                                        String userPass=dsa.child("user_pass").getValue().toString();
                                        if(userEmail.equals(UserEmail) && userPass.equals(UserPass))
                                        {
                                            idcreation(userEmail,userPass);
                                        }
                                        else
                                        {
                                            Toast.makeText(LoginActivity.this,"Please enter the given Email-ID and Password",Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(LoginActivity.this,""+error,Toast.LENGTH_SHORT).show();
                                }
                            });



                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        }
    }*/
 public void idcreation(String emailChecked,String passChecked){
     String UserPass=    emailId.getText().toString().trim();
        String UserRegEmail=passwordID.getText().toString().trim();
     usrauth.createUserWithEmailAndPassword(emailChecked, passChecked).
             addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task) {
                     if (task.isSuccessful()) {
                         Toast.makeText(AdminLoginActivity.this,"ID CREATED",Toast.LENGTH_SHORT).show();
                         signInFirstTimeToaccount(emailChecked,passChecked );
                     }
                     else
                     {
                         Toast.makeText(AdminLoginActivity.this,"Failed to Create ID ....",Toast.LENGTH_SHORT).show();
                     }
                 }
             });
 }
    public void signInExistingUsers(String useremail,String userPass)
    {
        usrauth.signInWithEmailAndPassword(useremail,userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(AdminLoginActivity.this,"Logged In Successfully",Toast.LENGTH_SHORT).show();
                    sendUserToMainActivity();
                    finish();
                }
                else
                {
                    Toast.makeText(AdminLoginActivity.this,"Failed ....",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    public void signInFirstTimeToaccount(String useremail,String userPass){
        usrauth.signInWithEmailAndPassword(useremail,userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(AdminLoginActivity.this,"Please wait for a moment..",Toast.LENGTH_SHORT).show();
                    //save_data_to_database(useremail,userPass);
                    sendUserToMainActivity();
                    finish();
                    LoadingBar.dismiss();
                }
                else
                {
                    Toast.makeText(AdminLoginActivity.this,"Failed ....",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

   /* @Override
    protected void onStart() {
        super.onStart();
        if (currentuser != null) {
            sendUserToMainActivity();
        }
    }*/
    private void sendUserToMainActivity() {
        Intent mainintent= new Intent(AdminLoginActivity.this,MainActivity.class);
       // mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }
    public void save_data_to_database(String useremail,String userPass){
        copy_from_admin_node= FirebaseDatabase.getInstance().getReference();
        copy_from_admin_node.child("users_created_id").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds:snapshot.getChildren())
                    {
                        String UserRegEmail=ds.child("user_email").getValue().toString();
                        String userRole=ds.child("user_role").getValue().toString();
                        String employeeId=ds.child("employee_id").getValue().toString();
                        String userName=ds.child("user_name").getValue().toString();
                        String userPass=ds.child("user_pass").getValue().toString();

                        if(useremail.equals(UserRegEmail) && userPass.equals(userPass))
                        {
                            currentUserID=FirebaseAuth.getInstance().getCurrentUser().getUid();
                            HashMap<String,Object> userDetails=new HashMap<>();
                            userDetails.put("user_name",userName);
                            userDetails.put("user_email",UserRegEmail);
                            userDetails.put("user_pass", userPass);
                            userDetails.put("user_role",userRole);
                            userDetails.put("employee_id",employeeId);
                            userDetails.put("user_uid",currentUserID);
                            userDetails.put("user_image","");
                            userDetails.put("user_phone","");
                            userDetails.put("user_dob","");

                            DatabaseReference userDetailsReference=FirebaseDatabase.getInstance().getReference();
                            userDetailsReference.child("existing_users").child(currentUserID).updateChildren(userDetails);
                            sendUserToMainActivity();
                            finish();
                        }


                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    @Override
    protected void onStart() {
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);
        super.onStart();
        //currentUserID=FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseUser currentusers = FirebaseAuth.getInstance().getCurrentUser();
        if (currentusers != null) {
            sendUserToMainActivity();
        }

    }
    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}


 /* check_if_id_already_exist=FirebaseDatabase.getInstance().getReference().child("existing_users");
        check_if_id_already_exist.child("existing_users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    String UserRegEmail=  ds.child("user_email").getValue().toString();

                    if(UserRegEmail.equals(emailChecked))
                    {
                        Toast.makeText(LoginActivity.this,"ID already Exists ....",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        usrauth.createUserWithEmailAndPassword(emailChecked, passChecked).
                                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(LoginActivity.this,"ID CREATED",Toast.LENGTH_SHORT).show();
                                            signInToaccount(emailChecked,passChecked );
                                        }
                                        else
                                        {
                                            Toast.makeText(LoginActivity.this,"Failed to Create ID ....",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/


/* private void loginFun(){
        String UserEmail=emailId.getText().toString();
        String UserPass=passwordID.getText().toString();
        if(TextUtils.isEmpty(UserEmail) && TextUtils.isEmpty(UserPass)){
            Toast.makeText(LoginActivity.this,"Please Enter the Required Credentials",Toast.LENGTH_LONG).show();
        }
        if(!UserEmail.isEmpty() && !UserPass.isEmpty())
        {
            usrauth.signInWithEmailAndPassword(UserEmail,UserPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {

                       // addbtn=categoryDialog.findViewById(R.id.add);
                     //   categoryDesc=categoryDialog.findViewById(R.id.enterCode);
                        addbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if(categoryDesc.getText()==null || categoryDesc.getText().toString().isEmpty())
                                {

                                    categoryDesc.setError("Required");
                                    return;
                                }
                                else {
                                    DatabaseReference dbref= FirebaseDatabase.getInstance().getReference();
                                    dbref.child("Admin").child("04e93f74-4ff2-46b9-81bc-1f0b1f05c0e6").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String  code=snapshot.child("Code").getValue().toString();
                                           if(categoryDesc.getText().toString().equals(code)){
                                                Toast.makeText(LoginActivity.this,"Logged in Successfully",Toast.LENGTH_LONG).show();
                                                sendUserToMainActivity();
                                                Intent intent= new Intent(LoginActivity.this,MainActivity.class);
                                                startActivity(intent);
                                                categoryDialog.dismiss();
                                                finish();
                                            }
                                            else
                                            {
                                                Toast.makeText(LoginActivity.this,"Reenter code something went wrong!!!",Toast.LENGTH_LONG).show();
                                            }
                                            Toast.makeText(LoginActivity.this,"!!!"+code,Toast.LENGTH_LONG).show();

                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                }
                            }
                        });

                        categoryDialog.show();
                    }
                    else{
                        String errormess=task.getException().toString();
                        Toast.makeText(LoginActivity.this,"Error :"+ errormess,Toast.LENGTH_LONG).show();

                    }
                }
            });

          *//*  DatabaseReference dbref= FirebaseDatabase.getInstance().getReference();
            dbref.child("Admin").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String email=snapshot.child("Email").getValue().toString();
                    String password=snapshot.child("Password").getValue().toString();
                    *//**//*if(UserEmail.equals(email) && UserPass.equals(password))
                    {*//**//*
                  //  }
                   *//**//* else{

                        Toast.makeText(LoginActivity.this,"Please Enter Correct Credentials",Toast.LENGTH_LONG).show();
                    }*//**//*
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });*//*
        }

    }*/


/*

    private void setCategoryDialog()
    {
        categoryDialog =new Dialog(this);
        categoryDialog.setContentView(R.layout.add_category_dialog);
        categoryDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_box));
        categoryDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        categoryDialog.setCancelable(true);

        addbtn=categoryDialog.findViewById(R.id.add);
        categoryDesc=categoryDialog.findViewById(R.id.enterCode);



        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(categoryDesc.getText()==null || categoryDesc.getText().toString().isEmpty())
                {
                    categoryDesc.setError("Required");
                    return;
                }
                categoryDialog.dismiss();
            }
        });

    }
*/
/*private void setCategoryDialog()
{
    categoryDialog =new Dialog(LoginActivity.this);
    categoryDialog.setContentView(R.layout.add_category_dialog);
    categoryDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_box));
    categoryDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
    categoryDialog.setCancelable(true);

}*/
















/*String user_created_Email=dsa.child("user_email").getValue().toString();
                        String user_created_Pass=dsa.child("user_pass").getValue().toString();
                        check_if_id_already_exist=FirebaseDatabase.getInstance().getReference();
                        check_if_id_already_exist.child("existing_users").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {
                                    for(DataSnapshot ds: snapshot.getChildren())
                                    {
                                        String UserexistingEmail=ds.child("user_email").getValue().toString().trim();
                                        String userexistingPass=ds.child("user_pass").getValue().toString().trim();
                                        if(user_created_Email.equals(UserexistingEmail) && user_created_Pass.equals(userexistingPass))
                                        {

                                            if(UserexistingEmail.equals(EdUserEmail) && userexistingPass.equals(EdUserPass))
                                            {
                                                signInExistingUsers(EdUserEmail,EdUserPass);
                                                Toast.makeText(AdminLoginActivity.this,"ID already Exists ....",Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                check_account_email_valid_from_list= FirebaseDatabase.getInstance().getReference();
                                                check_account_email_valid_from_list.child("users_created_id").addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for(DataSnapshot dsa:snapshot.getChildren())
                                                        {
                                                            String userEmail=dsa.child("user_email").getValue().toString();
                                                            String userPass=dsa.child("user_pass").getValue().toString();
                                                            if(userEmail.equals(EdUserEmail) && userPass.equals(EdUserPass))
                                                            {
                                                                idcreation(EdUserEmail,EdUserPass);
                                                            }
                                                            else
                                                            {
                                                                Toast.makeText(AdminLoginActivity.this,"Please enter the given Email-ID and Password",Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Toast.makeText(AdminLoginActivity.this,""+error,Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }

                                    }
                                }
                                else
                                {
                                    check_account_email_valid_from_list= FirebaseDatabase.getInstance().getReference();
                                    check_account_email_valid_from_list.child("users_created_id").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot dsa:snapshot.getChildren())
                                            {
                                                String userEmail=dsa.child("user_email").getValue().toString();
                                                String userPass=dsa.child("user_pass").getValue().toString();
                                                if(userEmail.equals(EdUserEmail) && userPass.equals(EdUserPass))
                                                {
                                                    idcreation(EdUserEmail,EdUserPass);
                                                }
                                                else
                                                {
                                                    Toast.makeText(AdminLoginActivity.this,"Please enter the given Email-ID and Password",Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(AdminLoginActivity.this,""+error,Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });*/