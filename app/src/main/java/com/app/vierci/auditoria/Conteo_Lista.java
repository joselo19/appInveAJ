package com.app.vierci.auditoria;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class Conteo_Lista extends AppCompatActivity {

    private TextView m_marbete;
    private ArrayList<Conteo_articulos> conteo = new ArrayList<>();
    private ListView lista;
    String ci,marbete;

    TextView t_nombre3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conteo_lista);
        marbete = getIntent().getExtras().getString("marbete");
        ci = getIntent().getExtras().getString("ci");
        String auditor = getIntent().getExtras().getString("auditor");

        t_nombre3 = (TextView) findViewById(R.id.t_nombre3);
        t_nombre3.setText(auditor);

        m_marbete = (TextView) findViewById(R.id.t_marbete);
        m_marbete.setText(marbete);

        lista = (ListView) findViewById(R.id.listaConteo);
        llenarLista(marbete, ci);

        Button conteo2 = (Button) findViewById(R.id.b_conteo2);

        conteo2.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          ir_conteo(marbete,ci, "", "");
                                      }
                                  }
        );


        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                //lista.getChildAt(pos).setBackgroundColor(Color.RED);
                Conteo_articulos selec = conteo.get(pos);
                ir_conteo(marbete,  ci, selec.getCodigo(), t_nombre3.getText().toString());
            }
        });


    }

    public void onResume() {
        super.onResume();
        m_marbete = (TextView) findViewById(R.id.t_marbete);
        conteo.removeAll(conteo);
        llenarLista(m_marbete.getText().toString(), ci);
    }

    public void llenarLista(String marbete, String ci) {

        MiBaseDatos conn = new MiBaseDatos(getApplicationContext());
        if (conn != null) {
            SQLiteDatabase db = conn.getReadableDatabase();
            String query = "SELECT descripcion,scanning,sum(conteo),max(hora) FROM conteo_articulo WHERE marbete = '" + marbete + "' AND cedula_auditor = '" + ci + "' group by descripcion,scanning";
            Cursor c = db.rawQuery(query, null);
            int id = 1;
            if (c.moveToFirst()) {
                do {
                    conteo.add((id-1), new Conteo_articulos(id++, c.getString(0), c.getString(1), c.getString(2), c.getString(3)));
                } while (c.moveToNext());
            }
        }


        AdapterItem adapter = new AdapterItem(this, conteo);
        lista.setAdapter(adapter);



    }



    private void ir_conteo(String marbete, String ci, String codigo, String m_nombre) {
        Intent intent = new Intent(this, Conteo.class);
        intent.putExtra("marbete", marbete );
        intent.putExtra("ci",ci);
        intent.putExtra("codigo",codigo);
        intent.putExtra("m_nombre",getIntent().getExtras().getString("auditor"));
        startActivity(intent);

    }


}
