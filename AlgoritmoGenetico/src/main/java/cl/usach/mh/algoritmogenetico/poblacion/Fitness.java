package cl.usach.mh.algoritmogenetico.poblacion;

import cl.usach.mh.algoritmogenetico.AlgoritmoGenetico;
import cl.usach.mh.algoritmogenetico.Individuo;

/**
 * Clase para obtener el fitness de un cromosoma (o solucion).
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
	public static void fitnessCromosoma(Individuo i1){
		// i1.getCromosoma() es la solución propuesta de entrada
		// li.fitness es la salida esperada (costo)
		
		int[] solucionInicial = i1.getCromosoma();
		
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
	 * Calcular el fitness de una solucion obtienen el mejor fitness para el un cromosoma dado, utilizando para ello
	 * un algoritmo Goloso. Se calcula el mejor fitness pero no se modifica el cromosoma del individuo, unicamente se
	 * modifica el fitness.
	 *
	 * @param i1 el individuo del cual obtener el fitness
	 * @param distancias las distancias entre las fabricas
	 * @param pesos los pesos entre las fabricas
	 */
	public static void fitnessGoloso(Individuo i1){
		fitnessCromosoma(i1);

		Individuo aux = new Individuo();
		aux = HeuristicaGolosa.mejorSolucion(i1);
		i1.setFitness(aux.getFitness());
	}
	
	/**
	 * Calcular el fitness de una solucion obtienen el mejor fitness para el un cromosoma dado, utilizando para ello
	 * un algoritmo Goloso. Se calcula el mejor fitness y se modifica el cromosoma del individuo, ademas de 
	 * modificar el fitness del individuo.
	 *
	 * @param i1 el individuo del cual obtener el fitness
	 * @param distancias las distancias entre las fabricas
	 * @param pesos los pesos entre las fabricas
	 */
	public static void fitnessGolosoModifica(Individuo i1){
		fitnessCromosoma(i1);

		Individuo aux = new Individuo();
		aux = HeuristicaGolosa.mejorSolucion(i1);
		
		i1.setFitness(aux.getFitness());
		i1.setCromosoma(aux.getCromosoma());
	}
}
