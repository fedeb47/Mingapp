package com.seminario.mingapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private String userID;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    DatabaseReference usuarioRef = myRef.child("usuarios");
    private EditText etBuscador;
    private BottomNavigationView barra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etBuscador = findViewById(R.id.etBuscador);

        //BARRA DE NAVEGACION INICIALIZO Y PONGO LISTENER
        barra = findViewById(R.id.bottom_navigation);
        barra.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        //FIREBASE OBETENGO INSTANCIA Y LUEGO PiDO USERID DEL USUARIO CONECTADO para pedir sus datos a la DB
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userID = user.getUid();
            displayProfileInfo(user);
        } else goLoginScreen();
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, activity_login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private void displayProfileInfo(FirebaseUser user) {
        String name = user.getDisplayName();                        //pido nombre de facebook a Firebase
        String photoUrl = user.getPhotoUrl().toString();            //pido URL de la foto de perfil de Facebook a Firebase
        String ID = user.getUid();                                  //pido ID de usuario a Firebase
        //photoUrl += "?type=large";                                  //agrego para que pida la imagen en tamaño grande para mejor calidad
        usuarioRef.child(ID).child("Nombre").setValue(name);       //defino variable Nombre en usuarios
        usuarioRef.child(ID).child("Foto").setValue(photoUrl);     //defino variable Foto en usuarios con el link de descarga de mi db
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void buscar(View view){
        String string = etBuscador.getText().toString();                      //obtengo el string a buscar
        Intent intent = new Intent(this, Buscador.class);      //creo intent
        intent.putExtra("string", string);                             //cargo el string intent
        startActivity(intent);
    }

    //barra de navegacion
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
                    Intent intent;
                    switch (menuItem.getItemId()){
                        case R.id.nav_add:
                            intent = new Intent(MainActivity.this, SubirPublicacion.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_perfil:
                            intent = new Intent(MainActivity.this, Perfil.class);
                            intent.putExtra("userID", userID);
                            startActivity(intent);
                            break;
                        case R.id.nav_favs:
                            intent = new Intent(MainActivity.this, Favoritos.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_search:
                            intent = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_mess:
                            intent = new Intent(MainActivity.this, VentanadeChat.class);
                            startActivity(intent);
                            break;
                    }
                    return true;
                }
            };
}
