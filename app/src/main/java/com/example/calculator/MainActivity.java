package com.example.calculator;

import static android.content.ContentValues.TAG;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button Average, logoutButton;
    TextView commentaire, td, tp, exam;
    int i, sumCoef;
    float Weighted;
    List<Module> moduleList = new ArrayList<>();
    com.android.volley.RequestQueue queue;
    String url = "https://num.univ-biskra.dz/psp/formations/get_modules_json?sem=1&spec=184";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

        setJosn(new ModuleCallback() {
            @Override
            public void onModulesLoaded(List<Module> moduleList) {
                ModuleAdapter moduleAdapter = new ModuleAdapter(moduleList);
                RecyclerView modulesRecyclerView = findViewById(R.id.modulesRecyclerView);
                modulesRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                modulesRecyclerView.setAdapter(moduleAdapter);

                // Calculate the Average
                Average = findViewById(R.id.Average);
                Average.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tp = findViewById(R.id.tpEditText);
                        td = findViewById(R.id.tdEditText);
                        exam = findViewById(R.id.examEditText);

                        // Hide keyboard
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(tp.getWindowToken(), 0);
                            imm.hideSoftInputFromWindow(td.getWindowToken(), 0);
                            imm.hideSoftInputFromWindow(exam.getWindowToken(), 0);
                        }

                        i = 1;
                        for (Module m : moduleList) {
                            if (m.getAverage() < 0) {
                                i = 0;
                                break;
                            }
                        }

                        if (i == 1) {
                            Weighted = 0;
                            sumCoef = 0;
                            for (Module m : moduleList) {
                                sumCoef = sumCoef + m.getCoef();
                                Weighted = Weighted + m.getAverage() * m.getCoef();
                            }
                            Weighted = Weighted / sumCoef;

                            commentaire = findViewById(R.id.commentaire);
                            if (Weighted >= 10) {
                                commentaire.setText("Weighted Average : " + Weighted + "\nPass");
                                commentaire.setTextColor(Color.GREEN);
                            } else {
                                commentaire.setText("Weighted Average : " + Weighted + "\nFail");
                                commentaire.setTextColor(Color.RED);
                            }
                        }
                    }
                });
            }
        });
    }

    void setJosn(ModuleCallback callback) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        if (isNetworkAvailable()) {
            queue = Volley.newRequestQueue(this);
            JsonArrayRequest jar = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                moduleList.clear();
                                dbHelper.clearModules();
                                for (i = 0; i < response.length(); i++) {
                                    JSONObject module = response.getJSONObject(i);
                                    Module m = new Module();
                                    m.setName(module.getString("Nom_module"));
                                    m.setCoef(module.getInt("Coefficient"));
                                    m.setCred(module.getInt("Credit"));
                                    moduleList.add(m);
                                    dbHelper.insertModule(m);
                                }
                                callback.onModulesLoaded(moduleList);
                            } catch (JSONException e) {
                                Log.e("API_ERROR", "Error parsing JSON: " + e.getMessage());
                            }

                        }
                    }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API_ERROR", "Volley error: " + error.getMessage());
                        moduleList = dbHelper.getAllModules();
                        callback.onModulesLoaded(moduleList);
                    }
                    });

            queue.add(jar);
        }else {
            Toast.makeText(this, "Offline mode. Loading from local storage.", Toast.LENGTH_SHORT).show();
            moduleList = dbHelper.getAllModules();
            callback.onModulesLoaded(moduleList);
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) return false;

            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            // For older devices
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }


}
