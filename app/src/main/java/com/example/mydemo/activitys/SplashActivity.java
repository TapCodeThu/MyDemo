package com.example.mydemo.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;

import com.example.mydemo.R;
import com.example.mydemo.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        binding.btnLogin.setOnClickListener(v->{
            startActivity(new Intent(SplashActivity.this,LoginActivity.class));
            finish();
        });
        binding.btnSignup.setOnClickListener(v->{
            startActivity(new Intent(SplashActivity.this,SignUpActivity.class));
            finish();
        });
    }
}