package com.cavaliers.mylocalbartender.menu.admin_menu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.home.review.ReviewPager;
import com.cavaliers.mylocalbartender.home.search.SearchPageFragment;
import com.cavaliers.mylocalbartender.jobadvert.JobAdvertFragment;
import com.cavaliers.mylocalbartender.menu.MenuActivity;
import com.cavaliers.mylocalbartender.menu.model.ItemSlideMenu;
import com.cavaliers.mylocalbartender.user.organiser.OrganiserProfileFragment;

import com.cavaliers.mylocalbartender.settings.SettingsPageFragment;


public class AdminOrganiserMenuActivity extends MenuActivity {

    @Override
    protected void initMenus() {
        listSliding.add(new ItemSlideMenu(R.drawable.menu_home, "Home"));
        listSliding.add(new ItemSlideMenu(R.drawable.menu_alerts, "Job Alerts"));
        listSliding.add(new ItemSlideMenu(R.drawable.menu_user, "Profile"));
        listSliding.add(new ItemSlideMenu(R.drawable.menu_message, "Messages"));
        listSliding.add(new ItemSlideMenu(R.drawable.menu_pencil, "Job Adverts"));
        listSliding.add(new ItemSlideMenu(R.drawable.menu_settings, "Settings"));
        listSliding.add(new ItemSlideMenu(R.drawable.menu_logout, "Logout"));
        listSliding.add(new ItemSlideMenu(R.drawable.menu_switch, "Switch Profile"));
    }

    @Override
    protected void replaceFragment(int pos) {
        Fragment fragment = null;
        switch (pos) {

            // Home
            case 0:
                fragment = SearchPageFragment.newInstance();
                break;

            // Job Alerts
            case 1:
                fragment = ReviewPager.newInstance();
                break;

            // Profile
            case 2:
                fragment = new OrganiserProfileFragment();
                break;

            // Chat
//            case 3:
//
//                fragment = new ChatServer();
//                break;

            // Job Advert
            case 4:
                fragment = new JobAdvertFragment();
                break;

            // Settings
            case 5:
                fragment = new SettingsPageFragment();
                break;

            // Logout
            case 6:
                displayLogout();
                break;

            // Switch to Bartender Profile
            case 7:
                //displayLogout();
                break;

            default:
                fragment = new Fragment();
                break;
        }

        if (null != fragment) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_content, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }
    }


}


