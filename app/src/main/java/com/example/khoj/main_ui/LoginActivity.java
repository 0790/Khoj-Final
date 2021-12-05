package com.example.khoj.main_ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.khoj.functions.FDB;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.SetOptions;
import com.mukesh.OtpView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.example.khoj.R;
import com.example.khoj.databinding.ActivityLoginBinding;
import com.example.khoj.functions.LocalDatabase;

public class LoginActivity extends AppCompatActivity {

	protected ActivityLoginBinding binding;
	private boolean mode = false;
	private AlertDialog alertDialog;
	private Local_Caller caller;
	private AlertDialog alertDialog_later;
	private FirebaseAuth mAuth;
	SignInButton signin;
	GoogleSignInClient mGoogleSignInClient;
	int RC_SIGN_IN=0;
	String TAG = "SignIn Activity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getString(R.string.default_web_client_id))
				.requestEmail()
				.build();
		mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
		mAuth = FirebaseAuth.getInstance();
		binding = ActivityLoginBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		String prompt = getString(R.string.already_have_an_account_login_here);
		Spannable spannable = new SpannableString(prompt);
		spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#185ADB")),
				prompt.length() - "Login here.".length(),
				prompt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		binding.loginPrompt.setText(spannable, TextView.BufferType.SPANNABLE);

		binding.loginPrompt.setOnClickListener(v -> {
			binding.signUpLoginText.setText(getString(R.string.login_in));
			binding.loginPrompt.setVisibility(View.GONE);
			binding.signUp.setText(getString(R.string.login_in_w_dot));
			binding.nameLayout.setVisibility(View.GONE);
			mode = true;

		});

		binding.signUp.setOnClickListener(v -> startLogin(binding.signUp));
		// Configure sign-in to request the user's ID, email address, and basic
		// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
		findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
					case R.id.sign_in_button:
						signIn();
						break;
				}
			}
		});
	}

	private void signIn() {
		Intent signInIntent = mGoogleSignInClient.getSignInIntent();
		startActivityForResult(signInIntent, RC_SIGN_IN);
	}

	public void onStart()
	{
            super.onStart();
		// Check if user is signed in (non-null) and update UI accordingly.
		FirebaseUser currentUser = mAuth.getCurrentUser();
		updateUI(currentUser);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
		if (requestCode == RC_SIGN_IN)
		{
			// The Task returned from this call is always completed, no need to attach
			// a listener.
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			try {
				// Google Sign In was successful, authenticate with Firebase
				GoogleSignInAccount account = task.getResult(ApiException.class);
				Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
				firebaseAuthWithGoogle(account.getIdToken());
			} catch (ApiException e) {
				// Google Sign In failed, update UI appropriately
				Log.w(TAG, "Google sign in failed", e);
			}
		}
	}

	private void firebaseAuthWithGoogle(String idToken)
	{
		AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							// Sign in success, update UI with the signed-in user's information
							Log.d(TAG, "signInWithCredential:success");
							FirebaseUser user = mAuth.getCurrentUser();
							updateUI(user);
						} else {
							// If sign in fails, display a message to the user.
							Log.w(TAG, "signInWithCredential:failure", task.getException());
							updateUI(null);
						}
					}
				});
	}

	private void updateUI(FirebaseUser user)
	{
		if(user!=null)
		{
			String personName = user.getDisplayName();
			String personEmail = user.getEmail();
			LocalDatabase.getInstance(getBaseContext()).setUsername(personName);
			LocalDatabase.getInstance(getBaseContext()).setEmail(personEmail);
			moveIntoMainMenu();
		}
	}

	public void startLogin(View view) {
		FirebaseAuth mAuth = FirebaseAuth.getInstance();

		String phone_number_text = Objects.requireNonNull(binding.phoneInput.getText()).toString();
		if (!phone_number_text.contains("+91"))
			phone_number_text = "+91" + phone_number_text;

		PhoneAuthOptions options =
				PhoneAuthOptions.newBuilder(mAuth)
						.setPhoneNumber(phone_number_text)
						.setTimeout(60L, TimeUnit.SECONDS)
						.setActivity(this)
						.setCallbacks(mCallbacks(mAuth))
						.build();
		PhoneAuthProvider.verifyPhoneNumber(options);
		Toast.makeText(this, "Please wait for OTP to be sent!", Toast.LENGTH_LONG).show();
	}

	private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks(FirebaseAuth auth) {
		return new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

			@Override
			public void onCodeSent(@NonNull String signature, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
				Toast.makeText(LoginActivity.this, "Verification Code Sent", Toast.LENGTH_SHORT).show();
				showOTPMenu(signature, auth);
				super.onCodeSent(signature, forceResendingToken);
			}

			@Override
			public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
				caller.timeout();
				super.onCodeAutoRetrievalTimeOut(s);
			}

			@Override
			public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
				Toast.makeText(LoginActivity.this, "Logged in!", Toast.LENGTH_SHORT).show();
				loggedIn();
				tryToVerify(auth, phoneAuthCredential);
				moveIntoMainMenu();
			}

			@Override
			public void onVerificationFailed(@NonNull FirebaseException e) {
				e.printStackTrace();
				Toast.makeText(LoginActivity.this, "Can't verify, please try again", Toast.LENGTH_SHORT).show();
			}
		};
	}

	private void showOTPMenu(String signature, FirebaseAuth auth) {
		View progressView = getLayoutInflater().inflate(R.layout.otp_dialogue, null);

		MaterialButton enter_otp = progressView.findViewById(R.id.enter_otp);
		MaterialButton otp_cancel = progressView.findViewById(R.id.otp_cancel);

		enter_otp.setOnClickListener(v -> caller.timeout());
		otp_cancel.setOnClickListener(v -> {
			if (alertDialog != null && alertDialog.isShowing())
				alertDialog.dismiss();
		});

		alertDialog = new AlertDialog.Builder(this)
				.setView(progressView)
				.setCancelable(false)
				.create();

		caller = () -> {
			alertDialog.dismiss();

			View progressView_later = getLayoutInflater().inflate(R.layout.otp_dialogue_2nd, null);
			alertDialog_later = new AlertDialog.Builder(LoginActivity.this)
					.setView(progressView_later)
					.setCancelable(false)
					.create();

			OtpView otp_view = progressView_later.findViewById(R.id.otp_view);

			otp_view.setOtpCompletionListener(otp -> tryToVerify(signature, auth, otp));

			alertDialog_later.show();
		};

		alertDialog.show();
	}

	private void loggedIn() {
		if (!mode) {
			LocalDatabase.getInstance(getBaseContext()).setUsername(Objects.requireNonNull(binding.nameInput.getText()).toString());
		}
	}

	private void tryToVerify(FirebaseAuth mAuth, PhoneAuthCredential credential) {
		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, task -> {
					if (task.isSuccessful()) {
						Toast.makeText(LoginActivity.this, "Logged in!", Toast.LENGTH_SHORT).show();
						loggedIn();
						moveIntoMainMenu();
					} else {
						Toast.makeText(LoginActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
					}
				});
	}

	private void tryToVerify(String signature, FirebaseAuth auth, String otp) {
		PhoneAuthCredential credential = PhoneAuthProvider.getCredential(signature, otp);
		auth.signInWithCredential(credential).addOnSuccessListener(authResult -> {
			Toast.makeText(LoginActivity.this, "Logged in!", Toast.LENGTH_SHORT).show();
			loggedIn();
			moveIntoMainMenu();
		}).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Wrong OTP", Toast.LENGTH_SHORT).show());
	}

	@Override
	protected void onPause() {
		if (alertDialog != null && alertDialog.isShowing())
			alertDialog.dismiss();

		if (alertDialog_later != null && alertDialog_later.isShowing())
			alertDialog_later.dismiss();
		super.onPause();
	}

	private void moveIntoMainMenu() {
		startActivity(new Intent(this, MainActivity.class));
		finish();
		storePhone();
	}

	private void storePhone() {
		LocalDatabase.getInstance(this).setPhone(Objects.requireNonNull(binding.phoneInput.getText()).toString());
	}

	private interface Local_Caller {
		void timeout();
	}
}