package cl.usach.mh.algoritmogenetico;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
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

	public static ArrayList<int[]> todasCombinacionesUnicas = new ArrayList<int[]>();
	
	public static int[] mejorSolucionHistorica;
	public static ArrayList<int[]> mejoresSolucionesHistoricas;
	public static ArrayList<Integer> mejoresCostosEncontrados = new ArrayList<Integer>();
	public static ArrayList<int[]> solucionesEncontradas;
	public static ArrayList<Integer> costosEncontrados = new ArrayList<Integer>();
	
	public static ArrayList<Timestamp> solucionesEncontradasTimestamp = new ArrayList<Timestamp>();
	public static ArrayList<Timestamp> mejoresSolucionesHistoricasTimestamp = new ArrayList<Timestamp>();
	
	public static void ejecucion(int[] solucionInicial, int tamanioPoblacion, int numeroPoblaciones, boolean hibrido, int tenor, int numeroCiclosTotales, int limiteIntercambios) throws URISyntaxException, IOException {
		
		// 1) Contador de salida de soluciones
		mejoresSolucionesHistoricas = new ArrayList<int[]>();
		solucionesEncontradas = new ArrayList<int[]>();
		costosEncontrados = new ArrayList<Integer>();
		mejoresCostosEncontrados = new ArrayList<Integer>();
		solucionesEncontradasTimestamp = new ArrayList<Timestamp>();
		mejoresSolucionesHistoricasTimestamp = new ArrayList<Timestamp>();
		
		// La siguiente lnea genera todos los posibles pares no repetidos del tamao de la poblacin.
		// Muy til para hacer el azar ms excto. Sólo gasto al comienzo.
		(new Combinations(tamanioPoblacion,2)).iterator().forEachRemaining(todasCombinacionesUnicas::add);
		
		// 3) Generacin de una poblacion inicial aleatoria
		ArrayList<Individuo> poblacion = new ArrayList<Individuo>();
						
		Individuo ind = new Individuo(solucionInicial);
		
		for(int i=0; i<tamanioPoblacion; i++){ 
			ind.intercambioFijo(QAP.paresUnicosSolucion[i]); // Intercambio fijo. Así aseguramos una poblacion inicial, para comparaciones justas.
			if (!hibrido) {
				Fitness.calculoFitness(ind);
			} else {
				Fitness.calculoFitnessHibrido(ind, tenor, numeroCiclosTotales, limiteIntercambios);
			}					
			System.out.println("Individuo inicial #" + (i + 1) + " añadido. Total: " + tamanioPoblacion);
			poblacion.add((new Cloner()).deepClone(ind));
		}
		mejorSolucionHistorica = new int[QAP.getCantidad()];
		// Fin de poblacin inicial alaetoria		
		
		// 4) Ciclo central. Criterio de parada: Nmero de Iteraciones / Poblaciones
		for(int j=0; j<numeroPoblaciones; j++){
			// 5) Selecciono los padres. Sabemos de tres mtodos a la fecha: Azar, Ruleta y Torneo
			// Ac se implementa el mtodo de seleccin de torneo (elitista)
			ArrayList<Individuo> padres = Seleccion.porTorneo(poblacion);
			
			// Operador de cruce			
			ArrayList<Individuo> hijos = Cruce.cruzamientoEnUnPunto(padres, hibrido, tenor, numeroCiclosTotales, limiteIntercambios);
			
			// Operador de mutacion (En este caso, aleatorio)
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
				mejorSolucionHistorica = min_fitness.getGenotipo();
			}			
			solucionesEncontradas.add(min_fitness.getGenotipo());
			costosEncontrados.add(QAP.calculoCosto(min_fitness.getGenotipo()));
			if(min_fitness.getFitness()<QAP.calculoCosto(mejorSolucionHistorica)) {
				mejorSolucionHistorica = min_fitness.getGenotipo();
			}
			mejoresSolucionesHistoricas.add(mejorSolucionHistorica);
			mejoresCostosEncontrados.add(QAP.calculoCosto(mejorSolucionHistorica));
			
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			
			// En el caso de las soluciones encontradas, cada Timestamp sera unico.
			solucionesEncontradasTimestamp.add(timestamp);
			// En el caso de las mejores soluciones, la pregunta es: Cambio? Si cambio, registro el nuevo Timestamp
			// Si no, repito el anterior.
			if(mejoresSolucionesHistoricas.size()>1) {
				if(!Arrays.equals(mejoresSolucionesHistoricas.get(mejoresSolucionesHistoricas.size()-1), mejoresSolucionesHistoricas.get(mejoresSolucionesHistoricas.size()-2))) {
					mejoresSolucionesHistoricasTimestamp.add(timestamp);
				} else {
					mejoresSolucionesHistoricasTimestamp.add(mejoresSolucionesHistoricasTimestamp.get(mejoresSolucionesHistoricasTimestamp.size()-1));
				}
			} else {
				mejoresSolucionesHistoricasTimestamp.add(timestamp);
			}			
			//
			
			System.out.println("Población procesada #: " + (j + 1) + ". Total: " + numeroPoblaciones);
		}				
	}
}
