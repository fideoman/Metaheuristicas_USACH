package cl.usach.mh.busquedatabu;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import cl.usach.mh.comunes.qap.QAP;
import cl.usach.mh.comunes.utilidades.representacionordinal.Operaciones;

public class BusquedaTabu {
			
	public static int[] mejorSolucionHistorica;
	public static int[] mejorSolucionActual;
	public static ArrayList<int[]> solucionesEncontradas = new ArrayList<int[]>();
	public static ArrayList<Integer> costosEncontrados = new ArrayList<Integer>();
	public static ArrayList<int[]> mejoresSolucionesHistoricas = new ArrayList<int[]>();
	public static ArrayList<Integer> mejoresCostosEncontrados = new ArrayList<Integer>();
	
	public static ArrayList<Timestamp> solucionesEncontradasTimestamp = new ArrayList<Timestamp>();
	public static ArrayList<Timestamp> mejoresSolucionesHistoricasTimestamp = new ArrayList<Timestamp>();
	
	private static TreeMap<String, Integer> listaTabu;
	private static ArrayList<TreeMap<Integer, Integer>> memoriaMedianoPlazo;
	private static DualHashBidiMap<String, Integer> memoriaLargoPlazo;
	
	private static DualHashBidiMap<int[], String> movimientosVecinosEvaluados;
	private static DualHashBidiMap<int[], Integer> costosVecinosEvaluados;
	private static List<int[]> mejoresVecinos;
	
	public static void ejecucion(int tenorExt, int[] solucionInicialExt, int numeroCiclosExt, boolean intercambiosCompletos, int limiteIntercambios, boolean diversif, int numCicloDiversif, boolean intensif, int numSolSinMej, int porcInt) throws IOException, URISyntaxException {
		// Busqueda Tabu
		// Lo mas sencillo posible.
		
		// 1) Cargar los datos de distancia y flujos en listas estaticas, listas para calculos, ademas de parametros
		int tenor = tenorExt; // Tenor
		int[] solucionInicial = solucionInicialExt; // Solucion Inicial
		int numeroCiclos = numeroCiclosExt; // Repeticiones - Ciclos de busqueda
		
		boolean intercambiosCompletosFlag = intercambiosCompletos; // Parametro para activar, o no, una busqueda en todos los mejores
		int limiteIntercambiosNum = limiteIntercambios; // Si el flag anterior es "false", aca se puede especificar el limite.
		
		boolean diversifico = diversif; // Parametro para diversificar
		int numeroCicloDiversificar = numCicloDiversif; // Cada cuanto diversificar
		
		boolean intensifico = intensif; // Parametro para intensificar
		int solucionesSinMejorar = numSolSinMej; // Cuantas soluciones vamos a evaluar para gatillar la diversificacion
		int porcentajeIntensificacion = porcInt; // De 1 a 100. Cuan agresivo sera la intensificacion
		// Fin de Parametros
		
		// Guardemos nuestras soluciones. Guardaremos: Soluciones encontradas, y mejor Solucion historica en X iteracion
		solucionesEncontradas = new ArrayList<int[]>();
		mejoresSolucionesHistoricas = new ArrayList<int[]>();
		costosEncontrados = new ArrayList<Integer>();
		mejoresCostosEncontrados = new ArrayList<Integer>();
		solucionesEncontradasTimestamp = new ArrayList<Timestamp>();
		mejoresSolucionesHistoricasTimestamp = new ArrayList<Timestamp>();
		
		// 2) Inicializar Lista Tabu (memoria corto plazo), su tenor (tenure), memoria a mediano y largo plazo, con las estructuras mas adecuadas. 
		listaTabu = new TreeMap<String, Integer>();
		memoriaMedianoPlazo = new ArrayList<TreeMap<Integer, Integer>>();
		TreeMap<Integer, Integer> init;
		for(int c = 0; c < QAP.getCantidad(); c++) {
			init = new TreeMap<Integer, Integer>();
			memoriaMedianoPlazo.add(init);
		}
		init = null;
		memoriaLargoPlazo = new DualHashBidiMap<String, Integer>();		
        
		// 4) La solucion inicial es la mejor Solucion historica y actual al comenzar. Es la unica que conocemos.
		mejorSolucionHistorica = solucionInicial;
		mejorSolucionActual = solucionInicial;
		
		for (int numeroIteracion = 1; numeroIteracion <= numeroCiclos; numeroIteracion++) { // Ciclo central
			//System.out.println("Iteracion: " + numeroIteracion);
			
			// 5) Por cada combinacion aplico un intercambio (swap) y almaceno sus evaluaciones y par de intercambio (swap) asociado en un mapa
			// Nota: Se hace el intercambio en una copia. No tocamos solucion actual
			movimientosVecinosEvaluados = new DualHashBidiMap<int[], String>();
			costosVecinosEvaluados = new DualHashBidiMap<int[], Integer>();
			for(int[] par : QAP.paresUnicosSolucion) {
				// 5a) Hago el intercambio (swap)
				int[] vecino = Operaciones.intercambio(mejorSolucionActual, par[0], par[1]);
				movimientosVecinosEvaluados.put(vecino, String.valueOf(par[0])+"-"+String.valueOf(par[1]));
				costosVecinosEvaluados.put(vecino, QAP.calculoCosto(vecino));
			}
			// 6) Ordenemos los vecinos por costo ascendente, dado que estamos buscando los menores
			mejoresVecinos = costosVecinosEvaluados.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(Map.Entry::getKey).collect(Collectors.toList());
			
			// Si vamos a recorrer todos los mejores vecinos, sera el tamanio completo de este arreglo, segun el parametro
			if(intercambiosCompletosFlag) {
				limiteIntercambiosNum = mejoresVecinos.size();
			}
			
			// 7) Recorremos los mejores vecinos.
			for (int i = 0; i < limiteIntercambiosNum; i++) {
				
				if(diversifico) {
					// Guardo en mi memoria de largo plazo el movimiento a procesar
					actualizarDatosMemoriaLargoPlazo(mejoresVecinos.get(i));
				}
				
				// Sera que se genero con un movimiento Prohibido?
				if (listaTabu.containsKey(movimientosVecinosEvaluados.get(mejoresVecinos.get(i)))) {
					// Es un vecino generado por un movimiento prohibido.

					// Ultimo posible aporte del movimiento tabu: Sera que su costo sea menor que
					// el historico?
					if (costosVecinosEvaluados.get(mejoresVecinos.get(i)) < QAP.calculoCosto(mejorSolucionHistorica)) {
						// Bingo! Tenemos una mejor solucion actual e historica.										
						
						if(diversifico && (numeroIteracion % numeroCicloDiversificar == 0)) { // Stop! Debo penalizar la solucion (Diversificacion)
							// Penalizado.
							mejoresVecinos.set(i, diversificar(mejoresVecinos.get(i)));
						}
						
						mejorSolucionActual = mejoresVecinos.get(i);
						mejorSolucionHistorica = mejoresVecinos.get(i);
											
						if(intensifico) { // Debo intensificar?
							mejorSolucionActual = intensificar(solucionesSinMejorar, porcentajeIntensificacion);
						}
						
						// Registro de soluciones.
						solucionesEncontradas.add(mejorSolucionActual);
						costosEncontrados.add(QAP.calculoCosto(mejorSolucionActual));
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
						
						break; // No necesito mas!
					}
					// Si es prohibido, y no es el mejor historico, voy por el siguiente mejor vecino. 
					continue;					
				} else { // Excelente, no es prohibido. Se acepta como nueva solucion.
					
					// Prohibimos el movimiento, con un tenor dado, a futuro
					listaTabu.put(movimientosVecinosEvaluados.get(mejoresVecinos.get(i)), tenor);
					
					if(diversifico && (numeroIteracion % numeroCicloDiversificar == 0)) { // Stop! Debo penalizar la solucion (Diversificacion)
						mejoresVecinos.set(i, diversificar(mejoresVecinos.get(i)));
					}
					
					mejorSolucionActual = mejoresVecinos.get(i); // Es la mejor solucion actual.
					// Sera la mejor historica?
					if(QAP.calculoCosto(mejoresVecinos.get(i)) < QAP.calculoCosto(mejorSolucionHistorica)) {
						// Si. Enhorabuena.
						mejorSolucionHistorica = mejoresVecinos.get(i);
					}
					
					if(intensifico) { // Debo intensificar?
						mejorSolucionActual = intensificar(solucionesSinMejorar, porcentajeIntensificacion);
					}
					
					// Registro de soluciones.
					solucionesEncontradas.add(mejorSolucionActual);
					costosEncontrados.add(QAP.calculoCosto(mejorSolucionActual));
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
					
					break; // Nada mas que hacer.
				}				
			}
			
			// 8) Finalmente, decrecemos todos los contadores de movimientos prohibidos en uno.
			listaTabu.entrySet().forEach(v -> v.setValue(v.getValue() - 1));
			// Si el tenor del movimiento es cero, lo elimino de la lista tabu
			listaTabu.entrySet().removeIf(entry -> entry.getValue().equals(0));			
		}
	}
	
	private static int[] diversificar(int[] solucion) {
		// Obtengo la suma total de la frecuencia de los movimientos contados
		int totalFrec = memoriaLargoPlazo.values().stream().mapToInt(Number::intValue).sum();
		// Obtengo el valor de la memoria de las frecuencias del movimiento
		int fracMovimiento = memoriaLargoPlazo.get(movimientosVecinosEvaluados.get(solucion));
		// Obtengo porcentaje de penalizacion
		double porcPenalizacion = (double) fracMovimiento / (double) totalFrec;
		// Se lo aplico al costo actual
		int costoPenalizado = (int) Math.round(((double)(costosVecinosEvaluados.get(solucion)))*porcPenalizacion + ((double)costosVecinosEvaluados.get(solucion)));
		
		// Sacaremos una solucion nueva al azar, para penalizar
		int[] solucionNueva = Operaciones.solucionAlAzar(QAP.getCantidad());
		
		// Vamos por esa penalizacion
		int contador = 1;
		while(QAP.calculoCosto(solucionNueva) < costoPenalizado) {
			solucionNueva = Operaciones.solucionAlAzar(QAP.getCantidad()); // Aseguremos que tomaremos una solucion mas mala que la penalizacion
			if(contador == 50) { // El costo penalizado es MUY malo. No perderemos mas ciclos: La solucion nueva será la más mala historica.
				solucionNueva = mejoresSolucionesHistoricas.get(0);
				break;
			}
			contador++;
		}
		return solucionNueva;		
	}
	
	private static void actualizarDatosMemoriaLargoPlazo(int[] solucion) {
		if(!memoriaLargoPlazo.containsKey(movimientosVecinosEvaluados.get(solucion))) {
			memoriaLargoPlazo.put(movimientosVecinosEvaluados.get(solucion), 1);
		} else {
			memoriaLargoPlazo.put(movimientosVecinosEvaluados.get(solucion), memoriaLargoPlazo.get(movimientosVecinosEvaluados.get(solucion)) + 1);
		}		
	}
	
	private static int[] intensificar(int solucionesSinMejorar, int porcentajeIntensificacion) {
		int[] solucionIntensificada = mejorSolucionActual;
		// Memoria de mediano plazo, para intensificar
		// Recorro cada elemento de la solucion (Indice del ArrayList) y aumento por cada elemento del universo (HashMap)
		for (int ii = 1; ii <= QAP.getCantidad(); ii++) {
			for(int iii = 0; iii < mejorSolucionActual.length; iii++) {
				if(ii == mejorSolucionActual[iii]) {
					if(memoriaMedianoPlazo.get(iii).containsKey(ii)) {
						memoriaMedianoPlazo.get(iii).put(ii, memoriaMedianoPlazo.get(iii).get(ii) + 1);
					} else {
						memoriaMedianoPlazo.get(iii).put(ii, 1);
					}
				}								
			}
		}
		
		if(mejoresSolucionesHistoricas.size()>0) {
			
			double promedio = 0.0;
			double acumulado = 0.0;
			int alg = 1;
			for(int ite = solucionesSinMejorar; ite < solucionesEncontradas.size(); ite++) { // Tomemos las ultimas soluciones iteradas
				acumulado += (double) QAP.calculoCosto(solucionesEncontradas.get(ite));
				if((ite + 1) == solucionesEncontradas.size()) { // Ultimo. Promedio.
					promedio = ((double) acumulado) / ((double)(alg));								
				}
				alg++;
			}
			int costoTemp = QAP.calculoCosto(mejoresSolucionesHistoricas.get((mejoresSolucionesHistoricas.size()-1)));
			// Es poca la diferencia del promedio de los ultimos con el ultimo historico? Si es asi, intensifico.
			if(promedio < ((double)costoTemp*((double)porcentajeIntensificacion/100.0)) + (double)costoTemp && promedio > 0) {
				// Necesito los que mas se repitan, y los que menos. Los intercambio y declaro dicha solucion como nueva solucion actual.
				for(int conta = 0; conta < memoriaMedianoPlazo.size()/2; conta++) {
					// El mas y menos repetido de la posicion n
					int elementoElite = Collections.max(memoriaMedianoPlazo.get(conta).entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
					int elementoPobre = Collections.min(memoriaMedianoPlazo.get(conta).entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
					// Swap del indice pobre al indice elite
					solucionIntensificada = Operaciones.intercambio(mejorSolucionActual, elementoElite, elementoPobre);
				}
			}
		}
		return solucionIntensificada;
	}
	
}
