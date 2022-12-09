package com.example.adminsidedemoproject;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;


public class GenerateIdFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    Spinner sp;
    private DatabaseReference myref,usrref;

    private FirebaseAuth UserAuth;
    ArrayList<String> list;
    ArrayAdapter<String> arrayAdapter;
    Button generatePassword,save_btn,add_emp_role,add_factory_role;
    EditText generatedPass,um_enter_email_id_of_user,um_enter_name_of_user,emp_role_et;
    String employeeId;
    private Dialog addRoleDialogue;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v=inflater.inflate(R.layout.fragment_generate_id,container,false);


        sp=v.findViewById(R.id.role_of_the_user);
        getActivity().setTitle("User Manage");
        generatedPass=v.findViewById(R.id.um_generate_password_of_user);
        generatePassword=v.findViewById(R.id.um_btn_generate_password_of_user);
        um_enter_email_id_of_user=v.findViewById(R.id.um_enter_email_id_of_user);
        save_btn=v.findViewById(R.id.create_Id);
        um_enter_name_of_user=v.findViewById(R.id.um_enter_name_of_user);
        UserAuth= FirebaseAuth.getInstance();
        add_factory_role=v.findViewById(R.id.add_factory_role);
        list=new ArrayList<>();
        arrayAdapter=new ArrayAdapter<>(getContext(), R.layout.style_spinner,list);
        sp.setAdapter(arrayAdapter);

        usrref=  FirebaseDatabase.getInstance().getReference();
        usrref.child("RolesInFactory").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
            {
                for(DataSnapshot ds:snapshot.getChildren())
                {

                    String name=ds.child("name").getValue().toString();
                    list.add((name));
                    arrayAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        generatedPass.setEnabled(false);
        generatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatedPass.setText(password_generation());

            }
        });
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(um_enter_name_of_user.getText().toString().isEmpty())
                {
                    um_enter_name_of_user.setError("Required");
                    return;
                }
                if(generatedPass.getText().toString().isEmpty())
                {
                    generatedPass.setError("Required");
                    return;
                }
                if(um_enter_email_id_of_user.getText().toString().isEmpty())
                {
                    um_enter_email_id_of_user.setError("Required");
                    return;
                }
                else
                {
                    save_data_to_database();
                }
            }
        });
        add_factory_role.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRoleDialogue.show();
            }
        });
        setAddEmpRole();
        return v;
    }


    private void setAddEmpRole() {
        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat FromToDateFormate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        addRoleDialogue =new Dialog(getContext());
        addRoleDialogue.setContentView(R.layout.add_emp_role_dialogue);
        addRoleDialogue.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.rounded_box));
        addRoleDialogue.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        addRoleDialogue.setCancelable(true);


        emp_role_et=addRoleDialogue.findViewById(R.id.emp_role_et);

        add_emp_role=addRoleDialogue.findViewById(R.id.add_emp_role);
        HashMap<String,Object> map =new HashMap<>();

        add_emp_role.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(emp_role_et.getText().toString().matches("") )
                {
                    emp_role_et.setError("Required");
                    return;
                }
                else
                {
                    DatabaseReference data=FirebaseDatabase.getInstance().getReference();
                    map.put("name",emp_role_et.getText().toString());

                    data.child("RolesInFactory").child(emp_role_et.getText().toString()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(), "Sucessfully Party Added", Toast.LENGTH_SHORT).show();
                            addRoleDialogue.dismiss();
                        }
                    });
                }

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        //Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    private static String password_generation(){

        String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";

        String numbers = "1234567890";
        String combinedChars = capitalCaseLetters + lowerCaseLetters + numbers;
        Random random = new Random();
        char[] password = new char[6];

        password[0] = lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length()));
        password[1] = capitalCaseLetters.charAt(random.nextInt(capitalCaseLetters.length()));
        password[2] = numbers.charAt(random.nextInt(numbers.length()));

        for(int i = 3; i< 6 ; i++) {
            password[i] = combinedChars.charAt(random.nextInt(combinedChars.length()));
        }
        return String.valueOf(password);
    }

    public static String id_generate_automatically(){

        int min = 111111;
        int max=666666;
        Random random = new Random();
        String s=String.valueOf((random.nextInt(max - min) + min));
        return s;
    }

    public void save_data_to_database(){


        String UserPass= generatedPass.getText().toString().trim();
        String UserRegEmail=um_enter_email_id_of_user.getText().toString().trim();
        String userRole=sp.getSelectedItem().toString();
        employeeId=id_generate_automatically();
        String userName=um_enter_name_of_user.getText().toString().trim();
        DatabaseReference checkIfEmpIdPresent=FirebaseDatabase.getInstance().getReference();
        checkIfEmpIdPresent.child("UsersCreatedId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.hasChild(employeeId))
                {
                   Toast.makeText(getContext(),"ID Exists please try again",Toast.LENGTH_SHORT).show();
                    id_generate_automatically();
                }
                else
                {
                    HashMap<String,Object> userDetails=new HashMap<>();
                    userDetails.put("UserName",userName);
                    userDetails.put("UserEmail",UserRegEmail);
                    userDetails.put("UserPass", UserPass);
                    userDetails.put("UserRole",userRole);
                    userDetails.put("EmployeeId",employeeId);
                    userDetails.put("UserImage","https://firebasestorage.googleapis.com/v0/b/heliosenterprisesm-f9c59.appspot.com/o/profile.png?alt=media&token=47456f18-c5cb-4983-b5d6-af6ab7df7bf0");
                    userDetails.put("UserPhone","");
                    userDetails.put("UserDob","");
                    userDetails.put("UserGender","");
                    DatabaseReference userDetailsReference=FirebaseDatabase.getInstance().getReference();
                    userDetailsReference.child("UsersCreatedId").child(employeeId).setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            employeeId="";
                            generatedPass.setText("");
                            um_enter_email_id_of_user.setText("");
                            um_enter_name_of_user.setText("");
                            Toast.makeText(getContext(), "Successfully Generated-ID", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}















/*    public void idcreation(){
        String UserPass= generatedPass.getText().toString().trim();
        String UserRegEmail=um_enter_email_id_of_user.getText().toString().trim();
       *//* UserAuth.createUserWithEmailAndPassword(UserRegEmail, UserPass).
            addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(User_Manage_Activity.this,"ID CREATED",Toast.LENGTH_SHORT).show();
                    }
                }
            });*//*
    }*/