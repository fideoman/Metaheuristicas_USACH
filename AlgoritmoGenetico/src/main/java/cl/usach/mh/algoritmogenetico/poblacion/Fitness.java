package cl.usach.mh.algoritmogenetico.poblacion;

import java.io.IOException;
import java.net.URISyntaxException;

import cl.usach.mh.algoritmogenetico.AlgoritmoGenetico;
import cl.usach.mh.algoritmogenetico.Individuo;
import cl.usach.mh.busquedatabu.BusquedaTabu;

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
		// i1.getGenotipo() es la soluci�n propuesta de entrada
		// li.fitness es la salida esperada (costo)
		
		int[] solucionInicial = i1.getGenotipo();
		
        int costo = 0;
		for (int i = 0; i < solucionInicial.length; i++) {
			for (int j = 0; j < AlgoritmoGenetico.locales.size(); j++) {
				if (String.valueOf(solucionInicial[i]).equals(AlgoritmoGenetico.locales.get(j).etiqueta)) {
					int m = 0;
					for (int l = 0; l < solucionInicial.length; l++) {
						for (int o = 0; o < AlgoritmoGenetico.locales.get(j).conjuntoComplemento.length; o++) {
							if (solucionInicial[l] == AlgoritmoGenetico.locales.get(j).conjuntoComplemento[o]) {
								costo = costo
										+ AlgoritmoGenetico.locales.get(j).flujos[o]
										* AlgoritmoGenetico.localidades.get(i).distancias[m];
								m++;
							}
						}
					}
				}
			}
		}	
		// 4) Costo calculado.
		i1.setFitness(costo);
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
		BusquedaTabu.ejecucion(AlgoritmoGenetico.locales, AlgoritmoGenetico.localidades, tenor, i1.getGenotipo(), numeroCiclosTotales, false, limiteIntercambios, false, 50, false, 10, 10);
		i1.setGenotipo(BusquedaTabu.mejorSolucionHistorica);
		calculoFitness(i1);
	}	
}
