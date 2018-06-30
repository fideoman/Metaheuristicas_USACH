package cl.usach.mh.templamientosimulado;

import java.util.ArrayList;
import java.util.Arrays;

import cl.usach.mh.comunes.qap.QAP;
import cl.usach.mh.comunes.utilidades.representacionordinal.Operaciones;

public class TemplamientoSimulado {
	public static int[] mejorSolucionHistorica;
	public static int[] mejorSolucionActual;
	public static ArrayList<int[]> solucionesEncontradas = new ArrayList<int[]>();
	public static ArrayList<int[]> mejoresSolucionesHistoricas = new ArrayList<int[]>();
		
	public static void ejecucion(double temperaturaInicial, int porcentajeTasaDescenso, int[] solucionInicialExt, int numeroCiclosExt) {
		
		// Parametros del Templamiento Simulado
		double temperatura = temperaturaInicial; // Temperatura Inicial
		int porcTasaDescenso = porcentajeTasaDescenso; // Porcentaje de tasa de descenso, en entero
		int[] solucionInicial = solucionInicialExt; // Solucion Inicial
		int maximoEpocas = numeroCiclosExt; // Epocas - Ciclos de busqueda
		// Fin de parametros
		
		// Guardemos nuestras soluciones. Guardaremos: Soluciones encontradas, y mejor Solucion historica en X iteracion
		solucionesEncontradas = new ArrayList<int[]>();
		mejoresSolucionesHistoricas = new ArrayList<int[]>();
		
		// La solucion inicial es la mejor Solucion historica y actual al comenzar. Es la unica que conocemos.
		mejorSolucionHistorica = solucionInicial;
		mejorSolucionActual = solucionInicial;
		
		// Contador de epoca
		int epoca = 1;
		while (temperatura > 1 || epoca <= maximoEpocas) { // Si la temperatura es menor que uno, o llegamos a un tope
															// de epocas, termina el calculo.			
			int[] solucionPropuesta = Operaciones.solucionAlAzar(QAP.getCantidad()); // Al azar
			while (Arrays.equals(solucionPropuesta, mejorSolucionActual)) { // Deben ser diferentes
				solucionPropuesta = Operaciones.solucionAlAzar(QAP.getCantidad());
			}
			// Si la solucion propuesta es mejor que la solucion actual, la acepto.
			if (QAP.calculoCosto(solucionPropuesta) < QAP.calculoCosto(mejorSolucionActual)) {
				mejorSolucionActual = solucionPropuesta;
			} else { // Si no lo es, aun hay una posibilidad: La probabilidad de Boltzmann
				// Si la probabilidad de Boltzmann es mejor que una probabilidad al azar, la
				// acepto
				if (probabilidadDeAceptacion(QAP.calculoCosto(mejorSolucionActual),
						QAP.calculoCosto(solucionPropuesta), temperatura) > Math.random()) {
					mejorSolucionActual = solucionPropuesta;
				}
			}
			// Guardamos la mejor solucion historica
			if (QAP.calculoCosto(mejorSolucionActual) < QAP.calculoCosto(mejorSolucionHistorica)) {
				mejorSolucionHistorica = mejorSolucionActual;
			}
			// Almacenamos todas las soluciones
			solucionesEncontradas.add(mejorSolucionActual);
			mejoresSolucionesHistoricas.add(mejorSolucionHistorica);
			
			// Fin de epoca. Enfriamos el sistema geometricamente, y vamos por la siguiente
			// epoca
			temperatura *= ((100.0 - (double) porcTasaDescenso) / 100.0);
			epoca++;
		}		
	}
	
    public static double probabilidadDeAceptacion(int energiaAnterior, int nuevaEnergia, double temperatura) {
        return Math.exp((energiaAnterior - nuevaEnergia) / temperatura);
    }
}
