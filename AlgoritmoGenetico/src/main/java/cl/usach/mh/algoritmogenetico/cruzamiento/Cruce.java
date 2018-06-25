package cl.usach.mh.algoritmogenetico.cruzamiento;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

import com.rits.cloning.Cloner;

import cl.usach.mh.algoritmogenetico.AlgoritmoGenetico;
import cl.usach.mh.algoritmogenetico.Individuo;
import cl.usach.mh.algoritmogenetico.poblacion.Fitness;

/**
 * Clase para realizar el cruce de dos cromosomas.
 * @author Jose Manuel Rosell Sanchez
 * Adaptado y mejorado por Isaac Silva
 */
public class Cruce {
		
	/**
	 * Cruce de dos individuos, utilizando el cruce en un punto. Se dividen los dos padres a partir de ese
	 * punto de cruce (que se obtiene aleatoriamente) y se mezclan las dos partes de los padres. Puede que
	 * la solución obtenida no sea correcta por lo que hay que arreglarla para que no se repitan las fabricas / locales. 
	 *
	 * @param poblacion poblacion inicial
	 * @param distancias las distancias entre las fabricas
	 * @param pesos los pesos entre las fabricas
	 * @param cantidad la longitud de la solucion (longitud del cromosoma)
	 * @return la nueva poblacion despues de realizar los cruces
	 */
	public static ArrayList<Individuo> cruzamientoEnUnPunto(ArrayList<Individuo> grupoPadres, int tipo){
		ArrayList<Individuo> nueva_poblacion = new ArrayList<Individuo>();			
				
		for(int iteracion = 0; iteracion < grupoPadres.size(); iteracion++){			
			int[] parUnico = AlgoritmoGenetico.todasCombinacionesUnicas.get((new Random()).nextInt(AlgoritmoGenetico.todasCombinacionesUnicas.size()));			
			
			int c1 = parUnico[0];
			int c2 = parUnico[1];
			// Genero dos números distintos dentro del rango del número de poblaciones, al azar
			
			Individuo aux1 = grupoPadres.get(c1);
			Individuo aux2 = grupoPadres.get(c2);
			// Selecciono dos padres al azar
			
			// Obtengo el punto de cruce, al azar
			int pos = (new Random()).nextInt(((AlgoritmoGenetico.largoCromosoma-2) - 1) + 1) + 1;
			
			// Obtengo el genotipo de los padres
			int[] padre1 = aux1.getCromosoma();
			int[] padre2 = aux2.getCromosoma();
						
			// Obtengo un pseudo-clon del padre 1 y del 2, el cuales serán la base de los verdaderos hijos de salida
			int[] hijo1 = (new Cloner()).deepClone(aux1.getCromosoma());
			int[] hijo2 = (new Cloner()).deepClone(aux2.getCromosoma());
			
			// Los hijos serán modificados en el mismo corte con código del otro padre
			for (int i = pos; i < hijo1.length; i++) hijo1[i] = -1;
			for (int i = pos; i < hijo2.length; i++) hijo2[i] = -1;
			
			// Reproducción que genera hijo 1			
			for (int i = pos; i < hijo1.length; i++) {
				for (int j = 0; j < padre2.length; j++) {
					if(!Arrays.stream(hijo1).boxed().collect(Collectors.toList()).contains(padre2[j])) {
						hijo1[i] = padre2[j];
						break;
					}
				}
			}			
			// Reproducción que genera hijo 2			
			for (int i = pos; i < hijo2.length; i++) {
				for (int j = 0; j < padre1.length; j++) {
					if(!Arrays.stream(hijo2).boxed().collect(Collectors.toList()).contains(padre1[j])) {
						hijo2[i] = padre1[j];
						break;
					}
				}
			}			
			aux1.setCromosoma(hijo1);
			aux2.setCromosoma(hijo2);
			//Calculamos el fitness del nuevo cromosoma, según el método pasado por parámetro
			
			switch(tipo) {
			case 1:
				Fitness.fitnessCromosoma(aux1);
				Fitness.fitnessCromosoma(aux2);
				break;
			case 2:
				Fitness.fitnessGoloso(aux1);
				Fitness.fitnessGoloso(aux2);
				break;
			case 3: 
				Fitness.fitnessGolosoModifica(aux1);
				Fitness.fitnessGolosoModifica(aux2);
				break;			
			}			
			
			//Añadimos los dos nuevos individuos a la poblacion
			nueva_poblacion.add(aux1);
			nueva_poblacion.add(aux2);			
		}		
		return nueva_poblacion;
	}
}
