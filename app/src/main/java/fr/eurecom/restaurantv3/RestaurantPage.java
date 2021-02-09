package fr.eurecom.restaurantv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class RestaurantPage extends AppCompatActivity {
    String resto_id;
    String resto_name;
    String resto_opening;
    String resto_closing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_page);
        resto_id = getIntent().getStringExtra("resto_id");
        resto_name = getIntent().getStringExtra("resto_name");
        resto_closing = getIntent().getStringExtra("resto_closing");
        resto_opening = getIntent().getStringExtra("resto_opening");
        getSupportActionBar().setTitle("Restaurant");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#B9CF5A0C")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("country")
                .document("France")
                .collection("postal")
                .document("06000")
                .collection("restaurants")
                .document(resto_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Restaurant r = new Restaurant(
                                    document.getId(),
                                    document.getData().get("name").toString(),
                                    document.getData().get("rating").toString(),
                                    document.getData().get("opening_time").toString(),
                                    document.getData().get("closing_time").toString(),
                                    document.getData().get("type").toString(),
                                    document.getData().get("price").toString(),
                                    document.getData().get("logo_URL").toString(),
                                    document.getData().get("menu").toString());
                            TextView a = (TextView) findViewById(R.id.restauran_name);
                            a.setText(r.name);
                            a = (TextView) findViewById(R.id.restaurant_price);
                            a.setText(r.price+" $");
                            a = (TextView) findViewById(R.id.restaurant_rating);
                            a.setText(r.rating+" / 5");
                            a = (TextView) findViewById(R.id.restaurant_opening);
                            a.setText(r.opening_time+":00 - "+r.closing_time+":00");
                            a = (TextView) findViewById(R.id.restaurant_type);
                            a.setText(r.type);
                            ImageView aa = (ImageView) findViewById(R.id.restaurant_image);
                            Picasso.with(getApplicationContext()).load(r.logo).into(aa);
                            TextView t = (TextView) findViewById(R.id.restaurant_menu);
                            t.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(r.menu));
                                    i.putExtra("resto_id", r.id);
                                    i.putExtra("resto_name", r.name);
                                    startActivity(i);
                                }
                            });
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void go_to_reserve(View view){
        Intent intent = new Intent(view.getContext(), Reservation.class);
        intent.putExtra("resto_id", resto_id);
        intent.putExtra("resto_name", resto_name);
        intent.putExtra("resto_opening", resto_opening);
        intent.putExtra("resto_closing", resto_closing);
        view.getContext().startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}