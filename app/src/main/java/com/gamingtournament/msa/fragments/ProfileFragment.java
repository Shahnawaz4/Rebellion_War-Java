package com.gamingtournament.msa.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gamingtournament.msa.LoginActivity;
import com.gamingtournament.msa.R;
import com.gamingtournament.msa.more.PrivacyPolicyActivity;
import com.gamingtournament.msa.profile.TopPlayersActivity;
import com.gamingtournament.msa.model.UserInformation;
import com.gamingtournament.msa.profile.ChangeEmailActivity;
import com.gamingtournament.msa.profile.ChangePassActivity;
import com.gamingtournament.msa.profile.MyProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private TextView tv_Name, tv_Email, tv_matchPlayed, tv_totalKills, tv_bonusPoint, tv_verify, tv_uid;
    private CardView cv_changeEmail, cv_changePass, cv_myProfile, cv_topPlayers, cv_CreateMatch;
    private CircleImageView iv_profileImage;
    private ImageView iv_clipboard;
    private String st_imageUrl;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private String uid;
    private DatabaseReference mRef;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tv_Name = view.findViewById(R.id.tv_profileName);
        tv_Email = view.findViewById(R.id.tv_profileEmail);
        tv_matchPlayed = view.findViewById(R.id.tv_matchPlayed);
        tv_totalKills = view.findViewById(R.id.tv_totalKills);
        tv_bonusPoint = view.findViewById(R.id.tv_bonusPoint);
        cv_myProfile = view.findViewById(R.id.cv_myProfile);
        tv_verify = view.findViewById(R.id.tv_verify);
        cv_changeEmail = view.findViewById(R.id.cv_changeEmail);
        cv_changePass = view.findViewById(R.id.cv_changePass);
        cv_topPlayers = view.findViewById(R.id.cv_topPlayers);
        cv_CreateMatch = view.findViewById(R.id.cv_CreateMatch);
        tv_uid = view.findViewById(R.id.tv_uid);
        iv_profileImage = view.findViewById(R.id.iv_profileImage);
        iv_clipboard = view.findViewById(R.id.iv_clipboard);

        dialog = new Dialog(getContext());

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        tv_uid.setText(uid);

        cv_myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MyProfileActivity.class));
            }
        });

        // check email is verified or not
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.isEmailVerified()) {
            tv_verify.setText("verified");
            tv_verify.setTextColor(Color.GREEN);
        }

        tv_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (!user.isEmailVerified()) {
                    verifyEmail();
                }
            }
        });


        cv_changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChangeEmailActivity.class));
            }
        });

        cv_changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChangePassActivity.class));
            }
        });

        cv_topPlayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TopPlayersActivity.class));
            }
        });

        cv_CreateMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent adminIntent = new Intent(getContext(), PrivacyPolicyActivity.class);
                adminIntent.putExtra("data", 9);
                requireContext().startActivity(adminIntent);
            }
        });

        // copy to clipboard
        iv_clipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.ClipboardManager cm = (android.content.ClipboardManager)
                        getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clipData = android.content.ClipData.newPlainText("text copied", uid);
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

                UserInformation information = new UserInformation();
                information.setName(snapshot.getValue(UserInformation.class).getName());
                information.setEmail(snapshot.getValue(UserInformation.class).getEmail());
                information.setImgurl(snapshot.getValue(UserInformation.class).getImgurl());
                information.setTotalmatch(snapshot.getValue(UserInformation.class).getTotalmatch());
                information.setTotalkill(snapshot.getValue(UserInformation.class).getTotalkill());
                information.setBonuspoint(snapshot.getValue(UserInformation.class).getBonuspoint());

                tv_Name.setText(information.getName());
                tv_Email.setText(information.getEmail());
                tv_matchPlayed.setText(String.valueOf(information.getTotalmatch()));
                tv_totalKills.setText(String.valueOf(information.getTotalkill()));
                tv_bonusPoint.setText(String.valueOf(information.getBonuspoint()));

                st_imageUrl = information.getImgurl();
                if (st_imageUrl != null && !st_imageUrl.equals("")) {
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

    private void verifyEmail() {
        dialog.setContentView(R.layout.pb_custom_layout);
        if (dialog.getWindow()!=null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();

        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (fuser != null && !fuser.isEmailVerified()) {
            fuser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        showAlert();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Error " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(getContext(), "Error "+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }

    }

    private void showAlert() {
        final AlertDialog.Builder existDialog = new AlertDialog.Builder(getContext());
        existDialog.setTitle("Email Sent");
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        existDialog.setMessage("A verification email is sent to "+email+". Please Verify and relogin. \n\n Note : Check your Email inbox and spam");

        existDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putBoolean("savelogin", false);
                editor.apply();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();

            }
        });
        existDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = existDialog.create();
        alertDialog.show();

    }

}