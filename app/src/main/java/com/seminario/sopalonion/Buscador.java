package com.seminario.sopalonion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Buscador extends AppCompatActivity {
    private String string;
    private TextView tvUser1;
    private TextView tvDescripcion1;
    private TextView tvNombre;
    private TextView tvBusqueda;
    private ImageView iv1;
    private ImageButton dos;
    private ProgressBar progressBar;
    private ImageButton btnBuscar;
    private EditText etBuscar;
    private String descripcion;
    private String userID;
    private String link;
    private String publiID;
    private BottomNavigationView barra;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscador);

        iv1 = (ImageView) findViewById(R.id.ivFoto1);
        tvUser1 = (TextView) findViewById(R.id.tvUser1);
        tvDescripcion1 = (TextView) findViewById(R.id.tvDescripcion1);
        tvNombre = (TextView) findViewById(R.id.tvNombre);
        tvBusqueda = (TextView) findViewById(R.id.tvBusqueda);
        btnBuscar = (ImageButton) findViewById(R.id.btnBuscar);
        etBuscar = (EditText) findViewById(R.id.etBuscar);
        barra = findViewById(R.id.bottom_navigation);

        barra.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        //Recibimos los datos enviados desde el main con el string a buscar denominado "string"
        Bundle datos = this.getIntent().getExtras();
        string = datos.getString("string");
        tvBusqueda.setText(string);
    }

    //barra de navegacion
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
                    Intent intent;
                    switch (menuItem.getItemId()){
                        case R.id.nav_add:
                            intent = new Intent(Buscador.this, SubirPublicacion.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_perfil:
                            intent = new Intent(Buscador.this, Perfil.class);
                            intent.putExtra("userID", userID);
                            startActivity(intent);
                        case R.id.nav_search:
                            intent = new Intent(Buscador.this, MainActivity.class);
                            startActivity(intent);
                    }

                    return true;
                }
            };

    @Override
    protected void onStart() {
        super.onStart();

        //Query q = myRef.child("Publicaciones").orderByChild("Lugar").equalTo(string);         //consulto en publicaciones un nombre igual al buscado
        Query q = myRef.child("Publicaciones").orderByChild("Nombre");                         //pido todas las publicaciones
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot imageSnapshot: dataSnapshot.getChildren()) {                 //recorro snapshot de todas las publicaciones recibidas
                    String nombre = imageSnapshot.child("Nombre").getValue(String.class);      //Nombre de la publicacion
                    Log.d("REVISANDO---:", nombre);
                    if(nombre != null && string != null){
                        if(nombre.contains(string) && !string.equals("")){     //si el titulo contiene la palabra buscada entro
                            Log.d("ENTRA A:----", nombre);
                            publiID = imageSnapshot.getKey();
                            Log.d("PUBLI ID:----", publiID);
                            descripcion = imageSnapshot.child("Descripcion").getValue(String.class);        //nombre de la publicacion
                            userID = imageSnapshot.child("UserID").getValue(String.class);                  //id del usuario que la publico
                            link = imageSnapshot.child("LinkFoto").getValue(String.class);                  //Foto de la publicacion
                            tvDescripcion1.setText(descripcion);
                            tvNombre.setText(nombre);



                            Glide.with(Buscador.this)
                                    .load(link)
                                    .fitCenter()
                                    //.centerCrop()
                                    .placeholder(R.drawable.loading)
                                    /*       .listener(new RequestListener<String, GlideDrawable>() {
                                               @Override
                                               public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                                   return false;
                                               }

                                               @Override
                                               public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                   //progressBar.setVisibility(View.GONE);
                                                   return false;
                                               }
                                           })*/
                                    .into(iv1);

                            Query q = myRef.child("usuarios").child(userID);               //mediante el userid sacamos los datos del usuario
                            q.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String nombre = dataSnapshot.child("nombre").getValue(String.class); //nombre del usuario
                                    tvUser1.setText(nombre);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {    }
                            });
                        }
                    }


                }
                if(tvNombre.getText().equals("")){
                    tvNombre.setText("No se encontraron resultados");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {        }

        });


    }

    public void buscar(View view){
        String string = etBuscar.getText().toString();
        Intent intent = new Intent(this, Buscador.class);
        intent.putExtra("string", string);
        startActivity(intent);
    }


    public void publicacion(View view){
        Intent intent = new Intent(this, Publicacion.class);
        intent.putExtra("userID", userID);                               //ID del usuario creador
        intent.putExtra("publiID", publiID);                                     //ID de la publicacion
        startActivity(intent);
    }
}
