package com.example.krushiler.domashka;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Set;

public class AutentificationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    private EditText ETemail;
    private EditText ETeditor;

    SharedPreferences sharedPreferencesEmail;
    SharedPreferences sharedPreferencesPassword;
    SharedPreferences sharedPreferencesEditor;

    FirebaseUser user;

    String userStatus;
    String needEnter = "YES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autentification);
        Intent i = getIntent();
        needEnter = i.getStringExtra("status");
        mAuth = FirebaseAuth.getInstance();
        ETemail = (EditText) findViewById(R.id.emailet);
        ETeditor = (EditText) findViewById(R.id.editoret);

        String savedText1, savedText2, savedText3;
        sharedPreferencesEmail = getSharedPreferences("emailPref", MODE_PRIVATE);
        sharedPreferencesEditor = getSharedPreferences("editorPref", MODE_PRIVATE);
        savedText1 = sharedPreferencesEmail.getString("email", "");
        savedText3 = sharedPreferencesEditor.getString("editor", "");
        ETemail.setText(savedText1);
        ETeditor.setText(savedText3);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(AutentificationActivity.this, HomeworkActivity.class);
                    startActivity(intent);
                    // User is signed in

                } else {
                    // User is signed out

                }

            }
        };
        myRef = FirebaseDatabase.getInstance().getReference();

    }

    public void onClick(View view) {
        if (view.getId() == R.id.enterbtn) {
            signin(ETemail.getText().toString(), "li1irk");
        } else if (view.getId() == R.id.registerbtn) {
            registration(ETemail.getText().toString(), "li1irk");
        }
    }

    public void checkEditor() {
        myRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String s = ((CharSequence) dataSnapshot.child("EditorCode").getValue()).toString();
                if (ETeditor.getText().toString().equals(s)) {
                    userStatus = "editor";
                } else {
                    userStatus = "guest";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void signin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email + "@li1irk.ru", password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    SharedPreferences.Editor emailEditor = sharedPreferencesEmail.edit();
                    SharedPreferences.Editor editorEditor = sharedPreferencesEditor.edit();

                    sharedPreferencesEmail = getSharedPreferences("emailPref", MODE_PRIVATE);
                    sharedPreferencesEditor = getSharedPreferences("editorPref", MODE_PRIVATE);

                    emailEditor.putString("email", ETemail.getText().toString());
                    editorEditor.putString("editor", ETeditor.getText().toString());

                    user = mAuth.getInstance().getCurrentUser();
                    myRef = FirebaseDatabase.getInstance().getReference();

                    checkEditor();

                    emailEditor.commit();
                    editorEditor.commit();
                    Intent intent = new Intent(AutentificationActivity.this, HomeworkActivity.class);
                    intent.putExtra("userStatus", ETeditor.getText().toString());
                    intent.putExtra("editorCode", ETeditor.getText().toString());
                    startActivity(intent);
                } else
                    Toast.makeText(AutentificationActivity.this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void registration(final String email, final String password) {
        if (ETemail.getText().toString().length() > 0) {
            mAuth.createUserWithEmailAndPassword(email + "@li1irk.ru", password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        DatabaseReference myRef;

                        myRef = FirebaseDatabase.getInstance().getReference();

                        user = mAuth.getInstance().getCurrentUser();

                        myRef.child(user.getUid()).child("EditorCode").setValue(ETeditor.getText().toString());

                        SharedPreferences.Editor emailEditor = sharedPreferencesEmail.edit();
                        SharedPreferences.Editor editorEditor = sharedPreferencesEditor.edit();

                        sharedPreferencesEmail = getSharedPreferences("emailPref", MODE_PRIVATE);
                        sharedPreferencesEditor = getSharedPreferences("editorPref", MODE_PRIVATE);

                        emailEditor.putString("email", ETemail.getText().toString());
                        editorEditor.putString("editor", ETeditor.getText().toString());

                        emailEditor.commit();
                        editorEditor.commit();

                        mAuth.signInWithEmailAndPassword(email, password);
                        Intent intent = new Intent(AutentificationActivity.this, HomeworkActivity.class);
                        intent.putExtra("userStatus", ETeditor.getText().toString());
                        intent.putExtra("editorCode", ETeditor.getText().toString());
                        startActivity(intent);
                    } else {
                        Toast.makeText(AutentificationActivity.this, "Код класса уже зарегестрирован", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(AutentificationActivity.this,"Код класса не может быть пустым", Toast.LENGTH_SHORT).show();
        }
    }
    public void saveToEditorPrefs(String s){
        SharedPreferences.Editor editorEditor = sharedPreferencesEditor.edit();

        sharedPreferencesEditor = getSharedPreferences("editorPref", MODE_PRIVATE);

        editorEditor.putString("editor", s);
    }
}