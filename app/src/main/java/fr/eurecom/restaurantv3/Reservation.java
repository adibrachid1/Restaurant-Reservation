package fr.eurecom.restaurantv3;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

public class Reservation extends AppCompatActivity {

    String resto_id;
    String resto_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        resto_id = getIntent().getStringExtra("resto_id");
        resto_name = getIntent().getStringExtra("resto_name");
        getSupportActionBar().setTitle("Reservation - "+resto_name);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#B9CF5A0C")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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