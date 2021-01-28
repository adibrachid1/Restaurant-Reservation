package fr.eurecom.restaurantv3;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Fragment_Search extends Fragment implements SearchView.OnQueryTextListener{
    SearchView editsearch;
    ArrayList<Restaurant> restaurant_list;
    myRestaurantAdapter adapter;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_search,container,false);
    }
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        // Locate the EditText in listview_main.xml
        editsearch = (SearchView) view.findViewById(R.id.searchView);
        editsearch.setOnQueryTextListener(this);

        //get restaurants
        restaurant_list = new ArrayList<>();
        adapter = new myRestaurantAdapter(getContext(),restaurant_list);
        ListView listView = view.findViewById(R.id.restaurant_list_search);
        listView.setAdapter(adapter);
        restaurant_list.clear(); adapter.clear();
        //get resto from db
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("country")
                .document("France")
                .collection("postal")
                .document("06000")
                .collection("restaurants")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("AHLENNN", "Error getti");
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
                                restaurant_list.add(r);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.ADIBB", task.getException());
                        }
                    }
                });

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        refresh_list(query);
        return false;
    }
    public void refresh_list(String query){
        restaurant_list.clear();
        adapter.clear();
        Log.d("ADIB","Maria "+ query );
        //get resto from db
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("country")
                .document("France")
                .collection("postal")
                .document("06000")
                .collection("restaurants")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("ADIB","Real name "+ document.getData().get("name").toString());
                                if(document.getData().get("name").toString().toLowerCase().matches(".*"+query.toLowerCase()+".*")){
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
                                    restaurant_list.add(r);
                                    adapter.notifyDataSetChanged();
                                    Log.d("ADIB","Maria "+ document.getData().get("name").toString());
                                }
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.ADIBB", task.getException());
                        }
                    }
                });
        editsearch.setQuery(null,false);
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
