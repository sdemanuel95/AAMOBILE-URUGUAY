package com.tofitsolutions.armasdurasargentinas.util;

import com.tofitsolutions.armasdurasargentinas.Maquina;

import java.util.ArrayList;
import java.util.List;

public class Util {
    //static String host;
    static String dbLocal;
    //LOCAL
    //static String host = "192.168.0.20:8080";

    //PRODUCCION
    static String host = "armadurasargentinas-uruguay.herokuapp.com";


    //static String dbLocal = "192.168.5.109:3306";
    public static String getHost(){
        return host;
    }
    /*
    public static String getDbLocal(){
        return dbLocal;
    }
*/

	public static double setearDosDecimales(double numero) {

		double response = Math.round(numero* 10d) / 10d;
		return response;
	}


    public static int calcularPosible(double kgPrecinto, double kgItem,int cantItem,int merma){
        double resp= 0;

        double kgTotalItem = kgItem * cantItem;
        kgTotalItem = kgTotalItem + (merma * kgTotalItem / 100);
        if(cantItem == 0){
            return 0;
        }
        if(kgPrecinto >= kgTotalItem){
            return cantItem;
        }
        else{
            return calcularPosible(kgPrecinto, kgItem, cantItem-1,merma);
        }
    }



    public static List<Integer> calcularPosibleDosPrecintos(double kgPrecintoA, double kgPrecintoB, double kgItemUnitario, int cantItemPendientes, Maquina maquina){

        List<Integer> resultado = new ArrayList<Integer>(2);
        int cantidadPosibleP1 = 0;
        int cantidadPosibleP2 = 0;
        boolean piezas1ok = false;
        boolean piezas2ok = false;

        int merma = Integer.parseInt(maquina.getMerma());
        Double kgTotalItemTrue = kgItemUnitario * cantItemPendientes;

        if(cantItemPendientes == 1){

            //Si solo hay un 1 item pendiente,probamos la cantidad posible con el primer precinto.
            if(calcularPosible(kgPrecintoA,kgItemUnitario,1,merma) == 1){
                resultado.add(1);
                resultado.add(0);
            }
            else{
                if(calcularPosible(kgPrecintoB,kgItemUnitario,1,merma) == 1){
                    resultado.add(0);
                    resultado.add(1);
                }
                else{
                    resultado = null;
                }
            }
        }
        else{

            //Acá va toda la lógica normal que existía antes.

            boolean esPar = true;
            if((cantItemPendientes % 2) != 0){
                cantItemPendientes = cantItemPendientes - 1;
                esPar = false;
            }
            int proporcionCantidad =cantItemPendientes/2;
            cantidadPosibleP1 = calcularPosible(kgPrecintoA,kgItemUnitario,proporcionCantidad,merma);
            cantidadPosibleP2 = calcularPosible(kgPrecintoB,kgItemUnitario,proporcionCantidad,merma);


            if(cantidadPosibleP1 == proporcionCantidad){
                piezas1ok = true;
            }
            if(cantidadPosibleP2 == proporcionCantidad){
                piezas2ok = true;
            }


            //SI ES INPAR
            if(!esPar){
                if(piezas1ok && piezas2ok){
                    cantidadPosibleP1 = calcularPosible(kgPrecintoA, kgItemUnitario, (proporcionCantidad +1),merma);

                    if(cantidadPosibleP1 != proporcionCantidad +1){
                        cantidadPosibleP2 = calcularPosible(kgPrecintoB, kgItemUnitario, (proporcionCantidad +1),merma);

                    }
                }
                else{
                    if(piezas1ok && !piezas2ok){

                        cantidadPosibleP1 = calcularPosible(kgPrecintoA, kgItemUnitario, (cantItemPendientes) +1 - cantidadPosibleP2,merma );

                    }
                    else{
                        cantidadPosibleP2 = calcularPosible(kgPrecintoB, kgItemUnitario, (cantItemPendientes) +1 - cantidadPosibleP1,merma );

                    }
                }
            }
            else{
                if(piezas1ok && piezas2ok){
                    cantidadPosibleP1 = calcularPosible(kgPrecintoA, kgItemUnitario, proporcionCantidad,merma );

                    if(cantidadPosibleP1 != proporcionCantidad +1){
                        cantidadPosibleP2 = calcularPosible(kgPrecintoB, kgItemUnitario, proporcionCantidad,merma );

                    }
                }
                else{
                    if(piezas1ok && !piezas2ok){

                        cantidadPosibleP1 = calcularPosible(kgPrecintoA, kgItemUnitario, (cantItemPendientes) - cantidadPosibleP2,merma );

                    }
                    else{
                        cantidadPosibleP2 = calcularPosible(kgPrecintoB, kgItemUnitario, (cantItemPendientes) - cantidadPosibleP1,merma );

                    }
                }
            }


            resultado.add(cantidadPosibleP1);
            resultado.add(cantidadPosibleP2);
            if(cantidadPosibleP1 == 0 && cantidadPosibleP2 == 0){
                resultado = null;
            }

        }


        return resultado;
    }


    public static String extraerLote(String codigoDeBarras){
        if(codigoDeBarras.length() == 24){
            return codigoDeBarras.substring(0,10);
        }
        else{
            if(codigoDeBarras.length() == 48){
                return codigoDeBarras.substring(28,38);
            }
            else{
                return null;
            }
        }
    }

    public static String extraerMaterial(String codigoDeBarras){
        if(codigoDeBarras.length() == 24){
            return codigoDeBarras.substring(10,20);
        }
        else{
            if(codigoDeBarras.length() == 48){
                return codigoDeBarras.substring(6,15);
            }
            else{
                return null;
            }
        }
    }
    public static String extraerPeso(String codigoDeBarras){
        if(codigoDeBarras.length() == 24){
            return codigoDeBarras.substring(20);
        }
        else{
            if(codigoDeBarras.length() == 48){
                return codigoDeBarras.substring(20,26);
            }
            else{
                return null;
            }
        }
    }

}


