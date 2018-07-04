package com.cavaliers.mylocalbartender.menu;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cavaliers.mylocalbartender.MyLocalBartender;
import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.activity.MLBActivity;
import com.cavaliers.mylocalbartender.home.review.ReviewPager;
import com.cavaliers.mylocalbartender.logout.Logout;
import com.cavaliers.mylocalbartender.menu.adapter.SlidingMenuAdapter;
import com.cavaliers.mylocalbartender.menu.model.ItemSlideMenu;
import com.cavaliers.mylocalbartender.tools.cavs_widgets.date_picker.CavsDatePicker;

import java.util.ArrayList;
import java.util.List;

/**
 * Team Cavaliers
 */
public abstract class MenuActivity extends MLBActivity {

    private View popupView;
    private int backButtonCount = 0;
    private Logout logoutPopup;
    private AlertDialog logoutDiaglog;
    protected List<ItemSlideMenu> listSliding;
    private ListView listViewSliding;
    private DrawerLayout drawerLayout;
    private RelativeLayout mainContent;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    protected int page = 0;
    protected ReviewPager reviewPager;

    protected abstract void initMenus();

    protected abstract void replaceFragment(int post);

    private void setUpPages(int pos) {
        setTitle(listSliding.get(pos).getTitle());
        listViewSliding.setItemChecked(pos, true);
        drawerLayout.closeDrawer(listViewSliding);
        replaceFragment(pos);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bartender_menu);

        listViewSliding = (ListView) findViewById(R.id.lv_sliding_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mainContent = (RelativeLayout) findViewById(R.id.main_content);

        listSliding = new ArrayList<>();

        initMenus();

        SlidingMenuAdapter adapter = new SlidingMenuAdapter(this, listSliding);

        listViewSliding.setAdapter(adapter);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeButtonEnabled(true);

        // HERE IS WHERE I WILL REPLACE THE SIGN IN AND SIGN UP BUTTONS WHAT
        // DIRECTS THE USER TO THE SPECIFIC PAGE =========================
        setUpPages(0);

        listViewSliding.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setUpPages(position);
            }
        });

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_opened, R.string.drawer_closed) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        // makes the menu thing spin around
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

    }

    protected void displayLogout() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MenuActivity.this);
        popupView = getLayoutInflater().inflate(R.layout.dialog_logout, null);
        alertBuilder.setView(popupView);
        this.logoutDiaglog = alertBuilder.create();

        logoutPopup = new Logout(this.logoutDiaglog, this.popupView, this);
        MenuActivity.this.logoutDiaglog.dismiss();
        MenuActivity.this.logoutDiaglog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }


    public void showDatePickerDialog(View v) {
        new CavsDatePicker(this, (EditText) v);
    }



    @Override
    public void onBackPressed() {
        if (MyLocalBartender.state == MyLocalBartender.DEFAULT_PROFILE) {
            Intent menu = new Intent(this, MyLocalBartender.class);
            startActivity(menu);
        } else {
            if (backButtonCount >= 1) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                backButtonCount = 0;
            } else {
                Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
                backButtonCount++;
            }
        }
    }
}



