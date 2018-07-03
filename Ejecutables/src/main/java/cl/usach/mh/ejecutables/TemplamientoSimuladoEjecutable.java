package cl.usach.mh.ejecutables;

import java.io.IOException;
import java.net.URISyntaxException;

import cl.usach.mh.comunes.qap.QAP;
import cl.usach.mh.comunes.utilidades.salida.Salida;
import cl.usach.mh.templamientosimulado.TemplamientoSimulado;

public class TemplamientoSimuladoEjecutable {
		
	public static void main(String[] args) throws IOException, URISyntaxException {
		// Templamiento Simulado
		// Lo mas sencillo posible.
		
		//QAP.cargarDatosQAP("qap/nug12.qap");
		//QAP.cargarDatosQAP("qap/chr25a.qap");
		QAP.cargarDatosQAP("qap/kra30a.qap");
		//QAP.cargarDatosQAP("qap/esc64a.qap");
		//QAP.cargarDatosQAP("qap/esc128.qap");		
		
		// Parmetros del programa:
		double temperaturaInicial = 10000; // Temperatura Inicial
		int porcentajeTasaDescenso = 5; // Porcentaje de tasa de descenso, en entero. Ej: 1 -> 1%
		// De 12:
		//int[] solucionInicial = new int[] {8, 5, 6, 1, 7, 12, 4, 3, 11, 10, 9, 2};
		// De 25:
		//int[] solucionInicial = new int[] {3, 8, 9, 13, 17, 6, 11, 7, 12, 18, 5, 19, 2, 16, 25, 15, 24, 4, 22, 10, 14, 23, 1, 20, 21};
		// De 30:
		int[] solucionInicial = new int[] {12, 18, 5, 26, 13, 11, 9, 21, 24, 20, 19, 14, 8, 15, 23, 3, 30, 17, 22, 16, 29, 7, 6, 4, 27, 25, 10, 1, 2, 28};
		// De 64:
		//int[] solucionInicial = new int[] {44, 34, 29, 61, 15, 1, 19, 36, 46, 43, 16, 54, 28, 56, 33, 40, 63, 7, 17, 14, 26, 22, 32, 23, 37, 2, 58, 48, 24, 41, 5, 6, 64, 4, 31, 25, 47, 10, 49, 21, 53, 52, 8, 35, 3, 20, 30, 11, 59, 60, 39, 55, 27, 57, 13, 12, 18, 42, 45, 62, 50, 38, 9, 51};
		// De 128:
		//int[] solucionInicial = new int[] {48, 11, 21, 40, 20, 82, 108, 128, 75, 56, 5, 15, 124, 46, 55, 63, 64, 106, 26, 4, 34, 86, 116, 57, 18, 93, 84, 9, 50, 90, 7, 94, 33, 85, 87, 114, 22, 1, 31, 52, 97, 112, 27, 41, 6, 78, 121, 12, 118, 110, 74, 61, 127, 99, 45, 95, 10, 123, 117, 72, 35, 2, 100, 69, 19, 16, 17, 76, 113, 66, 44, 71, 96, 59, 77, 92, 67, 105, 88, 107, 102, 73, 8, 83, 81, 120, 65, 111, 28, 91, 29, 23, 80, 38, 42, 54, 47, 109, 70, 98, 37, 14, 30, 125, 3, 39, 62, 122, 32, 25, 68, 53, 104, 79, 103, 36, 89, 58, 126, 13, 101, 115, 43, 119, 51, 49, 24, 60};
		int epocas = 10000; // Epocas - Ciclos de busqueda
		// Fin de Parametros	
				
		// Templamiento Simulado
		TemplamientoSimulado.ejecucion(temperaturaInicial, porcentajeTasaDescenso, solucionInicial, epocas);
		
		Salida.grafico("T ini: " + temperaturaInicial + " | %Des: " + porcentajeTasaDescenso + "% | Mejor Resultado: " + QAP.calculoCosto(TemplamientoSimulado.mejorSolucionHistorica), "Todas", "Mejor", TemplamientoSimulado.solucionesEncontradas, TemplamientoSimulado.mejoresSolucionesHistoricas);
	}
}
