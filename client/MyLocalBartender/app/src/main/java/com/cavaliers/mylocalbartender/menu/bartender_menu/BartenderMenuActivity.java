package com.cavaliers.mylocalbartender.menu.bartender_menu;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.home.review.ReviewPager;
import com.cavaliers.mylocalbartender.home.search.SearchActivity;
import com.cavaliers.mylocalbartender.home.search.SearchPageFragment;
import com.cavaliers.mylocalbartender.menu.MenuActivity;
import com.cavaliers.mylocalbartender.menu.model.ItemSlideMenu;
import com.cavaliers.mylocalbartender.messaging.MessagesFragment;
import com.cavaliers.mylocalbartender.settings.SettingsPageFragment;
import com.cavaliers.mylocalbartender.user.barstaff.BarStaffProfileFragment;

public class BartenderMenuActivity extends MenuActivity {

    MenuItem item;

    @Override
    protected void initMenus() {
        listSliding.add(new ItemSlideMenu(R.drawable.menu_home, "Home"));
        listSliding.add(new ItemSlideMenu(R.drawable.menu_alerts, "Job Alerts"));
        listSliding.add(new ItemSlideMenu(R.drawable.menu_user, "Profile"));
        listSliding.add(new ItemSlideMenu(R.drawable.menu_message, "Message"));
        listSliding.add(new ItemSlideMenu(R.drawable.menu_settings, "Settings"));
        listSliding.add(new ItemSlideMenu(R.drawable.menu_logout, "Logout"));
    }

    @Override
    protected void replaceFragment(int pos) {
        Fragment fragment = null;
        switch (pos) {
            case 0:
                // Home
                page = 0;
                fragment = SearchPageFragment.newInstance();
                break;
            case 1:
                page = 1;
                fragment = ReviewPager.newInstance();
                // Job
                break;
            case 2:
                // BarStaff Profile
                page = 2;
                fragment = new BarStaffProfileFragment();
                break;
            case 3:
                //Message
                page = 3;
                //Toast.makeText(getActivity(), "notifications :", Toast.LENGTH_LONG).show();
                fragment = new MessagesFragment();
                break;
            case 4:
                // Settings
                //Toast.makeText(getActivity(), "notifications :", Toast.LENGTH_LONG).show();
                page = 4;
                fragment = new SettingsPageFragment();
                break;
            case 5:
                // Logout
                page = 5;
                displayLogout();
                break;
            default:
                fragment = new Fragment();
                break;
        }

        if (page >= 5) return;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (page == 0) {
            item = menu.add(Menu.NONE, R.id.srch, 10, "");
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            item.setIcon(R.drawable.search);

            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent intent = new Intent(new Intent(getBaseContext(), SearchActivity.class));
                    getBaseContext().startActivity(intent);
                    System.out.println("SEARCHED");
                    return false;
                }
            });
        } else item.setVisible(false);
        return true;
    }
}


