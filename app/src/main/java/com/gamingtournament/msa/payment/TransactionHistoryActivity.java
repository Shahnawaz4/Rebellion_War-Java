package com.gamingtournament.msa.payment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.gamingtournament.msa.FreeFireActivity;
import com.gamingtournament.msa.MainActivity;
import com.gamingtournament.msa.PubgActivity;
import com.gamingtournament.msa.R;
import com.gamingtournament.msa.adapter.GameAdapter;
import com.gamingtournament.msa.adapter.TransactionAdapter;
import com.gamingtournament.msa.model.AllParticipantsModel;
import com.gamingtournament.msa.model.GameModel;
import com.gamingtournament.msa.model.TransactionModel;
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

public class TransactionHistoryActivity extends AppCompatActivity {

    private LinearLayoutManager mlinearLayoutManager;
    private List<TransactionModel> item_list;
    private TransactionAdapter mAdapter;
    private RecyclerView rv_transaction;
    private SwipeRefreshLayout refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        rv_transaction = findViewById(R.id.rv_transaction);
        refresh = findViewById(R.id.refresh);
        item_list = new ArrayList<TransactionModel>();
        mlinearLayoutManager = new LinearLayoutManager(this);
        rv_transaction.setLayoutManager(mlinearLayoutManager);

        // for title
        toolBar();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference  reference = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("000transaction");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                item_list.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    TransactionModel p = dataSnapshot1.getValue(TransactionModel.class);
                    item_list.add(p);
                }

                Collections.sort(item_list, new Comparator<TransactionModel>() {
                    @Override
                    public int compare(TransactionModel o1, TransactionModel o2) {
                        return o1.getId().compareTo(o2.getId());
                    }
                });

                Collections.reverse(item_list);

                mAdapter = new TransactionAdapter(TransactionHistoryActivity.this, item_list);
                rv_transaction.setAdapter(mAdapter);
                // check recyclerview is empty or not if empty show dialog
                if (mAdapter.getItemCount() == 0) {
                    Toast.makeText(TransactionHistoryActivity.this, "No History ", Toast.LENGTH_SHORT).show();
                }


                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TransactionHistoryActivity.this, "Error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void toolBar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TransactionHistoryActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
                finish();
            }
        });
    }
}