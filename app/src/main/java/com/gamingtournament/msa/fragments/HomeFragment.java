package com.gamingtournament.msa.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.gamingtournament.msa.ClashofClansActivity;
import com.gamingtournament.msa.FreeFireActivity;
import com.gamingtournament.msa.PubgActivity;
import com.gamingtournament.msa.PubgLiteActivity;
import com.gamingtournament.msa.R;
import com.gamingtournament.msa.model.UserInformation;
import com.gamingtournament.msa.more.ContactActivity;
import com.gamingtournament.msa.utils.UtilValues;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    private ImageSlider imageSlider;
    private TextView tv_wish, tv_username;
    private TextView tv_pubg, tv_pubglite, tv_freefire, tv_clashofclan;
    private CardView cv_whatsappGroup, cv_share;

    private String uid;
    private DatabaseReference db_name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tv_wish = view.findViewById(R.id.tv_wish);
        imageSlider = view.findViewById(R.id.imageSlider);
        tv_username = view.findViewById(R.id.tv_username);
        tv_pubg = view.findViewById(R.id.tv_pubg);
        tv_pubglite = view.findViewById(R.id.tv_pubglite);
        tv_freefire = view.findViewById(R.id.tv_freefire);
        tv_clashofclan = view.findViewById(R.id.tv_clashofclan);
        cv_whatsappGroup = view.findViewById(R.id.cv_whatsappGroup);
        cv_share = view.findViewById(R.id.cv_share);

        wish(tv_wish);
        tvClicked();
        imageSlider();
        joinShare();

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db_name = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        db_name.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserInformation information=new UserInformation();
                information.setName(snapshot.getValue(UserInformation.class).getName());
                tv_username.setText(information.getName());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void joinShare() {

        cv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = "Interested in online games? Make some cash out of it, Try Rebellion War Official eSport platform." +
                        " Download and register to get 51 Bonus point and Rs 11 cash. Join daily matches & get reward on each kill you score and get huge prize on chicken dinner." +
                        "\n\n\n\nDownload Link: "+ UtilValues.WEB_URL;
                String sub = "Share App";

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_SUBJECT, sub);
                share.putExtra(Intent.EXTRA_TEXT, msg);
                getContext().startActivity(Intent.createChooser(share, "Share using"));

            }
        });

        cv_whatsappGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference db_url = FirebaseDatabase.getInstance().getReference().child("url");
                db_url.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String groupLink = dataSnapshot.child("walink").getValue().toString();

                        String url = "https://chat.whatsapp.com/"+groupLink;

                        PackageManager pm = getContext().getPackageManager();
                        Intent whatsapp = new Intent(Intent.ACTION_VIEW);
                        whatsapp.setPackage("com.whatsapp");
                        whatsapp.setData(Uri.parse(url));
                        if (whatsapp.resolveActivity(pm) != null){
                            startActivity(whatsapp);
                        } else {
                            Toast.makeText(getContext(), "Whatsapp Not Installed", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    private void imageSlider() {

        List<SlideModel> imagelist = new ArrayList<>();
        imagelist.add(new SlideModel(R.drawable.bgmi, ScaleTypes.FIT));
//        imagelist.add(new SlideModel("https://srcodex.000webhostapp.com/pubglite.jpg", ScaleTypes.FIT));
        imagelist.add(new SlideModel(R.drawable.pubglite, ScaleTypes.FIT));
        imagelist.add(new SlideModel(R.drawable.freefire, ScaleTypes.FIT));
        imagelist.add(new SlideModel(R.drawable.clashofclan, ScaleTypes.FIT));

        imageSlider.setImageList(imagelist,ScaleTypes.FIT);

        imageSlider.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemSelected(int i) {

                if (i == 0){
                    startActivity(new Intent(getContext(),PubgActivity.class));
                } else if (i == 1){
                    startActivity(new Intent(getContext(), PubgLiteActivity.class));
                } else if (i == 2){
                    startActivity(new Intent(getContext(), FreeFireActivity.class));
                } else {
                    startActivity(new Intent(getContext(),ClashofClansActivity.class));
                }

            }
        });

    }

    private void tvClicked() {

        tv_pubg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PubgActivity.class));
            }
        });

        tv_pubglite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PubgLiteActivity.class));
            }
        });

        tv_freefire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), FreeFireActivity.class));
            }
        });

        tv_clashofclan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ClashofClansActivity.class));
            }
        });

    }

    private void wish(TextView tv_wish){
        Calendar c = Calendar.getInstance();
        int now = c.get(Calendar.HOUR_OF_DAY);

        if (now < 12){
            tv_wish.setText("Good Morning");
        } else if (now < 16){
            tv_wish.setText("Good Afternoon");
        } else if (now < 22){
            tv_wish.setText("Good Evening");
        } else {
            tv_wish.setText("Good Night");
        }
    }

}