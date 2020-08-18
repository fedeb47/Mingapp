package com.seminario.mingapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.seminario.mingapp2.R;

public class Publicacion extends AppCompatActivity {
    private ImageView fotoSubida;
    private TextView tvNombre;
    private TextView tvDescripcion;
    private TextView tvPrecio;
    private ProgressBar progressBar;
    private TextView tvUser;
    private ImageView ivLike;
    private Button btnEditar;
    private Button btnEliminar;
    private BottomNavigationView barra;


    private String publiID;
    private String userID;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String userActivo = user.getUid();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    DatabaseReference publiRef = myRef.child("Publicaciones");
    DatabaseReference userRef = myRef.child("usuarios").child(userActivo);

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
        ivLike = (ImageView) findViewById(R.id.ivLike);
        btnEditar = (Button) findViewById(R.id.btnEditar);
        btnEliminar = (Button) findViewById(R.id.btnEliminar);
        barra = findViewById(R.id.bottom_navigation);

        barra.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        Bundle datos = this.getIntent().getExtras();
        publiID = datos.getString("publiID");                //publicacion ID
        //userID = datos.getString("userID");        //ID de dueño publicacion
        //Log.d("ID dueño", userID);
        Log.d("ID userOnline", userActivo );
    }

    protected void onStart() {
        super.onStart();

        //Descargamos la publicacion de la base de datos
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("MYREF", publiID);
                progressBar.setVisibility(View.VISIBLE);
                String nombre = dataSnapshot.child("Publicaciones").child(publiID).child("Nombre").getValue(String.class);
                String link = dataSnapshot.child("Publicaciones").child(publiID).child("LinkFoto").getValue(String.class);
                String descripcion = dataSnapshot.child("Publicaciones").child(publiID).child("Descripcion").getValue(String.class);
                String precio = dataSnapshot.child("Publicaciones").child(publiID).child("Precio").getValue(String.class);
                userID = dataSnapshot.child("Publicaciones").child(publiID).child("UserID").getValue(String.class);
                String user = dataSnapshot.child("usuarios").child(userID).child("Nombre").getValue(String.class);

                //Si soy el dueño de la publicacion se activan los botones de edicion
                if (userID.equals(userActivo)){
                    btnEditar.setVisibility(View.VISIBLE);
                    btnEliminar.setVisibility(View.VISIBLE);
                }

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
                        //.centerCrop()
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

        //vamos a ver si la publi esta likeada
        userRef.child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("USEREF", publiID);
                Boolean isLike = dataSnapshot.child(publiID).exists();
                if(isLike){
                    ivLike.setImageResource(R.drawable.ic_corazon);
                    ivLike.setTag("Like");
                };
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("CANCELADOOOOO", publiID);
            }
        });

    }

    //barra de navegacion
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
                    Intent intent;
                    switch (menuItem.getItemId()){
                        case R.id.nav_add:
                            intent = new Intent(Publicacion.this, SubirPublicacion.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_perfil:
                            intent = new Intent(Publicacion.this, Perfil.class);
                            intent.putExtra("userID", userActivo);
                            startActivity(intent);
                        case R.id.nav_search:
                            intent = new Intent(Publicacion.this, MainActivity.class);
                            startActivity(intent);
                    }

                    return true;
                }
            };

    public void like(View view){
        //si el corazon esta activado "like" y es clickeado
        if(ivLike.getTag().equals("Like")){
            //userRef.child("Likes").child(id).setValue(false);
            userRef.child("Likes").child(publiID).removeValue();      //entra a mi usuarios y saca el like a la publicacion
            publiRef.child(publiID).child("Likes").child(user.getUid()).removeValue();      //entra a la publicaciones y le saca el like de mi usuario
            ivLike.setImageResource(R.drawable.ic_nolike);    //paso el corazon lleno a corazon vacio
            ivLike.setTag("Unlike");   //le pongo el tag "unlike"
        }else{
            //si el corazon esta vacio "unlike"
            userRef.child("Likes").child(publiID).setValue(true);   //entra a los likes del usuario activo y pone me gusta en la publicacion
            publiRef.child(publiID).child("Likes").child(userID).setValue(true);    //entra a la publicacion y agrega el usuario activo
            ivLike.setImageResource(R.drawable.ic_corazon);   //cambiamos la imagen al corazon lleno
            ivLike.setTag("Like");   //le seteamos el tag like
        }
    }

    public void perfil(View view){
        Intent intent = new Intent(this, Perfil.class);
        intent.putExtra("userID", userID);
        startActivity(intent);
    }

    public void editar(View view){
        Intent intent = new Intent(this, SubirPublicacion.class);
        intent.putExtra("publiID", publiID);
        intent.putExtra("nombre", tvNombre.getText());
        intent.putExtra("descripcion", tvDescripcion.getText());
        intent.putExtra("precio", tvPrecio.getText());
        startActivity(intent);
    }

    public void eliminar(View view){
        Log.d("ELIMINAR", "ENTRA");
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Deseas eliminar la publicacion?");
        builder.setTitle("ELIMINAR PUBLICACION");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                publiRef.child(publiID).removeValue();
                userRef.child("Publicaciones").child(publiID).removeValue();
                perfil(btnEliminar);
            }
        });
        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
