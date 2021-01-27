package fr.eurecom.restaurantv3;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.net.URL;

public class  Fragment_Home extends Fragment {
    ImageView images [];
    ViewFlipper v_flipper;
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
