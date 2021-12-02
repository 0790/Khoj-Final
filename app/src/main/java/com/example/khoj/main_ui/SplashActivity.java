package com.example.khoj.main_ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.khoj.R;
import com.example.khoj.functions.LocalDatabase;

public class SplashActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		boolean b;
		b = new Handler().postDelayed((Runnable) () -> {
		startActivity(new Intent(this, LocalDatabase.getInstance(this).getPhoneNumber() != null ?
				MainActivity.class : LoginActivity.class));
		finish();
		}, 3000);
	}
}