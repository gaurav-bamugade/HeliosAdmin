package com.example.adminsidedemoproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;


public class SendMailFragment extends Fragment implements AdapterView.OnItemSelectedListener{
    EditText sendEmailTo,sendMailSubject,sendMailMessage;
    Button sendMailButton;
    private DatabaseReference myref,usrref;
    private FirebaseAuth UserAuth;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> list;
    Spinner select_email_spinner;
    CardView show_hide_spinner;
    RelativeLayout spinner_relative;
    ImageButton arrow_show_email_id;
    TextView hidden_spinner_text;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_send_mail, container, false);
        getActivity().setTitle("User Manage");

        sendEmailTo=v.findViewById(R.id.send_mail_to_et);
        sendMailSubject=v.findViewById(R.id.send_mail_subject_et);
        sendMailMessage=v.findViewById(R.id.send_mail_message_et);
        sendMailButton=v.findViewById(R.id.send_mail_btn);
        select_email_spinner=v.findViewById(R.id.select_email_spinner);
        show_hide_spinner=v.findViewById(R.id.show_hide_spinner);
        spinner_relative=v.findViewById(R.id.spinner_relative);
        arrow_show_email_id=v.findViewById(R.id.arrow_show_email_id);
        hidden_spinner_text=v.findViewById(R.id.hidden_spinner_text);
        sendMailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });

        list=new ArrayList<>();
        arrayAdapter=new ArrayAdapter<>(getContext(), R.layout.style_spinner,list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        select_email_spinner.setAdapter(arrayAdapter);

        usrref=  FirebaseDatabase.getInstance().getReference();
        usrref.child("UsersCreatedId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
            {
                for(DataSnapshot ds:snapshot.getChildren())
                {

                    String name=ds.child("UserEmail").getValue().toString();
                    list.add((name));
                    arrayAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        show_hide_spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner_relative.getVisibility()==View.GONE)
                {
                    spinner_relative.setVisibility(View.VISIBLE);
                    arrow_show_email_id.setImageResource(R.drawable.ic_arrow_up_24);

                }
                else
                {
                    if(spinner_relative.getVisibility()==View.VISIBLE)
                    {
                        spinner_relative.setVisibility(View.GONE);
                        arrow_show_email_id.setImageResource(R.drawable.ic_arrow_down_24);
                    }
                }
            }
        });
        return v;
    }
    private void sendMail(){
        if(spinner_relative.getVisibility()==View.GONE)
        {
            String reciepientList=sendEmailTo.getText().toString();
            String[] recipients=reciepientList.split(",");
            String subject=sendMailSubject.getText().toString();
            String message=sendMailMessage.getText().toString();
            Intent i=new Intent(Intent.ACTION_SEND);
            i.putExtra(Intent.EXTRA_EMAIL,recipients);
            i.putExtra(Intent.EXTRA_SUBJECT,subject);
            i.putExtra(Intent.EXTRA_TEXT,message);
            i.setType("message/rfc822");
            startActivity(Intent.createChooser(i,"choose an email client"));
        }
        else if(spinner_relative.getVisibility()==View.VISIBLE)
        {
            hidden_spinner_text.setText(select_email_spinner.getSelectedItem().toString());
            usrref.child("UsersCreatedId").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
                {
                    for(DataSnapshot ds:snapshot.getChildren())
                    {
                        String email=ds.child("UserEmail").getValue().toString();
                        String pass=ds.child("UserPass").getValue().toString();
                        if(hidden_spinner_text.getText().equals(email))
                        {
                            String reciepientList=sendEmailTo.getText().toString();
                            String[] recipients=reciepientList.split(",");
                            String subject=sendMailSubject.getText().toString();
                            String message=sendMailMessage.getText().toString();
                            Intent i=new Intent(Intent.ACTION_SEND);
                            i.putExtra(Intent.EXTRA_EMAIL,recipients);
                            i.putExtra(Intent.EXTRA_SUBJECT,subject);
                            i.putExtra(Intent.EXTRA_TEXT,message+"\n"+"\n"+"Email : "+email+"\n"+"\n"+"Password : "+pass);
                            i.setType("message/rfc822");
                            startActivity(Intent.createChooser(i,"choose an email client"));
                        }

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

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}