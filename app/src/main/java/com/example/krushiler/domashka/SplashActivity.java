package com.example.krushiler.domashka;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SplashActivity extends AppCompatActivity {
    String mail, pass, edit;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    FirebaseUser user;

    SharedPreferences sharedPreferencesEmail;
    SharedPreferences sharedPreferencesEditor;
    boolean connected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            mAuth = FirebaseAuth.getInstance();

            sharedPreferencesEmail = getSharedPreferences("emailPref", MODE_PRIVATE);
            sharedPreferencesEditor = getSharedPreferences("editorPref", MODE_PRIVATE);
            mail = sharedPreferencesEmail.getString("email", "");
            edit = sharedPreferencesEditor.getString("editor", "");

            if (mail!=null && mail!=""){
                signin(mail, pass);
            }else {
                Intent intent = new Intent(this, AutentificationActivity.class);
                startActivity(intent);
                finish();
            }
            myRef = FirebaseDatabase.getInstance().getReference();
        }
        else {
            connected = false;
            Intent intent = new Intent(SplashActivity.this, HomeworkActivity.class);
            intent.putExtra("connected", connected);
            startActivity(intent);
            finish();
        }


    }
    public void signin(String email, final String password)
    {
        mAuth.signInWithEmailAndPassword(mail + "@li1irk.ru", "li1irk").addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    SharedPreferences.Editor emailEditor = sharedPreferencesEmail.edit();
                    SharedPreferences.Editor editorEditor = sharedPreferencesEditor.edit();

                    sharedPreferencesEmail = getSharedPreferences("emailPref", MODE_PRIVATE);
                    sharedPreferencesEditor = getSharedPreferences("editorPref", MODE_PRIVATE);

                    emailEditor.putString("email", mail);
                    editorEditor.putString("editor", edit);

                    user = mAuth.getInstance().getCurrentUser();
                    myRef = FirebaseDatabase.getInstance().getReference();

                    emailEditor.commit();
                    editorEditor.commit();
                    Intent intent = new Intent(SplashActivity.this, HomeworkActivity.class);
                    intent.putExtra("connected", connected);
                    intent.putExtra("editorCode", edit);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(SplashActivity.this, AutentificationActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}