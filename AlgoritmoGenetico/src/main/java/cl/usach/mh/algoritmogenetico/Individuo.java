package cl.usach.mh.algoritmogenetico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.rits.cloning.Cloner;

/**
 * Representacion de las soluciones al problema.
 * @author Jose Manuel Rosell Sanchez
 * Adaptado y mejorado por Isaac Silva
 */
public class Individuo {
	
	/** El fitness de la solucion. */
	private int fitness;
	
	/** La disposicion de las fabricas / locales. */
	private int[] genotipo;
		
	public Individuo() {
		fitness = 0;
	}
	
	public Individuo(int[] gen) {
		genotipo = gen;
	}
	
	/**
	 * Contruye un individuo inicial generado aleatoriamente.
	 *
	 * @param size tama�o del genotipo (num. de fabricas)
	 */
	public Individuo(int largo){
		fitness = 0;
		List<Integer> alAzar = IntStream.range(1, largo+1).boxed().collect(Collectors.toCollection(ArrayList::new));
		Collections.shuffle(alAzar);
		genotipo = alAzar.stream().mapToInt(Integer::valueOf).toArray();
	}
	
	/**
	 * Compara dos individuos. Devuelve true si son iguales; false en otro caso.
	 *
	 * @param individuo individuo con el que comparar
	 * @return true, si son iguales; false, si no lo son
	 */
	public boolean compareTo(Individuo individuo){	
		
		if(Arrays.equals(genotipo, individuo.genotipo)){
			return true;
		}
		
		return false;
	}
	
	/**
	 * Obtiene el fitness.
	 *
	 * @return el fitness
	 */
	public int getFitness() {
		return fitness;
	}

	/**
	 * Establece el fitness.
	 *
	 * @param fitnes el nuevo fitnes
	 */
	public void setFitness(int fitness) {
		this.fitness = fitness;
	}

	/**
	 * Obtiene el genotipo.
	 *
	 * @return el genotipo
	 */
	public int[] getGenotipo() {
		return genotipo;
	}

	/**
	 * Establece el genotipo.
	 *
	 * @param genotipo el nuevo genotipo genotipo
	 */
	public void setGenotipo(int[] genotipo) {
		this.genotipo = (new Cloner()).deepClone(genotipo);
	}
}
