package com.seminario.mingapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class VentanadeChat extends AppCompatActivity {
    private BottomNavigationView barra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c_ventanade_chat);

        barra = findViewById(R.id.bottom_navigation);


    }
}
