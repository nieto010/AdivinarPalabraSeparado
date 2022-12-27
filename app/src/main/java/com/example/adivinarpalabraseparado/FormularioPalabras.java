package com.example.adivinarpalabraseparado;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FormularioPalabras extends AppCompatActivity {

    private EditText nombre;
    private EditText descripcion;
    private Partida partida;
    private int posicion;
    private final BBDD_Helper helper = new BBDD_Helper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_palabras);

        nombre = findViewById(R.id.nombrePalabraEditText2);
        descripcion = findViewById(R.id.descripcionPalabraEditText2);
        Intent i = getIntent();
        try {
            partida = (Partida) i.getSerializableExtra("partida");
            posicion = i.getIntExtra("posicion", -1);
            nombre.setText(partida.getPalabras().get(posicion).getNombre());
            descripcion.setText(partida.getPalabras().get(posicion).getDescripcion());
        } catch (IndexOutOfBoundsException iobe){
            partida = (Partida) i.getSerializableExtra("partida");
            posicion = -1;
        }


    }

    @SuppressLint("SuspiciousIndentation")
    public void eliminarPalabra(View vista) {
        nombre = findViewById(R.id.nombrePalabraEditText2);
        descripcion = findViewById(R.id.descripcionPalabraEditText2);
        if (!nombre.getText().toString().equalsIgnoreCase("") && !descripcion.getText().toString().equalsIgnoreCase("")) {
            for (int i = 0; i < partida.getPalabras().size(); i++) {
                if (partida.getPalabras().get(i).getNombre().equals(nombre.getText().toString().toUpperCase())) {
                    partida.getPalabras().remove(i);
                }
            }
        } else {
                Toast.makeText(getApplicationContext(), "Introduce un nombre de la palabra y su descripcion", Toast.LENGTH_SHORT).show();
        }
        nombre.setText("");
        descripcion.setText("");
    }

    public void modificarPalabra(View vista) {
        nombre = findViewById(R.id.nombrePalabraEditText2);
        descripcion = findViewById(R.id.descripcionPalabraEditText2);
        if (!nombre.getText().toString().equalsIgnoreCase("") && !descripcion.getText().toString().equalsIgnoreCase("")) {
            for (int i = 0; i < partida.getPalabras().size(); i++) {
                if (partida.getPalabras().get(i).getNombre().equals(nombre.getText().toString().toUpperCase())) {
                    partida.getPalabras().get(i).setNombre(nombre.getText().toString().toUpperCase());
                    partida.getPalabras().get(i).setDescripcion(descripcion.getText().toString());
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Introduce un nombre de la palabra y su descripcion", Toast.LENGTH_SHORT).show();
        }
        Intent i = new Intent(this, EditarPalabras.class);
        i.putExtra("partida", partida);
        startActivity(i);
    }

    public void agregarPalabra(View vista) {
        nombre = findViewById(R.id.nombrePalabraEditText2);
        descripcion = findViewById(R.id.descripcionPalabraEditText2);
        if (!nombre.getText().toString().equalsIgnoreCase("") && !descripcion.getText().toString().equalsIgnoreCase("")) {
            partida.palabras.add(new Palabra(nombre.getText().toString().toUpperCase(), descripcion.getText().toString()));
        } else {
            Toast.makeText(getApplicationContext(), "Introduce un nombre de la palabra y su descripcion", Toast.LENGTH_SHORT).show();
        }
        Intent i = new Intent(this, EditarPalabras.class);
        i.putExtra("partida", partida);
        startActivity(i);
    }

    public void volverAtras(View vista) {
        Intent i = new Intent(this, EditarPalabras.class);
        i.putExtra("partida", partida);
        startActivity(i);
    }

    public void agregarPalabraMySQLite(View v) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(EstructuraBBDD.NOMBRE_COLUMNA2, nombre.getText().toString());
        values.put(EstructuraBBDD.NOMBRE_COLUMNA3, descripcion.getText().toString());

        long newRowId = db.insert(EstructuraBBDD.TABLE_NAME, null, values);

        if (newRowId > 0 ){
            Toast.makeText(getApplicationContext(),"Se guardó el registro con clave: " + newRowId, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(),"No se puedo guardar el registro", Toast.LENGTH_LONG).show();
        }

    }

    public void borrarPalabraMySQLite(View v) {
        SQLiteDatabase db = helper.getWritableDatabase();

        String selection = EstructuraBBDD.NOMBRE_COLUMNA2 + " LIKE ?";
        String[] selectionArgs = { nombre.getText().toString() };

        db.delete(EstructuraBBDD.TABLE_NAME, selection, selectionArgs);

        Toast.makeText(getApplicationContext(), "Se borró el registro con clave", Toast.LENGTH_SHORT).show();

        nombre.setText("");
        descripcion.setText("");
    }

    public void buscarPalabraMySQLite(View v) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = {
                EstructuraBBDD.NOMBRE_COLUMNA1,
                EstructuraBBDD.NOMBRE_COLUMNA3
        };

        String selection = EstructuraBBDD.NOMBRE_COLUMNA2 + " = ?";
        String[] selectionArgs = { nombre.getText().toString()};

        Cursor c = db.query(
                EstructuraBBDD.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        try {
            c.moveToFirst();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "No se encontró el registro", Toast.LENGTH_SHORT).show();
        }


        descripcion.setText(c.getString(2));
    }

    public void modificarPalabraMySQLite(View v) {
        SQLiteDatabase db = helper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(EstructuraBBDD.NOMBRE_COLUMNA2, nombre.getText().toString());
        values.put(EstructuraBBDD.NOMBRE_COLUMNA3, descripcion.getText().toString());

        String selection = EstructuraBBDD.NOMBRE_COLUMNA2 + " LIKE ?";
        String[] selectionArgs = { nombre.getText().toString() };

        int count = db.update(
                EstructuraBBDD.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        Toast.makeText(getApplicationContext(), "Se actualizó el registro", Toast.LENGTH_SHORT).show();

    }
}