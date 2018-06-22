package cl.usach.mh.busquedatabu;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import cl.usach.mh.busquedatabu.qap.Local;
import cl.usach.mh.busquedatabu.qap.Localidad;

public class BusquedaTabu12 {
	
	public static ArrayList<Local> locales = new ArrayList<Local>();
	public static ArrayList<Localidad> localidades = new ArrayList<Localidad>();

	public static void main(String[] args) throws IOException, URISyntaxException {
		// Búsqueda Tabú
		// Lo más sencillo posible.
		// Grupo de datos QAP de 12x12 (NUG12, QAPLIB)
		// Solución óptima: (12, 7, 9, 3, 4, 8, 11, 1, 5, 6, 10, 2), Costo: 578

		// 1) Cargar los datos de distancia y flujos en listas estáticas, listas para cálculos
		cargarDatosQAP();
		
        int[] solucionPropuesta = new int [] {12,7,9,3,4,8,11,1,5,6,10,2};
			
		System.out.println(calculoCosto(solucionPropuesta));
		
	}

	public static int calculoCosto (int[] solucionInicial) {				
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
		
        List<Integer> secuencia = Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12);
        
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
}
