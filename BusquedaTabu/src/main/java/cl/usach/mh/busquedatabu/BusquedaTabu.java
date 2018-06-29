package cl.usach.mh.busquedatabu;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.numbers.combinatorics.Combinations;

import cl.usach.mh.comunes.qap.QAP;
import cl.usach.mh.comunes.utilidades.representacionordinal.Operaciones;

public class BusquedaTabu {
			
	public static int[] mejorSolucionHistorica;
	public static int[] mejorSolucionActual;
	public static ArrayList<int[]> solucionesEncontradas = new ArrayList<int[]>();
	public static ArrayList<int[]> mejoresSolucionesHistoricas = new ArrayList<int[]>();
	
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
		
		// 2) Inicializar Lista Tabu (memoria corto plazo), su tenor (tenure), memoria a mediano y largo plazo, con las estructuras mas adecuadas. 
		TreeMap<String, Integer> listaTabu = new TreeMap<String, Integer>();
		ArrayList<TreeMap<Integer, Integer>> memoriaMedianoPlazo = new ArrayList<TreeMap<Integer, Integer>>();
		for(int c = 0; c < solucionInicial.length; c++) {
			TreeMap<Integer, Integer> init = new TreeMap<Integer, Integer>();
			memoriaMedianoPlazo.add(init);
		}
		DualHashBidiMap<String, Integer> memoriaLargoPlazo = new DualHashBidiMap<String, Integer>();		
		
		// 3) Generar todos los pares combinatorios, no repetidos, del universo. 
		ArrayList<int[]> parTemp = new ArrayList<int[]>(); 
		(new Combinations(solucionInicial.length,2)).iterator().forEachRemaining(parTemp::add);
		int [][] pares = parTemp.stream().toArray(int[][]::new);
		for(int i = 0; i < pares.length; i++) for(int j = 0; j < pares[i].length;j++) pares[i][j]++; 
		parTemp = null;
        
		// 4) La solucion inicial es la mejor Solucion historica y actual al comenzar. Es la unica que conocemos.
		mejorSolucionHistorica = solucionInicial;
		mejorSolucionActual = solucionInicial;
		
		for (int numeroIteracion = 1; numeroIteracion <= numeroCiclos; numeroIteracion++) { // Ciclo central
			//System.out.println("Porcentaje de completitud: " + Math.round((((double) numeroIteracion) / ((double) numeroCiclos))*100) + "%");
			
			// 5) Por cada combinacion aplico un intercambio (swap) y almaceno sus evaluaciones y par de intercambio (swap) asociado en un mapa
			// Nota: Se hace el intercambio en una copia. No tocamos solucion actual
			DualHashBidiMap<int[], String> movimientosVecinosEvaluados = new DualHashBidiMap<int[], String>();
			DualHashBidiMap<int[], Integer> costosVecinosEvaluados = new DualHashBidiMap<int[], Integer>();
			for(int[] par : pares) {
				// 5a) Hago el intercambio (swap)
				int[] vecino = Operaciones.intercambio(mejorSolucionActual, mejorSolucionActual[par[0]], mejorSolucionActual[par[1]]);
				movimientosVecinosEvaluados.put(vecino, String.valueOf(par[0])+"-"+String.valueOf(par[1]));
				costosVecinosEvaluados.put(vecino, QAP.calculoCosto(vecino));
			}
			// 6) Ordenemos los vecinos por costo ascendente, dado que estamos buscando los menores
			List<int[]> mejoresVecinos = costosVecinosEvaluados.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(Map.Entry::getKey).collect(Collectors.toList());
			
			// Si vamos a recorrer todos los mejores vecinos, sera el tamanio completo de este arreglo, segun el parametro
			if(intercambiosCompletosFlag) {
				limiteIntercambiosNum = mejoresVecinos.size();
			}
			
			// 7) Recorremos los mejores vecinos.
			for (int i = 0; i < limiteIntercambiosNum; i++) {
				
				if(diversifico) {
					// Guardo en mi memoria de largo plazo el movimiento a procesar
					if(!memoriaLargoPlazo.containsKey(movimientosVecinosEvaluados.get(mejoresVecinos.get(i)))) {
						memoriaLargoPlazo.put(movimientosVecinosEvaluados.get(mejoresVecinos.get(i)), 1);
					} else {
						memoriaLargoPlazo.put(movimientosVecinosEvaluados.get(mejoresVecinos.get(i)), memoriaLargoPlazo.get(movimientosVecinosEvaluados.get(mejoresVecinos.get(i))) + 1);
					}				
				}
				
				// Sera que se genero con un movimiento Prohibido?
				if (listaTabu.containsKey(movimientosVecinosEvaluados.get(mejoresVecinos.get(i)))) {
					// Es un vecino generado por un movimiento prohibido.

					// Ultimo posible aporte del movimiento tabu: Sera que su costo sea menor que
					// el historico?
					if (costosVecinosEvaluados.get(mejoresVecinos.get(i)) < QAP.calculoCosto(mejorSolucionHistorica)) {
						// Bingo! Tenemos una mejor solucion actual e historica.										
						
						if(diversifico && (numeroIteracion % numeroCicloDiversificar == 0)) { // Stop! Debo penalizar la solucion (Diversificacion)
							// Obtengo la suma total de la frecuencia de los movimientos contados
							int totalFrec = memoriaLargoPlazo.values().stream().mapToInt(Number::intValue).sum();
							// Obtengo el valor de la memoria de las frecuencias del movimiento
							int fracMovimiento = memoriaLargoPlazo.get(movimientosVecinosEvaluados.get(mejoresVecinos.get(i)));
							// Obtengo porcentaje de penalizacion
							double porcPenalizacion = fracMovimiento / totalFrec;
							// Se lo aplico al costo actual
							int costoPenalizado = (int) Math.round((costosVecinosEvaluados.get((mejoresVecinos.get(i)))*porcPenalizacion) + costosVecinosEvaluados.get(mejoresVecinos.get(i)));
													
							List<Integer> alAzar = IntStream.range(1, solucionInicial.length+1).boxed().collect(Collectors.toCollection(ArrayList::new));
							Collections.shuffle(alAzar);
							
							int[] costoNuevo = alAzar.stream().mapToInt(Integer::valueOf).toArray();
							// Vamos por esa penalizacion
							while(QAP.calculoCosto(costoNuevo) < costoPenalizado) {
								Collections.shuffle(alAzar);
								costoNuevo = alAzar.stream().mapToInt(Integer::valueOf).toArray();
							}
							// Penalizado.
							mejoresVecinos.set(i, costoNuevo);
						}
						
						mejorSolucionActual = mejoresVecinos.get(i);
						mejorSolucionHistorica = mejoresVecinos.get(i);
											
						if(intensifico) { // Debo intensificar?
							// Memoria de mediano plazo, para intensificar
							// Recorro cada elemento de la solucion (Indice del ArrayList) y aumento por cada elemento del universo (HashMap)
							for (int ii = 1; ii <= solucionInicial.length; ii++) {
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
									acumulado += QAP.calculoCosto(solucionesEncontradas.get(ite));
									if((ite + 1) == solucionesEncontradas.size()) { // Ultimo. Promedio.
										promedio = ((double) acumulado) / ((double)(alg));								
									}
									alg++;
								}
								int costoTemp = QAP.calculoCosto(mejoresSolucionesHistoricas.get((mejoresSolucionesHistoricas.size()-1)));
								// Es poca la diferencia del promedio de los ultimos con el ultimo historico? Si es asi, intensifico.
								if(promedio < ((double)costoTemp*((double)porcentajeIntensificacion/100.0)) + (double)costoTemp && promedio > 0) {
									// Necesito los que mas se repitan, y los que menos. Los intercambio y declaro dicha solucion como nueva solucion actual.
									List<Integer> copiaSolucionActual = Arrays.stream(mejorSolucionActual).boxed().collect(Collectors.toList());
									for(int conta = 0; conta < memoriaMedianoPlazo.size()/2; conta++) {
										// El mas repetido de la posicion n, su indice, y el menos repetido
										int indiceElite = Collections.max(memoriaMedianoPlazo.get(conta).entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
										int indicePobre = Collections.min(memoriaMedianoPlazo.get(conta).entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
										// Swap del indice pobre al elite
										Collections.swap(copiaSolucionActual, copiaSolucionActual.indexOf(indiceElite), copiaSolucionActual.indexOf(indicePobre));
									}			
									// Entreguemos el resultado ofuscado
									mejorSolucionActual = copiaSolucionActual.stream().mapToInt(Integer::valueOf).toArray();;
								}
							}
						}
						
						solucionesEncontradas.add(mejorSolucionActual);
						mejoresSolucionesHistoricas.add(mejorSolucionHistorica);
						
						break; // No necesito mas!
					}
					// Si es prohibido, y no es el mejor historico, voy por el siguiente mejor vecino. 
					continue;					
				} else { // Excelente, no es prohibido. Se acepta como nueva solucion.
					
					// Prohibimos el movimiento, con un tenor dado, a futuro
					listaTabu.put(movimientosVecinosEvaluados.get(mejoresVecinos.get(i)), tenor);
					
					if(diversifico && (numeroIteracion % numeroCicloDiversificar == 0)) { // Stop! Debo penalizar la solucion (Diversificacion)
						// Obtengo la suma total de la frecuencia de los movimientos contados
						float totalFrec = memoriaLargoPlazo.values().stream().mapToInt(Number::intValue).sum();
						// Obtengo el valor de la memoria de las frecuencias del movimiento
						float fracMovimiento = memoriaLargoPlazo.get(movimientosVecinosEvaluados.get(mejoresVecinos.get(i)));
						// Obtengo porcentaje de penalizacion
						float porcPenalizacion = (fracMovimiento / totalFrec);
						// Se lo aplico al costo actual
						int costoPenalizado = (int) Math.round((costosVecinosEvaluados.get((mejoresVecinos.get(i)))*porcPenalizacion) + costosVecinosEvaluados.get(mejoresVecinos.get(i)));
												
						List<Integer> alAzar = IntStream.range(1, solucionInicial.length+1).boxed().collect(Collectors.toCollection(ArrayList::new));
						Collections.shuffle(alAzar);
						
						int[] costoNuevo = alAzar.stream().mapToInt(Integer::valueOf).toArray();
						// Vamos por esa penalizacion
						while(QAP.calculoCosto(costoNuevo) < costoPenalizado) {
							Collections.shuffle(alAzar);
							costoNuevo = alAzar.stream().mapToInt(Integer::valueOf).toArray();
						}
						// Penalizado.
						mejoresVecinos.set(i, costoNuevo);
					}
					
					mejorSolucionActual = mejoresVecinos.get(i); // Es la mejor solucion actual.
					// Sera la mejor histrica?
					if(QAP.calculoCosto(mejoresVecinos.get(i)) < QAP.calculoCosto(mejorSolucionHistorica)) {
						// Si. Enhorabuena.
						mejorSolucionHistorica = mejoresVecinos.get(i);
					}
					
					if(intensifico) { // Debo intensificar?
						// Memoria de mediano plazo, para intensificar
						// Recorro cada elemento de la solucion (Indice del ArrayList) y aumento por cada elemento del universo (HashMap)
						for (int ii = 1; ii <= solucionInicial.length; ii++) {
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
								acumulado += QAP.calculoCosto(solucionesEncontradas.get(ite));
								if((ite + 1) == solucionesEncontradas.size()) { // Ultimo. Promedio.
									promedio = ((double) acumulado) / ((double)(alg));								
								}
								alg++;
							}
							int costoTemp = QAP.calculoCosto(mejoresSolucionesHistoricas.get((mejoresSolucionesHistoricas.size()-1)));
							// Es poca la diferencia del promedio de los ultimos con el ultimo historico? Si es asi, intensifico.
							if(promedio < ((double)costoTemp*((double)porcentajeIntensificacion/100.0)) + (double)costoTemp && promedio > 0) {
								// Necesito los que mas se repitan, y los que menos. Los intercambio y declaro dicha solucion como nueva solucion actual.
								List<Integer> copiaSolucionActual = Arrays.stream(mejorSolucionActual).boxed().collect(Collectors.toList());
								for(int conta = 0; conta < memoriaMedianoPlazo.size()/2; conta++) {
									// El mas repetido de la posicion n, su indice, y el menos
									int indiceElite = Collections.max(memoriaMedianoPlazo.get(conta).entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
									int indicePobre = Collections.min(memoriaMedianoPlazo.get(conta).entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
									// Swap del indice pobre al indice elite
									Collections.swap(copiaSolucionActual, copiaSolucionActual.indexOf(indiceElite), copiaSolucionActual.indexOf(indicePobre));
								}			
								// Entreguemos el resultado ofuscado
								mejorSolucionActual = copiaSolucionActual.stream().mapToInt(Integer::valueOf).toArray();;
							}
						}
					}
					
					solucionesEncontradas.add(mejorSolucionActual);
					mejoresSolucionesHistoricas.add(mejorSolucionHistorica);								
					
					break; // Nada mas que hacer.
				}				
			}
			
			// 8) Finalmente, decrecemos todos los contadores de movimientos prohibidos en uno.
			listaTabu.entrySet().forEach(v -> v.setValue(v.getValue() - 1));
			// Si el tenor del movimiento es cero, lo elimino de la lista tabu
			listaTabu.entrySet().removeIf(entry -> entry.getValue().equals(0));			
		}
	}
}
