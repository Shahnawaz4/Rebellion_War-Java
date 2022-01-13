package com.gamingtournament.msa.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.gamingtournament.msa.AllParticipantActivity;
import com.gamingtournament.msa.MainActivity;
import com.gamingtournament.msa.R;
import com.gamingtournament.msa.TeamNameActivity;
import com.gamingtournament.msa.admin.AdminReviewActivity;
import com.gamingtournament.msa.more.PrivacyPolicyActivity;
import com.gamingtournament.msa.more.RulesActivity;
import com.gamingtournament.msa.model.GameModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.modelviewHolder> {

    Context mContext;
    List<GameModel> itemlist;
    String by;

    public GameAdapter(Context mContext, List<GameModel> itemlist) {
        this.mContext = mContext;
        this.itemlist = itemlist;
    }

    @NonNull
    @Override
    public modelviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_recycle, parent, false);

        return new modelviewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull modelviewHolder holder, int position) {

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

        holder.tv_heading.setText(header);
        holder.tv_refId.setText("Ref Id: #" + refId);
        holder.tv_wish.setText(wish);
        holder.tv_date.setText(date);
        holder.tv_time.setText(time);
        holder.tv_map.setText(map);
        holder.tv_pers.setText(persp);
        holder.tv_winner.setText("₹ " + winner);
        holder.tv_perkill.setText("₹ " + perkill);
        holder.tv_playerjoined.setText(String.valueOf(playerjoined));
        holder.tv_playerremaining.setText(String.valueOf(playerrem));
        holder.pb_custom.setMax(playertotal);
        holder.pb_custom.setProgress(playerjoined);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("000admin").child(byUid).child("displayname");

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    String displayName = snapshot.getValue().toString();
                    holder.tv_by.setText("By: " + displayName);
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

        switch (gametype) {
            case "solo":
                holder.tv_entryfee.setText("₹" + entryfee);
                break;
            case "duo":
                finalEntryFee = entryfee / 2;
                holder.tv_entryfee.setText("₹" + finalEntryFee + " x 2");
                break;
            case "squad":
                finalEntryFee = entryfee / 4;
                holder.tv_entryfee.setText("₹" + finalEntryFee + " x 4");
                break;
            default:
                holder.tv_entryfee.setText("₹ " + entryfee);
                break;
        }


        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("000home")
                .child(game).child(refId).child("000userjoined").child(uid).child("teamuid");
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (!isGameCompleted) {
                        holder.tv_join.setText("ALREADY JOINED");
                        holder.tv_join.setBackgroundColor(Color.DKGRAY);
//                    holder.tv_join.setBackgroundColor(Integer.parseInt("#2e8b57"));
                        holder.tv_join.setClickable(false);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        holder.tv_refId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.ClipboardManager cm = (android.content.ClipboardManager)
                        mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clipData = android.content.ClipData.newPlainText("text copied", refId);
                assert cm != null;
                cm.setPrimaryClip(clipData);
                Toast.makeText(mContext, "Ref Id copied", Toast.LENGTH_SHORT).show();
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

        // goto all participants activity
        holder.cv_allPlayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, AllParticipantActivity.class);

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

        if (isGameCompleted) {
            holder.tv_join.setText("MATCH COMPLETED");
            holder.tv_join.setBackgroundColor(Color.DKGRAY);
            holder.tv_join.setClickable(false);
        } else {

            if (playerjoined >= playertotal) {

                holder.tv_join.setText("Contest Full");

            } else {

                holder.tv_join.setText("JOIN");
                holder.tv_join.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
                        if (fuser != null && fuser.isEmailVerified()) {

                            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
                            bottomSheetDialog.setContentView(R.layout.raw_bottomsheet);

                            Button btn_confirm_join = bottomSheetDialog.findViewById(R.id.btn_join_bottomsheet);
                            TextView tv_heading_bottomsheet = bottomSheetDialog.findViewById(R.id.tv_heading_bottomsheet);
                            TextView tv_date_bottomsheet = bottomSheetDialog.findViewById(R.id.tv_date_bottomsheet);
                            TextView tv_time_bottomsheet = bottomSheetDialog.findViewById(R.id.tv_time_bottomsheet);
                            TextView tv_map_bottomsheet = bottomSheetDialog.findViewById(R.id.tv_map_bottomsheet);
                            TextView tv_pers_bottomsheet = bottomSheetDialog.findViewById(R.id.tv_pers_bottomsheet);
                            TextView tv_winner_bottomsheet = bottomSheetDialog.findViewById(R.id.tv_winner_bottomsheet);
                            TextView tv_perkill_bottomsheet = bottomSheetDialog.findViewById(R.id.tv_perkill_bottomsheet);
                            TextView tv_entryfee_bottomsheet = bottomSheetDialog.findViewById(R.id.tv_entryfee_bottomsheet);
                            TextView tv_topay_bottomsheet = bottomSheetDialog.findViewById(R.id.tv_topay_bottomsheet);

                            tv_heading_bottomsheet.setText(header);
                            tv_date_bottomsheet.setText(date);
                            tv_time_bottomsheet.setText(time);
                            tv_map_bottomsheet.setText(map);
                            tv_pers_bottomsheet.setText(persp);
                            tv_winner_bottomsheet.setText("₹ " + winner);
                            tv_perkill_bottomsheet.setText("₹ " + perkill);
                            tv_entryfee_bottomsheet.setText("₹ " + entryfee);
                            tv_topay_bottomsheet.setText("₹ " + entryfee);

                            assert btn_confirm_join != null;
                            btn_confirm_join.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Context mContext = v.getContext();
                                    Intent i = new Intent(mContext, TeamNameActivity.class);
                                    i.putExtra("byuid", byUid);
                                    i.putExtra("by", by);
                                    i.putExtra("header", header);
                                    i.putExtra("game", game);
                                    i.putExtra("gametype", gametype);
                                    i.putExtra("date", date);
                                    i.putExtra("time", time);
                                    i.putExtra("refId", refId);
                                    i.putExtra("entryfee", entryfee);
                                    mContext.startActivity(i);
                                    bottomSheetDialog.dismiss();
                                }
                            });

                            bottomSheetDialog.show();

                        } else {
                            Toast.makeText(mContext, "Your Email is not Verified", Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
        }
    }

    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    public class modelviewHolder extends RecyclerView.ViewHolder {

        CardView cv_allPlayers;
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
        TextView tv_playerremaining;
        TextView tv_playerjoined;
        ProgressBar pb_custom;
        TextView tv_join;

        public modelviewHolder(@NonNull View itemView) {
            super(itemView);

            cv_allPlayers = (CardView) itemView.findViewById(R.id.cv_allPlayers);
            tv_heading = (TextView) itemView.findViewById(R.id.tv_heading);
            tv_refId = (TextView) itemView.findViewById(R.id.tv_refId);
            tv_by = (TextView) itemView.findViewById(R.id.tv_by);
            tv_wish = (TextView) itemView.findViewById(R.id.tv_chickendinner);
            myrule = (TextView) itemView.findViewById(R.id.myrule);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_map = (TextView) itemView.findViewById(R.id.tv_map);
            tv_pers = (TextView) itemView.findViewById(R.id.tv_pers);
            tv_winner = (TextView) itemView.findViewById(R.id.tv_winner);
            tv_perkill = (TextView) itemView.findViewById(R.id.tv_perkill);
            tv_entryfee = (TextView) itemView.findViewById(R.id.tv_entryfee);
            tv_playerremaining = (TextView) itemView.findViewById(R.id.tv_playerremaining);
            tv_playerjoined = (TextView) itemView.findViewById(R.id.tv_playerjoined);
            pb_custom = (ProgressBar) itemView.findViewById(R.id.pb_custom);
            tv_join = (TextView) itemView.findViewById(R.id.tv_join);

            myrule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent rules = new Intent(v.getContext(), PrivacyPolicyActivity.class);
                    rules.putExtra("data", 5);
                    v.getContext().startActivity(rules);
                }
            });


        }
    }
}
