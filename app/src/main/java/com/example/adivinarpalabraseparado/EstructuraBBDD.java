package com.example.adivinarpalabraseparado;

public class EstructuraBBDD {
    private EstructuraBBDD() {

    }

    public static final String TABLE_NAME = "palabras";
    public static final String NOMBRE_COLUMNA1 = "id";
    public static final String NOMBRE_COLUMNA2 = "nombre";
    public static final String NOMBRE_COLUMNA3 = "descripcion";

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + EstructuraBBDD.TABLE_NAME + " (" +
                    EstructuraBBDD.NOMBRE_COLUMNA1 + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    EstructuraBBDD.NOMBRE_COLUMNA2 + " TEXT," +
                    EstructuraBBDD.NOMBRE_COLUMNA3 + " TEXT)";
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + EstructuraBBDD.TABLE_NAME;

}
