package com.cavaliers.mylocalbartender.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.activity.MLBActivity;
import com.cavaliers.mylocalbartender.home.review.AnswerFragment;
import com.cavaliers.mylocalbartender.home.review.RequestFragment;
import com.cavaliers.mylocalbartender.home.search.SearchPageFragment;
import com.cavaliers.mylocalbartender.menu.organiser_menu.OrganiserMenuActivity;
import com.cavaliers.mylocalbartender.tools.access.AccessVerificationTools;

import java.util.List;

public class TestActivity extends MLBActivity {


    private TabHost tabHost;
    private ViewPager viewPager;
    private PagerAdaptero pagerAdapter;
    int i = 0;
    private View v;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Button pager = (Button)findViewById(R.id.pager);
        Button org_home = (Button)findViewById(R.id.org_homer);
        Button staff_home = (Button)findViewById(R.id.staff_homer);


        organiserHome();
        homeOrganiserTesting();
        homeStaffTesting();
        startFullJob();
    }


    public void organiserHome(){
        Button page = (Button)findViewById(R.id.pager);
        page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(),OrganiserMenuActivity.class);
                startActivity(intent);
            }
        });
    }

    public void startFullJob(){
        Button page = (Button)findViewById(R.id.job_full);
        page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    frameLayout.setVisibility(View.VISIBLE);


//                FragmentManager fragmentManager = getSupportFragmentManager();
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
//                transaction.replace(R.id.framer,new JobDescriptionFragment());
//                transaction.addToBackStack(null);
//                transaction.commit();
            }
        });
    }




    private void homeOrganiserTesting(){
        Button homeTest = (Button) findViewById(R.id.org_homer);
        homeTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccessVerificationTools.setProfile("organiser");
                startHomePage();
            }
        });
    }

    private void homeStaffTesting(){
        Button homeTest = (Button) findViewById(R.id.staff_homer);
        homeTest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AccessVerificationTools.setProfile("staff");
                startHomePage();
            }
        });
    }


    //START---------------------------------HOME PAGE-----

    public void startHomePage(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.framer,new SearchPageFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // ---------------------------------HOME PAGE-----END+

    /**
     * Generates new Intent holding a given Activity
     *
     * @param activityClass
     */
    private void startActivity(Class activityClass){
        Intent menu = new Intent(this, activityClass);
        this.startActivity(menu);
    }

    public void getIt(View v){
        Toast.makeText(this, "IT WORKED", Toast.LENGTH_SHORT).show();

    }


}

class PagerAdaptero extends FragmentPagerAdapter {

    List<Fragment> fragments;

    public PagerAdaptero(FragmentManager fm) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag;
        if(position==0) frag = new AnswerFragment();
        else frag = new RequestFragment();

        return frag;
    }

    @Override
    public int getCount() {
        return 2;
    }





}

