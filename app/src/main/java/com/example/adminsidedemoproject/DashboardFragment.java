package com.example.adminsidedemoproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adminsidedemoproject.Adapter.DashboardAdapter;
import com.example.adminsidedemoproject.Model.DashboardModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class DashboardFragment extends Fragment {
RecyclerView dashRecycler;
DashboardAdapter dashAdapter;
ArrayList<DashboardModel> dashModel;
private FirebaseAuth fireAuth;
private DatabaseReference fireDbRef;
private FirebaseAuth fireauth;
private String currentuserid;
private DatabaseReference firedb;
TextView adminName,adminEmail,adminRole;
CircleImageView custom_profile_image;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v= inflater.inflate(R.layout.fragment_dashboard, container, false);
        getActivity().setTitle("Dashboard");
        dashRecycler=v.findViewById(R.id.dashboard_Options_recyclerview);
        fireauth= FirebaseAuth.getInstance();
        currentuserid=fireauth.getCurrentUser().getUid();
        firedb= FirebaseDatabase.getInstance().getReference();
        fireAuth=FirebaseAuth.getInstance();
        fireDbRef= FirebaseDatabase.getInstance().getReference();
        dashModel=new ArrayList<>();
        dashAdapter=new DashboardAdapter(getContext(),dashModel);
        RecyclerView.LayoutManager layoutManager =new GridLayoutManager(getContext(), 3);
        dashRecycler.setAdapter(dashAdapter);
        dashRecycler.setLayoutManager(layoutManager);
        adminName=v.findViewById(R.id.dashboard_admin_name);
        adminEmail=v.findViewById(R.id.dashboard_admin_email);
        adminRole=v.findViewById(R.id.dashboard_admin_role);
        custom_profile_image=v.findViewById(R.id.custom_profile_image);
        loadData();
        loadAdminData();
        return v;
    }
    public void loadAdminData(){
        firedb.child("Admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName=snapshot.child("AdminName").getValue().toString();
                String userEmail=snapshot.child("AdminEmail").getValue().toString();
                String userRole=snapshot.child("AdminRole").getValue().toString();
                String userImage=snapshot.child("AdminImage").getValue().toString();

                adminName.setText(userName);
                adminEmail.setText(userEmail);
                adminRole.setText(userRole);
                if(userImage.equals(""))
                {

                }
                else
                {
                    Picasso.get().load(userImage).into(custom_profile_image);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loadData()
    {
        fireDbRef.child("AdminDashBoard").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds:snapshot.getChildren())
                    {
                        String nameOfDashboardItems=ds.child("name").getValue().toString();
                        String iconOfDashboardItems=ds.child("icon").getValue().toString();
                        String notificationOfDashboardItems=ds.child("notification").getValue().toString();
                        dashModel.add(new DashboardModel(nameOfDashboardItems,iconOfDashboardItems,notificationOfDashboardItems));
                       // Toast.makeText(getContext(), ""+iconOfDashboardItems, Toast.LENGTH_SHORT).show();
                    }
                dashAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}