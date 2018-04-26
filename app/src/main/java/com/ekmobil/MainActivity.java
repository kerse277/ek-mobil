package com.ekmobil;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.ekmobil.fragment.AddressFragment;
import com.ekmobil.fragment.AppointmentServiceFragment;
import com.ekmobil.fragment.CameraFragment;
import com.ekmobil.fragment.CampainsFragment;
import com.ekmobil.fragment.FavoriteFragment;
import com.ekmobil.fragment.HairAnalysisFragment;
import com.ekmobil.fragment.HairSecretBlogFragment;
import com.ekmobil.fragment.HelpFragment;
import com.ekmobil.fragment.InboxFragment;
import com.ekmobil.fragment.MainPageFragment;
import com.ekmobil.fragment.OnlineShopFragment;
import com.ekmobil.fragment.PersonelFragment;
import com.ekmobil.fragment.PriceListFragment;
import com.ekmobil.fragment.ProfileFragment;
import com.ekmobil.utility.ProjectUtility;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, TabLayout.OnTabSelectedListener {

    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private Button btn_logout, btn_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        defineComponents();

        if (savedInstanceState == null)
            changeFragment(new MainPageFragment());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_inbox, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.inbox:
                changeFragment(new InboxFragment());
                break;
        }
        return true;
    }


    private void defineComponents() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(null);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.ic_menu);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_main_page);
        navigationView.setNavigationItemSelectedListener(this);

        TabLayout tabLayout = findViewById(R.id.activity_main_tab_layout);
        tabLayout.setBackgroundColor(Color.parseColor("#fafafa"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_magaza_nonselected));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_appointment_nonselected));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_camera_nonselected));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_favori_nonselected));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_profile_non_selected));

        tabLayout.getTabAt(0).select();
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        tabLayout.addOnTabSelectedListener(this);

        btn_logout = findViewById(R.id.nav_logout);
        btn_settings = findViewById(R.id.nav_settings);

        btn_logout.setOnClickListener(this);
        btn_settings.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fm = getFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
            } else {
                DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.finish();
                    }
                };

                ProjectUtility.showDialog(MainActivity.this, getString(R.string.messages_info), getString(R.string.are_you_sure_to_logout), getString(R.string.messages_no), getString(R.string.messages_yes), null, okListener);
            }
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_main_page) {
            changeFragment(new MainPageFragment());
        } else if (id == R.id.nav_online_shop) {
            changeFragment(new OnlineShopFragment());
        } else if (id == R.id.nav_appointment) {
            changeFragment(new AppointmentServiceFragment());
        } else if (id == R.id.nav_price_list) {
            changeFragment(new PriceListFragment());
        } else if (id == R.id.nav_campains) {
            changeFragment(new CampainsFragment());
        } else if (id == R.id.nav_hair_analysis) {
            changeFragment(new HairAnalysisFragment());
        } else if (id == R.id.nav_hair_secrets_blog) {
            changeFragment(new HairSecretBlogFragment());
        } else if (id == R.id.nav_address) {
            changeFragment(new AddressFragment());
        } else if (id == R.id.nav_profile) {
            changeFragment(new ProfileFragment());
        } else if (id == R.id.nav_personel) {
            changeFragment(new PersonelFragment());
        } else {
            changeFragment(new HelpFragment());
            //Help Açılacak
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void changeFragment(Fragment fragment) {
        getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_layout, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commitAllowingStateLoss();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_settings:
                break;
            case R.id.nav_logout:
                DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.finish();
                    }
                };

                ProjectUtility.showDialog(MainActivity.this, getString(R.string.messages_info), getString(R.string.are_you_sure_to_logout), getString(R.string.messages_no), getString(R.string.messages_yes), null, okListener);

                break;
        }
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.e("tabselected", "onTabReselected: " + tab.getPosition());
        switch (tab.getPosition()) {
            case 0:
                tab.setIcon(R.drawable.ic_magaza_selected);
                changeFragment(new OnlineShopFragment());
                break;
            case 1:
                tab.setIcon(R.drawable.ic_appointment_selected);
                changeFragment(new AppointmentServiceFragment());
                break;
            case 2:
                tab.setIcon(R.drawable.ic_camera_selected);
                changeFragment(new CameraFragment());
                break;
            case 3:
                tab.setIcon(R.drawable.ic_favori_selected);
                changeFragment(new FavoriteFragment());
                break;
            case 4:
                tab.setIcon(R.drawable.ic_profile_selected);
                changeFragment(new ProfileFragment());
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                tab.setIcon(R.drawable.ic_magaza_nonselected);
                break;
            case 1:
                tab.setIcon(R.drawable.ic_appointment_nonselected);
                break;
            case 2:
                tab.setIcon(R.drawable.ic_camera_nonselected);
                break;
            case 3:
                tab.setIcon(R.drawable.ic_favori_nonselected);
                break;
            case 4:
                tab.setIcon(R.drawable.ic_profile_non_selected);
                break;
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        Log.e("tabselected", "onTabReselected: " + tab.getPosition());
        switch (tab.getPosition()) {
            case 0:
                changeFragment(new OnlineShopFragment());
                break;
            case 1:
                changeFragment(new AppointmentServiceFragment());
                break;
            case 2:
                changeFragment(new CameraFragment());
                break;
            case 3:
                changeFragment(new FavoriteFragment());
                break;
            case 4:
                changeFragment(new ProfileFragment());
                break;
        }
    }
}
