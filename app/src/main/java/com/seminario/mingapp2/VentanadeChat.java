package com.seminario.mingapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VentanadeChat extends AppCompatActivity {
    private BottomNavigationView barra;
    private String userActivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c_ventanade_chat);

        //BARRA DE NAVEGACION INICIALIZO Y PONGO LISTENER
        barra = findViewById(R.id.bottom_navigation);
        barra.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        //FIREBASE OBETENGO INSTANCIA Y LUEGO PiDO USERID DEL USUARIO CONECTADO
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {        userActivo = user.getUid();        }



    }

    //BARRA DE NAVEGACION
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
                    Intent intent;
                    switch (menuItem.getItemId()){
                        case R.id.nav_add:
                            intent = new Intent(VentanadeChat.this, SubirPublicacion.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_perfil:
                            intent = new Intent(VentanadeChat.this, Perfil.class);
                            intent.putExtra("userID", userActivo);
                            startActivity(intent);
                            break;
                        case R.id.nav_favs:
                            intent = new Intent(VentanadeChat.this, Favoritos.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_search:
                            intent = new Intent(VentanadeChat.this, MainActivity.class);
                            startActivity(intent);
                            break;
                    }
                    return true;
                }
            };
}
