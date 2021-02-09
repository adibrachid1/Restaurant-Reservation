package fr.eurecom.restaurantv3;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;

public class myReservationAdapter extends ArrayAdapter<Reservation_class> {
public myReservationAdapter(@NonNull Context context, ArrayList<Reservation_class> r) {
        super(context, 0, r);
        }
@Override
public View getView(int position, View convertView, ViewGroup parent){
        Log.d("Maria",getItem(position).toString());
        Reservation_class r = getItem(position);
        if(convertView == null){
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_reservation,parent,false);
        }
        TextView txtname = convertView.findViewById(R.id.reservation_restaurant_name);
        txtname.setText(r.resto_name);
        txtname = convertView.findViewById(R.id.reservation_date);
        txtname.setText("Date: "+r.date);
        txtname = convertView.findViewById(R.id.reservation_time);
        txtname.setText("Time: "+r.time);
        txtname = convertView.findViewById(R.id.reservation_deposit);
        txtname.setText("Deposit: "+r.deposit+" €");
        ImageView a = convertView.findViewById(R.id.reservation_image_resto);
        Picasso.with(getContext()).load(r.resto_image).into(a);
        txtname = convertView.findViewById(R.id.reservation_cancel);
        txtname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cancel reservation
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("country")
                        .document("France")
                        .collection("postal")
                        .document("06000")
                        .collection("restaurants")
                        .document(r.resto_id)
                        .collection("reservations")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                        .collection("menu_pre_order")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        document.getReference().delete();
                                    }
                                } else {
                                    Log.w("TAG", "Error getting documents.", task.getException());
                                }
                            }
                        });
                db.collection("country")
                        .document("France")
                        .collection("postal")
                        .document("06000")
                        .collection("restaurants")
                        .document(r.resto_id)
                        .collection("reservations")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                        .delete();

                db.collection("users")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                        .collection("reservations")
                        .document(r.resto_id)
                        .collection("menu_pre_order")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        document.getReference().delete();
                                    }
                                } else {
                                    Log.w("TAG", "Error getting documents.", task.getException());
                                }
                            }
                        });
                db.collection("users")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                        .collection("reservations")
                        .document(r.resto_id)
                        .delete();
                make_toast("Reservation cancelled");
                v.getContext().startActivity(new Intent(v.getContext(),Home.class));
            }
        });
        /*LinearLayout l = (LinearLayout)convertView.findViewById(R.id.row_id);
        l.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), RestaurantPage.class);
        intent.putExtra("resto_id", r.id);
        intent.putExtra("resto_name", r.name);
        v.getContext().startActivity(intent);
        }*/


return convertView;
}
    public void make_toast(String text){
        Context context = getContext();
        CharSequence t = text;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, t, duration);
        toast.show();
    }
}
