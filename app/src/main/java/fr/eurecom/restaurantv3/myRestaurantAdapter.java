package fr.eurecom.restaurantv3;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class myRestaurantAdapter extends ArrayAdapter<Restaurant> {
    public myRestaurantAdapter(@NonNull Context context, ArrayList<Restaurant> r) {
        super(context, 0, r);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //Log.d("Maria",getItem(position).toString());
        Restaurant r = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_restaurant,parent,false);
        }
        TextView txtname = convertView.findViewById(R.id.restaurant_name);
        txtname.setText(r.name);
        txtname = convertView.findViewById(R.id.text_rating);
        txtname.setText("Rating: "+r.rating+" / 5");
        txtname = convertView.findViewById(R.id.text_opening);
        txtname.setText("Opening: "+r.opening_time+":00 - " +r.closing_time+":00");
        txtname = convertView.findViewById(R.id.text_price);
        txtname.setText("Price: "+r.price+" $");
        txtname = convertView.findViewById(R.id.text_type);
        txtname.setText("Type: "+r.type+"");
        txtname = convertView.findViewById(R.id.text_indoor_outdoor);
        String tmp ="";
        if(r.parking =="true") tmp+= "Parking | ";
        if(r.indoor =="true") tmp+= "Indoor | ";
        if(r.outdoor =="true") tmp+= "Outdoor | ";
        txtname.setText("environment: "+tmp);
        ImageView a = convertView.findViewById(R.id.imageView_logo_list);
        Picasso.with(getContext()).load(r.logo).into(a);
        LinearLayout l = (LinearLayout)convertView.findViewById(R.id.row_id);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RestaurantPage.class);
                intent.putExtra("resto_id", r.id);
                intent.putExtra("resto_name", r.name);
                v.getContext().startActivity(intent);
            }

        });
        return convertView;
    }
}
