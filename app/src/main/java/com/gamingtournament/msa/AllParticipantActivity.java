package com.gamingtournament.msa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gamingtournament.msa.adapter.AllParticipantAdapter;
import com.gamingtournament.msa.model.AllParticipantsModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AllParticipantActivity extends AppCompatActivity {

    private DatabaseReference reference;

    private LinearLayoutManager mlinearLayoutManager;
    private List<AllParticipantsModel> item_list;
    private AllParticipantAdapter mAllParticipantAdapter;
    private RecyclerView rv_allparticipant;

    private TextView tv_by, tv_gameHeader, tv_date, tv_time, tv_map, tv_winner, tv_perkill, tv_entryfee;
    private TextView tv_noOneJoined;

    private String st_by,st_header,st_game, st_gametype,st_refId, st_date, st_time, st_map,st_youtubeUrl;
    private int winner,perkill,entryfee;
    private boolean isGameCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_participant);


        tv_by = findViewById(R.id.tv_by);
        tv_gameHeader = findViewById(R.id.tv_gameHeader);
        tv_date = findViewById(R.id.tv_date);
        tv_time = findViewById(R.id.tv_time);
        tv_map = findViewById(R.id.tv_map);
        tv_winner = findViewById(R.id.tv_winner);
        tv_perkill = findViewById(R.id.tv_perkill);
        tv_entryfee = findViewById(R.id.tv_entryfee);
        tv_noOneJoined = findViewById(R.id.tv_noOneJoined);

        rv_allparticipant = findViewById(R.id.rc_allparticipant);
        item_list = new ArrayList<AllParticipantsModel>();
        mlinearLayoutManager = new LinearLayoutManager(this);
        rv_allparticipant.setLayoutManager(mlinearLayoutManager);

        toolBar();

        Intent i = getIntent();
        st_by = i.getStringExtra("by");
        st_youtubeUrl = i.getStringExtra("youtubeUrl");
        st_header = i.getStringExtra("header");
        st_game = i.getStringExtra("game");
        st_gametype = i.getStringExtra("gametype");
        st_refId = i.getStringExtra("refId");
        st_date = i.getStringExtra("date");
        st_time = i.getStringExtra("time");
        st_map = i.getStringExtra("map");

        isGameCompleted = i.getBooleanExtra("isGameCompleted", false);

        winner = i.getIntExtra("winner", 0);
        perkill = i.getIntExtra("perkill", 0);
        entryfee= i.getIntExtra("entryfee", 0);

        tv_by.setText("By: "+st_by);
        tv_gameHeader.setText(st_header);
        tv_date.setText(st_date);
        tv_time.setText(st_time);
        tv_map.setText(st_map);

        tv_winner.setText("₹ "+winner);
        tv_perkill.setText("₹ "+perkill);

        int finalEntryFee = 0;
        switch (st_gametype){
            case "solo":
                tv_entryfee.setText("₹" + entryfee);
                break;
            case "duo":
                finalEntryFee = entryfee/2;
                tv_entryfee.setText("₹"+finalEntryFee+" x 2");
                break;
            case "squad":
                finalEntryFee = entryfee/4;
                tv_entryfee.setText("₹"+finalEntryFee+" x 4");
                break;
            default:
                tv_entryfee.setText("₹ " + entryfee);
                break;
        }

        reference = FirebaseDatabase.getInstance().getReference().child("000home").child(st_game).child(st_refId).child("000userjoined");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                item_list.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    AllParticipantsModel p = dataSnapshot1.getValue(AllParticipantsModel.class);
                    item_list.add(p);
                }

                Collections.sort(item_list, new Comparator<AllParticipantsModel>() {
                    @Override
                    public int compare(AllParticipantsModel o1, AllParticipantsModel o2) {
                        return o1.getTeamno().compareTo(o2.getTeamno());
                    }
                });

                mAllParticipantAdapter = new AllParticipantAdapter(AllParticipantActivity.this, item_list,st_game, st_gametype,st_refId,perkill,winner,isGameCompleted);
                rv_allparticipant.setAdapter(mAllParticipantAdapter);

                // check recyclerview is empty or not if empty show dialog
                if (mAllParticipantAdapter.getItemCount() == 0) {
                    tv_noOneJoined.setVisibility(View.VISIBLE);
                }

                mAllParticipantAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AllParticipantActivity.this, "Wrong "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(AllParticipantActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu_youtube,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.menu_youtube) {

            if (st_youtubeUrl!=null && !st_youtubeUrl.equals("")){

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(st_youtubeUrl));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.google.android.youtube");
                startActivity(intent);

            }

        }

        return super.onOptionsItemSelected(item);
    }

}