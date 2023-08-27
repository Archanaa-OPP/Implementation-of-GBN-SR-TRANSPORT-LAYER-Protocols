import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.LocalDateTime;
public class sender implements ActionListener{
 static JButton connectt,sendNew,killPacket,dkillPacket,resett,timerButton;
 static JLabel[] packett;
 static JList<String> jl;static DefaultListModel<String> dlm;
 static JScrollBar vertical;
 static Timer docTimer;
 Socket S;
 static DataInputStream dis;
 static DataOutputStream dos;

 static Timer timerr;
 static JLabel timeDisplay;
 LocalDateTime startTime;
 Duration duration=Duration.ofSeconds(15); 
 static int base=0;// next packet number,to be sent
 static int nextSeqNo=0; static JLabel seqNoLabel,baseLabel;
 sender(){
 JFrame jf=new JFrame("Sender in GBN");
 jf.setSize(1300,300);
 jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 jf.setLayout(new BorderLayout());
 //creating butons
 connectt=new JButton("Create Connection");connectt.addActionListener(this);
 sendNew=new JButton("Send New");sendNew.addActionListener(this);
 sendNew.setEnabled(false);
 killPacket=new JButton("Kill Packet");killPacket.addActionListener(this);
 killPacket.setEnabled(false);
 dkillPacket=new JButton("Dont Kill Packet");dkillPacket.addActionListener(this);
 dkillPacket.setEnabled(false);
 resett=new JButton("Reset");resett.addActionListener(this);
 resett.setEnabled(false);
 timerButton=new JButton("Start Timer");
 timerButton.setEnabled(false);
 //initialising display for time,and creating pane for it
 timeDisplay=new JLabel("-- -- --");
 JLabel impPermanentInfo=new JLabel("Send Window Size=5");
JPanel timeDisplayPanel=new JPanel();
 timeDisplayPanel.setLayout(new FlowLayout());
 timeDisplayPanel.add(impPermanentInfo);
 timeDisplayPanel.add(Box.createHorizontalStrut(120));
 timeDisplayPanel.add(timeDisplay);
 timeDisplayPanel.add(Box.createHorizontalStrut(10));
 timeDisplayPanel.add(timerButton);
 seqNoLabel=new JLabel("next Sequence no:"+nextSeqNo);
 baseLabel=new JLabel("base :"+base);
 timeDisplayPanel.add(Box.createHorizontalStrut(30));
 timeDisplayPanel.add(seqNoLabel);
 timeDisplayPanel.add(Box.createHorizontalStrut(10));
 timeDisplayPanel.add(baseLabel);
 baseLabel.setOpaque(true);
 baseLabel.setBackground(Color.white);
 seqNoLabel.setOpaque(true);
 seqNoLabel.setBackground(Color.white);
 //creating packets
 packett=new JLabel[20];
 for(int z=0;z<20;z++){
 packett[z]=new JLabel(" "+z+" ");
 packett[z].setForeground(Color.black);
 packett[z].setOpaque(true);
 packett[z].setBackground(Color.white);}
 for(int z=0;z<5;z++){
 packett[z].setForeground(Color.red);}
 //creating pane,to display summary
 dlm=new DefaultListModel<String>();
 jl=new JList<String>(dlm);jl.setVisibleRowCount(20);
 jl.setLayoutOrientation(JList.VERTICAL);
 jl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 JScrollPane scrollArea=new JScrollPane(jl);scrollArea.setSize(1000, 300);
 vertical=scrollArea.getVerticalScrollBar();
 docTimer=new Timer(700,new ActionListener(){
 public void actionPerformed(ActionEvent e1){
 vertical.validate();/*validation is,updating "Maximum" of vertical*/
 vertical.setValue( vertical.getMaximum() );
 }
 });
 docTimer.setRepeats(false);
 dlm.addListDataListener(new ListDataListener(){
 public void contentsChanged(ListDataEvent e99){}
 public void intervalRemoved(ListDataEvent e) {}
 public void intervalAdded(ListDataEvent e98) {
 if(docTimer.isRunning()){
 docTimer.restart();}
 else{docTimer.start();}}
 });



 //creating pane,to display buttons
 JPanel buttonPane=new JPanel();
 buttonPane.setLayout(new BoxLayout(buttonPane,BoxLayout.X_AXIS));
 buttonPane.add(connectt);
 buttonPane.add(Box.createHorizontalStrut(120));
 buttonPane.add(sendNew);
buttonPane.add(Box.createHorizontalStrut(10));
 buttonPane.add(killPacket);
 buttonPane.add(dkillPacket);
 buttonPane.add(Box.createHorizontalStrut(120));
 buttonPane.add(resett);
 buttonPane.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
 // creating pane,to display packets
 JPanel packetPane=new JPanel();
 packetPane.setLayout(new FlowLayout());
 packetPane.setBorder(BorderFactory.createEmptyBorder(80, 15, 80, 15));
 for(int z=0;z<20;z++){
 packetPane.add(packett[z]);
 packetPane.add(Box.createHorizontalStrut(5));
 }
 //functioning timer
 timerButton.addActionListener(new ActionListener(){
 public void actionPerformed(ActionEvent e1){
 if(timerr.isRunning()){
 timerr.stop();
 startTime=null;
 timerButton.setText("Start Timer");
 }
 else{
 startTime=LocalDateTime.now();
 timerr.start();
 timerButton.setText("Stop Timer");
 }
 }
 });
 timerr=new Timer(300,new ActionListener(){
 public void actionPerformed(ActionEvent e3){
 LocalDateTime now=LocalDateTime.now();
 Duration runningTime=Duration.between(startTime,now);
 Duration timeLeft=duration.minus(runningTime);
 if(timeLeft.isNegative() || timeLeft.isZero()){
 timeLeft=Duration.ZERO;
 timerButton.doClick();
 goBackN();
 }
 timeDisplay.setText(String.format("00h 00m %02ds", timeLeft.toSeconds()));
 }
 });
 //adding all panes,to main frame
 jf.add(scrollArea,BorderLayout.LINE_END);
 jf.add(buttonPane,BorderLayout.PAGE_END);
 jf.add(packetPane,BorderLayout.CENTER);
 jf.add(timeDisplayPanel,BorderLayout.PAGE_START);
 jf.setVisible(true);


 }//constructor end
 public static void goBackN(){
 dlm.addElement("TIMEOUT for packet no: "+base);
 dlm.addElement("Go-Back-N packets: "+base+"-"+(nextSeqNo-1));
 sendNew.setVisible(false);killPacket.setEnabled(true);
 dkillPacket.setEnabled(true);
 killPacket.setText("Kill Packets["+base+" -"+(nextSeqNo-1)+" ]");
dkillPacket.setText("don't Kill Packets["+base+" -"+(nextSeqNo-1)+" ]");
 killPacket.setVisible(false);killPacket.setVisible(true);
 dkillPacket.setVisible(false);dkillPacket.setVisible(true);
 }
 public static void implementingGoBackN(boolean pktsKilled){
 timerButton.doClick();
 dlm.addElement("Restarting Timer for packet no:"+base);
 if(pktsKilled==false){
 try{
 for(int g=base;g<nextSeqNo;g++){
 dos.writeInt(2);
 dos.writeInt(g);
 }
 dlm.addElement("packet no: "+base+"-"+(nextSeqNo-1)+" sent");
 }catch(Exception e9){}
 }
 else{
 dlm.addElement("packet no: "+base+"-"+(nextSeqNo-1)+" got killed in network");
 }
 killPacket.setVisible(false);dkillPacket.setVisible(false);
 killPacket.setText("Kill Packet");
 dkillPacket.setText("Dont Kill Packet");
 dkillPacket.setVisible(true);killPacket.setVisible(true);

 }
 public static void sendNewPressed(boolean pktKilled){
 if(nextSeqNo<base+5){
 if(pktKilled==false){
 try{
 dos.writeInt(2);
 dos.writeInt(nextSeqNo);
 dlm.addElement("packet no: "+(nextSeqNo)+" sent");}
 catch(Exception e9){
 dlm.addElement("error sending packet no"+(nextSeqNo)+" .");}
 }
 else{
 dlm.addElement("packet no"+(nextSeqNo)+" got killed in network");
 }

 if(base==nextSeqNo){
 if(!timerButton.isEnabled()){timerButton.setEnabled(true);}
 timerButton.doClick();
 dlm.addElement("timer started for packet no: "+(nextSeqNo)+" .");
 /*start timmer */
 }
 packett[nextSeqNo].setVisible(false);
 packett[nextSeqNo].setBackground(Color.cyan);
 packett[nextSeqNo].setVisible(true);
 nextSeqNo++;
 seqNoLabel.setText("next Sequence no:"+nextSeqNo);
 if(nextSeqNo==base+5){sendNew.setEnabled(false);}
 }
 else{dlm.addElement("sending request REJECTED-exceeding window size(5)");
 }
 }// func sendNewPressed end
 public void resetApplication(){
base=0;nextSeqNo=0;baseLabel.setText("base :"+base);
 seqNoLabel.setText("next Sequence no:"+nextSeqNo);
 dlm.clear();if(timerr.isRunning()){timerButton.doClick();}
 timerButton.setEnabled(false);
 dlm.addElement("tcp handshaking successful");
 killPacket.setVisible(false);dkillPacket.setVisible(false);
 killPacket.setText("Kill Packet");
 dkillPacket.setText("Dont Kill Packet");
 killPacket.setEnabled(false);
 dkillPacket.setEnabled(false);killPacket.setVisible(true);
 dkillPacket.setVisible(true);
 timeDisplay.setText("-- -- --");
 sendNew.setEnabled(true);sendNew.setVisible(true);
 for(int z=0;z<20;z++){
 packett[z].setVisible(false);
 packett[z].setForeground(Color.black);
 packett[z].setBackground(Color.white);
 packett[z].setVisible(true);}
 for(int z=0;z<5;z++){
 packett[z].setVisible(false);
 packett[z].setForeground(Color.red);
 packett[z].setVisible(true);}
 }
 public void actionPerformed(ActionEvent e){
 if(e.getSource()==connectt){
 if(e.getActionCommand().equals("Create Connection")){
 try{ S=new Socket("localhost",4040);
 dis=new DataInputStream(S.getInputStream());
 dos=new DataOutputStream(S.getOutputStream());
 dlm.addElement("tcp handshaking successful");
 connectt.setText("Close Connection");
 sendNew.setEnabled(true);resett.setEnabled(true);
 //killPacket.setEnabled(true);dkillPacket.setEnabled(true);
 }//try end
 catch(Exception ee){
 dlm.addElement("tcp handshaking failed..");}
 }
 else{
 try{dos.writeInt(1);}
 catch(Exception eee){
 dlm.addElement("error while closing connection.");}
 sendNew.setEnabled(false);resett.setEnabled(false);
 if(timerr.isRunning()){timerButton.doClick();};
 timerButton.setEnabled(false);
 killPacket.setEnabled(false);
 dkillPacket.setEnabled(false);
 dlm.addElement("Closing Socket...");
 connectt.setText("rerun,server & client code");
 connectt.setEnabled(false);
 }
 }//if end
 else if(e.getSource()==sendNew){
 sendNew.setVisible(false);
 killPacket.setEnabled(true);
 dkillPacket.setEnabled(true);
 }
 else if(e.getSource()==killPacket){
if(killPacket.getText().equals("Kill Packet")){
 sendNewPressed(true);}
 else{implementingGoBackN(true);}
 sendNew.setVisible(true);
 killPacket.setEnabled(false);
 dkillPacket.setEnabled(false);
 }
 else if(e.getSource()==dkillPacket){
 if(dkillPacket.getText().equals("Dont Kill Packet")){
 sendNewPressed(false);}
 else{implementingGoBackN(false);}
 sendNew.setVisible(true);
 killPacket.setEnabled(false);
 dkillPacket.setEnabled(false);
 }
 else if(e.getSource()==resett){
 try{dos.writeInt(3);}
 catch(Exception e3){}
 resetApplication();
 }
 }//func actionPerformed end
 public static void updateBase(int b_old,int b_new){
 int y;
 for( y=b_new;(y<b_new+5)&&(y<20);y++){
 packett[y].setForeground(Color.red);}
 for(y=b_old;y<b_new;y++){
 packett[y].setVisible(false);
 packett[y].setBackground(Color.yellow);
 packett[y].setForeground(Color.black);
 packett[y].setVisible(true);
 /*setVisible() from false to true,will repaint the component*/
 }
 if(!sendNew.isEnabled() &&(nextSeqNo-b_new<5)){
 sendNew.setEnabled(true);
 }
 baseLabel.setText("base :"+b_new);
 }
 public static void main(String[] args)throws Exception {
 new sender();
 int w=0;
 while(true){
 try{w=dis.readInt();
 if(killPacket.getText().equals("Kill Packet")){
 // to ensure implementingGoBackN() is not in progress
 dlm.addElement("ack: "+w+" received.");
 updateBase(base,w+1);
 base=w+1;
 if(base==nextSeqNo){
 timerButton.doClick();
 timerButton.setEnabled(false);
 dlm.addElement("Stoping Timer");/*stop timmer */
 }
 else{
timerButton.doClick();
 timerButton.doClick();
 dlm.addElement("Restarting Timer");
 /*restart timer */
 }
 }
 else{dlm.addElement("ack: "+w+" discarded, as GBN procedure is in progress");
 /*ACK received while implementing func: implementingGoBackN() will not be considered */}

 }
 catch(Exception e){
 /*dlm.addElement("error:while receving ack");error coming,*/}

 }
 }
}