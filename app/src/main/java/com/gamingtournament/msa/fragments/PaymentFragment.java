package com.gamingtournament.msa.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gamingtournament.msa.R;
import com.gamingtournament.msa.model.UserInformation;
import com.gamingtournament.msa.payment.AddMoneyActivity;
import com.gamingtournament.msa.payment.TransactionHistoryActivity;
import com.gamingtournament.msa.payment.WithdrawMoneyActivity;
import com.gamingtournament.msa.profile.MyProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PaymentFragment extends Fragment {

    private TextView tv_Name,tv_Email,tv_matchPlayed,tv_totalKills,tv_balance, tv_uid;
    private CardView cv_addMoney, cv_withdrawMoney, cv_transactionHistory;
    private CircleImageView iv_profileImage;
    private ImageView iv_clipboard;
    private String st_imageUrl;

    private String uid;
    private DatabaseReference mRef;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        tv_Name = view.findViewById(R.id.tv_profileName);
        tv_Email = view.findViewById(R.id.tv_profileEmail);
        tv_matchPlayed = view.findViewById(R.id.tv_matchPlayed);
        tv_totalKills = view.findViewById(R.id.tv_totalKills);
        tv_balance = view.findViewById(R.id.tv_balance);
        cv_addMoney = view.findViewById(R.id.cv_addMoney);
        cv_withdrawMoney = view.findViewById(R.id.cv_withdrawMoney);
        cv_transactionHistory = view.findViewById(R.id.cv_transactionHistory);
        tv_uid = view.findViewById(R.id.tv_uid);
        iv_profileImage = view.findViewById(R.id.iv_profileImage);
        iv_clipboard = view.findViewById(R.id.iv_clipboard);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        tv_uid.setText(uid);

        cv_addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddMoneyActivity.class));
            }
        });

        cv_withdrawMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), WithdrawMoneyActivity.class));
            }
        });

        cv_transactionHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TransactionHistoryActivity.class));
            }
        });




        // copy to clipboard
        iv_clipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.ClipboardManager cm = (android.content.ClipboardManager)
                        getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clipData = android.content.ClipData.newPlainText("text copied",uid);
                assert cm != null;
                cm.setPrimaryClip(clipData);
                Toast.makeText(getContext(), "Text copied", Toast.LENGTH_SHORT).show();

            }
        });


        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

//                String n = snapshot.getValue().toString();
//                tv_Name.setText(n);

                UserInformation information=new UserInformation();
                information.setName(snapshot.getValue(UserInformation.class).getName());
                information.setEmail(snapshot.getValue(UserInformation.class).getEmail());
                information.setImgurl(snapshot.getValue(UserInformation.class).getImgurl());
                information.setTotalmatch(snapshot.getValue(UserInformation.class).getTotalmatch());
                information.setTotalkill(snapshot.getValue(UserInformation.class).getTotalkill());
                information.setBalance(snapshot.getValue(UserInformation.class).getBalance());

                tv_Name.setText(information.getName());
                tv_Email.setText(information.getEmail());
                tv_matchPlayed.setText(""+information.getTotalmatch());
                tv_totalKills.setText(""+information.getTotalkill());
                tv_balance.setText(""+information.getBalance());

//                String url = "https://srcodex.000webhostapp.com/one.jpg";
//
//                Picasso.get()
//                        .load(url)
//                        .placeholder(R.mipmap.ic_launcher)
//                        .error(R.mipmap.ic_launcher)
//                        .into(iv_profileImage);

                st_imageUrl = information.getImgurl();
                if (st_imageUrl!=null && !st_imageUrl.equals("")) {
                    Picasso.get()
                            .load(st_imageUrl)
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .into(iv_profileImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}