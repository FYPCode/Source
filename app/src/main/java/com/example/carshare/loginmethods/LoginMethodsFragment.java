package com.example.carshare.loginmethods;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.carshare.R;
import com.example.carshare.login.LoginActivity;

public class LoginMethodsFragment extends Fragment {

    Activity context;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        context = getActivity();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_methods, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.login_user).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(LoginMethodsFragment.this)
//                        .navigate(R.id.action_LoginMethodsFragment_to_LoginActivity);
//            }
              public void onClick(View view){
                  Button bt= (Button)context.findViewById(R.id.login_user);
                  //create an Intent object
                  Intent intent=new Intent(context, LoginActivity.class);
                  //add data to the Intent object
                  intent.putExtra("text", bt.getText().toString());
                  //start the second activity
                  startActivity(intent);
              }
        });

        view.findViewById(R.id.login_car_owner).setOnClickListener(new View.OnClickListener() {
              public void onClick(View view){
                  Button bt= (Button)context.findViewById(R.id.login_car_owner);
                  //create an Intent object
                  Intent intent=new Intent(context, LoginActivity.class);
                  //add data to the Intent object
                  intent.putExtra("text", bt.getText().toString());
                  //start the second activity
                  startActivity(intent);
              }
        });

        view.findViewById(R.id.login_administrator).setOnClickListener(new View.OnClickListener() {
              public void onClick(View view){
                  Button bt= (Button)context.findViewById(R.id.login_administrator);
                  //create an Intent object
                  Intent intent=new Intent(context, LoginActivity.class);
                  //add data to the Intent object
                  intent.putExtra("text", bt.getText().toString());
                  //start the second activity
                  startActivity(intent);
              }
        });

        view.findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                Button bt= (Button)context.findViewById(R.id.register);
                //create an Intent object
                Intent intent=new Intent(context, LoginActivity.class);
                //add data to the Intent object
                intent.putExtra("text", bt.getText().toString());
                //start the second activity
                startActivity(intent);
            }
        });
    }
}