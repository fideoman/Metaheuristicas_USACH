package cl.usach.mh.busquedatabu;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.numbers.combinatorics.Combinations;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import cl.usach.mh.busquedatabu.qap.Local;
import cl.usach.mh.busquedatabu.qap.Localidad;

public class BusquedaTabu12 {
	
	public static final List<Integer> secuencia = Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12); // Secuencia básica fija (12)
	
	public static ArrayList<Local> locales = new ArrayList<Local>();
	public static ArrayList<Localidad> localidades = new ArrayList<Localidad>();	
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		// Búsqueda Tabú
		// Lo más sencillo posible.
		// Grupo de datos QAP de 12x12 (NUG12, QAPLIB)
		// Solución óptima: (12, 7, 9, 3, 4, 8, 11, 1, 5, 6, 10, 2), Costo: 578
		
		// Guardemos nuestras soluciones. Guardaremos: Soluciones encontradas, y mejor Solución histórica en X iteración
		ArrayList<int[]> solucionesEncontradas = new ArrayList<int[]>();
		ArrayList<int[]> mejoresSolucionesHistoricas = new ArrayList<int[]>();
		
		// Parámetros del programa:
		int tenor = 5; // Tenor
		int[] solucionInicial = new int [] {1,8,5,11,2,4,7,10,9,6,12,3}; // Solución Inicial
		int numeroCiclos = 500; // Repeticiones - Ciclos de búsqueda
		// Fin de Parámetros		
		
		// 1) Cargar los datos de distancia y flujos en listas estáticas, listas para cálculos
		cargarDatosQAP();
		
		// 2) Inicializar Lista Tabú (memoria corto plazo), su tenor (tenure), memoria a mediano y largo plazo, con las estructuras más adecuadas. 
		TreeMap<String, Integer> listaTabu = new TreeMap<String, Integer>();
		int[][] memoriaMedianoPlazo = new int[solucionInicial.length][solucionInicial.length];
		DualHashBidiMap<String, Integer> memoriaLargoPlazo = new DualHashBidiMap<String, Integer>();		
		
		// 3) Generar todos los pares combinatorios, no repetidos, del universo. 
		// En éste caso particular, universo: 12
		ArrayList<int[]> parTemp = new ArrayList<int[]>(); 
		(new Combinations(solucionInicial.length,2)).iterator().forEachRemaining(parTemp::add);
		int [][] pares = parTemp.stream().toArray(int[][]::new);
		for(int i = 0; i < pares.length; i++) for(int j = 0; j < pares[i].length;j++) pares[i][j]++; 
		parTemp = null;
        
		// 4) La solución inicial es la mejor Solución histórica y actual al comenzar. Es la única que conocemos.
		int[] mejorSolucionHistorica = solucionInicial;
		int[] mejorSolucionActual = solucionInicial;
		
		for (int numeroIteracion = 1; numeroIteracion <= numeroCiclos; numeroIteracion++) { // Ciclo central
			// 5) Por cada combinación aplico un intercambio (swap) y almaceno sus evaluaciones y par de intercambio (swap) asociado en un mapa
			// Nota: Se hace el intercambio en una copia. No tocamos solución actual
			DualHashBidiMap<int[], String> movimientosVecinosEvaluados = new DualHashBidiMap<int[], String>();
			DualHashBidiMap<int[], Integer> costosVecinosEvaluados = new DualHashBidiMap<int[], Integer>();
			for(int[] par : pares) {
				List<Integer> copiaSolucionActual = Arrays.stream(mejorSolucionActual).boxed().collect(Collectors.toList());
				// 5a) Hago el intercambio (swap)
				Collections.swap(copiaSolucionActual, copiaSolucionActual.indexOf(par[0]), copiaSolucionActual.indexOf(par[1]));
				int[] vecino = copiaSolucionActual.stream().mapToInt(Integer::valueOf).toArray();
				movimientosVecinosEvaluados.put(vecino, String.valueOf(par[0])+"-"+String.valueOf(par[1]));
				costosVecinosEvaluados.put(vecino, calculoCosto(vecino));
			}
			// 6) Ordenemos los vecinos por costo ascendente, dado que estamos buscando los menores
			List<int[]> mejoresVecinos = costosVecinosEvaluados.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(Map.Entry::getKey).collect(Collectors.toList());
			
			// 7) Recorremos los mejores vecinos.
			for (int i = 0; i < mejoresVecinos.size(); i++) {
				// ¿Será que se generó con un movimiento Prohibido?
				if (listaTabu.containsKey(movimientosVecinosEvaluados.get(mejoresVecinos.get(i)))) {
					// Es un vecino generado por un movimiento prohibido.

					// Ultimo posible aporte del movimiento tabú: ¿Será que su costo sea menor que
					// el histórico?
					if (costosVecinosEvaluados.get(mejoresVecinos.get(i)) < calculoCosto(mejorSolucionHistorica)) {
						// Bingo! Tenemos una mejor solución actual e histórica.						
						mejorSolucionActual = mejoresVecinos.get(i);
						mejorSolucionHistorica = mejoresVecinos.get(i);
						
						solucionesEncontradas.add(mejorSolucionActual);
						mejoresSolucionesHistoricas.add(mejorSolucionHistorica);
						break; // ¡No necesito más!
					}
					// Si es prohibido, y no es el mejor histórico, voy por el siguiente mejor vecino. 
					continue;					
				} else { // Excelente, no es prohibido. Se acepta como nueva solución.
					mejorSolucionActual = mejoresVecinos.get(i); // Es la mejor solución actual.
					// ¿Será la mejor histórica?
					if(costosVecinosEvaluados.get(mejoresVecinos.get(i)) < calculoCosto(mejorSolucionHistorica)) {
						// Si. Enhorabuena.
						mejorSolucionHistorica = mejoresVecinos.get(i);
					}
					solucionesEncontradas.add(mejorSolucionActual);
					mejoresSolucionesHistoricas.add(mejorSolucionHistorica);
					
					// Ahora, prohibimos el movimiento, con un tenor dado
					listaTabu.put(movimientosVecinosEvaluados.get(mejoresVecinos.get(i)), tenor);
					break; // Nada más que hacer.
				}
			}
			
			// 8) Finalmente, decrecemos todos los contadores de movimientos prohibidos en uno.
			listaTabu.entrySet().forEach(v -> v.setValue(v.getValue() - 1));
			// Si el tenor del movimiento es cero, lo elimino de la lista tabú
			listaTabu.entrySet().removeIf(entry -> entry.getValue().equals(0));
			
		}
		// Fin del programa. Mostremos el mejor resultado, en un gráfico
		grafico("Tenor: " + tenor + " | Mejor Resultado: " + (calculoCosto(mejorSolucionHistorica) == 578 ? "578 (El mejor del mundo)" : calculoCosto(mejorSolucionHistorica) + " (578 es el mejor del mundo)"), "Todas", "Mejor", solucionesEncontradas, mejoresSolucionesHistoricas);
	}

	public static int calculoCosto(int[] solucionInicial) {				
        int costo = 0;
		for (int i = 0; i < solucionInicial.length; i++) {
			for (int j = 0; j < locales.size(); j++) {
				if (String.valueOf(solucionInicial[i]).equals(locales.get(j).etiqueta)) {
					int m = 0;
					for (int l = 0; l < solucionInicial.length; l++) {
						for (int o = 0; o < locales.get(j).conjuntoComplemento.length; o++) {
							if (solucionInicial[l] == locales.get(j).conjuntoComplemento[o]) {
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
        return costo; 
	}
	
	public static void cargarDatosQAP() throws IOException, URISyntaxException {
		ClassLoader cargadorClase = Thread.currentThread().getContextClassLoader();
		
		int[][] localidadesEnCrudo = new int[12][11];
		for (int linea = 0; linea < Files.readAllLines(Paths.get(cargadorClase.getResource("nug12d.qap").toURI()), Charset.defaultCharset()).size(); linea++) {
			localidadesEnCrudo[linea] = Arrays.asList(Files.readAllLines(Paths.get(cargadorClase.getResource("nug12d.qap").toURI()), Charset.defaultCharset()).get(linea).split(" ")).stream().mapToInt(Integer::valueOf).toArray();
		}   
		
		int[][] localesEnCrudo = new int[12][11];
		for (int linea = 0; linea < Files.readAllLines(Paths.get(cargadorClase.getResource("nug12f.qap").toURI()), Charset.defaultCharset()).size(); linea++) {
			localesEnCrudo[linea] = Arrays.asList(Files.readAllLines(Paths.get(cargadorClase.getResource("nug12f.qap").toURI()), Charset.defaultCharset()).get(linea).split(" ")).stream().mapToInt(Integer::valueOf).toArray();
		}    			        
        
		for (int i = 1; i <= localidadesEnCrudo.length; i++) {
			Localidad localidad = new Localidad();
			int[] conjuntoComplemento = new int[secuencia.size() - 1];
			for (int j = 1; j <= conjuntoComplemento.length + 1; j++) {
				if (i == secuencia.get(j - 1)) {
					ArrayList<Integer> copiaSecuencia = new ArrayList<Integer>(secuencia);
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
			localidad.distancias = localidadesEnCrudo[i - 1];
			localidades.add(localidad);
		}

		for (int i = 1; i <= localesEnCrudo.length; i++) {
			Local local = new Local();
			int[] conjuntoComplemento = new int[secuencia.size() - 1];
			for (int j = 1; j <= conjuntoComplemento.length + 1; j++) {
				if (i == secuencia.get(j - 1)) {
					ArrayList<Integer> copiaSecuencia = new ArrayList<Integer>(secuencia);
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
			local.flujos = localesEnCrudo[i - 1];
			locales.add(local);
		}	
	}
	
	public static void grafico(String titulo, String linea1, String linea2, ArrayList<int[]> todos, ArrayList<int[]> mejores) {
		final XYSeries serie1 = new XYSeries(linea1);
		final XYSeries serie2 = new XYSeries(linea2);
		for(int i=0;i<todos.size();i++) {
			serie1.add(i, calculoCosto(todos.get(i)));
		}
		for(int i=0;i<mejores.size();i++) {
			serie2.add(i, calculoCosto(mejores.get(i)));
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
