package fr.eurecom.restaurantv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Reservation extends AppCompatActivity implements
        View.OnClickListener {
    int[] viewCoords = new int[2];
    ImageView a;
    LinearLayout lin_lay;
    String resto_id;
    int x = 1;
    String resto_name;
    Button btnDatePicker, btnTimePicker;
    EditText mtxtDate, mtxtTime;
    int table_x,table_y, table_places;
    private int mYear, mMonth, mDay, mHour, mMinute;
    CircleView circleView;
    ArrayList<String[]> tables;
    ArrayList<String[]> already_reserved_tables;
    int canReserve = 0;
    int deposit = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        resto_id = getIntent().getStringExtra("resto_id");
        resto_name = getIntent().getStringExtra("resto_name");
        getSupportActionBar().setTitle("Reservation - "+resto_name);
        lin_lay = (LinearLayout) findViewById(R.id.lin_lay);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#B9CF5A0C")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tables = new ArrayList<>(6);
        already_reserved_tables = new ArrayList<>(6);
        //data time
        btnDatePicker=(Button)findViewById(R.id.btn_date);
        btnTimePicker=(Button)findViewById(R.id.btn_time);
        mtxtDate=(EditText)findViewById(R.id.in_date);
        mtxtTime=(EditText)findViewById(R.id.in_time);
        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
    }

    public void reserve(View view){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(canReserve == 1){
            new AlertDialog.Builder(view.getContext())
                    .setTitle("Deposit")
                    .setMessage("Total: "+this.deposit*table_places+" € \nAccept?\nYou can cancel reservation up to 1h before the reservation time.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Map<String, Object> reservation = new HashMap<>();
                            reservation.put("date", mtxtDate.getText().toString().trim());
                            reservation.put("time", mtxtTime.getText().toString().trim());
                            reservation.put("table_x",table_x);
                            reservation.put("table_y",table_y);
                            reservation.put("table_places",table_places);
                            reservation.put("deposit",deposit*table_places);
                            db.collection("country")
                                    .document("France")
                                    .collection("postal")
                                    .document("06000")
                                    .collection("restaurants")
                                    .document(resto_id)
                                    .collection("reservations")
                                    .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                                    .set(reservation);
                            Map<String, Object> reservation_user = new HashMap<>();
                            reservation_user.put("date", mtxtDate.getText().toString().trim());
                            reservation_user.put("time", mtxtTime.getText().toString().trim());
                            reservation_user.put("table_x",table_x);
                            reservation_user.put("table_y",table_y);
                            reservation_user.put("table_places",table_places);
                            reservation_user.put("deposit",deposit*table_places);
                            db.collection("users")
                                    .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                                    .collection("reservations")
                                    .document(resto_id)
                                    .set(reservation_user);
                            make_toast("Reservation done");
                            startActivity(new Intent(getApplicationContext(),Home.class));
                            finish();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        }else if(canReserve ==0){
            make_toast("You should choose a table first!");
        }else{
            make_toast("You should choose an empty table!");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void load_plan(){
        if(this.x>0) {
            try{
                lin_lay.removeView(circleView);
            }catch(Exception e){

            }
        }
        this.already_reserved_tables = new ArrayList<>(16);
        this.tables = new ArrayList<>(16);
        Button b = (Button) findViewById(R.id.button_reserve);
        b.setVisibility(View.VISIBLE);
        a = (ImageView)findViewById(R.id.imageView_plan);
        a.setBackground(null);
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
                            ImageView a = (ImageView)findViewById(R.id.imageView_plan);
                            Picasso.with(getApplicationContext()).load(task.getResult().getData().get("plan").toString()).into(a);

                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
        //get all reserveed tables
        db.collection("country")
                .document("France")
                .collection("postal")
                .document("06000")
                .collection("restaurants")
                .document(resto_id)
                .collection("reservations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;
                            String d = mtxtDate.getText().toString().trim();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(d.trim().equals(document.getData().get("date").toString())){
                                    int t = Integer.parseInt(document.getData().get("time").toString().split(":")[0]);
                                    int t_user = Integer.parseInt(mtxtTime.getText().toString().trim().split(":")[0]);
                                    if(t == t_user) {
                                        String[] a = new String[3];
                                        a[0] = Integer.toString(i);
                                        i++;
                                        a[1] = document.getData().get("table_x").toString();
                                        a[2] = document.getData().get("table_y").toString();
                                        already_reserved_tables.add(a);
                                    }
                                }
                            }
                            save_already_reserved_tables(already_reserved_tables);
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });

        a.getLocationOnScreen(viewCoords);
        a.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == 0){
                    int touchX = (int) event.getX();
                    int touchY = (int) event.getY();
                    int imageX = touchX - viewCoords[0]; // viewCoords[0] is the X coordinate
                    int imageY = touchY - viewCoords[1];
                    Log.d("ADIBZ",imageX+"|"+imageY);
                    Iterator i = tables.iterator();
                    while(i.hasNext()){
                     String [] tmp = (String [])i.next();
                     // Check if clicked on a table
                     if((imageX>Integer.parseInt(tmp[2])-50 && imageX<Integer.parseInt(tmp[2])+50)&&((imageY>Integer.parseInt(tmp[3])-50 && imageY<Integer.parseInt(tmp[3])+50))){
                        //check if reserved or not
                         int reserved = 0;
                         Iterator j = already_reserved_tables.iterator();
                         while(j.hasNext()) {
                             String[] tmp2 = (String[]) j.next();
                             if(String.valueOf(tmp[2]).trim().equals(String.valueOf(tmp2[1]).trim()) && String.valueOf(tmp[3]).trim().equals(String.valueOf(tmp2[2]).trim())){
                                 reserved = 1;
                             }
                         }
                         if(reserved == 0){
                             addCircle(Integer.parseInt(tmp[2]), Integer.parseInt(tmp[3]), Integer.parseInt(tmp[1]),R.color.green);
                             setCanReserve(1);
                         }
                         else{
                             addCircle(Integer.parseInt(tmp[2]), Integer.parseInt(tmp[3]), Integer.parseInt(tmp[1]),R.color.red);
                             make_toast("This table is reserved");
                             setCanReserve(2);
                         }
                    }
                    }
                    return true;
                }
                return false;
            }
        });

        //load from db tables
        db.collection("country")
                .document("France")
                .collection("postal")
                .document("06000")
                .collection("restaurants")
                .document(resto_id)
                .collection("tables")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("MARIA", "" + document.getData().get("places").toString()+"-"+document.getData().get("x").toString()+"-"+document.getData().get("y").toString());
                                String [] a =new String[4];
                                a[0] = Integer.toString(i);i++;
                                a[1] = document.getData().get("places").toString();
                                a[2] = document.getData().get("x").toString();
                                a[3] = document.getData().get("y").toString();
                                tables.add(a);
                                Log.d("Mimi", "DEBUG TABLE PLACES" + tables.get(i-1)[1]+"");
                            }
                            save_tables(tables);
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    public void save_tables(ArrayList<String[]> t){
        this.tables = t;
    }
    public void save_already_reserved_tables(ArrayList<String[]> t){
        this.already_reserved_tables = t;
    }
    public void setCanReserve(int b){
        this.canReserve = b;
    }
    private void addCircle(int x, int y, int places, int color) {
        if(this.x>0) {
            lin_lay.removeView(circleView);
        }
        table_x = x;
        table_y = y;
        table_places = places;
        circleView = new CircleView(this, color);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(30, 30);
        params.leftMargin = x;
        params.topMargin = y-100;
        this.x +=1;
        lin_lay.addView(circleView, params);
    }
    public void proceed(View view){
        String txtDate = mtxtDate.getText().toString().trim();
        String txtTime = mtxtTime.getText().toString().trim();
        if(TextUtils.isEmpty(txtDate)){
            mtxtDate.setError("Date is Required");
            return;
        }
        if(TextUtils.isEmpty(txtTime)){
            mtxtTime.setError("Time is Required");
            return;
        }
        mtxtTime.setError(null);
        mtxtDate.setError(null);
        load_plan();
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
    @Override
    public void onClick(View v) {

        if (v == btnDatePicker) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            mtxtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == btnTimePicker) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            mtxtTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }
    public void make_toast(String text){
        Context context = getApplicationContext();
        CharSequence t = text;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, t, duration);
        toast.show();
    }
}