package com.example.adivinarpalabraseparado;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class EditarPalabras extends Activity {

    private ListView lista;
    private ArrayList<String> palabras = new ArrayList<>();
    private Partida partida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_palabras);

        /*Bundle datos = getIntent().getExtras();
        palabras = (ArrayList<String>) datos.getSerializable("palabras");

        lista = findViewById(R.id.listView);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,android.R.id.text1, palabras);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intento = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.wikipedia.org"));
                startActivity(intento);
            }
        });*/

        Intent i = getIntent();
        if (i.getSerializableExtra("partida") != null) {
            partida = (Partida) i.getSerializableExtra("partida");
            for (int j = 0; j < partida.getPalabras().size(); j++) {
                palabras.add(partida.getPalabras().get(j).getNombre());
            }
        }
        lista = findViewById(R.id.listView);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, palabras);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String value = (String) adapter.getItem(position);
                //Toast.makeText(getApplicationContext(), "La descripcion de la palabra " + palabras.get(position).toLowerCase() + " es: " + partida.getPalabras().get(position).getDescripcion().toLowerCase() , Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext() , FormularioPalabras.class);
                i.putExtra("partida",partida);
                i.putExtra("posicion", position);
                startActivity(i);
            }
        });
    }

    public void volver(View vista) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("partida", partida);
        startActivity(i);
    }

    public void administrarMySQLite(View vista) {
        Intent i = new Intent(this, FormularioPalabras.class);
        i.putExtra("partida", partida);
        startActivity(i);
    }

    public void administrarMySQL(View vista) {
        Intent i = new Intent(this, FormularioPalabrasMongoDB.class);
        i.putExtra("partida", partida);
        startActivity(i);
    }

    public void borrarTodo(View vista) {
        partida.palabras.clear();
        lista.setAdapter(null);
    }


}