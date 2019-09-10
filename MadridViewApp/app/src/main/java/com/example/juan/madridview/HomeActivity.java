package com.example.juan.madridview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ListenerInterfaces.OnFragmentInteractionListener;
import ListenerInterfaces.OnSearchFavouriteItemListener;
import PubSub.EventNotifier;
import Services.LocationService;
import User.User;
import Utils.AppUtils;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnFragmentInteractionListener,
        OnSearchFavouriteItemListener {

    private final String TAG = "HomeActivity";
    private boolean backButtonPressed = false;

    private Fragment[] fragments;
    private String[] fragmentTAGS;

    private Intent locationServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.homeMenu);
        View headerView = navigationView.getHeaderView(0);

        User user = User.getInstance();
        String userName = user.getUserName(getApplicationContext());
        String mail = user.getMail(getApplicationContext());

        TextView slideMenuUsername = (TextView) headerView.findViewById(R.id.slideMenuUsername);
        TextView slideMenuMail = (TextView) headerView.findViewById(R.id.slideMenuMail);

        slideMenuUsername.setText(userName != null? userName : getResources().getString(R.string.unknownText));
        slideMenuMail.setText(mail != null? mail : getResources().getString(R.string.unknownText));

        navigationView.setNavigationItemSelectedListener(this);

        Utils.StatusBarUtils.makeTraslucent(this);
        Utils.StatusBarUtils.setWhiteStyle(this);

        HomeFragment homeFragment = new HomeFragment();
        ProfileFragment profileFragment = new ProfileFragment();
        FavouriteFragment favouriteFragment = new FavouriteFragment();
        SettingsFragment settingsFragment = new SettingsFragment();

        fragments = new Fragment[]{homeFragment, profileFragment, favouriteFragment, settingsFragment};
        fragmentTAGS = new String[]{"HomeFragment","ProfileFragment","FavouriteFragment","SettingsFragment"};

        if (getSupportFragmentManager().findFragmentByTag(fragmentTAGS[0]) == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.starting_fragment,fragments[0],fragmentTAGS[0]).addToBackStack(fragmentTAGS[0]).commit();
        }

        EventNotifier.subscribe(this, AppUtils.ON_SEARCH_FAVOURITE_ITEM);

    }

    @Override
    public void onBackPressed() {
        //Se pulsa para atrás
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Sí el menú está abierto, cierralo
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //Si no, atras, (Main Activity)
            NavigationView navigationView = (NavigationView) findViewById(R.id.homeMenu);
            //Home
            MenuItem homeItem = navigationView.getMenu().findItem(R.id.nav_home);

            if(homeItem.isChecked()){
                if(backButtonPressed){

                    startService(new Intent(this, LocationService.class));
                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                    homeIntent.addCategory( Intent.CATEGORY_HOME );
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homeIntent);
                    return;
                }

                backButtonPressed = true;
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.backButtonPressedText), Toast.LENGTH_LONG).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backButtonPressed = false;
                    }
                }, 2000);
            }
            else{
                super.onBackPressed();
            }

        }

    }

    @Override
    public void onResume(){
        super.onResume();

        if (AppUtils.checkPermissionsGranted(getApplicationContext()) && locationServiceIntent == null) {

            if(checkPermissions()){
                Log.d(TAG, "Starting location service");
                locationServiceIntent = new Intent(this, LocationService.class);
                startService(locationServiceIntent);

            }
        }
    }

    public boolean checkPermissions() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(locationManager != null){
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                return true;
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Enable Location")
                        .setMessage("Your Locations AppSettings is set to 'Off'.\nPlease Enable Location to use this app")
                        .setPositiveButton("Location AppSettings",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                        Intent myIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivity(myIntent);
                                    }
                                }
                        ).setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                            }
                        }).create().show();
            }
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationServiceIntent != null) {
            stopService(locationServiceIntent);
            locationServiceIntent = null;
            Log.d(TAG, "Stopping location service");
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        if(item.getItemId()==R.id.nav_close_session) {
            closeSession();
        }
        else{
            List<MenuItem> items = new ArrayList<>();

            NavigationView navigationView = (NavigationView) findViewById(R.id.homeMenu);

            for (int i=0; i<navigationView.getMenu().size()-1; i++){
                items.add(navigationView.getMenu().getItem(i));
            }

            changeToFragment(items.indexOf(item));

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private void changeToFragment(int position) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (getSupportFragmentManager().findFragmentByTag(fragmentTAGS[position]) == null) {
            fragmentTransaction.add(R.id.starting_fragment,fragments[position],fragmentTAGS[position]).addToBackStack(fragmentTAGS[position]);
        }

        for(int i=0;i<fragments.length;i++) {
            if(i == position) {
                fragmentTransaction.show(fragments[i]);
            }
            else {

                if (getSupportFragmentManager().findFragmentByTag(fragmentTAGS[i]) != null) {
                    fragmentTransaction.hide(fragments[i]);
                }
            }
        }

        fragmentTransaction.commit();
    }

    private void closeSession(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.cautionText)).setMessage(getResources().getString(R.string.closeSessionDialogMessage));
        builder.setPositiveButton(getResources().getString(R.string.positiveAnswer), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                User user = User.getInstance();

                user.removeActivated(getApplicationContext());
                user.removeDateIn(getApplicationContext());
                user.removeMail(getApplicationContext());
                user.removeUserName(getApplicationContext());
                user.removeVerified(getApplicationContext());

                stopService(new Intent(HomeActivity.this, LocationService.class));

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.negativeAnswer), null);

        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorDialogAnswers));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorDialogAnswers));
            }
        });

        alertDialog.show();


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onSearchFavouriteItem(JSONObject item) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if(getSupportFragmentManager().findFragmentByTag(fragmentTAGS[0])!=null){
            fragmentTransaction.show(fragments[0]);
        }

        if(getSupportFragmentManager().findFragmentByTag(fragmentTAGS[2])!=null){
            fragmentTransaction.hide(fragments[2]);
        }

        fragmentTransaction.commit();
    }
}
