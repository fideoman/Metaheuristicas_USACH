package cl.usach.mh.comunes.qap;

import java.util.ArrayList;

import cl.usach.mh.comunes.qap.objetos.Local;
import cl.usach.mh.comunes.qap.objetos.Localidad;	

public class QAP {
	
	private static ArrayList<Local> locales = new ArrayList<Local>();
	private static ArrayList<Localidad> localidades = new ArrayList<Localidad>();
	
	public QAP(ArrayList<Local> locales, ArrayList<Localidad> localidades) {
		QAP.locales = locales;
		QAP.localidades = localidades;		
	}
	
	public static int calculoCosto(int[] solucionInicial) {				
        int costo = 0;
		for (int i = 0; i < solucionInicial.length; i++) {
			for (int j = 0; j < QAP.locales.size(); j++) {
				if (String.valueOf(solucionInicial[i]).equals(QAP.locales.get(j).etiqueta)) {
					int m = 0;
					for (int l = 0; l < solucionInicial.length; l++) {
						for (int o = 0; o < QAP.locales.get(j).conjuntoComplemento.length; o++) {
							if (solucionInicial[l] == QAP.locales.get(j).conjuntoComplemento[o]) {
								costo = costo
										+ QAP.locales.get(j).flujos[o]
										* QAP.localidades.get(i).distancias[m];
								m++;
							}
						}
					}
				}
			}
		}	
        return costo; 
	}

	public static ArrayList<Local> getLocales() {
		return QAP.locales;
	}

	public static void setLocales(ArrayList<Local> locales) {
		QAP.locales = locales;
	}

	public static ArrayList<Localidad> getLocalidades() {
		return QAP.localidades;
	}

	public static void setLocalidades(ArrayList<Localidad> localidades) {
		QAP.localidades = localidades;
	}
}
