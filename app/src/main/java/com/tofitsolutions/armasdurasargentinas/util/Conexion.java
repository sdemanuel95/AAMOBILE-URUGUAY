package com.tofitsolutions.armasdurasargentinas.util;

import android.util.Log;

import com.tofitsolutions.armasdurasargentinas.util.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion  {
    private Connection con;
    public Conexion() {
    }

    public Connection crearConexion() throws SQLException {
        // TEST
        //Connection con = DriverManager.getConnection("jdbc:mysql://b4e9xxkxnpu2v96i.cbetxkdyhwsb.us-east-1.rds.amazonaws.com:3306/fq9e54tk8ag0jl2f", "rnahyl78396j7usi", "z4x6xvpkmu82ptrc");

        //PRODUCCION
        con = DriverManager.getConnection("jdbc:mysql://b116114yu79iq05c.cbetxkdyhwsb.us-east-1.rds.amazonaws.com:3306/primary_app_db","vsso7a16nc4e8wth", "aky7wepaqgdo6qdn");

        //LOCAL
        //con = DriverManager.getConnection("jdbc:mysql://192.168.0.15:3306/armadurasargentinas","root","root");

        return con;
    }
}
