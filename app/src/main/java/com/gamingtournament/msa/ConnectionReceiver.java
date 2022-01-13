package com.gamingtournament.msa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionReceiver extends BroadcastReceiver {
    // initialize
    public static ReceiverListener listener;

    @Override
    public void onReceive(Context context, Intent intent) {
        // initialize connectivity manager
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (listener!=null){
            boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

            listener.onNetworkChange(isConnected);
        }
    }

    public interface ReceiverListener{

        // create method
        void onNetworkChange(boolean isConnected);
    }
}
