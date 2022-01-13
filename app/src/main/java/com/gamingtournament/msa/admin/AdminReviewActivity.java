package com.gamingtournament.msa.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gamingtournament.msa.AllParticipantActivity;
import com.gamingtournament.msa.MainActivity;
import com.gamingtournament.msa.R;
import com.gamingtournament.msa.adapter.AllParticipantAdapter;
import com.gamingtournament.msa.adapter.ReviewAdapter;
import com.gamingtournament.msa.model.AllParticipantsModel;
import com.gamingtournament.msa.model.ReviewModel;
import com.gamingtournament.msa.model.UserInformation;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminReviewActivity extends AppCompatActivity {

    private DatabaseReference reference;

    private LinearLayoutManager mlinearLayoutManager;
    private List<ReviewModel> item_list;
    private ReviewAdapter mAdapter;
    private RecyclerView rv_review;

    private CircleImageView iv_adminImg;
    private TextView tv_adminName, tv_adminEmail;
    private TextView tv_adminAvgRating, tv_adminTotalRating;
    private LinearLayout ll_rating;
    private RatingBar ratingBar, rb_adminRated;
    private EditText et_review;
    private Button btn_submit;

    private float ft_currRating, ft_netRating;
    private String adminUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_review);

        iv_adminImg = findViewById(R.id.iv_adminImg);
        tv_adminName = findViewById(R.id.tv_adminName);
        tv_adminEmail = findViewById(R.id.tv_adminEmail);
        tv_adminAvgRating = findViewById(R.id.tv_adminAvgRating);
        tv_adminTotalRating = findViewById(R.id.tv_adminTotalRating);
        ll_rating = findViewById(R.id.ll_rating);
        ratingBar = findViewById(R.id.ratingBar);
        rb_adminRated = findViewById(R.id.rb_adminRated);
        et_review = findViewById(R.id.et_review);
        btn_submit = findViewById(R.id.btn_submit);

        rv_review = findViewById(R.id.rv_review);
        item_list = new ArrayList<ReviewModel>();
        mlinearLayoutManager = new LinearLayoutManager(this);
        rv_review.setLayoutManager(mlinearLayoutManager);

        toolBar();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        Intent i = getIntent();
        adminUid = i.getStringExtra("adminUid");

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                ft_currRating = rating;

            }
        });

        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("000admin").child(adminUid);
        adminRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String disName = snapshot.child("displayname").getValue().toString();
                tv_adminName.setText(disName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference adminEmailRef = FirebaseDatabase.getInstance().getReference().child("users").child(adminUid);
        adminEmailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserInformation information = new UserInformation();
                information.setEmail(snapshot.getValue(UserInformation.class).getEmail());
                information.setImgurl(snapshot.getValue(UserInformation.class).getImgurl());

                tv_adminEmail.setText(information.getEmail());

                String st_imageUrl = information.getImgurl();
                if (st_imageUrl != null && !st_imageUrl.equals("")) {
                    Picasso.get()
                            .load(st_imageUrl)
                            .placeholder(R.drawable.logo)
                            .error(R.drawable.logo)
                            .into(iv_adminImg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        assert adminUid!=null;
        reference = FirebaseDatabase.getInstance().getReference().child("000admin").child(adminUid).child("000rating");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                item_list.clear();
                int netRating = 0;

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ReviewModel p = dataSnapshot1.getValue(ReviewModel.class);
                    assert p != null;
                    int totalRating = (int) p.getRating();
                    netRating = netRating + totalRating;
                    item_list.add(p);
                }

                Collections.sort(item_list, new Comparator<ReviewModel>() {
                    @Override
                    public int compare(ReviewModel o1, ReviewModel o2) {
                        return o1.getTimestamp().compareTo(o2.getTimestamp());
                    }
                });

                Collections.reverse(item_list);

                mAdapter = new ReviewAdapter(AdminReviewActivity.this, item_list,adminUid);
                rv_review.setAdapter(mAdapter);

                tv_adminTotalRating.setText("("+mAdapter.getItemCount()+")");

                if (mAdapter.getItemCount()!=0){
                    float finalRating = (float) netRating/mAdapter.getItemCount();
                    DecimalFormat df = new DecimalFormat("#.##");
                    float decimalrating = Float.parseFloat(df.format(finalRating));

                    rb_adminRated.setRating(decimalrating);
                    tv_adminAvgRating.setText(String.valueOf(decimalrating));
                }else {
                    rb_adminRated.setRating(Float.parseFloat("0"));
                    tv_adminAvgRating.setText("0.0");
                }

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminReviewActivity.this, "Wrong "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String st_review = et_review.getText().toString();

                if (ft_currRating == 0){
                    Toast.makeText(AdminReviewActivity.this, "Rate first", Toast.LENGTH_LONG).show();
                }else if (st_review.isEmpty()){
                    et_review.setError("Mandatory");
                    et_review.requestFocus();
                } else {

                    if (adminUid!=null){
                        String st_currtime = String.valueOf(System.currentTimeMillis() / 1000);
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        ReviewModel rm = new ReviewModel(st_currtime,st_review,uid,ft_currRating);

                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("000admin").child(adminUid).child("000rating").child(uid);
                        db.setValue(rm);

                        mAdapter.notifyDataSetChanged();
                        et_review.setText("");
                        ratingBar.setRating(Float.parseFloat("0"));
                        Toast.makeText(AdminReviewActivity.this, "Thank You for Review", Toast.LENGTH_LONG).show();


                    }

                }

            }
        });

    }


    private void toolBar() {
        // for title
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminReviewActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
                finish();
            }
        });

    }

}