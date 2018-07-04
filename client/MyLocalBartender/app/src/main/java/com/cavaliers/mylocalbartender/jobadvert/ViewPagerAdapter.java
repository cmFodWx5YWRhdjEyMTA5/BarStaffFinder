package com.cavaliers.mylocalbartender.jobadvert;

/**
 * Created by SimoneJRSharpe on 13/03/2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by SimoneJRSharpe on 13/03/2017.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments = new ArrayList<>();
    ArrayList<String> tabTitles = new ArrayList<>();


    /**
     *
     * @param fragments
     * @param titles
     */
    public void addFragments(Fragment fragments, String titles){

        this.fragments.add(fragments);
        this.tabTitles.add(titles);

    }


    /**
     *
     * @param fm
     */
    public ViewPagerAdapter(FragmentManager fm){
        super(fm);
    }

    /**
     *
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    /**
     *
     * @return
     */
    @Override
    public int getCount() {
        return fragments.size();
    }

    /**
     *
     * @param position
     * @return
     */
    @Override
    public CharSequence getPageTitle(int position){
        return tabTitles.get(position);
    }
}
