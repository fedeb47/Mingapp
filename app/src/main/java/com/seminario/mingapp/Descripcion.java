package com.seminario.mingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.seminario.mingapp.R;

public class Descripcion extends AppCompatActivity {
    private EditText etDescripcion;
    private Button aceptar;
    private Button cancelar;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    DatabaseReference usuarioRef = myRef.child("usuarios");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userID = user.getUid();

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

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("descipcionnnnnn", String.valueOf(etDescripcion.getText()));
                usuarioRef.child(userID).child("Descripcion").setValue(etDescripcion.getText().toString().trim());
                Intent intent = new Intent(Descripcion.this, Perfil.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}
