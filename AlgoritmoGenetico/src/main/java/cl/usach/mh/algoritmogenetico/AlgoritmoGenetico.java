package cl.usach.mh.algoritmogenetico;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.numbers.combinatorics.Combinations;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import cl.usach.mh.algoritmogenetico.cruzamiento.Cruce;
import cl.usach.mh.algoritmogenetico.mutacion.Mutacion;
import cl.usach.mh.algoritmogenetico.poblacion.Fitness;
import cl.usach.mh.algoritmogenetico.qap.Local;
import cl.usach.mh.algoritmogenetico.qap.Localidad;
import cl.usach.mh.algoritmogenetico.seleccion.Seleccion;

/**
 * Clase principal que ejecuta el algoritmo genetico.
 * @author Jose Manuel Rosell Sanchez
 * Adaptado y mejorado por Isaac Silva
 */
public class AlgoritmoGenetico {

	// Variables estáticas que no cambian, libres de ser accesadas
	public static ArrayList<int[]> todasCombinacionesUnicas = new ArrayList<int[]>(); 	
	public static ArrayList<Local> locales = new ArrayList<Local>();
	public static ArrayList<Localidad> localidades = new ArrayList<Localidad>();	
	public static int largoCromosoma = 0;	
	
	/**
	 * El metodo main
	 *
	 * @param args los argumentos
	 * @throws URISyntaxException 
	 */
	public static void main(String[] args) throws URISyntaxException {
		
		// 0) Contador de salida de soluciones
		ArrayList<Integer> mejores = new ArrayList<Integer>();
		ArrayList<Integer> todos = new ArrayList<Integer>();
		
		// 1) Cargar los datos de distancia y flujos en listas estáticas, listas para cálculos
		cargarDatosQAP("nug12.qap");
		
		// 2) Carga de parámetros: Tamaño de población, número de poblaciones y tipo de ejecución del algoritmo
		int tamanioPoblacion = 10;
		int numeroPoblaciones = 15;
		int tipoCalculoFitness = 1; // 1: Estándar | 2: Estándar + Fitness mejorado (Busqueda Local) | 3: Estándar + Fitness y cromosoma (Busqueda Local)
		// Fin de parámetros
		
		// La siguiente línea genera todos los posibles pares no repetidos del tamaño de la población.
		// Muy útil para hacer el azar más exácto
		(new Combinations(tamanioPoblacion,2)).iterator().forEachRemaining(todasCombinacionesUnicas::add);
		
		// 3) Generación de una poblacion inicial aleatoria
		ArrayList<Individuo> poblacion = new ArrayList<Individuo>();
				
		for(int i=0; i<tamanioPoblacion; i++){ 
			Individuo ind = new Individuo(largoCromosoma);
			switch (tipoCalculoFitness) {
			case 1: //Estandar. Obtiene el Fitness directamente
				Fitness.fitnessCromosoma(ind);
				break;
			case 2: //Baldwiniana. Obtiene Fitness del individuo y sobre éste busca la mejor solucion usando una Busqueda Local Golosa (Hibrido)
				Fitness.fitnessGoloso(ind);
				break;
			case 3: //Lamarckiana. Obtiene Fitness del individuo y sobre éste busca la mejor solucion Y cromosoma usando una Busqueda Local Golosa (Hibrido)
				Fitness.fitnessGolosoModifica(ind);
				break;
			default:
				break;
			}			
			poblacion.add(ind);
		}
		int mejorFitness = 0;
		// Fin de población inicial alaetoria		
		
		// 4) Ciclo central. Criterio de parada: Número de Iteraciones / Poblaciones
		for(int j=0; j<numeroPoblaciones; j++){
			// 5) Selecciono los padres. Sabemos de tres métodos a la fecha: Azar, Ruleta y Torneo
			// Acá se implementa el método de selección de torneo (elitista)
			ArrayList<Individuo> padres = Seleccion.porTorneo(poblacion);
			
			// Operador de cruce			
			ArrayList<Individuo> hijos = Cruce.cruzamientoEnUnPunto(padres, tipoCalculoFitness);

			// Operador de mutacion
			ArrayList<Individuo> hijosMutados = Mutacion.mutarPoblacion(hijos);
			
			// La evolución más fácil: Los nuevos hijos son la nueva población			
			poblacion = hijosMutados;
			
			// Obtengamos el mejor de la población
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
				mejorFitness = min_fitness.getFitness();
			}			
			todos.add(min_fitness.getFitness());
			if(min_fitness.getFitness()<mejorFitness) {
				mejorFitness = min_fitness.getFitness();
			}
			mejores.add(mejorFitness);
			
			System.out.println("# Población procesada: " + (j + 1) + ". Total: " + numeroPoblaciones);
		}
		grafico("Mejor Resultado: " + (mejorFitness == 578 ? "578 (El mejor del mundo)" : mejorFitness + " (578 es el mejor del mundo)"), "Todas", "Mejor", todos, mejores);		
	}
	
	public static void cargarDatosQAP(String archivoQAPLIB) throws URISyntaxException {
		ClassLoader cargadorContexto = Thread.currentThread().getContextClassLoader();

		ArrayList<ArrayList<Integer>> distancias = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> flujos = new ArrayList<ArrayList<Integer>>();

		List<Integer> secuenciaBase;
		try {
			Scanner sc = new Scanner(new File(cargadorContexto.getResource(archivoQAPLIB).toURI()));
			if (sc.hasNextInt()) {
				largoCromosoma = sc.nextInt();
			}
			for (int i = 0; i < largoCromosoma; i++) {
				distancias.add(new ArrayList<Integer>());
				for (int j = 0; j < largoCromosoma; j++) {
					distancias.get(i).add(sc.nextInt());
				}
			}
			for (int i = 0; i < largoCromosoma; i++) {
				flujos.add(new ArrayList<Integer>());
				for (int j = 0; j < largoCromosoma; j++) {
					flujos.get(i).add(sc.nextInt());
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		secuenciaBase = IntStream.rangeClosed(1, largoCromosoma).boxed().collect(Collectors.toList());

		// 1) Eliminamos la diagonal cero.
		for (int i = 0; i < flujos.size(); i++) {
			for (int j = 0; j < flujos.get(i).size(); j++) {
				if (i == j) { // Diagonal
					int[] fila = flujos.get(i).stream().mapToInt(Integer::valueOf).toArray();
					fila = ArrayUtils.remove(fila, j);
					flujos.set(i, (ArrayList<Integer>) Arrays.stream(fila).boxed().collect(Collectors.toList()));
				}
			}
		}
		for (int i = 0; i < distancias.size(); i++) {
			for (int j = 0; j < distancias.get(i).size(); j++) {
				if (i == j) { // Diagonal
					int[] fila = distancias.get(i).stream().mapToInt(Integer::valueOf).toArray();
					fila = ArrayUtils.remove(fila, j);
					distancias.set(i,(ArrayList<Integer>) Arrays.stream(fila).boxed().collect(Collectors.toList()));
				}
			}
		}
		// 2) Encapsulamos los objetos
		for (int i = 1; i <= flujos.size(); i++) {
			Local local = new Local();
			int[] conjuntoComplemento = new int[largoCromosoma - 1];
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
			local.flujos = flujos.get(i - 1).stream().mapToInt(Integer::valueOf).toArray();
			locales.add(local);
		}
		for (int i = 1; i <= distancias.size(); i++) {
			Localidad localidad = new Localidad();
			localidad.distancias = distancias.get(i - 1).stream().mapToInt(Integer::valueOf).toArray();
			localidades.add(localidad);
		}		
	}
	
	public static void grafico(String titulo, String linea1, String linea2, ArrayList<Integer> todos, ArrayList<Integer> mejores) {
		final XYSeries serie1 = new XYSeries(linea1);
		final XYSeries serie2 = new XYSeries(linea2);
		for(int i=0;i<todos.size();i++) {
			serie1.add(i, todos.get(i));
		}
		for(int i=0;i<mejores.size();i++) {
			serie2.add(i, mejores.get(i));
		}
		final XYSeriesCollection datos = new XYSeriesCollection();
		datos.addSeries(serie1);
		datos.addSeries(serie2);
		final JFreeChart chart = ChartFactory.createXYLineChart(titulo, "Iteraciones", "Costos", datos,
				PlotOrientation.VERTICAL, true, true, false);

		ChartFrame frame = new ChartFrame(titulo,chart);
		frame.pack();
		frame.setVisible(true);
	}
}
