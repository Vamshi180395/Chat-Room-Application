package com.example.inclass11;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {
    EditText useremail,userpassword;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Expense App (Login)");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent i=new Intent(LoginActivity.this,ChatRoomActivity.class);
            i.putExtra("UserID",user.getUid().toString());
            startActivity(i);
            finish();
        } else {
            findViewByIds();
        }

    }

    private void findViewByIds() {
        useremail= (EditText)findViewById(R.id.txtboxemail);
        userpassword= (EditText) findViewById(R.id.txtboxpassword);
    }

    public void goToRegisterActivity(View view) {
        Intent i=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(i);
        finish();
    }

    public void authenticateUserandgoToExpenseActivity(View view) {
        if(useremail.getText().toString().length()==0 || userpassword.getText().toString().length()==0) {
            Toast.makeText(LoginActivity.this, "Please enter all the fields to proceed further.",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(useremail.getText().toString(), userpassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("Demo", "signInWithEmail:onComplete:" + task.isSuccessful());
                            if (!task.isSuccessful()) {
                                Log.w("Warning", "signInWithEmail:failed", task.getException());
                                Toast.makeText(LoginActivity.this, "Invalid Credentials. Login unsuccessfull!!!",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Intent i = new Intent(LoginActivity.this, ChatRoomActivity.class);
                                i.putExtra("UserID", mAuth.getCurrentUser().getUid().toString());
                                startActivity(i);
                                finish();
                            }

                        }
                    });
        }
    }





}
