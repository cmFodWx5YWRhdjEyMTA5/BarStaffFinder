package com.cavaliers.mylocalbartender.tools.access;


import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Spinner;

import com.cavaliers.mylocalbartender.home.search.model.AdvertData;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Collection of tools to verify User ID inputs
 * Object cannot be instantiated. All methods are static.
 */

public class AccessVerificationTools {

    private static Date date;
    private static SimpleDateFormat formatYYYMMDDetc = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
    private static SimpleDateFormat formatDDMMYYYetc = new SimpleDateFormat("dd-MM-yyy HH:mm:ss a", Locale.ENGLISH);
    private static SimpleDateFormat formatHHMMSSa = new SimpleDateFormat("HH:mm:ss a", Locale.ENGLISH);
    private static final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.UK);
    private static boolean isOrganiser;
    private static boolean isFormCompleted;
    public static String []months = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};
    public static String[]days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday", };


    private AccessVerificationTools(){}


    /**
     * To be used at SignInDialog with value returned by Server in order to verify
     *  if access has been made by an ORGANISER profile or BAR_STAFF profile
     * @param profile_id
     */
    public static void setProfile(String profile_id){
        isOrganiser = profile_id.equals("organiser");
    }

    /**
     * Retrives boolean value TRUE if SIGNED_IN as ORGANISER, FALSE if SIGNED_IN as BAR_STAFF
     * @return
     */
    public static boolean isOrganiser(){
        return isOrganiser;
    }

    /**
     * Retrives boolean value TRUE if FORM is COMPLETED, FALSE if form is NOT-COMPLETED
     * @return
     */
    public static boolean isFormCompleted(){
        return isFormCompleted;
    }

    /**
     * To be used at SignInDialog in order to verify
     *  if user has completed compulsory form
     */
    public static void setFormCompleted(){
        isFormCompleted = true;
    }

    /**
     * Evaluates if given e-mail address is well formed as recognised by western standard
     * @param emailAddress e-mail address to evaluate
     * @return  TRUE if e-mail is well-formed, FALSE if e-mail is not well-formed
     */
    public static boolean isEmailFormatValid(String emailAddress){
        Pattern p = Pattern.compile("([\\w-\\.]+[_]?)@(\\1)?[-\\w\\.]+(\\1)?\\.[\\w]{2,}");
        Matcher m = p.matcher(emailAddress);
        boolean b = m.matches();
        return b;
    }


    /**
     * Takes two passwords in String form and evaluates their equality
     * @param passwordField1 first password entry
     * @param passwordField2 second password entry
     * @return TRUE if equal, FALSE if non-equal
     */
    public static boolean isSamePassword(String passwordField1, String passwordField2){
        return passwordField1.equals(passwordField2);
    }


    /**
     * Takes datae of birth in compounded-date form and calculates year
     * @param dateOfBirth
     * @return TRUE if age to current date is >= 18, FALSE if age to current date is < 18
     */
    public static boolean isAdult(String dateOfBirth){
        try {
            formatYYYMMDDetc = new SimpleDateFormat("yyyyMMdd");
            date = new Date();
            String subtraction = String.valueOf(Integer.valueOf(formatYYYMMDDetc.format(date).toString()) - Integer.valueOf(dateOfBirth));
            int age = Integer.valueOf(subtraction.length() > 6 ? subtraction.substring(0, 3) : subtraction.substring(0, 2));
            return age >= 18;
        }catch (StringIndexOutOfBoundsException ex){
            System.out.print("FAILED because of INDEX");
            return  false;
        }catch (NumberFormatException ex){
            System.out.print("FAILED because of number FORMAT");
            return  false;
        }
    }


    /**
     * Generates compounded date Egs. given (11,7,1990) returns "19900711"
     * @param day Day value to be formatted
     * @param month Month value to be formatted
     * @param year Year value to be formatted
     * @return Formatted date as String
     */
    public static String stringifyDate(int day, int month, int year){
        String userYear = String.valueOf(year);
        String userMonth = zeroAdder(String.valueOf(month));
        String userDay = zeroAdder(String.valueOf(day));
        return userYear.concat(userMonth).concat(userDay);
    }

    /**
     * Generates formatted date Egs. given (11,7,1990) returns "1990-07-11"
     * @param day Day value to be formatted
     * @param month Month value to be formatted
     * @param year Year value to be formatted
     * @return Formatted date as String
     */
    public static String dateFormatter(int day, int month, int year, boolean isDbFormat){

        String userYear = String.valueOf(year);
        String userMonth = zeroAdder(String.valueOf(month));
        String userDay = zeroAdder(String.valueOf(day));


        if(isDbFormat) return userYear+"-"+userMonth+"-"+userDay;

        else return userDay+"-"+userMonth+"-"+userYear;
    }


    /**
     * Generates formatted date Egs. given (19900711) returns "1990-07-11"
     * @param date Date to be formatted
     * @return Formatted date as String
     */
    public static String dateFormatter(String date){
        String userYear = date.substring(0,4);
        String userMonth = date.substring(4,6);
        String userDay = date.substring(6,date.length());
        return userYear+"-"+userMonth+"-"+userDay;
    }
//
//    public static String dateFormatter(String date){
//        date = Calendar.DATE
//        return userYear+"-"+userMonth+"-"+userDay;
//    }



    public static String timeFormatter(int hour, int minute, int amPm){

        String userHour = zeroAdder(String.valueOf(hour));
        String userMinute = zeroAdder(String.valueOf(minute));


        return userHour+":"+userMinute+" "+(amPm == 0 ? "AM" :"PM");
    }



    public static String getParsedDate(String the_date){
        try {
            date = AccessVerificationTools.formatDDMMYYYetc.parse(the_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  AccessVerificationTools.formatDDMMYYYetc.format(date).split(" ")[0];
    }

    public static String getParsedDay(String the_date){
        try {
            date = AccessVerificationTools.formatDDMMYYYetc.parse(the_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] fullDate = AccessVerificationTools.formatDDMMYYYetc.format(date).split(" ")[0].split("-");
        return String.valueOf(Integer.parseInt(fullDate[0]));

    }

    public static String getParsedMonth(String the_date){
        try {
            date = AccessVerificationTools.formatDDMMYYYetc.parse(the_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] fullDate = AccessVerificationTools.formatDDMMYYYetc.format(date).split(" ")[0].split("-");
        return String.valueOf(Integer.parseInt(fullDate[1]));

    }

    public static String getParsedYear(String the_date){
        try {
            date = AccessVerificationTools.formatDDMMYYYetc.parse(the_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] fullDate = AccessVerificationTools.formatDDMMYYYetc.format(date).split(" ")[0].split("-");
        return String.valueOf(Integer.parseInt(fullDate[2]));
    }


    public static String getParsedTime(String the_date, boolean isDatabase){
        try {
            date = AccessVerificationTools.formatYYYMMDDetc.parse(the_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] time = AdvertData.D_FORMAT.format(date).split(" ")[1].split(":");

        String hour = String.valueOf(time[0]);
        String minutes = String.valueOf(time[1]);
        String amPm = AdvertData.D_FORMAT.format(date).split(" ")[2];

        if(isDatabase)  return hour+":"+minutes+":"+00+" "+amPm;
        return hour+":"+minutes+" "+amPm;
    }

    public static String getParsedHourMin(String the_date){
        try {
            date = AccessVerificationTools.formatHHMMSSa.parse(the_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] time = formatHHMMSSa.format(date).split(" ")[0].split(":");

        String hour = String.valueOf(time[0]);
        String minutes = String.valueOf(time[1]);

        return hour+":"+minutes;
    }

    public static String getTimeFromDate(String the_date){
        try {
            date = AccessVerificationTools.formatDDMMYYYetc.parse(the_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] time = formatDDMMYYYetc.format(date).split(" ");

        String hour = time[1].split(":")[0];
        String minutes = time[1].split(":")[1];
        //String amPm = time[2];

        return hour+":"+minutes;
    }

    public static String getParsedHour(String the_date){
        try {
            date = AccessVerificationTools.formatDDMMYYYetc.parse(the_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] time = formatHHMMSSa.format(date).split(" ")[0].split(":");

        return String.valueOf(time[0]);
    }

    public static String getParsedMin(String the_date){
        try {
            date = AccessVerificationTools.formatDDMMYYYetc.parse(the_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] time = formatHHMMSSa.format(date).split(" ")[0].split(":");

        return String.valueOf(time[1]);
    }

    public static String getParsedAmPm(String the_date){
        try {
            date = AccessVerificationTools.formatHHMMSSa.parse(the_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatHHMMSSa.format(date).split(" ")[1];
    }

    public static String getAmPmfromDate(String the_date){
        try {
            date = AccessVerificationTools.formatYYYMMDDetc.parse(the_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatYYYMMDDetc.format(date).split(" ")[2];
    }

    public static String dateZeroAdder(String year, String month, String day, String hour, String seconds, String amPm){
        return AccessVerificationTools.zeroAdder(year)+"-"+AccessVerificationTools.zeroAdder(month)+"-"+AccessVerificationTools.zeroAdder(day)+
                " "+convertTime(AccessVerificationTools.zeroAdder(hour)+":"+AccessVerificationTools.zeroAdder(seconds)+" "+amPm);

    }

    private static String convertTime(String time){
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");

        Date date = null;
        try {
            date = parseFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(date != null)
        return displayFormat.format(date);

        return "";
    }



    public static String getParsedRate(String rate) {
        return currency.format(Integer.parseInt(rate)) + "/hr";
    }


    public static String getParsedDuration(String duration) {
        return duration+" Hrs";
    }



    /**
     * Adds "0" in front of given numeric value whereif it is a digit (Egs. given "1" returns "01")
     * @param number Number to be evaluated
     * @return Digit in ## format as String
     */
    public static String zeroAdder(String number){
        return number.length() > 1 ? number : 0+number;
    }


    public static String dateOrdiner(int day, int month, int year){
        return numberOrdiner(day)+" "+months[month]+" "+year;
    }


    /**
     * Generates number in ordinal string format
     * @param number Number to be evaluated
     * @return String format of ordinal formatted number
     */
    public static String numberOrdiner(int number){
        String num = String.valueOf(number);
        String digit = String.valueOf(num.charAt(num.length()-1));

        if(num.length() > 1 &&
                (num.substring(num.length()-2, num.length()).equals("11") ||
                        num.substring(num.length()-2, num.length()).equals("12") ||
                        num.substring(num.length()-2, num.length()).equals("13"))){
            return number+""+"th ";
        }
        else {
            switch(digit){
                case "1":  return number+""+"st";
                case "2":  return number+""+"nd";
                case "3":  return number+""+"rd";
                default:  return number+""+"th";
            }
        }
    }

    /**
     * Sets a error (with a message displayed) when an EditText field has no data input.
     *
     * @param field - specific EditText field
     * @param message - alert message to be displayed
     *
     */
    public static void isIncomplete(EditText field, String message){
        field.requestFocus();
        // field.setError("Error");
    }

    /**
     * Checks whether a valid item (Female or Male) has been selected on the Spinner.
     * If nothing has been selected, then Spinner is set to a red colour.
     * If something has been selected, the Spinner is untouched.
     *
     * @param spinner - specfic Spinner field
     * @return boolean - false means nothing has been selected, true means a valid option has been selected
     *
     */
    public static int isItemSelected(Spinner spinner) {


        if (spinner.getSelectedItem().toString().equals("Gender")){

            spinner.setBackgroundColor(Color.parseColor("#800000"));

            return 1;

        } else {

            spinner.setBackgroundColor(Color.parseColor("#dbdbdb"));
            return 0;

        }

    }


    /**
     * Checks whether EditText field is empty.
     *
     * @param fields - array of EditText fields,
     * @return int - acts as counter, see how many fields are empty
     */
    public static int isEmpty(EditText[] fields, Spinner spinner){

        int counter;

        if (spinner != null){

            counter = isItemSelected(spinner);

        }else{

            counter = 0;

        }



        for (EditText field : fields){ //loop through all fields in the EditText field array

            if (field.getText().toString().equals("")){ //if nothing has been entered

                counter++; //increment counter

                field.setHint("Please fill in this field");
                field.setHintTextColor(Color.parseColor("#800000"));

            }

        }

        return counter;

    }

    /**
     * Checks if EditText field is left empty and gives the user an error (via isIncomplete) as user is typing.
     *
     * @param field - specfic EditText field
     */
    public static void setEditTextListener(final EditText field){

        field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if (s.toString().equals("")){
                    isIncomplete(field, "Please complete this field!");
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().equals("")){

                    isIncomplete(field, "Please complete this field!");

                }

            }

            @Override
            public void afterTextChanged(Editable s) {


                if (s.toString().equals("")){

                    isIncomplete(field, "Please complete this field!");

                }

            }
        });

    }

}
