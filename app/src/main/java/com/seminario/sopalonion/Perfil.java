package com.seminario.sopalonion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.login.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Perfil extends AppCompatActivity {
    private String userID;
    private ImageView ivFotoPerfil;
    private TextView tvNombre;
    private Button btnSeguir;
    private Button btnLogout;
    private BottomNavigationView barra;

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
        btnSeguir = (Button) findViewById(R.id.btnSeguir);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        barra = findViewById(R.id.bottom_navigation);

        barra.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        //Recibimos el userID
        Bundle datos = this.getIntent().getExtras();
        userID = datos.getString("userID");


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
                String nombre = dataSnapshot.child(userID).child("Nombre").getValue(String.class);
                String foto = dataSnapshot.child(userID).child("Foto").getValue(String.class);
                //String seguidor = dataSnapshot.child(userID).child("seguidores").child(user.getUid()).getValue(String.class);
                Boolean seguido = dataSnapshot.child(user.getUid()).child("seguidos").child(userID).getValue(Boolean.class);
                if(user.getUid().equals(userID)){
                    btnSeguir.setVisibility(View.INVISIBLE);
                    btnLogout.setVisibility(View.VISIBLE);
                }else {
                    if (seguido != null) {
                        if (seguido == true) btnSeguir.setText("Dejar de seguir");
                    } else btnSeguir.setText("Seguir");
                }

                //Log.d("NOMBRE-----------------", nombre);
                //Log.d("lo sigo?---------", seguido.toString());

                tvNombre.setText(nombre);

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
}
