package com.gamingtournament.msa.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.gamingtournament.msa.AllParticipantActivity;
import com.gamingtournament.msa.R;
import com.gamingtournament.msa.admin.AdminReviewActivity;
import com.gamingtournament.msa.model.GameModel;
import com.gamingtournament.msa.more.PrivacyPolicyActivity;
import com.gamingtournament.msa.more.RulesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MyContestAdapter extends RecyclerView.Adapter<MyContestAdapter.modelviewHolder> {

    Context mContext;
    List<GameModel> itemlist;
    String by;

    public MyContestAdapter(Context mContext, List<GameModel> itemlist) {
        this.mContext = mContext;
        this.itemlist = itemlist;
    }

    @NonNull
    @Override
    public MyContestAdapter.modelviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_mycontest,parent,false);
        return new modelviewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyContestAdapter.modelviewHolder holder, int position) {

        final int playerjoined = itemlist.get(position).getPlayerjoined();
        final int playertotal = itemlist.get(position).getTotalplayer();
        final int playerrem = playertotal - playerjoined;
        final int winner = itemlist.get(position).getWinner();
        final int perkill = itemlist.get(position).getPerkill();
        final int entryfee = itemlist.get(position).getEntryfee();

        final String byUid = itemlist.get(position).getBy();
        final String youtubeUrl = itemlist.get(position).getYoutubeurl();
        final boolean isGameCompleted = itemlist.get(position).isIscompleted();
        final String refId = itemlist.get(position).getRefid();
        final String game = itemlist.get(position).getGame();
        final String gametype = itemlist.get(position).getGametype();
        final String header = itemlist.get(position).getHeader();
        final String wish = itemlist.get(position).getWish();
        final String date = itemlist.get(position).getDate();
        final String time = itemlist.get(position).getTime();
        final String map = itemlist.get(position).getMap();
        final String persp = itemlist.get(position).getPersp();
        final String roomId = itemlist.get(position).getRoomid();
        final String roomPass = itemlist.get(position).getRoompass();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("000home")
                .child(game).child(refId).child("000userjoined").child(uid).child("teamuid");
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    holder.cv_myContest.setVisibility(View.GONE);
                } else {
                    holder.tv_heading.setText(header);
                    holder.tv_refId.setText("Ref Id: #"+refId);
                    holder.tv_wish.setText(wish);
                    holder.tv_date.setText(date);
                    holder.tv_time.setText(time);
                    holder.tv_map.setText(map);
                    holder.tv_pers.setText(persp);

                    holder.tv_winner.setText("₹ " + winner);
                    holder.tv_perkill.setText("₹ " + perkill);

                    DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("000admin").child(byUid).child("displayname");

                    db.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()){
                                String displayName = snapshot.getValue().toString();
                                holder.tv_by.setText("By: "+displayName);
                                holder.tv_by.setPaintFlags(holder.tv_by.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                by = displayName;
                            } else {
                                holder.tv_by.setText("By: Unknown");
                                by = "Unknown";
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    int finalEntryFee = 0;

                    switch (gametype){
                        case "solo":
                            holder.tv_entryfee.setText("₹" + entryfee);
                            break;
                        case "duo":
                            finalEntryFee = entryfee/2;
                            holder.tv_entryfee.setText("₹"+finalEntryFee+" x 2");
                            break;
                        case "squad":
                            finalEntryFee = entryfee/4;
                            holder.tv_entryfee.setText("₹"+finalEntryFee+" x 4");
                            break;
                        default:
                            holder.tv_entryfee.setText("₹ " + entryfee);
                            break;
                    }

                    if (roomId!=null && !roomId.equals("")){
                        holder.tv_roomId.setText(roomId);
                    } else {
                        holder.tv_roomId.setText("-");
                    }

                    if (roomPass != null && !roomPass.equals("")){
                        holder.tv_roomPass.setText(roomPass);
                    } else {
                        holder.tv_roomPass.setText("-");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.tv_by.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, AdminReviewActivity.class);
                i.putExtra("adminUid", byUid);
                mContext.startActivity(i);
            }
        });

        holder.cv_myContest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, AllParticipantActivity.class);
                i.putExtra("byuid", byUid);
                i.putExtra("by", by);
                i.putExtra("youtubeUrl", youtubeUrl);
                i.putExtra("isGameCompleted", isGameCompleted);
                i.putExtra("header", header);
                i.putExtra("game", game);
                i.putExtra("gametype", gametype);
                i.putExtra("refId", refId);
                i.putExtra("date", date);
                i.putExtra("time", time);
                i.putExtra("map", map);
                i.putExtra("winner", winner);
                i.putExtra("perkill", perkill);
                i.putExtra("entryfee", entryfee);
                mContext.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    public class modelviewHolder extends RecyclerView.ViewHolder{

        CardView cv_myContest;
        TextView tv_heading;
        TextView tv_refId;
        TextView tv_by;
        TextView tv_wish;
        TextView myrule;
        TextView tv_date;
        TextView tv_time;
        TextView tv_map;
        TextView tv_pers;
        TextView tv_winner;
        TextView tv_perkill;
        TextView tv_entryfee;
        TextView tv_roomId;
        TextView tv_roomPass;


        public modelviewHolder(@NonNull View itemView) {
            super(itemView);

            cv_myContest =(CardView) itemView.findViewById(R.id.cv_myContest);
            tv_heading =(TextView) itemView.findViewById(R.id.tv_heading);
            tv_refId = (TextView) itemView.findViewById(R.id.tv_refId);
            tv_by =(TextView) itemView.findViewById(R.id.tv_by);
            tv_wish = (TextView) itemView.findViewById(R.id.tv_wish);
            myrule = (TextView) itemView.findViewById(R.id.tv_rules);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_map = (TextView) itemView.findViewById(R.id.tv_map);
            tv_pers = (TextView) itemView.findViewById(R.id.tv_pers);
            tv_winner = (TextView) itemView.findViewById(R.id.tv_winner);
            tv_perkill = (TextView) itemView.findViewById(R.id.tv_perkill);
            tv_entryfee = (TextView) itemView.findViewById(R.id.tv_entryfee);
            tv_roomId = (TextView) itemView.findViewById(R.id.tv_roomId);
            tv_roomPass = (TextView) itemView.findViewById(R.id.tv_roomPass);

            myrule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    v.getContext().startActivity(new Intent(v.getContext(), RulesActivity.class));
                    Intent rules = new Intent(v.getContext(), PrivacyPolicyActivity.class);
                    rules.putExtra("data", 5);
                    v.getContext().startActivity(rules);
                }
            });


        }
    }
}
