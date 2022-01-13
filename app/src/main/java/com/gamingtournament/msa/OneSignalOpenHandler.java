package com.gamingtournament.msa;

import android.content.Context;
import android.content.Intent;

import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

public class OneSignalOpenHandler implements OneSignal.OSNotificationOpenedHandler {

    private Context mContext;

    public OneSignalOpenHandler(Context mContext){
        this.mContext = mContext;
    }

    @Override
    public void notificationOpened(OSNotificationOpenedResult result) {
        // this method is fired when a notification is clicked
        
        String title = result.getNotification().getTitle();
        String desc = result.getNotification().getBody();

        Intent intent = new Intent(mContext, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("title", title);
        intent.putExtra("desc", desc);
        mContext.startActivity(intent);

    }
}
