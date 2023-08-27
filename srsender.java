import javax.swing.*;//will act like a client
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
public class srsender implements ActionListener{
 static JButton connectt,sendNew,killPacket,dkillPacket,resett;
 static JLabel[] packett; 
 static JList<String> jl;static DefaultListModel<String> dlm;
 static JScrollBar vertical;//to controll the vertical scrollBar,of JScrollPane
 static Timer docTimer;
 Socket S;
 static DataInputStream dis;
 static DataOutputStream dos;
 
 static Timer[] timerr;
 static JLabel[] timeDisplay;
 static Queue<Integer> q=new LinkedList<>();// to store unused timers
 static Queue<Integer> t=new LinkedList<>();// to store pktNo,for which timer TIMED-OUT
 static HashMap<Integer,Integer> map=new HashMap<Integer,Integer>();
 static LocalDateTime[] startTime;
 static Duration duration=Duration.ofSeconds(15); // this is the duration of timer
 static int base=0;// next packet number,to be sent
 static int nextSeqNo=0; static JLabel seqNoLabel,baseLabel;
 srsender(){
 JFrame jf=new JFrame("Sender in SR"); 
 jf.setSize(1300,300);
 jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 jf.setLayout(new BorderLayout());
 connectt=new JButton("Create Connection");connectt.addActionListener(this);
 sendNew=new JButton("Send New pkt-(0)");sendNew.addActionListener(this);
 sendNew.setEnabled(false);
 killPacket=new JButton("Kill Packet");killPacket.addActionListener(this);
 killPacket.setEnabled(false);
 dkillPacket=new JButton("Dont Kill Packet");dkillPacket.addActionListener(this);
 dkillPacket.setEnabled(false);
 resett=new JButton("Reset");resett.addActionListener(this);
 resett.setEnabled(false); 
 timeDisplay=new JLabel[20];
for(int z=0;z<20;z++){
 timeDisplay[z]=new JLabel("00m 00s");
 timeDisplay[z].setVisible(false);}
 JLabel impPermanentInfo=new JLabel("Send Window Size=5");
 JPanel timeDisplayPanel=new JPanel(); 
 timeDisplayPanel.setLayout(new FlowLayout()); 
 timeDisplayPanel.add(impPermanentInfo); 
 seqNoLabel=new JLabel("next Sequence no:"+nextSeqNo);
 baseLabel=new JLabel("base :"+base);
 timeDisplayPanel.add(Box.createHorizontalStrut(30));
 timeDisplayPanel.add(seqNoLabel);
 timeDisplayPanel.add(Box.createHorizontalStrut(10));
 timeDisplayPanel.add(baseLabel);
 baseLabel.setOpaque(true);
 baseLabel.setBackground(Color.white);
 seqNoLabel.setOpaque(true);seqNoLabel.setBackground(Color.white); 
 packett=new JLabel[20];
 for(int z=0;z<20;z++){
 packett[z]=new JLabel(" "+z+" ");
 packett[z].setForeground(Color.black);
 packett[z].setOpaque(true);
 packett[z].setBackground(Color.white);}
 for(int z=0;z<5;z++){
 packett[z].setForeground(Color.red);}
 dlm=new DefaultListModel<String>();
 jl=new JList<String>(dlm);
 jl.setVisibleRowCount(20);jl.setLayoutOrientation(JList.VERTICAL);
 jl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 JScrollPane scrollArea=new JScrollPane(jl);scrollArea.setSize(1000, 300);
 vertical=scrollArea.getVerticalScrollBar();
 docTimer=new Timer(700,new ActionListener(){
 public void actionPerformed(ActionEvent e1){
 vertical.validate();/*validation is,updating "Maximum" of vertical*/
 vertical.setValue( vertical.getMaximum() );}
 }); 
 docTimer.setRepeats(false); 
 dlm.addListDataListener(new ListDataListener(){
 public void contentsChanged(ListDataEvent e99){}
 public void intervalRemoved(ListDataEvent e) {}
 public void intervalAdded(ListDataEvent e98) {
 if(docTimer.isRunning()){docTimer.restart();}
 else{docTimer.start();}}
 }); 

 JPanel buttonPane=new JPanel();
 buttonPane.setLayout(new BoxLayout(buttonPane,BoxLayout.X_AXIS));
 buttonPane.add(connectt);
 buttonPane.add(Box.createHorizontalStrut(120));
 buttonPane.add(sendNew);
 buttonPane.add(Box.createHorizontalStrut(10));
 buttonPane.add(killPacket);buttonPane.add(dkillPacket);
buttonPane.add(Box.createHorizontalStrut(120));
 buttonPane.add(resett);
 buttonPane.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
 JPanel packetPane=new JPanel();
 packetPane.setLayout(new FlowLayout());
 packetPane.setBorder(BorderFactory.createEmptyBorder(80, 15, 80, 15));
 for(int z=0;z<20;z++){
 packetPane.add(packett[z]);
 packetPane.add(timeDisplay[z]);
 packetPane.add(Box.createHorizontalStrut(5));}
 
 
 startTime=new LocalDateTime[5]; 
 
 timerr=new Timer[5]; 
 
 
 jf.add(scrollArea,BorderLayout.LINE_END);
 jf.add(buttonPane,BorderLayout.PAGE_END);
 jf.add(packetPane,BorderLayout.CENTER);
 jf.add(timeDisplayPanel,BorderLayout.PAGE_START);
 jf.setVisible(true);
 
 
 }
 public static void startTimerFor(int pkttNo){ 
 if(q.size()>0){
 int timerIndexLeft=q.poll(); 
 timerr[timerIndexLeft]=new Timer(300,new ActionListener(){
 public void actionPerformed(ActionEvent e3){ 
 LocalDateTime now=LocalDateTime.now();//System.out.println("1");
 Duration runningTime=Duration.between(startTime[timerIndexLeft],now);
 Duration timeLeft=duration.minus(runningTime);//System.out.println("3");
 if(timeLeft.isNegative() || timeLeft.isZero()){
 timeLeft=Duration.ZERO;
 timerr[timerIndexLeft].stop();
 selectiveRepeat(pkttNo);/*TIMEOUT HAPPENED*/}
 timeDisplay[pkttNo].setText(String.format("00m %02ds", timeLeft.toSeconds()));
 }
 });
 startTime[timerIndexLeft]=LocalDateTime.now();//System.out.println("5");
 timerr[timerIndexLeft].start();//System.out.println("6");
 map.put(pkttNo,timerIndexLeft);
 }else{dlm.addElement("error:no timer to begin!!");}
 }
 public static void selectiveRepeat(int pkktNo){
 dlm.addElement("TIMEOUT for packet no: "+pkktNo);
 if(killPacket.isEnabled()){
 }
else{
 sendNew.setVisible(false);/*sendNew is not visible,now*/
 killPacket.setEnabled(true);dkillPacket.setEnabled(true);
 killPacket.setVisible(false);dkillPacket.setVisible(false);
 killPacket.setText("Kill ReSent Packet-"+pkktNo);
 dkillPacket.setText("srDont Kill ReSent Packet-"+pkktNo);
 killPacket.setVisible(true);dkillPacket.setVisible(true);}
 t.add(pkktNo);
 }
 public static void implementingSelectiveRepeat(boolean pktsKilled){
 int pkktNo=t.poll();
 startTime[map.get(pkktNo)]=LocalDateTime.now();/*vv imp*/
 timerr[map.get(pkktNo)].restart();
 dlm.addElement("Restarting Timer for packet no:"+pkktNo);/*restarting timed-out timer */
 if(pktsKilled==false){
 try{ 
 dos.writeInt(2);dos.writeInt(pkktNo); 
 dlm.addElement("packet no: "+pkktNo+" Re-sent");
 }catch(Exception e9){}
 }
 else{dlm.addElement("packet no: "+pkktNo+" got killed in network");}
 if(t.size()>0){
 pkktNo=t.peek();
 killPacket.setVisible(false);dkillPacket.setVisible(false);
 killPacket.setText("Kill ReSent Packet-"+pkktNo);
 dkillPacket.setText("Dont Kill ReSent Packet-"+pkktNo);
 killPacket.setVisible(true);dkillPacket.setVisible(true);
 return;}
 killPacket.setVisible(false);dkillPacket.setVisible(false);
 killPacket.setText("Kill Packet");dkillPacket.setText("Dont Kill Packet");
 dkillPacket.setVisible(true);killPacket.setVisible(true);
 sendNew.setVisible(true);killPacket.setEnabled(false);
 dkillPacket.setEnabled(false);
 
 }
 public static boolean sendNewPressed(boolean pktKilled){
 if(nextSeqNo<base+5){
 if(pktKilled==false){
 try{
 dos.writeInt(2);
 dos.writeInt(nextSeqNo);
 dlm.addElement("packet no: "+(nextSeqNo)+" sent");}//try end
 catch(Exception e9){
 dlm.addElement("error sending packet no: "+(nextSeqNo)+" .");
 dlm.addElement("CHECK CONNECTION,TO RECEIVER!!!");}/*catch end */
 }
 else{
 dlm.addElement("packet no: "+(nextSeqNo)+" got killed in network");}
 
 timeDisplay[nextSeqNo].setVisible(true);
 startTimerFor(nextSeqNo);
 dlm.addElement("timer started for packet no: "+(nextSeqNo)+" .");/*start timmer */
 packett[nextSeqNo].setVisible(false);
packett[nextSeqNo].setBackground(Color.cyan);
 packett[nextSeqNo].setVisible(true);
 nextSeqNo++;seqNoLabel.setText("next Sequence no:"+nextSeqNo);
 if((nextSeqNo==base+5)||(nextSeqNo>=20)){
 sendNew.setEnabled(false);}
 sendNew.setText("Send New pkt-("+nextSeqNo+")");
 }
 else{dlm.addElement("sending request REJECTED-exceeding window size(5)");}
 if(t.size()>0){
 return true;}
 else{return false;}
 }
 public void resetApplication(){
 base=0;nextSeqNo=0;
 baseLabel.setText("base :"+base);
 seqNoLabel.setText("next Sequence no:"+nextSeqNo);
 dlm.clear();dlm.addElement("tcp handshaking successful");
 killPacket.setVisible(false);dkillPacket.setVisible(false);
 killPacket.setText("Kill Packet");
 dkillPacket.setText("Dont Kill Packet");
 killPacket.setEnabled(false);dkillPacket.setEnabled(false);
 killPacket.setVisible(true);dkillPacket.setVisible(true);
 for(int z=0;z<20;z++){
 timeDisplay[z].setVisible(false);
 timeDisplay[z].setText("00m 00s");
 }
 sendNew.setEnabled(true);sendNew.setVisible(false);
 sendNew.setText("Send New pkt-(0)");
 sendNew.setVisible(true);
 for(int z=0;z<20;z++){
 packett[z].setVisible(false);
 packett[z].setForeground(Color.black);
 packett[z].setBackground(Color.white);
 packett[z].setVisible(true);}
 for(int z=0;z<5;z++){
 packett[z].setVisible(false);
 packett[z].setForeground(Color.red);
 packett[z].setVisible(true);}
 for(int z=0;z<5;z++){
 if((timerr[z]!=null)&&(timerr[z].isRunning())){
 timerr[z].stop();}
 }
 q.clear();
 q.add(0);q.add(1);q.add(2);q.add(3);q.add(4);
 map.clear();
 t.clear(); 
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
 }//try end
 catch(Exception ee){
 dlm.addElement("tcp handshaking failed..");}
 }
 else{
 try{dos.writeInt(1);}
 catch(Exception eee){
 dlm.addElement("error while closing connection.");}
 sendNew.setEnabled(false);
 resett.setEnabled(false);
 /*if(timerr.isRunning()){timerButton.doClick();};*/
 killPacket.setEnabled(false);dkillPacket.setEnabled(false);
 dlm.addElement("Closing Socket..."); 
 connectt.setText("rerun,server & client code");
 connectt.setEnabled(false);
 }
 }//if end
 else if(e.getSource()==sendNew){
 sendNew.setVisible(false);
 killPacket.setEnabled(true);dkillPacket.setEnabled(true); 
 }
 else if(e.getSource()==killPacket){
 if(killPacket.getText().equals("Kill Packet")){
 if(sendNewPressed(true)){
 killPacket.setVisible(false);
 dkillPacket.setVisible(false);
 killPacket.setText("Kill ReSent Packet-"+t.peek());
 dkillPacket.setText("Dont Kill ReSent Packet-"+t.peek());
 killPacket.setVisible(true);dkillPacket.setVisible(true);}
 else{
 sendNew.setVisible(true);
 killPacket.setEnabled(false);dkillPacket.setEnabled(false);}
 }
 else{implementingSelectiveRepeat(true);} 
 }
 else if(e.getSource()==dkillPacket){
 if(dkillPacket.getText().equals("Dont Kill Packet")){
 if(sendNewPressed(false)){
 killPacket.setVisible(false);dkillPacket.setVisible(false);
 killPacket.setText("Kill ReSent Packet-"+t.peek());
 dkillPacket.setText("Dont Kill ReSent Packet-"+t.peek());
 killPacket.setVisible(true);dkillPacket.setVisible(true);}
 else{
 sendNew.setVisible(true);
 killPacket.setEnabled(false);dkillPacket.setEnabled(false);}
 }
 else{implementingSelectiveRepeat(false);} 
 }
 else if(e.getSource()==resett){
 try{dos.writeInt(3);}
 catch(Exception e3){}
 resetApplication(); 
 }
}
 public static void updateBase(){
 int y;int b_old=base;
 for(y=b_old;((y<20)&&(y<nextSeqNo));y++){
 if(!map.containsKey(y)){base=y+1;}
 else{break;}
 }
 for( y=base;((y<base+5)&&(y<20));y++){
 packett[y].setForeground(Color.red);}
 for(y=b_old;y<base;y++){
 packett[y].setVisible(false);packett[y].setBackground(Color.yellow);
 packett[y].setForeground(Color.black);packett[y].setVisible(true);
}
 if(!sendNew.isEnabled() &&(nextSeqNo-base<5)){
 sendNew.setEnabled(true);}
 if(nextSeqNo>=20){sendNew.setEnabled(false);}
 baseLabel.setText("base :"+base);
 }
 public static void main(String[] args)throws Exception {
 new srsender();
 q.add(0);q.add(1);q.add(2);
 q.add(3);q.add(4);
 int w=0;
 while(true){ 
 try{
 w=dis.readInt(); 
 
 if(t.contains(w)){
 dlm.addElement("ack: "+w+" discarded,as selective Repeat procedure is being implemented for it.");}
 else{ dlm.addElement("ack: "+w+" received.");
 if(map.containsKey(w)){
 timerr[map.get(w)].stop();
 q.add(map.remove(w));
 timeDisplay[w].setVisible(false);}
 packett[w].setVisible(false);
 packett[w].setBackground(Color.yellow);
 packett[w].setVisible(true);
 if(w==base){updateBase();}
 }
 
 }
 catch(Exception e){}
 
 } 
 }//main end 
}//class end

