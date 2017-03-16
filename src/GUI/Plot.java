/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.awt.Color;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Escritorio
 */
public final class Plot extends JPanel
{
    private final JFreeChart chart;
    private final XYSeriesCollection dataPlot;
    private final XYSeries faseR,faseS,faseT;
    private final int senoLength = 200;
    
    public Plot()
    {
        this(true,50.0,0);
    }
    public Plot(boolean monofasico,double frec,double disparo)
    {
        dataPlot = new XYSeriesCollection();

        dataPlot.addSeries(CreateCursor("Disparo",Deg2Time(disparo,frec),frec));
        
        faseR = CreateSerie("Fase R",frec);
        dataPlot.addSeries(faseR);
        if( monofasico == false )
        {
            faseS = CreateSerie("Fase S",frec);
            faseT = CreateSerie("Fase T",frec);
            dataPlot.addSeries(faseS);
            dataPlot.addSeries(faseT);
        }
        else
        {
            faseS = null;
            faseT = null;
        }
        chart = ChartFactory.createXYLineChart(null,"Tiempo [Segundos]","Amplitud Normalizada [Volts]",dataPlot);
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.BLACK);
        add("Ondas",new ChartPanel(chart));
    }
    private XYSeries CreateSerie(String name,double frec)
    {
        if( name.equals("Fase R") == true )
            return CreateSerie(name,0.0,frec);
        if( name.equals("Fase S") == true )
            return CreateSerie(name,2.0*Math.PI/3.0,frec);
        if( name.equals("Fase T") == true )
            return CreateSerie(name,4.0*Math.PI/3.0,frec);
        return null;
    }
    private XYSeries CreateSerie(String name,double fase,double frec)
    {
        XYSeries serie = new XYSeries(name);
        for( int i=0 ; i<senoLength ; i++ )
            serie.add(((double)i)/((double)senoLength)/frec,Math.sin(2.0*Math.PI*((double)i)/((double)senoLength)+fase));
        return serie;
    }
    private XYSeries CreateCursor(String name,double fase,double frec)
    {
        XYSeries serie = new XYSeries(name,false,true);
        serie.add(fase+((double)0)/((double)senoLength*1)/frec,-0.1);
        serie.add(fase+((double)0)/((double)senoLength*1)/frec,0.1);
        serie.add(fase+((double)1)/((double)senoLength*1)/frec,0.1);
        serie.add(fase+((double)1)/((double)senoLength*1)/frec,-0.1);
        serie.add(fase+((double)0)/((double)senoLength*1)/frec,-0.1);
        return serie;
    }
    private double Deg2Time(double deg,double frec)
    {
        return deg/frec/2.0/180.0;
    }
}
