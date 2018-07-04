package com.cavaliers.mylocalbartender.home.search.handlers;

/**
 * Created by JamesRich on 04/03/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.home.review.adapter.AnswerAdapter;
import com.cavaliers.mylocalbartender.jobadvert.privatejobs.PrivateJobsActivity;


public class InviteButtonHandler implements View.OnClickListener {

    private Button hireButton;
    private Context context;


    private ImageView ic_profile_thumbnail;
    private TextView tv_ad_title;
    private TextView tv_ad_client;
    private TextView tv_ad_payrate;
    private TextView tv_ad_date;
    private TextView tv_ad_description;
    private TextView tv_ad_location;
    private RecyclerView recyclerView;
    private AnswerAdapter answerAdapter;



    public InviteButtonHandler(Button hireButton, Context context){
        this.hireButton = hireButton;
        this.context = context;
    }

    public InviteButtonHandler(ImageView thumbnail, TextView title, TextView client, TextView payrate, TextView date, TextView location, TextView description){
        this.ic_profile_thumbnail = thumbnail;
        this.tv_ad_title = title;
        this.tv_ad_client = client;
        this.tv_ad_payrate = payrate;
        this.tv_ad_date = date;
        this.tv_ad_location = location;
        this.tv_ad_description = description;
    }


    public void onClick(View view) {

        PopupMenu popupMenu = new PopupMenu(context,hireButton);
        popupMenu.getMenuInflater().inflate(R.menu.hire_menu,popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId() == R.id.hire_item1){
                    System.out.println("NEW JOB");
                }
                else if(item.getItemId() == R.id.hire_item2){
                    System.out.println("GET A JOB LIST");
                    Intent intent = new Intent(new Intent(context,PrivateJobsActivity.class));
                    intent.putExtra("STAFF_ID",hireButton.getTag().toString());
                    context.startActivity(intent);

                }

                return false;
            }
        });
        popupMenu.show();



    }

}
