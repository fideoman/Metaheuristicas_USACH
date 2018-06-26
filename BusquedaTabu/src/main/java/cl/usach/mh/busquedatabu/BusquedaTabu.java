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

import cl.usach.mh.busquedatabu.qap.Local;
import cl.usach.mh.busquedatabu.qap.Localidad;

public class BusquedaTabu {
		
	public static ArrayList<Local> locales = new ArrayList<Local>();
	public static ArrayList<Localidad> localidades = new ArrayList<Localidad>();	
	
	public static int[] mejorSolucionHistorica;
	public static int[] mejorSolucionActual;
	public static ArrayList<int[]> solucionesEncontradas = new ArrayList<int[]>();
	public static ArrayList<int[]> mejoresSolucionesHistoricas = new ArrayList<int[]>();
	
	public static void ejecucion(ArrayList<Local> localesExt, ArrayList<Localidad> localidadesExt, int tenorExt, int[] solucionInicialExt, int numeroCiclosExt, boolean intercambiosCompletos, int limiteIntercambios, boolean diversif, int numCicloDiversif, boolean intensif, int numSolSinMej, int porcInt) throws IOException, URISyntaxException {
		// B�squeda Tab�
		// Lo m�s sencillo posible.
		
		// 1) Cargar los datos de distancia y flujos en listas est�ticas, listas para c�lculos, adem�s de par�metros
		locales = localesExt; 
		localidades = localidadesExt;
		int tenor = tenorExt; // Tenor
		int[] solucionInicial = solucionInicialExt; // Soluci�n Inicial
		int numeroCiclos = numeroCiclosExt; // Repeticiones - Ciclos de b�squeda
		
		boolean intercambiosCompletosFlag = intercambiosCompletos; // Par�metro para activar, o no, una b�squeda en todos los mejores
		int limiteIntercambiosNum = limiteIntercambios; // Si el flag anterior es "false", ac� se puede especificar el l�mite.
		
		boolean diversifico = diversif; // Par�metro para diversificar
		int numeroCicloDiversificar = numCicloDiversif; // Cada cuanto diversificar
		
		boolean intensifico = intensif; // Parámetro para intensificar
		int solucionesSinMejorar = numSolSinMej; // Cuantas soluciones vamos a evaluar para gatillar la diversificación
		int porcentajeIntensificacion = porcInt; // De 1 a 100. Cuan agresivo será la intensificación
		// Fin de Par�metros
		
		// Guardemos nuestras soluciones. Guardaremos: Soluciones encontradas, y mejor Soluci�n hist�rica en X iteraci�n
		solucionesEncontradas = new ArrayList<int[]>();
		mejoresSolucionesHistoricas = new ArrayList<int[]>();
		
		// 2) Inicializar Lista Tab� (memoria corto plazo), su tenor (tenure), memoria a mediano y largo plazo, con las estructuras m�s adecuadas. 
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
        
		// 4) La soluci�n inicial es la mejor Soluci�n hist�rica y actual al comenzar. Es la �nica que conocemos.
		mejorSolucionHistorica = solucionInicial;
		mejorSolucionActual = solucionInicial;
		
		for (int numeroIteracion = 1; numeroIteracion <= numeroCiclos; numeroIteracion++) { // Ciclo central
			//System.out.println("Porcentaje de completitud: " + Math.round((((double) numeroIteracion) / ((double) numeroCiclos))*100) + "%");
			
			// 5) Por cada combinaci�n aplico un intercambio (swap) y almaceno sus evaluaciones y par de intercambio (swap) asociado en un mapa
			// Nota: Se hace el intercambio en una copia. No tocamos soluci�n actual
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
			
			// Si vamos a recorrer todos los mejores vecinos, ser� el tama�o completo de �ste arreglo, seg�n el par�metro
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
				
				// �Ser� que se gener� con un movimiento Prohibido?
				if (listaTabu.containsKey(movimientosVecinosEvaluados.get(mejoresVecinos.get(i)))) {
					// Es un vecino generado por un movimiento prohibido.

					// Ultimo posible aporte del movimiento tab�: �Ser� que su costo sea menor que
					// el hist�rico?
					if (costosVecinosEvaluados.get(mejoresVecinos.get(i)) < calculoCosto(mejorSolucionHistorica)) {
						// Bingo! Tenemos una mejor soluci�n actual e hist�rica.										
						
						if(diversifico && (numeroIteracion % numeroCicloDiversificar == 0)) { // Stop! Debo penalizar la soluci�n (Diversificaci�n)
							// Obtengo la suma total de la frecuencia de los movimientos contados
							int totalFrec = memoriaLargoPlazo.values().stream().mapToInt(Number::intValue).sum();
							// Obtengo el valor de la memoria de las frecuencias del movimiento
							int fracMovimiento = memoriaLargoPlazo.get(movimientosVecinosEvaluados.get(mejoresVecinos.get(i)));
							// Obtengo porcentaje de penalizaci�n
							double porcPenalizacion = fracMovimiento / totalFrec;
							// Se lo aplico al costo actual
							int costoPenalizado = (int) Math.round((costosVecinosEvaluados.get((mejoresVecinos.get(i)))*porcPenalizacion) + costosVecinosEvaluados.get(mejoresVecinos.get(i)));
													
							List<Integer> alAzar = IntStream.range(1, solucionInicial.length+1).boxed().collect(Collectors.toCollection(ArrayList::new));
							Collections.shuffle(alAzar);
							
							int[] costoNuevo = alAzar.stream().mapToInt(Integer::valueOf).toArray();
							//Vamos por esa penalizaci�n
							while(calculoCosto(costoNuevo) < costoPenalizado) {
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
							// Recorro cada elemento de la solución (Indice del ArrayList) y aumento por cada elemento del universo (HashMap)
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
								for(int ite = solucionesSinMejorar; ite < solucionesEncontradas.size(); ite++) { // Tomemos las últimas soluciones iteradas
									acumulado += calculoCosto(solucionesEncontradas.get(ite));
									if((ite + 1) == solucionesEncontradas.size()) { // Ultimo. Promedio.
										promedio = ((double) acumulado) / ((double)(alg));								
									}
									alg++;
								}
								int costoTemp = calculoCosto(mejoresSolucionesHistoricas.get((mejoresSolucionesHistoricas.size()-1)));
								// ¿Es poca la diferencia del promedio de los últimos con el último histórico? Si es así, intensifico.
								if(promedio < ((double)costoTemp*((double)porcentajeIntensificacion/100.0)) + (double)costoTemp && promedio > 0) {
									// Necesito los que más se repitan, y los que menos. Los intercambio y declaro dicha solución como nueva solución actual.
									List<Integer> copiaSolucionActual = Arrays.stream(mejorSolucionActual).boxed().collect(Collectors.toList());
									for(int conta = 0; conta < memoriaMedianoPlazo.size()/2; conta++) {
										// El más repetido de la posición n, su indice, y el menos
										int indiceElite = Collections.max(memoriaMedianoPlazo.get(conta).entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
										int indicePobre = Collections.min(memoriaMedianoPlazo.get(conta).entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
										// Swap!
										Collections.swap(copiaSolucionActual, copiaSolucionActual.indexOf(indiceElite), copiaSolucionActual.indexOf(indicePobre));
									}			
									// Entreguemos el resultado ofuscado
									mejorSolucionActual = copiaSolucionActual.stream().mapToInt(Integer::valueOf).toArray();;
								}
							}
						}
						
						solucionesEncontradas.add(mejorSolucionActual);
						mejoresSolucionesHistoricas.add(mejorSolucionHistorica);
						
						break; // �No necesito m�s!
					}
					// Si es prohibido, y no es el mejor hist�rico, voy por el siguiente mejor vecino. 
					continue;					
				} else { // Excelente, no es prohibido. Se acepta como nueva soluci�n.
					
					// Prohibimos el movimiento, con un tenor dado, a futuro
					listaTabu.put(movimientosVecinosEvaluados.get(mejoresVecinos.get(i)), tenor);
					
					if(diversifico && (numeroIteracion % numeroCicloDiversificar == 0)) { // Stop! Debo penalizar la soluci�n (Diversificaci�n)
						// Obtengo la suma total de la frecuencia de los movimientos contados
						float totalFrec = memoriaLargoPlazo.values().stream().mapToInt(Number::intValue).sum();
						// Obtengo el valor de la memoria de las frecuencias del movimiento
						float fracMovimiento = memoriaLargoPlazo.get(movimientosVecinosEvaluados.get(mejoresVecinos.get(i)));
						// Obtengo porcentaje de penalizaci�n
						float porcPenalizacion = (fracMovimiento / totalFrec);
						// Se lo aplico al costo actual
						int costoPenalizado = (int) Math.round((costosVecinosEvaluados.get((mejoresVecinos.get(i)))*porcPenalizacion) + costosVecinosEvaluados.get(mejoresVecinos.get(i)));
												
						List<Integer> alAzar = IntStream.range(1, solucionInicial.length+1).boxed().collect(Collectors.toCollection(ArrayList::new));
						Collections.shuffle(alAzar);
						
						int[] costoNuevo = alAzar.stream().mapToInt(Integer::valueOf).toArray();
						//Vamos por esa penalizaci�n
						while(calculoCosto(costoNuevo) < costoPenalizado) {
							Collections.shuffle(alAzar);
							costoNuevo = alAzar.stream().mapToInt(Integer::valueOf).toArray();
						}
						// Penalizado.
						mejoresVecinos.set(i, costoNuevo);
					}
					
					mejorSolucionActual = mejoresVecinos.get(i); // Es la mejor soluci�n actual.
					// �Ser� la mejor hist�rica?
					if(calculoCosto(mejoresVecinos.get(i)) < calculoCosto(mejorSolucionHistorica)) {
						// Si. Enhorabuena.
						mejorSolucionHistorica = mejoresVecinos.get(i);
					}
					
					if(intensifico) { // Debo intensificar?
						// Memoria de mediano plazo, para intensificar
						// Recorro cada elemento de la solución (Indice del ArrayList) y aumento por cada elemento del universo (HashMap)
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
							for(int ite = solucionesSinMejorar; ite < solucionesEncontradas.size(); ite++) { // Tomemos las últimas soluciones iteradas
								acumulado += calculoCosto(solucionesEncontradas.get(ite));
								if((ite + 1) == solucionesEncontradas.size()) { // Ultimo. Promedio.
									promedio = ((double) acumulado) / ((double)(alg));								
								}
								alg++;
							}
							int costoTemp = calculoCosto(mejoresSolucionesHistoricas.get((mejoresSolucionesHistoricas.size()-1)));
							// ¿Es poca la diferencia del promedio de los últimos con el último histórico? Si es así, intensifico.
							if(promedio < ((double)costoTemp*((double)porcentajeIntensificacion/100.0)) + (double)costoTemp && promedio > 0) {
								// Necesito los que más se repitan, y los que menos. Los intercambio y declaro dicha solución como nueva solución actual.
								List<Integer> copiaSolucionActual = Arrays.stream(mejorSolucionActual).boxed().collect(Collectors.toList());
								for(int conta = 0; conta < memoriaMedianoPlazo.size()/2; conta++) {
									// El más repetido de la posición n, su indice, y el menos
									int indiceElite = Collections.max(memoriaMedianoPlazo.get(conta).entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
									int indicePobre = Collections.min(memoriaMedianoPlazo.get(conta).entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
									// Swap!
									Collections.swap(copiaSolucionActual, copiaSolucionActual.indexOf(indiceElite), copiaSolucionActual.indexOf(indicePobre));
								}			
								// Entreguemos el resultado ofuscado
								mejorSolucionActual = copiaSolucionActual.stream().mapToInt(Integer::valueOf).toArray();;
							}
						}
					}
					
					solucionesEncontradas.add(mejorSolucionActual);
					mejoresSolucionesHistoricas.add(mejorSolucionHistorica);								
					
					break; // Nada m�s que hacer.
				}				
			}
			
			// 8) Finalmente, decrecemos todos los contadores de movimientos prohibidos en uno.
			listaTabu.entrySet().forEach(v -> v.setValue(v.getValue() - 1));
			// Si el tenor del movimiento es cero, lo elimino de la lista tab�
			listaTabu.entrySet().removeIf(entry -> entry.getValue().equals(0));			
		}
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
}
