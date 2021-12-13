package com.app.vierci.auditoria;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class Marbete extends AppCompatActivity {

    private TextView m_ci, m_nombre, m_auditoria, txt_ubicacion; //cabecera
    private EditText m_marbete;
    private RadioButton rb_CodInterno, rb_scanning, rb_codLargo;

    Spinner cantidadDigitos;

    Button colectar, b_csv;

    File fileLog = new File(Environment.getExternalStorageDirectory().getPath() + "/BD/", "log.txt");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marbete);

        m_marbete = (EditText) findViewById(R.id.t_marbete); //campo de texto

        //Radio Buttons
        rb_scanning = (RadioButton)findViewById(R.id.rb_scanning);
        rb_codLargo = (RadioButton)findViewById(R.id.rb_codLargo);

        cantidadDigitos = (Spinner) findViewById(R.id.cantidadDigitos);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cantidadDigitos, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        cantidadDigitos.setAdapter(adapter);

        //Botones
        colectar = (Button) findViewById(R.id.b_colectar);
        b_csv = (Button) findViewById(R.id.b_csv);

        /*m_marbete.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:

                            ingresar();

                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });*/

        colectar.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             ingresar();
                                         }
                                     }
        );

        b_csv.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            try {
                                                exportar_csv();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
        );

        //establece el ci introducido en el login
        String valor = getIntent().getExtras().getString("ci");
        m_ci = (TextView) findViewById(R.id.t_ci);
        m_ci.setText(valor);

        MiBaseDatos conn = new MiBaseDatos(getApplicationContext());
        SQLiteDatabase db=conn.getReadableDatabase();

        String[] parametros={m_ci.getText().toString()};
        String[] campos={"descripcion"};

        m_nombre = (TextView) findViewById(R.id.t_nombre);

        try {
            Cursor cursor =db.query("auditor",campos,"cedula=?",parametros,null,null,null);
            cursor.moveToFirst();
            m_nombre.setText(cursor.getString(0));
            cursor.close();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Sin Nombre Auditor",Toast.LENGTH_LONG).show();
        }


        String[] campos2={"desc_auditoria"};

        m_auditoria = (TextView) findViewById(R.id.t_auditoria);
        txt_ubicacion = (TextView) findViewById(R.id.txt_ubicacion);

        try {
            Cursor cursor =db.query("auditoria",campos2,"1=1",null,null,null,null);
            cursor.moveToFirst();
            m_auditoria.setText(cursor.getString(0));
            cursor.close();

            String[] campoUbicacion={"desc_ubicacion"};
            cursor =db.query("ubicacion",campoUbicacion,"1=1",null,null,null,null);
            cursor.moveToFirst();
            txt_ubicacion.setText(cursor.getString(0));
            cursor.close();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Sin Nombre Auditoria",Toast.LENGTH_LONG).show();
        }

        // play(getWindow().getDecorView().getRootView(), "OK");

        try
        {

            if (!fileLog.exists()) {
                fileLog.createNewFile();
            }

            FileWriter outputStream = new FileWriter (fileLog, true);
            Calendar cal = new GregorianCalendar();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String date = df.format(cal.getTime());

            outputStream.write(("***********************MARBETE***********************"));
            outputStream.append("\r\n");
            outputStream.write(("Fecha : " + date));
            outputStream.append("\r\n");
            outputStream.close();//FIN LOG
        }
        catch (Exception ex)
        {
        }

    }

    private void ingresar() {
        m_ci = (TextView) findViewById(R.id.t_ci);
        m_marbete = (EditText) findViewById(R.id.t_marbete);

        String marbete = m_marbete.getText().toString();
        String opConteo = "";
        int sw = 0;

        //if (marbete.length() == 0) { marbete = "0"; };
        //int marbete_int = Integer.parseInt(marbete);

        //if (marbete.length() == 4)
        if (marbete.length() > 0)
        {
            //if (marbete_int < 4000){ //valor de marbete debe ser menor a 4000

                if(rb_scanning.isChecked()){
                    opConteo = "1";
                }
                else if(rb_codLargo.isChecked()){
                    opConteo = "2";
                }
                else{
                    sw = 1;
                    Toast.makeText(this, "Elija una Opcion de Conteo ", Toast.LENGTH_SHORT).show();
                    play(getWindow().getDecorView().getRootView(), "NOTI");
                }


                if (sw == 0){
                    //play(getWindow().getDecorView().getRootView(), "OK");

                    Intent intent = new Intent(this, Colector.class);
                    intent.putExtra("marbete", marbete.toString() );
                    intent.putExtra("ci", m_ci.getText().toString());
                    intent.putExtra("conteo", opConteo.toString());
                    intent.putExtra("numero", cantidadDigitos.getSelectedItem().toString()); //Captura el valor de la Cantidad de Digitos
                    startActivity(intent);
                }
            /*}
            else {
                Toast.makeText(getApplicationContext(),"Marbete no Válido",Toast.LENGTH_LONG).show();
            }*/
        }
        else {
            Toast.makeText(getApplicationContext(),"Ingrese Marbete", Toast.LENGTH_LONG).show();
            play(getWindow().getDecorView().getRootView(), "NOTI");
        }
    }

    private void exportar_csv() throws IOException
    {
        File myFile;

        MiBaseDatos conn = new MiBaseDatos(getApplicationContext());
        SQLiteDatabase db=conn.getReadableDatabase();


        try {

            String date = new SimpleDateFormat("yyyy-MM-dd_HH_mm", Locale.getDefault()).format(new Date());

            myFile = new File(Environment.getExternalStorageDirectory().getPath()+"/CSV/"+date+".csv");

            myFile.createNewFile();



            FileOutputStream fOut = new FileOutputStream(myFile);

            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

            myOutWriter.append("AUDITORIA;SUCURSAL;UBICACION;MARBETE;CODLARGO;SCANNING;DESCRIPCION;CONTEO;FECHA;HORA;CEDULA_AUDITOR;NOMBRE_AUDITOR;DESCARGADO;COLECTOR");
            myOutWriter.append("\r\n");


            Cursor c = db.rawQuery("SELECT au.desc_auditoria AS auditoria, " +
                    "  dc.sucursal AS sucursal, " +
                    "  dc.ubicacion AS ubicacion, " +
                    "  dc.marbete AS marbete, " +
                    "  dc.codigo_interno AS codigo_interno, " +
                    "  dc.scanning AS scanning, " +
                    "  dc.descripcion AS descripcion, " +
                    "  dc.conteo AS conteo, " +
                    "  dc.fecha AS fecha, " +
                    "  dc.hora AS hora, " +
                    "  dc.cedula_auditor AS cedula_auditor, " +
                    "  dc.nombre_auditor AS nombre_auditor, " +
                    "  dc.descargado AS descargado, " +
                    "  dc.colector AS colector" +
                    " from  conteo_articulo dc " +
                    " JOIN auditoria au on au.cod_auditoria=dc.auditoria " +
                    " where au.cod_auditoria = dc.auditoria ", null);

            if (c != null) {
                if (c.moveToFirst()) {
                    do {

                        String v_auditoria          = c.getString(c.getColumnIndex("auditoria"));
                        String v_sucursal           = c.getString(c.getColumnIndex("sucursal"));
                        String v_ubicacion          = c.getString(c.getColumnIndex("ubicacion"));
                        String v_marbete            = c.getString(c.getColumnIndex("marbete"));

                        String v_codigo_interno     = c.getString(c.getColumnIndex("codigo_interno"));
                        String v_scanning           = c.getString(c.getColumnIndex("scanning"));

                        String v_descripcion        = c.getString(c.getColumnIndex("descripcion"));
                        String v_conteo             = c.getString(c.getColumnIndex("conteo"));
                        String v_fecha              = c.getString(c.getColumnIndex("fecha"));
                        String v_hora               = c.getString(c.getColumnIndex("hora"));

                        String v_cedula_auditor     = c.getString(c.getColumnIndex("cedula_auditor"));
                        String v_nombre_auditor     = c.getString(c.getColumnIndex("nombre_auditor"));
                        String v_descargado         = c.getString(c.getColumnIndex("descargado"));
                        String v_colector           = c.getString(c.getColumnIndex("colector"));

                        /*String v_unidad_de_medida   = c.getString(c.getColumnIndex("unidad_de_medida"));
                        String v_reconteo           = c.getString(c.getColumnIndex("reconteo"));
                        String v_reconteosn         = c.getString(c.getColumnIndex("reconteoSN"));*/


                        myOutWriter.append( "'" + v_auditoria       +"';" +
                                            "'" + v_sucursal        +"';" +
                                            "'" + v_ubicacion       +"';" +
                                            "'" + v_marbete         +"';" +
                                            "'" + v_scanning        +"';" +
                                                  v_codigo_interno  +";" +
                                            "'" + v_descripcion     +"';" +
                                            "'" + v_conteo          +"';" +
                                            "'" + v_fecha           +"';" +
                                            "'" + v_hora            +"';" +
                                            "'" + v_cedula_auditor  +"';" +
                                            "'" + v_nombre_auditor  +"';" +
                                            "'" + v_descargado      +"';" +
                                            "'" + v_colector      +"'"
                                           );
                        myOutWriter.append("\r\n");
                    }

                    while (c.moveToNext());
                }

                c.close();
                myOutWriter.close();
                fOut.close();
                Toast.makeText(getApplicationContext(),"¡CSV GENERADO!",Toast.LENGTH_LONG).show();
            }
        }
        catch (SQLiteException se)
        {
            Toast.makeText(getApplicationContext(),"ERROR GENERANDO CSV",Toast.LENGTH_LONG).show();
        }

        finally
        {
            db.close();
        }

    }


    public void play(View v, String tipo) {
        if(tipo.equals("OK")) {
            MediaPlayer mp = MediaPlayer.create(this, R.raw.ok);
            mp.start();
        }

        if(tipo.equals("NOTI")){
            MediaPlayer mp = MediaPlayer.create(this,R.raw.noti);
            mp.start();
        }
    }
}
