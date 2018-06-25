package cl.usach.mh.algoritmogenetico.poblacion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.numbers.combinatorics.Combinations;

import cl.usach.mh.algoritmogenetico.Individuo;

/**
 * Algoritmo Goloso para realizar busquedas locales a partir de un individuo.
 */
public class HeuristicaGolosa {
	
	/**
	 * Busca la mejor solucion a partir de un individuo de la poblacion.
	 *
	 * @param individuo el individuo inicial
	 * @param distancias distancias entre las fabricas
	 * @param pesos los pesos entre las fabricas
	 * @return el nuevo individuo
	 */
	public static Individuo mejorSolucion(Individuo individuo){
		Individuo resultado = new Individuo();
		Individuo mejor = new Individuo();

		// El primer individuo es el primer resultado y el mejor
		resultado.setFitness(individuo.getFitness());
		resultado.setCromosoma(individuo.getCromosoma());

		mejor.setFitness(individuo.getFitness());
		mejor.setCromosoma(individuo.getCromosoma());
		
		// Obtengamos todos los pares únicos
		ArrayList<int[]> parTemp = new ArrayList<int[]>(); 
		(new Combinations(individuo.getCromosoma().length,2)).iterator().forEachRemaining(parTemp::add);
		int [][] pares = parTemp.stream().toArray(int[][]::new);
		for(int i = 0; i < pares.length; i++) for(int j = 0; j < pares[i].length;j++) pares[i][j]++; 
		parTemp = null;

		for(int[] par : pares) {
			List<Integer> copiaSolucionActual = Arrays.stream(individuo.getCromosoma()).boxed().collect(Collectors.toList());
			// 5a) Hago el intercambio de alelos
			Collections.swap(copiaSolucionActual, copiaSolucionActual.indexOf(par[0]), copiaSolucionActual.indexOf(par[1]));
			resultado.setCromosoma(copiaSolucionActual.stream().mapToInt(Integer::valueOf).toArray());
			//calculamos el nuevo fitness
			Fitness.fitnessCromosoma(resultado);
			if(resultado.getFitness()<mejor.getFitness()){
				mejor.setCromosoma(resultado.getCromosoma());
				mejor.setFitness(resultado.getFitness());
			}
		}
		
		return mejor;
	}
}
