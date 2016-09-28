package com.akash.vachana.dbUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by akash on 9/13/16.
 */
public class MainDbHelper extends SQLiteOpenHelper {
    // All Static variables
    private static String DB_PATH;

    public static final String DATABASE_NAME = "main.db";
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;

    // Contacts table name
    private static final String TABLE_KATHRU = "Kathru";
    // Contacts Table Columns names
    private static final String KEY_KATHRU_ID = "Id";
    private static final String KEY_NAME = "Name";
    private static final String KEY_ANKITHA = "Ankitha";
    private static final String KEY_NUMBER  = "Num";
    private static final String KEY_DETAILS= "Details";


    ////// Vachana Table
    private static final String TABLE_VACHANA = "Vachana";
    // Table Columns names
    private static final String KEY_VACHANA_ID = "Id";
    private static final String KEY_TEXT = "Txt";
    private static final String KEY_TITLE = "Title";
    private static final String FOREIGN_KEY_KATHRU_ID = "KathruId";
    private static final String KEY_FAVORITE = "Favorite";

    private static Context mContext;
    private static SQLiteDatabase mDataBase;

    public MainDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if (android.os.Build.VERSION.SDK_INT >= 4.2) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.mContext = context;
    }


    /**
     * Creates a empty database on the system and rewrites it with your own
     * database.
     */
    public void createDataBase() throws IOException {

        // If database not exists copy it from the assets

        boolean mDataBaseExist = checkDataBase();

        if (!mDataBaseExist) {
            this.getReadableDatabase();
            this.close();
            try {
                // Copy the database from assests
                copyDataBase();
                Log.e("DataBaseHelper", "createDatabase database created");
            } catch (IOException mIOException) {
                throw new Error("Error Copying DataBase");
            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each
     * time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */

    //Check that the database exists here: /data/data/your package/databases/Da Name
    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DATABASE_NAME);
//        Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }


    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {
        InputStream mInput = mContext.getAssets().open(DATABASE_NAME);
        String outFileName = DB_PATH + DATABASE_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();

    }

    public void openDataBase() throws SQLException {

        // Open the database
        String myPath = DB_PATH + DATABASE_NAME;
        mDataBase = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if (mDataBase != null)
            mDataBase.close();

        super.close();

    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
/*
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_KATHRU + "("
                + KEY_KATHRU_ID+ " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_ANKITHA + "TEXT,"
                + KEY_NUMBER + "INT,"
                + KEY_DETAILS + "TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);
*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_KATHRU);
        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public KathruMini getKathruMiniById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_KATHRU, new String[] { KEY_KATHRU_ID,
                        KEY_NAME, KEY_ANKITHA, KEY_NUMBER}, KEY_KATHRU_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        String name = cursor.getString(1);
        return new KathruMini(Integer.parseInt(cursor.getString(0)), name, cursor.getString(2),
                cursor.getInt(3));
    }

    public ArrayList<KathruMini> getAllKathruMinis(){
        ArrayList<KathruMini> contactList = new ArrayList<KathruMini>();
        String selectQuery = "SELECT  * FROM " + TABLE_KATHRU + " ORDER BY Name";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                String name = cursor.getString(1);
                String ankitha = cursor.getString(2);
                int num = cursor.getInt(3);
//                String details = cursor.getString(2);

                KathruMini contact = new KathruMini(id, name, ankitha, num);

                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        return contactList;
    }

    public Kathru getKathruById (int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_KATHRU, new String[] { KEY_KATHRU_ID,
                        KEY_NAME, KEY_ANKITHA, KEY_NUMBER, KEY_DETAILS}, KEY_KATHRU_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        String name = cursor.getString(1);
        ArrayList<VachanaMini> vachanaMinis = getVachanaMinisByKathruId(id, name);

        return new Kathru(Integer.parseInt(cursor.getString(0)), name, cursor.getString(2),
                cursor.getInt(3), vachanaMinis);
    }

    public ArrayList<VachanaMini> getVachanaMinisByKathruId (int kathruId, String kathruName) {
        ArrayList<VachanaMini> vachanaMinis = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_VACHANA, new String[] { KEY_VACHANA_ID, KEY_TITLE, KEY_FAVORITE},
                FOREIGN_KEY_KATHRU_ID + "=?",
                new String[] { String.valueOf(kathruId) }, null, null, KEY_TITLE, null);

        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                String title = cursor.getString(1);
                VachanaMini vachanaMini = new VachanaMini(id, kathruId, kathruName, title, cursor.getInt(2));
                vachanaMinis.add(vachanaMini);
            } while (cursor.moveToNext());
        }

        return vachanaMinis;
    }

    public ArrayList<VachanaMini> getVachanaMinisByKathruId (int kathruId) {
        return  getVachanaMinisByKathruId(kathruId, getKathruNameById(kathruId));
    }

    public String getKathruNameById(int kathruId) { return getKathruById(kathruId).getName(); }

    public Vachana getFirstVachana(int kathruId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_VACHANA, new String[] { KEY_VACHANA_ID, KEY_TEXT, KEY_FAVORITE},
                FOREIGN_KEY_KATHRU_ID + "=?",
                new String[] { String.valueOf(kathruId) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        int id = Integer.parseInt(cursor.getString(0));
        String text = cursor.getString(1);
        return new Vachana(id, text, getKathruNameById(kathruId), cursor.getInt(2)==1? true : false);
    }

    public Vachana getVachana(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_VACHANA, new String[] { KEY_VACHANA_ID, KEY_TEXT, FOREIGN_KEY_KATHRU_ID,
                KEY_FAVORITE},
                KEY_VACHANA_ID+ "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        String text = cursor.getString(1);
        int kathruId = Integer.parseInt(cursor.getString(2));
        return new Vachana(id, text, getKathruNameById(kathruId), cursor.getInt(3)==1? true : false);
    }

    public void addVachanaToFavorite(int vachanaId){
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put(KEY_FAVORITE, 1);

        String[] args = new String[]{String.valueOf(vachanaId)};
        db.update(TABLE_VACHANA, newValues, KEY_VACHANA_ID+"=?", args);
    }

    public void removeVachanaToFavorite(int vachanaId){
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put(KEY_FAVORITE, 0);

        String[] args = new String[]{String.valueOf(vachanaId)};
        db.update(TABLE_VACHANA, newValues, KEY_VACHANA_ID+"=?", args);
    }

    public boolean getVachanaFavorite(int vachanaId){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_VACHANA, new String[] { KEY_FAVORITE},
                KEY_VACHANA_ID+ "=?",
                new String[] { String.valueOf(vachanaId) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return Integer.parseInt(cursor.getString(0)) == 1? true : false;
    }

    public ArrayList<VachanaMini> getFavoriteVachanaMinis() {
        ArrayList<VachanaMini> vachanaMinis = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_VACHANA, new String[] { KEY_VACHANA_ID, KEY_TITLE, FOREIGN_KEY_KATHRU_ID},
                KEY_FAVORITE + "=?",
                new String[] { String.valueOf(1) }, null, null, KEY_TITLE, null);

        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                String title = cursor.getString(1);
                int kathruId = cursor.getInt(2);
                VachanaMini vachanaMini = new VachanaMini(id, kathruId, getKathruNameById(kathruId),
                        title, 1);
                vachanaMinis.add(vachanaMini);
            } while (cursor.moveToNext());
        }

        return vachanaMinis;
    }
}
