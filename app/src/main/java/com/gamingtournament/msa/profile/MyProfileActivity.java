package com.gamingtournament.msa.profile;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gamingtournament.msa.MainActivity;
import com.gamingtournament.msa.R;
import com.gamingtournament.msa.model.UserInformation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URI;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileActivity extends AppCompatActivity {

    private TextView tv_Name, tv_Email;
    private CircleImageView iv_Image;
    private Button btn_update;
    private EditText et_name, et_phno;
    private String st_email;

    DatabaseReference reference;
    private StorageReference mStorageRef;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        iv_Image = findViewById(R.id.iv_Image);
        tv_Name = findViewById(R.id.tv_Name);
        tv_Email = findViewById(R.id.tv_Email);
        et_name = findViewById(R.id.et_name);
        et_phno = findViewById(R.id.et_phno);
        btn_update = findViewById(R.id.btn_update);

        dialog = new Dialog(MyProfileActivity.this, R.style.AlertDialogTheme);
        dialog.setContentView(R.layout.pb_custom_layout);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow()!=null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();


        // for title
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
                finish();
            }
        });

        iv_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                profileImageResultLauncher.launch(intent);
            }
        });

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("users").child(id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UserInformation uip = new UserInformation();

                uip.setName(dataSnapshot.getValue(UserInformation.class).getName());
                uip.setPhno(dataSnapshot.getValue(UserInformation.class).getPhno());
                uip.setEmail(dataSnapshot.getValue(UserInformation.class).getEmail());
                uip.setImgurl(dataSnapshot.getValue(UserInformation.class).getImgurl());

                et_name.setText(uip.getName());
                tv_Name.setText(uip.getName());
                tv_Email.setText(uip.getEmail());
                et_phno.setText(uip.getPhno());

                st_email = uip.getEmail();

             String st_imageUrl = uip.getImgurl();
                if (st_imageUrl!=null && !st_imageUrl.equals("")) {
                    Picasso.get()
                            .load(st_imageUrl)
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .into(iv_Image);
                }
                if (dialog.isShowing()){
                    dialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
                Toast.makeText(MyProfileActivity.this, "Error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = et_name.getText().toString();
                final String phno = et_phno.getText().toString();

                if (name.isEmpty()) {
                    et_name.setError("Enter Name");
                    et_name.requestFocus();
                } else if (phno.isEmpty()) {
                    et_phno.setError("Enter Phone Number");
                    et_phno.requestFocus();
                } else if (phno.length() < 10 || phno.length() > 13) {
                    et_phno.setError("Phone Number not valid");
                    et_phno.requestFocus();
                } else {

                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users").child(id);

                    db.child("name").setValue(name);
                    db.child("phno").setValue(phno);
                    Toast.makeText(MyProfileActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MyProfileActivity.this, MainActivity.class));
                    finish();
                    overridePendingTransition(0, 0);

                }

            }
        });

    }


    // this is for activity image result
    ActivityResultLauncher<Intent> profileImageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){

                        if (result.getData()!=null) {

                            TextView tv_pbMsg = dialog.findViewById(R.id.tv_pbMsg);
                            tv_pbMsg.setText("Updating...");
                            dialog.show();

                            Intent data = result.getData();
                            Uri fileUri = data.getData();

                            File imageFile = new File(fileUri.toString());
                            long fileSizeInBite = imageFile.length();
                            Toast.makeText(MyProfileActivity.this, "Size: "+fileSizeInBite, Toast.LENGTH_SHORT).show();

                            if (fileSizeInBite <= 500*1024){
                                mStorageRef = FirebaseStorage.getInstance().getReference();
                                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                StorageReference storageRef = mStorageRef.child("images/profile/"+ uid);
                                storageRef.putFile(fileUri)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                // get url of profile image
                                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String st_imgUrl = uri.toString();
                                                        updateUserprofileUrl(st_imgUrl);
                                                    }
                                                });
                                            }
                                        });
                                Toast.makeText(MyProfileActivity.this, "Selected", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MyProfileActivity.this, "Error : File size must be under 500KB", Toast.LENGTH_SHORT).show();
                            }

//                            iv_Image.setImageURI(fileUri);
//                            assert fileUri != null;
//                            String file = fileUri.getPath();


                        }
                    }else {
                        dialog.dismiss();
                        // return to the app without selecting any image.
                        Toast.makeText(MyProfileActivity.this, "Error", Toast.LENGTH_LONG).show();
                    }
                }
            }
    );

    private void updateUserprofileUrl(String st_imgUrl) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        db.child("imgurl").setValue(st_imgUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialog.dismiss();
                Toast.makeText(MyProfileActivity.this, "Image Updated successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(MyProfileActivity.this,"Error: "+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });


    }


}
