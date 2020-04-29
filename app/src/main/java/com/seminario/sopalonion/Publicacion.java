package com.seminario.sopalonion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Publicacion extends AppCompatActivity {
    private ImageView fotoSubida;
    private TextView tvNombre;
    private TextView tvDescripcion;
    private TextView tvPrecio;
    private ProgressBar progressBar;
    private TextView tvUser;


    private String id;
    private String Nombre;
    private Uri linkFoto;
    private String userID;
    //private String descripcion;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    DatabaseReference publiRef = myRef.child("Publicaciones");

    public Publicacion(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicacion);

        fotoSubida = (ImageView) findViewById(R.id.ivFotoPublicacion);
        tvNombre = (TextView) findViewById(R.id.tvNombre);
        tvDescripcion = (TextView) findViewById(R.id.tvDescripcion);
        tvPrecio = (TextView) findViewById(R.id.tvPrecio);
        progressBar = (ProgressBar) findViewById(R.id.progressBar3);
        tvUser = (TextView) findViewById(R.id.tvUser1);

        Bundle datos = this.getIntent().getExtras();
        id = datos.getString("id");
        userID = datos.getString("user");
        Log.d("ID", id);
    }

    protected void onStart() {
        super.onStart();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.VISIBLE);
                String nombre = dataSnapshot.child("Publicaciones").child(id).child("Nombre").getValue(String.class);
                String link = dataSnapshot.child("Publicaciones").child(id).child("LinkFoto").getValue(String.class);
                String descripcion = dataSnapshot.child("Publicaciones").child(id).child("Descripcion").getValue(String.class);
                String precio = dataSnapshot.child("Publicaciones").child(id).child("Precio").getValue(String.class);
                String user = dataSnapshot.child("usuarios").child(userID).child("nombre").getValue(String.class);

                //descripcion = "<b>" + user + ": </b>" + descripcion;
                //precio = "$ " + precio;
                tvNombre.setText(nombre);
                tvUser.setText(user);
                //tvUser.setTypeface(null, Typeface.BOLD);
                tvDescripcion.setText(descripcion);
                tvPrecio.setText("$ " + precio);

                Glide.with(Publicacion.this)
                        .load(link)
                        .fitCenter()
                        .centerCrop()
                        //.placeholder(R.id.ProgressBar3)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(fotoSubida);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
