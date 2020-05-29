/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Twitter;

import javax.mail.*;
import java.nio.file.Files;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Timer;
import java.util.TimerTask;
import javax.mail.internet.MimeMessage;
//import javax.swing.JFrame;
//import javax.swing.JOptionPane;
//JFrame frame = new JFrame("JOptionPane showMessageDialog example");
//JOptionPane.showMessageDialog(frame, "Eggs are not supposed to be green.");


/**
 *
 * @author Joel Gahr
 */

enum Providers{
    VERIZON,
    ATT,
    TMOBILE,
    SPRINT
}



public class OpeningMenu extends javax.swing.JFrame {

    static class GlobalVars{
        static String UserPhone = "";
        static String UserTwitter = "";
        static String RandoDingen = "Cna874195?";
        static int UserProvider = 0;
        static Path UserPresetsPath;
        static boolean StartLoop = false;
        static String OldTweetTime = "";
    }
    
    static int StringToDigit(String MyString){
        
        int MyDigit = 0;
        
        switch(MyString){
            case "0":MyDigit = 0;break;
            case "1":MyDigit = 1;break;
            case "2":MyDigit = 2;break;
            case "3":MyDigit = 3;break;
        }
        
        return(MyDigit);
    }
        static String GetProviderAddress()
        {
            String Address = "";
            switch(GlobalVars.UserProvider)
            {
                case 0: Address = "@vtext.com";break;
                case 1: Address = "@txt.att.net"; break;
                case 2: Address = "@tmomail.net"; break;
                case 3: Address = "@messaging.sprintpcs.com"; break;
            }

            return (Address);
        }
    
    class CheckAndUpdate extends TimerTask{
        String UriStr = "";
        @Override
        public void run(){
            if(GlobalVars.StartLoop == true){
                //Send GET request to Twitter using A Bearer Token For authorization.
                UriStr = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=" + GlobalVars.UserTwitter + "&count=1&trim_user=1";
                String TwitStr = "";
                try {
                    URL url = new URL(UriStr);
                    HttpURLConnection con;
                    try {
                        con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("GET");
                        con.setRequestProperty("Authorization", "Bearer AAAAAAAAAAAAAAAAAAAAANIFEgEAAAAAC1%2F4cuMGUpNgGfdcfCteF%2F4JHSA%3DhSNOROobfSNHj3rrVJ3AnTi3i2X8NhZ6m7gBJut2NIK0NgB0XC");
                        Reader readit = new InputStreamReader(con.getInputStream());
                        int data = readit.read();
                        while(data != -1){
                            TwitStr = TwitStr + (char)data;
                            data = readit.read();
                        }
                        readit.close();
                    } catch (IOException ex) {
                        Logger.getLogger(OpeningMenu.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (MalformedURLException ex) {
                    Logger.getLogger(OpeningMenu.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                boolean InQuotes = false;
                String TitleString = "";
                String NewTweetTime = "";
                String TweetText = "";
                for(int i = 0; i < TwitStr.length();++i){
                    if(TweetText.length() > 0 && NewTweetTime.length() > 0)
                    {
                        break;
                    }
                    else if (InQuotes)
                    {
                        if (TitleString.length() > 0 && TwitStr.charAt(i) == '"' && TwitStr.charAt(i - 1) != '\\')
                        {
                            switch (TitleString)
                            {
                                case "created_at":
                                    {
                                        //Write Next quote body to TimeString
                                        for (int ITime = i + 3; ITime < TwitStr.length(); ++ITime)
                                        {
                                            if (TwitStr.charAt(ITime) == '"' && TwitStr.charAt(ITime - 1) != '\\')
                                            {
                                                i = ITime;
                                                break;
                                            }
                                            else
                                                NewTweetTime += TwitStr.charAt(ITime);

                                        }

                                    }
                                    break;
                                case "text":
                                    {
                                        //Write next quote body to TweetContent
                                        for (int ITime = i + 3; ITime < TwitStr.length(); ++ITime)
                                        {
                                            if (TwitStr.charAt(ITime) == '"' && TwitStr.charAt(ITime - 1) != '\\')
                                            {
                                                i = ITime;
                                                break;
                                            }
                                            else
                                                TweetText += TwitStr.charAt(ITime);
                                        }

                                    }
                                    break;
                                default:
                                    {
                                    }
                                    break;
                            }
                            TitleString = "";
                            InQuotes = false;
                        }
                        else
                        {
                            TitleString += TwitStr.charAt(i);
                        }

                    }
                    else if (TwitStr.charAt(i) == '"' && !InQuotes)
                    {
                        InQuotes = true;
                    }
                }
                
                if(!NewTweetTime.equals(GlobalVars.OldTweetTime)){
                    GlobalVars.OldTweetTime = NewTweetTime;
                    String[] TweetChunk = new String[5];
                    TweetChunk = NewTweetTime.split(" ",5);
                    
                    //Send Text message via email containing time and body of the tweet.
                    
                    Properties props = System.getProperties();
                    props.put("mail.smtp.host", "smtp.gmail.com");
                    props.put("mail.smtp.port", "465");
                    props.put("mail.smtp.ssl.enable", "true");          
                    Session session = Session.getInstance(props, new javax.mail.Authenticator(){
                    protected PasswordAuthentication getPasswordAuthentication() {
                     return new PasswordAuthentication("1s22p63d104f14@gmail.com", GlobalVars.RandoDingen);
                          }
                     });
                    session.setDebug(true);


                    try {
                    MimeMessage msg = new MimeMessage(session);
                    msg.setFrom("1s2p63d104f14@gmail.com");
                    msg.setRecipients(Message.RecipientType.TO,
                    GlobalVars.UserPhone + GetProviderAddress());
                    msg.setSubject(GlobalVars.UserTwitter);
                    msg.setSentDate(new Date());
                    msg.setText(TweetChunk[3] + ": " + TweetText);
                    Transport.send(msg,"1s22p63d104f14@gmail.com", GlobalVars.RandoDingen);
                    } catch (MessagingException mex) {
                    System.out.println("send failed, exception: " + mex);
                    }
                }
                
                int PressF = 69;
            }

        }
    }

    /**
     * Creates new form OpeningMenu
     */
    public OpeningMenu() {
        initComponents();

        double MinutesToWait = 0.1;
        Timer CheckTimer = new Timer(true);
        TimerTask TaskOfTheTimer = new CheckAndUpdate();
        CheckTimer.schedule(TaskOfTheTimer, 0, (long)(1000*60*MinutesToWait));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblPhone = new javax.swing.JLabel();
        ButtonStart = new javax.swing.JButton();
        txtTwitter = new javax.swing.JTextField();
        comboProvider = new javax.swing.JComboBox<>();
        lblTwitter1 = new javax.swing.JLabel();
        lblProvider = new javax.swing.JLabel();
        txtPhone = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("OpeningMenu");
        setMaximumSize(getPreferredSize());
        setMinimumSize(getPreferredSize());
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        lblPhone.setFont(new java.awt.Font("Liberation Mono", 1, 14)); // NOI18N
        lblPhone.setText("Cell Phone Number:");
        lblPhone.setToolTipText("");

        ButtonStart.setFont(new java.awt.Font("Liberation Mono", 1, 18)); // NOI18N
        ButtonStart.setText("Start");
        ButtonStart.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ButtonStartMouseClicked(evt);
            }
        });
        ButtonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonStartActionPerformed(evt);
            }
        });

        txtTwitter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTwitterActionPerformed(evt);
            }
        });
        txtTwitter.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtTwitterPropertyChange(evt);
            }
        });
        txtTwitter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTwitterKeyTyped(evt);
            }
        });

        comboProvider.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Verizon", "AT&T", "T-Mobile", "Sprint" }));

        lblTwitter1.setFont(new java.awt.Font("Liberation Mono", 1, 14)); // NOI18N
        lblTwitter1.setText("Twitter Handle : @");
        lblTwitter1.setToolTipText("");

        lblProvider.setFont(new java.awt.Font("Liberation Mono", 1, 14)); // NOI18N
        lblProvider.setText("Service Provider:");
        lblProvider.setToolTipText("");

        txtPhone.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtPhonePropertyChange(evt);
            }
        });
        txtPhone.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPhoneKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(85, 85, 85)
                        .addComponent(lblTwitter1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTwitter, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblPhone)
                            .addComponent(lblProvider))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtPhone)
                            .addComponent(comboProvider, 0, 195, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(163, 163, 163)
                        .addComponent(ButtonStart, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(86, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTwitter1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTwitter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPhone, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProvider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(comboProvider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46)
                .addComponent(ButtonStart, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtTwitterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTwitterActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_txtTwitterActionPerformed

    private void ButtonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonStartActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ButtonStartActionPerformed

    private void ButtonStartMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ButtonStartMouseClicked
        // TODO add your handling code here:
        if(GlobalVars.StartLoop == false){
                    GlobalVars.UserPhone = this.txtPhone.getText();
        GlobalVars.UserTwitter = this.txtTwitter.getText();
        GlobalVars.UserProvider = this.comboProvider.getSelectedIndex();
        GlobalVars.StartLoop = true;
        this.ButtonStart.setEnabled(false);       
        this.ButtonStart.setText("Running");
        String PresetText = GlobalVars.UserPhone + " " + GlobalVars.UserTwitter + " " + GlobalVars.UserProvider;
        CharSequence CharPreset = new StringBuilder(PresetText);
        try {
            Files.writeString(GlobalVars.UserPresetsPath, CharPreset);
        } catch (IOException ex) {
            Logger.getLogger(OpeningMenu.class.getName()).log(Level.SEVERE, null, ex); 
        }
        }

    }//GEN-LAST:event_ButtonStartMouseClicked

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        this.txtPhone.setText(GlobalVars.UserPhone);
        this.txtTwitter.setText(GlobalVars.UserTwitter);
        this.comboProvider.setSelectedIndex(GlobalVars.UserProvider);

    }//GEN-LAST:event_formWindowOpened

    private void txtPhonePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtPhonePropertyChange
        // TODO add your handling code here:

    }//GEN-LAST:event_txtPhonePropertyChange

    private void txtTwitterPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTwitterPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTwitterPropertyChange

    private void txtTwitterKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTwitterKeyTyped
        // TODO add your handling code here:
        if(GlobalVars.StartLoop == true){
            this.ButtonStart.setEnabled(true);       
            this.ButtonStart.setText("Start");
            GlobalVars.StartLoop = false;
        }
    }//GEN-LAST:event_txtTwitterKeyTyped

    private void txtPhoneKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPhoneKeyTyped
        // TODO add your handling code here:
        if(GlobalVars.StartLoop == true){
            this.ButtonStart.setEnabled(true);       
            this.ButtonStart.setText("Start");
            GlobalVars.StartLoop = false;
        }
    }//GEN-LAST:event_txtPhoneKeyTyped


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
            java.util.logging.Logger.getLogger(OpeningMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(OpeningMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(OpeningMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OpeningMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
       
        //Read UserPresets.txt file
        // Put values read from it into GlobalVars.
        
        GlobalVars.UserPresetsPath = Paths.get("UserPresets.txt");
        boolean UserPresetFileExists = Files.exists(GlobalVars.UserPresetsPath);
        List<String> UserText = new ArrayList<>();
        if(UserPresetFileExists){
            try {
                UserText = Files.readAllLines(GlobalVars.UserPresetsPath);
            } catch (IOException ex) {
                Logger.getLogger(OpeningMenu.class.getName()).log(Level.SEVERE, null, ex);
            } 
            
            String UserPreset = UserText.get(0);
            String[] UserChunks = UserPreset.split(" ");
            
            GlobalVars.UserPhone = UserChunks[0];
            GlobalVars.UserTwitter = UserChunks[1];
            GlobalVars.UserProvider = StringToDigit(UserChunks[2]);
        }
        else{
            //create file           
            try {
                Files.createFile(GlobalVars.UserPresetsPath);
            } catch (IOException ex) {
                Logger.getLogger(OpeningMenu.class.getName()).log(Level.SEVERE, null, ex);
            } 
  
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OpeningMenu().setVisible(true);
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButtonStart;
    private javax.swing.JComboBox<String> comboProvider;
    private javax.swing.JLabel lblPhone;
    private javax.swing.JLabel lblProvider;
    private javax.swing.JLabel lblTwitter1;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtTwitter;
    // End of variables declaration//GEN-END:variables
}
