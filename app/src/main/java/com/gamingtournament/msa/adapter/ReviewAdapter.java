package com.gamingtournament.msa.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gamingtournament.msa.MainActivity;
import com.gamingtournament.msa.R;
import com.gamingtournament.msa.model.ReviewModel;
import com.gamingtournament.msa.model.TransactionModel;
import com.gamingtournament.msa.model.UserInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.modelviewHolder> {

    Context mContext;
    List<ReviewModel> itemlist;
    String adminUid;

    public ReviewAdapter(Context mContext, List<ReviewModel> itemlist, String adminUid) {
        this.mContext = mContext;
        this.itemlist = itemlist;
        this.adminUid = adminUid;
    }

    @NonNull
    @Override
    public ReviewAdapter.modelviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_review,parent,false);
        return new modelviewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.modelviewHolder holder, int position) {

        String userUid = itemlist.get(position).getUid();
        String timestamp = itemlist.get(position).getTimestamp();
        String reviewMsg = itemlist.get(position).getReview();
        float ft_rating = itemlist.get(position).getRating();


        holder.tv_reviewMsg.setText(""+reviewMsg);
        holder.rb_userRated.setRating(ft_rating);

        String st_date = getDate(Long.parseLong(timestamp));
        holder.tv_revieTime.setText(st_date);

        switch ((int) ft_rating){
            case 1:
            case 2:
                holder.rb_userRated.setProgressTintList(ColorStateList.valueOf(Color.RED));
                holder.rb_userRated.setSecondaryProgressTintList(ColorStateList.valueOf(Color.RED));
                break;
            case 3:
            case 4:
            case 5:
                holder.rb_userRated.setProgressTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorWhatsapp)));
                holder.rb_userRated.setSecondaryProgressTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorWhatsapp)));
                break;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (userUid.matches(uid)){
            holder.iv_delete.setVisibility(View.VISIBLE);
        }

        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference delete = FirebaseDatabase.getInstance().getReference().child("000admin").child(adminUid).child("000rating")
                        .child(uid);
                delete.setValue(null);
                Toast.makeText(mContext, "Review Deleted", Toast.LENGTH_SHORT).show();
            }
        });


        //set user name and image
        DatabaseReference db_name = FirebaseDatabase.getInstance().getReference().child("users").child(userUid);
        db_name.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserInformation information = new UserInformation();
                information.setName(snapshot.getValue(UserInformation.class).getName());
                information.setImgurl(snapshot.getValue(UserInformation.class).getImgurl());

                holder.tv_reviewName.setText(information.getName());

                String st_imageUrl = information.getImgurl();
                if (st_imageUrl != null && !st_imageUrl.equals("")) {
                    Picasso.get()
                            .load(st_imageUrl)
                            .placeholder(R.drawable.logo)
                            .error(R.drawable.logo)
                            .into(holder.iv_userImg);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                if (dialog.isShowing()) {
//                    dialog.dismiss();
//                }
                Toast.makeText(mContext, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    private String getDate(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time*1000);
        Date dat = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

        return sdf.format(dat);
    }


    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    public static class modelviewHolder extends RecyclerView.ViewHolder {

        CircleImageView iv_userImg;
        TextView tv_reviewName, tv_revieTime, tv_reviewMsg;
        ImageView iv_delete;
        RatingBar rb_userRated;

        public modelviewHolder(@NonNull View itemView) {
            super(itemView);

            iv_userImg = (CircleImageView) itemView.findViewById(R.id.iv_userImg);
            tv_reviewName = (TextView) itemView.findViewById(R.id.tv_reviewName);
            tv_revieTime = (TextView) itemView.findViewById(R.id.tv_revieTime);
            tv_reviewMsg = (TextView) itemView.findViewById(R.id.tv_reviewMsg);
            iv_delete = (ImageView) itemView.findViewById(R.id.iv_delete);
            rb_userRated = (RatingBar) itemView.findViewById(R.id.rb_userRated);

        }
    }

}
