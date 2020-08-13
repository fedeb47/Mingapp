package com.seminario.mingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.seminario.mingapp.R;


public class MainActivity extends AppCompatActivity {

    private String userID;
    private ImageView ivFotoPerfil;
    private TextView tvNombreUser;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    DatabaseReference usuarioRef = myRef.child("usuarios");
    private TextView tvBienvenido;
    private EditText etBuscador;
    private BottomNavigationView barra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivFotoPerfil = findViewById((R.id.ivFotoPerfil));
        etBuscador = findViewById(R.id.etBuscador);
        barra = findViewById(R.id.bottom_navigation);

        barra.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if (user != null) {
            userID = user.getUid();
        } else goLoginScreen();
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
                    }

                    return true;
                }
            };

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, activity_login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();            //Desconecto Firebase
        LoginManager.getInstance().logOut();             //Desconecto Facebook
        goLoginScreen();                                 //Vuelvo al Login
    }

    private void displayProfileInfo(FirebaseUser user) {
        String name = user.getDisplayName();                        //pido nombre de facebook a Firebase
        String photoUrl = user.getPhotoUrl().toString();            //pido URL de la foto de perfil de Facebook a Firebase
        String ID = user.getUid();                                  //pido ID de usuario a Firebase
        photoUrl += "?type=large";                                  //agrego para que pida la imagen en tamaño grande para mejor calidad
        usuarioRef.child(ID).child("Nombre").setValue(name);       //defino variable Nombre en usuarios
        usuarioRef.child(ID).child("Foto").setValue(photoUrl);     //defino variable Foto en usuarios con el link de descarga de mi db
        name = name.split(" ")[0];                           //solo dejo el primer nombre
        tvNombreUser.setText(name + "!");

        Glide.with(getApplicationContext())
                .load(photoUrl)
                //.override(500,500)
                //.crossFade()  ------------------------------ANIMACION LUEGO DE CARGAR
                //.centerCrop()-------------------------------OCUPAR TODO EL ESPACIO DISPONIBLE
                //.placeholder(R.drawable.ic_temp_image) PONER IMAGEN TEMPORAL MIENTRAS SE DESCARGA LA IMAGEN REQUERIDA
                //.diskCacheStrategy(DiskCacheStrategy.ALL)---ESTRATEGIA DE CACHE A UTILIZAR
                //.thumbnail(0.5f) ---------------------------DESCARGAR MINIATURA MIENTRAS SE CARGA COMPLETA
                .into(ivFotoPerfil);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void crearPublicacion(View view) {
        Intent intent = new Intent(this, SubirPublicacion.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void buscar(View view){
        String string = etBuscador.getText().toString();                      //obtengo el string a buscar
        Intent intent = new Intent(this, Buscador.class);      //creo intent
        intent.putExtra("string", string);                             //cargo el string intent
        startActivity(intent);
    }
}
