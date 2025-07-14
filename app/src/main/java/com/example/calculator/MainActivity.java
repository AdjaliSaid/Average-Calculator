package com.example.calculator;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class MainActivity extends AppCompatActivity {
    Button Average, logoutButton;
    TextView commentaire;
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
            System.exit(0);
        });
        class ModuleListener implements ModuleCallback {
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
                });
            }
        }
        setJson(new ModuleListener());
    }

    void setJson(ModuleCallback callback) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        class MyListener implements Response.Listener<JSONArray> {
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
        }
        class MyErrorListener  implements Response.ErrorListener {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("API_ERROR", "Volley error: " + error.getMessage());
                moduleList = dbHelper.getAllModules();
                callback.onModulesLoaded(moduleList);
            }
        }
        if (isNetworkAvailable()) {
            queue = Volley.newRequestQueue(this);
            JsonArrayRequest jar = new JsonArrayRequest(Request.Method.GET, url, null,new MyListener(),new MyErrorListener());
            queue.add(jar);
        }else {
            Toast.makeText(this, "Offline mode. Loading from local storage.", Toast.LENGTH_SHORT).show();
            moduleList = dbHelper.getAllModules();
            callback.onModulesLoaded(moduleList);
        }
    }
    // methode to know if the app connected with network
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }


}
