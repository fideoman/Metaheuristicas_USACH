package cl.usach.mh.algoritmogenetico;

import java.util.ArrayList;
import java.util.Random;

/**
 * Representacion de las soluciones al problema.
 * @author Jose Manuel Rosell Sanchez
 * Adaptado por Isaac Silva
 */
public class Individuos {
	
	/** El fitness de la solucion. */
	private int fitness;
	
	/** La disposicion de las fabricas. */
	private ArrayList<Integer> cromosoma;
	
	/**
	 * Constructor estandar.
	 */
	public Individuos(){
		fitness = 0;
		cromosoma = new ArrayList<Integer>();
	}
	
	/**
	 * Contruye un individuo inicial generado aleatoriamente.
	 *
	 * @param size tamaño del cromosoma (num. de fabricas)
	 */
	public Individuos(int size){
		fitness = 0;
		cromosoma = new ArrayList<Integer>();
		
		Random rnd = new Random();

		for(int i=0; i<size; i++){
			int number = rnd.nextInt(((size-1) - 0) + 1) + 0;
			while(cromosoma.contains(number)){
				number = rnd.nextInt(((size-1) - 0) + 1) + 0;
			}
			cromosoma.add(number);
		}
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
	public ArrayList<Integer> getCromosoma() {
		return cromosoma;
	}

	/**
	 * Establece el cromosoma.
	 *
	 * @param cromosoma el nuevo cromosoma cromosoma
	 */
	@SuppressWarnings("unchecked")
	public void setCromosoma(ArrayList<Integer> cromosoma) {
		this.cromosoma = (ArrayList<Integer>) cromosoma.clone();
	}
	
	/**
	 * Compara dos individuos. Devuelve true si son iguales; false en otro caso.
	 *
	 * @param individuo individuo con el que comparar
	 * @return true, si son iguales; false, si no lo son
	 */
	public boolean compareTo(Individuos individuo){
		for(int i=0; i<individuo.getCromosoma().size(); i++){
			if(this.getCromosoma().get(i) != individuo.getCromosoma().get(i)){
				return false;
			}
		}
		
		if((this.getFitness() != individuo.getFitness())){
			return false;
		}
		
		return true;
	}
}
