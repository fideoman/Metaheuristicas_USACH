package cl.usach.mh.ejecutables;

import java.io.IOException;
import java.net.URISyntaxException;

import cl.usach.mh.busquedatabu.BusquedaTabu;
import cl.usach.mh.comunes.qap.QAP;

public class BusquedaTabuEjecutable {
		
	public static void main(String[] args) throws IOException, URISyntaxException {
		// Busqueda Tabu
		// Lo mas sencillo posible.
		
		//QAP.cargarDatosQAP("qap/nug12.qap");
		//QAP.cargarDatosQAP("qap/chr25a.qap");
		QAP.cargarDatosQAP("qap/esc32a.qap");
		//QAP.cargarDatosQAP("qap/esc64a.qap");
		//QAP.cargarDatosQAP("qap/esc128.qap");		
		
		// Parmetros del programa:
		int tenor = 5; // Tenor
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
		int numeroCiclos = 500; // Repeticiones - Ciclos de busqueda
		
		boolean intercambiosCompletos = false; // Parametro para activar, o no, una busqueda en todos los mejores
		int limiteIntercambios = 4; // Si el flag anterior es "false", aca se puede especificar el limite.
		
		boolean diversifico = true; // Parametro donde especifico si diversifico o no
		int ciclosDiversificacion = 50; // Si el anterior es "Si", se diversificara en este periodo
		
		boolean intensifico = true; // Parametro donde especifico si intensifico o no
		int numeroSolucionesSinMejorar = 10; // Si el anterior es "Si", se intensificara cuando hayan 10 soluciones sin mejorar
		int porcentajeIntensificacion = 10; // Porcentaje de cuan agresiva sera la intensificacion
		// Fin de Parametros	
				
		// Busqueda Tabu
		BusquedaTabu.ejecucion(tenor, solucionInicial, numeroCiclos, intercambiosCompletos, limiteIntercambios, diversifico, ciclosDiversificacion, intensifico, numeroSolucionesSinMejorar, porcentajeIntensificacion);
		
		QAP.grafico("Tenor: " + tenor + " | Mejor Resultado: " + QAP.calculoCosto(BusquedaTabu.mejorSolucionHistorica), "Todas", "Mejor", BusquedaTabu.solucionesEncontradas, BusquedaTabu.mejoresSolucionesHistoricas);
	}
}
