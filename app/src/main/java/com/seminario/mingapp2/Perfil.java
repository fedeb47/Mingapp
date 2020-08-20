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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Perfil extends AppCompatActivity {
    private String userID;
    private String userActivo;
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

        ivFotoPerfil = (ImageView) findViewById(R.id.ivFotoPerfil);
        tvNombre = (TextView) findViewById(R.id.tvNombre);
        tvSeguidores = (TextView) findViewById(R.id.tvNseguidores);
        tvSeguidos = (TextView) findViewById(R.id.tvNseguidos);
        tvDescripcion = (TextView) findViewById(R.id.tvDescripcion);
        btnSeguir = (Button) findViewById(R.id.btnSeguir);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnEditar = (Button) findViewById(R.id.btnEditar);

        //BARRA DE NAVEGACION INICIALIZO Y PONGO LISTENER
        barra = findViewById(R.id.bottom_navigation);
        barra.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        //SECCION ADAPTER
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(linearLayoutManager);
        lista = new ArrayList<>();
        fotoAdapter = new FotoAdapter(this, lista);
        fotoAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                               //LISTENER PARA QUE AL CLICKEAR VAYA A LA PUBLICACION
                String[] split = lista.get(recyclerView.getChildAdapterPosition(v)). split(" ");  //hago esto porque tengo los valores guardados en un string
                Intent intent = new Intent(Perfil.this, Publicacion.class);
                intent.putExtra("publiID", split[1]);                                     //ID de la publicacion seleccionada
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(fotoAdapter);

        //pedimos el id de usuario activo
        userActivo = user.getUid();

        //Si recibimos un userID significa que es el perfil de otra persona, si el Bundle esta vacio le ponemos a userID nuestro ID
        Bundle datos = this.getIntent().getExtras();
        if(datos != null){
            userID = datos.getString("userID");
        } else userID = userActivo;
        Log.d("USERID", userID);

        misFotos();
        seguidores();
    }

    protected void onStart() {
        super.onStart();
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nombre = dataSnapshot.child(userID).child("Nombre").getValue(String.class);
                String foto = dataSnapshot.child(userID).child("Foto").getValue(String.class);
                String descripcion = dataSnapshot.child(userID).child("Descripcion").getValue(String.class);
                Boolean seguido = dataSnapshot.child(userActivo).child("seguidos").child(userID).getValue(Boolean.class);
                if(userActivo.equals(userID)){
                    btnSeguir.setVisibility(View.GONE);
                    btnLogout.setVisibility(View.VISIBLE);
                    btnEditar.setVisibility(View.VISIBLE);
                }else {
                    if (seguido != null) {
                        if (seguido == true) btnSeguir.setText("Dejar de seguir");
                    } else btnSeguir.setText("Seguir");
                }

                tvNombre.setText(nombre);
                tvDescripcion.setText(descripcion);

                Glide.with(Perfil.this)
                        .load(foto)
                        .fitCenter()
                        .centerCrop()
                        .into(ivFotoPerfil);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void editarDescripcion(View view) {
        Intent intent = new Intent(Perfil.this, Descripcion.class);
        intent.putExtra("back", tvDescripcion.getText());
        startActivity(intent);
    }

    //Al presionar el boton de seguir usuario
    public void seguir(View view) {
        if(btnSeguir.getText() == "Seguir"){
            usuarioRef.child(user.getUid()).child("seguidos").child(userID).setValue(true);
            usuarioRef.child(userID).child("seguidores").child(user.getUid()).setValue(true);
            btnSeguir.setText("Dejar de seguir");
        } else{
            usuarioRef.child(user.getUid()).child("seguidos").child(userID).setValue(false);
            usuarioRef.child(userID).child("seguidores").child(user.getUid()).setValue(false);
            btnSeguir.setText("Seguir");
        }
    }

    //IMAGENES DE LAS PUBLICACIONES
    public void misFotos(){
        Log.d("USERID mis fotos", userID);
        Log.d("USERACTIVO fotos", userActivo);
        myRef.child("Publicaciones").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lista.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.d("SNAPSHOT USER", userID);
                    Log.d("SNAPSHOT", snapshot.child("UserID").getValue(String.class));
                    if(snapshot.child("UserID").getValue(String.class).equals(userID)){
                        Log.d("ETNTRA AL IF", snapshot.getKey());
                        String link = snapshot.child("LinkFoto").getValue(String.class);
                        String ID = snapshot.getKey();
                        lista.add(link + " " + ID);                  //guardo los strings en un solo string
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

    //calcular la cantidad de seguidores y seguidos
    public void seguidores() {
        usuarioRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("USERID seguidores", userID);
                Log.d("USERACTIVO seguidores", userActivo);
                Long seguidores = dataSnapshot.child("seguidores").getChildrenCount();
                Long seguidos = dataSnapshot.child("seguidos").getChildrenCount();
                Log.d("AAAAAAAAAAAAA", seguidores + " " + seguidos);
                tvSeguidores.setText("" + dataSnapshot.child("seguidores").getChildrenCount());
                if(seguidos != null){
                    tvSeguidos.setText("" + seguidos);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {     }
        });
    }

    //LOGOUT
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();            //Desconecto Firebase
        LoginManager.getInstance().logOut();             //Desconecto Facebook
        goLoginScreen();                                 //Vuelvo al Login
    }

    //VOY A PANTALLA DE LOGIN
    private void goLoginScreen() {
        Intent intent = new Intent(this, activity_login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
                            startActivity(intent);
                            break;
                        case R.id.nav_perfil:
                            intent = new Intent(Perfil.this, Perfil.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_mess:
                            intent = new Intent(Perfil.this, VentanadeChat.class);
                            startActivity(intent);
                            break;
                    }
                    return true;
                }
            };
}
