package cl.usach.mh.comunes.utilidades.salida;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import cl.usach.mh.comunes.qap.QAP;

public class Salida {
	public static void grafico(String titulo, String linea1, String linea2, ArrayList<int[]> todos, ArrayList<int[]> mejores) {
		final XYSeries serie1 = new XYSeries(linea1);
		final XYSeries serie2 = new XYSeries(linea2);
		for(int i=0;i<todos.size();i++) {
			serie1.add(i, QAP.calculoCosto(todos.get(i)));
		}
		for(int i=0;i<mejores.size();i++) {
			serie2.add(i, QAP.calculoCosto(mejores.get(i)));
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
	
	public static void excel(String archivoSalida, ArrayList<int[]> todos, ArrayList<Integer> todosCostos, ArrayList<Timestamp> todosTimestamp, ArrayList<int[]> mejores, ArrayList<Integer> mejoresCostos, ArrayList<Timestamp> mejoresTimestamp) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet1 = workbook.createSheet("Todas");
        XSSFSheet sheet2 = workbook.createSheet("Mejores");
        
        for (int rowNum = 0; rowNum < todos.size(); rowNum++) {
            Row row = sheet1.createRow(rowNum);
            Cell cell = row.createCell(0);
            cell.setCellValue((String) Arrays.toString(todos.get(rowNum)));
            cell = row.createCell(1);
            cell.setCellValue((Integer) todosCostos.get(rowNum));
            cell = row.createCell(2);            
            cell.setCellValue((Timestamp) todosTimestamp.get(rowNum));
            row = sheet2.createRow(rowNum);
            cell = row.createCell(0);
            cell.setCellValue((String) Arrays.toString(mejores.get(rowNum)));
            cell = row.createCell(1);
            cell.setCellValue((Integer) mejoresCostos.get(rowNum));
            cell = row.createCell(2);
            cell.setCellValue((Timestamp) mejoresTimestamp.get(rowNum));
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(archivoSalida);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
