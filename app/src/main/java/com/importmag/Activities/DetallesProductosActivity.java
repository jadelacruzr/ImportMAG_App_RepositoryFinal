package com.importmag.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.importmag.R;
import com.importmag.Models.Slider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

public class DetallesProductosActivity extends AppCompatActivity {
    ImageView btnregresar, cargando2, compartirBtn, copiarBtn, btnmas, btnmenos;
    TextView nomb, subnombre, desc;
    ConstraintLayout clo;
    EditText edtCantprod;
    String url_prod;
    ExtendedFloatingActionButton btnCotizar;
    Integer cantidIms;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setStatusBarColor(ContextCompat.getColor(DetallesProductosActivity.this, R.color.white));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_productos);
        //IMPLEMENTACIÓN DEL SLIDER
        ImageSlider slider = findViewById(R.id.image_sliderDetalles);
        consumoDetallesprods(slider);

        nomb = findViewById(R.id.txtDetNombre);
        desc = findViewById(R.id.txtDetDescripcion);
        subnombre = findViewById(R.id.txtDesc_Short);
        btnCotizar = findViewById(R.id.btnWppMirianAnd);
        cargando2 = findViewById(R.id.img_cargando2);
        btnregresar = findViewById(R.id.btnCerrar);
        clo = findViewById(R.id.clo);
        compartirBtn = findViewById(R.id.compartir_button);
        copiarBtn = findViewById(R.id.copiar_buttom);
        btnmas = findViewById(R.id.btnSumprod);
        btnmenos = findViewById(R.id.btnrestProd);
        edtCantprod = findViewById(R.id.edtCantProd);
        btnCotizar.setVisibility(View.INVISIBLE);
        clo.setVisibility(View.INVISIBLE);
        cargando2.setVisibility(View.VISIBLE);


        btnmas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cant = edtCantprod.getText().toString();
                Integer canti = Integer.parseInt(cant);

                canti += 1;
                cant = canti.toString();
                edtCantprod.setText(cant);
            }
        });
        btnmenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cant = edtCantprod.getText().toString();
                Integer canti = Integer.parseInt(cant);
                if (canti > 1) {
                    canti -= 1;
                    cant = canti.toString();
                    edtCantprod.setText(cant);
                } else {
                    edtCantprod.setText("1");
                }

            }
        });


        copiarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", url_prod);
                clipboard.setPrimaryClip(clip);
                Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), "Enlace copiado al portapapeles", Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(DetallesProductosActivity.this, R.color.mensajeinfo));
                snackbar.show();


            }
        });

        compartirBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent compartir = new Intent(android.content.Intent.ACTION_SEND);
                compartir.setType("text/plain");
                String mensaje = url_prod;
                compartir.putExtra(android.content.Intent.EXTRA_SUBJECT, "Producto de ImporMAG");
                compartir.putExtra(android.content.Intent.EXTRA_TEXT, mensaje);
                startActivity(Intent.createChooser(compartir, "Compartir vía"));

            }
        });

        btnCotizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String cant = edtCantprod.getText().toString();
                if (!cant.isEmpty()) {
                    Integer canti = Integer.parseInt(cant);
                    if (canti >= 1) {
                        String cotizacion = "Saludos, Estoy interesado en cotizar este producto:\n"
                                + url_prod + "\n"
                                + "Cantidad del producto: " + cant;
                        intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=593962589522&text=" + cotizacion));
                        startActivity(intent);
                    } else {
                        Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), "La cantidad no debe ser menor a 1", Snackbar.LENGTH_LONG)
                                .setAction("Action", null);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(DetallesProductosActivity.this, R.color.mensaerror));
                        snackbar.show();
                        edtCantprod.setText("1");
                    }
                }else {
                    Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), "La cantidad no debe ser menor a 1", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(DetallesProductosActivity.this, R.color.mensaerror));
                    snackbar.show();
                    edtCantprod.setText("1");
                }

            }

        });


        btnregresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * Método que hace el consumo de la api Detalle de los productos
     */
    private void consumoDetallesprods(ImageSlider slider) {

        //IDS OBTENIDOS POR SELECCION
        Intent i = getIntent();
        Integer id_product = i.getIntExtra("id_product", 0);


        final String url = "https://import-mag.com/rest/productdetail?product_id=" + id_product;


        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject psdata = jsonObject.getJSONObject("psdata");
                    String name, description, descr_short, url_product;

                    name = psdata.getString("name");
                    description = psdata.getString("description");
                    descr_short = psdata.getString("description_short");
                    url_product = psdata.getString("product_url");
                    url_prod = url_product;
                    String descr = html2text(description);
                    String desc_sho = html2text(descr_short);

                    JSONArray images = psdata.getJSONArray("images");
                    List<Slider> sliderList = new ArrayList();
                    Integer numImages = images.length();
                    cantidIms = numImages;
                    for (int i = 0; i < numImages; i++) {
                        JSONObject aux = images.getJSONObject(i);
                        String url = aux.getString("src");
                        sliderList.add(new Slider(url));
                    }
                    ArrayList<SlideModel> remoteimages = new ArrayList();

                    for (Slider s : sliderList) {

                        remoteimages.add(new SlideModel(s.getImage(), ScaleTypes.CENTER_INSIDE));
                    }
                    slider.setImageList(remoteimages);
                    nomb.setText(name);
                    subnombre.setText(desc_sho);
                    desc.setText(descr);
                    cargando2.setVisibility(View.INVISIBLE);
                    clo.setVisibility(View.VISIBLE);
                    btnCotizar.setVisibility(View.VISIBLE);
                } catch (
                        JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), "Error de conexíon con el servidor", Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(DetallesProductosActivity.this, R.color.mensajeinfo));
                snackbar.show();


            }
        };

        StringRequest request2 = new StringRequest(Request.Method.GET, url,
                responseListener, errorListener) {
        };
        Volley.newRequestQueue(DetallesProductosActivity.this).add(request2);
    }

    public static String html2text(String html) {
        return Jsoup.parse(html).wholeText();
    }
}