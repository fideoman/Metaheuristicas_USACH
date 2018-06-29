package cl.usach.mh.comunes.utilidades.representacionordinal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Operaciones {

	public static int[] intercambio(int[] entrada, int local1, int local2) {
		List<Integer> copiaSolucionActual = Arrays.stream(entrada).boxed().collect(Collectors.toList());
		Collections.swap(copiaSolucionActual, copiaSolucionActual.indexOf(local1), copiaSolucionActual.indexOf(local2));
		return copiaSolucionActual.stream().mapToInt(Integer::valueOf).toArray();
	}
	
	public static int[] intercambioAlAzar(int[] entrada) {
		List<Integer> alAzar = Arrays.stream(entrada).boxed().collect(Collectors.toList());
		Collections.shuffle(alAzar);
		return alAzar.stream().mapToInt(Integer::valueOf).toArray();
	}
	
	public static int[] solucionAlAzar(int largoSolucion) {
		List<Integer> alAzar = IntStream.range(1, largoSolucion+1).boxed().collect(Collectors.toCollection(ArrayList::new));
		Collections.shuffle(alAzar);
		return alAzar.stream().mapToInt(Integer::valueOf).toArray();
	}	
}
