package com.seminario.mingapp2.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.seminario.mingapp2.R;

import java.util.List;

public class BusquedaAdapter extends RecyclerView.Adapter<BusquedaAdapter.ViewHolder> implements View.OnClickListener {

    private Context context;
    List<List<String>> lista;
    private List<String> publi;
    private View.OnClickListener listener;

    public BusquedaAdapter(Context context, List<List<String>> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public BusquedaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.busqueda_item, parent, false);
        view.setOnClickListener(this);   //escucha para el click
        return new BusquedaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusquedaAdapter.ViewHolder holder, int position) {
        publi = lista.get(position);
        holder.id.setText(publi.get(0));
        holder.nombre.setText(publi.get(1));
        Glide.with(context).load(publi.get(2)).into(holder.imagen);
    }

    @Override
    public int getItemCount() {
            return lista.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if(listener!=null){
            listener.onClick(v);
        }
    }

    //inicializo los objetos del layout
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView id;
        public TextView nombre;
        public ImageView imagen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.publiID);
            nombre = itemView.findViewById(R.id.tvNombre);
            imagen = itemView.findViewById(R.id.ivFoto1);
        }
    }
}
