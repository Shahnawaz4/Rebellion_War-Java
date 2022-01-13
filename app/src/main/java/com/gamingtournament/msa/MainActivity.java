package com.gamingtournament.msa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.gamingtournament.msa.fragments.HomeFragment;
import com.gamingtournament.msa.fragments.MyContestFragment;
import com.gamingtournament.msa.fragments.ProfileFragment;
import com.gamingtournament.msa.fragments.PaymentFragment;
import com.gamingtournament.msa.model.UpdateModel;
import com.gamingtournament.msa.model.UserInformation;
import com.gamingtournament.msa.more.ContactActivity;
import com.gamingtournament.msa.more.HelpSupportActivity;
import com.gamingtournament.msa.more.PrivacyPolicyActivity;
import com.gamingtournament.msa.payment.AddMoneyActivity;
import com.gamingtournament.msa.utils.UtilValues;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements ConnectionReceiver.ReceiverListener {

    private DrawerLayout drawerLayout;
    private TextView tv_header_username, tv_header_email;
    private CircleImageView iv_header_profile;
    NavigationView navigationView;
    TextView tv_app_version;
    MeowBottomNavigation meo;

    private String uid;
    private DatabaseReference db_name;
//    private Dialog dialog;
    private boolean exit;

    private String msg;
    private boolean isman;
    private String ver;

    private final static int ID_HOME = 1;
    private final static int ID_MYCONTEST = 2;
    private final static int ID_PROFILE = 3;
    private final static int ID_PAYMENT = 4;


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_app_version = findViewById(R.id.tv_app_version);

        // set app version in navigation drawer layout
        String versionName = BuildConfig.VERSION_NAME;
        tv_app_version.setText("App Version " + versionName);

//        dialog = new Dialog(MainActivity.this, R.style.AlertDialogTheme);
//        dialog.setContentView(R.layout.pb_custom_layout);
//        dialog.setCanceledOnTouchOutside(false);
//        if (dialog.getWindow() != null) {
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//        }
//        dialog.show();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationMethod();
        bottomNavigation();

        // check internet connection
        checkConnection();

        checkUpdate();

    }
    // check internet connection
    public void checkConnection() {
        // initialize intent filter
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        registerReceiver(new ConnectionReceiver(), intentFilter);

        ConnectionReceiver.listener = this;

        ConnectivityManager manager = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        showSnackBar(isConnected);
    }

    private void showSnackBar(boolean isConnected) {
        String msg;
        int color;

        Snackbar snackbar = Snackbar.make(findViewById(R.id.tv_app_version),
                "No internet! check connection", Snackbar.LENGTH_LONG);

        View view = snackbar.getView();

        TextView textView = view.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        if (isConnected) {

        } else {
            snackbar.show();
        }


    }

    private void navigationMethod() {

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        View header = navigationView.getHeaderView(0);
        tv_header_username = (TextView) header.findViewById(R.id.tv_header_userName);
        tv_header_email = (TextView) header.findViewById(R.id.tv_header_email);
        iv_header_profile = (CircleImageView) header.findViewById(R.id.iv_header_profile);

        tv_header_username.setText(R.string.app_name);
        tv_header_email.setText("example123@gmail.com");

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db_name = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        db_name.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserInformation information = new UserInformation();
                information.setName(snapshot.getValue(UserInformation.class).getName());
                information.setEmail(snapshot.getValue(UserInformation.class).getEmail());
                information.setImgurl(snapshot.getValue(UserInformation.class).getImgurl());

                tv_header_username.setText(information.getName());
                tv_header_email.setText(information.getEmail());

                String st_imageUrl = information.getImgurl();
                if (st_imageUrl != null && !st_imageUrl.equals("")) {
                    Picasso.get()
                            .load(st_imageUrl)
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .into(iv_header_profile);
                }
//                if (dialog.isShowing()) {
//                    dialog.dismiss();
//                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                if (dialog.isShowing()) {
//                    dialog.dismiss();
//                }
                Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        // for right side arrow icon in drawer layout
        navigationView.getMenu().getItem(0).setActionView(R.layout.nav_action_layout);
        navigationView.getMenu().getItem(1).setActionView(R.layout.nav_action_layout);
        navigationView.getMenu().getItem(2).setActionView(R.layout.nav_action_layout);
        navigationView.getMenu().getItem(3).setActionView(R.layout.nav_action_layout);
        navigationView.getMenu().getItem(4).setActionView(R.layout.nav_action_layout);
        navigationView.getMenu().getItem(5).setActionView(R.layout.nav_action_layout);
        navigationView.getMenu().getItem(6).setActionView(R.layout.nav_action_layout);
        navigationView.getMenu().getItem(7).setActionView(R.layout.nav_action_layout);
        navigationView.getMenu().getItem(8).setActionView(R.layout.nav_action_layout);
        navigationView.getMenu().getItem(9).setActionView(R.layout.nav_action_layout);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                drawerLayout.closeDrawer(GravityCompat.START);

                switch (id) {
                    case R.id.nav_privacy:
                        Intent p = new Intent(MainActivity.this, PrivacyPolicyActivity.class);
                        p.putExtra("data", 1);
                        startActivity(p);
                        break;
                    case R.id.nav_refund:
                        Intent r = new Intent(MainActivity.this, PrivacyPolicyActivity.class);
                        r.putExtra("data", 2);
                        startActivity(r);
                        break;
                    case R.id.nav_aboutus:
                        Intent a = new Intent(MainActivity.this, PrivacyPolicyActivity.class);
                        a.putExtra("data", 3);
                        startActivity(a);
                        break;
                    case R.id.nav_contactus:
                        startActivity(new Intent(MainActivity.this, ContactActivity.class));
//                        Intent cont = new Intent(MainActivity.this, PrivacyPolicyActivity.class);
//                        cont.putExtra("data", 4);
//                        startActivity(cont);
                        break;
                    case R.id.nav_rules:
                        Intent rules = new Intent(MainActivity.this, PrivacyPolicyActivity.class);
                        rules.putExtra("data", 5);
                        startActivity(rules);
                        break;
                    case R.id.nav_term:
                        Intent term = new Intent(MainActivity.this, PrivacyPolicyActivity.class);
                        term.putExtra("data", 6);
                        startActivity(term);
                        break;
                    case R.id.nav_faqs:
                        Intent faq = new Intent(MainActivity.this, PrivacyPolicyActivity.class);
                        faq.putExtra("data", 7);
                        startActivity(faq);
                        break;
                    case R.id.nav_howtoplay:
                        Intent play = new Intent(MainActivity.this, PrivacyPolicyActivity.class);
                        play.putExtra("data", 8);
                        startActivity(play);
                        break;
                    case R.id.nav_help_support:
                        startActivity(new Intent(MainActivity.this, HelpSupportActivity.class));
                        break;
                    case R.id.nav_logout:
                        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                        editor = sharedPreferences.edit();
                        editor.putBoolean("savelogin", false);
                        editor.apply();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });

    }

    private void bottomNavigation() {

        meo = findViewById(R.id.bottom_nav);
        meo.add(new MeowBottomNavigation.Model(1, R.drawable.ic_home));
        meo.add(new MeowBottomNavigation.Model(2, R.drawable.ic_mycontest));
        meo.add(new MeowBottomNavigation.Model(3, R.drawable.ic_profile));
        meo.add(new MeowBottomNavigation.Model(4, R.drawable.ic_payment));

        // to already ic_home fragment active
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        // which bottom navigation clicked
        meo.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                //      Toast.makeText(getApplicationContext(),"clicked item " + item.getId(), Toast.LENGTH_SHORT).show();

            }
        });

        meo.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {

            }
        });

        // changing the fragment on bottom navigation clicked
        meo.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                Fragment fragment_selected = null;
                switch (item.getId()) {
                    case ID_HOME:
                        fragment_selected = new HomeFragment();
                        getSupportActionBar().setTitle(R.string.app_name);
                        break;
                    case ID_MYCONTEST:
                        fragment_selected = new MyContestFragment();
                        getSupportActionBar().setTitle("My Contest");
                        break;
                    case ID_PROFILE:
                        fragment_selected = new ProfileFragment();
                        getSupportActionBar().setTitle("Profile");
                        break;
                    case ID_PAYMENT:
                        fragment_selected = new PaymentFragment();
                        getSupportActionBar().setTitle("Payment");
                        break;

                }
                assert fragment_selected != null;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment_selected).commit();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option, menu);

        MenuItem menuItem = menu.getItem(0);

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UserInformation uip = new UserInformation();

                uip.setBalance(dataSnapshot.getValue(UserInformation.class).getBalance());

                int balance = uip.getBalance();
                menuItem.setTitle("₹ " + balance);
//                if (dialog.isShowing()) {
//                    dialog.dismiss();
//
//                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                if (dialog.isShowing()) {
//                    dialog.dismiss();
//                }
                Toast.makeText(MainActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


//        return super.onCreateOptionsMenu(menu);

        return true;

    }

    private void checkUpdate() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("update");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UpdateModel um = new UpdateModel();
                um.setMsg(dataSnapshot.getValue(UpdateModel.class).getMsg());
                um.setVer(dataSnapshot.getValue(UpdateModel.class).getVer());
                um.setIsman(dataSnapshot.getValue(UpdateModel.class).isIsman());

                msg = um.getMsg();
                ver = um.getVer();
                isman = um.isIsman();

                String versionName = BuildConfig.VERSION_NAME;
                if (!ver.equalsIgnoreCase("wr-"+versionName)){
                    Dialog dialog = new Dialog(MainActivity.this, R.style.AlertDialogTheme);
                    dialog.setContentView(R.layout.custom_dialog_update);
                    dialog.setCanceledOnTouchOutside(false);
                    if (dialog.getWindow() != null) {
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    }

                    TextView tv_updateMsg = dialog.findViewById(R.id.tv_updateMsg);
                    TextView tv_cancel = dialog.findViewById(R.id.tv_cancel);
                    Button btn_save = dialog.findViewById(R.id.btn_save);

                    tv_updateMsg.setText(msg);

                    if (isman){
                        tv_cancel.setVisibility(View.GONE);
                    }

                    tv_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    btn_save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try {

                                Intent updateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(UtilValues.WEB_URL));
                                startActivity(updateIntent);

                            }catch (ActivityNotFoundException e){
                                Toast.makeText(MainActivity.this, "Error: Browser App Not Found", Toast.LENGTH_LONG).show();
                            }

                            dialog.dismiss();


                        }
                    });

                    dialog.show();

                } else {
                    // No update found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                if (dialog.isShowing()) {
//                    dialog.dismiss();
//                }
                Toast.makeText(MainActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });




    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.menu_balance) {
            startActivity(new Intent(MainActivity.this, AddMoneyActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {

            if (exit) {
                finish();
            } else {
                Toast.makeText(MainActivity.this, "Press again to Exit", Toast.LENGTH_SHORT).show();
            }
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    exit = false;

                }
            }, 2000);

            exit = true;
            //super.onBackPressed();

        }
    }

    // check connection
    @Override
    public void onNetworkChange(boolean isConnected) {
        showSnackBar(isConnected);
    }

    // check connection
    @Override
    protected void onResume() {
        super.onResume();

        checkConnection();

    }

    // check connection
    @Override
    protected void onPause() {
        super.onPause();

        checkConnection();

    }
}