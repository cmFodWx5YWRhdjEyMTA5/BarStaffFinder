package com.cavaliers.mylocalbartender.menu.admin_menu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.user.barstaff.BarStaffProfileFragment;
import com.cavaliers.mylocalbartender.home.review.ReviewPager;
import com.cavaliers.mylocalbartender.home.search.SearchPageFragment;
import com.cavaliers.mylocalbartender.menu.MenuActivity;
import com.cavaliers.mylocalbartender.menu.model.ItemSlideMenu;
import com.cavaliers.mylocalbartender.settings.SettingsPageFragment;


public class AdminBartenderMenuActivity extends MenuActivity {

    @Override
    protected void initMenus() {
        listSliding.add(new ItemSlideMenu(R.drawable.menu_home, "Home"));
        listSliding.add(new ItemSlideMenu(R.drawable.menu_alerts, "Job Alerts"));
        listSliding.add(new ItemSlideMenu(R.drawable.menu_user, "Profile"));
        listSliding.add(new ItemSlideMenu(R.drawable.menu_message, "Message"));
        listSliding.add(new ItemSlideMenu(R.drawable.menu_settings, "Settings"));
        listSliding.add(new ItemSlideMenu(R.drawable.menu_logout, "Logout"));
        listSliding.add(new ItemSlideMenu(R.drawable.menu_switch, "Switch Profile"));
    }

    @Override
    protected void replaceFragment(int pos) {
        Fragment fragment = null;
        switch (pos) {
            case 0:
                // Home
                fragment = SearchPageFragment.newInstance();
                break;
            case 1:
                // Job Alerts
                fragment = ReviewPager.newInstance();
                break;
            case 2:
                // BarStaff Profile
                fragment = new BarStaffProfileFragment();
                break;
            case 3:
                //Message
                //Toast.makeText(getActivity(), "notifications :", Toast.LENGTH_LONG).show();
//                fragment = new ChatServer();
                break;
            case 4:
                // Settings
                //Toast.makeText(getActivity(), "notifications :", Toast.LENGTH_LONG).show();
                fragment = new SettingsPageFragment();
                break;
            case 5:
                // Logout
                displayLogout();
                break;

            case 6:
                // Switch to Organiser profile
                // displayLogout();
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


