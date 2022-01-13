package com.gamingtournament.msa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gamingtournament.msa.model.GameModel;
import com.gamingtournament.msa.model.UserInformation;
import com.gamingtournament.msa.utils.UtilValues;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import papaya.in.sendmail.SendMail;

public class TeamNameActivity extends AppCompatActivity {

    private TextView tv_gameName, tv_direction, tv_contestDate, tv_contestTime, tv_wallet_money;
    private EditText et_player1, et_player2, et_player3, et_player4;
    private Button btn_pay;

    private String st_game, st_header, st_refId, st_date, st_time, st_by, st_byUid, st_gametype;
    private String userEmail;
    private String userName;
    private int entryfee;
    private int count = 0, playerjoined = 0, totalplayer = 0;

    private FirebaseAuth mAuth;
    private DatabaseReference ref_000home;
    private String uid;
    private int userBal;
    private int adminBal;
    private int userTotalMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_name);

        tv_gameName = findViewById(R.id.tv_gameName);
        tv_direction = findViewById(R.id.tv_direction);
        tv_contestDate = findViewById(R.id.tv_contestDate);
        tv_contestTime = findViewById(R.id.tv_contestTime);
        tv_wallet_money = findViewById(R.id.tv_wallet_money);
        et_player1 = findViewById(R.id.et_player1);
        et_player2 = findViewById(R.id.et_player2);
        et_player3 = findViewById(R.id.et_player3);
        et_player4 = findViewById(R.id.et_player4);
        btn_pay = findViewById(R.id.btn_pay);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userEmail = mAuth.getCurrentUser().getEmail();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref_000home = FirebaseDatabase.getInstance().getReference().child("000home");

        Intent i = getIntent();
        st_byUid = i.getStringExtra("byuid");
        st_by = i.getStringExtra("by");
        st_gametype = i.getStringExtra("gametype");
        st_header = i.getStringExtra("header");
        st_game = i.getStringExtra("game");
        st_date = i.getStringExtra("date");
        st_time = i.getStringExtra("time");
        st_refId = i.getStringExtra("refId");
        entryfee = i.getIntExtra("entryfee", 0);


        if (st_game.trim().equalsIgnoreCase("pubg")) {
            tv_direction.setText("Open your pubg app, go to profile menu and copy your username and paste below");
        } else if (st_game.trim().equalsIgnoreCase("freefire")) {
            tv_direction.setText("Open your Free Fire app, go to profile menu and copy your username and paste below");
        } else if (st_game.trim().equalsIgnoreCase("clashofclan")) {
            tv_direction.setText("Open your Clash of Clan app, go to profile menu and copy your username and paste below");
        } else if (st_game.trim().equalsIgnoreCase("pubglite")) {
            tv_direction.setText("Open your Pubg Lite app, go to profile menu and copy your username and paste below");
        } else {
            tv_direction.setText("Open your Pubg Lite app, go to profile menu and copy your username and paste below");
        }

        if (st_header != null) {
            tv_gameName.setText(st_header);
        }
        if (st_date != null) {
            tv_contestDate.setText(st_date);
        }
        if (st_time != null) {
            tv_contestTime.setText(st_time);
        }

        // check game type and visible edittext according them
        if (st_gametype.equals("solo")) {
            et_player2.setVisibility(View.GONE);
            et_player3.setVisibility(View.GONE);
            et_player4.setVisibility(View.GONE);
        } else if (st_gametype.equals("duo")) {
            et_player3.setVisibility(View.GONE);
            et_player4.setVisibility(View.GONE);
        }

        // get user Balance
        getUserData();

        getAdminData(st_byUid);


        // get no of joined players
        DatabaseReference ref_playerjoined = ref_000home.child(st_game).child(st_refId);
        ref_playerjoined.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    GameModel gm = new GameModel();
                    gm.setPlayerjoined(snapshot.getValue(GameModel.class).getPlayerjoined());
                    gm.setTotalplayer(snapshot.getValue(GameModel.class).getTotalplayer());

                    playerjoined = gm.getPlayerjoined();
                    totalplayer = gm.getTotalplayer();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // get count of joined users
        DatabaseReference ref_count = ref_000home.child(st_game).child(st_refId).child("000userjoined");
        ref_count.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    count = (int) snapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TeamNameActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btn_pay.setText("Pay (₹ " + entryfee + ")");
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (playerjoined < totalplayer) {

                    if (userBal >= entryfee) {
                        uploadJoinedData(entryfee);
                    } else {
                        Toast.makeText(TeamNameActivity.this, "Insufficient Balance", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(TeamNameActivity.this, "Contest Full", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateBalance(int entryfee) {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        db.child("balance").setValue(userBal - entryfee).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                // for user transaction record
                String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
                DatabaseReference dr = db.child("000transaction").child(timeStamp);
                dr.child("bal").setValue(entryfee);
                dr.child("id").setValue(timeStamp);
                dr.child("status").setValue("debited");
//                dr.child("txnid").setValue(timeStamp);

            }
        });

        db.child("totalmatch").setValue(userTotalMatch + 1);

        // for admin balance update

        if (st_byUid != null) {

            DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("users").child(st_byUid);
            if (adminBal != 0) {
                rf.child("balance").setValue(adminBal + entryfee);
            }

        }

        String userMailId = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (userMailId != null) {

            String game;

            if (st_game.equals("pubg")){
                game = "BGMI";
            } else {
                game = st_game;
            }

            String gameDetails = "Game Name    \t: \t\t"+ game+ "\n"+
                                 "Game Type    \t: \t\t"+ st_gametype+ "\n"+
                                 "Game Date    \t: \t\t"+ st_date+ "\n"+
                                 "Game Time    \t: \t\t"+ st_time+ "\n"+
                                 "Entry Fee    \t: \t\t"+"₹ "+ entryfee+ "\n"+
                                 "Game Ref Id  \t: \t\t"+ st_refId+ "\n"+
                                 "Conducted by \t: \t\t"+ st_by+ "\n";

            String emailSub = "Congratulations on successfully joined the contest | Ref Id: "+st_refId;

            String emailMsg = "Dear "+userName+ ", \n\nThank you for being an active user of WR family." +
                    "\nYou have successfully joined the contest with the following details: \n\n" +
                    gameDetails+
                    "\n Please be on time (15 min before game time) and read game rules carefully before joining custom room"+
                    "\n\nHave question about Rebellion War? We'd love to help you! just hit reply." +
                    " \n\n\n\nTeam WR family\n\n"+
                    "Whatsapp : \t\t +917520244089 \n"+
                    "Website  : \t\t"+UtilValues.WEB_URL;

            SendMail mail = new SendMail(UtilValues.SENDER_EMAIL, UtilValues.SENDER_PASS,
                    userMailId, emailSub , emailMsg);
            mail.execute();
        }

    }

    private void getUserData() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    UserInformation ui = new UserInformation();
                    ui.setBalance(snapshot.getValue(UserInformation.class).getBalance());
                    ui.setTotalmatch(snapshot.getValue(UserInformation.class).getTotalmatch());
                    ui.setName(snapshot.getValue(UserInformation.class).getName());

                    userBal = ui.getBalance();
                    userTotalMatch = ui.getTotalmatch();
                    userName = ui.getName();

                    tv_wallet_money.setText("₹ " + userBal);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void getAdminData(String uid) {

        if (uid != null) {

            DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        UserInformation ui = new UserInformation();
                        ui.setBalance(snapshot.getValue(UserInformation.class).getBalance());
//                    ui.setTotalmatch(snapshot.getValue(UserInformation.class).getTotalmatch());

                        adminBal = ui.getBalance();

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    private void uploadJoinedData(int entryfee) {

        final String player1 = et_player1.getText().toString();
        String player2 = et_player2.getText().toString();
        String player3 = et_player3.getText().toString();
        String player4 = et_player4.getText().toString();

        DatabaseReference dr = ref_000home.child(st_game).child(st_refId).child("000userjoined").child(uid);
        DatabaseReference ref_playerJoined = ref_000home.child(st_game).child(st_refId);

        if (st_gametype.equals("solo")) {
            if (player1.isEmpty()) {
                et_player1.setError("Mandatory field");
                et_player1.requestFocus();
            } else {
                // update number of joined player
                ref_playerJoined.child("playerjoined").setValue(playerjoined + 1);

                dr.child("teamno").setValue("" + (count + 1));
                dr.child("teamkill").setValue(0);
                dr.child("teamuid").setValue(uid);
                dr.child("iswinner").setValue(false);
                dr.child("player1").setValue(player1);
                dr.child("player1kill").setValue(0);
                updateBalance(entryfee);
                Toast.makeText(TeamNameActivity.this, "success", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(TeamNameActivity.this, MainActivity.class));
                finish();
            }

        } else if (st_gametype.equals("duo")) {

            if (player1.isEmpty()) {
                et_player1.setError("Mandatory field");
                et_player1.requestFocus();
            } else if (player2.isEmpty()) {
                et_player2.setError("Mandatory field");
                et_player2.requestFocus();
            } else {
                // update number of joined player
                ref_playerJoined.child("playerjoined").setValue(playerjoined + 2);

                dr.child("teamno").setValue("" + (count + 1));
                dr.child("teamkill").setValue(0);
                dr.child("teamuid").setValue(uid);
                dr.child("iswinner").setValue(false);
                dr.child("player1").setValue(player1);
                dr.child("player1kill").setValue(0);
                dr.child("player2").setValue(player2);
                dr.child("player2kill").setValue(0);
                updateBalance(entryfee);
                Toast.makeText(TeamNameActivity.this, "success", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(TeamNameActivity.this, MainActivity.class));
                finish();
            }

        } else {

            if (player1.isEmpty()) {
                et_player1.setError("Mandatory field");
                et_player1.requestFocus();
            } else if (player2.isEmpty()) {
                et_player2.setError("Mandatory field");
                et_player2.requestFocus();
            } else if (player3.isEmpty()) {
                et_player3.setError("Mandatory field");
                et_player3.requestFocus();
            } else if (player4.isEmpty()) {
                et_player4.setError("Mandatory field");
                et_player4.requestFocus();
            } else {
                // update number of joined player
                ref_playerJoined.child("playerjoined").setValue(playerjoined + 4);

                dr.child("teamno").setValue("" + (count + 1));
                dr.child("teamkill").setValue(0);
                dr.child("teamuid").setValue(uid);
                dr.child("iswinner").setValue(false);
                dr.child("player1").setValue(player1);
                dr.child("player1kill").setValue(0);
                dr.child("player2").setValue(player2);
                dr.child("player2kill").setValue(0);
                dr.child("player3").setValue(player3);
                dr.child("player3kill").setValue(0);
                dr.child("player4").setValue(player4);
                dr.child("player4kill").setValue(0);
                updateBalance(entryfee);
                Toast.makeText(TeamNameActivity.this, "success", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(TeamNameActivity.this, MainActivity.class));
                finish();
            }

        }

    }

}