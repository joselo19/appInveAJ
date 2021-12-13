package com.app.vierci.auditoria;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class AdapterItem extends BaseAdapter {



    protected Activity activity;
    protected ArrayList<Conteo_articulos> items;

    public AdapterItem (Activity activity, ArrayList<Conteo_articulos> items) {
        this.activity = activity;
        this.items = items;

    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    public void addAll(ArrayList<Conteo_articulos> category) {
        for (int i = 0 ; i < category.size(); i++) {
            items.add(category.get(i));
        }
    }

    @Override
    public Object getItem(int arg0) {
        return items.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.activity_conteo_agrupado, null);
        }

        Conteo_articulos dir = items.get(position);

        TextView idl = (TextView) v.findViewById(R.id.txtIdList);
        String idLis = String.valueOf(dir.getId());
        idl.setText(idLis);

        TextView codigo = (TextView) v.findViewById(R.id.txtCodigoList);
        codigo.setText(dir.getCodigo());

        TextView description = (TextView) v.findViewById(R.id.txtProductoList);
        description.setText(dir.getArticulo());

        TextView totalConteo = (TextView) v.findViewById(R.id.txttotalConteo);
        totalConteo.setText(dir.getConteo());

        TextView horaConteo = (TextView) v.findViewById(R.id.txthoraConteo);
        horaConteo.setText(dir.getHora());
        return v;
    }



}


