package com.app.vierci.auditoria;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import android.os.Environment;
import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MiBaseDatos extends SQLiteOpenHelper {

    private static MiBaseDatos mInstance = null;

    private static String DB_PATH = "/BD/";
    private static String DB_NAME = "dbInveAjv.sqlite";
    private SQLiteDatabase myDataBase;
    private final Context myContext;


    public static MiBaseDatos getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new MiBaseDatos(ctx.getApplicationContext());
        }
        return mInstance;
    }



    public MiBaseDatos(Context context) {
        super(context, Environment.getExternalStorageDirectory().getPath() + DB_PATH + DB_NAME, null, 1);
        this.myContext = context;
    }


    public boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {

            /// se debe agregar permisos de storage en el manifest.
            String myPath = Environment.getExternalStorageDirectory().getPath() +DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {

            //la base de datos no existe .

        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }



}
