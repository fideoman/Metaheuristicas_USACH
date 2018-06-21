package cl.usach.mh.busquedatabu;

public class BusquedaTabu4 {

	public static int[][] lugares;
	public static int[][] locales;
	
	public static void main(String[] args) {
		// Búsqueda Tabú
		// Lo más sencillo posible.
		// Grupo de datos QAP de 4x4
		// Solución óptima: 395
		// Lugar A, local 3 - Lugar B, local 4 - Lugar C, local 1 - Lugar D, local 2
		// Solución: {3, 4, 1, 2} {Indice cero: {2, 3, 0, 1}}
		
		// Lugares:
		// 0 22 53 53
		// 22 0 40 62
		// 53 40 0 55
		// 53 62 55 0
		
		// Locales:
		// 0 3 0 2
		// 3 0 0 1
		// 0 0 0 4
		// 2 1 4 0

		// 1) Inicializar las matrices en Java
		lugares = new int[][] { { 0, 22, 53, 53 }, { 22, 0, 40, 62 }, { 53, 40, 0, 55 }, { 53, 62, 55, 0 } };
		locales = new int[][] { { 0, 3, 0, 2 }, { 3, 0, 0, 1 }, { 0, 0, 0, 4 }, { 2, 1, 4, 0 } };

		
	}

	public static int calculoCosto (int[] solucionInicial) {				
        int costo = 0;
        for (int i = 0; i < solucionInicial.length; i++) {
            for (int j = 0; j < solucionInicial.length; j++) { // Recorremos los indices
                costo += locales[i][j] * lugares[solucionInicial[i]][solucionInicial[j]]; 
                // Simplemente tomamos como indice X e Y las evaluaciones del arreglo de la solucion inicial propuesta
            }
        }
        return costo/2; // Elimina los duplicados de la matriz simetrica
	}
	
}
