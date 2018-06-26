package cl.usach.mh.busquedatabu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ArrayUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import cl.usach.mh.busquedatabu.qap.Local;
import cl.usach.mh.busquedatabu.qap.Localidad;

public class BusquedaTabuEjecutable {
		
	public static ArrayList<Local> locales = new ArrayList<Local>();
	public static ArrayList<Localidad> localidades = new ArrayList<Localidad>();	
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		// B�squeda Tab�
		// Lo m�s sencillo posible.
		
		// Par�metros del programa:
		int tenor = 5; // Tenor
		int[] solucionInicial = new int [] {1,8,5,11,2,4,7,10,9,6,12,3}; // Soluci�n Inicial
		int numeroCiclos = 500; // Repeticiones - Ciclos de b�squeda
		
		boolean intercambiosCompletos = false; // Par�metro para activar, o no, una b�squeda en todos los mejores
		int limiteIntercambios = 4; // Si el flag anterior es "false", ac� se puede especificar el l�mite.
		
		boolean diversifico = true; // Par�metro donde especifico si diversifico o no
		int ciclosDiversificacion = 50; // Si el anterior es "Si", se diversificar� en �ste per�odo
		
		boolean intensifico = true; // Parámetro donde especifico su intensifico o no
		int numeroSolucionesSinMejorar = 10; // Si el anterior es "Si", se intensificará cuando hayan 10 soluciones sin mejorar
		int porcentajeIntensificacion = 10; // Porcentaje de cuan agresiva será la intensificación
		// Fin de Par�metros	
		
		cargarDatosQAP("nug12.qap");
		
		// Busqueda Tabu
		BusquedaTabu.ejecucion(locales, localidades, tenor, solucionInicial, numeroCiclos, intercambiosCompletos, limiteIntercambios, diversifico, ciclosDiversificacion, intensifico, numeroSolucionesSinMejorar, porcentajeIntensificacion);
		
		grafico("Tenor: " + tenor + " | Mejor Resultado: " + (BusquedaTabu.calculoCosto(BusquedaTabu.mejorSolucionHistorica) == 578 ? "578 (El mejor del mundo)" : BusquedaTabu.calculoCosto(BusquedaTabu.mejorSolucionHistorica) + " (578 es el mejor del mundo)"), "Todas", "Mejor", BusquedaTabu.solucionesEncontradas, BusquedaTabu.mejoresSolucionesHistoricas);
	}
	
	public static void cargarDatosQAP(String archivoQAPLIB) throws URISyntaxException {
		ClassLoader cargadorContexto = Thread.currentThread().getContextClassLoader();

		ArrayList<ArrayList<Integer>> distancias = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> flujos = new ArrayList<ArrayList<Integer>>();
		int cantidad = 0;
		
		List<Integer> secuenciaBase;
		try {
			Scanner sc = new Scanner(new File(cargadorContexto.getResource(archivoQAPLIB).toURI()));
			if (sc.hasNextInt()) {
				cantidad = sc.nextInt();
			}
			for (int i = 0; i < cantidad; i++) {
				distancias.add(new ArrayList<Integer>());
				for (int j = 0; j < cantidad; j++) {
					distancias.get(i).add(sc.nextInt());
				}
			}
			for (int i = 0; i < cantidad; i++) {
				flujos.add(new ArrayList<Integer>());
				for (int j = 0; j < cantidad; j++) {
					flujos.get(i).add(sc.nextInt());
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		secuenciaBase = IntStream.rangeClosed(1, cantidad).boxed().collect(Collectors.toList());

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
			int[] conjuntoComplemento = new int[cantidad - 1];
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
