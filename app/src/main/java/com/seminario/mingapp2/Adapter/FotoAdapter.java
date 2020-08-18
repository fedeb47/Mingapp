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
import com.seminario.mingapp2.R;

import java.util.List;

public class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.ViewHolder>
                        implements View.OnClickListener{

    private Context context;
    private List<String> post;
    private View.OnClickListener listener;

    public FotoAdapter(Context context, List<String> post) {
        this.context = context;
        this.post = post;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fotos_item, parent, false);
        view.setOnClickListener(this);   //escucha para el click
        return new FotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("FOTOADEPTER--", "entra");
        String publi = post.get(position);
        Log.d("FOTOADEPTER--", String.valueOf(position));
        Log.d("FOTOADEPTER--", publi);
        String[] s = publi.split(" ");
        Glide.with(context).load(s[0]).into(holder.post_image);
        holder.publiID.setText(s[1]);
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
        public TextView publiID;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.post_image);
            publiID = itemView.findViewById(R.id.publiID);

        }
    }
}
