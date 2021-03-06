package login;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class AppForm extends javax.swing.JFrame {

    // jdbc config
    private String jdbcDriver = "#####";
    private String jdbcLogin = "#####";
    private String jdbcPassword = "#####";

    private double addingEating = 0;
    private double addingMobile = 0;
    private double addingFlat = 0;
    private double addingFuel = 0;
    private double addingTickets = 0;
    private double addingPayment = 0;
    private double addingOther = 0;
    private double addingTotal = 0;

    private final String JANUARY = "january";
    private final String FEBRUARY = "february";
    private final String MARCH = "march";
    private final String APRIL = "april";
    private final String MAY = "may";
    private final String JUNE = "june";
    private final String JULY = "july";
    private final String AUGUST = "august";
    private final String SEPTEMBER = "september";
    private final String OCTOBER = "october";
    private final String NOVEMBER = "november";
    private final String DECEMBER = "december";

    int xx;
    int yy;

    private Date date;

    public AppForm() {
        initComponents();
    }

    public AppForm(String userName) {
        initComponents();
        jLabel_UserName.setText(userName);

        showExpendingsInJTable(jTable_january, JANUARY);
        showExpendingsInJTable(jTable_february, FEBRUARY);
        showExpendingsInJTable(jTable_march, MARCH);
        showExpendingsInJTable(jTable_april, APRIL);
        showExpendingsInJTable(jTable_may, MAY);
        showExpendingsInJTable(jTable_june, JUNE);
        showExpendingsInJTable(jTable_july, JULY);
        showExpendingsInJTable(jTable_august, AUGUST);
        showExpendingsInJTable(jTable_september, SEPTEMBER);
        showExpendingsInJTable(jTable_october, OCTOBER);
        showExpendingsInJTable(jTable_november, NOVEMBER);
        showExpendingsInJTable(jTable_december, DECEMBER);

        //Show date
        showCurrentDate();
        toggleCurrentMonthTab();
    }

    public Connection getConnection() {
        Connection con;
        try {
            con = DriverManager.getConnection(jdbcDriver, jdbcLogin, jdbcPassword);
            return con;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Expending> getExpendingsList(String month) {
        ArrayList<Expending> expendingsList = new ArrayList<>();
        Connection connection = getConnection();

        String query = "SELECT * FROM `1_" + month + "_2018`";
        Statement st;
        ResultSet rs;

        try {
            st = connection.createStatement();
            rs = st.executeQuery(query);
            Expending expending;
            while (rs.next()) {
                expending = new Expending(rs.getInt("id"), rs.getString("date"), rs.getDouble("eating"), rs.getDouble("mobile"), rs.getDouble("flat"), rs.getDouble("fuel"), rs.getDouble("tickets"), rs.getDouble("payment"), rs.getDouble("other"), rs.getDouble("total"));
                expendingsList.add(expending);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return expendingsList;
    }

    public void showUserIncomingsSave(String month) {
        Connection con = getConnection();
        String query = "SELECT `" + month + "_salary` FROM `dane` WHERE `username`='" + jLabel_UserName.getText() + "'";
        Statement st;
        ResultSet rs;

        try {
            st = con.createStatement();
            rs = st.executeQuery(query);
            rs.next();
            Double incomings = rs.getDouble("" + month + "_salary");
            jTextField_Incomings.setText(Double.toString(incomings));

            double save = incomings - (Double.parseDouble(jTextField_Outgoings.getText()));
            jTextField_Save.setText(Double.toString(save));

            // Change foreground color depending on save balance
            if (save < 0) {
                jTextField_Save.setForeground(Color.RED);
            } else {
                jTextField_Save.setForeground(Color.GREEN);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showExpendingsInJTable(JTable jTable_month, String monthName) {
        ArrayList<Expending> list = getExpendingsList(monthName);
        DefaultTableModel model = (DefaultTableModel) jTable_month.getModel();
        Object[] row = new Object[10];
        for (int i = 0; i < list.size(); i++) {
            row[0] = list.get(i).getId();
            row[1] = list.get(i).getDate();
            row[2] = list.get(i).getEating();
            row[3] = list.get(i).getMobile();
            row[4] = list.get(i).getFlat();
            row[5] = list.get(i).getFuel();
            row[6] = list.get(i).getTickets();
            row[7] = list.get(i).getPayment();
            row[8] = list.get(i).getOther();
            row[9] = list.get(i).getTotal();

            model.addRow(row);
        }
    }

    public void refreshJtableData(JTable jTable_month, String month) {
        DefaultTableModel model = (DefaultTableModel) jTable_month.getModel();
        model.setRowCount(0);
        showExpendingsInJTable(jTable_month, month);
        showSumOfColumns(jTable_month);
        showUserIncomingsSave(month);
    }

    public void executeSQlQuery(JTable jTable_month, String month, String query, String message) {
        Connection con = getConnection();
        Statement st;
        try {
            st = con.createStatement();
            if ((st.executeUpdate(query)) == 1) {
                // Refreshing data in jTable
                refreshJtableData(jTable_month, month);

                JOptionPane.showMessageDialog(null, "Date " + message + " successfully");
            } else {
                JOptionPane.showMessageDialog(null, "Date NOT " + message);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showCurrentDate() {
        this.date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        jLabel_Date.setText("Today is: " + sdf.format(date));
    }

    public void toggleCurrentMonthTab() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        //Selecting jTabbedPanel depend on actual month
        if (calendar.get(Calendar.MONTH) == 0) {
            jTabbedPane1.setSelectedIndex(0);
            showSumOfColumns(jTable_january);
            showUserIncomingsSave(JANUARY);
        } else if (calendar.get(Calendar.MONTH) == 1) {
            jTabbedPane1.setSelectedIndex(1);
            showSumOfColumns(jTable_february);
            showUserIncomingsSave(FEBRUARY);
        } else if (calendar.get(Calendar.MONTH) == 2) {
            jTabbedPane1.setSelectedIndex(2);
            showSumOfColumns(jTable_march);
            showUserIncomingsSave(MARCH);
        } else if (calendar.get(Calendar.MONTH) == 3) {
            jTabbedPane1.setSelectedIndex(3);
            showSumOfColumns(jTable_april);
            showUserIncomingsSave(APRIL);
        } else if (calendar.get(Calendar.MONTH) == 4) {
            jTabbedPane1.setSelectedIndex(4);
            showSumOfColumns(jTable_may);
            showUserIncomingsSave(MAY);
        } else if (calendar.get(Calendar.MONTH) == 5) {
            jTabbedPane1.setSelectedIndex(5);
            showSumOfColumns(jTable_june);
            showUserIncomingsSave(JUNE);
        } else if (calendar.get(Calendar.MONTH) == 6) {
            jTabbedPane1.setSelectedIndex(6);
            showSumOfColumns(jTable_july);
            showUserIncomingsSave(JULY);
        } else if (calendar.get(Calendar.MONTH) == 7) {
            jTabbedPane1.setSelectedIndex(7);
            showSumOfColumns(jTable_august);
            showUserIncomingsSave(AUGUST);
        } else if (calendar.get(Calendar.MONTH) == 8) {
            jTabbedPane1.setSelectedIndex(8);
            showSumOfColumns(jTable_september);
            showUserIncomingsSave(SEPTEMBER);
        } else if (calendar.get(Calendar.MONTH) == 9) {
            jTabbedPane1.setSelectedIndex(9);
            showSumOfColumns(jTable_october);
            showUserIncomingsSave(OCTOBER);
        } else if (calendar.get(Calendar.MONTH) == 10) {
            jTabbedPane1.setSelectedIndex(10);
            showSumOfColumns(jTable_november);
            showUserIncomingsSave(NOVEMBER);
        } else if (calendar.get(Calendar.MONTH) == 11) {
            jTabbedPane1.setSelectedIndex(11);
            showSumOfColumns(jTable_december);
            showUserIncomingsSave(DECEMBER);
        }
    }

    public Double addingMonthlyColumnExpand(JTable jTable_month, double addingSomething, int col) {
        TableModel model = jTable_month.getModel();
        double add = 0;
        for (int i = 0; i < jTable_month.getRowCount(); i++) {
            add = (double) model.getValueAt(i, col);
            addingSomething += add;
        }
        return addingSomething;
    }

    public void showSumOfColumns(JTable jTable_month) {
        double eating = addingMonthlyColumnExpand(jTable_month, addingEating, 2);
        double mobile = addingMonthlyColumnExpand(jTable_month, addingMobile, 3);
        double flat = addingMonthlyColumnExpand(jTable_month, addingFlat, 4);
        double fuel = addingMonthlyColumnExpand(jTable_month, addingFuel, 5);
        double tickets = addingMonthlyColumnExpand(jTable_month, addingTickets, 6);
        double payment = addingMonthlyColumnExpand(jTable_month, addingPayment, 7);
        double other = addingMonthlyColumnExpand(jTable_month, addingOther, 8);
        double outgoings = addingMonthlyColumnExpand(jTable_month, addingTotal, 9);

        jLabel_Eating1.setText(Double.toString(eating));
        jLabel_Mobile1.setText(Double.toString(mobile));
        jLabel_Flat1.setText(Double.toString(flat));
        jLabel_Fuel1.setText(Double.toString(fuel));
        jLabel_Tickets1.setText(Double.toString(tickets));
        jLabel_Payment1.setText(Double.toString(payment));
        jLabel_Other1.setText(Double.toString(other));
        jTextField_Outgoings.setText(Double.toString(outgoings));
        jLabel_Total1.setText(Double.toString(outgoings));
    }

    public void showSelectedExpandingsInTextFields(JTable jTable_month) {
        int i = jTable_month.getSelectedRow();
        TableModel model = jTable_month.getModel();
        jTextField_ID.setText(model.getValueAt(i, 0).toString());
        jTextField_Date.setText(model.getValueAt(i, 1).toString());
        jTextField_Eating.setText(model.getValueAt(i, 2).toString());
        jTextField_Mobile.setText(model.getValueAt(i, 3).toString());
        jTextField_Flat.setText(model.getValueAt(i, 4).toString());
        jTextField_Fuel.setText(model.getValueAt(i, 5).toString());
        jTextField_Tickets.setText(model.getValueAt(i, 6).toString());
        jTextField_Payment.setText(model.getValueAt(i, 7).toString());
        jTextField_Other.setText(model.getValueAt(i, 8).toString());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel_UserName = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jTextField_Incomings = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jTextField_Outgoings = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jTextField_Save = new javax.swing.JTextField();
        jLabel_refresh = new javax.swing.JLabel();
        jLabel_Logout = new javax.swing.JLabel();
        jLabel_Date = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel_Exit = new javax.swing.JLabel();
        jLabel_Iconified = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jTextField_Date = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField_Eating = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField_Mobile = new javax.swing.JTextField();
        jTextField_Flat = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField_Fuel = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTextField_Tickets = new javax.swing.JTextField();
        jTextField_Payment = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jTextField_Other = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel_Edit = new javax.swing.JLabel();
        jLabel_Remove = new javax.swing.JLabel();
        jLabel_Add = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jTextField_ID = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel_Excel = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_january = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable_february = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable_march = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable_april = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable_may = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable_june = new javax.swing.JTable();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable_july = new javax.swing.JTable();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTable_august = new javax.swing.JTable();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTable_september = new javax.swing.JTable();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTable_october = new javax.swing.JTable();
        jScrollPane11 = new javax.swing.JScrollPane();
        jTable_november = new javax.swing.JTable();
        jScrollPane12 = new javax.swing.JScrollPane();
        jTable_december = new javax.swing.JTable();
        jLabel_Total1 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel_Eating1 = new javax.swing.JLabel();
        jLabel_Mobile1 = new javax.swing.JLabel();
        jLabel_Flat1 = new javax.swing.JLabel();
        jLabel_Fuel1 = new javax.swing.JLabel();
        jLabel_Tickets1 = new javax.swing.JLabel();
        jLabel_Payment1 = new javax.swing.JLabel();
        jLabel_Other1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(850, 500));
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel1.setLayout(null);

        jPanel2.setBackground(new java.awt.Color(0, 153, 153));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel2MouseClicked(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Hello:");

        jLabel_UserName.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabel_UserName.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_UserName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_UserName.setText("UserName");

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/user.png"))); // NOI18N

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setPreferredSize(new java.awt.Dimension(0, 2));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2, Short.MAX_VALUE)
        );

        jLabel17.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("Total Incomings:");

        jTextField_Incomings.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jTextField_Incomings.setForeground(new java.awt.Color(51, 51, 51));
        jTextField_Incomings.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_Incomings.setToolTipText("Total Incomings");
        jTextField_Incomings.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTextField_Incomings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_IncomingsActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Total Outgoings:");

        jTextField_Outgoings.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jTextField_Outgoings.setForeground(new java.awt.Color(51, 51, 51));
        jTextField_Outgoings.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_Outgoings.setToolTipText("Total outgoings");
        jTextField_Outgoings.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        jLabel19.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Save Up:");

        jTextField_Save.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jTextField_Save.setForeground(new java.awt.Color(51, 51, 51));
        jTextField_Save.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_Save.setToolTipText("You save");
        jTextField_Save.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        jLabel_refresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/refresh.png"))); // NOI18N
        jLabel_refresh.setText("jLabel4");
        jLabel_refresh.setToolTipText("Refresh incomings");
        jLabel_refresh.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel_refresh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_refreshMouseClicked(evt);
            }
        });

        jLabel_Logout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/logout.png"))); // NOI18N
        jLabel_Logout.setToolTipText("Logout");
        jLabel_Logout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel_Logout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_LogoutMouseClicked(evt);
            }
        });

        jLabel_Date.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel_Date.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_Date.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Date.setText("Date");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel_Logout, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField_Outgoings)
                            .addComponent(jTextField_Incomings)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField_Save)
                            .addComponent(jLabel_UserName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel_Date, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel_Logout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel_UserName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel_Date)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField_Incomings, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField_Outgoings, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField_Save, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(101, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2);
        jPanel2.setBounds(0, 0, 200, 501);

        jPanel3.setBackground(new java.awt.Color(0, 153, 153));

        jLabel_Exit.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel_Exit.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_Exit.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Exit.setText("X");
        jLabel_Exit.setToolTipText("Exit");
        jLabel_Exit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel_Exit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_ExitMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel_Exit, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel_Exit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel3);
        jPanel3.setBounds(810, 0, 44, 50);

        jLabel_Iconified.setFont(new java.awt.Font("Calibri", 1, 24)); // NOI18N
        jLabel_Iconified.setForeground(new java.awt.Color(51, 51, 51));
        jLabel_Iconified.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Iconified.setText("-");
        jLabel_Iconified.setToolTipText("Minimize");
        jLabel_Iconified.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel_Iconified.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_IconifiedMouseClicked(evt);
            }
        });
        jPanel1.add(jLabel_Iconified);
        jLabel_Iconified.setBounds(780, 0, 30, 50);

        jLabel7.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(51, 51, 51));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel7.setText("Date:");

        jTextField_Date.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTextField_Date.setForeground(new java.awt.Color(51, 51, 51));
        jTextField_Date.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_Date.setToolTipText("");
        jTextField_Date.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));

        jLabel8.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 51, 51));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("Eating:");

        jTextField_Eating.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTextField_Eating.setForeground(new java.awt.Color(51, 51, 51));
        jTextField_Eating.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_Eating.setToolTipText("");
        jTextField_Eating.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));

        jLabel9.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(51, 51, 51));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("Mobile:");

        jTextField_Mobile.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTextField_Mobile.setForeground(new java.awt.Color(51, 51, 51));
        jTextField_Mobile.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_Mobile.setToolTipText("");
        jTextField_Mobile.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));

        jTextField_Flat.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTextField_Flat.setForeground(new java.awt.Color(51, 51, 51));
        jTextField_Flat.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_Flat.setToolTipText("");
        jTextField_Flat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));

        jLabel10.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(51, 51, 51));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("Flat:");

        jLabel11.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(51, 51, 51));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText("Fuel:");

        jTextField_Fuel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTextField_Fuel.setForeground(new java.awt.Color(51, 51, 51));
        jTextField_Fuel.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_Fuel.setToolTipText("");
        jTextField_Fuel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));

        jLabel12.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(51, 51, 51));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel12.setText("Tickets:");

        jTextField_Tickets.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTextField_Tickets.setForeground(new java.awt.Color(51, 51, 51));
        jTextField_Tickets.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_Tickets.setToolTipText("");
        jTextField_Tickets.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));

        jTextField_Payment.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTextField_Payment.setForeground(new java.awt.Color(51, 51, 51));
        jTextField_Payment.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_Payment.setToolTipText("");
        jTextField_Payment.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));

        jLabel13.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(51, 51, 51));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("Payment:");

        jLabel14.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(51, 51, 51));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("Other:");

        jTextField_Other.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTextField_Other.setForeground(new java.awt.Color(51, 51, 51));
        jTextField_Other.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_Other.setToolTipText("");
        jTextField_Other.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));

        jLabel5.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 51, 51));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText("Your expendings:");

        jLabel_Edit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/mod.png"))); // NOI18N
        jLabel_Edit.setToolTipText("Edit");
        jLabel_Edit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel_Edit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_EditMouseClicked(evt);
            }
        });

        jLabel_Remove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/delete.png"))); // NOI18N
        jLabel_Remove.setToolTipText("Remove");
        jLabel_Remove.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel_Remove.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_RemoveMouseClicked(evt);
            }
        });

        jLabel_Add.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/add.png"))); // NOI18N
        jLabel_Add.setToolTipText("Add");
        jLabel_Add.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel_Add.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_AddMouseClicked(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(51, 51, 51));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("ID:");

        jTextField_ID.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTextField_ID.setForeground(new java.awt.Color(51, 51, 51));
        jTextField_ID.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_ID.setToolTipText("");
        jTextField_ID.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel_Excel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/excel_col.png"))); // NOI18N
        jLabel_Excel.setToolTipText("Export to XLS");
        jLabel_Excel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel_Excel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_ExcelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField_ID, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_Date, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_Eating, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_Mobile, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_Flat, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_Fuel, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_Tickets, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_Payment, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_Other, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel_Add, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel_Remove, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel_Edit, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel_Excel)
                        .addGap(7, 7, 7))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel_Remove, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel_Edit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel_Add, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel_Excel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel14)
                            .addGap(4, 4, 4)
                            .addComponent(jTextField_Other, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel13)
                            .addGap(4, 4, 4)
                            .addComponent(jTextField_Payment, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel12)
                            .addGap(4, 4, 4)
                            .addComponent(jTextField_Tickets, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel11)
                            .addGap(4, 4, 4)
                            .addComponent(jTextField_Fuel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel10)
                            .addGap(4, 4, 4)
                            .addComponent(jTextField_Flat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel9)
                            .addGap(4, 4, 4)
                            .addComponent(jTextField_Mobile, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(4, 4, 4)
                                .addComponent(jTextField_Eating, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel16))
                                .addGap(4, 4, 4)
                                .addComponent(jTextField_Date, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jTextField_ID, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33))
        );

        jPanel1.add(jPanel4);
        jPanel4.setBounds(200, 50, 650, 100);

        jLabel15.setFont(new java.awt.Font("Calibri", 0, 24)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(51, 51, 51));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Expends Management System");
        jLabel15.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jLabel15MouseDragged(evt);
            }
        });
        jLabel15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel15MousePressed(evt);
            }
        });
        jPanel1.add(jLabel15);
        jLabel15.setBounds(0, 0, 850, 50);

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jTable_january.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTable_january.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Date", "Eating", "Mobile", "Flat", "Fuel", "Tickets", "Payment", "Other", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_january.getTableHeader().setReorderingAllowed(false);
        jTable_january.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_januaryMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable_januaryMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_january);
        if (jTable_january.getColumnModel().getColumnCount() > 0) {
            jTable_january.getColumnModel().getColumn(0).setResizable(false);
            jTable_january.getColumnModel().getColumn(0).setPreferredWidth(30);
            jTable_january.getColumnModel().getColumn(1).setResizable(false);
            jTable_january.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTable_january.getColumnModel().getColumn(2).setResizable(false);
            jTable_january.getColumnModel().getColumn(3).setResizable(false);
            jTable_january.getColumnModel().getColumn(4).setResizable(false);
            jTable_january.getColumnModel().getColumn(5).setResizable(false);
            jTable_january.getColumnModel().getColumn(6).setResizable(false);
            jTable_january.getColumnModel().getColumn(7).setResizable(false);
            jTable_january.getColumnModel().getColumn(8).setResizable(false);
            jTable_january.getColumnModel().getColumn(9).setResizable(false);
        }

        jTabbedPane1.addTab("January 2018", jScrollPane1);

        jTable_february.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTable_february.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Date", "Eating", "Mobile", "Flat", "Fuel", "Tickets", "Payment", "Other", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_february.getTableHeader().setReorderingAllowed(false);
        jTable_february.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_februaryMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable_februaryMousePressed(evt);
            }
        });
        jScrollPane2.setViewportView(jTable_february);
        if (jTable_february.getColumnModel().getColumnCount() > 0) {
            jTable_february.getColumnModel().getColumn(0).setResizable(false);
            jTable_february.getColumnModel().getColumn(0).setPreferredWidth(30);
            jTable_february.getColumnModel().getColumn(1).setResizable(false);
            jTable_february.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTable_february.getColumnModel().getColumn(2).setResizable(false);
            jTable_february.getColumnModel().getColumn(3).setResizable(false);
            jTable_february.getColumnModel().getColumn(4).setResizable(false);
            jTable_february.getColumnModel().getColumn(5).setResizable(false);
            jTable_february.getColumnModel().getColumn(6).setResizable(false);
            jTable_february.getColumnModel().getColumn(7).setResizable(false);
            jTable_february.getColumnModel().getColumn(8).setResizable(false);
            jTable_february.getColumnModel().getColumn(9).setResizable(false);
        }

        jTabbedPane1.addTab("February 2018", jScrollPane2);

        jTable_march.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTable_march.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Date", "Eating", "Mobile", "Flat", "Fuel", "Tickets", "Payment", "Other", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_march.getTableHeader().setReorderingAllowed(false);
        jTable_march.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_marchMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable_marchMousePressed(evt);
            }
        });
        jScrollPane3.setViewportView(jTable_march);
        if (jTable_march.getColumnModel().getColumnCount() > 0) {
            jTable_march.getColumnModel().getColumn(0).setResizable(false);
            jTable_march.getColumnModel().getColumn(0).setPreferredWidth(30);
            jTable_march.getColumnModel().getColumn(1).setResizable(false);
            jTable_march.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTable_march.getColumnModel().getColumn(2).setResizable(false);
            jTable_march.getColumnModel().getColumn(3).setResizable(false);
            jTable_march.getColumnModel().getColumn(4).setResizable(false);
            jTable_march.getColumnModel().getColumn(5).setResizable(false);
            jTable_march.getColumnModel().getColumn(6).setResizable(false);
            jTable_march.getColumnModel().getColumn(7).setResizable(false);
            jTable_march.getColumnModel().getColumn(8).setResizable(false);
            jTable_march.getColumnModel().getColumn(9).setResizable(false);
        }

        jTabbedPane1.addTab("March 2018", jScrollPane3);

        jTable_april.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTable_april.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Date", "Eating", "Mobile", "Flat", "Fuel", "Tickets", "Payment", "Other", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_april.getTableHeader().setReorderingAllowed(false);
        jTable_april.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_aprilMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable_aprilMousePressed(evt);
            }
        });
        jScrollPane4.setViewportView(jTable_april);
        if (jTable_april.getColumnModel().getColumnCount() > 0) {
            jTable_april.getColumnModel().getColumn(0).setResizable(false);
            jTable_april.getColumnModel().getColumn(0).setPreferredWidth(30);
            jTable_april.getColumnModel().getColumn(1).setResizable(false);
            jTable_april.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTable_april.getColumnModel().getColumn(2).setResizable(false);
            jTable_april.getColumnModel().getColumn(3).setResizable(false);
            jTable_april.getColumnModel().getColumn(4).setResizable(false);
            jTable_april.getColumnModel().getColumn(5).setResizable(false);
            jTable_april.getColumnModel().getColumn(6).setResizable(false);
            jTable_april.getColumnModel().getColumn(7).setResizable(false);
            jTable_april.getColumnModel().getColumn(8).setResizable(false);
            jTable_april.getColumnModel().getColumn(9).setResizable(false);
        }

        jTabbedPane1.addTab("April 2018", jScrollPane4);

        jTable_may.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTable_may.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Date", "Eating", "Mobile", "Flat", "Fuel", "Tickets", "Payment", "Other", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_may.getTableHeader().setReorderingAllowed(false);
        jTable_may.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_mayMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable_mayMousePressed(evt);
            }
        });
        jScrollPane5.setViewportView(jTable_may);
        if (jTable_may.getColumnModel().getColumnCount() > 0) {
            jTable_may.getColumnModel().getColumn(0).setResizable(false);
            jTable_may.getColumnModel().getColumn(0).setPreferredWidth(30);
            jTable_may.getColumnModel().getColumn(1).setResizable(false);
            jTable_may.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTable_may.getColumnModel().getColumn(2).setResizable(false);
            jTable_may.getColumnModel().getColumn(3).setResizable(false);
            jTable_may.getColumnModel().getColumn(4).setResizable(false);
            jTable_may.getColumnModel().getColumn(5).setResizable(false);
            jTable_may.getColumnModel().getColumn(6).setResizable(false);
            jTable_may.getColumnModel().getColumn(7).setResizable(false);
            jTable_may.getColumnModel().getColumn(8).setResizable(false);
            jTable_may.getColumnModel().getColumn(9).setResizable(false);
        }

        jTabbedPane1.addTab("May 2018", jScrollPane5);

        jTable_june.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTable_june.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Date", "Eating", "Mobile", "Flat", "Fuel", "Tickets", "Payment", "Other", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_june.getTableHeader().setReorderingAllowed(false);
        jTable_june.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_juneMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable_juneMousePressed(evt);
            }
        });
        jScrollPane6.setViewportView(jTable_june);
        if (jTable_june.getColumnModel().getColumnCount() > 0) {
            jTable_june.getColumnModel().getColumn(0).setResizable(false);
            jTable_june.getColumnModel().getColumn(0).setPreferredWidth(30);
            jTable_june.getColumnModel().getColumn(1).setResizable(false);
            jTable_june.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTable_june.getColumnModel().getColumn(2).setResizable(false);
            jTable_june.getColumnModel().getColumn(3).setResizable(false);
            jTable_june.getColumnModel().getColumn(4).setResizable(false);
            jTable_june.getColumnModel().getColumn(5).setResizable(false);
            jTable_june.getColumnModel().getColumn(6).setResizable(false);
            jTable_june.getColumnModel().getColumn(7).setResizable(false);
            jTable_june.getColumnModel().getColumn(8).setResizable(false);
            jTable_june.getColumnModel().getColumn(9).setResizable(false);
        }

        jTabbedPane1.addTab("June 2018", jScrollPane6);

        jTable_july.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTable_july.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Date", "Eating", "Mobile", "Flat", "Fuel", "Tickets", "Payment", "Other", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_july.getTableHeader().setReorderingAllowed(false);
        jTable_july.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_julyMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable_julyMousePressed(evt);
            }
        });
        jScrollPane7.setViewportView(jTable_july);
        if (jTable_july.getColumnModel().getColumnCount() > 0) {
            jTable_july.getColumnModel().getColumn(0).setResizable(false);
            jTable_july.getColumnModel().getColumn(0).setPreferredWidth(30);
            jTable_july.getColumnModel().getColumn(1).setResizable(false);
            jTable_july.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTable_july.getColumnModel().getColumn(2).setResizable(false);
            jTable_july.getColumnModel().getColumn(3).setResizable(false);
            jTable_july.getColumnModel().getColumn(4).setResizable(false);
            jTable_july.getColumnModel().getColumn(5).setResizable(false);
            jTable_july.getColumnModel().getColumn(6).setResizable(false);
            jTable_july.getColumnModel().getColumn(7).setResizable(false);
            jTable_july.getColumnModel().getColumn(8).setResizable(false);
            jTable_july.getColumnModel().getColumn(9).setResizable(false);
        }

        jTabbedPane1.addTab("July 2018", jScrollPane7);

        jTable_august.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTable_august.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Date", "Eating", "Mobile", "Flat", "Fuel", "Tickets", "Payment", "Other", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_august.getTableHeader().setReorderingAllowed(false);
        jTable_august.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_augustMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable_augustMousePressed(evt);
            }
        });
        jScrollPane8.setViewportView(jTable_august);
        if (jTable_august.getColumnModel().getColumnCount() > 0) {
            jTable_august.getColumnModel().getColumn(0).setResizable(false);
            jTable_august.getColumnModel().getColumn(0).setPreferredWidth(30);
            jTable_august.getColumnModel().getColumn(1).setResizable(false);
            jTable_august.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTable_august.getColumnModel().getColumn(2).setResizable(false);
            jTable_august.getColumnModel().getColumn(3).setResizable(false);
            jTable_august.getColumnModel().getColumn(4).setResizable(false);
            jTable_august.getColumnModel().getColumn(5).setResizable(false);
            jTable_august.getColumnModel().getColumn(6).setResizable(false);
            jTable_august.getColumnModel().getColumn(7).setResizable(false);
            jTable_august.getColumnModel().getColumn(8).setResizable(false);
            jTable_august.getColumnModel().getColumn(9).setResizable(false);
        }

        jTabbedPane1.addTab("August 2018", jScrollPane8);

        jTable_september.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTable_september.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Date", "Eating", "Mobile", "Flat", "Fuel", "Tickets", "Payment", "Other", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_september.getTableHeader().setReorderingAllowed(false);
        jTable_september.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_septemberMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable_septemberMousePressed(evt);
            }
        });
        jScrollPane9.setViewportView(jTable_september);
        if (jTable_september.getColumnModel().getColumnCount() > 0) {
            jTable_september.getColumnModel().getColumn(0).setResizable(false);
            jTable_september.getColumnModel().getColumn(0).setPreferredWidth(30);
            jTable_september.getColumnModel().getColumn(1).setResizable(false);
            jTable_september.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTable_september.getColumnModel().getColumn(2).setResizable(false);
            jTable_september.getColumnModel().getColumn(3).setResizable(false);
            jTable_september.getColumnModel().getColumn(4).setResizable(false);
            jTable_september.getColumnModel().getColumn(5).setResizable(false);
            jTable_september.getColumnModel().getColumn(6).setResizable(false);
            jTable_september.getColumnModel().getColumn(7).setResizable(false);
            jTable_september.getColumnModel().getColumn(8).setResizable(false);
            jTable_september.getColumnModel().getColumn(9).setResizable(false);
        }

        jTabbedPane1.addTab("September 2018", jScrollPane9);

        jTable_october.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTable_october.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Date", "Eating", "Mobile", "Flat", "Fuel", "Tickets", "Payment", "Other", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_october.getTableHeader().setReorderingAllowed(false);
        jTable_october.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_octoberMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable_octoberMousePressed(evt);
            }
        });
        jScrollPane10.setViewportView(jTable_october);
        if (jTable_october.getColumnModel().getColumnCount() > 0) {
            jTable_october.getColumnModel().getColumn(0).setResizable(false);
            jTable_october.getColumnModel().getColumn(0).setPreferredWidth(30);
            jTable_october.getColumnModel().getColumn(1).setResizable(false);
            jTable_october.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTable_october.getColumnModel().getColumn(2).setResizable(false);
            jTable_october.getColumnModel().getColumn(3).setResizable(false);
            jTable_october.getColumnModel().getColumn(4).setResizable(false);
            jTable_october.getColumnModel().getColumn(5).setResizable(false);
            jTable_october.getColumnModel().getColumn(6).setResizable(false);
            jTable_october.getColumnModel().getColumn(7).setResizable(false);
            jTable_october.getColumnModel().getColumn(8).setResizable(false);
            jTable_october.getColumnModel().getColumn(9).setResizable(false);
        }

        jTabbedPane1.addTab("October 2018", jScrollPane10);

        jTable_november.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTable_november.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Date", "Eating", "Mobile", "Flat", "Fuel", "Tickets", "Payment", "Other", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_november.getTableHeader().setReorderingAllowed(false);
        jTable_november.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_novemberMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable_novemberMousePressed(evt);
            }
        });
        jScrollPane11.setViewportView(jTable_november);
        if (jTable_november.getColumnModel().getColumnCount() > 0) {
            jTable_november.getColumnModel().getColumn(0).setResizable(false);
            jTable_november.getColumnModel().getColumn(0).setPreferredWidth(30);
            jTable_november.getColumnModel().getColumn(1).setResizable(false);
            jTable_november.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTable_november.getColumnModel().getColumn(2).setResizable(false);
            jTable_november.getColumnModel().getColumn(3).setResizable(false);
            jTable_november.getColumnModel().getColumn(4).setResizable(false);
            jTable_november.getColumnModel().getColumn(5).setResizable(false);
            jTable_november.getColumnModel().getColumn(6).setResizable(false);
            jTable_november.getColumnModel().getColumn(7).setResizable(false);
            jTable_november.getColumnModel().getColumn(8).setResizable(false);
            jTable_november.getColumnModel().getColumn(9).setResizable(false);
        }

        jTabbedPane1.addTab("November 2018", jScrollPane11);

        jTable_december.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTable_december.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Date", "Eating", "Mobile", "Flat", "Fuel", "Tickets", "Payment", "Other", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_december.getTableHeader().setReorderingAllowed(false);
        jTable_december.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_decemberMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable_decemberMousePressed(evt);
            }
        });
        jScrollPane12.setViewportView(jTable_december);
        if (jTable_december.getColumnModel().getColumnCount() > 0) {
            jTable_december.getColumnModel().getColumn(0).setResizable(false);
            jTable_december.getColumnModel().getColumn(0).setPreferredWidth(30);
            jTable_december.getColumnModel().getColumn(1).setResizable(false);
            jTable_december.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTable_december.getColumnModel().getColumn(2).setResizable(false);
            jTable_december.getColumnModel().getColumn(3).setResizable(false);
            jTable_december.getColumnModel().getColumn(4).setResizable(false);
            jTable_december.getColumnModel().getColumn(5).setResizable(false);
            jTable_december.getColumnModel().getColumn(6).setResizable(false);
            jTable_december.getColumnModel().getColumn(7).setResizable(false);
            jTable_december.getColumnModel().getColumn(8).setResizable(false);
            jTable_december.getColumnModel().getColumn(9).setResizable(false);
        }

        jTabbedPane1.addTab("December 2018", jScrollPane12);

        jPanel1.add(jTabbedPane1);
        jTabbedPane1.setBounds(210, 160, 630, 310);

        jLabel_Total1.setFont(new java.awt.Font("Calibri", 1, 12)); // NOI18N
        jLabel_Total1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel_Total1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel_Total1.setText("0.0");
        jPanel1.add(jLabel_Total1);
        jLabel_Total1.setBounds(760, 470, 60, 20);

        jLabel21.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(51, 51, 51));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("SUM:");
        jPanel1.add(jLabel21);
        jLabel21.setBounds(240, 470, 80, 20);

        jLabel_Eating1.setFont(new java.awt.Font("Calibri", 1, 12)); // NOI18N
        jLabel_Eating1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel_Eating1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel_Eating1.setText("0.0");
        jPanel1.add(jLabel_Eating1);
        jLabel_Eating1.setBounds(320, 470, 60, 20);

        jLabel_Mobile1.setFont(new java.awt.Font("Calibri", 1, 12)); // NOI18N
        jLabel_Mobile1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel_Mobile1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel_Mobile1.setText("0.0");
        jPanel1.add(jLabel_Mobile1);
        jLabel_Mobile1.setBounds(380, 470, 60, 20);

        jLabel_Flat1.setFont(new java.awt.Font("Calibri", 1, 12)); // NOI18N
        jLabel_Flat1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel_Flat1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel_Flat1.setText("0.0");
        jPanel1.add(jLabel_Flat1);
        jLabel_Flat1.setBounds(450, 470, 60, 20);

        jLabel_Fuel1.setFont(new java.awt.Font("Calibri", 1, 12)); // NOI18N
        jLabel_Fuel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel_Fuel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel_Fuel1.setText("0.0");
        jPanel1.add(jLabel_Fuel1);
        jLabel_Fuel1.setBounds(510, 470, 60, 20);

        jLabel_Tickets1.setFont(new java.awt.Font("Calibri", 1, 12)); // NOI18N
        jLabel_Tickets1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel_Tickets1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel_Tickets1.setText("0.0");
        jPanel1.add(jLabel_Tickets1);
        jLabel_Tickets1.setBounds(570, 470, 60, 20);

        jLabel_Payment1.setFont(new java.awt.Font("Calibri", 1, 12)); // NOI18N
        jLabel_Payment1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel_Payment1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel_Payment1.setText("0.0");
        jPanel1.add(jLabel_Payment1);
        jLabel_Payment1.setBounds(630, 470, 60, 20);

        jLabel_Other1.setFont(new java.awt.Font("Calibri", 1, 12)); // NOI18N
        jLabel_Other1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel_Other1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel_Other1.setText("0.0");
        jPanel1.add(jLabel_Other1);
        jLabel_Other1.setBounds(690, 470, 60, 20);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 850, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setSize(new java.awt.Dimension(850, 500));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel_ExitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_ExitMouseClicked
        // Exit window
        dispose();
    }//GEN-LAST:event_jLabel_ExitMouseClicked

    private void jLabel_IconifiedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_IconifiedMouseClicked
        // Iconified window
        this.setState(JFrame.ICONIFIED);
    }//GEN-LAST:event_jLabel_IconifiedMouseClicked

    private void jLabel_EditMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_EditMouseClicked
        if (jTable_january.isShowing()) {
            String query = "UPDATE `1_january_2018` SET `date`='" + jTextField_Date.getText() + "',`eating`='" + jTextField_Eating.getText() + "',`mobile`='" + jTextField_Mobile.getText() + "',`flat`='" + jTextField_Flat.getText() + "',`fuel`='" + jTextField_Fuel.getText() + "',`tickets`='" + jTextField_Tickets.getText() + "',`payment`='" + jTextField_Payment.getText() + "',`other`='" + jTextField_Other.getText() + "' WHERE `id`=" + jTextField_ID.getText();
            executeSQlQuery(jTable_january, JANUARY, query, "Updated");
        } else if (jTable_february.isShowing()) {
            String query = "UPDATE `1_february_2018` SET `date`='" + jTextField_Date.getText() + "',`eating`='" + jTextField_Eating.getText() + "',`mobile`='" + jTextField_Mobile.getText() + "',`flat`='" + jTextField_Flat.getText() + "',`fuel`='" + jTextField_Fuel.getText() + "',`tickets`='" + jTextField_Tickets.getText() + "',`payment`='" + jTextField_Payment.getText() + "',`other`='" + jTextField_Other.getText() + "' WHERE `id`=" + jTextField_ID.getText();
            executeSQlQuery(jTable_february, FEBRUARY, query, "Updated");
        } else if (jTable_march.isShowing()) {
            String query = "UPDATE `1_march_2018` SET `date`='" + jTextField_Date.getText() + "',`eating`='" + jTextField_Eating.getText() + "',`mobile`='" + jTextField_Mobile.getText() + "',`flat`='" + jTextField_Flat.getText() + "',`fuel`='" + jTextField_Fuel.getText() + "',`tickets`='" + jTextField_Tickets.getText() + "',`payment`='" + jTextField_Payment.getText() + "',`other`='" + jTextField_Other.getText() + "' WHERE `id`=" + jTextField_ID.getText();
            executeSQlQuery(jTable_march, MARCH, query, "Updated");
        } else if (jTable_april.isShowing()) {
            String query = "UPDATE `1_april_2018` SET `date`='" + jTextField_Date.getText() + "',`eating`='" + jTextField_Eating.getText() + "',`mobile`='" + jTextField_Mobile.getText() + "',`flat`='" + jTextField_Flat.getText() + "',`fuel`='" + jTextField_Fuel.getText() + "',`tickets`='" + jTextField_Tickets.getText() + "',`payment`='" + jTextField_Payment.getText() + "',`other`='" + jTextField_Other.getText() + "' WHERE `id`=" + jTextField_ID.getText();
            executeSQlQuery(jTable_april, APRIL, query, "Updated");
        } else if (jTable_may.isShowing()) {
            String query = "UPDATE `1_may_2018` SET `date`='" + jTextField_Date.getText() + "',`eating`='" + jTextField_Eating.getText() + "',`mobile`='" + jTextField_Mobile.getText() + "',`flat`='" + jTextField_Flat.getText() + "',`fuel`='" + jTextField_Fuel.getText() + "',`tickets`='" + jTextField_Tickets.getText() + "',`payment`='" + jTextField_Payment.getText() + "',`other`='" + jTextField_Other.getText() + "' WHERE `id`=" + jTextField_ID.getText();
            executeSQlQuery(jTable_may, MAY, query, "Updated");
        } else if (jTable_june.isShowing()) {
            String query = "UPDATE `1_june_2018` SET `date`='" + jTextField_Date.getText() + "',`eating`='" + jTextField_Eating.getText() + "',`mobile`='" + jTextField_Mobile.getText() + "',`flat`='" + jTextField_Flat.getText() + "',`fuel`='" + jTextField_Fuel.getText() + "',`tickets`='" + jTextField_Tickets.getText() + "',`payment`='" + jTextField_Payment.getText() + "',`other`='" + jTextField_Other.getText() + "' WHERE `id`=" + jTextField_ID.getText();
            executeSQlQuery(jTable_june, JUNE, query, "Updated");
        } else if (jTable_july.isShowing()) {
            String query = "UPDATE `1_july_2018` SET `date`='" + jTextField_Date.getText() + "',`eating`='" + jTextField_Eating.getText() + "',`mobile`='" + jTextField_Mobile.getText() + "',`flat`='" + jTextField_Flat.getText() + "',`fuel`='" + jTextField_Fuel.getText() + "',`tickets`='" + jTextField_Tickets.getText() + "',`payment`='" + jTextField_Payment.getText() + "',`other`='" + jTextField_Other.getText() + "' WHERE `id`=" + jTextField_ID.getText();
            executeSQlQuery(jTable_july, JULY, query, "Updated");
        } else if (jTable_august.isShowing()) {
            String query = "UPDATE `1_august_2018` SET `date`='" + jTextField_Date.getText() + "',`eating`='" + jTextField_Eating.getText() + "',`mobile`='" + jTextField_Mobile.getText() + "',`flat`='" + jTextField_Flat.getText() + "',`fuel`='" + jTextField_Fuel.getText() + "',`tickets`='" + jTextField_Tickets.getText() + "',`payment`='" + jTextField_Payment.getText() + "',`other`='" + jTextField_Other.getText() + "' WHERE `id`=" + jTextField_ID.getText();
            executeSQlQuery(jTable_august, AUGUST, query, "Updated");
        } else if (jTable_september.isShowing()) {
            String query = "UPDATE `1_september_2018` SET `date`='" + jTextField_Date.getText() + "',`eating`='" + jTextField_Eating.getText() + "',`mobile`='" + jTextField_Mobile.getText() + "',`flat`='" + jTextField_Flat.getText() + "',`fuel`='" + jTextField_Fuel.getText() + "',`tickets`='" + jTextField_Tickets.getText() + "',`payment`='" + jTextField_Payment.getText() + "',`other`='" + jTextField_Other.getText() + "' WHERE `id`=" + jTextField_ID.getText();
            executeSQlQuery(jTable_september, SEPTEMBER, query, "Updated");
        } else if (jTable_october.isShowing()) {
            String query = "UPDATE `1_october_2018` SET `date`='" + jTextField_Date.getText() + "',`eating`='" + jTextField_Eating.getText() + "',`mobile`='" + jTextField_Mobile.getText() + "',`flat`='" + jTextField_Flat.getText() + "',`fuel`='" + jTextField_Fuel.getText() + "',`tickets`='" + jTextField_Tickets.getText() + "',`payment`='" + jTextField_Payment.getText() + "',`other`='" + jTextField_Other.getText() + "' WHERE `id`=" + jTextField_ID.getText();
            executeSQlQuery(jTable_october, OCTOBER, query, "Updated");
        } else if (jTable_november.isShowing()) {
            String query = "UPDATE `1_november_2018` SET `date`='" + jTextField_Date.getText() + "',`eating`='" + jTextField_Eating.getText() + "',`mobile`='" + jTextField_Mobile.getText() + "',`flat`='" + jTextField_Flat.getText() + "',`fuel`='" + jTextField_Fuel.getText() + "',`tickets`='" + jTextField_Tickets.getText() + "',`payment`='" + jTextField_Payment.getText() + "',`other`='" + jTextField_Other.getText() + "' WHERE `id`=" + jTextField_ID.getText();
            executeSQlQuery(jTable_november, NOVEMBER, query, "Updated");
        } else if (jTable_december.isShowing()) {
            String query = "UPDATE `1_december_2018` SET `date`='" + jTextField_Date.getText() + "',`eating`='" + jTextField_Eating.getText() + "',`mobile`='" + jTextField_Mobile.getText() + "',`flat`='" + jTextField_Flat.getText() + "',`fuel`='" + jTextField_Fuel.getText() + "',`tickets`='" + jTextField_Tickets.getText() + "',`payment`='" + jTextField_Payment.getText() + "',`other`='" + jTextField_Other.getText() + "' WHERE `id`=" + jTextField_ID.getText();
            executeSQlQuery(jTable_december, DECEMBER, query, "Updated");
        }
    }//GEN-LAST:event_jLabel_EditMouseClicked

    private void jTable_januaryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_januaryMouseClicked

    }//GEN-LAST:event_jTable_januaryMouseClicked

    private void jLabel_RemoveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_RemoveMouseClicked
        if (jTable_january.isShowing()) {
            String query = "DELETE FROM `1_january_2018` WHERE id =" + jTextField_ID.getText();
            executeSQlQuery(jTable_january, JANUARY, query, "Deleted");
        } else if (jTable_february.isShowing()) {
            String query = "DELETE FROM `1_february_2018` WHERE id =" + jTextField_ID.getText();
            executeSQlQuery(jTable_february, FEBRUARY, query, "Deleted");
        } else if (jTable_march.isShowing()) {
            String query = "DELETE FROM `1_march_2018` WHERE id =" + jTextField_ID.getText();
            executeSQlQuery(jTable_march, MARCH, query, "Deleted");
        } else if (jTable_april.isShowing()) {
            String query = "DELETE FROM `1_april_2018` WHERE id =" + jTextField_ID.getText();
            executeSQlQuery(jTable_april, APRIL, query, "Deleted");
        } else if (jTable_may.isShowing()) {
            String query = "DELETE FROM `1_may_2018` WHERE id =" + jTextField_ID.getText();
            executeSQlQuery(jTable_may, MAY, query, "Deleted");
        } else if (jTable_june.isShowing()) {
            String query = "DELETE FROM `1_june_2018` WHERE id =" + jTextField_ID.getText();
            executeSQlQuery(jTable_june, JUNE, query, "Deleted");
        } else if (jTable_july.isShowing()) {
            String query = "DELETE FROM `1_july_2018` WHERE id =" + jTextField_ID.getText();
            executeSQlQuery(jTable_july, JULY, query, "Deleted");
        } else if (jTable_august.isShowing()) {
            String query = "DELETE FROM `1_august_2018` WHERE id =" + jTextField_ID.getText();
            executeSQlQuery(jTable_august, AUGUST, query, "Deleted");
        } else if (jTable_september.isShowing()) {
            String query = "DELETE FROM `1_september_2018` WHERE id =" + jTextField_ID.getText();
            executeSQlQuery(jTable_september, SEPTEMBER, query, "Deleted");
        } else if (jTable_october.isShowing()) {
            String query = "DELETE FROM `1_october_2018` WHERE id =" + jTextField_ID.getText();
            executeSQlQuery(jTable_october, OCTOBER, query, "Deleted");
        } else if (jTable_november.isShowing()) {
            String query = "DELETE FROM `1_november_2018` WHERE id =" + jTextField_ID.getText();
            executeSQlQuery(jTable_november, NOVEMBER, query, "Deleted");
        } else if (jTable_december.isShowing()) {
            String query = "DELETE FROM `1_december_2018` WHERE id =" + jTextField_ID.getText();
            executeSQlQuery(jTable_december, DECEMBER, query, "Deleted");
        }
    }//GEN-LAST:event_jLabel_RemoveMouseClicked

    private void jLabel_AddMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_AddMouseClicked
        if (jTable_january.isShowing()) {
            String query = "INSERT INTO `1_january_2018`(`date`, `eating`, `mobile`, `flat`, `fuel`, `tickets`, `payment`, `other`) VALUES ('" + jTextField_Date.getText() + "'," + jTextField_Eating.getText() + "," + jTextField_Mobile.getText() + "," + jTextField_Flat.getText() + "," + jTextField_Fuel.getText() + "," + jTextField_Tickets.getText() + "," + jTextField_Payment.getText() + "," + jTextField_Other.getText() + ")";
            executeSQlQuery(jTable_january, JANUARY, query, "Added");
        } else if (jTable_february.isShowing()) {
            String query = "INSERT INTO `1_february_2018`(`date`, `eating`, `mobile`, `flat`, `fuel`, `tickets`, `payment`, `other`) VALUES ('" + jTextField_Date.getText() + "'," + jTextField_Eating.getText() + "," + jTextField_Mobile.getText() + "," + jTextField_Flat.getText() + "," + jTextField_Fuel.getText() + "," + jTextField_Tickets.getText() + "," + jTextField_Payment.getText() + "," + jTextField_Other.getText() + ")";
            executeSQlQuery(jTable_february, FEBRUARY, query, "Added");
        } else if (jTable_march.isShowing()) {
            String query = "INSERT INTO `1_march_2018`(`date`, `eating`, `mobile`, `flat`, `fuel`, `tickets`, `payment`, `other`) VALUES ('" + jTextField_Date.getText() + "'," + jTextField_Eating.getText() + "," + jTextField_Mobile.getText() + "," + jTextField_Flat.getText() + "," + jTextField_Fuel.getText() + "," + jTextField_Tickets.getText() + "," + jTextField_Payment.getText() + "," + jTextField_Other.getText() + ")";
            executeSQlQuery(jTable_march, MARCH, query, "Added");
        } else if (jTable_april.isShowing()) {
            String query = "INSERT INTO `1_april_2018`(`date`, `eating`, `mobile`, `flat`, `fuel`, `tickets`, `payment`, `other`) VALUES ('" + jTextField_Date.getText() + "'," + jTextField_Eating.getText() + "," + jTextField_Mobile.getText() + "," + jTextField_Flat.getText() + "," + jTextField_Fuel.getText() + "," + jTextField_Tickets.getText() + "," + jTextField_Payment.getText() + "," + jTextField_Other.getText() + ")";
            executeSQlQuery(jTable_april, APRIL, query, "Added");
        } else if (jTable_may.isShowing()) {
            String query = "INSERT INTO `1_may_2018`(`date`, `eating`, `mobile`, `flat`, `fuel`, `tickets`, `payment`, `other`) VALUES ('" + jTextField_Date.getText() + "'," + jTextField_Eating.getText() + "," + jTextField_Mobile.getText() + "," + jTextField_Flat.getText() + "," + jTextField_Fuel.getText() + "," + jTextField_Tickets.getText() + "," + jTextField_Payment.getText() + "," + jTextField_Other.getText() + ")";
            executeSQlQuery(jTable_may, MAY, query, "Added");
        } else if (jTable_june.isShowing()) {
            String query = "INSERT INTO `1_june_2018`(`date`, `eating`, `mobile`, `flat`, `fuel`, `tickets`, `payment`, `other`) VALUES ('" + jTextField_Date.getText() + "'," + jTextField_Eating.getText() + "," + jTextField_Mobile.getText() + "," + jTextField_Flat.getText() + "," + jTextField_Fuel.getText() + "," + jTextField_Tickets.getText() + "," + jTextField_Payment.getText() + "," + jTextField_Other.getText() + ")";
            executeSQlQuery(jTable_june, JUNE, query, "Added");
        } else if (jTable_july.isShowing()) {
            String query = "INSERT INTO `1_july_2018`(`date`, `eating`, `mobile`, `flat`, `fuel`, `tickets`, `payment`, `other`) VALUES ('" + jTextField_Date.getText() + "'," + jTextField_Eating.getText() + "," + jTextField_Mobile.getText() + "," + jTextField_Flat.getText() + "," + jTextField_Fuel.getText() + "," + jTextField_Tickets.getText() + "," + jTextField_Payment.getText() + "," + jTextField_Other.getText() + ")";
            executeSQlQuery(jTable_july, JULY, query, "Added");
        } else if (jTable_august.isShowing()) {
            String query = "INSERT INTO `1_august_2018`(`date`, `eating`, `mobile`, `flat`, `fuel`, `tickets`, `payment`, `other`) VALUES ('" + jTextField_Date.getText() + "'," + jTextField_Eating.getText() + "," + jTextField_Mobile.getText() + "," + jTextField_Flat.getText() + "," + jTextField_Fuel.getText() + "," + jTextField_Tickets.getText() + "," + jTextField_Payment.getText() + "," + jTextField_Other.getText() + ")";
            executeSQlQuery(jTable_august, AUGUST, query, "Added");
        } else if (jTable_september.isShowing()) {
            String query = "INSERT INTO `1_september_2018`(`date`, `eating`, `mobile`, `flat`, `fuel`, `tickets`, `payment`, `other`) VALUES ('" + jTextField_Date.getText() + "'," + jTextField_Eating.getText() + "," + jTextField_Mobile.getText() + "," + jTextField_Flat.getText() + "," + jTextField_Fuel.getText() + "," + jTextField_Tickets.getText() + "," + jTextField_Payment.getText() + "," + jTextField_Other.getText() + ")";
            executeSQlQuery(jTable_september, SEPTEMBER, query, "Added");
        } else if (jTable_october.isShowing()) {
            String query = "INSERT INTO `1_october_2018`(`date`, `eating`, `mobile`, `flat`, `fuel`, `tickets`, `payment`, `other`) VALUES ('" + jTextField_Date.getText() + "'," + jTextField_Eating.getText() + "," + jTextField_Mobile.getText() + "," + jTextField_Flat.getText() + "," + jTextField_Fuel.getText() + "," + jTextField_Tickets.getText() + "," + jTextField_Payment.getText() + "," + jTextField_Other.getText() + ")";
            executeSQlQuery(jTable_october, OCTOBER, query, "Added");
        } else if (jTable_november.isShowing()) {
            String query = "INSERT INTO `1_november_2018`(`date`, `eating`, `mobile`, `flat`, `fuel`, `tickets`, `payment`, `other`) VALUES ('" + jTextField_Date.getText() + "'," + jTextField_Eating.getText() + "," + jTextField_Mobile.getText() + "," + jTextField_Flat.getText() + "," + jTextField_Fuel.getText() + "," + jTextField_Tickets.getText() + "," + jTextField_Payment.getText() + "," + jTextField_Other.getText() + ")";
            executeSQlQuery(jTable_november, NOVEMBER, query, "Added");
        } else if (jTable_december.isShowing()) {
            String query = "INSERT INTO `1_december_2018`(`date`, `eating`, `mobile`, `flat`, `fuel`, `tickets`, `payment`, `other`) VALUES ('" + jTextField_Date.getText() + "'," + jTextField_Eating.getText() + "," + jTextField_Mobile.getText() + "," + jTextField_Flat.getText() + "," + jTextField_Fuel.getText() + "," + jTextField_Tickets.getText() + "," + jTextField_Payment.getText() + "," + jTextField_Other.getText() + ")";
            executeSQlQuery(jTable_december, DECEMBER, query, "Added");
        }
    }//GEN-LAST:event_jLabel_AddMouseClicked

    private void jTextField_IncomingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_IncomingsActionPerformed

    }//GEN-LAST:event_jTextField_IncomingsActionPerformed

    private void jTable_februaryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_februaryMouseClicked

    }//GEN-LAST:event_jTable_februaryMouseClicked

    private void jTable_marchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_marchMouseClicked

    }//GEN-LAST:event_jTable_marchMouseClicked

    private void jTable_aprilMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_aprilMouseClicked

    }//GEN-LAST:event_jTable_aprilMouseClicked

    private void jTable_mayMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_mayMouseClicked

    }//GEN-LAST:event_jTable_mayMouseClicked

    private void jTable_juneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_juneMouseClicked

    }//GEN-LAST:event_jTable_juneMouseClicked

    private void jTable_julyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_julyMouseClicked

    }//GEN-LAST:event_jTable_julyMouseClicked

    private void jTable_augustMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_augustMouseClicked

    }//GEN-LAST:event_jTable_augustMouseClicked

    private void jTable_septemberMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_septemberMouseClicked

    }//GEN-LAST:event_jTable_septemberMouseClicked

    private void jTable_octoberMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_octoberMouseClicked

    }//GEN-LAST:event_jTable_octoberMouseClicked

    private void jTable_novemberMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_novemberMouseClicked

    }//GEN-LAST:event_jTable_novemberMouseClicked

    private void jTable_decemberMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_decemberMouseClicked

    }//GEN-LAST:event_jTable_decemberMouseClicked

    private void jLabel_refreshMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_refreshMouseClicked
        if (jTable_january.isShowing()) {
            String query = "UPDATE `dane` SET `january_salary`='" + jTextField_Incomings.getText() + "' WHERE `username`='" + jLabel_UserName.getText() + "'";
            executeSQlQuery(jTable_january, JANUARY, query, "Refreshed");
            showSumOfColumns(jTable_january);
            showUserIncomingsSave(JANUARY);

        } else if (jTable_february.isShowing()) {
            String query = "UPDATE `dane` SET `february_salary`='" + jTextField_Incomings.getText() + "' WHERE `username`='" + jLabel_UserName.getText() + "'";
            executeSQlQuery(jTable_february, FEBRUARY, query, "Refreshed");
            showSumOfColumns(jTable_february);
            showUserIncomingsSave(FEBRUARY);

        } else if (jTable_march.isShowing()) {
            String query = "UPDATE `dane` SET `march_salary`='" + jTextField_Incomings.getText() + "' WHERE `username`='" + jLabel_UserName.getText() + "'";
            executeSQlQuery(jTable_march, MARCH, query, "Refreshed");
            showSumOfColumns(jTable_march);
            showUserIncomingsSave(MARCH);

        } else if (jTable_april.isShowing()) {
            String query = "UPDATE `dane` SET `april_salary`='" + jTextField_Incomings.getText() + "' WHERE `username`='" + jLabel_UserName.getText() + "'";
            executeSQlQuery(jTable_april, APRIL, query, "Refreshed");
            showSumOfColumns(jTable_april);
            showUserIncomingsSave(APRIL);

        } else if (jTable_may.isShowing()) {
            String query = "UPDATE `dane` SET `may_salary`='" + jTextField_Incomings.getText() + "' WHERE `username`='" + jLabel_UserName.getText() + "'";
            executeSQlQuery(jTable_may, MAY, query, "Refreshed");
            showSumOfColumns(jTable_may);
            showUserIncomingsSave(MAY);

        } else if (jTable_june.isShowing()) {
            String query = "UPDATE `dane` SET `june_salary`='" + jTextField_Incomings.getText() + "' WHERE `username`='" + jLabel_UserName.getText() + "'";
            executeSQlQuery(jTable_june, JUNE, query, "Refreshed");
            showSumOfColumns(jTable_june);
            showUserIncomingsSave(JUNE);

        } else if (jTable_july.isShowing()) {
            String query = "UPDATE `dane` SET `july_salary`='" + jTextField_Incomings.getText() + "' WHERE `username`='" + jLabel_UserName.getText() + "'";
            executeSQlQuery(jTable_july, JULY, query, "Refreshed");
            showSumOfColumns(jTable_july);
            showUserIncomingsSave(JULY);

        } else if (jTable_august.isShowing()) {
            String query = "UPDATE `dane` SET `august_salary`='" + jTextField_Incomings.getText() + "' WHERE `username`='" + jLabel_UserName.getText() + "'";
            executeSQlQuery(jTable_august, AUGUST, query, "Refreshed");
            showSumOfColumns(jTable_august);
            showUserIncomingsSave(AUGUST);

        } else if (jTable_september.isShowing()) {
            String query = "UPDATE `dane` SET `september_salary`='" + jTextField_Incomings.getText() + "' WHERE `username`='" + jLabel_UserName.getText() + "'";
            executeSQlQuery(jTable_september, SEPTEMBER, query, "Refreshed");
            showSumOfColumns(jTable_september);
            showUserIncomingsSave(SEPTEMBER);

        } else if (jTable_october.isShowing()) {
            String query = "UPDATE `dane` SET `october_salary`='" + jTextField_Incomings.getText() + "' WHERE `username`='" + jLabel_UserName.getText() + "'";
            executeSQlQuery(jTable_october, OCTOBER, query, "Refreshed");
            showSumOfColumns(jTable_october);
            showUserIncomingsSave(OCTOBER);

        } else if (jTable_november.isShowing()) {
            String query = "UPDATE `dane` SET `november_salary`='" + jTextField_Incomings.getText() + "' WHERE `username`='" + jLabel_UserName.getText() + "'";
            executeSQlQuery(jTable_november, NOVEMBER, query, "Refreshed");
            showSumOfColumns(jTable_november);
            showUserIncomingsSave(NOVEMBER);

        } else if (jTable_december.isShowing()) {
            String query = "UPDATE `dane` SET `december_salary`='" + jTextField_Incomings.getText() + "' WHERE `username`='" + jLabel_UserName.getText() + "'";
            executeSQlQuery(jTable_december, DECEMBER, query, "Refreshed");
            showSumOfColumns(jTable_december);
            showUserIncomingsSave(DECEMBER);
        }
    }//GEN-LAST:event_jLabel_refreshMouseClicked

    private void jLabel_LogoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_LogoutMouseClicked
        showLoginForm();
    }//GEN-LAST:event_jLabel_LogoutMouseClicked

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        if (jTable_january.isShowing()) {
            showSumOfColumns(jTable_january);
            showUserIncomingsSave(JANUARY);
        } else if (jTable_february.isShowing()) {
            showSumOfColumns(jTable_february);
            showUserIncomingsSave(FEBRUARY);
        } else if (jTable_march.isShowing()) {
            showSumOfColumns(jTable_march);
            showUserIncomingsSave(MARCH);
        } else if (jTable_april.isShowing()) {
            showSumOfColumns(jTable_april);
            showUserIncomingsSave(APRIL);
        } else if (jTable_may.isShowing()) {
            showSumOfColumns(jTable_may);
            showUserIncomingsSave(MAY);
        } else if (jTable_june.isShowing()) {
            showSumOfColumns(jTable_june);
            showUserIncomingsSave(JUNE);
        } else if (jTable_july.isShowing()) {
            showSumOfColumns(jTable_july);
            showUserIncomingsSave(JULY);
        } else if (jTable_august.isShowing()) {
            showSumOfColumns(jTable_august);
            showUserIncomingsSave(AUGUST);
        } else if (jTable_september.isShowing()) {
            showSumOfColumns(jTable_september);
            showUserIncomingsSave(SEPTEMBER);
        } else if (jTable_october.isShowing()) {
            showSumOfColumns(jTable_october);
            showUserIncomingsSave(OCTOBER);
        } else if (jTable_november.isShowing()) {
            showSumOfColumns(jTable_november);
            showUserIncomingsSave(NOVEMBER);
        } else if (jTable_december.isShowing()) {
            showSumOfColumns(jTable_december);
            showUserIncomingsSave(DECEMBER);
        }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void jLabel15MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel15MouseDragged
        // moving form 
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x - xx, y - yy);
    }//GEN-LAST:event_jLabel15MouseDragged

    private void jLabel15MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel15MousePressed
        // reading coordination of pressed poin
        xx = evt.getX();
        yy = evt.getY();
    }//GEN-LAST:event_jLabel15MousePressed

    private void jPanel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel2MouseClicked

    public void saveToExcel(JTable JTable) {
        JFileChooser fs = new JFileChooser(new File("C:\\"));
        fs.setDialogTitle("Save a File");
        fs.setFileFilter(new FileTypeFilter(".xls", "Excel File"));
        int result = fs.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File fi = fs.getSelectedFile();
            try {
                HSSFWorkbook fWorkbook = new HSSFWorkbook();
                HSSFSheet fSheet = fWorkbook.createSheet("New Sheet");
                File file = new File(fi.getPath());
                HSSFCellStyle cellStyle = fWorkbook.createCellStyle();

                TableModel model = JTable.getModel();

                for (int i = 0; i < model.getRowCount(); i++) {
                    HSSFRow fRow = fSheet.createRow((short) i);
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        HSSFCell cell = fRow.createCell((short) j);
                        cell.setCellValue(model.getValueAt(i, j).toString());
                        cell.setCellStyle(cellStyle);
                    }
                }
                FileOutputStream fileOutputStream;
                fileOutputStream = new FileOutputStream(file + ".xls");
                BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
                fWorkbook.write(bos);
                bos.close();
                fileOutputStream.close();
                JOptionPane.showMessageDialog(null, "Data exported successfully.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    private void jLabel_ExcelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_ExcelMouseClicked

        if (jTable_january.isShowing()) {
            saveToExcel(jTable_january);
        } else if (jTable_february.isShowing()) {
            saveToExcel(jTable_february);
        } else if (jTable_march.isShowing()) {
            saveToExcel(jTable_march);
        } else if (jTable_april.isShowing()) {
            saveToExcel(jTable_april);
        } else if (jTable_may.isShowing()) {
            saveToExcel(jTable_may);
        } else if (jTable_june.isShowing()) {
            saveToExcel(jTable_june);
        } else if (jTable_july.isShowing()) {
            saveToExcel(jTable_july);
        } else if (jTable_august.isShowing()) {
            saveToExcel(jTable_august);
        } else if (jTable_september.isShowing()) {
            saveToExcel(jTable_september);
        } else if (jTable_october.isShowing()) {
            saveToExcel(jTable_october);
        } else if (jTable_november.isShowing()) {
            saveToExcel(jTable_november);
        } else if (jTable_december.isShowing()) {
            saveToExcel(jTable_december);
        }
    }//GEN-LAST:event_jLabel_ExcelMouseClicked

    private void jTable_januaryMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_januaryMousePressed
        showSelectedExpandingsInTextFields(jTable_january);
    }//GEN-LAST:event_jTable_januaryMousePressed

    private void jTable_februaryMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_februaryMousePressed
        showSelectedExpandingsInTextFields(jTable_february);
    }//GEN-LAST:event_jTable_februaryMousePressed

    private void jTable_marchMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_marchMousePressed
        showSelectedExpandingsInTextFields(jTable_march);
    }//GEN-LAST:event_jTable_marchMousePressed

    private void jTable_aprilMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_aprilMousePressed
        showSelectedExpandingsInTextFields(jTable_april);
    }//GEN-LAST:event_jTable_aprilMousePressed

    private void jTable_mayMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_mayMousePressed
        showSelectedExpandingsInTextFields(jTable_may);
    }//GEN-LAST:event_jTable_mayMousePressed

    private void jTable_juneMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_juneMousePressed
        showSelectedExpandingsInTextFields(jTable_june);
    }//GEN-LAST:event_jTable_juneMousePressed

    private void jTable_julyMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_julyMousePressed
        showSelectedExpandingsInTextFields(jTable_july);
    }//GEN-LAST:event_jTable_julyMousePressed

    private void jTable_augustMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_augustMousePressed
        showSelectedExpandingsInTextFields(jTable_august);
    }//GEN-LAST:event_jTable_augustMousePressed

    private void jTable_septemberMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_septemberMousePressed
        showSelectedExpandingsInTextFields(jTable_september);
    }//GEN-LAST:event_jTable_septemberMousePressed

    private void jTable_octoberMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_octoberMousePressed
        showSelectedExpandingsInTextFields(jTable_october);
    }//GEN-LAST:event_jTable_octoberMousePressed

    private void jTable_novemberMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_novemberMousePressed
        showSelectedExpandingsInTextFields(jTable_november);
    }//GEN-LAST:event_jTable_novemberMousePressed

    private void jTable_decemberMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_decemberMousePressed
        showSelectedExpandingsInTextFields(jTable_december);
    }//GEN-LAST:event_jTable_decemberMousePressed

    public void showLoginForm() {
        LoginForm lfm = new LoginForm();
        lfm.setVisible(true);
        lfm.pack();
        lfm.setLocationRelativeTo(null);
        lfm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }

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
            java.util.logging.Logger.getLogger(AppForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AppForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AppForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AppForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AppForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_Add;
    private javax.swing.JLabel jLabel_Date;
    private javax.swing.JLabel jLabel_Eating1;
    private javax.swing.JLabel jLabel_Edit;
    private javax.swing.JLabel jLabel_Excel;
    private javax.swing.JLabel jLabel_Exit;
    private javax.swing.JLabel jLabel_Flat1;
    private javax.swing.JLabel jLabel_Fuel1;
    private javax.swing.JLabel jLabel_Iconified;
    private javax.swing.JLabel jLabel_Logout;
    private javax.swing.JLabel jLabel_Mobile1;
    private javax.swing.JLabel jLabel_Other1;
    private javax.swing.JLabel jLabel_Payment1;
    private javax.swing.JLabel jLabel_Remove;
    private javax.swing.JLabel jLabel_Tickets1;
    private javax.swing.JLabel jLabel_Total1;
    private javax.swing.JLabel jLabel_UserName;
    private javax.swing.JLabel jLabel_refresh;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
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
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable_april;
    private javax.swing.JTable jTable_august;
    private javax.swing.JTable jTable_december;
    private javax.swing.JTable jTable_february;
    private javax.swing.JTable jTable_january;
    private javax.swing.JTable jTable_july;
    private javax.swing.JTable jTable_june;
    private javax.swing.JTable jTable_march;
    private javax.swing.JTable jTable_may;
    private javax.swing.JTable jTable_november;
    private javax.swing.JTable jTable_october;
    private javax.swing.JTable jTable_september;
    private javax.swing.JTextField jTextField_Date;
    private javax.swing.JTextField jTextField_Eating;
    private javax.swing.JTextField jTextField_Flat;
    private javax.swing.JTextField jTextField_Fuel;
    private javax.swing.JTextField jTextField_ID;
    private javax.swing.JTextField jTextField_Incomings;
    private javax.swing.JTextField jTextField_Mobile;
    private javax.swing.JTextField jTextField_Other;
    private javax.swing.JTextField jTextField_Outgoings;
    private javax.swing.JTextField jTextField_Payment;
    private javax.swing.JTextField jTextField_Save;
    private javax.swing.JTextField jTextField_Tickets;
    // End of variables declaration//GEN-END:variables
}
