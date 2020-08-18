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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.seminario.mingapp2.Adapter.FavoritosAdapter;
import com.seminario.mingapp2.Modelos.Publi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Favoritos extends AppCompatActivity {
    private String userActivo;
    RecyclerView recyclerView;
    FavoritosAdapter favAdapter;
    List<String> lista;
    List<Publi> publicaciones;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);
        Log.d("ACTIVITY----", this.toString());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {        userActivo = user.getUid();        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(linearLayoutManager);
        lista = new ArrayList<>();
        publicaciones = new ArrayList<>();

        myRef.child("usuarios").child(user.getUid()).child("Likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    lista.add(snapshot.getValue(String.class));
                }
                Log.d("AAAAAAAAAAA", dataSnapshot.getChildren().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("publicaciones--", "cancelado");
            }
        });


        favAdapter = new FavoritosAdapter(this, publicaciones);
        favAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Publi publi = publicaciones.get(recyclerView.getChildAdapterPosition(v));
                Intent intent = new Intent(Favoritos.this, Publicacion.class);
                intent.putExtra("publiID", publi.getID());                                     //ID de la publicacion
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(favAdapter);

        Log.d("1111111111-", lista.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("22222222-", lista.toString());
        publis(lista);
        Log.d("3333333-", lista.toString());
    }

    //entro a los likes del usuarios activo y guardo en la lista "lista" todos los ID de las publicaciones likeadas


    //recorro la lista "lista" para entrar a cada publicacion likeada y crear objetos Publi para pasarle al adapter por medio de la lista "publicaciones"
    public void publis(List<String> lista){
        Log.d("FUNCION PUBLICACIONES", lista.toString());
        for (int i = 0; i < lista.size(); i++){
            Log.d("BUCLE FOR", lista.get(i));
            myRef.child("Publicaciones").child(lista.get(i)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String link = dataSnapshot.child("LinkFoto").getValue(String.class);
                    String nombre = dataSnapshot.child("Nombre").getValue(String.class);
                    String id = dataSnapshot.getKey();
                    Publi p = new Publi("",nombre,link,"","");
                    p.setID(id);
                    publicaciones.add(p);
                    Log.d("ntrara aca????", p.getID());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        Collections.reverse(publicaciones);
        favAdapter.notifyDataSetChanged();
    }

    //barra de navegacion
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
                    Log.d("BARRA NAVEGADORA", "entraaa  " + userActivo);
                    Intent intent;
                    switch (menuItem.getItemId()){
                        case R.id.nav_add:
                            intent = new Intent(Favoritos.this, SubirPublicacion.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_perfil:
                            intent = new Intent(Favoritos.this, Perfil.class);
                            intent.putExtra("userID", userActivo);
                            startActivity(intent);
                            break;
                        case R.id.nav_favs:
                            intent = new Intent(Favoritos.this, Favoritos.class);
                            //intent.putExtra("userID", userID);
                            startActivity(intent);
                            break;
                        case R.id.nav_search:
                            intent = new Intent(Favoritos.this, MainActivity.class);
                            startActivity(intent);
                            break;
                    }
                    return true;
                }
            };
}
