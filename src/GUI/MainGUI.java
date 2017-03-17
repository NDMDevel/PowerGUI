/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Escritorio
 */
public class MainGUI extends javax.swing.JFrame
{
    private final double resolucionAnguloDisparo;
    private final double anguloDisparoMaximo;
    private final double anguloDisparoMinimo;
    private double anguloDisparo;
    private int h,w;
    public MainGUI() throws IOException
    {
        initComponents();
        
        //Inicializa variables:
        resolucionAnguloDisparo = 1e-4;
        anguloDisparoMaximo = Math.PI;
        anguloDisparoMinimo = 0; //aprox 7°
        anguloDisparo = anguloDisparoMinimo;

        //Modifica objetos de la GUI
        jSlider_AnguloDisparo.setMaximum((int)(1.0/resolucionAnguloDisparo));
        jSlider_AnguloDisparo.setValue((int)(anguloDisparoMinimo*resolucionAnguloDisparo));
        jTextField_AnguloDisparo.setText(Double.toString(anguloDisparoMinimo*180.0/Math.PI));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //System.out.println("H: "+screenSize.height+"W "+screenSize.width);
        //CrearCircuito("./Circuitos/RectificadorTrifasico_TotalmenteControlado.png",screenSize.width/2,screenSize.height/2);
        //CrearCircuito("./Circuitos/RectificadorTrifasico_SemiControlado.png",screenSize.width/2,screenSize.height/2);
        UpdateCircuito();
        //CrearCircuito("./Circuitos/ConversorAC_Trifasico.png",screenSize.width/2,screenSize.height/2);
        jTabbedPane_Onda_Circuito.setSelectedIndex(0);

        String IconName = "1489788883_plugs_512pxGREY.png";
        ImageIcon imageIcon = new ImageIcon("Icons/"+IconName); // load the image to a imageIcon
        Image image = imageIcon.getImage(); // transform it 
        setIconImage(image);  //carga el icono en la aplicacion
        
        //Cambia Look&Feel
        try
        {
//            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex)
        {
            Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        SwingUtilities.updateComponentTreeUI(this);
        pack();
    }
    
    private void UpdateCircuito()
    {
        String circuito = "./Circuitos/";
        if( jComboBox_ConvertidorTipo.getSelectedItem().toString().equals("Rectificador (AC/DC)") == true )
        {
            circuito += "Rectificador";
            if( jComboBox_RectificadorTipo.getSelectedItem().toString().equals("Semi Controlado") == true )
                circuito += "SemiControlado";
            else
                circuito += "TotalmenteControlado";
        }
        else
            circuito += "ConversorAC";
        if( jComboBox_SelectorFases.getSelectedItem().toString().equals("Monofasico") == true )
            circuito += "Monofasico";
        else
            circuito += "Trifasico";
        circuito += ".png";
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        w = screenSize.width/2;
        h = screenSize.height/2;
        ImageIcon imageIcon = new ImageIcon(circuito); // load the image to a imageIcon
        Image image = imageIcon.getImage(); // transform it 
        Image newimg = image.getScaledInstance(w,h,java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
        imageIcon = new ImageIcon(newimg);  // transform it back
        jLabel_Circuito.setIcon(imageIcon);
    }
    
    private void ActualizarGUI()
    {
        double frec = Double.valueOf(jComboBox_Frecuencia.getSelectedItem().toString());
        boolean monofasico = false;
        if( jComboBox_SelectorFases.getSelectedItem().toString().equals("Monofasico") == true )
            monofasico = true;
        String dispStr = jTextField_AnguloDisparo.getText();
        double disparo = Double.valueOf(dispStr);
        if( jComboBox_AnguloDisparoUnidad.getSelectedItem().toString().equals("Rad") == true )
            disparo = disparo*180.0/Math.PI;

        int fases = 1;
        if( monofasico == false )
            fases = 3;
        double disparoArray[] = new double[fases];
        boolean rectificador = true;
        if( jComboBox_ConvertidorTipo.getSelectedItem().toString().equals("Rectificador (AC/DC)") == false )
            rectificador = false;
        boolean semicontrolado = true;
        if( jComboBox_RectificadorTipo.getSelectedItem().toString().equals("Semi Controlado") == false )
            semicontrolado = false;
        
        InicializarDisparos(disparoArray,disparo,frec,monofasico,rectificador);
        
        if( jTabbedPane_Onda_Circuito.getTabCount() > 1 )
        {
            int idx_selected = jTabbedPane_Onda_Circuito.getSelectedIndex();
            int idx = jTabbedPane_Onda_Circuito.indexOfTab("Formas de Onda");
            jTabbedPane_Onda_Circuito.remove(idx);
            jTabbedPane_Onda_Circuito.insertTab("Formas de Onda",null,new Plot(monofasico,frec,disparoArray,rectificador,semicontrolado),null,idx);
            //jTabbedPane_Onda_Circuito.setSelectedIndex(idx);
            jTabbedPane_Onda_Circuito.setSelectedIndex(idx_selected);
        }
        else
            jTabbedPane_Onda_Circuito.insertTab("Formas de Onda",null,new Plot(monofasico,frec,disparoArray,rectificador,semicontrolado),null,0);
        UpdateCircuito();        
    }
    
    private void InicializarDisparos(double []disparoArray,double disparo,double frec,boolean monofasico,boolean rectificador)
    {
        disparoArray[0] = disparo;
        if( monofasico == false )
            for( int i=1 ; i<3 ; i++ )
                disparoArray[i] = disparo + ((double)i)*2.0/3.0*180.0;
    }
    
    private double ValidarAnguloDisparo()
    {
        double disparo;
        try
        {
            disparo = Double.valueOf(jTextField_AnguloDisparo.getText());
            if( jComboBox_AnguloDisparoUnidad.getSelectedItem().toString().equals("Deg") == true )
                disparo *= Math.PI/180.0;
            if( disparo > Math.PI || disparo < 0 )
                disparo = anguloDisparo;
        }
        catch(NumberFormatException ex)
        {
            System.out.println(ex.toString());
            disparo = anguloDisparo;
        }
        if( jComboBox_AnguloDisparoUnidad.getSelectedItem().toString().equals("Deg") == true )
            jTextField_AnguloDisparo.setText(FormateoDisparo(disparo*180.0/Math.PI));
        else
            jTextField_AnguloDisparo.setText(FormateoDisparo(disparo));
        return disparo;
    }
    
    private String FormateoDisparo(double disparo)
    {
        String angStr = Double.toString(disparo);
        int idx = angStr.lastIndexOf(".");
        if( angStr.length()-idx > 6 && idx != -1 )
            angStr = String.valueOf(Double.valueOf(angStr.substring(0,idx+6)));
        return angStr;
    }
    
    private String FormateoDisparo(String disparo)
    {
        String angStr = disparo;
        int idx = angStr.lastIndexOf(".");
        if( angStr.length()-idx > 6 && idx != -1 )
            angStr = String.valueOf(Double.valueOf(angStr.substring(0,idx+6)));
        return angStr;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jComboBox_ConvertidorTipo = new javax.swing.JComboBox();
        jComboBox_SelectorFases = new javax.swing.JComboBox();
        jComboBox_RectificadorTipo = new javax.swing.JComboBox();
        jLabel_Frecuencia = new javax.swing.JLabel();
        jTabbedPane_Onda_Circuito = new javax.swing.JTabbedPane();
        jLabel_Circuito = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jComboBox_Frecuencia = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        jTextField_AnguloDisparo = new javax.swing.JTextField();
        jSlider_AnguloDisparo = new javax.swing.JSlider();
        jComboBox_AnguloDisparoUnidad = new javax.swing.JComboBox();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jComboBox_ConvertidorTipo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Rectificador (AC/DC)", "Conversor de AC (AC/AC)" }));
        jComboBox_ConvertidorTipo.setFocusable(false);
        jComboBox_ConvertidorTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_ConvertidorTipoActionPerformed(evt);
            }
        });

        jComboBox_SelectorFases.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Monofasico", "Trifasico" }));
        jComboBox_SelectorFases.setFocusable(false);
        jComboBox_SelectorFases.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_SelectorFasesActionPerformed(evt);
            }
        });

        jComboBox_RectificadorTipo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Semi Controlado", "Total Controlado" }));
        jComboBox_RectificadorTipo.setFocusable(false);
        jComboBox_RectificadorTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_RectificadorTipoActionPerformed(evt);
            }
        });

        jLabel_Frecuencia.setText("Frecuencia [Hz]:");

        jTabbedPane_Onda_Circuito.setFocusable(false);

        jLabel_Circuito.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTabbedPane_Onda_Circuito.addTab("Circuito", jLabel_Circuito);

        jButton1.setBackground(new java.awt.Color(255, 51, 51));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/download-icon-32px.png"))); // NOI18N
        jButton1.setToolTipText("Escribir configuracion");
        jButton1.setFocusable(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(0, 204, 51));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/upload-icon-32px.png"))); // NOI18N
        jButton3.setToolTipText("Leer configuracion");
        jButton3.setFocusable(false);

        jComboBox_Frecuencia.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "50", "60" }));
        jComboBox_Frecuencia.setFocusable(false);
        jComboBox_Frecuencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_FrecuenciaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane_Onda_Circuito)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jComboBox_ConvertidorTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox_RectificadorTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jComboBox_SelectorFases, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel_Frecuencia)
                                .addGap(3, 3, 3)
                                .addComponent(jComboBox_Frecuencia, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jComboBox_ConvertidorTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jComboBox_RectificadorTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jComboBox_SelectorFases, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel_Frecuencia)
                                .addComponent(jComboBox_Frecuencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane_Onda_Circuito))
        );

        jTabbedPane_Onda_Circuito.getAccessibleContext().setAccessibleDescription("");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Angulo de disparo:"));

        jTextField_AnguloDisparo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_AnguloDisparo.setText("0.0");
        jTextField_AnguloDisparo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField_AnguloDisparoFocusLost(evt);
            }
        });
        jTextField_AnguloDisparo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField_AnguloDisparoKeyPressed(evt);
            }
        });

        jSlider_AnguloDisparo.setMaximum(10000);
        jSlider_AnguloDisparo.setMinorTickSpacing(500);
        jSlider_AnguloDisparo.setPaintLabels(true);
        jSlider_AnguloDisparo.setPaintTicks(true);
        jSlider_AnguloDisparo.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider_AnguloDisparoStateChanged(evt);
            }
        });

        jComboBox_AnguloDisparoUnidad.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Deg", "Rad" }));
        jComboBox_AnguloDisparoUnidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_AnguloDisparoUnidadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox_AnguloDisparoUnidad, 0, 64, Short.MAX_VALUE)
                    .addComponent(jTextField_AnguloDisparo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSlider_AnguloDisparo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSlider_AnguloDisparo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jComboBox_AnguloDisparoUnidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jTextField_AnguloDisparo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jSlider_AnguloDisparoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider_AnguloDisparoStateChanged
        // TODO add your handling code here:
        int slider = jSlider_AnguloDisparo.getValue();
        anguloDisparo = ((double)slider)*resolucionAnguloDisparo*anguloDisparoMaximo;
        String angStr;
        double disparo = Double.valueOf(jTextField_AnguloDisparo.getText());
        if( jComboBox_AnguloDisparoUnidad.getSelectedItem().toString().equals("Deg") == true )
        {
            angStr = Double.toString(anguloDisparo*180.0/Math.PI);  //convierte a grados
            disparo *= Math.PI/180.0;
        }
        else
            angStr = Double.toString(anguloDisparo);    //lo deja en rad

        double diff = Math.abs(((double)slider)*resolucionAnguloDisparo*anguloDisparoMaximo-disparo);
        if( diff > (resolucionAnguloDisparo*anguloDisparoMaximo) || diff < 1e-9 )
            jTextField_AnguloDisparo.setText(FormateoDisparo(angStr));
        jTabbedPane_Onda_Circuito.setSelectedIndex(jTabbedPane_Onda_Circuito.indexOfTab("Formas de Onda"));
        ActualizarGUI();
    }//GEN-LAST:event_jSlider_AnguloDisparoStateChanged

    private void jComboBox_AnguloDisparoUnidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_AnguloDisparoUnidadActionPerformed
        // TODO add your handling code here:
        if( jComboBox_AnguloDisparoUnidad.getSelectedItem().toString().equals("Rad") == true )
            jTextField_AnguloDisparo.setText(FormateoDisparo(anguloDisparo));
        else
            jTextField_AnguloDisparo.setText(FormateoDisparo(anguloDisparo*180.0/Math.PI));
        anguloDisparo = ValidarAnguloDisparo();
        ActualizarGUI();
    }//GEN-LAST:event_jComboBox_AnguloDisparoUnidadActionPerformed

    private void jComboBox_FrecuenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_FrecuenciaActionPerformed
        // TODO add your handling code here:
        ActualizarGUI();
    }//GEN-LAST:event_jComboBox_FrecuenciaActionPerformed

    private void jComboBox_SelectorFasesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_SelectorFasesActionPerformed
        // TODO add your handling code here:
        ActualizarGUI();
    }//GEN-LAST:event_jComboBox_SelectorFasesActionPerformed

    private void jTextField_AnguloDisparoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField_AnguloDisparoFocusLost
        // TODO add your handling code here:
        anguloDisparo = ValidarAnguloDisparo();
        jSlider_AnguloDisparo.setValue((int)(anguloDisparo/anguloDisparoMaximo/resolucionAnguloDisparo));
        ActualizarGUI();
    }//GEN-LAST:event_jTextField_AnguloDisparoFocusLost

    private void jTextField_AnguloDisparoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_AnguloDisparoKeyPressed
        // TODO add your handling code here:
        if( evt.getKeyCode() == 10 )    //ENTER key
            jTextField_AnguloDisparoFocusLost(null);
    }//GEN-LAST:event_jTextField_AnguloDisparoKeyPressed

    private void jComboBox_ConvertidorTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_ConvertidorTipoActionPerformed
        // TODO add your handling code here:
        if( jComboBox_ConvertidorTipo.getSelectedItem().toString().equals("Rectificador (AC/DC)") == true )
            jComboBox_RectificadorTipo.setEnabled(true);
        else
            jComboBox_RectificadorTipo.setEnabled(false);
        ActualizarGUI();
    }//GEN-LAST:event_jComboBox_ConvertidorTipoActionPerformed

    private void jComboBox_RectificadorTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_RectificadorTipoActionPerformed
        // TODO add your handling code here:
        ActualizarGUI();
    }//GEN-LAST:event_jComboBox_RectificadorTipoActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new MainGUI().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox jComboBox_AnguloDisparoUnidad;
    private javax.swing.JComboBox jComboBox_ConvertidorTipo;
    private javax.swing.JComboBox jComboBox_Frecuencia;
    private javax.swing.JComboBox jComboBox_RectificadorTipo;
    private javax.swing.JComboBox jComboBox_SelectorFases;
    private javax.swing.JLabel jLabel_Circuito;
    private javax.swing.JLabel jLabel_Frecuencia;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSlider jSlider_AnguloDisparo;
    private javax.swing.JTabbedPane jTabbedPane_Onda_Circuito;
    private javax.swing.JTextField jTextField_AnguloDisparo;
    // End of variables declaration//GEN-END:variables
}
