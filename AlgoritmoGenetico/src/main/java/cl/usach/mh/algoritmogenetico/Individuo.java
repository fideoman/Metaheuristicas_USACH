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
	private int[] cromosoma;
		
	public Individuo() {
		fitness = 0;
	}
	
	/**
	 * Contruye un individuo inicial generado aleatoriamente.
	 *
	 * @param size tamaño del cromosoma (num. de fabricas)
	 */
	public Individuo(int largo){
		fitness = 0;
		List<Integer> alAzar = IntStream.range(1, largo+1).boxed().collect(Collectors.toCollection(ArrayList::new));
		Collections.shuffle(alAzar);
		cromosoma = alAzar.stream().mapToInt(Integer::valueOf).toArray();
	}
	
	/**
	 * Compara dos individuos. Devuelve true si son iguales; false en otro caso.
	 *
	 * @param individuo individuo con el que comparar
	 * @return true, si son iguales; false, si no lo son
	 */
	public boolean compareTo(Individuo individuo){	
		
		if(Arrays.equals(cromosoma, individuo.cromosoma)){
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
	 * Obtiene el cromosoma.
	 *
	 * @return el cromosoma
	 */
	public int[] getCromosoma() {
		return cromosoma;
	}

	/**
	 * Establece el cromosoma.
	 *
	 * @param cromosoma el nuevo cromosoma cromosoma
	 */
	public void setCromosoma(int[] cromosoma) {
		this.cromosoma = (new Cloner()).deepClone(cromosoma);
	}
}
