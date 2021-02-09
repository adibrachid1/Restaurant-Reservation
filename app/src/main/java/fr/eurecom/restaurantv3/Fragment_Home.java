package fr.eurecom.restaurantv3;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class  Fragment_Home extends Fragment {
    ImageView images [];
    ViewFlipper v_flipper;
    List<String> user_preferences = new ArrayList<>();
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        images = new ImageView[4];
        Log.d("VIEW","ADIB");
        return inflater.inflate(R.layout.fragment_home,container,false);
    }
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String country = "France";
        String code_postal = "06000";
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Slider images
        db.collection("country")
                .document(country)
                .collection("postal")
                .document(code_postal)
                .collection("offers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int i=0;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                images[i] = new ImageView(getActivity().getApplicationContext());
                                Picasso.with(getContext()).load(document.getData().get("URL").toString()).into(images[i]);
                                i++;
                            }
                            v_flipper = view.findViewById(R.id.v_flipper);
                            i=0;
                            for (ImageView image:images) {
                                flipperImages(image);
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });

        //get preferences

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
                            if(map!=null) {
                                Iterator<String> iterator = map.keySet().iterator();
                                while (iterator.hasNext()) {
                                    String key = iterator.next();
                                    if (map.get(key).toString().equals("1")) {
                                        user_preferences.add(key);
                                    }
                                }
                            }
                            //get restaurants
                            final ArrayList<Restaurant> restaurant_list = new ArrayList<>();
                            final myRestaurantAdapter adapter = new myRestaurantAdapter(getContext(),restaurant_list);
                            ListView listView = view.findViewById(R.id.restaurant_list);
                            listView = view.findViewById(R.id.restaurant_list);
                            listView.setAdapter(adapter);
                            restaurant_list.clear(); adapter.clear();
                            //get resto from db
                            db.collection("country")
                                    .document(country)
                                    .collection("postal")
                                    .document(code_postal)
                                    .collection("restaurants")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            int i=0;
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Restaurant r = new Restaurant(
                                                            document.getId(),
                                                            document.getData().get("name").toString(),
                                                            document.getData().get("rating").toString(),
                                                            document.getData().get("opening_time").toString(),
                                                            document.getData().get("closing_time").toString(),
                                                            document.getData().get("type").toString(),
                                                            document.getData().get("price").toString(),
                                                            document.getData().get("logo_URL").toString(),
                                                            document.getData().get("menu").toString(),
                                                            document.getData().get("parking").toString(),
                                                            document.getData().get("outdoor").toString(),
                                                            document.getData().get("indoor").toString());
                                                    //check preferences
                                                    Log.d("RANDAA",user_preferences+"");
                                                    //check normal preferences
                                                    if(user_preferences.contains(document.getData().get("type").toString())){
                                                        //check parking
                                                        if(!user_preferences.contains("parking") ||(user_preferences.contains("parking") && document.getData().get("parking").toString()=="true")) {
                                                            //check indoor and outdoor
                                                            if(!user_preferences.contains("indoor") ||(user_preferences.contains("indoor") && document.getData().get("indoor").toString()=="true")) {
                                                                if(!user_preferences.contains("outdoor") ||(user_preferences.contains("outdoor") && document.getData().get("outdoor").toString()=="true")) {
                                                                    restaurant_list.add(r);
                                                                    adapter.notifyDataSetChanged();
                                                                }
                                                            }
                                                        }
                                                    }
                                                    i++;
                                                }
                                            } else {
                                                Log.w("TAG", "Error getting documents.", task.getException());
                                            }
                                        }
                                    });
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });


    }
    public void flipperImages(ImageView image){
        v_flipper.addView(image);
        v_flipper.setFlipInterval(4000);
        v_flipper.startFlipping();
        v_flipper.setAutoStart(true);
        //animation
        v_flipper.setInAnimation(getActivity().getApplicationContext(),android.R.anim.slide_in_left);
        v_flipper.setOutAnimation(getActivity().getApplicationContext(),android.R.anim.slide_out_right);
    }
}
