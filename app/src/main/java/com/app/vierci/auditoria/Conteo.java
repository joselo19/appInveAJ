package com.app.vierci.auditoria;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.widget.Button;
import android.view.View.OnClickListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.widget.AbsListView.MultiChoiceModeListener;


public class Conteo extends AppCompatActivity {

    private TextView m_marbete;
    private ArrayList<Conteo_articulos> conteo = new ArrayList<>();
    private ArrayList<Conteo_articulos> conteoEliminar = new ArrayList<>();
    private ArrayList<Integer> conteEliminar = new ArrayList<>();
    private ListView lista;
    private int usuarioSelecionado = -1;
    private Object mActionMode;
    private Button mybuttonEliminar;
    private Context context = this;
    String ci;
    String codigo;
    TextView textViewSeleccionados;
    ListViewAdapter listviewadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conteo);
        String marbete = getIntent().getExtras().getString("marbete");
        String m_nombre = getIntent().getExtras().getString("m_nombre");

        codigo = getIntent().getExtras().getString("codigo");

        TextView nombreConteo = (TextView) findViewById(R.id.t_nombreConteo);
        nombreConteo.setText(m_nombre);

        ci = getIntent().getExtras().getString("ci");
        m_marbete = (TextView) findViewById(R.id.t_marbete);
        m_marbete.setText(marbete);

        textViewSeleccionados = (TextView) findViewById(R.id.textViewSeleccionados);

        lista = (ListView) findViewById(R.id.lv);
        llenarLista(marbete, ci);
        //onClick();




        mybuttonEliminar = (Button) findViewById(R.id.button_eliminarConteo);

        // add button listener
        mybuttonEliminar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set title
                alertDialogBuilder.setTitle("Vaciar Marbete");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Desea eliminar todo conteo de esta Marbete?")
                        .setCancelable(false)
                        .setPositiveButton("Si",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                eliminarMarbete();
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                Conteo_articulos usu = conteo.get(position);
                if(conteoEliminar.contains(usu)){
                    // lista.getChildAt(pos).setBackgroundColor(Color.TRANSPARENT); //No funciona bien
                    conteoEliminar.remove(usu);
                }else{
                    //   lista.getChildAt(pos).setBackgroundColor(Color.BLUE); //No funciona bien
                    conteoEliminar.add(usu);
                }
                String borrarLis = "";
                for(Conteo_articulos borrar : conteoEliminar){
                    borrarLis += borrar.id + ", ";
                }
                textViewSeleccionados.setText("IDs: " + borrarLis.replaceAll(", $", ""));
                Toast.makeText(Conteo.this, "IDs: " + borrarLis.replaceAll(", $", ""), Toast.LENGTH_LONG).show();
                mActionMode = Conteo.this.startActionMode(amc);
                view.setSelected(true);


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
            String query = "SELECT descripcion,scanning,conteo,hora FROM conteo_articulo WHERE marbete = '" + marbete + "' AND cedula_auditor = '" + ci + "'";
            if(!codigo.equals("")){
                query += "  AND scanning = '" + codigo + "'" ;
            }
            query += " ORDER BY scanning, hora " ;
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
/*
        listviewadapter = new ListViewAdapter(this, R.layout.activity_conteo_agrupado, conteo);
        lista.setAdapter(listviewadapter);
        lista.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        // Capture ListView item click
        lista.setMultiChoiceModeListener(new MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode,
                                                  int position, long id, boolean checked) {
                // Capture total checked items
                final int checkedCount = lista.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(checkedCount + " Selected");
                // Calls toggleSelection method from ListViewAdapter Class
                listviewadapter.toggleSelection(position);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.EliminarItem:
                        // Calls getSelectedIds method from ListViewAdapter Class
                        SparseBooleanArray selected = listviewadapter
                                .getSelectedIds();
                        // Captures all selected ids with a loop
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                Conteo_articulos selecteditem = listviewadapter
                                        .getItem(selected.keyAt(i));
                                // Remove selected items following the ids
                                listviewadapter.remove(selecteditem);
                            }
                        }
                        // Close CAB
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
               // mode.getMenuInflater().inflate(R.menu.activity_conteo, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // TODO Auto-generated method stub
                listviewadapter.removeSelection();
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub
                return false;
            }
        });
*/

    }

    public void eliminar( Conteo_articulos usu ) {

        MiBaseDatos conn = new MiBaseDatos(getApplicationContext());
        if (conn != null) {
            SQLiteDatabase db = conn.getWritableDatabase();
           // Conteo_articulos usu = conteo.get(usuarioSelecionado);
            long response = db.delete("conteo_articulo", " hora = '" + usu.getHora() + "' AND scanning = '" + usu.getCodigo() + "'", null);
            if (response > 0) {
                Toast.makeText(Conteo.this, "Eliminado con exito", Toast.LENGTH_LONG).show();
                conteo.removeAll(conteo);
                m_marbete = (TextView) findViewById(R.id.t_marbete);

                llenarLista(m_marbete.getText().toString(), ci);
            } else {
                Toast.makeText(Conteo.this, "Fallo", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void eliminarMarbete() {
        m_marbete = (TextView) findViewById(R.id.t_marbete);

        MiBaseDatos conn = new MiBaseDatos(getApplicationContext());
        if (conn != null) {
            SQLiteDatabase db = conn.getWritableDatabase();
           // Conteo_articulos usu = conteo.get(usuarioSelecionado);
            long response = db.delete("conteo_articulo", " marbete = '" + m_marbete.getText() + "' AND cedula_auditor = '" + ci + "'", null);
            if (response > 0) {
                Toast.makeText(Conteo.this, "Eliminado con exito", Toast.LENGTH_LONG).show();
                conteo.removeAll(conteo);
                m_marbete = (TextView) findViewById(R.id.t_marbete);

                llenarLista(m_marbete.getText().toString(),ci);
            } else {
                Toast.makeText(Conteo.this, "Fallo", Toast.LENGTH_LONG).show();
            }
        }
    }
/*
    private void onClick() {
        lista.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        try {
        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                usuarioSelecionado = position;
                Conteo_articulos usu = conteo.get(usuarioSelecionado);
                usu.setPosition(position);

                if(conteoEliminar.contains(usu)){

                    lista.getChildAt(usuarioSelecionado).setBackgroundColor(Color.TRANSPARENT);
                    conteoEliminar.remove(usu);

                }else{
                    lista.getChildAt(usuarioSelecionado).setBackgroundColor(Color.RED);
                    conteoEliminar.add(usu);
                }



                mActionMode = Conteo.this.startActionMode(amc);
                view.setSelected(true);
                return true;
            }
        });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
*/
    private ActionMode.Callback amc = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.opciones,menu);


            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item)
        {

            if(item.getItemId() == R.id.EliminarItem){

                for(Conteo_articulos usu : conteoEliminar){
                    eliminar(usu);

                }
                conteoEliminar.clear();
                textViewSeleccionados.setText( " Eliminados ...");
                mode.finish();
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode)
        {

        }
    };






}
