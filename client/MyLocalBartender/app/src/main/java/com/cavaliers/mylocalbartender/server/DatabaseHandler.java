package com.cavaliers.mylocalbartender.server;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cavaliers.mylocalbartender.tools.Tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public class DatabaseHandler {
    private final static String TAG = "DBConnection";
    private SQLiteDatabase db;
    private DatabaseConnection dbConnection = null;

    DatabaseHandler(){}

    /**
     * set up the db for future use
     *
     * @param context the context to start the process
     *
     * @see DatabaseHandler#open() open()
     */
    void loadDB(Context context){

        if(dbConnection == null) {

            dbConnection = new DatabaseConnection(context);
            open();
        }
    }

    /**
     * Open the database for read and write
     *
     * @throws SQLException if database could not be found
     *
     * @see DatabaseConnection#loadDB(Context) loadDB(Context)
     */
    public void open() throws SQLException {

        dbConnection.openDatabase();
        db = dbConnection.getReadableDatabase();
    }

    /**
     * closes the connection to the database
     */
    public  void close() {

        if(dbConnection != null) dbConnection.close();
    }

    /**
     * Test function to get content from the CHAT table
     *
     * @return row from the CHAT table
     */
    public void getTestData(final DBRequest request){

        submitTask(new Runnable() {

            @Override
            public void run() {

                String sql ="SELECT * FROM CHAT";

                final Cursor cursor = db.rawQuery(sql, null);

                if (cursor != null) cursor.moveToNext();

                ((Activity) dbConnection.mContext).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        request.onResponse(cursor);
                    }
                });
            }
        });
    }

    public void addDataToChat(int event_id, String sender, String receiver, String message, Date date){

        rawSql("INSERT INTO CHAT(event_id,sender,receiver,message,date_time) VALUES("+ event_id +",'"+ sender +"','" +
                receiver +"','"+ message +"','"+ Tools.DATE_FORMAT.format(date) +"')");
    }

    /**
     *
     * @param query raw sql query
     *
     * @return Cursor pointing to data in the database
     */
    public void query(final String query, final DBRequest request){

        submitTask(new Runnable() {

            @Override
            public void run() {

                final Cursor cursor = db.rawQuery(query, null);

                ((Activity) dbConnection.mContext).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        request.onResponse(cursor);
                    }
                });
            }
        });
    }

    /**
     * function to apply raw sql statement
     *
     * @param sql raw sql statement
     */
    public void rawSql(final String sql){

        submitTask(new Runnable() {

            @Override
            public void run() {

                db.execSQL(sql);
            }
        });
    }

    /**
     * clears the CHAT table
     */
    public void clearMessageTable(){

        rawSql("DELETE FROM CHAT");
    }

    public interface DBRequest{

        public void onResponse(Cursor cursor);
    }

    private void submitTask(Runnable runnable){

        MLBService.submitTask(runnable, null);
    }

    private class DatabaseConnection extends SQLiteOpenHelper {

        private final static String DB_NAME ="mylocalbartender.db";
        private final Context mContext;
        private String DB_PATH = null;
        private SQLiteDatabase mDB;

        private DatabaseConnection(Context context) {
            super(context, DB_NAME, null, 1);// 1? Its database Version

            mContext = context;

            if(android.os.Build.VERSION.SDK_INT >= 17){
                DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
            }
            else {

                DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
            }

            try {

                createDatabase();

            } catch (IOException e) {

                Log.i(TAG,"UnableToCreateDatabase");
                e.printStackTrace();
            }
        }

        private void createDatabase() throws IOException {

            //If the database does not exist, copy it from the assets.
            boolean mDataBaseExist = checkDatabase();
            if(!mDataBaseExist)
            {
                this.getReadableDatabase();
                this.close();
                try {

                    //Copy the database from assets folder to project cache
                    copyDatabase();
                    Log.i(TAG, "database created");
                }
                catch (IOException e){

                    e.printStackTrace();
                }
            }
        }

        //Check that the database exists in cache
        private boolean checkDatabase()
        {
            File dbFile = new File(DB_PATH + DB_NAME);
            return dbFile.exists();
        }

        //Copy the database from assets folder
        private void copyDatabase() throws IOException
        {
            InputStream in = mContext.getAssets().open(DB_NAME);
            String outFileName = DB_PATH + DB_NAME;
            Log.i(TAG,"output: " + outFileName);
            OutputStream out = new FileOutputStream(outFileName);
            byte[] mBuffer = new byte[1024];
            int mLength;
            while ((mLength = in.read(mBuffer))>0) {

                out.write(mBuffer, 0, mLength);
            }

            out.flush();
            out.close();
            in.close();
        }

        //Open the database, so we can query it
        private boolean openDatabase() throws SQLException {

            String mPath = DB_PATH + DB_NAME;
            mDB = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.OPEN_READWRITE);

            return mDB != null;
        }

        @Override
        public synchronized void close() {

            if(mDB != null) mDB.close();
            super.close();
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {}

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}
    }
}