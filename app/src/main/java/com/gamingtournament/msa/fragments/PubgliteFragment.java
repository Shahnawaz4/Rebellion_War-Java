package com.gamingtournament.msa.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gamingtournament.msa.R;
import com.gamingtournament.msa.adapter.MyContestAdapter;
import com.gamingtournament.msa.model.GameModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PubgliteFragment extends Fragment {

    private DatabaseReference reference;

    private LinearLayoutManager mlinearLayoutManager;
    private List<GameModel> item_list;
    private MyContestAdapter mMyContestAdapter;
    private RecyclerView rv_freefire;
    private SwipeRefreshLayout refresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pubglite, container, false);

        rv_freefire = view.findViewById(R.id.rv_freefire);
        refresh = view.findViewById(R.id.refresh);
        item_list = new ArrayList<GameModel>();
        mlinearLayoutManager = new LinearLayoutManager(getContext());
        rv_freefire.setLayoutManager(mlinearLayoutManager);

        reference = FirebaseDatabase.getInstance().getReference().child("000home").child("pubglite");

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

                mMyContestAdapter = new MyContestAdapter(getContext(), item_list);
                rv_freefire.setAdapter(mMyContestAdapter);

                mMyContestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Wrong", Toast.LENGTH_SHORT).show();
            }
        });


        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                if (Build.VERSION.SDK_INT >= 26){
//                    ft.setReorderingAllowed(false)
//                }
//                ft.detach(get).attach(this).commit();
//                startActivity(getIntent());
//                finish();

                refresh.setRefreshing(false);
            }
        });


        return view;
    }
}