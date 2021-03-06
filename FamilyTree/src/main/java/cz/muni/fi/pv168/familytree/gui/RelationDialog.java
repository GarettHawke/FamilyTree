/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.familytree.gui;

import cz.muni.fi.pv168.familytree.PeopleManagerImpl;
import cz.muni.fi.pv168.familytree.Person;
import cz.muni.fi.pv168.familytree.RelationCatalogImpl;
import cz.muni.fi.pv168.familytree.ServiceFailureException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.sql.DataSource;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import static cz.muni.fi.pv168.familytree.gui.FamilyTreeGUI.LOG;

/**
 *
 * @author brani
 */
public class RelationDialog extends javax.swing.JDialog {

    private DataSource datasource;
    private List<Person> list;
    private Person parent;
    private Person child;
    private java.util.ResourceBundle bundle;
    
    List<Person> parents;
    boolean getParents;
    /**
     * Creates new form RelationDialog
     * @param parent
     * @param modal
     */
    public RelationDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public RelationDialog(java.awt.Frame parent, boolean modal, DataSource datasource, List<Person> peopleList, java.util.ResourceBundle bundle) {
        super(parent, modal);
        initComponents();
        this.datasource = datasource;
        this.bundle = bundle;
        getParents = false;
        list = peopleList;
        for (Person p : list) {
            parentComboBox.addItem(p.getName());
            childComboBox.addItem(p.getName());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        JPanel = new javax.swing.JPanel();
        parentLabel = new javax.swing.JLabel();
        childLabel = new javax.swing.JLabel();
        buttonsSplitPane = new javax.swing.JSplitPane();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        parentComboBox = new javax.swing.JComboBox<>();
        childComboBox = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("localization"); // NOI18N
        parentLabel.setText(bundle.getString("parentLabel")); // NOI18N

        childLabel.setText(bundle.getString("childLabel")); // NOI18N

        buttonsSplitPane.setBorder(null);
        buttonsSplitPane.setDividerSize(0);

        cancelButton.setText(bundle.getString("cancelButton")); // NOI18N
        cancelButton.setPreferredSize(new java.awt.Dimension(70, 32));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        buttonsSplitPane.setRightComponent(cancelButton);

        okButton.setText(bundle.getString("okButton")); // NOI18N
        okButton.setPreferredSize(new java.awt.Dimension(70, 32));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        buttonsSplitPane.setLeftComponent(okButton);

        javax.swing.GroupLayout JPanelLayout = new javax.swing.GroupLayout(JPanel);
        JPanel.setLayout(JPanelLayout);
        JPanelLayout.setHorizontalGroup(
            JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(JPanelLayout.createSequentialGroup()
                        .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(childLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(parentLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(parentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(childComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(JPanelLayout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(buttonsSplitPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        JPanelLayout.setVerticalGroup(
            JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(parentLabel)
                    .addComponent(parentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(childComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(childLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonsSplitPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(JPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(JPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        try {
            parent = list.get(parentComboBox.getSelectedIndex());
            child = list.get(childComboBox.getSelectedIndex());
            getParents = false;
            GetParentsSwingWorker gp = new GetParentsSwingWorker();
            gp.execute();
            while(!gp.isDone()){};
            parents = gp.get();
            validateRelation(parent, child);
            new CreateRelationSwingWorker().execute();
            setVisible(false);
        } catch(IllegalArgumentException ex) {
            LOG.warn(ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage(), bundle.getString("warning"), JOptionPane.ERROR_MESSAGE);
        } catch (InterruptedException | ExecutionException ex) {
            LOG.error("Error while creating realtion", ex); 
        }
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void validateRelation(Person parent, Person child) {
        if(parent.equals(child)) {
            throw new IllegalArgumentException(bundle.getString("parentIsChild"));
        }
        if(parent.getDateOfBirth().isAfter(child.getDateOfBirth())) {
            throw new IllegalArgumentException(bundle.getString("parentYounger"));
        }
        if(parent.getDateOfBirth().isAfter(child.getDateOfBirth().minusYears(RelationCatalogImpl.ACCEPTED_AGE_FOR_PARENTS))) {
            throw new IllegalArgumentException(bundle.getString("parentTooYoung"));
        }
        if(parents.size() == 2) {
            throw new IllegalArgumentException(bundle.getString("childParents"));
        }
        if(parents.contains(parent)) {
            throw new IllegalArgumentException(bundle.getString("relationExist"));
        }
    }
    
    private class CreateRelationSwingWorker extends SwingWorker<Boolean, Void> {

        @Override
        protected Boolean doInBackground() throws Exception {
            try {
                new RelationCatalogImpl(datasource, new PeopleManagerImpl(datasource)).makeRelation(parent, child);
                return false;
            } catch(ServiceFailureException | IllegalArgumentException ex) {
                LOG.error("Failed to create Relation", ex);
                return true;
            }
        }
        
        @Override
        protected void done() {
            try {
                if (get()) {
                    JOptionPane.showMessageDialog(null, bundle.getString("createRelationFail"), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
                } else {
                    LOG.info("Successfully created relation in database.");
                    updateGUI();
                }
            } catch(InterruptedException | ExecutionException ex) {
                LOG.error("Failed to create Relation", ex);
            }
        }
        
    }
    
    private class GetParentsSwingWorker extends SwingWorker<List<Person>, Void> {
        @Override
        protected List<Person> doInBackground() throws Exception {
            return new RelationCatalogImpl(datasource, new PeopleManagerImpl(datasource)).findParents(child);
        }
    }
    
    private void updateGUI() {
        ((FamilyTreeGUI)this.getParent()).updateGUI();
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
            java.util.logging.Logger.getLogger(RelationDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RelationDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RelationDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RelationDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RelationDialog dialog = new RelationDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel JPanel;
    private javax.swing.JSplitPane buttonsSplitPane;
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox<String> childComboBox;
    private javax.swing.JLabel childLabel;
    private javax.swing.JButton okButton;
    private javax.swing.JComboBox<String> parentComboBox;
    private javax.swing.JLabel parentLabel;
    // End of variables declaration//GEN-END:variables
}
