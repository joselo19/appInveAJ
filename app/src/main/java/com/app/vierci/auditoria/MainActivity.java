package com.app.vierci.auditoria;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;

public class MainActivity extends AppCompatActivity implements View.OnKeyListener {

    Button siguiente;

    private TextView mBd_State;
    private EditText mCi;
    private boolean estado_bd = false;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();


        mCi = (EditText) findViewById(R.id.t_ci);

        mCi.setOnKeyListener(new View.OnKeyListener()
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
        });

        siguiente = (Button) findViewById(R.id.b_siguiente);
        siguiente.setOnClickListener(new View.OnClickListener() {
              @Override
                 public void onClick(View view) {
                  ingresar();


                    }
            }
        );
        mCi.setText("");
    }

    private void ingresar() {

        MiBaseDatos conn = new MiBaseDatos(getApplicationContext());

        SQLiteDatabase db=conn.getReadableDatabase();
        mCi = (EditText) findViewById(R.id.t_ci);

        String[] parametros={mCi.getText().toString()};

        Cursor cursor= db.rawQuery(
                "SELECT COUNT (*) FROM auditor WHERE cedula =?",
                parametros
        );

        int count = 0;

        if(null != cursor)
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                count = cursor.getInt(0);

            }

        cursor.close();

        if (count > 0) {

            Intent intent = new Intent(this, Marbete.class);
            intent.putExtra("ci", mCi.getText().toString() );
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "¡Usuario no registrado!", Toast.LENGTH_SHORT).show();
        }

    }





    @Override
    protected void  onStart() {

        super.onStart();

        MiBaseDatos db = new MiBaseDatos(getApplicationContext());
        this.estado_bd =  db.checkDataBase() ;
        mBd_State = (TextView) findViewById(R.id.bd_state);

        if (this.estado_bd == false){
            mBd_State.setText("Error en Base de Datos - Establecer Permiso de Almacenamiento a la Aplicación");
        }else
        {
            mBd_State.setText("Base de Datos Ok");
        };

        mCi = (EditText) findViewById(R.id.t_ci);
        mCi.setText("");
    }


//SOLO DE EJEMPLO
    private void consultar() {

        MiBaseDatos conn = new MiBaseDatos(getApplicationContext());

        SQLiteDatabase db=conn.getReadableDatabase();

        String[] parametros={"2"};
        String[] campos={"cod_sucursal","desc_sucursal","cod_empresa"};

        try {
            Cursor cursor =db.query("sucursal",campos,"cod_sucursal=?",parametros,null,null,null);
            cursor.moveToFirst();
            siguiente.setText(cursor.getString(0)); //el campo oluo
            siguiente.setText(cursor.getString(1));
            siguiente.setText(cursor.getString(2));
            cursor.close();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"El documento no existe",Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return false;
    }
}
