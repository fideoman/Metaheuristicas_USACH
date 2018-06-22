package cl.usach.mh.algoritmogenetico.seleccion;

import java.util.ArrayList;
import java.util.Random;

import cl.usach.mh.algoritmogenetico.Individuos;

/**
 * Seleccion de los individuos de la poblacion.
 * @author Jose Manuel Rosell Sanchez
 * Adaptado por Isaac Silva
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
	public static ArrayList<Individuos> porTorneo(ArrayList<Individuos> poblacion){
		Random rnd = new Random();
		ArrayList<Individuos> nueva_poblacion = new ArrayList<Individuos>();
		
		for(int i=0; i<poblacion.size(); i++){
			int cromosoma1 = rnd.nextInt(((poblacion.size()-1) - 0) + 1) + 0;
			int cromosoma2 = rnd.nextInt(((poblacion.size()-1) - 0) + 1) + 0;
			while(cromosoma2==cromosoma1){
				cromosoma2 = rnd.nextInt(((poblacion.size()-1) - 0) + 1) + 0;
			}

			Individuos aux1 = poblacion.get(cromosoma1);
			Individuos aux2 = poblacion.get(cromosoma2);
			
			if(aux1.getFitness() < aux2.getFitness()){
				nueva_poblacion.add(aux1);
			}else{
				nueva_poblacion.add(aux2);
			}
		}
		return nueva_poblacion;
	}
}
