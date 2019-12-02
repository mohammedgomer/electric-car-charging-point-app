package uk.ac.mmu.electricchargingproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MyDataBase extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "station_db.db";     ///  databaes name

    // Contacts table name
    private static final String TABLE_CONTACTS = "data";            //// table  name

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_latitude = "latitude";
    private static final String KEY_longitude="longitude";
    private static final String KEY_paymentRequired="paymentRequired";
    private static final String KEY_buildingName="buildingName";
    private static final String KEY_thoroughfare="thoroughfare";
    private static final String KEY_street="street";
    private static final String KEY_town="town";
    private static final String KEY_county="county";
    private static final String KEY_postcode="postcode";
    private static final String KEY_locationLongDescription="locationLongDescription";
    private static final String KEY_locationType="locationType";

    /////
    public  String DB_PATH=null;
    private final Context myContext;
    private SQLiteDatabase myDataBase;

    //
    public MyDataBase(Context context)
    {
        super(context,DATABASE_NAME , null, DATABASE_VERSION);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {    ///  this will create database when you create the object of this class

        String  Create_Table="CREATE TABLE IF NOT EXISTS "+TABLE_CONTACTS+" ("+KEY_ID+" Integer PRIMARY KEY AUTOINCREMENT,"
                +KEY_NAME+" Text,"
                +KEY_latitude+" REAL,"
                +KEY_longitude+" REAL,"
                +KEY_paymentRequired+" boolean,"
                +KEY_buildingName+" Text,"
                +KEY_thoroughfare+" Text,"
                +KEY_street+" Text,"
                +KEY_town+" Text,"
                +KEY_county+" Text,"
                +KEY_postcode+" Text,"
                +KEY_locationLongDescription+" Text,"
                +KEY_locationType+" Text"+")";

        sqLiteDatabase.execSQL(Create_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS data");
        onCreate(db);
    }

    public  void delete_all()
    {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_CONTACTS, null, null);
        database.close();
    }

    public void add_Station(ArrayList<MyDataModel> myDataModelArrayList)
    {
        SQLiteDatabase database = this.getWritableDatabase();

        for (MyDataModel model : myDataModelArrayList)
        {
            ContentValues contentValue = new ContentValues();
            contentValue.put(KEY_NAME, model.getName());
            contentValue.put(KEY_latitude,model.getLatitude());
            contentValue.put(KEY_longitude,model.getLongitude());
            contentValue.put(KEY_paymentRequired,model.isPaymentRequired());

            database.insert(TABLE_CONTACTS, null, contentValue);
        }

    }

    // Getting All Contacts
    public ArrayList<MyDataModel> getAllContacts(LatLng latLngCurrent)    ////  this will return the all the data in the array list to the main Activity
    {

        ArrayList<MyDataModel> dataList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);



        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MyDataModel model = new MyDataModel();
                model.setName(cursor.getString(1));
                model.setLatitude(cursor.getDouble(2));
                model.setLongitude(cursor.getDouble(3));
                model.setPaymentRequired(cursor.getInt(4) != 0);

                LatLng latLngNew = new LatLng(Double.valueOf(model.getLatitude()),Double.valueOf(model.getLongitude()));
                model.setDistance(calculate_distance(latLngCurrent,latLngNew));

                dataList.add(model);                ///  add data from cursor to arraylist
            } while (cursor.moveToNext());
        }

        // return data list
        return dataList;
    }


    public double calculate_distance(LatLng currentlatlng, LatLng newLatlng)   ///  this method calculate the distance between two locations
    {
        Location locationCurrent = new Location("point A");         ///// location one
        locationCurrent.setLatitude(currentlatlng.latitude);
        locationCurrent.setLongitude(currentlatlng.longitude);

        Location locationB = new Location("point B");                //// location two
        locationB.setLatitude(newLatlng.latitude);
        locationB.setLongitude(newLatlng.longitude);

        double distance = locationCurrent.distanceTo(locationB);      /// return distance between two points


        return  getMiles(distance);                                    ///  convert distance from meters to the miles

    }

    public double getMiles(double i) {
        return i*0.000621371192;
    }     ///  meter to miles method



}