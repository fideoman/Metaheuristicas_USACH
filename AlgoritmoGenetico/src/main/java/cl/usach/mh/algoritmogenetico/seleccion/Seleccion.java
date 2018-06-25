package cl.usach.mh.algoritmogenetico.seleccion;

import java.util.ArrayList;
import java.util.Random;

import cl.usach.mh.algoritmogenetico.AlgoritmoGenetico;
import cl.usach.mh.algoritmogenetico.Individuo;

/**
 * Seleccion de los individuos de la poblacion.
 * @author Jose Manuel Rosell Sanchez
 * Adaptado y mejorado por Isaac Silva
 */
public class Seleccion {
	
	/**
	 * Seleccion por torneo: Se eligen dos individuos de la poblacion aleatoriamente
	 * y se realiza un torneo entre los dos, el individuo que mas fitness tenga pasa 
	 * a la nueva poblacion.
	 *
	 * @param poblacion poblacion actual
	 * @return la nueva poblacion
	 */
	public static ArrayList<Individuo> porTorneo(ArrayList<Individuo> poblacion){
		ArrayList<Individuo> nueva_poblacion = new ArrayList<Individuo>();				
		
		for(int i=0; i<poblacion.size(); i++){	
			int[] parUnico = AlgoritmoGenetico.todasCombinacionesUnicas.get(new Random().nextInt(AlgoritmoGenetico.todasCombinacionesUnicas.size()));
			
			int c1 = parUnico[0];
			int c2 = parUnico[1];
			
			Individuo aux1 = poblacion.get(c1);
			Individuo aux2 = poblacion.get(c2);
			
			if(aux1.getFitness() < aux2.getFitness()){
				nueva_poblacion.add(aux1);
			}else{
				nueva_poblacion.add(aux2);
			}
		}
		return nueva_poblacion;
	}	
}
