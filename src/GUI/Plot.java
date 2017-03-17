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
        this(true,50.0,null,true,false);
    }
    public Plot(boolean monofasico,double frec,double[] disparo,boolean rectificador,boolean semicontrolado)
    {
        dataPlot = new XYSeriesCollection();
        if( disparo == null )
            dataPlot.addSeries(CreateCursor("Disparo",Deg2Time(0,frec),frec,rectificador,semicontrolado,monofasico,((double)0)/20));
        else
            for( int i=0 ; i<disparo.length ; i++ )
            {
                String title = "Disparo";
                if( rectificador == false )
                {
                    if( i == 0 )
                        title = title + " R";
                    if( i == 1 )
                        title = title + " S";
                    if( i == 2 )
                        title = title + " T";
                    dataPlot.addSeries(CreateCursor(title,Deg2Time(disparo[i],frec),frec,rectificador,semicontrolado,monofasico,((double)i)/20.0));
                }
                else
                {
                    dataPlot.addSeries(CreateCursor(title,Deg2Time(disparo[i],frec),frec,rectificador,semicontrolado,monofasico,((double)i)/20.0));
                    break;
                }
            }
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
        if( monofasico == false && rectificador == false )
        {
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0, Color.red);
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(1, Color.blue);
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(2, Color.green);
        }
        add("Ondas",new ChartPanel(chart));
    }
    private XYSeries CreateSerie(String name,double frec)
    {
        if( name.equals("Fase R") == true )
            return CreateSerie(name,0.0,frec);
        if( name.equals("Fase S") == true )
            return CreateSerie(name,-2.0*Math.PI/3.0,frec);
        if( name.equals("Fase T") == true )
            return CreateSerie(name,-4.0*Math.PI/3.0,frec);
        return null;
    }
    private XYSeries CreateSerie(String name,double fase,double frec)
    {
        XYSeries serie = new XYSeries(name);
        for( int i=0 ; i<senoLength ; i++ )
            serie.add(((double)i)/((double)senoLength)/frec,Math.sin(2.0*Math.PI*((double)i)/((double)senoLength)+fase));
        return serie;
    }
    private XYSeries CreateCursor(String name,double fase,double frec,boolean rectificador,boolean semicontrolado,boolean monofasico,double offsetV)
    {
        XYSeries serie = new XYSeries(name,false,true);
        double width = 1;
        double heigth = 0.1;
        if( rectificador == false )
        {
            if( fase > 1/frec )
                fase -= 1/frec;
            serie.add(fase+((double)0)/((double)senoLength*1)/frec,-heigth+offsetV);
            serie.add(fase+((double)0)/((double)senoLength*1)/frec,heigth+offsetV);
            serie.add(fase+((double)width)/((double)senoLength*1)/frec,heigth+offsetV);
            serie.add(fase+((double)width)/((double)senoLength*1)/frec,-heigth+offsetV);
            serie.add(fase+((double)0)/((double)senoLength*1)/frec,-heigth+offsetV);

            fase += 0.5/frec;
            if( fase > 1/frec )
                fase -= 1/frec;
            serie.add(fase+((double)0)/((double)senoLength*1)/frec,-heigth+offsetV);
            serie.add(fase+((double)0)/((double)senoLength*1)/frec,heigth+offsetV);
            serie.add(fase+((double)width)/((double)senoLength*1)/frec,heigth+offsetV);
            serie.add(fase+((double)width)/((double)senoLength*1)/frec,-heigth+offsetV);
            serie.add(fase+((double)0)/((double)senoLength*1)/frec,-heigth+offsetV);
        }
        else
        {
            double factor = 1.0/3.0;
            int disparos = 2;
            if( semicontrolado == false )
                factor /= 2.0;
            if( monofasico == true )
            {
                factor = 0.5;
            }
            else
            {
                serie.add(1.0/frec+((double)0)/((double)senoLength*1)/frec,-heigth+offsetV);
                serie.add(((double)0)/((double)senoLength*1)/frec,-heigth+offsetV);
                if( semicontrolado == true )
                    disparos = 3;
                else
                    disparos = 6;
            }
            
            while( disparos-- > 0 )
            {
                if( fase > 1/frec )
                    fase -= 1/frec;
                serie.add(fase+((double)0)/((double)senoLength*1)/frec,-heigth+offsetV);
                serie.add(fase+((double)0)/((double)senoLength*1)/frec,heigth+offsetV);
                serie.add(fase+((double)width)/((double)senoLength*1)/frec,heigth+offsetV);
                serie.add(fase+((double)width)/((double)senoLength*1)/frec,-heigth+offsetV);
                serie.add(fase+((double)0)/((double)senoLength*1)/frec,-heigth+offsetV);
                fase += factor/frec;
            }
        }
        return serie;
    }
    private double Deg2Time(double deg,double frec)
    {
        return deg/frec/2.0/180.0;
    }
    private double Rad2Time(double rad,double frec)
    {
        return rad/frec/2.0/Math.PI;
    }
}
