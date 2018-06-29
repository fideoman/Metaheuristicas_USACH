package cl.usach.mh.algoritmogenetico.poblacion;

import java.io.IOException;
import java.net.URISyntaxException;

import cl.usach.mh.algoritmogenetico.Individuo;
import cl.usach.mh.busquedatabu.BusquedaTabu;
import cl.usach.mh.comunes.qap.QAP;

/**
 * Clase para obtener el fitness de un genotipo (o solucion).
 * @author Jose Manuel Rosell Sanchez
 * Adaptado y mejorado por Isaac Silva
 */
public class Fitness {
	
	/**
	 * Obtiene el fitness de una solucion del problema.
	 *
	 * @param i1 el individuo del cual obtener el fitness
	 * @param distancias las distancias entre las fabricas
	 * @param pesos los pesos entre las fabricas
	 */
	public static void calculoFitness(Individuo i1){
		// i1.getGenotipo() es la solucin propuesta de entrada
		// li.fitness es la salida esperada (costo)
		
		int[] solucionInicial = i1.getGenotipo();

		// 4) Costo calculado.
		i1.setFitness(QAP.calculoCosto(solucionInicial));
	}
	
	/**
	 * Calcular el fitness de una solucion obtienen el mejor fitness para el un genotipo dado, utilizando para ello
	 * una Búsqueda Tabú relajada, para no sobrecargar la rutina
	 *
	 * @param i1 el individuo del cual obtener el fitness
	 * @param distancias las distancias entre las fabricas
	 * @param pesos los pesos entre las fabricas
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public static void calculoFitnessHibrido(Individuo i1, int tenor, int numeroCiclosTotales, int limiteIntercambios) throws IOException, URISyntaxException{
		BusquedaTabu.ejecucion(tenor, i1.getGenotipo(), numeroCiclosTotales, false, limiteIntercambios, false, 50, false, 10, 10);
		i1.setGenotipo(BusquedaTabu.mejorSolucionHistorica);
		calculoFitness(i1);
	}	
}
