package com.seminario.sopalonion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SubirPublicacion extends AppCompatActivity {
        private Usuario Usuario;
        private Fotos Fotos;
        private Rubro Rubro;
        private Usuario[] Likes;
        private Comentarios Comentarios;

        private String userID;
        private String id;
        private String Nombre;
        private String Link;
        private String Descripcion;
        private String Precio;
        private String mensajeErorr;

        private EditText etNombre;
        private EditText etDescripcion;
        private EditText etPrecio;
        private Button bExaminar;
        private Button bPublicar;
        private ProgressBar progressBar;
        private TextView tvPrecio;
        private TextView tvSubirImgen;
        private TextView tvError;

        private StorageReference mStorageRef;
        private DatabaseReference myRef;
        private static final int GALERY_INTENT = 1;
        private Uri downloadUri;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_subir_publicacion);
            //Toast.makeText(getApplicationContext(), "oncreate", Toast.LENGTH_SHORT).show();
            mStorageRef = FirebaseStorage.getInstance().getReference();
            myRef = FirebaseDatabase.getInstance().getReference();
            etNombre = (EditText) findViewById(R.id.etNombre);
            etDescripcion = (EditText) findViewById(R.id.etDescripcion);
            etPrecio = (EditText) findViewById(R.id.etPrecio);
            progressBar = (ProgressBar) findViewById(R.id.progressBar2);
            tvPrecio = (TextView) findViewById(R.id.tvPrecio);
            tvSubirImgen = (TextView) findViewById(R.id.tvSubirImagen);
            tvError = (TextView) findViewById(R.id.tvError);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                userID = user.getUid();
            }

            bExaminar = (Button) findViewById(R.id.bExaminar);
            bExaminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALERY_INTENT);
                    //progressBar.setVisibility(View.VISIBLE);
                }
            });

            bPublicar = (Button) findViewById(R.id.bPublicar);
            bPublicar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Nombre = etNombre.getText().toString();
                    Descripcion = etDescripcion.getText().toString();
                    Precio = (etPrecio.getText().toString());
                    Log.d("-----NOMBRE-----", Nombre);
                    if(Nombre.length() == 0){
                        mensajeErorr = "Debes ponerle un nombre a la publicacion";
                        fError(mensajeErorr);
                    }else{
                        if(Descripcion.length() == 0){
                            mensajeErorr = "Debes ponerle una descripcion al articulo";
                            fError(mensajeErorr);
                        }
                        else{
                            if(downloadUri == null){
                                mensajeErorr = "Debes subir una imagen";
                                fError(mensajeErorr);
                            }else{
                                Link = downloadUri.toString();
                                if(Precio.length() == 0){
                                    mensajeErorr = "Debes ponerle un precio al articulo";
                                    fError(mensajeErorr);
                                }else{
                                    Log.d("LINK DESCARGA", downloadUri.toString());
                                    Publi p = new Publi(userID, Nombre, Link, Descripcion, Precio);        //creo objeto publicacion
                                    DatabaseReference publi = myRef.child("Publicaciones");                //referencio las publicaciones
                                    DatabaseReference userRef = myRef.child("usuarios").child(userID);     //referencio al usuario conectado
                                    id = publi.push().getKey();                                            //creo y obtengo key para la publicacion
                                    userRef.child("Publicaciones").child(id).setValue("true");      //creo una clave para cada publicacion dentro del user (?) sino me pisa el dato
                                    publi.child(id).setValue(p);                                           //cargo objeto Publicacion a la base de datos
                                    Publican();
                                }
                            }
                        }
                    }
                }
            });
        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALERY_INTENT && resultCode == RESULT_OK){
            cargando();
            Uri uri = data.getData();

            final StorageReference filePath = mStorageRef.child("fotos").child(uri.getLastPathSegment());

            filePath.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    //Toast.makeText(SubirPublicacion.this, linkDescarga.toString(), Toast.LENGTH_LONG).show();
                    //Log.d("LINK DESCARGA", linkDescarga.toString());
                    //return filePath.getDownloadUrl();
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    cargaOK();
                    if (task.isSuccessful()) {
                        downloadUri = task.getResult();
                        Log.d("SUBIDA COMPLETA", downloadUri.toString());
                    } else {
                        Toast.makeText(SubirPublicacion.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void Publican(){
        //Nombre = etNombre.getText().toString();
        //Log.d("LINK DESCARGA", downloadUri.toString());
        // Publicacion p = new Publicacion(downloadUri);
       // String id = myRef.push().getKey();
       // Publi p = new Publi(Nombre, downloadUri);
       // myRef.child(id).setValue(p);
        //dbPublicacion.setValue(downloadUri);
        Intent intent = new Intent(this, Publicacion.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("user", userID);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    public void cargando(){
        progressBar.setVisibility(View.VISIBLE);
        //tvSubirImgen.setText("Cargando imagen");
        tvSubirImgen.setVisibility(View.GONE);
        tvError.setVisibility(View.VISIBLE);
        tvError.setText("Cargando imagen");
        etNombre.setVisibility(View.GONE);
        etDescripcion.setVisibility(View.GONE);
        etPrecio.setVisibility(View.GONE);
        bExaminar.setVisibility(View.GONE);
        bPublicar.setVisibility(View.GONE);
        tvPrecio.setVisibility(View.GONE);
    }

    public void cargaOK(){
        progressBar.setVisibility(View.GONE);
        tvSubirImgen.setText("Imagen subida exitosamente");
        //tvError.setText("Imagen subida exiosemente");
        tvError.setVisibility(View.GONE);
        tvSubirImgen.setVisibility(View.VISIBLE);
        tvSubirImgen.setTextColor(Color.RED);
        tvSubirImgen.setTypeface(null, Typeface.ITALIC);
        etNombre.setVisibility(View.VISIBLE);
        etDescripcion.setVisibility(View.VISIBLE);
        etPrecio.setVisibility(View.VISIBLE);
        bPublicar.setVisibility(View.VISIBLE);
        tvPrecio.setVisibility(View.VISIBLE);
    }

    public void fError(String er){
        //etNombre.setVisibility(View.GONE);
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(er);
    }

}
