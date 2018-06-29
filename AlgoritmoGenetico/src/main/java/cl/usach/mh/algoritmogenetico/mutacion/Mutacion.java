package cl.usach.mh.algoritmogenetico.mutacion;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import cl.usach.mh.algoritmogenetico.Individuo;
import cl.usach.mh.algoritmogenetico.poblacion.Fitness;

/**
 * Clase para realizar la mutacion de la poblacion.
 * @author Jose Manuel Rosell Sanchez
 * Adaptado y mejorado por Isaac Silva
 */
public class Mutacion {	
	/**
	 * Realiza la mutacion a una poblacion completa, con intercambio de alelos aleatorios.
	 *
	 * @param poblacion la poblacion actual
	 * @param distancias las distancias entre fabricas
	 * @param pesos los pesos entre las fabricas
	 * @param cantidad el tamao de la solucion
	 * @return la nueva poblacion mutada
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public static ArrayList<Individuo> mutarPoblacion(ArrayList<Individuo> poblacion, boolean esHibrido, int tenor, int numeroCiclosTotales, int limiteIntercambios) throws IOException, URISyntaxException{
		ArrayList<Individuo> nueva_poblacion = new ArrayList<Individuo>();
		
		for(int i=0; i<poblacion.size(); i++){
			// TODO: Justo en este punto, se puede aplicar otro tipo de mutacion.
			Individuo aux = mutacionAleatoria(poblacion.get(i));
			
			//Calculamos el fitness del nuevo genotipo
			if(!esHibrido) {
				Fitness.calculoFitness(aux);
			} else {
				Fitness.calculoFitnessHibrido(aux, tenor, numeroCiclosTotales, limiteIntercambios);
			}
			
			//Aadimos el nuevo individuo a la poblacion
			nueva_poblacion.add(aux);
		}
		
		return nueva_poblacion;
	}
	
	/**
	 * Realiza la mutacion de un individuo. Cambia una posicion del genotipo
	 * por otra. Ambas posiciones son elegidas aleatoriamente.
	 *
	 * @param ind individuo a mutar
	 * @param cantidad tamao del genotipo (o solucion)
	 * @return el individuo mutado
	 */
	public static Individuo mutacionAleatoria(Individuo ind){
		ind.intercambioAleatorioInterno();		
		return ind;
	}
}
