package com.example.adivinarpalabraseparado;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

public class FormularioPalabrasMongoDB extends AppCompatActivity {

    EditText nombrePalabra, descripcionPalabra;
    private Partida partida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_palabras_mongodb);

        Intent i = getIntent();
        if (i.getSerializableExtra("partida") != null) {
            partida = (Partida) i.getSerializableExtra("partida");
        }
        nombrePalabra = findViewById(R.id.nombrePalabraEditText2);
        descripcionPalabra = findViewById(R.id.descripcionPalabraEditText2);
    }

    public void agregar(View vista) {
        final String palabra = nombrePalabra.getText().toString();
        final String descripcion = descripcionPalabra.getText().toString();

    }
    private void getMongo() {
        //mongod --port 27017 --dbpath C:/MongoDB/data/db --bind_ip_all
        @SuppressLint("StaticFieldLeak")
        class GetMONGO extends AsyncTask<Void, Void, String> {
            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some time
            //Document doc;
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String jsonStr) {
                super.onPostExecute(jsonStr);
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "________________________json: " + jsonStr);
                System.out.println(jsonStr);
                Toast.makeText(getApplicationContext(), jsonStr, Toast.LENGTH_SHORT).show();
            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    String uri = "mongodb://192.168.1.57:27017";
                    MongoClient mongoClient = MongoClients.create(uri);
                    MongoDatabase db = mongoClient.getDatabase("adivinaPalabras");
                    MongoCollection<Document> collection = db.getCollection("mispalabras");

                    collection.find().forEach(doc -> {
                        System.out.println(doc.toJson());
                        String nombre = null;
                        String descripcion = null;
                        JSONObject jsonObject = new JSONObject(doc);
                        try {
                            nombre = jsonObject.getString("nombre").toUpperCase();
                            descripcion = jsonObject.getString("descripcion");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Palabra temp = new Palabra(nombre, descripcion);
                        partida.palabras.add(temp);

                    });

                    return uri;
                } catch (Exception e) {
                    e.printStackTrace();
                    e.getMessage();
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        GetMONGO getMongo = new GetMONGO();
        getMongo.execute();
    }
}