package com.cavaliers.mylocalbartender.home.search.helper;

import android.widget.Filter;

import com.cavaliers.mylocalbartender.home.search.SearchPageFragment;
import com.cavaliers.mylocalbartender.home.search.adapter.AdvertAdapter;
import com.cavaliers.mylocalbartender.server.MLBService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import com.cavaliers.mylocalbartender.tools.advert.Advert;

public class FilterHelper extends Filter {

    static ArrayList<Advert> listToFilter; //will hold the list of adverts we need to filter
    static AdvertAdapter advertAdapter;

    static List<String> eventsChecked = new ArrayList<>();
    static String locationOfBartender;
    static int distanceToFilter = -1;
    static double totalDistance;
    static boolean loading = false;
    static double minimumRateWanted;
    static String eventDate;
    static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public static FilterHelper getFilter(ArrayList<Advert> filterList, AdvertAdapter adapter){

        AdvertAdapter.resetAdvertList();

        FilterHelper.advertAdapter = adapter;
        FilterHelper.listToFilter = filterList;

        return new FilterHelper();
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        FilterResults results = new FilterResults();

        if(constraint != null){

            constraint = constraint.toString().toLowerCase();
            ArrayList<Advert> filteredAdverts = new ArrayList<>(); //will hold the list of adverts that match the filter

            Advert advert = null;
            boolean isContained = true;

            for(int i = 0; i < listToFilter.size(); ++i){

                advert = listToFilter.get(i);

                if (constraint.equals("")){

                    isContained = true;

                }else{

                    if(advert.getValue(MLBService.JSONKey.EVENT_TITLE).toLowerCase().contains(constraint)){
                        System.out.println("\n+++++FOUND A BASIC SEARCH MATCH IN EVENT TITLE");

                        isContained = true;

                    } else{

                        System.out.println("\n+++++CAN NOT FIND A BASIC SEARCH MATCH IN EVENT TITLE");
                        isContained = false;

                    }

                }

                if(isContained){

                    isContained = filterByEventType(advert, isContained);
                    System.out.println("\n+++++FILTER BY EVENT TYPE");
                }


                if (isContained){

                    isContained = filterByPayrate(advert);
                    System.out.println("\n+++++FILTER BY MIN PAY RATE");

                }

                if (isContained){

                    isContained = filterByDate(advert);
                    System.out.println("\n+++++FILTER BY DATE");

                }

                if (isContained){

                    System.out.println("\n+++++ALL MATCH FIELDS MATCH ADVERT? ADD THE AD TO FILTERED ADVERTS TO BE SHOWN ON SCREEN");
                    filteredAdverts.add(advert);
                    isContained = false;

                }else{

                    System.out.println("TESRFRFVGB");
                }

            }

            if (distanceToFilter == -1){

                System.out.println("\n+++++NO DISTANCE WAS INPUT");

                results.count = filteredAdverts.size();
                results.values = filteredAdverts;

            } else{

                loading = false;
                System.out.println("\n+++++DISTANCE WAS INPUT AND WAS : " + distanceToFilter);
                filterByLocation(filteredAdverts);

            }

            System.out.println("\n+++++CHECKS/FILTERING COMPLETE SO ADD THE FILTERED ADVERTS TO RESULTS");
            results.count = filteredAdverts.size();
            results.values = filteredAdverts; //results that are returned

        } else {

            System.out.println("\n+++++CONSTRAINT IS NULL, SO RETURN ORGINAL LIST");
            results.count = listToFilter.size();
            results.values = listToFilter; //results that are returned

        }

        System.out.println("\n+++++RETURNING THE RESULTS FOUND");
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        advertAdapter.setAdverts((ArrayList<Advert>) results.values);
        advertAdapter.notifyDataSetChanged();

    }

    public static void getChecked(ArrayList<String> checkboxArray){

        eventsChecked = checkboxArray;

    }

    private static boolean filterByEventType(Advert ad, boolean isContained){

        if(eventsChecked.size() > 0 ) {

            for (int j = 0; j < eventsChecked.size(); ++j) {

                if (ad.getValue(MLBService.JSONKey.TYPE).toLowerCase().contains(eventsChecked.get(j))) {

                    isContained = true;
                    break;

                } else {

                    isContained = false;

                }
            }

        } else{

            return true;

        }

        return isContained;

    }

    public static void setLocation(String location){

        System.out.println("\n+++++LOCATION OF BARTENDER : " + locationOfBartender);
        locationOfBartender = location;

    }

    public static void setDistance(int distance){

        distanceToFilter = distance;
        System.out.println("\n+++++DISTANCE SET TO : " + distanceToFilter);

    }

    //requires internet - add textview
    private static void filterByLocation(final ArrayList<Advert> filteredAdverts){

        MLBService.submitTask(new Runnable() {
            @Override
            public void run() {

                System.out.println("\n+++++START FILTER BY LOCATION and filteredAdverts SIZE SO FAR : " + filteredAdverts.size());

//                ArrayList<Advert> filterList = new ArrayList<>();

                try{

//                    for (int i = 0; i < filteredAdverts.size(); ++i){

                    Iterator<Advert> it_advert = filteredAdverts.iterator();

                    while(it_advert.hasNext()){

                        Advert ad = it_advert.next();

                        //Bar Staff Location
                      URL url_bartender = new URL("http://api.postcodes.io/postcodes/" + locationOfBartender);
                        System.out.println("\n+++++BARTENDER LOCATION : " + locationOfBartender);
//                        URL url_bartender = new URL("http://api.postcodes.io/postcodes/" + "e49dh");

                        JSONObject bartender_json_object = getResponse(url_bartender);

                        System.out.println("\n+++++BARTENDER_JSON_OBJECT : " + bartender_json_object);

                        JSONObject bartender_result = bartender_json_object.getJSONObject("result");
                        double bartender_longitude = bartender_result.getDouble("longitude");
                        double bartender_latitude = bartender_result.getDouble("latitude");

                        //Advert Location
                        System.out.println("\n+++++ADVERT POSTCODE : " + ad.getValue(MLBService.JSONKey.POSTCODE));
                        URL url_advert = new URL("http://api.postcodes.io/postcodes/" + ad.getValue(MLBService.JSONKey.POSTCODE));

                        JSONObject advert_json_object = getResponse(url_advert);

                        if (advert_json_object != null){

                            System.out.println("\n+++++ADVERT_JSON_OBJECT IS NOT NULL");

                            JSONObject advert_result = advert_json_object.getJSONObject("result");
                            double advert_longitude = advert_result.getDouble("longitude");
                            double advert_latitude = advert_result.getDouble("latitude");

                            totalDistance = calculateDistance(bartender_longitude, bartender_latitude, advert_longitude, advert_latitude);

                            System.out.println("\n+++++distanceTOFilter : " + distanceToFilter);
                            System.out.println("\n+++++totalDistance : " + totalDistance);

                            if (totalDistance > distanceToFilter){

                                System.out.println("\n+++++REMOVING ADVERT AS TOTAL DISTANCE IS LESS THAN DISTANCE TO FILTER");
                                it_advert.remove();

                            }else{

                                System.out.println("\n+++++ADDING ADVERT");

                            }

                        } else{

                            System.out.println("\n+++++REMOVING ADVERT");
                            it_advert.remove();

                        }

                    }

                    System.out.println("\n+++++FILTERED ADVERTS SIZE IS NOW :  " + filteredAdverts.size() + " AFTER FILTER BY LOCATION");
                    updateUI(filteredAdverts);

                } catch (MalformedURLException | JSONException e){

                    e.printStackTrace();

                }

            }
        }, null);

    }

    public static void setMinimumPay(float minimumPay){

        minimumRateWanted = minimumPay;

    }

    private static boolean filterByPayrate(Advert ad){

        return Float.parseFloat(ad.getValue(MLBService.JSONKey.RATE)) >= minimumRateWanted;
    }

    public static void setEventDate(String date){

            eventDate = date;
    }

    private static boolean filterByDate(Advert ad){

        Date date1 = null;
        Date date2 = null;

        System.out.println("TEST;;; " + eventDate);

        if(eventDate == null || eventDate.equals("//")) return true;

        try{

            date1 = dateFormat.parse(ad.getValue(MLBService.JSONKey.JOB_START));
            date2 = dateFormat.parse(eventDate);

            return date2.after(date1);

        } catch (ParseException pe){

            pe.printStackTrace();

        }

        return true;

    }

    private static JSONObject getResponse(URL url){

        try{

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");

            InputStreamReader isr = new InputStreamReader(http.getInputStream(), Charset.defaultCharset());
            StringBuilder sb = new StringBuilder();

            if (http != null && http.getInputStream() != null) {

                BufferedReader bufferedReader = new BufferedReader(isr);
                if (bufferedReader != null) {
                    int cp;
                    while ((cp = bufferedReader.read()) != -1) {

                        sb.append((char) cp);
                    }
                    bufferedReader.close();
                }
            }

            isr.close();

            JSONObject json_object = new JSONObject(sb.toString());

            return json_object;

        } catch (FileNotFoundException fnfe){

            System.out.println("FILE NOT FOUND");
            return null;

        } catch(IOException | JSONException e){

            e.printStackTrace();

            return null;

        }

    }

    private static double calculateDistance(double b_longitude, double b_latitude, double a_longitude, double a_latitude){

        a_longitude = (a_longitude * Math.PI) / 180;
        a_latitude = (a_latitude * Math.PI) / 180;

        b_longitude = (b_longitude * Math.PI) / 180;
        b_latitude = (b_latitude * Math.PI) / 180;

        double dLongitude = b_longitude - a_longitude;
        double dLatitude = b_latitude - a_latitude;

        double a = Math.pow((Math.sin(dLatitude/2)), 2) + Math.cos(a_latitude) * Math.cos(b_latitude) * Math.pow((Math.sin(dLongitude/2)),2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        int radius = 6373000;

        return (radius * c) * 0.000621371; //convert distance to miles

    }

    private static void updateUI(final ArrayList<Advert> filteredAdverts){

        SearchPageFragment.handler.removeCallbacksAndMessages(null);

        SearchPageFragment.handler.post(new Runnable() {
            @Override
            public void run() {

                System.out.println("filteredAdverts : " + filteredAdverts);

                AdvertAdapter.setFilteredList(filteredAdverts);

                advertAdapter.notifyDataSetChanged();

                distanceToFilter = -1;

            }
        });

    }



}