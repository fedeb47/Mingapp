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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.seminario.mingapp2.Adapter.BusquedaAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Buscador extends AppCompatActivity {
    private String string;
    private TextView tvUser1;
    private TextView tvBusqueda;
    private ImageButton btnBuscar;
    private EditText etBuscar;
    private String userID;
    private String link;
    private String publiID;

    private BottomNavigationView barra;

    RecyclerView recyclerView;
    BusquedaAdapter busquedaAdapter;
    List<List<String>> lista;
    List<String> publicacion;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscador);

        tvUser1 = (TextView) findViewById(R.id.tvUser1);
        tvBusqueda = (TextView) findViewById(R.id.tvBusqueda);
        btnBuscar = (ImageButton) findViewById(R.id.btnBuscar);
        etBuscar = (EditText) findViewById(R.id.etBuscar);
        barra = findViewById(R.id.bottom_navigation);
        barra.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        lista = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(linearLayoutManager);
        busquedaAdapter = new BusquedaAdapter(this, lista);
        busquedaAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publicacion = lista.get(recyclerView.getChildAdapterPosition(v));
                Intent intent = new Intent(Buscador.this, Publicacion.class);
                intent.putExtra("publiID", publicacion.get(0));                                     //paso ID de la publicacion
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(busquedaAdapter);

        //Recibimos los datos enviados desde el main con el string a buscar
        Bundle datos = this.getIntent().getExtras();
        string = datos.getString("string");

        //si el buscador esta vacio directamente no devuelvo nada
        if(string.equals("")){
            tvBusqueda.setText("No se encontraron resultados");
        } else{
            tvBusqueda.setText(string);
            resultado();
        }
    }



    public void resultado(){
        //Query q = myRef.child("Publicaciones").orderByChild("Lugar").equalTo(string);         //para implementar mas adelante agregando la localidad
        Query q = myRef.child("Publicaciones").orderByChild("Nombre");                         //pido todas las publicaciones para manejarlas con java
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot imageSnapshot: dataSnapshot.getChildren()) {                 //recorro snapshot de todas las publicaciones recibidas
                    publicacion = new ArrayList<>();
                    String nombre = imageSnapshot.child("Nombre").getValue(String.class);      //Nombre de la publicacion
                    if(nombre != null && string != null){
                        if(nombre.contains(string) && !string.equals("")){               //si el titulo contiene la palabra buscada entro a pedir los demas datos
                            publiID = imageSnapshot.getKey();                                               //id de publicacion
                            userID = imageSnapshot.child("UserID").getValue(String.class);                  //id del usuario que la publico
                            link = imageSnapshot.child("LinkFoto").getValue(String.class);                  //Foto de la publicacion

                            publicacion.add(publiID);
                            publicacion.add(nombre);
                            publicacion.add(link);
                            lista.add(publicacion);
                        }
                    }
                }
                Collections.reverse(lista);               //doy vuelta la lista para acomodarla
                busquedaAdapter.notifyDataSetChanged();     //aviso al adapter que se actualizaron los datos
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {        }
        });
    }

    //al presionar la lupa para iniciar la busqueda
    public void buscar(View view){
        String string = etBuscar.getText().toString();
        Intent intent = new Intent(this, Buscador.class);
        intent.putExtra("string", string);
        startActivity(intent);
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
                            break;
                        case R.id.nav_search:
                            intent = new Intent(Buscador.this, MainActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_favs:
                            intent = new Intent(Buscador.this, Favoritos.class);
                            //intent.putExtra("userID", userID);
                            startActivity(intent);
                            break;
                    }
                    return true;
                }
            };
}
