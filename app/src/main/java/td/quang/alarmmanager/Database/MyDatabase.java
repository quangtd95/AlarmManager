package td.quang.alarmmanager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import td.quang.alarmmanager.Models.Alarm;

/**
 * Created by Quang_TD on 12/10/2016.
 */
public class MyDatabase {
    public static final String DATABASE_NAME = "Alarm.db";
    public static final String TABLE_NAME = "alarm";
    private static final int VERSION = 1;
    private static final String COLUMN_ID = "Id";
    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_H = "Hour";
    private static final String COLUMN_M = "Minute";
    private static final String COLUMN_SOUND = "Sound";
    private static final String COLUMN_DAY = "Day";
    private static final String COLUMN_REPEAT = "Repeat";
    private static final String COLUMN_STATUS = "Status";
    private static final String COLUMN_SKIP ="Skip";
    private static final String COLUMN_REMAIN = "Remain";
    private static final String COLUMN_REMAIN_HOUR = "RemainHour";
    private static final String COLUMN_REMAIN_MINUTE = "RemainMinute";

    private static MyDatabase instance;
    private static SQLiteDatabase database;
    private Context context;
    private static OpenHelper openHelper;

    public MyDatabase(Context context) {
        this.context = context;
    }

    public static MyDatabase getInstance(Context context) {
        if (instance == null) instance = new MyDatabase(context);
        return instance;
    }

    public void open() {
        if (openHelper == null) openHelper = new OpenHelper(context, DATABASE_NAME, null, VERSION);
        if (database == null) database = openHelper.getWritableDatabase();
    }

    public void close() {
        if (openHelper != null)
            openHelper.close();
    }

    public Alarm convertToAlarm(Cursor cursor) {
        Alarm alarm = new Alarm();
        alarm.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
        alarm.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
        alarm.setDay(cursor.getString(cursor.getColumnIndex(COLUMN_DAY)));
        alarm.setH(cursor.getInt(cursor.getColumnIndex(COLUMN_H)));
        alarm.setM(cursor.getInt(cursor.getColumnIndex(COLUMN_M)));
        alarm.setSound(cursor.getInt(cursor.getColumnIndex(COLUMN_SOUND)));
        alarm.setRepeat(cursor.getInt(cursor.getColumnIndex(COLUMN_REPEAT)));
        alarm.setStatus(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)));
        alarm.setSkip(cursor.getInt(cursor.getColumnIndex(COLUMN_SKIP)));
        alarm.setRemain(cursor.getInt(cursor.getColumnIndex(COLUMN_REMAIN)));
        alarm.setrH(cursor.getInt(cursor.getColumnIndex(COLUMN_REMAIN_HOUR)));
        alarm.setrM(cursor.getInt(cursor.getColumnIndex(COLUMN_REMAIN_MINUTE)));
        return alarm;
    }

    public ContentValues convertToContentValues(Alarm alarm) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, alarm.getName());
        contentValues.put(COLUMN_H, alarm.getH());
        contentValues.put(COLUMN_M, alarm.getM());
        contentValues.put(COLUMN_SOUND, alarm.getSound());
        contentValues.put(COLUMN_DAY, alarm.getDay());
        contentValues.put(COLUMN_REPEAT, alarm.getRepeat());
        contentValues.put(COLUMN_STATUS, alarm.getStatus());
        contentValues.put(COLUMN_SKIP,alarm.getSkip());
        contentValues.put(COLUMN_REMAIN,alarm.getRemain());
        contentValues.put(COLUMN_REMAIN_HOUR,alarm.getrH());
        contentValues.put(COLUMN_REMAIN_MINUTE,alarm.getrM());
        return contentValues;
    }

    public void onoff(int id, boolean b) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS, (b) ? 1 : 0);
        database.update(TABLE_NAME, contentValues, COLUMN_ID + " = " + id, null);
    }

    public void delete(int id) {
        database.delete(TABLE_NAME, COLUMN_ID + " = " + id, null);
    }

    public void update(Alarm alarm) {
        database.update(TABLE_NAME, convertToContentValues(alarm), COLUMN_ID + " = " + alarm.getId(), null);
    }

    public void add(Alarm alarm) {
        database.insert(TABLE_NAME, null, convertToContentValues(alarm));
    }

    public ArrayList<Alarm> getAll() {
        Cursor cursor;
        try {
            ArrayList<Alarm> alarms = new ArrayList<>();
            String sql = "SELECT * FROM " + TABLE_NAME;
            cursor = database.rawQuery(sql, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Alarm alarm = convertToAlarm(cursor);
                alarms.add(alarm);
            }
            return alarms;
        } catch (Exception e) {
            Log.e("TAGG", e.getMessage());
            return null;
        }
    }

    public ArrayList<Alarm> getAllWaiting() {
        Cursor cursor;
        try {
            ArrayList<Alarm> alarms = new ArrayList<>();
            String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = 1";
            cursor = database.rawQuery(sql, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Alarm alarm = convertToAlarm(cursor);
                alarms.add(alarm);
            }
            return alarms;
        } catch (Exception e) {
            Log.e("TAGG", e.getMessage());
            return null;
        }
    }
    public boolean iSkip(int id){
        Cursor cursor;
        String sql = "SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_ID +" = "+id;
        cursor= database.rawQuery(sql,null);
        cursor.moveToFirst();
        Alarm alarm = convertToAlarm(cursor);
        if (alarm.getSkip()==1) return true;
        return false;
    }
    public void setSkip(int id,boolean b){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SKIP,(b)?1:0);
        database.update(TABLE_NAME,contentValues,COLUMN_ID + " = "+id,null);
    }
    public boolean isRemain(int id){
        Cursor cursor;
        String sql = "SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_ID +" = "+id;
        cursor= database.rawQuery(sql,null);
        cursor.moveToFirst();
        Alarm alarm = convertToAlarm(cursor);
        if (alarm.getRemain()==1) return true;
        return false;
    }
    public void setRemain(int id,boolean b){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_REMAIN,(b)?1:0);
        database.update(TABLE_NAME,contentValues,COLUMN_ID + " = "+id,null);
    }
    public void setRemainTime(int id,int rH,int rM){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_REMAIN_HOUR,rH);
        contentValues.put(COLUMN_REMAIN_MINUTE,rM);
        database.update(TABLE_NAME,contentValues,COLUMN_ID + " = "+id,null);
    }

    public int getRemainHour(int id){
        Cursor cursor;
        String sql = "SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_ID +" = "+id;
        cursor= database.rawQuery(sql,null);
        cursor.moveToFirst();
        Alarm alarm = convertToAlarm(cursor);
        return alarm.getrH();
    }
    public int getRemainMinute(int id){
        Cursor cursor;
        String sql = "SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_ID +" = "+id;
        cursor= database.rawQuery(sql,null);
        cursor.moveToFirst();
        Alarm alarm = convertToAlarm(cursor);
        return alarm.getrM();
    }

    /*


    ***************************************************************


     */
    static class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "
                    + TABLE_NAME + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY,"
                    + COLUMN_NAME + " TEXT, "
                    + COLUMN_H + " INTEGER NOT NULL,"
                    + COLUMN_M + " INTEGER NOT NULL,"
                    + COLUMN_SOUND + " INTEGER NOT NULL,"
                    + COLUMN_DAY + " TEXT NOT NULL,"
                    + COLUMN_REPEAT + " INTEGER NOT NULL,"
                    + COLUMN_STATUS + " INTEGER NOT NULL,"
            +COLUMN_SKIP + " INTEGER NOT NULL,"
            +COLUMN_REMAIN+" INTEGER NOT NULL,"
            +COLUMN_REMAIN_HOUR+" INTEGER NOT NULL,"
            +COLUMN_REMAIN_MINUTE+" INTEGER NOT NULL);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}