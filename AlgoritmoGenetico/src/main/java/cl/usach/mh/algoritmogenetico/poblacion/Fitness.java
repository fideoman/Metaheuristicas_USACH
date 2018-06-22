package cl.usach.mh.algoritmogenetico.poblacion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ArrayUtils;

import cl.usach.mh.algoritmogenetico.Individuos;

/**
 * Clase para obtener el fitness de un cromosoma (o solucion).
 * @author Jose Manuel Rosell Sanchez
 * Adaptado por Isaac Silva
 */
public class Fitness {
	
	/**
	 * Obtiene el fitness de una solucion del problema.
	 *
	 * @param i1 el individuo del cual obtener el fitness
	 * @param distancias las distancias entre las fabricas
	 * @param pesos los pesos entre las fabricas
	 */
	public static void fitnessCromosoma(Individuos i1, ArrayList<ArrayList<Integer>> distancias,  ArrayList<ArrayList<Integer>> pesos){
		class Local {
			public String etiqueta;
			public int[] flujos;
			public int[] conjuntoComplemento;
		};
		
		class Localidad {
			@SuppressWarnings("unused")
			public String etiqueta;
			public int[] distancias;
			@SuppressWarnings("unused")
			public int[] conjuntoComplemento;
		};
		
		List<Integer> secuenciaBase = IntStream.rangeClosed(1, distancias.size()).boxed().collect(Collectors.toList());
		ArrayList<Local> locales = new ArrayList<Local>();
		ArrayList<Localidad> localidades = new ArrayList<Localidad>();
		
		// 1) Filtrar las listas de entrada, lo haremos en copias.
		// Eliminamos la diagonal cero.
		ArrayList<ArrayList<Integer>> distanciasClonadas = new ArrayList<ArrayList<Integer>>(distancias);
		ArrayList<ArrayList<Integer>> pesosClonados = new ArrayList<ArrayList<Integer>>(pesos);
		
		for (int i = 0; i < pesosClonados.size(); i++) {
			for (int j = 0; j < pesosClonados.get(i).size(); j++) {
				if(i == j) { // Diagonal
					int[] fila = pesosClonados.get(i).stream().mapToInt(Integer::valueOf).toArray();
					fila = ArrayUtils.remove(fila, j);
					pesosClonados.set(i, (ArrayList<Integer>) Arrays.stream(fila).boxed().collect(Collectors.toList()));
				}
			}
		}
		for (int i = 0; i < distanciasClonadas.size(); i++) {
			for (int j = 0; j < distanciasClonadas.get(i).size(); j++) {
				if(i == j) { // Diagonal
					int[] fila = distanciasClonadas.get(i).stream().mapToInt(Integer::valueOf).toArray();
					fila = ArrayUtils.remove(fila, j);
					distanciasClonadas.set(i, (ArrayList<Integer>) Arrays.stream(fila).boxed().collect(Collectors.toList()));
				}
			}
		}		
		// 2) Encapsulamos los objetos
		for (int i = 1; i <= pesosClonados.size(); i++) {
			Local local = new Local();
			int[] conjuntoComplemento = new int[secuenciaBase.size() - 1];
			for (int j = 1; j <= conjuntoComplemento.length + 1; j++) {
				if (i == secuenciaBase.get(j - 1)) {
					ArrayList<Integer> copiaSecuencia = new ArrayList<Integer>(secuenciaBase);
					for (Iterator<Integer> elementoCopia = copiaSecuencia.iterator(); elementoCopia.hasNext();) {
						Integer etiqueta = elementoCopia.next();
						if (j == etiqueta) {
							elementoCopia.remove();
						}
					}
					local.conjuntoComplemento = copiaSecuencia.stream().mapToInt(Integer::valueOf).toArray();
					break;
				}
			}
			local.etiqueta = String.valueOf(i);
			local.flujos = pesosClonados.get(i - 1).stream().mapToInt(Integer::valueOf).toArray();
			locales.add(local);
		}
		for (int i = 1; i <= distanciasClonadas.size(); i++) {
			Localidad localidad = new Localidad();
			int[] conjuntoComplemento = new int[secuenciaBase.size() - 1];
			for (int j = 1; j <= conjuntoComplemento.length + 1; j++) {
				if (i == secuenciaBase.get(j - 1)) {
					ArrayList<Integer> copiaSecuencia = new ArrayList<Integer>(secuenciaBase);
					for (Iterator<Integer> elementoCopia = copiaSecuencia.iterator(); elementoCopia.hasNext();) {
						Integer etiqueta = elementoCopia.next();
						if (j == etiqueta) {
							elementoCopia.remove();
						}
					}
					localidad.conjuntoComplemento = copiaSecuencia.stream().mapToInt(Integer::valueOf).toArray();
					break;
				}
			}
			localidad.etiqueta = String.valueOf(i);
			localidad.distancias = distanciasClonadas.get(i - 1).stream().mapToInt(Integer::valueOf).toArray();
			localidades.add(localidad);
		}
		// 3) Hagamos el cálculo del costo.
		// i1.getCromosoma() es la solución propuesta
		// li.fitness es la salida esperada (costo)
		
		int[] solucionPropuesta = i1.getCromosoma().stream().mapToInt(Integer::valueOf).toArray();
		// Las soluciones esperadas son unidades, no índices.
		for(int i = 0; i < solucionPropuesta.length; i++) {
			solucionPropuesta[i] = solucionPropuesta[i] + 1;
		}
		
        int costo = 0;
		for (int i = 0; i < solucionPropuesta.length; i++) {
			for (int j = 0; j < locales.size(); j++) {
				if (String.valueOf(solucionPropuesta[i]).equals(locales.get(j).etiqueta)) {
					int m = 0;
					for (int l = 0; l < solucionPropuesta.length; l++) {
						for (int o = 0; o < locales.get(j).conjuntoComplemento.length; o++) {
							if (solucionPropuesta[l] == locales.get(j).conjuntoComplemento[o]) {
								costo = costo
										+ locales.get(j).flujos[o]
										* localidades.get(i).distancias[m];
								m++;
							}
						}
					}
				}
			}
		}	
		// 4) Costo calculado.
		i1.setFitness(costo);
	}
	
	/**
	 * Calcular el fitness de una solucion obtienen el mejor fitness para el un cromosoma dado, utilizando para ello
	 * un algoritmo Goloso. Se calcula el mejor fitness pero no se modifica el cromosoma del individuo, unicamente se
	 * modifica el fitness.
	 *
	 * @param i1 el individuo del cual obtener el fitness
	 * @param distancias las distancias entre las fabricas
	 * @param pesos los pesos entre las fabricas
	 */
	public static void fitnessGoloso(Individuos i1, ArrayList<ArrayList<Integer>> distancias,  ArrayList<ArrayList<Integer>> pesos){
		fitnessCromosoma(i1, distancias, pesos);

		Individuos aux = new Individuos();
		aux = HeuristicaGolosa.mejorSolucion(i1, distancias, pesos);
		i1.setFitness(aux.getFitness());
	}
	
	/**
	 * Calcular el fitness de una solucion obtienen el mejor fitness para el un cromosoma dado, utilizando para ello
	 * un algoritmo Goloso. Se calcula el mejor fitness y se modifica el cromosoma del individuo, ademas de 
	 * modificar el fitness del individuo.
	 *
	 * @param i1 el individuo del cual obtener el fitness
	 * @param distancias las distancias entre las fabricas
	 * @param pesos los pesos entre las fabricas
	 */
	public static void fitnessGolosoModifica(Individuos i1, ArrayList<ArrayList<Integer>> distancias,  ArrayList<ArrayList<Integer>> pesos){
		fitnessCromosoma(i1, distancias, pesos);
	
		Individuos aux = new Individuos();
		aux = HeuristicaGolosa.mejorSolucion(i1, distancias, pesos);
		
		i1.setFitness(aux.getFitness());
		i1.setCromosoma(aux.getCromosoma());
	}
}
