package com.seminario.sopalonion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class Buscador extends AppCompatActivity {
    private String string;
    private TextView tvString;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscador);
        //tvString = (TextView) findViewById(R.id.textView10);

        Bundle datos = this.getIntent().getExtras();
        string = datos.getString("string");
        //tvString.setText("Resultado de la busqueda: " + string);

    }

    @Override
    protected void onStart() {
        super.onStart();

        //consulto en publicaciones un nombre igual al buscado
        Query q=myRef.child("Publicaciones").orderByChild("Nombre").equalTo(string);
        //Query q=myRef.child("Publicaciones");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //recorro snapshot de todas las publicaciones con el nombre que se busca
                for (DataSnapshot imageSnapshot: dataSnapshot.getChildren()) {
                    String g = imageSnapshot.child("Nombre").getValue(String.class); //nombre de la publicacion
                    String userID = imageSnapshot.child("UserID").getValue(String.class); //id del usuario que la publico
                    String linkFoto = imageSnapshot.child("LinkFoto").getValue(String.class); //Foto de la publicacion

                    Query q = myRef.child("usuarios").child(userID); //mediante el userid sacamos los datos del usuario
                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String nombre = dataSnapshot.child("nombre").getValue(String.class); //nombre del usuario
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {    }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {        }

        });


    }
}
