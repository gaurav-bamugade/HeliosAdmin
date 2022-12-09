package com.example.adminsidedemoproject.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.adminsidedemoproject.GenerateIdFragment;
import com.example.adminsidedemoproject.SendMailFragment;
import com.example.adminsidedemoproject.UserManageActivity;



public class UserManageTabAdapter extends FragmentPagerAdapter {

    public UserManageTabAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                GenerateIdFragment cf= new GenerateIdFragment ();
                return cf;
            case 1:
                SendMailFragment cd= new SendMailFragment ();
                return cd;

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:

                return "Generate-ID";
            case 1:
                return "Send Mail";

            default:
                return null;
        }

    }
}
