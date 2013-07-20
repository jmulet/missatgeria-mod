/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.missatgeria;

import com.l2fprod.common.swing.StatusBar;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import javax.help.CSH;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import org.iesapp.clients.iesdigital.missatgeria.BeanMissatge;
import org.iesapp.clients.iesdigital.missatgeria.MissatgeriaCollection;
import org.iesapp.framework.pluggable.TopModuleWindow;
import org.iesapp.util.DataCtrl;
import org.iesapp.util.MySorter;

/**
 *
 * @author Josep
 */
//@org.openide.util.lookup.ServiceProvider(service=TopModuleWindow.class, path="modules")
public class MissatgeriaModule extends org.iesapp.framework.pluggable.TopModuleWindow {
    private ArrayList<BeanMissatge> missatges;
    private int missatgeActual=0;
    private ArrayList<BeanMissatge> enviats;
    private int enviatActual=0;
    private String m_abrev="";
    private boolean isListening = false;
    private DefaultTableModel modelTable1;
    private MySorter msort;
    private int idSGD = -1;
    private int heigth2=92;
//    private Timer timer;
    /**
     * Creates new form SolicitudsModule
     */
    public MissatgeriaModule() {
        super();
        this.moduleName = "missatgeria";
        this.moduleDisplayName = "Missatgeria";
        this.moduleDescription = "Un mòdul per al transpàs d'informació entre l'equip docent i el tutor d'un grup.";
         
        initComponents();
        
        jCancel.setIcon(new ImageIcon(MissatgeriaModule.class.getResource("/org/iesapp/framework/icons/exit.gif")));
        jAnterior.setIcon(new ImageIcon(MissatgeriaModule.class.getResource("/org/iesapp/framework/icons/back.gif")));
        jSeguent.setIcon(new ImageIcon(MissatgeriaModule.class.getResource("/org/iesapp/framework/icons/forward.gif")));
        jEnvial.setIcon(new ImageIcon(MissatgeriaModule.class.getResource("/org/iesapp/framework/icons/envia.gif")));
        associateLookup(this);
    }
    
    @Override
    public void postInitialize(){
        this.openingRequired = coreCfg.getIesClient().getMissatgeriaCollection().getNumSolPendents()>0;
           
        CSH.setHelpIDString(jScrollPane9, "org-iesapp-modules-missatgeria");
        CSH.setHelpIDString(jScrollPane11, "org-iesapp-modules-missatgeria-reenviar");
        jTabbedPane1.setEnabledAt(2, false);
        jCancel.setVisible(false);
        jLabel6.setVisible(false);
        jNotes.setDocument(new LimitDocument(MissatgeriaCollection.MAXLENGTHNOTES));
        jNotes1.setDocument(new LimitDocument(MissatgeriaCollection.MAXLENGTHNOTES));
        
         JTableHeader header = jTable1.getTableHeader();
         header.addMouseListener(new java.awt.event.MouseAdapter() {
         
             @Override
             public void mouseClicked(java.awt.event.MouseEvent evt) {
                 JTable table = ((JTableHeader) evt.getSource()).getTable();
                 TableColumnModel colModel = table.getColumnModel();

                 int index = colModel.getColumnIndexAtX(evt.getX());

                 if (index == 1) {
                     msort.setFirst("alumno");
                     mostraTaulaEnviats();
                 } else if (index == 2) //ordena per grup
                 {
                     msort.setFirst("grupo");
                     mostraTaulaEnviats();
                 } else if (index == 3) //ordena per llinatge1
                 {
                     msort.setFirst("dataEntrevista");
                     mostraTaulaEnviats();
                 } else {
                     return;
                 }

                 java.awt.Rectangle headerRect = table.getTableHeader().getHeaderRect(index);
                 if (index == 0) {
                     headerRect.width -= 10;
                 } else {
                     headerRect.grow(-10, 0);
                 }
                 if (!headerRect.contains(evt.getX(), evt.getY())) {
                     int vLeftColIndex = index;
                     if (evt.getX() < headerRect.x) {
                         vLeftColIndex--;
                     }
                 }
             }
         });

//         
//        //Periodically check new solicituds
//       timer = new Timer(30000, new ActionListener(){
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                
//                 if(tabComponent!=null && beanModule!=null)
//                 {
//                    if(coreCfg.getIesClient().getMissatgeriaCollection().getNumSolPendents()>0)
//                    {
//                        tabComponent.setIcon(beanModule.getModuleIcon16x16(TopModuleWindow.STATUS_AWAKE));
//                        tabComponent.setStatus(TopModuleWindow.STATUS_AWAKE);
//                    }
//                    else
//                    {
//                        tabComponent.setIcon(beanModule.getModuleIcon16x16(TopModuleWindow.STATUS_NORMAL));
//                        tabComponent.setStatus(TopModuleWindow.STATUS_NORMAL);
//                    }
//                 }
//            }
//        });
//       timer.setInitialDelay(2000);
//       timer.start();
//         
        
       msort = new MySorter(new String[]{"dataEntrevista", "grupo", "alumno"});
        
       jTable1.setIntercellSpacing( new java.awt.Dimension(2,2) );
       jTable1.setGridColor(java.awt.Color.gray);
       jTable1.setShowGrid(true);
        
   
       idSGD = coreCfg.getUserInfo().getIdSGD();//Data.getIdSgd(coreCfg.getMysql(), m_abrev); 
        

       //Carrega els items de la base de dades
       jComboBox1.setModel( getModelCombo("actitud"));
       jComboBox2.setModel( getModelCombo("feina"));
       jComboBox3.setModel( getModelCombo("notes"));
       jComboBox4.setModel( getModelCombo("observacions"));
       
       jLabel8.setVisible(false);
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel(){
            //Add a gradient background
            @Override
            public void paintComponent(Graphics g)
            {
                Graphics2D g2d = (Graphics2D)g;
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(w, 0, Color.ORANGE, 0, h, Color.WHITE);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        jNous = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jAnterior = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jEnvial = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jCancel = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jSeguent = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane9 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jActitud = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jFeina = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        jObservacions = new javax.swing.JTextArea();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jNotes = new javax.swing.JTextArea();
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();
        jComboBox4 = new javax.swing.JComboBox();
        jComboBox3 = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jAlumne = new javax.swing.JTextField();
        jTutor = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jData = new javax.swing.JTextField();
        jMateria = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jPhoto = new javax.swing.JLabel();
        jGrup = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane12 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        }
        ;
        jLabel25 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jActitud1 = new javax.swing.JTextArea();
        jLabel15 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jFeina1 = new javax.swing.JTextArea();
        jScrollPane7 = new javax.swing.JScrollPane();
        jObservacions1 = new javax.swing.JTextArea();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jNotes1 = new javax.swing.JTextArea();
        jPanel7 = new javax.swing.JPanel();
        jMateria1 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jTutor1 = new javax.swing.JTextField();
        jData2 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jGrup1 = new javax.swing.JTextField();
        jAlumne1 = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLinkButton1 = new com.l2fprod.common.swing.JLinkButton();

        jPanel1.setBackground(new java.awt.Color(255, 204, 0));

        jNous.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jNous.setForeground(new java.awt.Color(153, 0, 0));
        jNous.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jNous.setText(" ");

        jPanel3.setOpaque(false);

        jAnterior.setText("Anterior");
        jAnterior.setToolTipText("Sol·licitud anterior");
        jAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAnteriorActionPerformed(evt);
            }
        });
        jPanel3.add(jAnterior);

        jLabel1.setText("        ");
        jPanel3.add(jLabel1);

        jEnvial.setText("Envia'l");
        jEnvial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jEnvialActionPerformed(evt);
            }
        });
        jPanel3.add(jEnvial);

        jLabel2.setText("            ");
        jPanel3.add(jLabel2);

        jCancel.setText("Cancel·la");
        jCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCancelActionPerformed(evt);
            }
        });
        jPanel3.add(jCancel);

        jLabel6.setText("            ");
        jPanel3.add(jLabel6);

        jSeguent.setText("Següent");
        jSeguent.setToolTipText("Següent sol·licitud");
        jSeguent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSeguentActionPerformed(evt);
            }
        });
        jPanel3.add(jSeguent);

        jLabel14.setText("            ");
        jPanel3.add(jLabel14);

        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(233, 202, 140));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jActitud.setColumns(20);
        jActitud.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jActitud.setLineWrap(true);
        jActitud.setRows(5);
        jActitud.setToolTipText("Escriviu o triau una opció");
        jActitud.setWrapStyleWord(true);
        jScrollPane1.setViewportView(jActitud);

        jLabel3.setText("Actitud");

        jLabel11.setText("Feina");

        jFeina.setColumns(20);
        jFeina.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jFeina.setLineWrap(true);
        jFeina.setRows(5);
        jFeina.setToolTipText("Escriviu o triau una opció");
        jFeina.setWrapStyleWord(true);
        jScrollPane2.setViewportView(jFeina);

        jObservacions.setColumns(20);
        jObservacions.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jObservacions.setLineWrap(true);
        jObservacions.setRows(5);
        jObservacions.setToolTipText("Escriviu o triau una opció");
        jObservacions.setWrapStyleWord(true);
        jScrollPane3.setViewportView(jObservacions);

        jLabel12.setText("Observacions");

        jLabel13.setText("Notes");

        jNotes.setColumns(20);
        jNotes.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jNotes.setLineWrap(true);
        jNotes.setRows(5);
        jNotes.setToolTipText("Escriviu o triau una opció");
        jNotes.setWrapStyleWord(true);
        jScrollPane4.setViewportView(jNotes);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "triau opcions", "Mostra interès", "Cal que participi més", "Ha de prestar més atenció al professor", "Actitud passiva i molesta" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "triau opcions", "Fa sempre els deures", "A vegades fa els deures", "Mai duu els deures fets", "Sempre treballa a classe", "A vegades treballa a classe", "Mai treballa a classe" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "triau opcions", "Que seguexi així", "Cal que s'esforci més", "Falta molt a classe" }));
        jComboBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox4ActionPerformed(evt);
            }
        });

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "triau opcions", "Li falta fer algun examen", "No ha entregat el quadern", "Li falten tasques per lliurar" }));
        jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox3ActionPerformed(evt);
            }
        });

        jButton1.setText("Auto");
        jButton1.setToolTipText("Importa les dades de l'SGD");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel6.setOpaque(false);

        jLabel4.setText("Alumne/a:");

        jAlumne.setEditable(false);
        jAlumne.setBackground(new java.awt.Color(255, 255, 153));

        jTutor.setEditable(false);
        jTutor.setBackground(new java.awt.Color(255, 255, 153));

        jLabel5.setText(" Entrevista:");

        jData.setEditable(false);
        jData.setBackground(new java.awt.Color(255, 255, 153));

        jMateria.setEditable(false);
        jMateria.setBackground(new java.awt.Color(255, 255, 153));

        jLabel23.setText("Materia:");

        jGrup.setEditable(false);
        jGrup.setBackground(new java.awt.Color(255, 255, 153));

        jLabel8.setText("Grup:");

        jLabel10.setText("Tutor/a:");

        jLabel9.setBackground(new java.awt.Color(255, 255, 51));
        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/modules/missatgeria/atention.png"))); // NOI18N
        jLabel9.setText(" ");
        jLabel9.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jLabel9.setOpaque(true);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel8))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jGrup, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jData, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jMateria, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTutor)
                            .addComponent(jAlumne))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPhoto, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jAlumne, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(2, 2, 2)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jGrup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel5)
                            .addComponent(jData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23)
                            .addComponent(jMateria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTutor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)))
                    .addComponent(jPhoto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2)
                .addComponent(jLabel9)
                .addGap(3, 3, 3))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addGap(2, 2, 2))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox4, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1)))
                        .addGap(1, 1, 1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(2, 2, 2))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox1, 0, 653, Short.MAX_VALUE)))
                        .addGap(2, 2, 2))
                    .addComponent(jScrollPane3))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(2, 2, 2)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane9.setViewportView(jPanel2);

        jTabbedPane1.addTab("PENDENTS", jScrollPane9);

        jPanel5.setBackground(new java.awt.Color(255, 255, 204));

        modelTable1 = new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "Id", "Alumne/a", "Grup", "Data Entrevista"
            }
        );
        jTable1.setModel(modelTable1);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(200);
        jTable1.setRowHeight(25);
        jScrollPane12.setViewportView(jTable1);

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel25.setText("* Fer doble-click sobre l'alumne per modificar i tornar a enviar la sol·licitud");

        jCheckBox1.setText("Amaga si ja s'ha realitzat l'entrevista");
        jCheckBox1.setOpaque(false);
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(204, 0, 0));
        jLabel7.setText("Sol·licituds que heu enviat");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox1))
                    .addComponent(jLabel25))
                .addContainerGap(264, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 714, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel25)
                .addGap(3, 3, 3)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jCheckBox1))
                .addContainerGap(378, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(64, 64, 64)
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jScrollPane11.setViewportView(jPanel5);

        jTabbedPane1.addTab("ENVIATS", jScrollPane11);

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jActitud1.setColumns(20);
        jActitud1.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jActitud1.setLineWrap(true);
        jActitud1.setRows(5);
        jActitud1.setWrapStyleWord(true);
        jScrollPane5.setViewportView(jActitud1);

        jLabel15.setText("Actitud");

        jLabel20.setText("Feina");

        jFeina1.setColumns(20);
        jFeina1.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jFeina1.setLineWrap(true);
        jFeina1.setRows(5);
        jFeina1.setWrapStyleWord(true);
        jScrollPane6.setViewportView(jFeina1);

        jObservacions1.setColumns(20);
        jObservacions1.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jObservacions1.setLineWrap(true);
        jObservacions1.setRows(5);
        jObservacions1.setWrapStyleWord(true);
        jScrollPane7.setViewportView(jObservacions1);

        jLabel21.setText("Observacions");

        jLabel22.setText("Notes");

        jNotes1.setColumns(20);
        jNotes1.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jNotes1.setLineWrap(true);
        jNotes1.setRows(5);
        jNotes1.setToolTipText("");
        jNotes1.setWrapStyleWord(true);
        jScrollPane8.setViewportView(jNotes1);

        jMateria1.setEditable(false);
        jMateria1.setBackground(new java.awt.Color(244, 244, 244));

        jLabel16.setText("Alumne/a:");

        jLabel19.setText("Tutor/a:");

        jTutor1.setEditable(false);
        jTutor1.setBackground(new java.awt.Color(244, 244, 244));

        jData2.setEditable(false);
        jData2.setBackground(new java.awt.Color(244, 244, 244));

        jLabel17.setText("Data Entrevista:");

        jGrup1.setEditable(false);
        jGrup1.setBackground(new java.awt.Color(244, 244, 244));

        jAlumne1.setEditable(false);
        jAlumne1.setBackground(new java.awt.Color(244, 244, 244));

        jLabel24.setText("Materia:");

        jLabel18.setText("Grup:");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel19)
                            .addComponent(jLabel18)))
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jGrup1, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jData2, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jMateria1, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE))
                    .addComponent(jAlumne1)
                    .addComponent(jTutor1))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jAlumne1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGap(3, 3, 3)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jGrup1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jData2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18)
                    .addComponent(jMateria1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24))
                .addGap(3, 3, 3)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTutor1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addGap(2, 2, 2))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(jLabel20)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 710, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 710, Short.MAX_VALUE)
                    .addComponent(jLabel22)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 710, Short.MAX_VALUE)
                    .addComponent(jLabel21)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 710, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(3, 3, 3)
                .addComponent(jLabel15)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jLabel20)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jLabel22)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );

        jScrollPane10.setViewportView(jPanel4);

        jTabbedPane1.addTab("ENVIATS: Edició", jScrollPane10);

        jLinkButton1.setBackground(new java.awt.Color(255, 51, 51));
        jLinkButton1.setText(" ");
        jLinkButton1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLinkButton1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLinkButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 741, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jNous, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jNous)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLinkButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentContainer());
        getContentContainer().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(1, 1, 1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAnteriorActionPerformed
        int tab= jTabbedPane1.getSelectedIndex();

        if(tab==0)
        {
            //Desa en memòria el missatge actual
            if(missatgeActual>=0 && missatgeActual<missatges.size())
            {
                missatges.get(missatgeActual).setActitud(jActitud.getText());
                missatges.get(missatgeActual).setFeina(jFeina.getText());
                missatges.get(missatgeActual).setNotes(jNotes.getText());
                missatges.get(missatgeActual).setComentari(jObservacions.getText());
            }

            doUpdate(false);
            if(missatgeActual>0) {
                missatgeActual -=1;
            }

            mostraMissatge(missatgeActual);
        }
        else
        {
            //Desa en memòria el missatge actual
            if(enviatActual>=0 && enviatActual < enviats.size())
            {
                enviats.get(enviatActual).setActitud(jActitud1.getText());
                enviats.get(enviatActual).setFeina(jFeina1.getText());
                enviats.get(enviatActual).setNotes(jNotes1.getText());
                enviats.get(enviatActual).setComentari(jObservacions1.getText());
            }

            doUpdate(false);
            if(enviatActual>0) {
                enviatActual -=1;
            }

            mostraEnviats(enviatActual);
        }

    }//GEN-LAST:event_jAnteriorActionPerformed

    private void jEnvialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEnvialActionPerformed
        //Determina si estic a l'inbox o outboux

        int tab= jTabbedPane1.getSelectedIndex();

        int nup = this.doUpdate(true);
        if(nup<=0) {
            return;
        }

        //      SplashWindow3 splash = new SplashWindow3("Informació enviada al tutor/a.", null, 3000, this.getLocation());
        //      splash.setVisible(true);
        //      splash.setAlwaysOnTop(true);
        AnimatedPanel animatedPanel = new AnimatedPanel(true, 1250, AnimatedPanel.LETTER);
        animatedPanel.setVisible(true);

        if(nup>0 && tab==0) //l'elimina de pendents
        {
            missatges.remove(missatgeActual);
            if(missatges.isEmpty())
            {
                setVisibleInbox(false);
                jEnvial.setEnabled(false);
                 jNous.setVisible(false);
            }
            else
            {
                if(missatgeActual<missatges.size())
                {
                    mostraMissatge(missatgeActual);
                }
                else
                {
                    missatgeActual -= 1;
                    mostraMissatge(missatgeActual);
                }
            }

        }
        else if(nup>0 && tab==2)
        {
            //torna a la taula d'enviats
            jTabbedPane1.setSelectedIndex(1);
        }

        //Actualitza el camp jNous
        actualitzaMissatge();

    }//GEN-LAST:event_jEnvialActionPerformed

    private void jCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCancelActionPerformed
        jTabbedPane1.setSelectedIndex(1);
    }//GEN-LAST:event_jCancelActionPerformed

    private void jSeguentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSeguentActionPerformed

        int tab= jTabbedPane1.getSelectedIndex();

        if(tab==0)
        {
            //Desa en memòria el missatge actual
            if(missatgeActual>=0 && missatgeActual<missatges.size())
            {
                missatges.get(missatgeActual).setActitud(jActitud.getText());
                missatges.get(missatgeActual).setFeina(jFeina.getText());
                missatges.get(missatgeActual).setNotes(jNotes.getText());
                missatges.get(missatgeActual).setComentari(jObservacions.getText());
            }

            if(missatgeActual<missatges.size()-1) {
                missatgeActual +=1;
            }
            mostraMissatge(missatgeActual);
        }
        else
        {
            //Desa en memòria el missatge actual
            if(enviatActual>=0 && enviatActual < enviats.size())
            {
                enviats.get(enviatActual).setActitud(jActitud1.getText());
                enviats.get(enviatActual).setFeina(jFeina1.getText());
                enviats.get(enviatActual).setNotes(jNotes1.getText());
                enviats.get(enviatActual).setComentari(jObservacions1.getText());
            }

            if(enviatActual<enviats.size()-1) {
                enviatActual +=1;
            }
            mostraEnviats(enviatActual);
        }

    }//GEN-LAST:event_jSeguentActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        if(jComboBox1.getSelectedIndex()==0) {
            return;
        }

        String tmp = jActitud.getText();
        jActitud.setText(tmp+". "+jComboBox1.getSelectedItem());
        jComboBox1.setSelectedIndex(0);
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        if(jComboBox2.getSelectedIndex()==0) {
            return;
        }

        String tmp = jFeina.getText();
        jFeina.setText(tmp+". "+jComboBox2.getSelectedItem());
        jComboBox2.setSelectedIndex(0);
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jComboBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox4ActionPerformed
        if(jComboBox4.getSelectedIndex()==0) {
            return;
        }

        String tmp = jObservacions.getText();
        jObservacions.setText(tmp+". "+jComboBox4.getSelectedItem());
        jComboBox4.setSelectedIndex(0);
    }//GEN-LAST:event_jComboBox4ActionPerformed

    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed
        if(jComboBox3.getSelectedIndex()==0) {
            return;
        }

        String tmp = jNotes.getText();
        jNotes.setText(tmp+". "+jComboBox3.getSelectedItem());
        jComboBox3.setSelectedIndex(0);
    }//GEN-LAST:event_jComboBox3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        //System.out.println("hellooo....");
        //Si no hi ha notes ho fa automàticament
        if(idSGD>=0)
        {
            String txt =  coreCfg.getIesClient().getMissatgeriaCollection().getAutoNotes(missatges.get(missatgeActual).getExpedient(),
            missatges.get(missatgeActual).getIdMateria(), idSGD);
            missatges.get(missatgeActual).setNotes(txt);
            // System.out.println("he anat....");
        }
        jNotes.setText(missatges.get(missatgeActual).getNotes());

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if(evt.getClickCount()==2)
        {
            int row=jTable1.getSelectedRow();
            if(row<0) {
                return;
            }

            jTabbedPane1.setEnabledAt(2, true);
            enviatActual = ((Number) jTable1.getValueAt(row,0)).intValue();
            jTabbedPane1.setSelectedIndex(2);

        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        mostraTaulaEnviats();
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        if(!isListening) {
            return;
        }

        int tab = jTabbedPane1.getSelectedIndex();

        jEnvial.setVisible(true);
        jAnterior.setVisible(true);
        jSeguent.setVisible(true);
        jCancel.setVisible(false);

        jLabel1.setVisible(true);
        jLabel2.setVisible(true);
        jLabel6.setVisible(true);
        jLabel14.setVisible(true);

        if(tab==0)
        {
            jTabbedPane1.setEnabledAt(0, true);
            jTabbedPane1.setEnabledAt(1, true);
            jTabbedPane1.setEnabledAt(2, false);
            jEnvial.setText("Envia'l");
            jEnvial.setEnabled(!missatges.isEmpty());
            jLabel2.setVisible(false);
        }
        else if(tab==1)
        {
            jTabbedPane1.setEnabledAt(0, true);
            jTabbedPane1.setEnabledAt(1, true);
            jTabbedPane1.setEnabledAt(2, false);
            mostraTaulaEnviats();
            jEnvial.setVisible(false);
            jAnterior.setVisible(false);
            jSeguent.setVisible(false);

            jLabel1.setVisible(false);
            jLabel2.setVisible(false);
            jLabel6.setVisible(false);
            jLabel14.setVisible(false);

        }
        else if(tab==2)
        {
            jTabbedPane1.setEnabledAt(0, false);
            jTabbedPane1.setEnabledAt(1, false);
            jTabbedPane1.setEnabledAt(2, true);
             jCancel.setVisible(true);
            jAnterior.setVisible(false);
            jSeguent.setVisible(false);
            enviats = coreCfg.getIesClient().getMissatgeriaCollection().loadEnviats(msort.getMysqlOrder());
            mostraEnviats(enviatActual);
            jEnvial.setText("Torna'l a enviar");
            jEnvial.setEnabled(!enviats.isEmpty());

        }

    }//GEN-LAST:event_jTabbedPane1StateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea jActitud;
    private javax.swing.JTextArea jActitud1;
    private javax.swing.JTextField jAlumne;
    private javax.swing.JTextField jAlumne1;
    private javax.swing.JButton jAnterior;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jCancel;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox4;
    private javax.swing.JTextField jData;
    private javax.swing.JTextField jData2;
    private javax.swing.JButton jEnvial;
    private javax.swing.JTextArea jFeina;
    private javax.swing.JTextArea jFeina1;
    private javax.swing.JTextField jGrup;
    private javax.swing.JTextField jGrup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private com.l2fprod.common.swing.JLinkButton jLinkButton1;
    private javax.swing.JTextField jMateria;
    private javax.swing.JTextField jMateria1;
    private javax.swing.JTextArea jNotes;
    private javax.swing.JTextArea jNotes1;
    private javax.swing.JLabel jNous;
    private javax.swing.JTextArea jObservacions;
    private javax.swing.JTextArea jObservacions1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JLabel jPhoto;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JButton jSeguent;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTutor;
    private javax.swing.JTextField jTutor1;
    // End of variables declaration//GEN-END:variables

    @Override
    public ImageIcon getModuleIcon() {
       return null;
    }

    @Override
    public boolean isMultipleInstance() {
        return false;
    }

    @Override
    public void refreshUI() {
        
       if(beanModule!=null)
       {
           if(!beanModule.getIniParameters().containsKey("tabPlacement"))
           {
                jTabbedPane1.setTabPlacement(JTabbedPane.LEFT);
           }
           else
           {
               String placement = (String) beanModule.getIniParameters().get("tabPlacement");
               if(placement.equalsIgnoreCase("top"))
               {
                    jTabbedPane1.setTabPlacement(JTabbedPane.TOP);
               }
               else if(placement.equalsIgnoreCase("bottom"))
               {
                    jTabbedPane1.setTabPlacement(JTabbedPane.BOTTOM);
               }
               else if(placement.equalsIgnoreCase("left"))
               {
                    jTabbedPane1.setTabPlacement(JTabbedPane.LEFT);
               }
           }         
       }
        
       heigth2 = jPhoto.getHeight();
       m_abrev = coreCfg.getUserInfo().getAbrev();
       //inbox...
       missatges = coreCfg.getIesClient().getMissatgeriaCollection().listSolicitudsPendents();
       int ncaducats = coreCfg.getIesClient().getMissatgeriaCollection().getNumCaducats(missatges);
       
       jLinkButton1.setText("Eliminar "+ncaducats+" sol·licituds caducades");
       jLinkButton1.setVisible(ncaducats>0);
       jLinkButton1.setOpaque(ncaducats>0);
       jLinkButton1.addActionListener(new ActionListener(){
           
            @Override
            public void actionPerformed(ActionEvent e) {
                Calendar calendar0 = Calendar.getInstance();
                calendar0.add(Calendar.DAY_OF_MONTH, -1);
                String topDia = new DataCtrl(calendar0).getDataSQL();
            
                String SQL1 = "UPDATE sig_missatgeria as mis INNER JOIN tuta_entrevistes as tenv ON tenv.id=mis.idEntrevista SET dataContestat=CURRENT_DATE(), actitud=' : Sol.licitud caducada - no contestat -', feina=' : Sol.licitud caducada - no contestat -', notes=' : Sol.licitud caducada - no contestat -' WHERE "
                        + " mis.destinatari='"+coreCfg.getUserInfo().getAbrev()+"' AND tenv.dia<='"+topDia+"'";
                
                coreCfg.getMysql().executeUpdate(SQL1);
                refreshUI();
            }   
        });

        enviats = coreCfg.getIesClient().getMissatgeriaCollection().loadEnviats(msort.getMysqlOrder());
        isListening = true; 
        
        missatgeActual=0;
        enviatActual=0;
        
        //mostra el primer missatge
        if(missatges.isEmpty())
        {
            setVisibleInbox(false);
            jNous.setVisible(false);
            jTabbedPane1.setSelectedIndex(1);
            mostraEnviats(enviatActual);
        }
        else
        {
            mostraMissatge(0);
            actualitzaMissatge();
        }
        
       
    }

    
    private void setVisibleInbox(boolean ver) {
       jAlumne.setVisible(ver);
       jGrup.setVisible(ver);
       jData.setVisible(ver);
       jTutor.setVisible(ver);
       jActitud.setVisible(ver);
       jNotes.setVisible(ver);
       jFeina.setVisible(ver);
       jObservacions.setVisible(ver);
       jMateria.setVisible(ver);
       jButton1.setVisible(ver);
       jPhoto.setVisible(ver);
       
       if(ver){
        jLabel4.setText("Alumne/a:");
        jLabel9.setVisible(true);
       }
       else{
        jLabel4.setText("No teniu cap sol·licitud pendent. Gràcies");
        jLabel9.setVisible(false);
       }
       
       jScrollPane1.setVisible(ver);
       jScrollPane2.setVisible(ver);
       jScrollPane3.setVisible(ver);
       jScrollPane4.setVisible(ver);
       
       jLabel8.setVisible(ver);
       jLabel5.setVisible(ver);
       jLabel10.setVisible(ver);
       jLabel3.setVisible(ver);
       jLabel11.setVisible(ver);
       jLabel12.setVisible(ver);
       jLabel13.setVisible(ver);
       jLabel23.setVisible(ver);
       
       jComboBox1.setVisible(ver);
       jComboBox2.setVisible(ver);
       jComboBox3.setVisible(ver);
       jComboBox4.setVisible(ver);
       
    }

            
    
    public int getSolPendents()
    {
        return missatges.size();
    }

    
    private void actualitzaMissatge()
    {
        jNous.setVisible(true);
        int n = missatges.size();
        if(n>1)
        {
            jNous.setText("Teniu "+n+" noves sol·licituds pendents");
        }
        else if(n==1)
        {
            jNous.setText("Teniu una sol·licitud pendent");
        }
        else if(n==0)
        {
            jNous.setVisible(false);
        }
    }

    
    private ComboBoxModel getModelCombo(String tipus) {
                       
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        ArrayList<String> list = coreCfg.getIesClient().getMissatgeriaCollection().listMissatgeriaItems(tipus);
        
        for(String s: list)
        {
            model.addElement(s);
        }
        return model;
    }

    private int doUpdate(boolean tanca) {

        
      int tab= jTabbedPane1.getSelectedIndex();   
      
      BeanMissatge sms = null;
    
      String actitud="";
      String feina="";
      String notes="";
      String comentaris="";
      if(tab==0 && !missatges.isEmpty())
      {
            sms =missatges.get(missatgeActual);
            actitud=jActitud.getText();
            feina=jFeina.getText();
            notes=jNotes.getText();
            comentaris=jObservacions.getText();
      }
      else
      {
            sms = enviats.get(enviatActual);
            actitud=jActitud1.getText();
            feina=jFeina1.getText();
            notes=jNotes1.getText();
            comentaris=jObservacions1.getText();
      }
      if( (actitud.isEmpty() || feina.isEmpty() || notes.isEmpty() ) && tanca )
      {
            JOptionPane.showMessageDialog(this, "Tots els camps, excepte 'Observacions', són obligatoris.");
            return -1;
      }
    
      if(tanca)
      {
          sms.setDataContestat(new java.util.Date());
      }
      int nup = coreCfg.getIesClient().getMissatgeriaCollection().saveBeanMissatge(sms);
      return nup;
      
    }

        
  private void mostraImatge(byte[] foto)
    {
        if(foto!=null)
        {
            Image image = Toolkit.getDefaultToolkit().createImage(foto);
            Image scaledInstance = image.getScaledInstance(-1, heigth2>0?heigth2:90, Image.SCALE_SMOOTH);
            jPhoto.setIcon( new ImageIcon(scaledInstance) );  
        }
        else
        {
            jPhoto.setIcon( null );  
        }
    }

      
     private void mostraMissatge(int i) {

         
           //check size
         if(i>=missatges.size()) {
             return;
         }
         
         if(missatges.get(i).getInstruccions()!=null && !missatges.get(i).getInstruccions().trim().isEmpty())
         {
             jLabel9.setVisible(true);
             jLabel9.setText(missatges.get(i).getInstruccions());
         }
         else
         {
             jLabel9.setVisible(false);
         }
         //Si no hi ha notes ho fa automàticament
         //System.out.println(" idSGD is...."+idSGD);
         if( (missatges.get(i).getNotes()==null || missatges.get(i).getNotes().isEmpty()) && idSGD>=0)
         {
           //  System.out.println("missatge null vaig a cercar notes...."+missatges.get(i).getExpedient()+" "
           //          +missatges.get(i).getIdMateria());
            String txt = coreCfg.getIesClient().getMissatgeriaCollection().getAutoNotes(missatges.get(i).getExpedient(), 
            missatges.get(i).getIdMateria(), idSGD);
            missatges.get(i).setNotes(txt.equals(MissatgeriaCollection.EMPTY_MESSAGE)?"":txt);
         }
         
         jNotes.setText(missatges.get(i).getNotes());
         jAlumne.setText(missatges.get(i).getNomAlumne());
         jGrup.setText(missatges.get(i).getGrupo());
         jTutor.setText(missatges.get(i).getRemitent());
         jData.setText(new DataCtrl(missatges.get(i).getDataEntrevista()).getDiaMesComplet());
         jActitud.setText(missatges.get(i).getActitud());
         jFeina.setText(missatges.get(i).getFeina());
         jObservacions.setText(missatges.get(i).getComentari());
         jNotes.setText(missatges.get(i).getNotes());
         jMateria.setText(missatges.get(i).getMateria());
         mostraImatge(missatges.get(i).getPhoto());
         jNous.setToolTipText("id="+missatges.get(i).getId());
         jPhoto.setToolTipText("Expd:"+missatges.get(i).getExpedient()+",idGrupAsig="+missatges.get(i).getIdMateria());
     }
     
    private void mostraEnviats(int i) {
        //check size
         if(i>=enviats.size()) {
             return;
         }
         
         jAlumne1.setText(enviats.get(i).getNomAlumne());
         jGrup1.setText(enviats.get(i).getGrupo());
         jTutor1.setText(enviats.get(i).getRemitent());
         jData2.setText(new DataCtrl(enviats.get(i).getDataEntrevista()).getDiaMesComplet());
         jActitud1.setText(enviats.get(i).getActitud());
         jFeina1.setText(enviats.get(i).getFeina());
         jObservacions1.setText(enviats.get(i).getComentari());
         jNotes1.setText(enviats.get(i).getNotes());
         jMateria1.setText(enviats.get(i).getMateria());
    }

    private void mostraTaulaEnviats()
    {
        //erase table
        while(jTable1.getRowCount()>0){
             modelTable1.removeRow(0);
        }
           
        enviats = coreCfg.getIesClient().getMissatgeriaCollection().loadEnviats(msort.getMysqlOrder());
        
        java.util.Date now = new java.util.Date();
        
        for(int i=0; i<enviats.size();i++)
        {
            BeanMissatge sms = enviats.get(i);
            boolean mostra = true;
             
                if(jCheckBox1.isSelected() && sms.getDataEntrevista().before(now))
                {
                    mostra = false;
                }
                    
            if(mostra)
            {
                modelTable1.addRow(new Object[]{i,sms.getNomAlumne(), sms.getGrupo(), new DataCtrl(sms.getDataEntrevista()).getDiaMesComplet()});
            }
        }
    }

//    @Override
//    public void setMenus(JMenuBar jMenuBar1, JToolBar jToolbar1, StatusBar jStatusBar1) {
//        //
//    }
// 

}
