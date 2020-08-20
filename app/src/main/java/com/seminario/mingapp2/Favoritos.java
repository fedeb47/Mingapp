package com.seminario.mingapp2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.seminario.mingapp2.Adapter.FavoritosAdapter;
import com.seminario.mingapp2.Modelos.Publi;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Favoritos extends AppCompatActivity {
    private String userActivo;
    RecyclerView recyclerView;
    FavoritosAdapter favAdapter;
    List<Publi> publicaciones;
    private String publiID;
    private BottomNavigationView barra;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);

        //FIREBASE OBETENGO INSTANCIA Y LUEGO PiDO USERID DEL USUARIO CONECTADO
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {        userActivo = user.getUid();        }

        //BARRA DE NAVEGACION INICIALIZO Y PONGO LISTENER
        barra = findViewById(R.id.bottom_navigation);
        barra.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        //INICIALIZO EL RECYCLER
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(linearLayoutManager);
        publicaciones = new ArrayList<>();
        favAdapter = new FavoritosAdapter(this, publicaciones);

        //ESCUCHA PARA IR A LA PUBLICACION SELECCIONADA
        favAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Publi publi = publicaciones.get(recyclerView.getChildAdapterPosition(v));
                Intent intent = new Intent(Favoritos.this, Publicacion.class);
                intent.putExtra("publiID", publi.UserID);             //se llama userID pero es el ID de la publicacion
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(favAdapter);

        favoritos();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //FUNCION QUE CARGA LA LISTA PARA ENVIARLE AL ADAPTER
    public void favoritos(){
        //entro a los likes del usuarios activo y guardo en la lista "lista" todos los ID de las publicaciones likeadas
        myRef.child("usuarios").child(userActivo).child("Likes").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                return Transaction.success(mutableData);   //PASO LOS RESULTADOS A ONCOMPLETE Y LOS MANEJO CON EL SNAPSHOT POR PRACTICIDAD
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot1) {
                for (DataSnapshot ds : dataSnapshot1.getChildren()) {                                   //RECORRO EL SNAPSHOT CON EL ID DE LAS PUBLICACIONES
                    myRef.child("Publicaciones").child(ds.getKey()).runTransaction(new Transaction.Handler() {      //HAGO LLAMADA A LA DB con el id de cada publicacion
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            return Transaction.success(mutableData);               //PASO LOS RESULTADOS A ONCOMPLETE Y LOS MANEJO CON EL SNAPSHOT POR PRACTICIDAD
                    }
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot2) {
                            if(dataSnapshot2.getValue() != null) {                                               //este chequeo es porque las publicaciones eliminadas siguen queando en favoritos
                                String link = dataSnapshot2.child("LinkFoto").getValue(String.class);           //descargamos link de la foto
                                String nombre = dataSnapshot2.child("Nombre").getValue(String.class);          //descargamos el nombre de la publicacion
                                publiID = dataSnapshot2.getKey();                                             //le pedimos el id de la publicacion
                                Publi p = new Publi("", link, nombre, "", publiID);       //creamos un objeto pero solo a modo de almacenaje de datos
                                publicaciones.add(p);                                                      //agregamos la Publi a la lista de Publi
                                Collections.reverse(publicaciones);                                       //invertimos la lista para que quede en orden
                                favAdapter.notifyDataSetChanged();                                       //avisamos al adapter que se actualizaron los objetos
                            }
                        }
                    });
                }
            }
        });
    }

    //BARRA DE NAVEGACION
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
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
                            startActivity(intent);
                            break;
                        case R.id.nav_search:
                            intent = new Intent(Favoritos.this, MainActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_mess:
                            intent = new Intent(Favoritos.this, VentanadeChat.class);
                            startActivity(intent);
                            break;
                    }
                    return true;
                }
            };
}
