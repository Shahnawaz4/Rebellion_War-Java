package com.gamingtournament.msa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gamingtournament.msa.adapter.GameAdapter;
import com.gamingtournament.msa.model.GameModel;
import com.gamingtournament.msa.more.PrivacyPolicyActivity;
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

public class ClashofClansActivity extends AppCompatActivity {

    private DatabaseReference reference;

    private LinearLayoutManager mlinearLayoutManager;
    private List<GameModel> item_list;
    private GameAdapter mGameAdapter;
    private RecyclerView rv_freefire;
    private SwipeRefreshLayout refresh;
    private TextView tv_noContest;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clashof_clans);

        tv_noContest = findViewById(R.id.tv_noContest);
        rv_freefire = findViewById(R.id.rv_freefire);
        refresh = findViewById(R.id.refresh);
        item_list = new ArrayList<GameModel>();
        mlinearLayoutManager = new LinearLayoutManager(this);
        rv_freefire.setLayoutManager(mlinearLayoutManager);

        dialog = new Dialog(ClashofClansActivity.this, R.style.AlertDialogTheme);
        dialog.setContentView(R.layout.pb_custom_layout);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow()!=null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();

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
                startActivity(new Intent(ClashofClansActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
                finish();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference().child("000home").child("clashofclan");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                item_list.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    GameModel p = dataSnapshot1.getValue(GameModel.class);
                    item_list.add(p);
                }

                Collections.sort(item_list, new Comparator<GameModel>() {
                    @Override
                    public int compare(GameModel o1, GameModel o2) {
                        return o1.getRefid().compareTo(o2.getRefid());
                    }
                });

                Collections.reverse(item_list);

                mGameAdapter = new GameAdapter(ClashofClansActivity.this, item_list);
                rv_freefire.setAdapter(mGameAdapter);

                // check recyclerview is empty or not if empty show dialog
                if (mGameAdapter.getItemCount() == 0) {
                    tv_noContest.setVisibility(View.VISIBLE);
                }

                mGameAdapter.notifyDataSetChanged();
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
                Toast.makeText(ClashofClansActivity.this, "Error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
}
