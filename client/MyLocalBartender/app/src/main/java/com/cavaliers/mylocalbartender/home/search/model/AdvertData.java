package com.cavaliers.mylocalbartender.home.search.model;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.server.SocketIO;
import com.cavaliers.mylocalbartender.tools.Tools;
import com.cavaliers.mylocalbartender.tools.access.AccessVerificationTools;
import com.cavaliers.mylocalbartender.tools.advert.Advert;
import com.cavaliers.mylocalbartender.tools.cavs_widgets.availability_calendar.AvailabilityCalendarData;
import com.cavaliers.mylocalbartender.user.barstaff.model.ProfileBarstaffData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

/**
 * JobAdvert data-setter
 */

public class AdvertData {

    public static enum TYPE{

        ADVERTS,
        REQUEST,
        ANSWEARS,
        OWNJOBS,
        CHATJOBS

    }

    private static boolean isBarstaff = false;
    private static HashMap<TYPE,ArrayList<Advert>> types = new HashMap<>();

    private static ArrayList<Advert> adverts = new ArrayList<>();
    private static ArrayList<Advert> request = new ArrayList<>();
    private static ArrayList<Advert> answers = new ArrayList<>();
    private static ArrayList<Advert> ownJobs = new ArrayList<>();
    private static ArrayList<Advert> chatJobs = new ArrayList<>();

    public static ArrayList<Advert> getAnswers() {
        return answers;
    }

    public static ArrayList<Advert> getOwnJobs() {
        return ownJobs;
    }

    public static ArrayList<Advert> getRequest() {
        return request;
    }

    public static ArrayList<Advert> getChatJobs() {
        return chatJobs;
    }

    static {

        types.put(TYPE.ADVERTS,adverts);
        types.put(TYPE.ANSWEARS,answers);
        types.put(TYPE.REQUEST,request);
        types.put(TYPE.OWNJOBS,ownJobs);
        types.put(TYPE.CHATJOBS,chatJobs);
    }

    public static final SimpleDateFormat D_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a", Locale.ENGLISH);


    //hardcoded data for testing purpose

    private static final int[] icons = {R.drawable.thumbnail, R.drawable.thumbnail, R.drawable.thumbnail, R.drawable.thumbnail,
            R.drawable.thumbnail, R.drawable.thumbnail, R.drawable.thumbnail, R.drawable.thumbnail,
            R.drawable.thumbnail, R.drawable.thumbnail};

    private static final String[] title = {"Bartender needed", "Barista Internship, competitive salary", "Graduate Mixologist",
            "Graduate Bar staff required", "Waiter needed for new opening", "Experienced Bartender needed ASAP", "Luxury Club experienced staff needed",
            "Experienced cleaners, min 3 years", "Bartender required", "Graduate Bar Cook"};

    private static final String[] eventHolder = {"Bud Spencer Society Ltd.", "Akrasi Corp.", "Drenedies Ink.",
            "DKA Ltd.", "Party Exc Ltd.", "KCL Ceremonies", "Ringo People",
            "Exclusive mat", "Casanova Inc.", "PattyGal one"};

    private static final String[] eventType = {"Birthday Party", "Wedding Party", "Graduation Party",
            "Wedding Party", "Private in-house party", "Wedding Ceremony", "Wedding Party",
            "Birthday Party", "Wedding Party", "Birthday Party"};

    private static final String[] location = {"at London City, London", "at Stratford City, London", "at Strand, London City",
            "at Requiem, Liverpool", "at City Center, Manchester", "at SideStreet, Manchester", "at Outlandish, Boston",
            "at St.James's Park, London", "at Beckton, EastHam", "at HydePark, London"};

    private static final double[] payrate = { 23.00 ,  8.00 ,  12.00 ,
            13.00,  12.46, 7.80,  11.00 ,
            9.00 ,  18.00 ,  7.50};

    private static final String[] description = {"Gavin Grimm, 17, whose initials are on a case heading to the Supreme Court, has become the new face of the transgender rights movement.",
            "Got a confidential news tip?" +
                    "The New York Times offers several ways to get in touch with and provide materials to our journalists.",
            "The nation is close to what economists believe is full productive capacity. The Trump administration’s ambitious growth targets depend on how much slack remains.",
            "India Condemns Deadly Shooting in Kansas, a Possible Hate Crime 37 minutes ago" +
                    "China Names Official to Oversee Troubled Banks 3:14 AM ET" +
                    "British Voters Deliver a Loss and a Win to Labour Party" +
                    "Google Accuses Uber of Using Stolen Self-Driving JobModifyData" +
                    "Mildred Dresselhaus, M.I.T.’s ‘Queen of Carbon,’ Dies at 86" +
                    "Public Editor’s Mailbag: Faulty Headlines, Insensitive Descriptions",
            "The good news: In a small co-op building your fellow residents become like family. That’s also the bad news.",
            "In this week’s Modern Love column, a writer develops a deep and unlikely bond with his elderly neighbor.",
            "The Interpreter brings sharp insight and context to the major news stories of the week. Sign up to get it by email.",
            "A scuffle over a case of beer left in a South Bronx doorway ended with an aspiring rapper shot dead and an expectant father in jail.",
            "A psychologist says the ultrarich suffer from the same existential angst the rest of us do. The main difference: Few people want to hear about it.",
            "Mr. Trump may have overruled Betsy DeVos on transgender bathroom access, but people who have worked with her say she will not be a meek team player."};


    private static final String[] date = {"11/07/2017", "15/02/2017", "01/01/2017",
            "21/01/2017", "24/04/2017", "15/07/2017", "04/04/2017",
            "08/02/2017", "21/06/2017", "19/03/2017"};

    private static final String[] time = {"12PM - 9PM", "2AM - 9AM", "13PM - 9PM",
            "12PM - 19PM", "12AM - 9AM", "12AM - 9AM", "12PM - 19PM",
            "11AM - 3PM", "12PM - 2PM", "12AM - 9AM"};










    public static void reset(){

        answers.clear();
        request.clear();
        ownJobs.clear();
        adverts.clear();
        chatJobs.clear();
    }


   /* //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++UPDATE SEARCH ADVERTS LIST
    public static void updateAdverts(JSONObject data){
        reset();

        JSONObject bar_staff_signin = null;
        JSONObject organiser_signin = null;

        //BAR STAF SIGN IN
        //job_list
        //applications
        //offers
        //profile IS AN OBJECT



        //ORGANISER SIGNIN
        //bar_staff_list
        //applicants
        //created_jobs
        //created_private_jobs
        //profile IS AN OBJECT


        try {

            JSONArray adverList;
            if(data.has(MLBService.JSONKey.BARSTAFF_SIGNIN)){
                bar_staff_signin = data.getJSONObject(MLBService.JSONKey.BARSTAFF_SIGNIN);
                adverList = bar_staff_signin.getJSONArray(MLBService.JSONKey.JOB_LIST);
                for (int i = 0 ; i < adverList.length() ; ++i) {

//                    addJobAdvert((JSONObject) adverList.get(i));
                }
            }else {
                organiser_signin = data.getJSONObject(MLBService.JSONKey.ORGANISER_SIGNIN);
                adverList = organiser_signin.getJSONArray(MLBService.JSONKey.BARSTAFF_LIST);
                for (int i = 0 ; i < adverList.length() ; ++i) {

                    addProfileAdvert((JSONObject) adverList.get(i), answers);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }*/


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++SETUP JOB-ADVERTS
    public static void addJobAdvert(JSONObject obj, TYPE type){

        D_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {

            Date eventStartDate = AdvertData.D_FORMAT.parse(obj.getString(MLBService.JSONKey.JOB_START));
            Date eventEndDate = AdvertData.D_FORMAT.parse(obj.getString(MLBService.JSONKey.JOB_END));

            String[] date = AdvertData.D_FORMAT.format(eventStartDate).split(" ")[0].split("-");
            String[] time = AdvertData.D_FORMAT.format(eventStartDate).split(" ")[1].split(":");

            String hour = String.valueOf(time[0]);
            String minutes = String.valueOf(time[1]);
            String amPm = AdvertData.D_FORMAT.format(eventStartDate).split(" ")[2];

            int year = Integer.parseInt(date[0]);
            int month = Integer.parseInt(date[1]);
            int day = Integer.parseInt(date[2]);


            //WHEN THE ADVERT WAS POSTED
            Date postedDate = AdvertData.D_FORMAT.parse(obj.getString(MLBService.JSONKey.JOB_CREATION));//.replaceAll("Z$", "+0000"));
            String[] postDate = AdvertData.D_FORMAT.format(postedDate).split(" ")[0].split("-");


            int yearPost = Integer.parseInt(postDate[0]);
            int monthPost = Integer.parseInt(postDate[1]);
            int dayPost = Integer.parseInt(postDate[2]);



            Advert advert = Advert.builder().put(MLBService.JSONKey.JOB_ID,obj.getString(MLBService.JSONKey.JOB_ID))
                    .put(MLBService.JSONKey.IMAGE_PATH,"")
                    .put(MLBService.JSONKey.USERNAME,obj.getString(MLBService.JSONKey.USERNAME))
                    .put(MLBService.JSONKey.JOB_RATE,""+obj.getDouble(MLBService.JSONKey.JOB_RATE))
                    .put(MLBService.JSONKey.SPECIALITY,obj.getString(MLBService.JSONKey.SPECIALITY))
                    .put(MLBService.JSONKey.CITY,obj.getString(MLBService.JSONKey.CITY))
                    .put(MLBService.JSONKey.DESCRIPTION,obj.getString(MLBService.JSONKey.DESCRIPTION))
                    .put(MLBService.JSONKey.STATUS,obj.getString(MLBService.JSONKey.STATUS))
                    .put(MLBService.JSONKey.ORG_ID,""+obj.getInt(MLBService.JSONKey.ORG_ID))
                    .put(MLBService.JSONKey.STAFF_ID,obj.isNull(MLBService.JSONKey.STAFF_ID)?"-1":""+obj.getInt(MLBService.JSONKey.STAFF_ID))
                    .put(MLBService.JSONKey.EVENT_TITLE,obj.getString(MLBService.JSONKey.TITLE))
                    .put(MLBService.JSONKey.TITLE,AccessVerificationTools.dateFormatter(day,month,year,false))
                    .put(MLBService.JSONKey.JOB_START,obj.getString(MLBService.JSONKey.JOB_START))
                    .put(MLBService.JSONKey.BUILDING_NUMBER,obj.getString(MLBService.JSONKey.BUILDING_NUMBER))
                    .put(MLBService.JSONKey.STREET,obj.getString(MLBService.JSONKey.STREET))
                    .put(MLBService.JSONKey.POSTCODE,obj.getString(MLBService.JSONKey.POSTCODE))
                    .put(MLBService.JSONKey.DURATION,obj.getString(MLBService.JSONKey.DURATION))
                    .put(MLBService.JSONKey.TYPE,obj.getString(MLBService.JSONKey.TYPE))
                    .put(MLBService.JSONKey.JOB_END,AccessVerificationTools.dateFormatter(dayPost,monthPost,yearPost,false))
                    .put(MLBService.JSONKey.PRIVATE,""+obj.getInt(MLBService.JSONKey.PRIVATE))
                    .build();
            types.get(type).add(advert);

        }catch (JSONException e){
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }








    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++SETUP PROFILE-ADVERTS
    public static void addProfileAdvert(JSONObject obj, TYPE type){

        Advert.AdvertBuilder builder = Advert.builder();
        try {

            Advert advert = builder.put(MLBService.JSONKey.USER_ID,obj.getString(MLBService.JSONKey.USER_ID))
                    .put(MLBService.JSONKey.USERNAME,obj.getString(MLBService.JSONKey.USERNAME))
                    .put(MLBService.JSONKey.IMAGE_PATH,obj.getString(MLBService.JSONKey.IMAGE_PATH))
                    .put(MLBService.JSONKey.HOUR_RATE,""+obj.getDouble(MLBService.JSONKey.HOUR_RATE))
                    .put(MLBService.JSONKey.NIGHT_RATE,""+obj.getDouble(MLBService.JSONKey.NIGHT_RATE))
                    .put(MLBService.JSONKey.SPECIALITY,obj.getString(MLBService.JSONKey.SPECIALITY))
                    .build();
            types.get(type).add(advert);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static void addAdvert(JSONObject obj, TYPE type){

        Iterator<String> itr = obj.keys();

        Advert.AdvertBuilder builder = Advert.builder();

        while (itr.hasNext()){

            String key = itr.next();

            try {
                builder = builder.put(key,obj.get(key).toString());
            } catch (JSONException e) {

                e.printStackTrace();
                builder = null;
                break;
            }
        }

        if(builder != null) types.get(type).add(builder.build());

    }

    public static void load(JSONObject response) throws JSONException{
        loadAnswers(response.getJSONArray("answers"));
        loadRequests(response.getJSONArray("requests"));
        loadAdverts(response.getJSONArray("adverts"));
        if(Tools.accountType.equals(MLBService.AccountType.ORGANISER)) {
            isBarstaff = false;
            loadOwnJobs(response.getJSONArray("public_jobs"));
            loadAvailability(response.getJSONArray("staff_availability"));
        }

        else if(Tools.accountType.equals(MLBService.AccountType.BARSTAFF)) {
            isBarstaff = true;
            JSONObject jsonObject = response.getJSONObject("profile");
            loadAvailability(jsonObject.getJSONArray("availability"));
            if(jsonObject.getJSONArray("availability").length() > 0){
                AvailabilityCalendarData.clearAvailabilityList();
                loadProfileBarStaffData(jsonObject);
            }

        }

        loadChats(response.getJSONArray("chat_jobs"));


    }

    public static void loadAnswers(JSONArray answersList) throws JSONException{

        for(int i = 0 ; i < answersList.length() ; ++i){

            addAdvert((JSONObject) answersList.get(i), TYPE.ANSWEARS);
        }
    }

    public static void loadRequests(JSONArray requestsList) throws JSONException{

        for(int i = 0 ; i < requestsList.length() ; ++i){

            addAdvert((JSONObject) requestsList.get(i), TYPE.REQUEST);
        }
    }

    public static void loadAdverts(JSONArray answersList) throws JSONException {

        for (int i = 0; i < answersList.length(); ++i) {

            addAdvert((JSONObject) answersList.get(i), TYPE.ADVERTS);
        }
    }

    public static void loadOwnJobs(JSONArray ownJobsList) throws JSONException{


        for(int i = 0 ; i < ownJobsList.length() ; ++i){

            addAdvert((JSONObject) ownJobsList.get(i), TYPE.OWNJOBS);
        }
    }

    public static void loadProfileBarStaffData(JSONObject barstaffObj) throws JSONException{

        for(int i = 0 ; i < barstaffObj.length()-1 ; ++i){

            ProfileBarstaffData.setProfileData(barstaffObj.names().get(i).toString(),
                    barstaffObj.get(barstaffObj.names().get(i).toString()).toString());
        }
    }

    public static void loadAvailability(JSONArray staff_availability) throws JSONException{


        for(int i = 0 ; i < staff_availability.length() ; ++i){

            if(isBarstaff){
                AvailabilityCalendarData.addProfileAvailability((JSONObject) staff_availability.get(i));
            }else{
                AvailabilityCalendarData.addAdvertAvailability((JSONObject) staff_availability.get(i));
            }

        }
    }

    public static void loadChats(JSONArray chatJobsList) throws  JSONException {
        for(int i = 0; i < chatJobsList.length(); ++i) {
            addAdvert((JSONObject) chatJobsList.get(i), TYPE.CHATJOBS);
            String job_id = ((JSONObject) chatJobsList.get(i)).getString(MLBService.JSONKey.JOB_ID);
            String staff_id = "";
            String org_id = "";
            if(Tools.accountType.equals(MLBService.AccountType.ORGANISER)){
                staff_id = ((JSONObject) chatJobsList.get(i)).getString(MLBService.JSONKey.STAFF_ID);
                org_id = MLBService.getLoggedInUserID();
            } else if (Tools.accountType.equals(MLBService.AccountType.BARSTAFF)) {
                staff_id = MLBService.getLoggedInUserID();
                org_id = ((JSONObject) chatJobsList.get(i)).getString(MLBService.JSONKey.ORG_ID);
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(MLBService.JSONKey.JOB_ID, job_id);
            jsonObject.put(MLBService.JSONKey.ORG_ID, org_id);
            jsonObject.put(MLBService.JSONKey.STAFF_ID, staff_id);
            MLBService.emit(SocketIO.Event.INITIATE_CHAT, jsonObject);
        }
    }

    public static void updateAdvert(JSONObject obj, TYPE type){

        addAdvert(obj,type);
    }



    public static ArrayList<Advert> getAdverts(){
        return adverts;
    }


}
