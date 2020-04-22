package com.seminario.sopalonion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    private ImageView photoImageView;
    private TextView nameTextView;
    private ProfileTracker profileTracker;
    private Usuario usuario;
    Profile profile;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    DatabaseReference mensajeRef = myRef.child("mensaje");
    DatabaseReference contenidoRef = myRef.child("contenido");
    DatabaseReference usuarioRef = myRef.child("usuarios");
    private TextView texto;
    private TextView buscador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameTextView = (TextView) findViewById(R.id.textView4);
        photoImageView = findViewById((R.id.photoImageView));
        texto = findViewById(R.id.textView);
        buscador = findViewById(R.id.editText);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            displayProfileInfo(user);
        } else goLoginScreen();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mensajeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //contenidoRef.setValue("Hello, World!");
                Toast.makeText(getApplicationContext(), "entra cuando se modifica el mensaje", Toast.LENGTH_SHORT).show();
                String value = dataSnapshot.getValue(String.class);
                texto.setText(value);
                //Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
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
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goLoginScreen();
    }

    private void displayProfileInfo(FirebaseUser user) {
        String name = user.getDisplayName();
        String photoUrl = user.getPhotoUrl().toString();
        String ID = user.getUid();
        photoUrl += "?type=large";
        usuario = new Usuario(name, photoUrl);
        usuarioRef.child(ID).setValue(usuario);
        //String nuevo  = photoUrl.replace("height=100&width=100","height=500&width=500");//replaces all occurrences of "is" to "was"
        //Uri photoUrl = user.getPhotoUrl();
        //String photoUrl = profile.getProfilePictureUri(100, 100).toString();
        String[] arreglo = name.split(" ");
        //nameTextView.setText(arreglo[0]);
        nameTextView.setText(arreglo[0]);
        //Toast.makeText(getApplicationContext(), photoUrl, Toast.LENGTH_SHORT).show();
        //photoUrl = "https://static5lonelyplanetes.cdnstatics.com/sites/default/files/styles/max_1300x1300/public/blog/argentina_bestintravel2019_nido_huebl_shutterstock_0.jpg?itok=XATeMWcp";
        Glide.with(getApplicationContext())
                .load(photoUrl)
                //.override(500,500)
                //.crossFade()  ------------------------------ANIMACION LUEGO DE CARGAR
                //.centerCrop()-------------------------------OCUPAR TODO EL ESPACIO DISPONIBLE
                //.placeholder(R.drawable.ic_temp_image) PONER IMAGEN TEMPORAL MIENTRAS SE DESCARGA LA IMAGEN REQUERIDA
                //.diskCacheStrategy(DiskCacheStrategy.ALL)---ESTRATEGIA DE CACHE A UTILIZAR
                //.thumbnail(0.5f) ---------------------------DESCARGAR MINIATURA MIENTRAS SE CARGA COMPLETA
                .into(photoImageView);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

       // profileTracker.stopTracking();
    }
    public void crearPublicacion(View view) {
        Intent intent = new Intent(this, SubirPublicacion.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    public void buscar(View view){
        String string = buscador.getText().toString();
        Intent intent = new Intent(this, Buscador.class);
        intent.putExtra("string", string);
        startActivity(intent);
    }
}
