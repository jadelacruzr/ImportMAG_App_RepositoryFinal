package com.importmag.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.importmag.R;

public class VentanaBuscarActivity extends AppCompatActivity {
    EditText strBusqueda;
    Button cancelar,buscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana_buscar);

        DisplayMetrics medidasVentana = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVentana);
        int ancho = medidasVentana.widthPixels;
        int alto = medidasVentana.heightPixels;
        getWindow().setLayout((int) (ancho * 0.85), (int) (alto * 0.4));


        strBusqueda = findViewById(R.id.txtStringBusqueda);

        cancelar = findViewById(R.id.btnCancelarB);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        buscar = findViewById(R.id.btnBuscar);
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strBusqueda.setFocusable(true);
                String palabra=strBusqueda.getText().toString();
                Intent i = new Intent(view.getContext(), BuscarProdsActivity.class);
                 i.putExtra("stringBusqueda",palabra);
                startActivity(i);
                finish();
            }
        });


    }
}