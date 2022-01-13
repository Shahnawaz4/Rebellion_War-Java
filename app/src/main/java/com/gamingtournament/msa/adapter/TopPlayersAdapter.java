package com.gamingtournament.msa.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gamingtournament.msa.R;
import com.gamingtournament.msa.model.TopPlayersModel;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TopPlayersAdapter extends RecyclerView.Adapter<TopPlayersAdapter.modelviewHolder> {

    Context mContext;
    List<TopPlayersModel> itemlist;

    public TopPlayersAdapter(Context mContext, List<TopPlayersModel> itemlist) {
        this.mContext = mContext;
        this.itemlist = itemlist;
    }

    @NonNull
    @Override
    public TopPlayersAdapter.modelviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_top_players,parent,false);
        return new modelviewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TopPlayersAdapter.modelviewHolder holder, int position) {

        String name = itemlist.get(position).getName();
        String phno = itemlist.get(position).getPhno();
        String imgUrl = itemlist.get(position).getImgurl();

       final int totalkill = itemlist.get(position).getTotalkill();
       final int totalmatch = itemlist.get(position).getTotalmatch();

        holder.tv_rank.setText(String.valueOf(position+1));

//        if (totalmatch == 0){
//            holder.tv_playerKd.setText("0.00");
//        } else {
//            float kd = (float) totalkill/totalmatch;
//
//            DecimalFormat df = new DecimalFormat("0.00");
//            holder.tv_playerKd.setText(df.format(kd));
//
//        }
        holder.tv_playerKd.setText(String.valueOf(totalkill));

        String maskPhno = "xxxxx" + phno.substring(5);

        holder.tv_playerName.setText(name);
        holder.tv_playerPhone.setText(maskPhno);

        if (imgUrl != null && !imgUrl.equals("")) {
            Picasso.get()
                    .load(imgUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.iv_profilePic);
        }

    }
    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    public class modelviewHolder extends RecyclerView.ViewHolder {

        CircleImageView iv_profilePic;
        TextView tv_rank, tv_playerName, tv_playerPhone, tv_playerKd;

        public modelviewHolder(@NonNull View itemView) {
            super(itemView);

            iv_profilePic = (CircleImageView) itemView.findViewById(R.id.iv_profilePic);
            tv_rank = (TextView) itemView.findViewById(R.id.tv_rank);
            tv_playerName = (TextView) itemView.findViewById(R.id.tv_playerName);
            tv_playerPhone = (TextView) itemView.findViewById(R.id.tv_playerPhone);
            tv_playerKd = (TextView) itemView.findViewById(R.id.tv_playerKd);

        }
    }

}
