package com.gamingtournament.msa.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gamingtournament.msa.R;
import com.gamingtournament.msa.model.TransactionModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.modelviewHolder> {

    Context mContext;
    List<TransactionModel> itemlist;

    public TransactionAdapter(Context mContext, List<TransactionModel> itemlist) {
        this.mContext = mContext;
        this.itemlist = itemlist;
    }

    @NonNull
    @Override
    public modelviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_transaction_history,parent,false);
        return new modelviewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull modelviewHolder holder, int position) {

        String st_status = itemlist.get(position).getStatus();
        String st_id = itemlist.get(position).getId();
        int balance = itemlist.get(position).getBal();

        holder.tv_status.setText(st_status);
        holder.tv_id.setText("#SR"+st_id);
        holder.tv_bal.setText("₹ "+balance);

       String st_date = getDate(Long.parseLong(st_id));
       holder.tv_date.setText(st_date);

        switch (st_status) {
            case "debited":
                holder.iv_debitCredit.setImageResource(R.drawable.ic_debited);
                break;
            case "credited":
                holder.iv_debitCredit.setImageResource(R.drawable.ic_credited);
                break;
            case "pending":
                holder.iv_debitCredit.setImageResource(R.drawable.ic_pending);
                holder.iv_debitCredit.setRotation(0);
                holder.tv_status.setTextColor(Color.parseColor("#E6420E"));
                break;
        }


    }

    private String getDate(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time*1000);
        Date dat = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        String dates = sdf.format(dat);

        return dates;
    }

    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    public class modelviewHolder extends RecyclerView.ViewHolder {

        ImageView iv_debitCredit;
        TextView tv_status, tv_id, tv_date, tv_bal;


        public modelviewHolder(@NonNull View itemView) {
            super(itemView);

            iv_debitCredit = (ImageView) itemView.findViewById(R.id.iv_debitCredit);
            tv_status = (TextView) itemView.findViewById(R.id.tv_status);
            tv_id = (TextView) itemView.findViewById(R.id.tv_id);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_bal = (TextView) itemView.findViewById(R.id.tv_bal);


        }


    }
}
