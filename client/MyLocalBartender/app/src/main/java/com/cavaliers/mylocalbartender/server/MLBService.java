/**
 * @author Cavaliers
 */
package com.cavaliers.mylocalbartender.server;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cavaliers.mylocalbartender.home.search.model.AdvertData;
import com.cavaliers.mylocalbartender.server.ssl.MLBCA;
import com.cavaliers.mylocalbartender.tools.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * A class that provided service to communicate with the server api
 */
public final class MLBService {

    private static RequestQueue mRequestQuest;

    private static HashMap<String, SocketIO.EventListener> listeners = new HashMap<>();

    // Socket listeners keys
    public static final String BARSTAFF_JOINED = SocketIO.BarStaffJoinedListener.class.getName();

    private static String currentUserID = "-1";

    private final static Handler handler = new Handler();

    /**
     * The request timeout in milliseconds
     */
    private final static int SOCKET_TIMEOUT_MS = 2500;

    /**
     * The list that stores all the request currently in the queue
     */
    private final static List<String> sentRequestList;

    private static final String SALT;

    private static MLBBackgroundService service = null;

    private static String API = "https://91.121.78.111:1133/api/";
//    private static String API = "http://192.168.1.5:1133/api/";
    //private static String API = "http://192.168.1.13:1133/api/";

    private static String token;

    /**
     * Hash map that links all the possible MLB http routes with its corresponding url path
     */
    private static final HashMap<API_ROUTE, String> REQUEST;

    private static final HashMap<SocketIO.Event, String>EVENTS;

    /**
     * Hash map that links MLB's methods to its corrisponding HTTP Method (GET, PUT, DELETE, POST)
     */
    private static final HashMap<ACTION, Pair<String, Integer>> HTTP_METHOD;

    /**
     * JSON keys to be sent to the server
     */
    public static class JSONKey {

        public static final String ORGANISER_SIGNIN = "organiser_signin";
        public static final String BARSTAFF_LIST = "bar_staff_list";
        public static final String APPLICANTS = "applicants";
        public static final String CREATED_PRIVATE_JOBS = "created_private_jobs";



        public static final String BARSTAFF_SIGNIN = "bar_staff_signin";
        public static final String JOB_LIST = "job_list";
        public static final String APPLICATIONS = "applications";
        public static final String OFFERS = "offers";



        public static final String PROFILE = "profile";
        public static final String USER = "user";
        public static final String USERNAME = "username";
        public static final String USER_ID = "user_ID";
        public static final String STAFF_ID = "staff_ID";
        public static final String ORG_ID = "org_ID";
        public static final String JOB_ID = "job_ID";
        public static final String FIRST_NAME = "first_name";
        public static final String SURNAME = "last_name";
        public static final String GENDER = "gender";
        public static final String DOB = "DOB";
        public static final String POSTCODE = "postcode";
        public static final String CITY = "city";
        public static final String STREET = "street_name";
        public static final String TOWN = "town";
        public static final String MOBILE = "mobile_number";
        public static final String EMAIL = "email_address";
        public static final String HASH = "hash";
        public static final String ACTIVE = "active";
        public static final String TYPE = "type";
        public static final String SPECIALITY = "speciality";
        public static final String DESCRIPTION = "description";
        public static final String PROXIMITY = "proximity";
        public static final String HOUR_RATE = "hour_rate";
        public static final String RATE = "rate";
        public static final String NIGHT_RATE = "night_rate";
        public static final String IMAGE_PATH = "image_path";
        public static final String ACCOUNT_TYPE = "account_type";
        public static final String DURATION = "duration";
        public static final String MESSAGE = "message";
        public static final String PROF_POS = "prof_pos";
        public static final String DAY = "day";
        public static final String START_TIME = "start_time";
        public static final String END_TIME = "end_time";
        public static final String LOCATION = "location";
        public static final String JOB_RATE = "rate";
        public static final String STATUS = "status";
        public static final String AVAILABLE = "availability";
        public static final String PRIVATE = "private";
        public static final String TOKEN = "token";
        public static final String JOB_CREATION = "job_creation";
        public static final String JOB_START = "job_start";
        public static final String JOB_END = "job_end";
        public static final String TITLE = "title";
        public static final String BUILDING_NUMBER = "no";
        public static final String EVENT_TYPE = "event_type";
        public static final String STRIPE_TOKEN = "stripe_token";
        public static final String EXPERIENCE = "experience";
        public static final String CHAT_MESSAGE_KEY = "chat_message";
        public static final String EVENT_TITLE = "title";
        public static final String SIGNIN_JSON  = "signin_json";
    }

    /**
     * The account type
     */
    public static enum AccountType{
        BARSTAFF,
        ORGANISER,
        ADMIN,
        UNKNOWN // Use for error handling
    }

    /**
     * All the routes the MBL RESTFul API has got
     */
    private static enum API_ROUTE{
        BARTENDER,
        BARTENDER_APPLICATIONS,
        BARTENDER_OFFERS,
        BARTENDER_COMPLETE_PROFILE,
        BARTENDER_RESPONSE,
        BARTENDER_RESPONSE_ACCEPT,
        BARTENDER_RESPONSE_REJECT,

        ORGANISER,
        ORGANISER_COMPLETE_PROFILE,
        ORGANISER_APPLICANTS,
        ORGANISER_REQUEST,
        ORGANISER_ASSIGN,
        ORGANISER_RESPONSE_ACCEPT,
        ORGANISER_RESPONSE_REJECT,

        EVENT,
        EVENT_REMOVE,
        EVENT_MODIFY,

        SOCKET_SEND_MESSAGE,
        SOCKET_SEND_OFFER,
        SOCKET_ACCEPT_OFFER,
        SOCKET_REJECT_OFFER,
        SOCKET_JOB_COMPLETED

    }

    /**
     * All method supported by the MLB RESTFul API
     */
    public static enum ACTION {

        //----------------------------- GET METHODS -----------------------------------------------
        GET_APPLICATIONS, //https://91.121.78.111:1133/api/bartender/applications/<:userid>?token=<value>
        GET_OFFERS, //https://91.121.78.111:1133/api/bartender/offers/<:userid>?token=<value>
        GET_BARTENDERS_PROFILE, //https://91.121.78.111:1133/api/bartender?token=<value>
        //GET_BARTENDER_PROFILE, //https://91.121.78.111:1133/api/bartender/<:userid>?token=<value> TODO

        GET_APPLICANTS, // https://91.121.78.111:1133/api/organiser/applicants/<:userid>?token=<value>
        GET_ORGANISER_PRIVATE_JOBS, //https//91.121.78.111:1133/api/organiser/request/<:userid>?token<value>

        GET_JOBS, //https://91.121.78.111:1133/api/event?token=<value>
        GET_ORGANISER_PUBLIC_JOBS, //https://91.121.78.111:1133/api/event/<:organiser>?token=<value>


        //-------------------------- PUT METHODS ---------------------------------------------------
        ACCEPT_OFFER, //https://91.121.78.111:1133/api/bartender/response_accept/<:userid>/<:eventid>
        UPDATE_BARTENDER_PROFILE, //https://91.121.78.111:1133/api/bartender/<:userid>
        COMPLETE_BARTENDER_PROFILE, //https//91.121.78.111:1133/api/bartender/complete_profile/<:userid>


        ACCEPT_APPLICANT, //https://91.121.78.111:1133/api/organiser/response_accept/<:userid>r
        UPDATE_ORGANISER_PROFILE, //https://91.121.78.111:1133/api/organiser/<:userid>
        COMPLETE_ORGANISER_PROFILE, //https//91.121.78.111:1133/api/organiser/complete_profile/<:userid>

        UPDATE_ORGANISER_PUBLIC_JOB, //https://91.121.78.111:1133/api/event/modify/<:organiserid>/<:eventid> //TODO on server


        //---------------------------- DELETE METHOD -----------------------------------------------
        REJECT_OFFER, //https://91.121.78.111:1133/api/bartender/response_reject/<:userid>/<:eventid>
        DELETE_APPLICATION, //https://91.121.78.111:1133/api/bartender/applications/<:userid>/:<eventid>

        REJECT_APPLICANT, //https://91.121.78.111:1133/api/organiser/response_reject/<:userid>
        DELETE_PRIVATE_JOB, //https://91.121.78.111:1133/api/organiser/request/<:userid>
        //DELETE_ORGANISER_PROFILE, https://91.121.78.111:1133/api/organiser/<:userid> //TODO on server

        DELETE_ORGANISER_PUBLIC_JOB, //https://91.121.78.111:1133/api/event/remove/<:organiserid>/<:eventid>


        //-------------------------- POST METHOD ---------------------------------------------------
        APPLY_JOB , //https://91.121.78.111:1133/api/organiser/applications/<:userid>/<:eventid>

        SEND_PRIVATE_JOB, //https://91.121.78.111:1133/api/organiser/assign/<:userid>

        CREATE_PRIVATE_JOB, //https://91.121.78.111:1133/api/event/<:bartenderid>

        CREATE_PUBLIC_JOB, //https://91.121.78.111:1133/api/event/

        CHAT_SEND_MESSAGE, //https://91.121.78.111:1133/api/socket/message/<:eventid>/<:userid(who to send to)>

        CHAT_SEND_JOB_OFFER, //https://91.121.78.111:1133/api/socket/offer/<:eventid>/<:userid(who to send to)>

        CHAT_ACCEPT_JOB_OFFER, //https://91.121.78.111:1133/api/socket/acceptoffer/<:eventid>/<:userid(who to send to)>

        CHAT_REJECT_OFFER, //https://91.121.78.111:1133/api/socket/rejectoffer/<:eventid>/<:userid(who to send to)>

        SOCKET_JOB_COMPLETED, //https://91.121.78.111:1133/api/socket/jobcompleted/<:eventid>
    }

    /**
     * All the possible request supported
     */
    public static enum POST {
        SIGN_UP, //http://91.121.78.111:1133/api/signup
        SIGN_IN //http://91.121.78.111:1133/api/signin
    }



    static Socket socket;

    static {

        REQUEST = new HashMap<>();
        //Base
        REQUEST.put(API_ROUTE.BARTENDER, "bartender");
        REQUEST.put(API_ROUTE.ORGANISER, "organiser");
        REQUEST.put(API_ROUTE.EVENT, "event");
        REQUEST.put(API_ROUTE.EVENT_MODIFY, "event/modify");
        REQUEST.put(API_ROUTE.EVENT_REMOVE, "event/remove");

        //Bartender
        REQUEST.put(API_ROUTE.BARTENDER_APPLICATIONS, "bartender/applications");
        REQUEST.put(API_ROUTE.BARTENDER_OFFERS, "bartender/offers");
        REQUEST.put(API_ROUTE.BARTENDER_COMPLETE_PROFILE, "bartender/complete_profile");
        REQUEST.put(API_ROUTE.BARTENDER_RESPONSE_ACCEPT, "bartender/response_accept");
        REQUEST.put(API_ROUTE.BARTENDER_RESPONSE_REJECT, "bartender/response_reject");

        //Organiser
        REQUEST.put(API_ROUTE.ORGANISER_APPLICANTS, "organiser/applicants");
        REQUEST.put(API_ROUTE.ORGANISER_COMPLETE_PROFILE, "organiser/complete_profile");
        REQUEST.put(API_ROUTE.ORGANISER_RESPONSE_ACCEPT, "organiser/response_accept");
        REQUEST.put(API_ROUTE.ORGANISER_RESPONSE_REJECT, "organiser/response_reject");
        REQUEST.put(API_ROUTE.ORGANISER_REQUEST, "organiser/request");
        REQUEST.put(API_ROUTE.ORGANISER_ASSIGN, "organiser/assign");

        //chat/socket
        REQUEST.put(API_ROUTE.SOCKET_SEND_MESSAGE, "socket/message");
        REQUEST.put(API_ROUTE.SOCKET_SEND_OFFER, "socket/offer");
        REQUEST.put(API_ROUTE.SOCKET_ACCEPT_OFFER, "socket/acceptoffer");
        REQUEST.put(API_ROUTE.SOCKET_REJECT_OFFER, "socket/rejectoffer");
        REQUEST.put(API_ROUTE.SOCKET_JOB_COMPLETED, "socket/jobcompleted");

        //-------------------------------- API ROUTES DEFINITIONS ----------------------------------
        HTTP_METHOD = new HashMap<>();
        //Bartender
        setHTTP_METHOD(ACTION.GET_APPLICATIONS, API_ROUTE.BARTENDER_APPLICATIONS, JsonObjectRequest.Method.GET);
        setHTTP_METHOD(ACTION.DELETE_APPLICATION, API_ROUTE.BARTENDER_APPLICATIONS, JsonObjectRequest.Method.PUT);
        setHTTP_METHOD(ACTION.APPLY_JOB, API_ROUTE.BARTENDER_APPLICATIONS, JsonObjectRequest.Method.POST);
        setHTTP_METHOD(ACTION.GET_OFFERS, API_ROUTE.BARTENDER_OFFERS, JsonObjectRequest.Method.GET);
        setHTTP_METHOD(ACTION.ACCEPT_OFFER, API_ROUTE.BARTENDER_RESPONSE_ACCEPT, JsonObjectRequest.Method.PUT);
        setHTTP_METHOD(ACTION.REJECT_OFFER, API_ROUTE.BARTENDER_RESPONSE_REJECT, JsonObjectRequest.Method.PUT);
        setHTTP_METHOD(ACTION.GET_BARTENDERS_PROFILE, API_ROUTE.BARTENDER, JsonObjectRequest.Method.GET);
        setHTTP_METHOD(ACTION.COMPLETE_BARTENDER_PROFILE, API_ROUTE.BARTENDER_COMPLETE_PROFILE, JsonObjectRequest.Method.PUT);
        //setHTTP_METHOD(ACTION.GET_BARTENDER_PROFILE, API_ROUTE.BARTENDER, JsonObjectRequest.Method.GET); //
        //setHTTP_METHOD(ACTION.DELETE_BARTENDER_PROFILE, API_ROUTE.BARTENDER, JsonObjectRequest.Method.PUT); //
        setHTTP_METHOD(ACTION.UPDATE_BARTENDER_PROFILE, API_ROUTE.BARTENDER, JsonObjectRequest.Method.PUT); // TODO on server

        //Organiser
        setHTTP_METHOD(ACTION.GET_APPLICANTS, API_ROUTE.ORGANISER_APPLICANTS, JsonObjectRequest.Method.GET);
        setHTTP_METHOD(ACTION.ACCEPT_APPLICANT, API_ROUTE.ORGANISER_RESPONSE_ACCEPT, JsonObjectRequest.Method.PUT);
        setHTTP_METHOD(ACTION.REJECT_APPLICANT, API_ROUTE.ORGANISER_RESPONSE_REJECT, JsonObjectRequest.Method.PUT);
        setHTTP_METHOD(ACTION.GET_ORGANISER_PRIVATE_JOBS, API_ROUTE.ORGANISER, JsonObjectRequest.Method.GET);
        setHTTP_METHOD(ACTION.DELETE_PRIVATE_JOB, API_ROUTE.ORGANISER, JsonObjectRequest.Method.PUT);
        setHTTP_METHOD(ACTION.SEND_PRIVATE_JOB, API_ROUTE.ORGANISER_ASSIGN, JsonObjectRequest.Method.POST);
        setHTTP_METHOD(ACTION.COMPLETE_ORGANISER_PROFILE, API_ROUTE.ORGANISER_COMPLETE_PROFILE, JsonObjectRequest.Method.PUT); // TODO on server
        //setHTTP_METHOD(ACTION.GET_ORGANISER_PROFILE, API_ROUTE.ORGANISER, JsonObjectRequest.Method.GET); //
        setHTTP_METHOD(ACTION.UPDATE_ORGANISER_PROFILE, API_ROUTE.ORGANISER, JsonObjectRequest.Method.PUT); // TODO on server
        //setHTTP_METHOD(ACTION.DELETE_ORGANISER_PROFILE, API_ROUTE.ORGANISER, JsonObjectRequest.Method.PUT); //


        // Event/Job
        setHTTP_METHOD(ACTION.GET_JOBS, API_ROUTE.EVENT, JsonObjectRequest.Method.GET);
        setHTTP_METHOD(ACTION.CREATE_PRIVATE_JOB, API_ROUTE.EVENT, JsonObjectRequest.Method.POST);
        setHTTP_METHOD(ACTION.CREATE_PUBLIC_JOB, API_ROUTE.EVENT, JsonObjectRequest.Method.POST);
        setHTTP_METHOD(ACTION.GET_ORGANISER_PUBLIC_JOBS, API_ROUTE.EVENT, JsonObjectRequest.Method.GET);
        setHTTP_METHOD(ACTION.DELETE_ORGANISER_PUBLIC_JOB, API_ROUTE.EVENT_REMOVE, JsonObjectRequest.Method.PUT);
        setHTTP_METHOD(ACTION.UPDATE_ORGANISER_PUBLIC_JOB, API_ROUTE.EVENT_MODIFY, JsonObjectRequest.Method.PUT); //TODO on server

        setHTTP_METHOD(ACTION.CHAT_SEND_MESSAGE, API_ROUTE.SOCKET_SEND_MESSAGE, JsonObjectRequest.Method.POST);
        setHTTP_METHOD(ACTION.CHAT_SEND_JOB_OFFER, API_ROUTE.SOCKET_SEND_OFFER, JsonObjectRequest.Method.POST);
        setHTTP_METHOD(ACTION.CHAT_ACCEPT_JOB_OFFER, API_ROUTE.SOCKET_ACCEPT_OFFER, JsonObjectRequest.Method.POST);
        setHTTP_METHOD(ACTION.CHAT_REJECT_OFFER, API_ROUTE.SOCKET_REJECT_OFFER, JsonObjectRequest.Method.POST);
        setHTTP_METHOD(ACTION.SOCKET_JOB_COMPLETED, API_ROUTE.SOCKET_JOB_COMPLETED, JsonObjectRequest.Method.POST);

        // Sockets events -----------------------------------------------------
        EVENTS = new HashMap<>();
        EVENTS.put(SocketIO.Event.SETUP_CONNECTION,"setupConnection");
        EVENTS.put(SocketIO.Event.APPLY_TO_JOB,"barStaffApplied");
        EVENTS.put(SocketIO.Event.ACCEPT_JOB_INVITE,"barStaffAcceptedInvite");
        EVENTS.put(SocketIO.Event.ORGANISER_ACCEPTED_APPLICANT,"organsierAcceptedApplicant");
        EVENTS.put(SocketIO.Event.SEND_INVITE,"sendInvite");
        EVENTS.put(SocketIO.Event.NEW_JOB,"newJobCreated");
        EVENTS.put(SocketIO.Event.BAR_STAFF_JOINED,"barStaffJoined");
        EVENTS.put(SocketIO.Event.CARD_UPDATE, "cardUpdated");
        EVENTS.put(SocketIO.Event.INITIATE_CHAT, "initSession");

        sentRequestList = new ArrayList<>();
        token = null;
        SALT = "76y1w!ijkyb672#";
    }

    //no instances can be made of this class
    private MLBService(){}

    /**
     *
     * @param action The method supported by the MLB server
     * @param route The name of the route
     * @param method HTTP method used for the route
     */
    private static void setHTTP_METHOD(ACTION action, API_ROUTE route, int method){
        Pair routeAndMethodPair = Pair.create(REQUEST.get(route), method);
        HTTP_METHOD.put(action, routeAndMethodPair);
    }

    /**
     * Starts the service by instantiating the event queue
     *
     * @param context the context Volley uses
     */
    public static void startService(Context context){
        setContext(context);
    }

    /**
     * Changes the HOST address the request are sent too
     *
     * @param newHost the server ip address
     * @param port the port it uses
     */
    public static void setHost(String newHost, String port) {

        API = "https://" + newHost + ":"+ port + "/api/";
        System.out.println("new host: "+API);
    }

    public static void submitTask(@NonNull Runnable runnable, Runnable afterExec){

        service.submitTask(runnable,afterExec);
    }

    /**
     * instantiates the event queue for later use
     *
     * @param context the context Volley uses
     */
    private static void setContext(Context context){
        if(mRequestQuest == null) {
            MLBCA mlbca = new MLBCA(context);
            mRequestQuest = Volley.newRequestQueue(context, mlbca);
            IO.Options opts = new IO.Options();
            try {
                opts.sslContext = mlbca.getSSlContext();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            opts.hostnameVerifier = mlbca.getHostnameVerifier();
            try {
                socket = IO.socket("https://91.121.78.111:1133", opts);
                //socket = IO.socket("http://192.168.1.8:1133"/*, opts*/);
                //socket = IO.socket("http://192.168.1.13:1133/" /*,opts*/);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            DatabaseHandler dbHandler = new DatabaseHandler();
            service = new MLBBackgroundService();
            service.start();
            dbHandler.loadDB(context);
            Tools.DB_CONNECTION = dbHandler;
            setListeners();
        }
    }

    /**
     * sign user out by cancelling all request in queue and removes token
     * as well is cancel all request currently in the queue
     */
    public static void signOut(){

        token = null;
        Iterator<String> it = sentRequestList.iterator();
        disconnectSocket();
        Tools.accountType = null;
        AdvertData.reset();
        while(it.hasNext()){

            String s = it.next();
            mRequestQuest.cancelAll(s);
            it.remove();
        }
    }

    /**
     * The method sends a sign up request to the server
     *
     * @param context to check if you have internet connection
     * @param email the email address of the user
     * @param password the password for the user
     * @param dob the date of birth of the user
     * @param type the account type of the user
     * @param resListender the response listener for the request
     * @param errorListener the error listener for the request
     *
     * //@see MLBService#createSalt() createSalt()
     *
     * @see MLBService#createHash(String) createHash(String)
     *
     * @see POST#SIGN_UP
     *
     * @see JsonObjectRequest#JsonObjectRequest(int, String, JSONObject, Response.Listener, Response.ErrorListener)
     *          JsonObjectRequest(int, String, JSONObject, Response.Listener, Response.ErrorListener)
     *
     * @throws VolleyError if their is not internet connection
     *
     */
    public static void signUp(Context context ,String email, String password, String dob, AccountType type,
                              final MLBResponseListener resListender, final MLBErrorListener errorListener) throws UnsupportedEncodingException, VolleyError {
        if(!sentRequestList.contains(POST.SIGN_UP.toString())){
            try {
                if (!isConnectedToNetwork(context)) throw new VolleyError("No Internet Connection");
                JSONObject signup = new JSONObject();
                signup.put(JSONKey.EMAIL, email);
                signup.put(JSONKey.HASH, createHash(password));
                signup.put(JSONKey.DOB, dob);
                signup.put(JSONKey.ACCOUNT_TYPE, type.toString());
                System.out.println(signup);
                MLBJsonObjectRequest JSONObjReq = new MLBJsonObjectRequest(JsonObjectRequest.Method.POST, API + "signup", signup, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        resListender.onResponse(response);
                        sentRequestList.remove(POST.SIGN_UP.toString());
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorListener.onErrorResponse(error);
                        sentRequestList.remove(POST.SIGN_UP.toString());
                    }
                }, false);
                sentRequestList.add(POST.SIGN_UP.toString());
                addToQueue(JSONObjReq);
            } catch (JSONException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getLoggedInUserID(){
        return currentUserID;
    }

    private static void getUserId(JSONObject obj){
        try{
            currentUserID = obj.getJSONObject(JSONKey.USER).getString(JSONKey.USER_ID);
        }catch(JSONException e){
            currentUserID = "-1";
            e.printStackTrace();
        }
    }

    /**
     * The method sends a sign in request to the server
     *
     * @param context to check if you have internet connection
     * @param email the email address of the user
     * @param password the password for the user
     * @param resListender the response listener for the request
     * @param errorListener the error listener for the request
     *
     * //@see MLBService#createSalt() createSalt()
     *
     * @see MLBService#createHash(String) createHash(String)
     *
     * @see POST#SIGN_IN
     *
     * @see JsonObjectRequest#JsonObjectRequest(int, String, JSONObject, Response.Listener, Response.ErrorListener)
     *          JsonObjectRequest(int, String, JSONObject, Response.Listener, Response.ErrorListener)
     *
     * @throws VolleyError if their is not internet connection
     */
    public static void signIn(final Context context, final String email, String password, final MLBResponseListener resListender,
                              final MLBErrorListener errorListener) throws UnsupportedEncodingException, VolleyError {

        if(token == null && !sentRequestList.contains(POST.SIGN_IN.toString())) {
            try {
                if (!isConnectedToNetwork(context)) throw new VolleyError("No Internet Connection");
                final JSONObject signin = new JSONObject();
                signin.put(JSONKey.EMAIL, email);
                final String hash = createHash(password);
                signin.put(JSONKey.HASH, createHash(password));
                System.out.println("SIGNIN");
                MLBJsonObjectRequest JSONObjReq = new MLBJsonObjectRequest(JsonObjectRequest.Method.POST, API + "signin", signin, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        getUserId(response);
                        token = response.remove(JSONKey.TOKEN).toString();
                        try{
                            MLBService.connectSocket();
                            emit(SocketIO.Event.SETUP_CONNECTION, jsonConnect(response));

                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                        Log.i("MLB_SERVICE_TOKEN",token);
                        resListender.onResponse(response);

                        Tools.DB_CONNECTION.rawSql("DELETE FROM PROFILE");
                        Tools.DB_CONNECTION.rawSql("INSERT INTO PROFILE("+JSONKey.EMAIL+","+JSONKey.HASH+") VALUES('"+email+"','"+hash+"');");

                        sentRequestList.remove(POST.SIGN_IN.toString());
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorListener.onErrorResponse(error);
                        sentRequestList.remove(POST.SIGN_IN.toString());
                    }
                },false);
                sentRequestList.add(POST.SIGN_IN.toString());
                addToQueue(JSONObjReq);
            } catch (JSONException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }


    private static JSONObject jsonConnect(JSONObject signinJson) throws JSONException {
        signinJson = signinJson.getJSONObject(JSONKey.USER);
        JSONObject obj = new JSONObject();
        obj.put(JSONKey.USER_ID, signinJson.getInt(JSONKey.USER_ID));
        System.out.println(signinJson.getInt(JSONKey.USER_ID));
        obj.put(JSONKey.ACCOUNT_TYPE, signinJson.getString(JSONKey.ACCOUNT_TYPE));
        return obj;
    }
   /**
     * Ability to send any request type you must sign in before using this
     *
     * example:
     *      for BARTENDER_PROFILE : sendRequest(ACTION.BARTENDER_PROFILE, resListender,
     *      errorListener, null, userid)
     *
     *      this will send the request to the public profile of the user with the email
     *      example@hotmail.com
     *
     * @param context to check if you have internet connection
     * @param request the request type
     * @param resListender the responce listener
     * @param errorListener the error listener
     * @param jsonRequest the JSON to send along the post request
     * @param urlParams extra parameter to be added to the url
     *
     * @throws VolleyError if their is not internet connection
     */
    public static void sendRequest(final Context context, final ACTION request, final MLBResponseListener resListender,
                                      final MLBErrorListener errorListener, JSONObject jsonRequest, String... urlParams) throws VolleyError{

        if(token != null && !sentRequestList.contains(request.toString())) {

            if (!isConnectedToNetwork(context)) throw new VolleyError("No Internet Connection");

            final int requestMethod = HTTP_METHOD.get(request).second;

            String url = constructURL(request, jsonRequest, requestMethod, urlParams);

            System.out.println(url);

            MLBJsonObjectRequest JSONObjReq = new MLBJsonObjectRequest(requestMethod, API + url, jsonRequest, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    resListender.onResponse(response);
                    sentRequestList.remove(request.toString());
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println(error);
                    errorListener.onErrorResponse(error);
                    sentRequestList.remove(request.toString());
                }
            }, false);
            sentRequestList.add(request.toString());
            addToQueue(JSONObjReq);
        }
    }


    /**
     * Method use to download the image from the INTERNET
     * @param imageUrl The URL of the image
     * @param imageResponseListener On Okay response listener
     * @param errorListener On error response listener
     */
    public static void loadImage(String imageUrl, final MLBImageResponseListener imageResponseListener, final MLBErrorListener errorListener ){

        ImageRequest imgRequest = new MLBImageRequest(imageUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imageResponseListener.onResponse(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorListener.onErrorResponse(error);
                    }
                });
        addToQueue(imgRequest);
    }

    /**
     *
     * @return if user has internet connection
     */
    public static boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



        public static void sendImage(Context context, JSONObject jsonRequest,
                                 final MLBResponseListener resListender, final MLBErrorListener errorListener) throws VolleyError{

        if (!isConnectedToNetwork(context)) throw new VolleyError("No Internet Connection");

        MLBJsonObjectRequest JSONObjReq = new MLBJsonObjectRequest(Request.Method.POST, API + "upload" , jsonRequest, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                resListender.onResponse(response);

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorListener.onErrorResponse(error);
            }
        }, false);

        addToQueue(JSONObjReq);
    }

    /**
     *
     * @param request the request type supported by the MLB RESTFUL API
     * @param jsonRequest the JSON to send along the post request
     * @param requestMethod the http request type
     * @param urlParams extra parameter to be added to the url
     * @return The constructed url to be used when the request is ready to be sent
     */
    private static String constructURL(final ACTION request, JSONObject jsonRequest,final int requestMethod, String... urlParams) {
        String url = HTTP_METHOD.get(request).first;

        if(urlParams != null || urlParams.length > 0){
            for(String path : urlParams){
                url += "/" + path;
            }
        }

        if(requestMethod == JsonObjectRequest.Method.GET){
            url += "?token=" + token;
        }else {
            try{
                jsonRequest.put(JSONKey.TOKEN, token);
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return url;
    }

    /**
     * Adds the request to the request queue which will be handles and sent to the server
     * by Volley
     *
     * @param req the json request
     *
     * @see MLBService#signUp(Context, String, String, String, AccountType, MLBResponseListener, MLBErrorListener)
     *          signUp(String, String, String, AccountType, MLBResponseListener, MLBErrorListener)
     *
     * @see MLBService#signIn(Context, String, String, MLBResponseListener, MLBErrorListener)
     *          signIn(String, String, MLBResponseListener, MLBErrorListener)
     *
     * @see MLBService#sendRequest(Context, ACTION, MLBResponseListener, MLBErrorListener, JSONObject, String...)
     *              sendRequest(ACTIOIN, MLBResponseListener, MLBErrorListener, JSONObject, String...)
     */
    private static void addToQueue(Request req){
        setReqPolicy(req);
        mRequestQuest.add(req);
    }

    /**
     * Ability to change the request policy of the request in the queue
     *
     * @param req the json request object
     *
     * @see JsonObjectRequest#setRetryPolicy(RetryPolicy) setRetryPolicy(RetryPolicy)
     */
    private static void setReqPolicy(Request req){
        req.setRetryPolicy(new DefaultRetryPolicy(
                SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /**
     * The method uses the random salt and concat half before the password
     * and other half after password
     *
     * @param password the password to hash
     * @return sha-256 hash
     * @throws NoSuchAlgorithmException if sha-256 algorithm does not exist
     *
     * @see MLBService#signUp(Context, String, String, String, AccountType, MLBResponseListener, MLBErrorListener)
     *          signUp(String, String, String, AccountType, MLBResponseListener, MLBErrorListener)
     *
     * @see MLBService#signIn(Context, String, String, MLBResponseListener, MLBErrorListener)
     *          signIn(String, String, MLBResponseListener, MLBErrorListener)
     *
     * @see MLBService#sendRequest(Context, ACTION, MLBResponseListener, MLBErrorListener, JSONObject, String...)
     *              sendRequest(ACTIOIN, MLBResponseListener, MLBErrorListener, JSONObject, String...)
     *
     */
    private static String createHash(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        String first = SALT.substring(SALT.length()/2);
        String last = SALT.substring((SALT.length()/2) + 1,SALT.length()-1);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
//        return new String(digest.digest(first.concat(password).concat(last).getBytes("UTF-8")));
        return password;
    }

    public static void stopService() throws InterruptedException {

        service.stopService();
        if(socket.connected()) disconnectSocket();
    }

    /**
     * An event listener which will call the onResonse when server responds back
     * with a message
     *
     * @see MLBService#signUp(Context, String, String, String, AccountType, MLBResponseListener, MLBErrorListener)
     *          signUp(String, String, String, AccountType, MLBResponseListener, MLBErrorListener)
     *
     * @see MLBService#signIn(Context, String, String, MLBResponseListener, MLBErrorListener)
     *          signIn(String, String, MLBResponseListener, MLBErrorListener)
     *
     *
     * @see MLBService#sendRequest(Context, ACTION, MLBResponseListener, MLBErrorListener, JSONObject, String...)
     *              sendRequest(ACTIOIN, MLBResponseListener, MLBErrorListener, JSONObject, String...)
     *
     */
    public interface MLBResponseListener {

        public void onResponse(JSONObject response);
    }

    /**
     * An event listener which will call the onResonse when server responds back with an image
     * with a message
     *
     * @see MLBService#signUp(Context, String, String, String, AccountType, MLBResponseListener, MLBErrorListener)
     *          signUp(String, String, String, AccountType, MLBResponseListener, MLBErrorListener)
     *
     * @see MLBService#signIn(Context, String, String, MLBResponseListener, MLBErrorListener)
     *          signIn(String, String, MLBResponseListener, MLBErrorListener)
     *
     *
     * @see MLBService#sendRequest(Context, ACTION, MLBResponseListener, MLBErrorListener, JSONObject, String...)
     *              sendRequest(ACTIOIN, MLBResponseListener, MLBErrorListener, JSONObject, String...)
     *
     */
    public interface MLBImageResponseListener {

        public void onResponse(Bitmap response);
    }

    public interface MLBDatabaseActivityEventListener {

        public void onResponse(JSONArray response);
    }


    /**
     * An event listener which will call the onErrorResponse when server error occurred or
     * request timed out
     *
     * @see MLBService#signUp(Context, String, String, String, AccountType, MLBResponseListener, MLBErrorListener)
     *          signUp(String, String, String, AccountType, MLBResponseListener, MLBErrorListener)
     *
     * @see MLBService#signIn(Context, String, String, MLBResponseListener, MLBErrorListener)
     *          signIn(String, String, MLBResponseListener, MLBErrorListener)
     *
     * @see MLBService#sendRequest(Context, ACTION, MLBResponseListener, MLBErrorListener, JSONObject, String...)
     *              sendRequest(ACTIOIN, MLBResponseListener, MLBErrorListener, JSONObject, String...)
     */
    public interface MLBErrorListener {
        public void onErrorResponse(VolleyError error);
    }

    public static class ImageUploader {

        private ImageUploader(Bitmap image){}

        public static String encode(Bitmap image) {
            if(image == null) return null;
            ByteArrayOutputStream oStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, oStream);
            return Base64.encodeToString(oStream.toByteArray(), Base64.DEFAULT);
        }
    }

    public static class RequestBuilder {

        private RequestBuilder(){}

        public static JSONObject buildJSONRequest(Pair... pairs) throws JSONException{
            if(pairs == null || pairs.length == 0) return null;
            JSONObject request = new JSONObject();
            for (Pair p: pairs) {

                if(p.second != null) {
                    request.put(p.first.toString(), p.second);
                }
            }
            return request;
        }
    }


    public static void setSocketEvent(SocketIO.EventListener eventListener){

        System.out.println("NAME-------: " + eventListener.getName());

        String eventName = (""+eventListener.getName().charAt(0)).toLowerCase() + eventListener.getName().substring(1).replace("Listener", "");

        System.out.println("THIS IS WOW::::" + eventName);

        String splitBy = "class ";
        String class_name = eventListener.getClass().toString().split("\\$")[0].split(splitBy)[1];

        listeners.put(eventListener.getName(), eventListener);

        setSocketEvent(class_name,eventName, eventListener);

    }

    public static void emit(SocketIO.Event event, JSONObject data){
        socket.emit(EVENTS.get(event),data);
    }

    public static void emit(String event, JSONObject obj){
        socket.emit(event,obj);
    }

    public static void connectSocket(){
        socket.connect();
    }


    /*public static String getActivityName(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        Log.d("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        return componentInfo.getPackageName();*/

    public static void disconnectSocket(){

        socket.disconnect();
        socket.close();
    }

    public static void getResponses(final MLBDatabaseActivityEventListener request){

        String splitBy = "class ";
        final String activity_name = request.getClass().toString().split("\\$")[0].split(splitBy)[1];

        Tools.DB_CONNECTION.query("SELECT * FROM SOCKET_RESPONSE WHERE activity_name = '" + activity_name + "';", new DatabaseHandler.DBRequest() {

            @Override
            public void onResponse(Cursor cursor) {

                if (cursor.moveToFirst()) {
                    String data = cursor.getString(2);

                    JSONArray array = new JSONArray();


                    do {

                        array.put(cursor.getString(2));

                    } while (cursor.moveToNext());

                    request.onResponse(array);
                    Tools.DB_CONNECTION.rawSql("DELETE FROM SOCKET_RESPONSE WHERE activity_name = '"+activity_name+"';");

                }else{

                    request.onResponse(null);
                }
            }
        });
    }

    private static void runOnUI(Handler handler, Runnable runnable){

        handler.post(runnable);
    }

    private static void addToCache(String name, JSONObject data){

        Tools.DB_CONNECTION.rawSql("INSERT INTO SOCKET_RESPONSE(activity_name, response) VALUES( '"+name+"' , '" +data.toString()+ "');");
    }

    private static void setDefaultSocketEvent(String name){

        final String eventName = (""+name.charAt(0)).toLowerCase() + name.substring(1).replace("Listener", "");

        socket.on(eventName, new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                addToCache(eventName, (JSONObject) args[0]);
            }
        });
    }

    private static void setSocketEvent(final String class_name, final String event_name, final SocketIO.EventListener listener){

        if(socket.hasListeners(event_name)) socket.off(event_name);

        System.out.println("class: " + class_name + " event: " + event_name);

        Tools.DB_CONNECTION.rawSql("UPDATE SOCKET_RESPONSE SET activity_name = '" + class_name + "' WHERE activity_name = '" + event_name + "';");

        socket.on(event_name, new Emitter.Listener() {

            @Override
            public void call(final Object... args) {

                runOnUI(handler, new Runnable() {

                    @Override
                    public void run() {

                        JSONObject object = (JSONObject) args[0];

                        String key = listener.getName();

                        if(listeners.containsKey(key)){

                            if(Tools.GetCurrentActivityName().equals(class_name)) {

                                listeners.get(key).onResponse(object);

                            }else{

                                addToCache(class_name, object);
                            }

                        }else{

                            addToCache(event_name, object);
                        }
                    }
                });
            }
        });
    }

    private static void setListeners(){

        setDefaultSocketEvent("BarStaffJoinedListener");

        setDefaultSocketEvent("BarStaffAppliedListener");

        setDefaultSocketEvent("PrivateJobAcceptedListener");

        setDefaultSocketEvent("PrivateJobRejectedListener");

        setDefaultSocketEvent("BarStaffRejectedApplication");

        setDefaultSocketEvent("JobCreatedListener");

        setDefaultSocketEvent("JobDeletedListener");

        setDefaultSocketEvent("JobPostedListener");

        setDefaultSocketEvent("JobAcceptedListener");

        setDefaultSocketEvent("JobRejectedListener");

        setDefaultSocketEvent("PrivateJobReceivedListener");

        setDefaultSocketEvent("ChatMessageReceivedListener");

        setDefaultSocketEvent("JobPayRateOfferReceivedListener");

        setDefaultSocketEvent("JobPayRateOfferRejectedListener");

        setDefaultSocketEvent("ContractMadeListener");

        setDefaultSocketEvent("JobCompletedListener");

        setDefaultSocketEvent("ProfileChangeListener");

        setDefaultSocketEvent("CardUpdatedListener");

    }
}