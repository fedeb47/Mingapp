package com.seminario.mingapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.seminario.mingapp2.Adapter.FotoAdapter;
import com.seminario.mingapp2.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Perfil extends AppCompatActivity {
    private String userID;
    private ImageView ivFotoPerfil;
    private TextView tvNombre;
    private TextView tvSeguidores;
    private TextView tvSeguidos;
    private TextView tvDescripcion;
    private Button btnSeguir;
    private Button btnLogout;
    private Button btnEditar;
    private BottomNavigationView barra;
    RecyclerView recyclerView;
    FotoAdapter fotoAdapter;
    List<String> lista;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    DatabaseReference usuarioRef = myRef.child("usuarios");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Log.d("ACTIVITY----", this.toString());
        Log.d("creando perfil!!!!", user.getUid());

        ivFotoPerfil = (ImageView) findViewById(R.id.ivFotoPerfil);
        tvNombre = (TextView) findViewById(R.id.tvNombre);
        tvSeguidores = (TextView) findViewById(R.id.tvNseguidores);
        tvDescripcion = (TextView) findViewById(R.id.tvDescripcion);
        btnSeguir = (Button) findViewById(R.id.btnSeguir);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnEditar = (Button) findViewById(R.id.btnEditar);
        barra = findViewById(R.id.bottom_navigation);
        barra.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(linearLayoutManager);
        lista = new ArrayList<>();
        fotoAdapter = new FotoAdapter(this, lista);
        fotoAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] split = lista.get(recyclerView.getChildAdapterPosition(v)). split(" ");
                Intent intent = new Intent(Perfil.this, Publicacion.class);
                intent.putExtra("publiID", split[1]);                                     //ID de la publicacion
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(fotoAdapter);


        //Recibimos el userID
        Bundle datos = this.getIntent().getExtras();
        if(datos != null){
            userID = datos.getString("userID");
        } else userID = user.getUid();


        misFotos();
        seguidores();

        if (user != null) {
            //displayProfileInfo(user);
        } else goLoginScreen();

    }

    //barra de navegacion
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
                    Intent intent;
                    switch (menuItem.getItemId()){
                        case R.id.nav_add:
                            intent = new Intent(Perfil.this, SubirPublicacion.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_search:
                            intent = new Intent(Perfil.this, MainActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_favs:
                            intent = new Intent(Perfil.this, Favoritos.class);
                            //intent.putExtra("userID", userID);
                            startActivity(intent);
                            break;
                        case R.id.nav_perfil:
                            intent = new Intent(Perfil.this, Perfil.class);
                            startActivity(intent);
                            break;
                    }

                    return true;
                }
            };

    protected void onStart() {
        super.onStart();
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //progressBar.setVisibility(View.VISIBLE);
                Log.d("POR BAJAR LOS DATOS", userID);
                String nombre = dataSnapshot.child(userID).child("Nombre").getValue(String.class);
                String foto = dataSnapshot.child(userID).child("Foto").getValue(String.class);
                String descripcion = dataSnapshot.child(userID).child("Descripcion").getValue(String.class);
                Boolean seguido = dataSnapshot.child(user.getUid()).child("seguidos").child(userID).getValue(Boolean.class);
                if(user.getUid().equals(userID)){
                    btnSeguir.setVisibility(View.GONE);
                    btnLogout.setVisibility(View.VISIBLE);
                    btnEditar.setVisibility(View.VISIBLE);
                }else {
                    if (seguido != null) {
                        if (seguido == true) btnSeguir.setText("Dejar de seguir");
                    } else btnSeguir.setText("Seguir");
                }

                //Log.d("NOMBRE-----------------", nombre);
                //Log.d("lo sigo?---------", seguido.toString());

                tvNombre.setText(nombre);
                tvDescripcion.setText(descripcion);

                Glide.with(Perfil.this)
                        .load(foto)
                        .fitCenter()
                        .centerCrop()
                        //.placeholder(R.id.ProgressBar3)
                        .into(ivFotoPerfil);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, activity_login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void editarDescripcion(View view) {
        Intent intent = new Intent(Perfil.this, Descripcion.class);
       // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("back", tvDescripcion.getText());
        startActivity(intent);
    }

    public void seguidores() {
        usuarioRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvSeguidores.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();            //Desconecto Firebase
        LoginManager.getInstance().logOut();             //Desconecto Facebook
        goLoginScreen();                                 //Vuelvo al Login
    }

    public void seguir(View view) {
        Log.d("BOTON--------", btnSeguir.getText().toString());

        if(btnSeguir.getText() == "Seguir"){
            Log.d("BOTON--------", "seguir");
            usuarioRef.child(user.getUid()).child("seguidos").child(userID).setValue(true);
            usuarioRef.child(userID).child("seguidores").child(user.getUid()).setValue(true);
            btnSeguir.setText("Dejar de seguir");
        } else{
            Log.d("BOTON--------", "dejar de seguir");
            usuarioRef.child(user.getUid()).child("seguidos").child(userID).setValue(false);
            usuarioRef.child(userID).child("seguidores").child(user.getUid()).setValue(false);
            btnSeguir.setText("Seguir");
        }

    }

    //IMAGENES DE LAS PUBLICACIONES
    public void misFotos(){
//        Log.d("publicaciones--", userID);
        myRef.child("Publicaciones").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lista.clear();
                Log.d("publicaciones--", "entras2");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.child("UserID").getValue(String.class).equals(userID)){
                        String link = snapshot.child("LinkFoto").getValue(String.class);
                        String ID = snapshot.getKey();
                        lista.add(link + " " + ID);
                        Log.d("METE EN LISTA", link + " " + ID);
                    }
                }
                Collections.reverse(lista);
                fotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("publicaciones--", "cancelado");
            }
        });
    }
}
