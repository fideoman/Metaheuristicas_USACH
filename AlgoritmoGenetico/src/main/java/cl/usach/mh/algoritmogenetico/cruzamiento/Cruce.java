package cl.usach.mh.algoritmogenetico.cruzamiento;

import java.util.ArrayList;
import java.util.Random;

import cl.usach.mh.algoritmogenetico.Individuos;
import cl.usach.mh.algoritmogenetico.poblacion.Fitness;

/**
 * Clase para realizar el cruce de dos cromosomas.
 * @author Jose Manuel Rosell Sanchez
 * Adaptado por Isaac Silva
 */
public class Cruce {
	
	/**
	 * Obtiene el array desde posicion inicio hasta posicion fin, del array que se pasa.
	 *
	 * @param array Array a cortar
	 * @param pos_ini posicion de inicio
	 * @param pos_fin posicion de fin
	 * @return el array recortado
	 */
	public static ArrayList<Integer> subArray(ArrayList<Integer> array, int pos_ini, int pos_fin){
		ArrayList<Integer> sub = new ArrayList<Integer>();

		for(int i=pos_ini; i<pos_fin; i++){
			sub.add(array.get(i));
		}
		
		return sub;
	}
	
	/**
	 * Cruce de dos individuos, utilizando el cruce en un punto. Se dividen los dos padres a partir de ese
	 * punto de cruce (que se obtiene aleatoriamente) y se mezclan las dos partes de los padres. Puede que
	 * la solución obtenida no sea correcta por lo que hay que arreglarla para que no se repitan las fabricas. 
	 *
	 * @param poblacion poblacion inicial
	 * @param distancias las distancias entre las fabricas
	 * @param pesos los pesos entre las fabricas
	 * @param cantidad la longitud de la solucion (longitud del cromosoma)
	 * @return la nueva poblacion despues de realizar los cruces
	 */
	public static ArrayList<Individuos> cruzarEstandar(ArrayList<Individuos> poblacion, ArrayList<ArrayList<Integer>> distancias,
											ArrayList<ArrayList<Integer>> pesos, int cantidad){
		ArrayList<Individuos> nueva_poblacion = new ArrayList<Individuos>();
		int tam = poblacion.size();
		Random rnd = new Random();

		while(!poblacion.isEmpty()){
			int c1 = rnd.nextInt(((tam-1) - 0) + 1) + 0;
			int c2 = rnd.nextInt(((tam-1) - 0) + 1) + 0;
			while(c1==c2){
				c2 = rnd.nextInt(((tam-1) - 0) + 1) + 0;
			}
			
			Individuos aux1 = poblacion.get(c1);
			Individuos aux2 = poblacion.get(c2);
			
			int pos = rnd.nextInt(((cantidad-2) - 1) + 1) + 1;
			
			ArrayList<Integer> p1 = subArray(aux1.getCromosoma(), 0, pos);
			ArrayList<Integer> p2 = subArray(aux2.getCromosoma(), 0, pos);

			for(int z=pos; z<cantidad; z++){
				if(p1.contains(aux2.getCromosoma().get(z)) == false){
					p1.add(aux2.getCromosoma().get(z));
				}
				if(p2.contains(aux1.getCromosoma().get(z)) == false){
					p2.add(aux1.getCromosoma().get(z));
				}
			}
			
			for(int i=0; i<cantidad; i++){
				if(p1.contains(aux2.getCromosoma().get(i)) == false){
					p1.add(aux2.getCromosoma().get(i));
				}
				if(p2.contains(aux1.getCromosoma().get(i)) == false){
					p2.add(aux1.getCromosoma().get(i));
				}
			}
			
			ArrayList<Integer> cromosoma1 = new ArrayList<>(p1);
			ArrayList<Integer> cromosoma2 = new ArrayList<>(p2);
			aux1.setCromosoma(cromosoma1);
			aux2.setCromosoma(cromosoma2);
			//Calculamos el fitness del nuevo cromosoma
			Fitness.fitnessCromosoma(aux1, distancias, pesos);
			Fitness.fitnessCromosoma(aux2, distancias, pesos);
			
			//Añadimos los dos nuevos individuos a la poblacion
			nueva_poblacion.add(aux1);
			nueva_poblacion.add(aux2);
			
			if(c1>c2){
				poblacion.remove(c1);
				poblacion.remove(c2);
			}else{
				poblacion.remove(c2);
				poblacion.remove(c1);
			}
			
			tam-=2;
		}
		
		return nueva_poblacion;
	}
	
	/**
	 * Cruce de dos individuos, utilizando el cruce en un punto. Se dividen los dos padres a partir de ese
	 * punto de cruce (que se obtiene aleatoriamente) y se mezclan las dos partes de los padres. Puede que
	 * la solución obtenida no sea correcta por lo que hay que arreglarla para que no se repitan las fabricas. 
	 * Ademas, se introduce un algoritmo goloso para calcular el fitness del hijo, en el que se obtiene el 
	 * mejor fitness para el cromosoma pero el cromosoma no se modifica.
	 *
	 * @param poblacion poblacion inicial
	 * @param distancias las distancias entre las fabricas
	 * @param pesos los pesos entre las fabricas
	 * @param cantidad la longitud de la solucion (longitud del cromosoma)
	 * @return la nueva poblacion despues de realizar los cruces
	 */
	public static ArrayList<Individuos> cruzarBaldwin(ArrayList<Individuos> poblacion, ArrayList<ArrayList<Integer>> distancias,
			ArrayList<ArrayList<Integer>> pesos, int cantidad){
		ArrayList<Individuos> nueva_poblacion = new ArrayList<Individuos>();
		int tam = poblacion.size();
		Random rnd = new Random();
		
		while(!poblacion.isEmpty()){
			int c1 = rnd.nextInt(((tam-1) - 0) + 1) + 0;
			int c2 = rnd.nextInt(((tam-1) - 0) + 1) + 0;
			while(c1==c2){
				c2 = rnd.nextInt(((tam-1) - 0) + 1) + 0;
			}
			
			Individuos aux1 = poblacion.get(c1);
			Individuos aux2 = poblacion.get(c2);
			
			int pos = rnd.nextInt(((cantidad-2) - 1) + 1) + 1;
			
			ArrayList<Integer> p1 = subArray(aux1.getCromosoma(), 0, pos);
			ArrayList<Integer> p2 = subArray(aux2.getCromosoma(), 0, pos);
			
			for(int z=pos; z<cantidad; z++){
				if(p1.contains(aux2.getCromosoma().get(z)) == false){
					p1.add(aux2.getCromosoma().get(z));
				}
				if(p2.contains(aux1.getCromosoma().get(z)) == false){
					p2.add(aux1.getCromosoma().get(z));
				}
			}
			
			for(int i=0; i<cantidad; i++){
				if(p1.contains(aux2.getCromosoma().get(i)) == false){
					p1.add(aux2.getCromosoma().get(i));
				}
				if(p2.contains(aux1.getCromosoma().get(i)) == false){
					p2.add(aux1.getCromosoma().get(i));
				}
			}
			
			ArrayList<Integer> cromosoma1 = new ArrayList<>(p1);
			ArrayList<Integer> cromosoma2 = new ArrayList<>(p2);
			aux1.setCromosoma(cromosoma1);
			aux2.setCromosoma(cromosoma2);
			//Calculamos el fitness del nuevo cromosoma
			Fitness.fitnessGoloso(aux1, distancias, pesos);
			Fitness.fitnessGoloso(aux2, distancias, pesos);
			
			//Añadimos los dos nuevos individuos a la poblacion
			nueva_poblacion.add(aux1);
			nueva_poblacion.add(aux2);
			
			if(c1>c2){
				poblacion.remove(c1);
				poblacion.remove(c2);
			}else{
				poblacion.remove(c2);
				poblacion.remove(c1);
			}
			
			tam-=2;
		}
		
		return nueva_poblacion;
	}
	
	/**
	 * Cruce de dos individuos, utilizando el cruce en un punto. Se dividen los dos padres a partir de ese
	 * punto de cruce (que se obtiene aleatoriamente) y se mezclan las dos partes de los padres. Puede que
	 * la solución obtenida no sea correcta por lo que hay que arreglarla para que no se repitan las fabricas.
	 * Ademas, se introduce un algoritmo goloso para calcular el fitness del hijo, en el que se obtiene el 
	 * mejor fitness para el cromosoma y el cromosoma se modifica con este algoritmo. 
	 *
	 * @param poblacion poblacion inicial
	 * @param distancias las distancias entre las fabricas
	 * @param pesos los pesos entre las fabricas
	 * @param cantidad la longitud de la solucion (longitud del cromosoma)
	 * @return la nueva poblacion despues de realizar los cruces
	 */
	public static ArrayList<Individuos> cruzarLamarck(ArrayList<Individuos> poblacion, ArrayList<ArrayList<Integer>> distancias,
			ArrayList<ArrayList<Integer>> pesos, int cantidad){
		ArrayList<Individuos> nueva_poblacion = new ArrayList<Individuos>();
		int tam = poblacion.size();
		Random rnd = new Random();
		
		while(!poblacion.isEmpty()){
			int c1 = rnd.nextInt(((tam-1) - 0) + 1) + 0;
			int c2 = rnd.nextInt(((tam-1) - 0) + 1) + 0;
			while(c1==c2){
				c2 = rnd.nextInt(((tam-1) - 0) + 1) + 0;
			}
			
			Individuos aux1 = poblacion.get(c1);
			Individuos aux2 = poblacion.get(c2);
			
			int pos = rnd.nextInt(((cantidad-2) - 1) + 1) + 1;
			
			ArrayList<Integer> p1 = subArray(aux1.getCromosoma(), 0, pos);
			ArrayList<Integer> p2 = subArray(aux2.getCromosoma(), 0, pos);
			
			for(int z=pos; z<cantidad; z++){
				if(p1.contains(aux2.getCromosoma().get(z)) == false){
					p1.add(aux2.getCromosoma().get(z));
				}
				if(p2.contains(aux1.getCromosoma().get(z)) == false){
					p2.add(aux1.getCromosoma().get(z));
				}
			}
			
			for(int i=0; i<cantidad; i++){
				if(p1.contains(aux2.getCromosoma().get(i)) == false){
					p1.add(aux2.getCromosoma().get(i));
				}
				if(p2.contains(aux1.getCromosoma().get(i)) == false){
					p2.add(aux1.getCromosoma().get(i));
				}
			}
			
			ArrayList<Integer> cromosoma1 = new ArrayList<>(p1);
			ArrayList<Integer> cromosoma2 = new ArrayList<>(p2);
			aux1.setCromosoma(cromosoma1);
			aux2.setCromosoma(cromosoma2);
			//Calculamos el fitness del nuevo cromosoma
			Fitness.fitnessGolosoModifica(aux1, distancias, pesos);
			Fitness.fitnessGolosoModifica(aux2, distancias, pesos);
			
			//Añadimos los dos nuevos individuos a la poblacion
			nueva_poblacion.add(aux1);
			nueva_poblacion.add(aux2);
			
			if(c1>c2){
				poblacion.remove(c1);
				poblacion.remove(c2);
			}else{
				poblacion.remove(c2);
				poblacion.remove(c1);
			}
			
			tam-=2;
		}
		
		return nueva_poblacion;
	}
}
