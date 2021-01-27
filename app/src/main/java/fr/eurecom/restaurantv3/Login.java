package fr.eurecom.restaurantv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class Login extends AppCompatActivity {

    EditText memail,mpassword;
    ProgressBar mprogressBar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        memail = findViewById(R.id.email_address_login);
        mpassword = findViewById(R.id.password_login);
        mprogressBar = findViewById(R.id.progressBar_login);

        //Check if logged in directly log in
        if(mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified()){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
    }
    public void sign_in(View view) {
        String email = memail.getText().toString().trim();
        String password = mpassword.getText().toString().trim();

        //check conditions
        if(TextUtils.isEmpty(email)){
            memail.setError("Email is Required");
            return;
        }
        if(TextUtils.isEmpty(password)){
            mpassword.setError("Password is Required");
            return;
        }
        if(password.length()<6){
            mpassword.setError("Password must be >=6 charachters");
            return;
        }
        mprogressBar.setVisibility(View.VISIBLE);

        //authentiate user
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //check if email verified
                    FirebaseUser user =mAuth.getCurrentUser();
                    if(user.isEmailVerified()){
                        make_toast("Successfully Logged in");
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else{
                        mprogressBar.setVisibility(View.INVISIBLE);
                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                make_toast("Please verify your email. Link has been resent!");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                make_toast("Email not sent due to an error" + e.getMessage().toString());
                            }
                        });
                    }
                }
                else{
                    make_toast("Error: !"+task.getException().getMessage());
                    mprogressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void sign_up(View view) {
        Intent i = new Intent(this, Register.class);
        startActivity(i);
    }

    public void forgot_password(View view) {

        EditText resetMail = new EditText(view.getContext());
        final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
        passwordResetDialog.setTitle("Password Reset");
        passwordResetDialog.setMessage("Enter your email");
        passwordResetDialog.setView(resetMail);
        passwordResetDialog.setPositiveButton("Send reset link", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String mail = resetMail.getText().toString();
                mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        make_toast("Reset link sent to your email");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        make_toast("Error! Reset link no sent"+e.getMessage().toString());
                    }
                });
            }
        });
        passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        passwordResetDialog.create().show();

    }

    public void make_toast(String text){
        Context context = getApplicationContext();
        CharSequence t = text;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, t, duration);
        toast.show();
    }
}