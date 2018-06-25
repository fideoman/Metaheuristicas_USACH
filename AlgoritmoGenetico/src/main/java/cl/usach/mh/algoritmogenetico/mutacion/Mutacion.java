package cl.usach.mh.algoritmogenetico.mutacion;

import java.util.ArrayList;
import java.util.Random;

import cl.usach.mh.algoritmogenetico.AlgoritmoGenetico;
import cl.usach.mh.algoritmogenetico.Individuo;
import cl.usach.mh.algoritmogenetico.poblacion.Fitness;

/**
 * Clase para realizar la mutacion de la poblacion.
 * @author Jose Manuel Rosell Sanchez
 * Adaptado y mejorado por Isaac Silva
 */
public class Mutacion {
	
	/**
	 * Realiza la mutacion de un individuo. Cambia una posicion del cromosoma
	 * por otra. Ambas posiciones son elegidas aleatoriamente.
	 *
	 * @param ind individuo a mutar
	 * @param cantidad tamaño del cromosoma (o solucion)
	 * @return el individuo mutado
	 */
	public static Individuo mutacion(Individuo ind, int cantidad){
		Random rnd = new Random();
		
		int posicion1 = rnd.nextInt(((cantidad-1) - 0) + 1) + 0;
		int posicion2 = rnd.nextInt(((cantidad-1) - 0) + 1) + 0;
		while(posicion1==posicion2) {
			posicion2 = rnd.nextInt(((cantidad-1) - 0) + 1) + 0;
		}

		int valor1 = ind.getCromosoma()[posicion1];
		int valor2 = ind.getCromosoma()[posicion2];
		
		ind.getCromosoma()[posicion1] = valor2;
		ind.getCromosoma()[posicion2] = valor1;
		
		return ind;
	}
	
	/**
	 * Realiza la mutacion a una poblacion completa.
	 *
	 * @param poblacion la poblacion actual
	 * @param distancias las distancias entre fabricas
	 * @param pesos los pesos entre las fabricas
	 * @param cantidad el tamaño de la solucion
	 * @return la nueva poblacion mutada
	 */
	public static ArrayList<Individuo> mutarPoblacion(ArrayList<Individuo> poblacion){
		ArrayList<Individuo> nueva_poblacion = new ArrayList<Individuo>();
		
		for(int i=0; i<poblacion.size(); i++){
			Individuo aux = new Individuo();
			
			aux = mutacion(poblacion.get(i), AlgoritmoGenetico.largoCromosoma);
			
			//Calculamos el fitness del nuevo cromosoma
			Fitness.fitnessCromosoma(aux);
			//Añadimos el nuevo individuo a la poblacion
			nueva_poblacion.add(aux);
		}
		
		return nueva_poblacion;
	}
}
