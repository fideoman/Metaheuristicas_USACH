package cl.usach.mh.algoritmogenetico.poblacion;

import java.util.ArrayList;

import cl.usach.mh.algoritmogenetico.Individuos;

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
	public static Individuos mejorSolucion(Individuos individuo, ArrayList<ArrayList<Integer>> distancias, ArrayList<ArrayList<Integer>> pesos){
		Individuos resultado = new Individuos();
		Individuos mejor = new Individuos();

		resultado.setFitness(individuo.getFitness());
		resultado.setCromosoma(individuo.getCromosoma());

		mejor.setFitness(individuo.getFitness());
		mejor.setCromosoma(individuo.getCromosoma());

		for(int i=0; i<resultado.getCromosoma().size(); i++){
			for(int j=i+1; j<resultado.getCromosoma().size(); j++){
				//Intercambiamos el alelo i por el alelo j
				int valor1 = resultado.getCromosoma().get(i);
				int valor2 = resultado.getCromosoma().get(j);
				
				resultado.getCromosoma().set(i, valor2);
				resultado.getCromosoma().set(j, valor1);
				
				//calculamos el nuevo fitness
				Fitness.fitnessCromosoma(resultado, distancias, pesos);

				if(resultado.getFitness()<mejor.getFitness()){
					mejor.setCromosoma(resultado.getCromosoma());
					mejor.setFitness(resultado.getFitness());
				}
			}
		}

		return mejor;
	}
}
