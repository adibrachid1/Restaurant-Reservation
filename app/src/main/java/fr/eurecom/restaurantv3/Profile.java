package fr.eurecom.restaurantv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#B9CF5A0C")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //get information from database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        db.collection("users")
                .document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("TEST", task.getResult().getData()+"");
                            TextView a = (TextView)findViewById(R.id.profile_fullname_title);
                            a.setText(task.getResult().getData().get("fullname").toString());
                            EditText aa = (EditText)findViewById(R.id.profile_fullname);
                            aa.setText(task.getResult().getData().get("fullname").toString());
                            a = (TextView) findViewById(R.id.profile_email);
                            a.setText(task.getResult().getData().get("email").toString());
                            aa = (EditText)findViewById(R.id.profile_phone_number);
                            aa.setText(task.getResult().getData().get("phone_number").toString());
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    public void save_changes(View view){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        EditText fullname = (EditText)findViewById(R.id.profile_fullname);
        String _full_name = fullname.getText().toString().trim();
        EditText phone_number = (EditText)findViewById(R.id.profile_phone_number);
        String _phone_number = phone_number.getText().toString().trim();
        user.put("fullname", _full_name);
        user.put("phone_number",_phone_number);
        user.put("email",FirebaseAuth.getInstance().getCurrentUser().getEmail());

        //Checks
        if(TextUtils.isEmpty(_full_name)){
            fullname.setError("Full Name is Required");
            return;
        }
        if(TextUtils.isEmpty(_phone_number)){
            phone_number.setError("Phone number is Required");
            return;
        }
        if(_phone_number.length()<8){
            phone_number.setError("Phone Number must be >=8 charachters");
            return;
        }
        if(_full_name.length()<5){
            fullname.setError("Full Name must be >=5 charachters");
            return;
        }
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).set(user);
        make_toast("Changes saved successfully!");
        finish();
    }
    public void make_toast(String text){
        Context context = getApplicationContext();
        CharSequence t = text;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, t, duration);
        toast.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}