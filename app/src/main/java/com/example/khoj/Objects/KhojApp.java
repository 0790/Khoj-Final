package com.example.khoj.Objects;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;

public class KhojApp extends Application {
	@Override
	public void onCreate() {
		FirebaseApp.initializeApp(this);
		FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
		firebaseAppCheck.installAppCheckProviderFactory(
				SafetyNetAppCheckProviderFactory.getInstance());
		super.onCreate();
	}
}
