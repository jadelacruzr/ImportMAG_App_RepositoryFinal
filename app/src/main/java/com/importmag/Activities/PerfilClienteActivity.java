package com.importmag.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.importmag.R;

import org.json.JSONException;
import org.json.JSONObject;

public class PerfilClienteActivity extends AppCompatActivity {

    ImageView btncerrar,cargando;
    ConstraintLayout clPU;
    Button cmbcontra;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_cliente);
        cmbcontra=findViewById(R.id.btncambiarcontra);
        cmbcontra.setVisibility(View.INVISIBLE);
        btncerrar=findViewById(R.id.btnCerrar);
        cargando=findViewById(R.id.img_cargando2);
        cargando.setVisibility(View.VISIBLE);
        clPU=findViewById(R.id.clPU);
        clPU.setVisibility(View.INVISIBLE);
        perfConsumo();


        btncerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cmbcontra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PerfilClienteActivity.this, EditarPerfilActivity.class));
            }
        });

    }

    /*
    Metodo que hace get a la api accountInfo
    * */
    private void perfConsumo() {

        final String url = "https://import-mag.com/rest/accountInfo";

        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject resp = new JSONObject(response);
                    JSONObject psdata = resp.getJSONObject("psdata");
                    String firstname = psdata.getString("firstname");
                    String lastname = psdata.getString("lastname");
                    String email = psdata.getString("email");
                    String gender = psdata.getString("id_gender");
                    TextView nombtxt, apellidotxt, emailtxt, generotxt;
                    generotxt=findViewById(R.id.txt_esteban);
                    if (gender.equals("1")) {
                        generotxt.setText("Sr.");
                    }else if(gender.equals("2")){
                        generotxt.setText("Sra/Srta.");
                    }


                    nombtxt = findViewById(R.id.txtNumTelf1);
                    apellidotxt = findViewById(R.id.txtSlogan1);
                    emailtxt = findViewById(R.id.txt_correoUser);

                    nombtxt.setText(lastname);
                    apellidotxt.setText(firstname);
                    emailtxt.setText(email);
                    clPU.setVisibility(View.VISIBLE);
                    cargando.setVisibility(View.INVISIBLE);
                    cmbcontra.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Handle your errors
            }
        };

        StringRequest request2 = new StringRequest(Request.Method.GET, url,
                responseListener, errorListener) {
        };
        Volley.newRequestQueue(PerfilClienteActivity.this).add(request2);

    }

}