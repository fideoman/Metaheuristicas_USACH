package cl.usach.mh.algoritmogenetico.cruzamiento;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

import com.rits.cloning.Cloner;

import cl.usach.mh.algoritmogenetico.AlgoritmoGenetico;
import cl.usach.mh.algoritmogenetico.Individuo;
import cl.usach.mh.algoritmogenetico.poblacion.Fitness;

/**
 * Clase para realizar el cruce de dos genotipos.
 * 
 * @author Jose Manuel Rosell Sanchez Adaptado y mejorado por Isaac Silva
 */
public class Cruce {

	/**
	 * Cruce de dos individuos, utilizando el cruce en un punto. Se dividen los dos
	 * padres a partir de ese punto de cruce (que se obtiene aleatoriamente) y se
	 * mezclan las dos partes de los padres. Puede que la soluci�n obtenida no sea
	 * correcta por lo que hay que arreglarla para que no se repitan las fabricas /
	 * locales.
	 *
	 * @param poblacion
	 *            poblacion inicial
	 * @param distancias
	 *            las distancias entre las fabricas
	 * @param pesos
	 *            los pesos entre las fabricas
	 * @param cantidad
	 *            la longitud de la solucion (longitud del genotipo)
	 * @return la nueva poblacion despues de realizar los cruces
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static ArrayList<Individuo> cruzamientoEnUnPunto(ArrayList<Individuo> grupoPadres, boolean esHibrido, int tenor, int numeroCiclosTotales, int limiteIntercambios)
			throws IOException, URISyntaxException {
		ArrayList<Individuo> nueva_poblacion = new ArrayList<Individuo>();

		for (int iteracion = 0; iteracion < grupoPadres.size(); iteracion++) {
			int[] parUnico = AlgoritmoGenetico.todasCombinacionesUnicas
					.get((new Random()).nextInt(AlgoritmoGenetico.todasCombinacionesUnicas.size()));

			// Genero dos n�meros distintos dentro del rango del n�mero de poblaciones, al
			// azar
			int c1 = parUnico[0];
			int c2 = parUnico[1];			

			// Selecciono dos padres al azar y los clono
			Individuo padre1 = grupoPadres.get(c1);
			Individuo padre2 = grupoPadres.get(c2);			

			int[] genotipoPadre2 = (new Cloner()).deepClone(padre2.getGenotipo());
			ArrayList<Integer> genotipoHijoArray = (ArrayList<Integer>) Arrays.stream((new Cloner()).deepClone(padre1.getGenotipo())).boxed().collect(Collectors.toList());

			// Obtengo el punto de cruce, al azar
			int pos = (new Random()).nextInt(((AlgoritmoGenetico.largoGenotipo - 2) - 1) + 1) + 1;

			for (int i = pos; i <= genotipoHijoArray.size(); i++) {
				for(int j = 0; j < genotipoPadre2.length; j++) {
					if(!genotipoHijoArray.subList(0, i).contains(genotipoPadre2[j])) {
						genotipoHijoArray.set(i, genotipoPadre2[j]);
						break;
					}
				}
			}
			
			Individuo hijo = new Individuo(genotipoHijoArray.stream().mapToInt(Integer::valueOf).toArray());

			// Calculamos el fitness del nuevo genotipo, seg�n el m�todo pasado por
			// par�metro

			if (!esHibrido) {
				Fitness.calculoFitness(hijo);
			} else {
				Fitness.calculoFitnessHibrido(hijo, tenor, numeroCiclosTotales, limiteIntercambios);
			}

			// A�adimos los dos nuevos individuos a la poblacion
			nueva_poblacion.add(hijo);
		}
		return nueva_poblacion;
	}
}
