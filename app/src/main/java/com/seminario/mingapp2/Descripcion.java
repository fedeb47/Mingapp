package com.seminario.mingapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Descripcion extends AppCompatActivity {
    private EditText etDescripcion;
    private Button aceptar;
    private Button cancelar;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    DatabaseReference usuarioRef = myRef.child("usuarios");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userActivo = user.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descripcion);

        etDescripcion = (EditText) findViewById(R.id.etDescripcion);
        aceptar = (Button) findViewById(R.id.btnAceptar);
        cancelar = (Button) findViewById(R.id.btnCancelar);

        //Recibimos el back up de la descripcion para no acceder a base de datos
        Bundle datos = this.getIntent().getExtras();
        String back = datos.getString("back");
        etDescripcion.setText(back);

        //AL ACEPTAR GUARDAMOS EN BASE DE DATOS Y VOLVEMOS AL PERFIL
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuarioRef.child(userActivo).child("Descripcion").setValue(etDescripcion.getText().toString().trim());    //guardo en la base de datos la nueva descripcion
                Intent intent = new Intent(Descripcion.this, Perfil.class);                                //vuelvo al perfil
                intent.putExtra("userID", userActivo);                                                             //le paso el usuariio activo
                startActivity(intent);
            }
        });

        //AL CANCELAR VOLVEMOS ATRAS
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
                            intent = new Intent(Descripcion.this, SubirPublicacion.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_perfil:
                            intent = new Intent(Descripcion.this, Perfil.class);
                            intent.putExtra("userID", userActivo);
                            startActivity(intent);
                            break;
                        case R.id.nav_search:
                            intent = new Intent(Descripcion.this, MainActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_favs:
                            intent = new Intent(Descripcion.this, Favoritos.class);
                            //intent.putExtra("userID", userID);
                            startActivity(intent);
                            break;
                    }
                    return true;
                }
            };
}
