package com.cavaliers.mylocalbartender.home.search;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cavaliers.mylocalbartender.MyLocalBartender;
import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.fragment.MLBFragment;
import com.cavaliers.mylocalbartender.home.search.adapter.AdvertAdapter;
import com.cavaliers.mylocalbartender.home.search.helper.FilterHelper;
import com.cavaliers.mylocalbartender.home.search.model.AdvertData;
import com.cavaliers.mylocalbartender.menu.bartender_menu.BartenderMenuActivity;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.server.SocketIO;
import com.cavaliers.mylocalbartender.tools.Tools;
import com.cavaliers.mylocalbartender.tools.access.AccessVerificationTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchPageFragment extends MLBFragment {

    private View view;
    private RecyclerView rc_recycler;
    private AdvertAdapter advertAdapter;
    private SearchView sv_searcher;
    private LinearLayoutManager linearLayoutManager;
    private BartenderMenuActivity bartenderMain;

    //Advanced Filter
    private CheckBox checkbox_birthday;
    private CheckBox checkbox_wedding;
    private CheckBox checkbox_dinnerparty;
    private CheckBox checkbox_other;
    private CheckBox[] checkboxArray = new CheckBox[4];
    private ArrayList<String> selectedCheckbox;
    private EditText location_input;
    private EditText distance_input;
    private SeekBar skb_payrate;
    private TextView minimum_pay_display;

    private EditText dayDate;
    private TextView date_divider1;
    private EditText monthDate;
    private TextView date_divider2;
    private EditText yearDate;

    private Button btn_apply;
    private Button btn_reset;
    private String inputValue = "";
    public static Handler handler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        MLBService.getResponses(new MLBService.MLBDatabaseActivityEventListener() {
            @Override
            public void onResponse(JSONArray response) {

                if(response != null) {

                    for (int i = 0; i < response.length(); ++i) {

                        try {
                            AdvertData.addAdvert(response.getJSONObject(i), AdvertData.TYPE.ADVERTS);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    advertAdapter.notifyDataSetChanged();
                }
            }
        });






        MLBService.setSocketEvent(new SocketIO.BarStaffJoinedListener() {

            @Override
            public void onResponse(JSONObject object) {

                AdvertData.addAdvert(object, AdvertData.TYPE.ADVERTS);
                advertAdapter.notifyDataSetChanged();
            }
        });


        MLBService.setSocketEvent(new SocketIO.JobCreatedListener() {
              @Override
              public void onResponse(JSONObject object) {
                  AdvertData.addAdvert(object, AdvertData.TYPE.ADVERTS);
                  advertAdapter.notifyDataSetChanged();
                  Toast.makeText(getContext(), "Job Posted", Toast.LENGTH_LONG).show();
              }
        });


                MLBService.getResponses(new MLBService.MLBDatabaseActivityEventListener() {

                    @Override
                    public void onResponse(JSONArray response) {
                        if (response != null) {
                            for (int i = 0; i < response.length(); ++i) {
                                try {
                                    AdvertData.addJobAdvert(response.getJSONObject(i), AdvertData.TYPE.ADVERTS);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            advertAdapter.notifyDataSetChanged();
                        }
                    }
                });



//        MLBService.getResponses(new MLBService.MLBDatabaseActivityEventListener() {
//
//            @Override
//            public void onResponse(JSONArray response) {
//                if(response != null) {
//                    for (int i = 0; i < response.length(); ++i) {
//                        try {
//                            AdvertData.addJobAdvert(response.getJSONObject(i), AdvertData.TYPE.ADVERTS);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    advertAdapter.notifyDataSetChanged();
//                }
//            }
//        });





        //initialixe adapter with data from model
        //will crash if you got no data from the server

        if(MyLocalBartender.state == MyLocalBartender.DEFAULT_PROFILE) {

            AccessVerificationTools.setProfile("staff");
            advertAdapter = new AdvertAdapter(AdvertData.getAdverts(), this.getActivity(), this);

        }else if(Tools.accountType == MLBService.AccountType.BARSTAFF) {


            view = inflater.inflate(R.layout.fragment_filter_drag,container,false);


            initAdvancedFilterWidgit();
            setApplyButtonListener();
            setResetButtonListener();

        }else if(Tools.accountType == MLBService.AccountType.ORGANISER) {
            view = inflater.inflate(R.layout.activity_search_page,container,false);
            Toolbar toolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
            toolbar.setVisibility(View.GONE);
        }



        sv_searcher = (SearchView)view.findViewById(R.id.search_bar);
        rc_recycler = (RecyclerView)view.findViewById(R.id.recyclerView);

        handler = new Handler(view.getContext().getMainLooper());

        sv_searcher.setIconifiedByDefault(true);
        sv_searcher.setFocusable(false);

        sv_searcher.setImeOptions(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        sv_searcher.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                System.out.println("working");
                advertAdapter.notifyDataSetChanged();
                inputValue = "";
                AdvertAdapter.resetAdvertList();
                advertAdapter.getFilter().filter(inputValue);
                sv_searcher.onActionViewCollapsed();
                return true;
            }
        });

//

        linearLayoutManager = new LinearLayoutManager(getActivity());
        rc_recycler.setLayoutManager(linearLayoutManager);

        advertAdapter = new AdvertAdapter(AdvertData.getAdverts(), this.getActivity(), this);

        rc_recycler.setAdapter(advertAdapter);






        //checks content of search filed and runs FILTERING
        sv_searcher.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getActivity(), "I'M READING="+query, Toast.LENGTH_SHORT).show();
                advertAdapter.getFilter().filter(query);
                inputValue = query;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //TODO: implement most recent searches suggestion
                return false;
            }
        });

        return view;
    }

    public LinearLayoutManager getLayoutManager(){
        return linearLayoutManager;
    }


    public static void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    public static SearchPageFragment newInstance() {
        SearchPageFragment fragment = new SearchPageFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    private void initAdvancedFilterWidgit(){

        checkboxArray[0] = checkbox_birthday = (CheckBox)view.findViewById(R.id.chk_birthday_checkbox);
        checkboxArray[1] = checkbox_wedding = (CheckBox)view.findViewById(R.id.chk_wedding_checkbox);
        checkboxArray[2] = checkbox_dinnerparty = (CheckBox)view.findViewById(R.id.chk_dinner_party_checkbox);
        checkboxArray[3] = checkbox_other = (CheckBox)view.findViewById(R.id.chk_other_checkbox);

        location_input = (EditText) view.findViewById(R.id.et_location_input);
        distance_input = (EditText) view.findViewById(R.id.et_distance_input);

        skb_payrate = (SeekBar) view.findViewById(R.id.sb_payrate_seekbar);
        minimum_pay_display = (TextView) view.findViewById(R.id.tv_min_rate);
        setSeekBarListener();

        dayDate = (EditText) view.findViewById(R.id.et_event_day);
        date_divider1 = (TextView) view.findViewById(R.id.tv_divider1);
        monthDate = (EditText) view.findViewById(R.id.et_event_month);
        date_divider2 = (TextView) view.findViewById(R.id.tv_divider2);
        yearDate = (EditText) view.findViewById(R.id.et_event_year);

        btn_apply = (Button) view.findViewById(R.id.btn_apply_button);
        btn_reset = (Button) view.findViewById(R.id.btn_reset_button);


    }

    private void setApplyButtonListener(){

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getEventType();
                getLocationPostcode();
                getDistanceValue();
                getRate();
                getDate();

                AdvertAdapter.resetAdvertList();
                advertAdapter.getFilter().filter(inputValue);
//                inputValue = "";

            }
        });

    }

    private void setResetButtonListener(){

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reset();
            }
        });
    }

    private void reset(){

        clearFilterContent();

        advertAdapter.notifyDataSetChanged();
        inputValue = "";
        AdvertAdapter.resetAdvertList();
        advertAdapter.getFilter().filter(inputValue);
        sv_searcher.onActionViewCollapsed();
    }

    private void getEventType(){

        selectedCheckbox = new ArrayList<>();

        for (CheckBox option : checkboxArray){

            if (option.isChecked()){

                selectedCheckbox.add(option.getText().toString().toLowerCase());

            }

        }

        FilterHelper.getChecked(selectedCheckbox);


    }

    private void getLocationPostcode(){

        FilterHelper.setLocation(location_input.getText().toString().toLowerCase());

    }

    private void getDistanceValue(){

        if(!distance_input.getText().toString().equals("")){

            System.out.println("\n+++++DISTANCE INPUT BY USER WAS : " + distance_input.getText());

            FilterHelper.setDistance(Integer.parseInt(distance_input.getText().toString()));

        }

    }

    private void setSeekBarListener(){

        minimum_pay_display.setText("Minimum Rate £ " + (skb_payrate.getProgress()/100f + "0"));

        skb_payrate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int minimum_rate;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                minimum_rate = progress;
                minimum_pay_display.setText("Minimum Rate £ " + (progress/100f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                minimum_pay_display.setText("Minimum Rate £ " + (minimum_rate/100f));

            }
        });

    }

    private void getRate(){

        FilterHelper.setMinimumPay(skb_payrate.getProgress()/100f);

    }

    private void getDate(){

        String date = dayDate.getText().toString() + date_divider1.getText().toString() +
                monthDate.getText().toString() + date_divider2.getText().toString() + yearDate.getText().toString();

        FilterHelper.setEventDate(date);

    }

    private void clearFilterContent(){

        sv_searcher.setQuery("", true);

        if(selectedCheckbox != null){

            selectedCheckbox.clear();

        }

        checkbox_birthday.setChecked(false);
        checkbox_wedding.setChecked(false);
        checkbox_dinnerparty.setChecked(false);
        checkbox_other.setChecked(false);

        location_input.getText().clear();
        distance_input.getText().clear();

        dayDate.getText().clear();
        monthDate.getText().clear();
        yearDate.getText().clear();

        skb_payrate.setProgress(0);
        FilterHelper.setMinimumPay(0);

    }

    public static String getClassName() {
        return "" + SearchPageFragment.class;
    }

}

