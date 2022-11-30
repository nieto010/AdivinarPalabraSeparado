package com.example.adivinarpalabraseparado;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView palabraSinResolver;
    private TextView palabrasDisponibles;
    private TextView pista;
    private Button botonAdivinar;
    private Button botonNuevo;
    private Partida p;
    private ArrayList<Palabra> palabrasInicio = new ArrayList<>();
    private final BBDD_Helper helper = new BBDD_Helper(this);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intento = getIntent();
        if (intento.getSerializableExtra("partida") != null) {
            p = (Partida) intento.getSerializableExtra("partida");
            p = new Partida(p.palabras);
            iniciarJuego();
        } else {
            crearPalabrasInicio();
            p = new Partida(palabrasInicio);
            iniciarJuego();
        }


    }

    @SuppressLint("SetTextI18n")
    public void iniciarJuego() {
        pista = findViewById(R.id.descripcion);
        botonNuevo = findViewById(R.id.nuevo);
        botonAdivinar = findViewById(R.id.adivinar);
        palabraSinResolver = findViewById(R.id.palabra);
        TextView numIntentos = findViewById(R.id.intentos);
        palabrasDisponibles = findViewById(R.id.palabrasDisponibles);
        TextView descripcion = findViewById(R.id.descripcion);
        if (!p.palabras.isEmpty()) {
            botonAdivinar.setEnabled(true);
            pista.setText("");
            //p.iniciarPartida();
            numIntentos.setText("Intentos: " + p.getIntentos());
            palabrasDisponibles.setText("Palabras disponibles: " + p.getPalabras().size());
            palabraSinResolver.setText(p.mostrarPalabra());
            pista.setText(p.getPalabras().get(p.getPosicionPalabra()).getDescripcion());
        } else {
            Toast.makeText(this, "No hay palabras", Toast.LENGTH_SHORT).show();
            botonAdivinar.setEnabled(false);
            botonNuevo.setEnabled(false);
            palabraSinResolver.setText("");
            descripcion.setText("");
            palabrasDisponibles.setText("Palabras disponibles: " + p.palabras.size());
        }
    }

    public void probarLetra(View vista) {
        EditText letra = findViewById(R.id.letra);
        String letraAProbar = letra.getText().toString().toUpperCase();

        if (letraAProbar.equals("")) {
            Toast.makeText(getApplicationContext(),"Inserte una letra", Toast.LENGTH_LONG).show();
        } else {
            p.probarLetra(letraAProbar);
            mostrarDatos();
            cuadroDialogo();
        }
    }


    @SuppressLint("SetTextI18n")
    public void mostrarDatos() {
        palabraSinResolver = findViewById(R.id.palabra);
        TextView intentosText = findViewById(R.id.intentos);
        EditText letra = findViewById(R.id.letra);
        palabraSinResolver.setText(p.mostrarPalabra());
        intentosText.setText("Intentos: " + p.getIntentos());
        letra.setText("");
    }


    public void iniciarNuevoJuego(View vista) {
        p = new Partida(p.palabras);
        iniciarJuego();
    }

    public void cuadroDialogo() {
        botonAdivinar = findViewById(R.id.adivinar);
        if (p.getIntentos() == 0) {
            Toast.makeText(getApplicationContext(), "Te has quedado sin intentos" , Toast.LENGTH_LONG).show();
            botonAdivinar.setEnabled(false);
        }
        if (p.terminarPartida() == 0) {
            Toast.makeText(getApplicationContext(), "Has ganado la partida", Toast.LENGTH_LONG).show();
            botonAdivinar.setEnabled(false);
        }
    }

    public void exportarArchivoTXT() {
        String nombreArchivo = "palabras.txt";
        try {
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput(nombreArchivo, Context.MODE_PRIVATE));
            if (!p.palabras.isEmpty()) {
                for (int i = 0; i < p.palabras.size(); i++) {
                    archivo.write(p.palabras.get(i).getNombre() + "," + p.palabras.get(i).getDescripcion() + "\n");
                }
            } else {
                Toast.makeText(getApplicationContext(), "A", Toast.LENGTH_SHORT).show();
            }
            archivo.flush();
            archivo.close();
            Toast.makeText(getApplicationContext(), "Datos guardados", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(), "No se pudo crear el archivo", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void importarFicheroTXT(){
        String nombreArchivo = "palabras.txt";
        try{
            InputStreamReader archivo = new InputStreamReader(openFileInput(nombreArchivo));
            BufferedReader br = new BufferedReader(archivo);
            String linea;
            while ((linea =br.readLine()) != null) {
                String[] palabrasSeparadas = linea.split(",");
                p.palabras.add(new Palabra(palabrasSeparadas[0].toUpperCase(), palabrasSeparadas[1]));
            }
                palabrasDisponibles.setText("Palabras disponibles: " + p.getPalabras().size());
                botonNuevo.setEnabled(true);
            br.close();
            archivo.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(), "No existe el archivo", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportarFicheroObjetos() {
        ObjectOutput oos = null;
        FileOutputStream fos = null;
        try {
            fos = getApplicationContext().openFileOutput("palabras.ser", Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            for (Palabra objeto: p.palabras) {
                oos.writeObject(objeto);
            }
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void importarFicheroObjetos() {
        FileInputStream fis = null;
        try {
            fis = getApplicationContext().openFileInput("palabras.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            while (true) {
                try {
                    Palabra p = (Palabra) ois.readObject();
                    palabrasInicio.add(p);
                } catch (EOFException eofe) {
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.verPalabra:
                verPalabraActual();
                return true;
            case R.id.agregarPalabra:
                crearPalabranueva();
                return true;
            case R.id.mostrarPalabras:
                mostrarPalabras();
                return true;
            case R.id.importarPalabras:
                importarFicheroTXT();
                return true;
            case R.id.exportarPalabras:
                exportarArchivoTXT();
                return true;
            case R.id.importarPalabrasObjetos:
                importarFicheroObjetos();
                return true;
            case R.id.exportarPalabrasObjetos:
                exportarFicheroObjetos();
                return true;
            case R.id.importarPalabrasMySQLite:
                importarPalabrasMySQLite();
                return true;
            case R.id.exportarPalabrasMySQLite:
                exportarPalabrasMySQLite();
                return true;
            case R.id.importarPalabrasMongoDB:
                getMongo();
                return true;
            case R.id.salir:
                salir();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void exportarPalabrasMySQLite() {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();

        long newrowId = -1;

        for (Palabra palabra: palabrasInicio) {
            values.put(EstructuraBBDD.NOMBRE_COLUMNA2, palabra.getNombre());
            values.put(EstructuraBBDD.NOMBRE_COLUMNA3, palabra.getDescripcion());

            newrowId = db.insert(EstructuraBBDD.TABLE_NAME, null, values);
        }

        if (newrowId == -1) {
            Toast.makeText(getApplicationContext(), "No se ha podido realizar la exportación", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Se ha realizado la exportación correctamente", Toast.LENGTH_SHORT).show();
        }
    }

    private void importarPalabrasMySQLite() {
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = {
                EstructuraBBDD.NOMBRE_COLUMNA2,
                EstructuraBBDD.NOMBRE_COLUMNA3
        };

        try {
            Cursor cursor = db.query(
                    EstructuraBBDD.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            while (cursor.moveToNext()) {
                String itemName = cursor.getString(cursor.getColumnIndexOrThrow(EstructuraBBDD.NOMBRE_COLUMNA2));
                String itemDescription = cursor.getString(cursor.getColumnIndexOrThrow(EstructuraBBDD.NOMBRE_COLUMNA3));
                p.palabras.add(new Palabra(itemName, itemDescription));
            }

            cursor.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "El registro no existe", Toast.LENGTH_SHORT).show();
        }
    }

    private void salir() {
        finish();
    }


    private void verPalabraActual() {
        Toast.makeText(this,"La palabra es: " + p.getPalabra(),Toast.LENGTH_LONG).show();
    }
    @SuppressLint("SetTextI18n")
    private void crearPalabranueva() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Context context = this;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText nombre = new EditText(context);
        nombre.setHint("Nombre de la palabra");
        layout.addView(nombre);
        final EditText descripcion = new EditText(context);
        descripcion.setHint("Descripcion de la palabra");
        layout.addView(descripcion);
        builder.setTitle("Crear nueva Palabra")
                .setMessage("Mensaje")
                .setView(layout)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    if (!nombre.getText().toString().equalsIgnoreCase("") && !descripcion.getText().toString().equalsIgnoreCase("")) {
                        p.palabras.add(new Palabra(nombre.getText().toString().toUpperCase(), descripcion.getText().toString()));
                        palabrasDisponibles.setText("Palabras Disponibles: " + p.palabras.size());
                    } else {
                        Toast.makeText(getApplicationContext(), "Introduce un nombre y una descripcion por favor", Toast.LENGTH_LONG).show();
                    }
                    if (!p.palabras.isEmpty()) {
                        botonNuevo = findViewById(R.id.nuevo);
                        botonNuevo.setEnabled(true);
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void mostrarPalabras() {
        Intent i = new Intent(this, EditarPalabras.class);
        i.putExtra("partida", p);
        //i.putExtra("palabras",p.getPalabras());

        startActivity(i);
    }

    public void crearPalabrasInicio() {
        Palabra p1 = new Palabra("XML", "lenguaje texto plano");
        Palabra p2 = new Palabra("JAVA", "lenguaje orientado a objetos");
        palabrasInicio.add(p1);
        palabrasInicio.add(p2);
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
                        p.palabras.add(temp);

                    });
                    palabrasDisponibles.setText("Palabras disponibles: " + String.valueOf(p.palabras.size()));

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