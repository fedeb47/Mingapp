package com.seminario.mingapp2.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.seminario.mingapp2.Modelos.Publi;
import com.seminario.mingapp2.R;

import java.util.List;

public class FavoritosAdapter extends RecyclerView.Adapter<FavoritosAdapter.ViewHolder>
        implements View.OnClickListener {

    private Context context;
    private List<Publi> post;
    private View.OnClickListener listener;

    public FavoritosAdapter(Context context, List<Publi> post) {
        this.context = context;
        this.post = post;
    }

    @NonNull
    @Override
    public FavoritosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favoritos_item, parent, false);
        view.setOnClickListener(this);   //escucha para el click
        return new FavoritosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritosAdapter.ViewHolder holder, int position) {
        Log.d("FavoritosADEPTER--", "entra");
        Publi publi = post.get(position);
        Log.d("FavoritosADEPTER--", String.valueOf(position));
        Log.d("FavoritsADEPTER--", publi.toString());
        Glide.with(context).load(publi.getLinkFoto()).into(holder.post_image);
        holder.nombre.setText(publi.getNombre());
        holder.id.setText(publi.getID());
    }

    @Override
    public int getItemCount() {
        return post.size();
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

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView post_image;
        public TextView nombre;
        public TextView id;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.post_image);
            nombre = itemView.findViewById(R.id.tvNombre);
            id = itemView.findViewById(R.id.publiID);
        }
    }
}
