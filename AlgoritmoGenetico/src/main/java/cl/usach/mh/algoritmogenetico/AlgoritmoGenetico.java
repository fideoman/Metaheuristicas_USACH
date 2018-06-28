package cl.usach.mh.algoritmogenetico;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.numbers.combinatorics.Combinations;

import com.rits.cloning.Cloner;

import cl.usach.mh.algoritmogenetico.cruzamiento.Cruce;
import cl.usach.mh.algoritmogenetico.mutacion.Mutacion;
import cl.usach.mh.algoritmogenetico.poblacion.Fitness;
import cl.usach.mh.algoritmogenetico.seleccion.Seleccion;
import cl.usach.mh.comunes.qap.QAP;

/**
 * Clase principal que ejecuta el algoritmo genetico.
 * @author Jose Manuel Rosell Sanchez
 * Adaptado y mejorado por Isaac Silva
 */
public class AlgoritmoGenetico {

	// Variables estticas que no cambian, libres de ser accesadas
	public static ArrayList<int[]> todasCombinacionesUnicas = new ArrayList<int[]>();
	
	/**
	 * El metodo main
	 *
	 * @param args los argumentos
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws URISyntaxException, IOException {
		
		// 0) Contador de salida de soluciones
		ArrayList<int[]> mejores = new ArrayList<int[]>();
		ArrayList<int[]> todos = new ArrayList<int[]>();
		
		// 1) Cargar los datos de distancia y flujos en listas estticas, listas para clculos
		//QAP.cargarDatosQAP("qap/nug12.qap");
		//QAP.cargarDatosQAP("qap/chr25a.qap");
		QAP.cargarDatosQAP("qap/esc32a.qap");
		//QAP.cargarDatosQAP("qap/esc64a.qap");
		//QAP.cargarDatosQAP("qap/esc128.qap");	
		
		// De 12:
		//int[] solucionInicial = new int[] {8, 5, 6, 1, 7, 12, 4, 3, 11, 10, 9, 2};
		// De 25:
		//int[] solucionInicial = new int[] {3, 8, 9, 13, 17, 6, 11, 7, 12, 18, 5, 19, 2, 16, 25, 15, 24, 4, 22, 10, 14, 23, 1, 20, 21};
		// De 32:
		int[] solucionInicial = new int[] {12, 18, 5, 31, 26, 13, 11, 9, 32, 21, 24, 20, 19, 14, 8, 15, 23, 3, 30, 17, 22, 16, 29, 7, 6, 4, 27, 25, 10, 1, 2, 28};
		// De 64:
		//int[] solucionInicial = new int[] {44, 34, 29, 61, 15, 1, 19, 36, 46, 43, 16, 54, 28, 56, 33, 40, 63, 7, 17, 14, 26, 22, 32, 23, 37, 2, 58, 48, 24, 41, 5, 6, 64, 4, 31, 25, 47, 10, 49, 21, 53, 52, 8, 35, 3, 20, 30, 11, 59, 60, 39, 55, 27, 57, 13, 12, 18, 42, 45, 62, 50, 38, 9, 51};
		// De 128:
		//int[] solucionInicial = new int[] {48, 11, 21, 40, 20, 82, 108, 128, 75, 56, 5, 15, 124, 46, 55, 63, 64, 106, 26, 4, 34, 86, 116, 57, 18, 93, 84, 9, 50, 90, 7, 94, 33, 85, 87, 114, 22, 1, 31, 52, 97, 112, 27, 41, 6, 78, 121, 12, 118, 110, 74, 61, 127, 99, 45, 95, 10, 123, 117, 72, 35, 2, 100, 69, 19, 16, 17, 76, 113, 66, 44, 71, 96, 59, 77, 92, 67, 105, 88, 107, 102, 73, 8, 83, 81, 120, 65, 111, 28, 91, 29, 23, 80, 38, 42, 54, 47, 109, 70, 98, 37, 14, 30, 125, 3, 39, 62, 122, 32, 25, 68, 53, 104, 79, 103, 36, 89, 58, 126, 13, 101, 115, 43, 119, 51, 49, 24, 60};
		
		// 2) Carga de parmetros: Tamao de poblacin, nmero de poblaciones y tipo de ejecucin del algoritmo
		int tamanioPoblacion = 100;
		int numeroPoblaciones = 100;
		boolean hibrido = false; // false : Normal - true : AG + Busqueda Tabu
		// Parámetros del Tabu Search
		int tenor = 2;
		int numeroCiclosTotales = 20;
		int limiteIntercambios = 4;
		// Fin de parmetros
		
		// La siguiente lnea genera todos los posibles pares no repetidos del tamao de la poblacin.
		// Muy til para hacer el azar ms excto. Sólo gasto al comienzo.
		(new Combinations(tamanioPoblacion,2)).iterator().forEachRemaining(todasCombinacionesUnicas::add);
		
		// 3) Generacin de una poblacion inicial aleatoria
		ArrayList<Individuo> poblacion = new ArrayList<Individuo>();
				
		ArrayList<int[]> combSol = new ArrayList<int[]>();
		(new Combinations(QAP.getCantidad(),2)).iterator().forEachRemaining(combSol::add);
		
		Individuo ind = new Individuo(solucionInicial);
		
		for(int i=0; i<tamanioPoblacion; i++){ 
			ind.intercambioFijo(combSol.get(i)); // Intercambio fijo. Así aseguramos una población inicial, para comparaciones justas.
			if (!hibrido) {
				Fitness.calculoFitness(ind);
			} else {
				Fitness.calculoFitnessHibrido(ind, tenor, numeroCiclosTotales, limiteIntercambios);
			}					
			System.out.println("Individuo inicial #" + (i + 1) + " añadido. Total: " + tamanioPoblacion);
			poblacion.add((new Cloner()).deepClone(ind));
		}
		int[] mejorSolucion = new int[QAP.getCantidad()];
		// Fin de poblacin inicial alaetoria		
		
		// 4) Ciclo central. Criterio de parada: Nmero de Iteraciones / Poblaciones
		for(int j=0; j<numeroPoblaciones; j++){
			// 5) Selecciono los padres. Sabemos de tres mtodos a la fecha: Azar, Ruleta y Torneo
			// Ac se implementa el mtodo de seleccin de torneo (elitista)
			ArrayList<Individuo> padres = Seleccion.porTorneo(poblacion);
			
			// Operador de cruce			
			ArrayList<Individuo> hijos = Cruce.cruzamientoEnUnPunto(padres, hibrido, tenor, numeroCiclosTotales, limiteIntercambios);
			
			// Operador de mutacion
			ArrayList<Individuo> hijosMutados = Mutacion.mutarPoblacion(hijos, hibrido, tenor, numeroCiclosTotales, limiteIntercambios);
			
			// Recomendaciones generales en http://www.baeldung.com/java-genetic-algorithm
			// Tasa de cruze: 90%
			// Tasa de mutación: 2%
			// Mejores padres: 8%
			int cantidadCruze = (int) Math.round((double) tamanioPoblacion * 0.9);
			int cantidadMutacion = (int) Math.round((double) tamanioPoblacion * 0.02);
			int cantidadPadresTop = (int) Math.round((double) tamanioPoblacion * 0.08);
			int diff = tamanioPoblacion - (cantidadCruze + cantidadMutacion + cantidadPadresTop);
			if(diff > 0) {
				cantidadCruze += diff;
			}
			
			List<Individuo> poblacionFinal = hijos.subList(0, cantidadCruze);
			poblacionFinal.addAll(hijosMutados.subList(0, cantidadMutacion));
			poblacionFinal.addAll(padres.subList(0, cantidadPadresTop));
			poblacion = new ArrayList<Individuo>(poblacionFinal);

			// Rutina de obtencion de los mejores
			// Obtengamos el mejor de la poblacin
			Individuo min_fitness;
			min_fitness = poblacion.get(0);
			for(int ii=0; ii<poblacion.size();ii++){
				Individuo aux = poblacion.get(ii);
				if(aux.getFitness()<min_fitness.getFitness()){
					min_fitness = aux;
				}
			}			
			if(j==0) {
				// El primero es el mejor Fitness
				mejorSolucion = min_fitness.getGenotipo();
			}			
			todos.add(min_fitness.getGenotipo());
			if(min_fitness.getFitness()<QAP.calculoCosto(mejorSolucion)) {
				mejorSolucion = min_fitness.getGenotipo();
			}
			mejores.add(mejorSolucion);
			//
			
			System.out.println("Población procesada #: " + (j + 1) + ". Total: " + numeroPoblaciones);
		}
		QAP.grafico("Mejor Resultado: " + mejorSolucion, "Mejor de la Población X", "Mejor Histórico", todos, mejores);		
	}
}
