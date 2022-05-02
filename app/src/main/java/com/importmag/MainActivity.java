package com.importmag;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.importmag.Activities.DetallesProductosActivity;
import com.importmag.Activities.PerfilClienteActivity;
import com.importmag.Activities.SobrenosActivity;
import com.importmag.Activities.VentanaBuscarActivity;
import com.importmag.R;
import com.importmag.databinding.ActivityMainBinding;
import com.importmag.Activities.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        //notificacion
        recibirtoken();
        notificacion();

        //Obteniendo datos del shared preferences
        String email_nn = getFromSharedPreferences(MainActivity.this, "email");
        String last_name = getFromSharedPreferences(MainActivity.this, "last_name");
        String first_name = getFromSharedPreferences(MainActivity.this, "first_name");

        // Mandando datos al header del drawer
        TextView emailTxt, nombre, apellido;
        final View vistaheader = binding.navView.getHeaderView(0);
        emailTxt = vistaheader.findViewById(R.id.txtEmailUser);
        nombre = vistaheader.findViewById(R.id.txtSlogan1);
        apellido = vistaheader.findViewById(R.id.txtNumTelf1);
        emailTxt.setText(email_nn);
        nombre.setText(first_name);
        apellido.setText(last_name);

        // Implementación de botones de redes sociales
        ImageView ins, wpp, fb;
        ins = binding.imgINSicono;
        wpp = binding.imgWPPicono;
        fb = binding.imgFBicono;

        ins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String insId = "https://instagram.com/_u/importmagecuador/";
                String urlPage = "https://www.instagram.com/importmagecuador/";

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(insId)));
                } catch (Exception e) {
                    Log.e(TAG, "Aplicación no Encontrada.");
                    //Abre url de pagina.
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlPage)));
                }

            }
        });

        wpp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://wa.me/593994013402?text="));
                startActivity(intent);

            }

        });

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String facebookId = "fb://page/102219398958150";
                String urlPage = "https://www.facebook.com/PruebaImport-102219398958150";

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookId)));
                } catch (Exception e) {
                    Log.e(TAG, "Aplicación no Encontrada.");
                    //Abre url de pagina.
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlPage)));
                }

            }
        });


        DrawerLayout drawer = binding.drawerLayout;
        NavigationView drawerNavView = binding.navView;
        BottomNavigationView bottomNavView = findViewById(R.id.nav_view2);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_inicio, R.id.nav_categorias, R.id.nav_todos_productos, R.id.nav_favoritos)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(drawerNavView, navController);
        NavigationUI.setupWithNavController(bottomNavView, navController);


        drawerNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navMicuenta) {
                    Intent i = new Intent(MainActivity.this, PerfilClienteActivity.class);
                    startActivity(i);
                }
                if (item.getItemId() == R.id.nav_SobreNos) {
                    Intent i = new Intent(MainActivity.this, SobrenosActivity.class);
                    startActivity(i);
                     }
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.itm_login_out:

                final String url = "https://import-mag.com/rest/logout";

                final Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                };

                final Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                };

                StringRequest request2 = new StringRequest(Request.Method.GET, url,
                        responseListener, errorListener) {
                };
                Volley.newRequestQueue(MainActivity.this).add(request2);
                Snackbar snackbar = Snackbar.make(getWindow().findViewById(android.R.id.content), "Cerrando sesión", Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.mensajeok));
                snackbar.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                }, 1500);

                SharedPreferences.Editor editor = getSharedPreferences("logvalidate", MODE_PRIVATE).edit();
                editor.clear().apply();

                return true;

            case R.id.itm_buscar:
                startActivity(new Intent(this, VentanaBuscarActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public static String getFromSharedPreferences(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences("logvalidate", Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    /**
     * MÉTODO QUE VERIFICA CONECTIVIDAD DEL DISPOSITIVO
     */
    public static Boolean isOnlineNet(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
    private void notificacion(){
        Intent intent = new Intent();
        String id = intent.getStringExtra("id_notificacion");
        System.out.println("LA NOTIFICACION TRAJO ESTO: "+id);
        if (id != null) {
            intent = new Intent(MainActivity.this, DetallesProductosActivity.class);
            Integer idprod = Integer.parseInt(id);
            intent.putExtra("id_product",idprod);
            startActivity(intent);
        }
    }
    public void recibirtoken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        System.out.println(token);
                    }
                });
    }

}