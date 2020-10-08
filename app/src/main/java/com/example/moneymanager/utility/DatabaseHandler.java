package com.example.moneymanager.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    //Table 1: name|id|lastmodified
    private static final String DATABASE_NAME = "paymentManager";
    private static final String TABLE_PAYMENTS = "payments";
    private static final String KEY_ID = "id";
    private static final String COST = "cost";
    private static final String DESCRIPTION = "description";
    private static final String UNIXTIME = "unixtime";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PAYMENTS_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_PAYMENTS + "(" +
                        KEY_ID + " INTEGER PRIMARY KEY," +
                        COST + " DOUBLE," +
                        DESCRIPTION + " TEXT, " +
                        UNIXTIME + " INT" +
                        ")";
        db.execSQL(CREATE_PAYMENTS_TABLE);
    }

    public void reset() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENTS);
        onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        // Drop older table if existed
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
//
//        // Create tables again
//        onCreate(db);
    }
    public void addPayment(Payment payment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COST, payment.get_cost());
        values.put(DESCRIPTION, payment.get_description());
        values.put(UNIXTIME, payment.get_unixtime());
        db.insert(TABLE_PAYMENTS, null, values);
        db.close();
    }

    public List<Payment> getAllPayments() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Payment> payments = new ArrayList<Payment>();
        String selectQuery = "SELECT  * FROM " + TABLE_PAYMENTS + " ORDER BY "+ UNIXTIME +" DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do {
                Payment payment = new Payment();
                payment.set_id(Integer.parseInt(cursor.getString(0)));
                payment.set_cost(Double.parseDouble(cursor.getString(1)));
                payment.set_description(cursor.getString(2));
                payment.set_unixtime(Integer.parseInt(cursor.getString(3)));
                payments.add(payment);
            } while (cursor.moveToNext());
        }
        return payments;
    }

    public void deletePayment(Payment payment) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PAYMENTS, KEY_ID + " = ?", new String[] {String.valueOf(payment.get_id())});
        db.close();
    }
}
