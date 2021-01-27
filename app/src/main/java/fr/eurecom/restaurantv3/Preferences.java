package fr.eurecom.restaurantv3;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Preferences extends AppCompatActivity {
    String preferences_array[] = {"finedining","casualdining","contemporarycasual","familystyle","fastcasual","fastfood","cafe","buffet","popup","indoor","outdoor","parking"};
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        getSupportActionBar().setTitle("Preferences");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#B9CF5A0C")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = FirebaseFirestore.getInstance();
        load_values();
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
    public void save_changes(View view){
        Map<String, Object> preferences = new HashMap<>();
        for (String pre:preferences_array){
            if(((CheckBox) findViewById(getResources().getIdentifier("checkBox_"+pre, "id", getPackageName()))).isChecked()){
                preferences.put(pre,1);
            }else{
                preferences.put(pre,0);
            }
        }
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("information").document("preferences").set(preferences, SetOptions.merge());
        make_toast("Changes saved successfully!");
        finish();
    }
    public void load_values(){
        db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection("information")
                .document("preferences")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("TEST", task.getResult().getData()+"");
                            Map map = task.getResult().getData();
                            Iterator<String> iterator = map.keySet().iterator();
                            while (iterator.hasNext()) {
                                String key = iterator.next();
                                if(map.get(key).toString().equals("1")){
                                    CheckBox a = ((CheckBox) findViewById(getResources().getIdentifier("checkBox_" + key, "id", getPackageName())));
                                    a.setChecked(true);
                                }
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
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