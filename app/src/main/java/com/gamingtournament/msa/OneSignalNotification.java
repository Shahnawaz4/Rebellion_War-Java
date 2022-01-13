package com.gamingtournament.msa;

import android.app.Application;

import com.onesignal.OneSignal;

public class OneSignalNotification extends Application {

    public static final String ONESIGNAL_APP_ID = "b1887c02-df7f-48cc-90b3-3e3da1ef65e1";

    @Override
    public void onCreate() {
        super.onCreate();

        com.onesignal.OneSignal.initWithContext(this);
        OneSignal.setNotificationOpenedHandler(new OneSignalOpenHandler(getApplicationContext()));
        OneSignal.setAppId(ONESIGNAL_APP_ID);

    }
}
