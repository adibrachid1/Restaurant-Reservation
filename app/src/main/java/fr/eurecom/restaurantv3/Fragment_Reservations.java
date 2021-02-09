package fr.eurecom.restaurantv3;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Fragment_Reservations extends Fragment {
    ArrayList<Reservation_class> reservation_list;
    myReservationAdapter adapter;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_reservations,container,false);
    }
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        //get reservations
        reservation_list = new ArrayList<>();
        adapter = new myReservationAdapter(getContext(),reservation_list);
        ListView listView = view.findViewById(R.id.reservations_list);
        listView.setAdapter(adapter);
        reservation_list.clear();
        adapter.clear();
        //get resto from db
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection("reservations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String resto_id;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                resto_id = document.getId();

                                String finalResto_id = resto_id;
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
                                                    String resto_name = task.getResult().getData().get("name").toString();
                                                    String resto_image = task.getResult().getData().get("logo_URL").toString();
                                                    //String date, String deposit, String table_places, String table_x, String table_y, String time, String resto_id, String resto_name
                                                    Reservation_class r = new Reservation_class(
                                                            document.getData().get("date").toString(),
                                                            document.getData().get("deposit").toString(),
                                                            document.getData().get("table_places").toString(),
                                                            document.getData().get("table_x").toString(),
                                                            document.getData().get("table_y").toString(),
                                                            document.getData().get("time").toString(),
                                                            finalResto_id,
                                                            resto_image,
                                                            resto_name
                                                    );
                                                    reservation_list.add(r);
                                                    adapter.notifyDataSetChanged();
                                                }}});
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.ADIBB", task.getException());
                        }
                    }
                });

    }
}
