package com.example.khoj.main_ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.example.khoj.R;
import com.example.khoj.databinding.ActivityMainBinding;
import com.example.khoj.functions.FDB;
import com.example.khoj.functions.LocalDatabase;

public class MainActivity extends AppCompatActivity {

	AlertDialog here;

	GoogleSignInClient mGoogleSignInClient;
	Button signOut;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
				R.id.navigation_home, R.id.navigation_profile, R.id.navigation_orders)
				.build();
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
		NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
		NavigationUI.setupWithNavController(binding.navView, navController);


		FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
			if (firebaseAuth.getCurrentUser() == null)
				return;
			if (LocalDatabase.getInstance(getBaseContext()).getUsername() == null)
			{
				FDB.getFDB()
						.collection("users")
						.document(Objects.requireNonNull(firebaseAuth.getUid()))
						.get().addOnSuccessListener(documentSnapshot -> LocalDatabase.getInstance(getBaseContext())
						.setUsername(String.valueOf(documentSnapshot.get("USER_NAME"))));
			}
			else if (!LocalDatabase.getInstance(getBaseContext()).isUploaded())
			{
				Map<String, String> map = new HashMap<>();
				map.put("USER_NAME", LocalDatabase.getInstance(getBaseContext()).getUsername());
				FDB.getFDB()
						.collection("users")
						.document(Objects.requireNonNull(firebaseAuth.getUid()))
						.set(map, SetOptions.merge()).addOnSuccessListener(unused -> Toast.makeText(MainActivity.this, "Login Success!", Toast.LENGTH_SHORT).show())
						.addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Signup partially succeeded!", Toast.LENGTH_SHORT).show());
				LocalDatabase.getInstance(getBaseContext()).uploaded();
			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.log_out, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.log_out) {
			here = new AlertDialog.Builder(MainActivity.this)
					.setTitle("Are you sure to logout?")
					.setMessage("This will erase all local userdata and take you to the login screen.")
					.setPositiveButton("Yes", (dialog, which) -> {
						LocalDatabase.getInstance(getBaseContext()).clear();
						FirebaseAuth.getInstance().signOut();
						startActivity(new Intent(MainActivity.this, SplashActivity.class));
						MainActivity.this.finish();
					})
					.setNegativeButton("No", null)
					.create();

			if (!this.isFinishing())
				here.show();
		}
		return super.onOptionsItemSelected(item);
	}
}