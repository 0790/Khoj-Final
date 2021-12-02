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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OtpView;

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

	SignInButton signin;
	GoogleSignInClient mGoogleSignInClient;
	int RC_SIGN_IN=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

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

		signin = findViewById(R.id.sign_in_button);
		signin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
					case R.id.sign_in_button:
						signIn();
						break;
					// ...
				}
			}
		});
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestEmail()
				.build();
		// Build a GoogleSignInClient with the options specified by gso.

	/*	GoogleSignIn.silentSignIn()
				.addOnCompleteListener(
						this,
						new OnCompleteListener<GoogleSignInAccount>() {
							@Override
							public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
								handleSignInResult(task);
							}
						});
*/
		mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
	}

	private void signIn() {
		Intent signInIntent = mGoogleSignInClient.getSignInIntent();
		startActivityForResult(signInIntent, RC_SIGN_IN);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
		if (requestCode == RC_SIGN_IN) {
			// The Task returned from this call is always completed, no need to attach
			// a listener.
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			handleSignInResult(task);
		}
	}

	private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
		try {
			GoogleSignInAccount account = completedTask.getResult(ApiException.class);

			// Signed in successfully, show authenticated UI.
			//updateUI(account);
			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(intent);
		} catch (ApiException e) {
			// The ApiException status code indicates the detailed failure reason.
			// Please refer to the GoogleSignInStatusCodes class reference for more information.
			Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		// Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
		GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
		//updateUI(account);
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

	private void loggedIn() {
		if (!mode) {
			LocalDatabase.getInstance(getBaseContext()).setUsername(Objects.requireNonNull(binding.nameInput.getText()).toString());
		}
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