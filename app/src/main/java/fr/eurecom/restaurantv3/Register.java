package fr.eurecom.restaurantv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText mfullname,mphonenumber,memail,mpassword;
    ProgressBar mprogressBar;

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        memail = findViewById(R.id.email_address);
        mpassword = findViewById(R.id.password);
        mphonenumber = findViewById(R.id.phone_number);
        mfullname = findViewById(R.id.full_name);
        mprogressBar = findViewById(R.id.progressBar_register);
    }

    public void register(View view){
        String email = memail.getText().toString().trim();
        String password = mpassword.getText().toString().trim();
        String fullname = mfullname.getText().toString().trim();
        String phonenumber = mphonenumber.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            memail.setError("Email is Required");
            return;
        }
        if(TextUtils.isEmpty(password)){
            mpassword.setError("Password is Required");
            return;
        }
        if(TextUtils.isEmpty(fullname)){
            mfullname.setError("Full Name is Required");
            return;
        }
        if(TextUtils.isEmpty(phonenumber)){
            mphonenumber.setError("Phone number is Required");
            return;
        }
        if(password.length()<6){
            mpassword.setError("Password must be >=6 charachters");
            return;
        }
        if(phonenumber.length()<8){
            mphonenumber.setError("Phone Number must be >=8 charachters");
            return;
        }
        if(fullname.length()<5){
            mfullname.setError("Full Name must be >=5 charachters");
            return;
        }
        mprogressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    //send verification link
                    FirebaseUser user = mAuth.getCurrentUser();
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //save instance in DB
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String, Object> user = new HashMap<>();
                            user.put("fullname", fullname);
                            user.put("email", email);
                            user.put("phone_number",phonenumber);
                            db.collection("users").document(email).set(user);
                            make_toast("Please verify your email");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            make_toast("Error: Email not sent"+e.getMessage().toString());
                        }
                    });
                    Intent i = new Intent(getApplicationContext(), Login.class);
                    i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(i);
                    finish();
                }
                else{
                    make_toast("Error: !"+task.getException().getMessage());
                    mprogressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void make_toast(String text){
        Context context = getApplicationContext();
        CharSequence t = text;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, t, duration);
        toast.show();
    }
}