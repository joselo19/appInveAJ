package com.app.vierci.auditoria;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class ListViewAdapter extends ArrayAdapter<Conteo_articulos> {
    // Declare Variables
    Context context;
    LayoutInflater inflater;
    List<Conteo_articulos> articuloslist;
    private SparseBooleanArray mSelectedItemsIds;

    public ListViewAdapter(Context context, int resourceId,
                           List<Conteo_articulos> articuloslist) {
        super(context,  android.R.layout.simple_list_item_multiple_choice, resourceId, articuloslist);
        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
        this.articuloslist = articuloslist;
        inflater = LayoutInflater.from(context);
    }

    private class ViewHolder {
        TextView txtIdList;
        TextView txtCodigoList;
        TextView txtProductoList;
        TextView txttotalConteo;
        TextView txthoraConteo;


    }

    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.activity_conteo_agrupado, null);
            // Locate the TextViews in listview_item.xml
            holder.txtIdList = (TextView) view.findViewById(R.id.txtIdList);
            holder.txtCodigoList = (TextView) view.findViewById(R.id.txtCodigoList);
            holder.txtProductoList = (TextView) view.findViewById(R.id.txtProductoList);
            holder.txttotalConteo = (TextView) view.findViewById(R.id.txttotalConteo);
            holder.txthoraConteo = (TextView) view.findViewById(R.id.txthoraConteo);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Capture position and set to the TextViews
        holder.txtIdList.setText(String.valueOf(articuloslist.get(position).getId()));
        holder.txtCodigoList.setText(articuloslist.get(position).getCodigo());
        holder.txtProductoList.setText(articuloslist.get(position).getArticulo());
        holder.txttotalConteo.setText(articuloslist.get(position).getConteo());
        holder.txthoraConteo.setText(articuloslist.get(position).getHora());
        return view;
    }

    @Override
    public void remove(Conteo_articulos object) {
        articuloslist.remove(object);
        notifyDataSetChanged();
    }

    public List<Conteo_articulos> getArticuloslist() {
        return articuloslist;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}