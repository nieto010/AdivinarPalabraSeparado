package com.example.adivinarpalabraseparado;

import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Partida implements Serializable {
    private int intentos;
    private String palabra;
    public ArrayList<Palabra> palabras = new ArrayList<Palabra>();
    private boolean[] posicionesEncontradas;
    private char[] letras;
    private int posicionPalabra;

    public Partida(ArrayList<Palabra> palabras ) {
        this.palabras = palabras;
        iniciarPartida();
    }

    public void iniciarPartida(){
        Random generador = new Random();
        if (!palabras.isEmpty()) {
            posicionPalabra = generador.nextInt(palabras.size());
            palabra = palabras.get(posicionPalabra).getNombre();
            letras = palabra.toCharArray();
            posicionesEncontradas = new boolean[letras.length];
            Arrays.fill(posicionesEncontradas, false);
            intentos = palabra.length() / 2;
            letrasAleatorias();
            mostrarPalabra();
        }
    }

    private void letrasAleatorias() {
        int tamanio = palabra.length()/2;
        Random generador = new Random();
        int i = 0;
        while (i < tamanio) {
            int posicion = generador.nextInt(posicionesEncontradas.length);
            if (!posicionesEncontradas[posicion]) {
                posicionesEncontradas[posicion] = true;
                i++;
            }
        }
    }


    public String mostrarPalabra() {
        StringBuilder cadena = new StringBuilder();
        for (int i = 0; i < letras.length; i++) {
            if (!posicionesEncontradas[i]) {
                cadena.append('-');
            } else {
                cadena.append(letras[i]);
            }
        }
        return cadena.toString();
    }

    public void probarLetra(String letra) {
        boolean encontrado = false;

        if (intentos > 0) {
            for (int i = 0; i < letras.length; i++) {
                if (letras[i] == letra.charAt(0)) {
                    posicionesEncontradas[i] = true;
                    encontrado = true;
                }
            }
            if (!encontrado) intentos--;
        }
        terminarPartida();
    }

    public int terminarPartida() {
        int cuenta = 0;
        for (int i = 0; i < posicionesEncontradas.length; i++) {
            if (!posicionesEncontradas[i]) cuenta++;
        }
        return cuenta;
    }

    public int getIntentos() {
        return intentos;
    }

    public void setIntentos(int intentos) {
        this.intentos = intentos;
    }

    public String getPalabra() {
        return palabra;
    }

    public void setPalabra(String palabra) {
        this.palabra = palabra;
    }

    public ArrayList<Palabra> getPalabras() {
        return palabras;
    }

    public boolean[] getPosicionesEncontradas() {
        return posicionesEncontradas;
    }

    public void setPosicionesEncontradas(boolean[] posicionesEncontradas) {
        this.posicionesEncontradas = posicionesEncontradas;
    }

    public char[] getLetras() {
        return letras;
    }

    public void setLetras(char[] letras) {
        this.letras = letras;
    }

    public int getPosicionPalabra() {
        return posicionPalabra;
    }

    public void setPosicionPalabra(int posicionPalabra) {
        this.posicionPalabra = posicionPalabra;
    }
}
