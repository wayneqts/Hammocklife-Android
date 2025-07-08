package com.app.hammocklife.fragment;

import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.hammocklife.Main;
import com.app.hammocklife.R;
import com.app.hammocklife.model.ObjectHammocks;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

public class BaseFragment extends Fragment {
    int wwidth = 0;
    int height = 0;
    boolean checkLoginOrSkip = false;
    static ArrayList<ObjectHammocks> arrHammock = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static Location location_current;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWidthHeight();
    }

    private void getWidthHeight() {
        try {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            height = displaymetrics.heightPixels;
            wwidth = displaymetrics.widthPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setLayoutView(View view, int width, int height) {
        if (view != null) {
            view.getLayoutParams().width = width;
            view.getLayoutParams().height = height;
        }
    }

    public void showLoading(){
        try {
            ((Main) Objects.requireNonNull(getActivity())).showLoading();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void hideLoading(){
        try {
            ((Main) Objects.requireNonNull(getActivity())).hideLoading();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addFragmentBack(Fragment fragment) {
        FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);
        fragmentTransaction.add(R.id.frameLayout,fragment);
        fragmentTransaction.addToBackStack(fragment.getTag());
        fragmentTransaction.commit();
    }

    public void addFragmentBack2(Fragment fragment) {
        FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);
        fragmentTransaction.add(R.id.frameLayout,fragment);
        fragmentTransaction.disallowAddToBackStack();
        fragmentTransaction.commit();
    }
}
