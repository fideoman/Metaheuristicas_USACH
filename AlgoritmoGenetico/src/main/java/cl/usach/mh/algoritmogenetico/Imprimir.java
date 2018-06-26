package cl.usach.mh.algoritmogenetico;

import java.util.ArrayList;

/**
 * Clase para mostrar por pantalla las poblaciones generadas.
 * @author Jose Manuel Rosell Sanchez
 * Adaptado y mejorado por Isaac Silva
 */
public class Imprimir {
	
	/**
	 * Imprime la poblacion completa. Imprime el genotipo y el fitness de todos
	 * los indiviudos de la poblacion.
	 *
	 * @param poblacion la poblacion actual
	 */
	public static void imprimirPoblacion(ArrayList<Individuo> poblacion){
		for(int i=0; i<poblacion.size();i++){
			for(int j=0; j<poblacion.get(i).getGenotipo().length;j++){
				System.out.print(poblacion.get(i).getGenotipo()[j]+" ");
			}
			System.out.println("\n"+poblacion.get(i).getFitness()+"\n");
		}
	}
	
	/**
	 * Busca la mejor solucion, es decir, la que tiene el menor fitness
	 * dentro de toda la poblacion. Imprime el genotipo y el fitness del
	 * mejor individuo de la poblacion.
	 *
	 * @param poblacion la poblacion actual
	 */
	public static void mejorSolucion(ArrayList<Individuo> poblacion){
		Individuo min_fitness;
		min_fitness = poblacion.get(0);
		for(int ii=0; ii<poblacion.size();ii++){
			Individuo aux = poblacion.get(ii);
			if(aux.getFitness()<min_fitness.getFitness()){
				min_fitness = aux;
			}
		}
		
		for(int j=0; j<min_fitness.getGenotipo().length; j++){
			System.out.print(min_fitness.getGenotipo()[j]+" ");
		}
		System.out.println("\n"+min_fitness.getFitness());
	}
}
