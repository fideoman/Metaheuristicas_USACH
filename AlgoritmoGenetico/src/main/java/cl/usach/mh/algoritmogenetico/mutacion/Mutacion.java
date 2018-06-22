package cl.usach.mh.algoritmogenetico.mutacion;

import java.util.ArrayList;
import java.util.Random;

import cl.usach.mh.algoritmogenetico.Individuos;
import cl.usach.mh.algoritmogenetico.poblacion.Fitness;

/**
 * Clase para realizar la mutacion de la poblacion.
 * @author Jose Manuel Rosell Sanchez
 * Adaptado por Isaac Silva
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
	public static Individuos mutacion(Individuos ind, int cantidad){
		Random rnd = new Random();
		
		int posicion1 = rnd.nextInt(((cantidad-1) - 0) + 1) + 0;
		int posicion2 = rnd.nextInt(((cantidad-1) - 0) + 1) + 0;

		int valor1 = ind.getCromosoma().get(posicion1);
		int valor2 = ind.getCromosoma().get(posicion2);
		
		ind.getCromosoma().set(posicion1, valor2);
		ind.getCromosoma().set(posicion2, valor1);
		
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
	public static ArrayList<Individuos> mutarPoblacion(ArrayList<Individuos> poblacion, ArrayList<ArrayList<Integer>> distancias,
												ArrayList<ArrayList<Integer>> pesos, int cantidad){
		ArrayList<Individuos> nueva_poblacion = new ArrayList<Individuos>();
		
		for(int i=0; i<poblacion.size(); i++){
			Individuos aux = new Individuos();
			
			aux = mutacion(poblacion.get(i), cantidad);
			
			//Calculamos el fitness del nuevo cromosoma
			Fitness.fitnessCromosoma(aux, distancias, pesos);
			//Añadimos el nuevo individuo a la poblacion
			nueva_poblacion.add(aux);
		}
		
		return nueva_poblacion;
	}
}
