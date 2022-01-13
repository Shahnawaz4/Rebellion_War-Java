package com.gamingtournament.msa.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.gamingtournament.msa.MainActivity;
import com.gamingtournament.msa.R;
import com.gamingtournament.msa.TeamNameActivity;
import com.gamingtournament.msa.model.AllParticipantsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AllParticipantAdapter extends RecyclerView.Adapter<AllParticipantAdapter.modelviewHolder> {

    Context mContext;
    List<AllParticipantsModel> itemlist;
    String game,gametype,refId;
    int perkill, winner;
    boolean isMatchCompleted;

    public AllParticipantAdapter(Context mContext, List<AllParticipantsModel> itemlist,
                                 String game, String gametype, String refId, int perkill, int winner, boolean isMatchCompleted) {
        this.mContext = mContext;
        this.itemlist = itemlist;
        this.game = game;
        this.gametype = gametype;
        this.refId = refId;
        this.perkill = perkill;
        this.winner = winner;
        this.isMatchCompleted = isMatchCompleted;
    }

    @NonNull
    @Override
    public AllParticipantAdapter.modelviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_all_participant,parent,false);
        return new AllParticipantAdapter.modelviewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AllParticipantAdapter.modelviewHolder holder, int position) {

        int winningAmount = 0;
        final int teamKill = itemlist.get(position).getTeamkill();
        final boolean isWinner = itemlist.get(position).isIswinner();
        final String teamNo = itemlist.get(position).getTeamno();
        final String teamUid = itemlist.get(position).getTeamuid();

        final String player1 = itemlist.get(position).getPlayer1();
        final String player2 = itemlist.get(position).getPlayer2();
        final String player3 = itemlist.get(position).getPlayer3();
        final String player4 = itemlist.get(position).getPlayer4();

        final int player1kill = itemlist.get(position).getPlayer1kill();
        final int player2kill = itemlist.get(position).getPlayer2kill();
        final int player3kill = itemlist.get(position).getPlayer3kill();
        final int player4kill = itemlist.get(position).getPlayer4kill();

        if (gametype.equals("solo")){
            holder.ll_player2.setVisibility(View.GONE);
            holder.ll_player3.setVisibility(View.GONE);
            holder.ll_player4.setVisibility(View.GONE);
        } else if (gametype.equals("duo")){
            holder.ll_player3.setVisibility(View.GONE);
            holder.ll_player4.setVisibility(View.GONE);
        }

        if (isWinner){
            winningAmount = (teamKill * perkill);
            holder.tv_teamWinnings.setText("₹"+winningAmount+" + "+"₹"+winner);
            holder.tv_winnerTeam.setVisibility(View.VISIBLE);
        } else {
            winningAmount = (teamKill *perkill);
            holder.tv_teamWinnings.setText("₹ "+winningAmount);
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (teamUid.equals(uid)){
            if (!isMatchCompleted){
                holder.tv_editPlayerName.setVisibility(View.VISIBLE);
            }

        }

        holder.tv_teamNo.setText(teamNo);
        holder.tv_teamKills.setText(""+teamKill);

//        holder.tv_teamWinnings.setText(winningAmount);

        holder.tv_player1.setText(player1);
        holder.tv_player2.setText(player2);
        holder.tv_player3.setText(player3);
        holder.tv_player4.setText(player4);

        holder.tv_player1kills.setText(player1kill+" kill");
        holder.tv_player2kills.setText(player2kill+" kill");
        holder.tv_player3kills.setText(player3kill+" kill");
        holder.tv_player4kills.setText(player4kill+" kill");


        holder.tv_editPlayerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlayerNames(game,gametype,refId,player1,player2,player3,player4);

            }
        });

    }

    private void updatePlayerNames(String game,String gametype,String refId,String p1, String p2, String p3, String p4) {

        Dialog dialog = new Dialog(mContext, R.style.AlertDialogTheme);
        dialog.setContentView(R.layout.custom_dialog_for_playername);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow()!=null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        ImageView iv_closeDialog = dialog.findViewById(R.id.iv_closeDialog);
        EditText et_dialogPlayer1 = dialog.findViewById(R.id.et_player1);
        EditText et_dialogPlayer2 = dialog.findViewById(R.id.et_player2);
        EditText et_dialogPlayer3 = dialog.findViewById(R.id.et_player3);
        EditText et_dialogPlayer4 = dialog.findViewById(R.id.et_player4);
        CardView cv_player1 = dialog.findViewById(R.id.cv_player1);
        CardView cv_player2 = dialog.findViewById(R.id.cv_player2);
        CardView cv_player3 = dialog.findViewById(R.id.cv_player3);
        CardView cv_player4 = dialog.findViewById(R.id.cv_player4);

        Button btn_save = dialog.findViewById(R.id.btn_save);

        et_dialogPlayer1.setText(p1);
        et_dialogPlayer2.setText(p2);
        et_dialogPlayer3.setText(p3);
        et_dialogPlayer4.setText(p4);

        // check game type and visible edittext according them
        if (gametype.equals("solo")) {
            cv_player2.setVisibility(View.GONE);
            cv_player3.setVisibility(View.GONE);
            cv_player4.setVisibility(View.GONE);
        } else if (gametype.equals("duo")) {
            cv_player3.setVisibility(View.GONE);
            cv_player4.setVisibility(View.GONE);
        }


        iv_closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadJoinedData();

                final String player1 = et_dialogPlayer1.getText().toString();
                String player2 = et_dialogPlayer2.getText().toString();
                String player3 = et_dialogPlayer3.getText().toString();
                String player4 = et_dialogPlayer4.getText().toString();

                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("000home")
                        .child(game).child(refId).child("000userjoined").child(uid);

                if (gametype.equals("solo")) {
                    if (player1.isEmpty()) {
                        et_dialogPlayer1.setError("Mandatory field");
                        et_dialogPlayer1.requestFocus();
                    } else {
                        dr.child("player1").setValue(player1);
                        Toast.makeText(mContext, "Saved Successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                } else if (gametype.equals("duo")) {
                    if (player1.isEmpty()) {
                        et_dialogPlayer1.setError("Mandatory field");
                        et_dialogPlayer1.requestFocus();
                    } else if (player2.isEmpty()) {
                        et_dialogPlayer2.setError("Mandatory field");
                        et_dialogPlayer2.requestFocus();
                    } else {
                        dr.child("player1").setValue(player1);
                        dr.child("player2").setValue(player2);
                        Toast.makeText(mContext, "Saved Successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                } else {

                    if (player1.isEmpty()) {
                        et_dialogPlayer1.setError("Mandatory field");
                        et_dialogPlayer1.requestFocus();
                    } else if (player2.isEmpty()) {
                        et_dialogPlayer2.setError("Mandatory field");
                        et_dialogPlayer2.requestFocus();
                    } else if (player3.isEmpty()) {
                        et_dialogPlayer3.setError("Mandatory field");
                        et_dialogPlayer3.requestFocus();
                    } else if (player4.isEmpty()) {
                        et_dialogPlayer4.setError("Mandatory field");
                        et_dialogPlayer4.requestFocus();
                    } else {

                        dr.child("player1").setValue(player1);
                        dr.child("player2").setValue(player2);
                        dr.child("player3").setValue(player3);
                        dr.child("player4").setValue(player4);
                        Toast.makeText(mContext, "Saved Successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                }
            }
        });


        dialog.show();

    }

    private void uploadJoinedData() {


    }

    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    public class modelviewHolder extends RecyclerView.ViewHolder{

        TextView tv_winnerTeam, tv_editPlayerName, tv_teamNo, tv_teamKills, tv_teamWinnings;
        LinearLayout ll_player1, ll_player2,ll_player3,ll_player4;
        TextView tv_player1, tv_player2, tv_player3, tv_player4;
        TextView tv_player1kills, tv_player2kills, tv_player3kills, tv_player4kills;


        public modelviewHolder(@NonNull View itemView) {
            super(itemView);

            tv_winnerTeam = (TextView) itemView.findViewById(R.id.tv_winnerTeam);
            tv_editPlayerName = (TextView) itemView.findViewById(R.id.tv_editPlayerName);
            tv_teamNo = (TextView) itemView.findViewById(R.id.tv_teamNo);
            tv_teamKills = (TextView) itemView.findViewById(R.id.tv_teamKills);
            tv_teamWinnings = (TextView) itemView.findViewById(R.id.tv_teamWinnings);

            ll_player1 = (LinearLayout) itemView.findViewById(R.id.ll_player1);
            ll_player2 = (LinearLayout) itemView.findViewById(R.id.ll_player2);
            ll_player3 = (LinearLayout) itemView.findViewById(R.id.ll_player3);
            ll_player4 = (LinearLayout) itemView.findViewById(R.id.ll_player4);

            tv_player1 = (TextView) itemView.findViewById(R.id.tv_player1);
            tv_player2 = (TextView) itemView.findViewById(R.id.tv_player2);
            tv_player3 = (TextView) itemView.findViewById(R.id.tv_player3);
            tv_player4 = (TextView) itemView.findViewById(R.id.tv_player4);

            tv_player1kills = (TextView) itemView.findViewById(R.id.tv_player1kills);
            tv_player2kills = (TextView) itemView.findViewById(R.id.tv_player2kills);
            tv_player3kills = (TextView) itemView.findViewById(R.id.tv_player3kills);
            tv_player4kills = (TextView) itemView.findViewById(R.id.tv_player4kills);


        }
    }

}
