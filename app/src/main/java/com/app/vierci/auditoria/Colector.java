package com.app.vierci.auditoria;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.text.SimpleDateFormat;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Date;
import java.util.Locale;

public class Colector extends AppCompatActivity {

    private TextView t_nombre;
    private TextView t_marbete;
    private TextView t_opConteo;
    private TextView t_numero;

    private CheckBox checkTeclado, checkHabilita;

    String  v_ci;
    String codConteo;
    String numero;
    String v_marbete;

    private EditText m_codigo, e_Cant;
    private TextView m_des_articulo,  m_total_marbete,  m_total_art;
    private Button atras, conteo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colector);

        //Cabecera
        t_nombre  = (TextView) findViewById(R.id.t_nombre);
        t_marbete = (TextView) findViewById(R.id.t_marbete);
        t_opConteo = (TextView) findViewById(R.id.t_opConteo);
        t_numero = (TextView) findViewById(R.id.t_numero);

        //Obteniendo valores del Activitie anterior yEstableciendo Valores de las Cabeceras
        v_marbete = getIntent().getExtras().getString("marbete");
        t_marbete.setText(v_marbete);

        codConteo = getIntent().getExtras().getString("conteo");
        numero = getIntent().getExtras().getString("numero");
        t_numero.setText(numero);

        m_codigo = (EditText) findViewById(R.id.t_codigo);
        e_Cant = (EditText) findViewById(R.id.e_Cant);
        e_Cant.setEnabled(false); //inhabilita el campo

        if (codConteo.equals("1")){
            t_opConteo.setText("Scanning");
            m_codigo.setHint("Scanning");
        }
        else if (codConteo.equals("2")){
            t_opConteo.setText("Código Largo");
            m_codigo.setHint("Código Largo");
        }

        v_ci = getIntent().getExtras().getString("ci");
        nombre_auditor(v_ci); //funcion

        m_des_articulo = (TextView) findViewById(R.id.t_articulo);
        total_marbete(t_marbete.getText().toString(), v_ci);

        //check de TECLADO
        checkTeclado = (CheckBox)findViewById(R.id.checkTeclado);

        checkTeclado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    m_codigo.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                else
                {
                    m_codigo.setInputType(InputType.TYPE_CLASS_NUMBER);
                }

            }
        });


        //check de HABILITA
        checkHabilita = (CheckBox)findViewById(R.id.checkHabilita);

        checkHabilita.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    e_Cant.setEnabled(true);
                }
                else
                {
                    e_Cant.setEnabled(false);
                }

            }
        });



        // Mantiene el foco el en input de codigo de barra
        m_codigo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER)
                        || keyCode == KeyEvent.KEYCODE_TAB) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //String numero = cantidadDigitos.getSelectedItem().toString();

                            /*if (numero.isEmpty()){
                                play(getWindow().getDecorView().getRootView());
                                Toast.makeText(getApplicationContext(), "Nº de Dígitos Requerido", Toast.LENGTH_SHORT).show();
                                m_codigo.setText("");//limpia código sino existe
                            }
                            else { aqui va el codigo de abajo}*/

                            int num = Integer.parseInt(numero);

                            if (m_codigo != null) {
                                String codigo = m_codigo.getText().toString();
                                String verifica = "N";
                                int sw = 0;
                                //verifica = existe(codigo);

                                if (codigo.length() > 0) {
                                    if (codigo.length() >= num){
                                        sw = 1;
                                        String lecturaNum  = codigo.substring(0,num);
                                        //Toast.makeText(getApplicationContext(), "Nº de Dígitos: " +  lecturaNum, Toast.LENGTH_SHORT).show();

                                        verifica = existe(lecturaNum);
                                        codigo = lecturaNum;
                                    }
                                    else{
                                        play(getWindow().getDecorView().getRootView(), "NOTI");
                                        Toast.makeText(getApplicationContext(), "Logitud de Código MAYOR al Código de Búsqueda!!!" , Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                    play(getWindow().getDecorView().getRootView(), "NOTI");
                                    Toast.makeText(getApplicationContext(), "Ingrese Código de Búsqueda!!!!" , Toast.LENGTH_SHORT).show();
                                }

                                if (sw == 1) {
                                    // Vamos si existe
                                    if (verifica.equals("S")) {
                                        String scanning, codigo_interno, auditoria, sucursal, ubicacion;

                                        auditoria = dev_auditoria();
                                        sucursal = dev_sucursal();
                                        ubicacion = dev_ubicacion();

                                        play(getWindow().getDecorView().getRootView(), "OK");

                                        if (codConteo.equals("1")) { //scanning
                                            descripcion_articulo(codigo);
                                            codigo_interno = getCodInterno(1, codigo); // pasa el scanning

                                            insertar(auditoria,
                                                    t_marbete.getText().toString(),
                                                    codigo, //seria el scanning
                                                    codigo_interno,
                                                    m_des_articulo.getText().toString(),
                                                    sucursal,
                                                    ubicacion,
                                                    v_ci,
                                                    t_nombre.getText().toString()
                                            );

                                            total_marbete(t_marbete.getText().toString(), v_ci);
                                            total_articulo(t_marbete.getText().toString(), v_ci, m_codigo.getText().toString());

                                        } else if (codConteo.equals("2")) {//COD LARGo
                                            codigo = m_codigo.getText().toString();
                                            scanning = getScanning(codigo); //pasa el cod Largo

                                            descripcion_articulo(scanning);
                                            codigo_interno = getCodInterno(2, codigo); //pasa el cod Largo

                                            insertar(auditoria,
                                                    t_marbete.getText().toString(),
                                                    scanning,
                                                    codigo_interno,
                                                    m_des_articulo.getText().toString(),
                                                    sucursal,
                                                    ubicacion,
                                                    v_ci,
                                                    t_nombre.getText().toString()
                                            );

                                            total_marbete(t_marbete.getText().toString(), v_ci);
                                            total_articulo(t_marbete.getText().toString(), v_ci, scanning);
                                        }

                                        m_codigo.setText("");
                                        m_codigo.requestFocus();
                                    } else {
                                        play(getWindow().getDecorView().getRootView(), "NOTI");

                                        Toast.makeText(getApplicationContext(), "No existe Articulo", Toast.LENGTH_LONG).show();
                                        m_des_articulo = (TextView) findViewById(R.id.t_articulo);
                                        m_des_articulo.setText("");

                                        m_total_art = (TextView) findViewById(R.id.t_cantidad_a);
                                        m_total_art.setText("");

                                        m_codigo.setText("");
                                        m_codigo.requestFocus();
                                    }
                                }
                            }
                        }
                    }, 10);
                    return true;
                }
                return false;
            }
        });


        // Boton atras
        atras = (Button) findViewById(R.id.b_atras);
        atras.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            verificaConteo();
                                        }
                                    }
        );

        conteo = (Button) findViewById(R.id.b_conteo);
        conteo.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {
                                         ir_conteo(t_marbete.getText().toString(), v_ci);
                                     }
                                 }
        );
    }

    public void verificaConteo() {
        MiBaseDatos conn = MiBaseDatos.getInstance(getApplicationContext());
        SQLiteDatabase db = conn.getReadableDatabase();

        String marbete =  t_marbete.getText().toString();
        marbete = marbete.trim();

        String[] campos={"nroArticulo", "teorico"};
        String[] parametro = { marbete };
        String articulo = "";
        int teorico = 0;
        String interno = "";
        int suma = 0;

        String artDiferencia = null;
        int teoricoDif = 0;
        int sumaDif = 0;
        int cont = 0;

        Cursor c = null;
        Cursor c2 = null;

        try {
            c = db.query("teoricos", campos, "trim(lote)=?", parametro,null,null,null,null);

            if (c.moveToFirst()) {
                do{
                    articulo= (c.getString(0));
                    teorico= (c.getInt(1));

                    c2= db.rawQuery("SELECT codigo_interno, SUM (conteo) FROM conteo_articulo " +
                            "WHERE trim(marbete)=? GROUP BY codigo_interno, marbete",
                            parametro
                    );

                    if (c2.moveToFirst()) {
                        do{
                            interno = c2.getString(0);
                            suma = c2.getInt(1);

                            if (suma != teorico) {
                                artDiferencia = articulo;
                                teoricoDif = teorico;
                                sumaDif = suma;
                                cont++;
                            }
                        }
                        while(c2.moveToNext());
                    }

                }
                while(c.moveToNext());
            }
        }
        catch (Exception e) {
            //teorico = e.getMessage();
        }
        finally{
            c.close();
            c2.close();
        }


        if(cont > 0){
            //msnDiferenicia(artDiferencia, teoricoDif, sumaDif);
            msnDiferenicia();
        }
        else{
            finish();
        }
    }


    //public void msnDiferenicia (String artDiferencia, int teoricoDif, int sumaDif){
    public void msnDiferenicia (){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
       // builder.setMessage("Existe diferencias = Art: "  + artDiferencia + ", Teorico: " + teoricoDif + ", Suma Conteo: "+ sumaDif);
        builder.setMessage("Marbete con Diferencias");

        builder.setTitle("Aviso");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


   /* public void play(View v) {
        MediaPlayer mp = MediaPlayer.create(this,R.raw.noti);
        mp.start();
    };*/


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


    @Override
    protected void  onStart() {
        super.onStart();

        t_marbete = (TextView) findViewById(R.id.t_marbete);

        total_marbete(t_marbete.getText().toString(), v_ci);

        m_des_articulo = (TextView) findViewById(R.id.t_articulo);
        m_des_articulo.setText("");

        m_total_art = (TextView) findViewById(R.id.t_cantidad_a);
        m_total_art.setText("");
    }

    private void nombre_auditor(String ci) {
        MiBaseDatos conn = MiBaseDatos.getInstance(getApplicationContext());
        SQLiteDatabase db=conn.getReadableDatabase();

        String[] parametros={ci};
        String[] campos={"descripcion"};

        t_nombre = (TextView) findViewById(R.id.t_nombre);

        try {
            Cursor cursor =db.query("auditor",campos,"cedula=?",parametros,null,null,null);
            cursor.moveToFirst();
            t_nombre.setText(cursor.getString(0));
            cursor.close();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Sin Nombre Auditor",Toast.LENGTH_LONG).show();
            t_nombre.setText("--");
        }
    }

    private void ir_conteo(String marbete, String ci) {
       // Intent intent = new Intent(this, Conteo.class);
        Intent intent = new Intent(this, Conteo_Lista.class);
        intent.putExtra("marbete", marbete );
        intent.putExtra("ci",ci);
        intent.putExtra("auditor", t_nombre.getText().toString());
        startActivity(intent);
    }

    private void descripcion_articulo( String codigo) {

        MiBaseDatos conn = MiBaseDatos.getInstance(getApplicationContext());
        SQLiteDatabase db=conn.getReadableDatabase();

            /*String seleccion = spinner.getSelectedItem().toString();

            if (seleccion.equals("8")) {
                if(codigo.length() > 8){
                    codigo = codigo.substring(0,num);
                };
            }*/

            //codigo = codigo.substring(0,num);

            String[] parametros={codigo};
            String[] campos={"descripcion"};

            m_des_articulo = (TextView) findViewById(R.id.t_articulo);

            try {
                Cursor cursor =db.query("maestro_articulos",campos,"scanning=?",parametros,null,null,null);
                cursor.moveToFirst();
                m_des_articulo.setText(cursor.getString(0));
                cursor.close();
            }catch (Exception e){
                play(getWindow().getDecorView().getRootView(), "NOTI");
                Toast.makeText(getApplicationContext(),"No existe Articulo - DESCRIPCION",Toast.LENGTH_LONG).show();
            }


    }

    private String getCodInterno(int num, String codigo){
        MiBaseDatos conn = MiBaseDatos.getInstance(getApplicationContext());
        SQLiteDatabase db=conn.getReadableDatabase();

        String codInterno = "";
        String[] campo ={"codigo_interno"};
        String[] parametro ={codigo};

        if (num == 1){ //scanning
           try {
                Cursor cursor = db.query("maestro_articulos",campo,"scanning=?", parametro, null,null,null);
                cursor.moveToFirst();
                codInterno = (cursor.getString(0));
                cursor.close();
                //return codInterno;
            }
            catch (Exception e)
            {
                //return codInterno;
            }
        }
        else if (num == 2){ //cod Largo
            try {
                Cursor cursor = db.query("maestro_articulos",campo,"codLargo=?", parametro, null,null,null);
                cursor.moveToFirst();
                codInterno=(cursor.getString(0));
                cursor.close();
                //return codInterno;
            }
            catch (Exception e)
            {
                //return codInterno;
            }
        }

        return codInterno;
    }

    private String getScanning(String codigo){
        MiBaseDatos conn = MiBaseDatos.getInstance(getApplicationContext());
        SQLiteDatabase db = conn.getReadableDatabase();

        String scanning = "";
        String[] campo ={"scanning"};
        String[] parametro ={codigo};

        try {
            Cursor cursor = db.query("maestro_articulos", campo,"codLargo=?", parametro, null,null,null);
            cursor.moveToFirst();
            scanning = (cursor.getString(0));
            cursor.close();
        }
        catch (Exception e)
        {
            //return scanning;
        }

        return scanning;
    }

    private void total_marbete( String marbete, String ci) {

        MiBaseDatos conn = MiBaseDatos.getInstance(getApplicationContext());
        SQLiteDatabase db=conn.getReadableDatabase();

        String[] parametros={marbete, ci};

        /*Cursor cursor= db.rawQuery(
                "SELECT COUNT (*) FROM conteo_articulo WHERE marbete=? AND cedula_auditor =?",
                parametros
        );*/

        Cursor cursor= db.rawQuery(
                "SELECT SUM (conteo) FROM conteo_articulo WHERE marbete=? AND cedula_auditor=?",
                parametros
        );

        int count = 0;

        m_total_marbete = (TextView) findViewById(R.id.t_total_m);

        if(null != cursor)
            if(cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                count = cursor.getInt(0);
            }

        cursor.close();

        if (count > 0)
        {
            m_total_marbete.setText(String.valueOf(count));
        }
        else
        {
            m_total_marbete.setText(String.valueOf(0));
        }

    }

    private void total_articulo( String marbete, String ci, String codigo) {
        MiBaseDatos conn = MiBaseDatos.getInstance(getApplicationContext());
        SQLiteDatabase db=conn.getReadableDatabase();

        String[] parametros={marbete,ci,codigo};

        /*Cursor cursor= db.rawQuery(
                "SELECT COUNT (*) FROM conteo_articulo WHERE marbete=? AND cedula_auditor=? AND scanning=?",
                parametros
        );*/

        Cursor cursor= db.rawQuery(
                "SELECT SUM (conteo) FROM conteo_articulo WHERE marbete=? AND cedula_auditor=? AND scanning=?",
                parametros
        );

        int count = 0;

        m_total_art = (TextView) findViewById(R.id.t_cantidad_a);

        if(null != cursor)
            if(cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                count = cursor.getInt(0);
            }

        cursor.close();

        if (count > 0)
        {
            m_total_art.setText(String.valueOf(count));
        }
        else
        {
            m_total_art.setText(String.valueOf(0));
        }

    }



   /////HACE EL INSERT LOCOOOOO
    private void insertar(  String auditoria,
                          String marbete,
                          String scanning,
                          String codigo_interno,
                          String descripcion,
                          String sucursal,
                          String ubicacion,
                          String ci,
                          String nombre
                           ) {


        String cant = e_Cant.getText().toString();
        int num = Integer.parseInt(cant);

        MiBaseDatos conn = MiBaseDatos.getInstance(getApplicationContext());
        SQLiteDatabase db=conn.getWritableDatabase();

        String fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        String hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        ContentValues values = new ContentValues();

        // Valores del colector
        values.put("auditoria", auditoria );
        values.put("marbete",marbete);
        values.put("codigo_interno","'" + codigo_interno + "'");
        values.put("scanning",scanning);
        values.put("descripcion",descripcion);
        values.put("sucursal",sucursal);
        values.put("ubicacion",ubicacion);
        values.put("cedula_auditor",ci);
        values.put("nombre_auditor",nombre);

        //valores del sistema

        values.put("fecha",fecha);
        values.put("hora",hora);
        values.put("colector","ANDROID");

        //VALORES FIJOS
        //values.put("conteo",1);
        values.put("conteo",num);
        values.put("descargado","N");
        values.put("unidad_de_medida","UN");
        values.put("reconteo","0");
        values.put("reconteoSN","N");

        long newRowId = db.insert("conteo_articulo", null, values);

    }


    private String existe(String codigo){

        MiBaseDatos conn = MiBaseDatos.getInstance(getApplicationContext());
        SQLiteDatabase db=conn.getReadableDatabase();

        String[] parametros={codigo};
        int count = 0;

        if (codConteo.equals("1")){
            //Toast.makeText(this, "Busca por Scanning", Toast.LENGTH_SHORT).show();
            Cursor cursor= db.rawQuery("SELECT COUNT (*) FROM maestro_articulos WHERE scanning=? ", parametros);

            if(null != cursor)
                if(cursor.getCount() > 0)
                {
                    cursor.moveToFirst();
                    count = cursor.getInt(0);
                }

            cursor.close();
        }

        else if (codConteo.equals("2")){
            //Toast.makeText(this, "Busca por Cod Largo", Toast.LENGTH_SHORT).show();
            Cursor cursor= db.rawQuery("SELECT COUNT (*) FROM maestro_articulos WHERE codLargo=? ", parametros);

            if(null != cursor)
                if(cursor.getCount() > 0)
                {
                    cursor.moveToFirst();
                    count = cursor.getInt(0);
                }

            cursor.close();
        }

        if (count > 0) { return "S"; } else { return "N"; }
    }

    private String dev_auditoria(){

        MiBaseDatos conn = MiBaseDatos.getInstance(getApplicationContext());
        SQLiteDatabase db=conn.getReadableDatabase();
        String[] campos={"cod_auditoria"};
        String auditoria="SIN DATOS";

        try {
            Cursor cursor =db.query("auditoria",campos,"1=1",null,null,null,null);
            cursor.moveToFirst();
            auditoria=(cursor.getString(0));
            cursor.close();
            return auditoria;
        }catch (Exception e){

            return auditoria;
        }
    }

    private String dev_sucursal(){

        MiBaseDatos conn = MiBaseDatos.getInstance(getApplicationContext());
        SQLiteDatabase db=conn.getReadableDatabase();


        String[] campos={"desc_sucursal"};

        String sucursal="SIN DATOS";

        try {

            Cursor cursor =db.query("sucursal",campos,"1=1",null,null,null,null);
            cursor.moveToFirst();
            sucursal=(cursor.getString(0));
            cursor.close();
            return sucursal;

        }catch (Exception e){

            return sucursal;
        }
    }

    private String dev_ubicacion(){

        MiBaseDatos conn = MiBaseDatos.getInstance(getApplicationContext());
        SQLiteDatabase db=conn.getReadableDatabase();
        String[] campos={"cod_ubicacion"};
        String ubicacion="SIN DATOS";

        try {
            Cursor cursor =db.query("ubicacion",campos,"1=1",null,null,null,null);
            cursor.moveToFirst();
            ubicacion=(cursor.getString(0));
            cursor.close();
            return ubicacion;
        }
        catch (Exception e)
        {
            return ubicacion;
        }
    }





//// BOTON ATRAS CHE RA'A
    @Override
    public void onBackPressed() {

    }
}
