package cl.usach.mh.algoritmogenetico;

import com.rits.cloning.Cloner;

import cl.usach.mh.comunes.utilidades.representacionordinal.Operaciones;

/**
 * Representacion de las soluciones al problema.
 * @author Jose Manuel Rosell Sanchez
 * Adaptado y mejorado por Isaac Silva
 */
public class Individuo {
	
	/** El fitness de la solucion. */
	private int fitness = 0;
	
	/** La disposicion de las fabricas / locales. */
	private int[] genotipo;		
	
	public Individuo(int[] gen) {
		genotipo = gen;
	}
	
	public void intercambioAleatorioInterno() {
		genotipo = Operaciones.intercambioAlAzar(genotipo);
	}
	
	public void intercambioFijo(int[] par) {
		genotipo = Operaciones.intercambio(genotipo, par[0], par[1]);
	}
	
	/**
	 * Contruye un individuo inicial generado aleatoriamente.
	 *
	 * @param size tamao del genotipo (num. de fabricas)
	 */
	public Individuo(int largo){
		genotipo = Operaciones.solucionAlAzar(largo);
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
