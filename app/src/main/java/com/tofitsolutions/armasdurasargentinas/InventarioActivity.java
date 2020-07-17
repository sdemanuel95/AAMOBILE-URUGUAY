package com.tofitsolutions.armasdurasargentinas;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.media.MediaPlayer;

import com.tofitsolutions.armasdurasargentinas.controllers.IngresoMPController;
import com.tofitsolutions.armasdurasargentinas.controllers.IngresoMP_TEMPController;
import com.tofitsolutions.armasdurasargentinas.util.Conexion;
import com.tofitsolutions.armasdurasargentinas.util.Util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class InventarioActivity extends AppCompatActivity {
    private Button bt_ok, bt_finalizar;
    private EditText et_codigoDeBarras, et_kgReal;
    private TextView tv_informativo, excepcion, tv_contador;
    private ProgressDialog progress;
    private ArrayList<String> materiasPrima;
    private int contadorDeChapasCargadas;
    String loteObtenido;
    String materialObtenido;
    String pesoObtenido;
    String kgReal;
    boolean validacion;
    String loteActual;
    MediaPlayer mp;
    IngresoMPController ingresoMPController = new IngresoMPController();
    IngresoMP_TEMPController ingresoMP_tempController = new IngresoMP_TEMPController();
    IngresoMP ingresoMP;
    String materiaAIngresar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);
        mp =  MediaPlayer.create(this, R.raw.click);
        contadorDeChapasCargadas = 0;
        tv_contador = (TextView) findViewById(R.id.textView_Contador);
        bt_ok = (Button) findViewById(R.id.button_ok);
        bt_finalizar = (Button) findViewById(R.id.button_Finalizar);
        et_codigoDeBarras = (EditText) findViewById(R.id.editText_CodigoDeBarras);
        et_codigoDeBarras.setHint("Por favor lea el codigo");
        et_codigoDeBarras.setHintTextColor(Color.RED);
        et_kgReal = (EditText) findViewById(R.id.editText_KgReal);
        et_kgReal.setHintTextColor(Color.RED);
        et_kgReal.setHintTextColor(Color.RED);
        tv_informativo = (TextView) findViewById(R.id.textView_Informativo);
        excepcion = (TextView) findViewById(R.id.textView_Exception);
        materiasPrima = new ArrayList<String>();
        materiaAIngresar = "";
        Intent intentRemitoMP = getIntent();

        et_codigoDeBarras.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String nro = editable.toString();
                if (nro.length() == 24){
                    ingresoMP = ingresoMPController.getMP(nro);

                    if(ingresoMP!=null){
                        et_kgReal.requestFocus();
                        et_kgReal.setHint("Por favor ingrese el peso");
                        et_kgReal.setHintTextColor(Color.RED);
                        et_kgReal.setText(ingresoMP.getKgDisponible());
                    }
                    else{

                        et_codigoDeBarras.setText("");
                        et_codigoDeBarras.setHint("Por favor lea el codigo");
                        et_codigoDeBarras.setHintTextColor(Color.RED);
                        et_codigoDeBarras.requestFocus();
                        AlertDialog.Builder builder = new AlertDialog.Builder(InventarioActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                        builder.setTitle("Atencion!");
                        builder.setMessage("El codigo de barras ingresado no existe.");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_red_light);
                        dialog.show();

                        return;
                    }

                }
            }
        });


        et_codigoDeBarras.setEnabled(true);

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                bt_ok.setEnabled(false);
                bt_ok.setClickable(false);
                System.out.println("Cantidad de materias ingresadas "+ materiasPrima.size());
                validacion = false;
                String codigoDeBarras = et_codigoDeBarras.getText().toString();
                loteActual = codigoDeBarras;
                kgReal = et_kgReal.getText().toString();
                System.out.println(kgReal);

                if(ingresoMP_tempController.existe(codigoDeBarras)){
                    et_codigoDeBarras.setText("");
                    et_codigoDeBarras.setHint("Por favor lea el codigo");
                    et_codigoDeBarras.setHintTextColor(Color.RED);
                    et_codigoDeBarras.requestFocus();
                    AlertDialog.Builder builder = new AlertDialog.Builder(InventarioActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                    builder.setTitle("Atencion!");
                    builder.setMessage("El precinto ya fue ingresado antes.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_red_light);
                    dialog.show();
                }
                if(!esNumero(kgReal)){

                    AlertDialog.Builder builder = new AlertDialog.Builder(InventarioActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                    builder.setTitle("Atencion!");
                    builder.setMessage("El codigo de barras ingresado no existe.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_red_light);
                    dialog.show();
                    return;
                }
                if(kgReal.length() == 4) {
                    if (codigoDeBarras.length() == 24) {

                        loteObtenido = Util.extraerLote(codigoDeBarras);
                        materialObtenido = Util.extraerLote(codigoDeBarras);
                        pesoObtenido = Util.extraerPeso(codigoDeBarras);
                        System.out.println(loteObtenido);
                        System.out.println(materialObtenido);
                        System.out.println(pesoObtenido);
                        // InventarioActivity.ValidarIngresoMP().execute();


                    }
                    else {
                        et_codigoDeBarras.setText("");
                        et_codigoDeBarras.setHint("Por favor lea el codigo");
                        et_codigoDeBarras.setHintTextColor(Color.RED);
                        et_codigoDeBarras.requestFocus();
                        AlertDialog.Builder builder = new AlertDialog.Builder(InventarioActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                        builder.setTitle("Atencion!");
                        builder.setMessage("El codigo de barras ingresado no existe.");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_red_light);
                        dialog.show();
                        return;
                    }
                }
                else{
                        System.out.println(kgReal);
                    et_kgReal.setText("");

                    et_kgReal.setHint("Por favor reingrese el peso.");
                        et_kgReal.setHintTextColor(Color.RED);
                        et_kgReal.requestFocus();
                    AlertDialog.Builder builder = new AlertDialog.Builder(InventarioActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                    builder.setTitle("Atencion!");
                    builder.setMessage("El peso debe tener 4 digitos.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_red_light);
                    dialog.show();
                        return;

                }


                materiaAIngresar = codigoDeBarras;

                for(String materia : materiasPrima){
                    if(materiaAIngresar.equals(materia.substring(0,24))){
                        et_codigoDeBarras.setText("");
                        et_kgReal.setText("");
                        et_codigoDeBarras.setHint("Por favor lea el codigo");
                        et_codigoDeBarras.setHintTextColor(Color.RED);
                        et_codigoDeBarras.requestFocus();
                        AlertDialog.Builder builder = new AlertDialog.Builder(InventarioActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                        builder.setTitle("Atencion!");
                        builder.setMessage("El codigo de barras ya fue ingresado.");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_red_light);
                        dialog.show();
                        return;
                    }
                }

                ingresoMP = ingresoMPController.getMP(codigoDeBarras);

                if(ingresoMP!=null){
                    et_kgReal.requestFocus();
                    et_kgReal.setHint("Por favor ingrese el peso");
                    et_kgReal.setHintTextColor(Color.RED);
                    et_kgReal.setText(ingresoMP.getKgDisponible());
                }
                else{

                    et_codigoDeBarras.setText("");
                    et_codigoDeBarras.setHint("Por favor lea el codigo");
                    et_codigoDeBarras.setHintTextColor(Color.RED);
                    et_codigoDeBarras.requestFocus();
                    AlertDialog.Builder builder = new AlertDialog.Builder(InventarioActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                    builder.setTitle("Atencion!");
                    builder.setMessage("El codigo de barras ingresado no existe.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_red_light);
                    dialog.show();
                    return;
                }


                et_kgReal.setText("");
                et_codigoDeBarras.setText("");
                et_codigoDeBarras.setHint("Por favor lea el codigo");
                et_codigoDeBarras.setHintTextColor(Color.RED);
                et_codigoDeBarras.requestFocus();
                AlertDialog.Builder builder = new AlertDialog.Builder(InventarioActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                builder.setTitle("Valicación correcta.");
                builder.setMessage("El precinto fue validado con éxito.");
                builder.setCancelable(false);
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_green_light);
                dialog.show();

                materiasPrima.add(materiaAIngresar + kgReal);
                et_codigoDeBarras.requestFocus();
            }
        });
        //boton finalizar
        bt_finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                new InventarioActivity.guardarMateriasPrimas().execute();
            }
        });
    }



    private class guardarMateriasPrimas extends AsyncTask<Void, Integer, Void> {

        private int progreso = 0;

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(InventarioActivity.this);
            progress.setMessage("Guardando");
            progress.setTitle("Materia Prima");
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            //progress.show();

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progress.incrementProgressBy(1);
            if (progreso == progress.getMax()) {
                progress.dismiss();
            }
            super.onProgressUpdate(values);
        }


        @Override
        protected Void doInBackground(Void... params) {

            Conexion conexion = new Conexion();
            progress.setMax(materiasPrima.size());

            try {
                Class.forName("com.mysql.jdbc.Driver");

                Connection con = conexion.crearConexion();

                for(String codigoDeBarras : materiasPrima){
                    ingresoMP_tempController.insertarIngresoMP_TEMP(codigoDeBarras);

                }
                Statement stmt = con.createStatement();
                String query2="update ajuste_inventario set ajuste=1;";
                stmt.executeUpdate(query2);

            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent i = new Intent(InventarioActivity.this, InicialActivity.class);
            finish();
            startActivity(i);
            super.onPostExecute(aVoid);
        }
    }

    private boolean esNumero(String num){
        try{
            double l = Double.parseDouble(num);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
}
