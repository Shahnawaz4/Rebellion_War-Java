package com.gamingtournament.msa.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.gamingtournament.msa.MainActivity;
import com.gamingtournament.msa.R;
import com.gamingtournament.msa.adapter.TopPlayersAdapter;
import com.gamingtournament.msa.model.TopPlayersModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TopPlayersActivity extends AppCompatActivity {

    private LinearLayoutManager mlinearLayoutManager;
    private List<TopPlayersModel> item_list;
    private TopPlayersAdapter mAdapter;
    private RecyclerView rv_topPlayers;
    private SwipeRefreshLayout refresh;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_players);

        rv_topPlayers = findViewById(R.id.rv_topPlayers);
        refresh = findViewById(R.id.refresh);
        item_list = new ArrayList<TopPlayersModel>();
        mlinearLayoutManager = new LinearLayoutManager(this);
        rv_topPlayers.setLayoutManager(mlinearLayoutManager);

        dialog = new Dialog(TopPlayersActivity.this);
        dialog.setContentView(R.layout.pb_custom_layout);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow()!=null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();

        gettitle();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                item_list.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    TopPlayersModel p = dataSnapshot1.getValue(TopPlayersModel.class);
                    item_list.add(p);
                }

                Collections.sort(item_list, new Comparator<TopPlayersModel>() {
                    @Override
                    public int compare(TopPlayersModel o1, TopPlayersModel o2) {

                        return Integer.compare(o1.getTotalkill(),o2.getTotalkill());

                    }
                });

                Collections.reverse(item_list);

                mAdapter = new TopPlayersAdapter(TopPlayersActivity.this, item_list);
                rv_topPlayers.setAdapter(mAdapter);

                // check recyclerview is empty or not if empty show dialog
                if (mAdapter.getItemCount() == 0) {

                    Toast.makeText(TopPlayersActivity.this, "No Data", Toast.LENGTH_SHORT).show();
                }
                mAdapter.notifyDataSetChanged();
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
                Toast.makeText(TopPlayersActivity.this, "Error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startActivity(getIntent());
                finish();
                refresh.setRefreshing(false);
            }
        });

    }

    private void gettitle(){
        // for title
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TopPlayersActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
                finish();
            }
        });
    }

}