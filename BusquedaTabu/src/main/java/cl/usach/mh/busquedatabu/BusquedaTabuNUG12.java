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

public class BusquedaTabuNUG12 {
	
	public static final List<Integer> secuencia = Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12); // Secuencia básica fija (12)
	
	public static ArrayList<Local> locales = new ArrayList<Local>();
	public static ArrayList<Localidad> localidades = new ArrayList<Localidad>();	
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		// Búsqueda Tabú
		// Lo más sencillo posible.
		
		// Parámetros del programa:
		int tenor = 5; // Tenor
		int[] solucionInicial = new int [] {1,8,5,11,2,4,7,10,9,6,12,3}; // Solución Inicial
		int numeroCiclos = 500; // Repeticiones - Ciclos de búsqueda
		// Fin de Parámetros	
		
		cargarDatosQAPNUG12();
		
		// Busqueda Tabu
		BusquedaTabu.ejecucion(BusquedaTabuNUG12.locales, BusquedaTabuNUG12.localidades, tenor, solucionInicial, numeroCiclos);
		
		grafico("Tenor: " + tenor + " | Mejor Resultado: " + (BusquedaTabu.calculoCosto(BusquedaTabu.mejorSolucionHistorica) == 578 ? "578 (El mejor del mundo)" : BusquedaTabu.calculoCosto(BusquedaTabu.mejorSolucionHistorica) + " (578 es el mejor del mundo)"), "Todas", "Mejor", BusquedaTabu.solucionesEncontradas, BusquedaTabu.mejoresSolucionesHistoricas);
	}
	
	public static void cargarDatosQAPNUG12() throws IOException, URISyntaxException {
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
			serie1.add(i, BusquedaTabu.calculoCosto(todos.get(i)));
		}
		for(int i=0;i<mejores.size();i++) {
			serie2.add(i, BusquedaTabu.calculoCosto(mejores.get(i)));
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
