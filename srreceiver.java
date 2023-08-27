import javax.swing.*;//will act like a server(shld be executed ,before sender code)
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
public class srreceiver {
 static JButton ack,d_ack;
 static JLabel[] packett;
 static DataInputStream dis;
 static DataOutputStream dos;
 static JList<String> jl;static DefaultListModel<String> dlm;
 static JScrollBar vertical;//to controll the vertical scrollBar,of JScrollPane
 static Timer docTimer;
 static int base=0;static JLabel baseLabel;
 static boolean duplicateAck=false;
 static int w=-1;//received pkt no
 //static int nextSeqNo=0;//next packet to be received

 srreceiver(){
 JFrame jf=new JFrame("Receiver in SR");
 jf.setSize(1300,300);
 jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 jf.setLayout(new BorderLayout());
 //creating buttons
 ack=new JButton("send ack");
 ack.setEnabled(false);
 ack.addActionListener(new ActionListener(){
 public void actionPerformed(ActionEvent e5){
 try{dos.writeInt(w);
 if(duplicateAck){
 dlm.addElement("Duplicate ack- "+(w)+" sent");
 duplicateAck=false;}
 else{
 dlm.addElement("ack- "+(w)+" sent");}
 }
 catch(Exception ew){
 dlm.addElement("error:sending ack- "+w);}
 ack.setEnabled(false);
 d_ack.setEnabled(false);
 }
});
 d_ack=new JButton("kill ack");
 d_ack.setEnabled(false);
 d_ack.addActionListener(new ActionListener(){
 public void actionPerformed(ActionEvent e9){
 ack.setEnabled(false);
 d_ack.setEnabled(false);
 if(duplicateAck){
 dlm.addElement("Duplicate ack- "+(w)+" killed");}
 else{dlm.addElement("ack- "+(w)+" killed");}
 duplicateAck=false;
 }
 });
 //creating packets
 packett=new JLabel[20];
 for(int z=0;z<20;z++){
 packett[z]=new JLabel(" "+z+" ");
 packett[z].setOpaque(true);
 packett[z].setBackground(Color.WHITE);}
 for(int z=0;z<5;z++){
 packett[z].setForeground(Color.red);}
 //creating pane,to display summary
 dlm=new DefaultListModel<String>();
 jl=new JList<String>(dlm);
 jl.setLayoutOrientation(JList.VERTICAL);
 jl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 jl.setVisibleRowCount(20);
 JScrollPane scrollArea=new JScrollPane(jl);
 scrollArea.setSize(1000, 600);
 vertical=scrollArea.getVerticalScrollBar();
 docTimer=new Timer(700,new ActionListener(){
 public void actionPerformed(ActionEvent e19){
 vertical.validate();/*validation is,updating "Maximum" of vertical*/
 vertical.setValue( vertical.getMaximum() );}
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
 buttonPane.add(ack);
 buttonPane.add(Box.createHorizontalStrut(120));
 buttonPane.add(d_ack);
 buttonPane.setBorder(BorderFactory.createEmptyBorder(5,360,5,120));
 // creating pane,to display packets
 JPanel packetPane=new JPanel();
 packetPane.setLayout(new FlowLayout());
 packetPane.setBorder(BorderFactory.createEmptyBorder(80, 15, 80, 15));
for(int z=0;z<20;z++){
 packetPane.add(packett[z]);
 packetPane.add(Box.createHorizontalStrut(5));}
 // creating pane,for heading panel
 JPanel headingPane=new JPanel();
 JLabel heading=new JLabel("Send Window Size=5");
 baseLabel=new JLabel("base: "+0);
 baseLabel.setOpaque(true);
 baseLabel.setBackground(Color.WHITE);
 headingPane.setLayout(new FlowLayout());
 headingPane.add(heading);
 headingPane.add(Box.createHorizontalStrut(30));
 headingPane.add(baseLabel);
 //adding all panes,to main frame
 jf.add(scrollArea,BorderLayout.LINE_END);
 jf.add(buttonPane,BorderLayout.PAGE_END);
 jf.add(packetPane,BorderLayout.CENTER);
 jf.add(headingPane,BorderLayout.PAGE_START);
 jf.setVisible(true);
 }// constructor end
 public static void updateBase(){
 int b_old=base;int y;
 for( y=b_old;((y<b_old+5)&&(y<20));y++){
 if(packett[y].getBackground()==Color.GREEN){base=y+1;}
 else{break;}
 }
 dlm.addElement("b_old:"+b_old+" b_new:"+base);
 for(y=b_old;y<base;y++){
 packett[y].setVisible(false);
 packett[y].setForeground(Color.BLACK);
 packett[y].setVisible(true);}
 for(y=base;((y<base+5)&&(y<20));y++){
 packett[y].setVisible(false);
 packett[y].setForeground(Color.RED);
 packett[y].setVisible(true);}
 baseLabel.setText("base: "+base);
 }//func updateBase end
 public static void afterReceivingPacket(){
 try{w=dis.readInt();// w is received pkt no
 if((w>=base)&&(w<base+5)){
 dlm.addElement("packet no: "+w+" received");
 packett[w].setBackground(Color.GREEN);
 if(w==base){updateBase();}
 }
 else{
 dlm.addElement("packet no: "+w+" received,AGAIN");
 duplicateAck=true;
 }
 ack.setVisible(false);d_ack.setVisible(false);
 if(duplicateAck){
 ack.setText("Send Duplicate ack-"+w);
 d_ack.setText("Kill Duplicate ack-"+w);}
 else{
 ack.setText("Send ack-"+w);
 d_ack.setText("kill ack-"+w);}
 ack.setEnabled(true);d_ack.setEnabled(true);
ack.setVisible(true);d_ack.setVisible(true);
 }catch(Exception ew){}
 }// func afterReceivingPacket end
 public static void resetApplication(){
 dlm.clear();
 dlm.addElement("Listening at port 4040");
 dlm.addElement("TCP connection estabilished!!");
 ack.setEnabled(false);d_ack.setEnabled(false);
 base=0;duplicateAck=false;
 w=-1;
 baseLabel.setText("base: "+base);
 for(int z=0;z<20;z++){
 packett[z].setVisible(false);
 packett[z].setBackground(Color.WHITE);
 packett[z].setForeground(Color.black);
 packett[z].setVisible(true);}
 for(int z=0;z<5;z++){
 packett[z].setVisible(false);
 packett[z].setForeground(Color.red);
 packett[z].setVisible(true);}
 }// func resetApplication end
 public static void main(String[] args) throws Exception{
 new srreceiver();
 ServerSocket SS=new ServerSocket(4040);
 //System.out.println("Listening at port 4040");
 dlm.addElement("Listening at port 4040");

 Socket S=SS.accept();
 //System.out.println("Serving client..");
 dlm.addElement("TCP connection estabilished!!");
 dis=new DataInputStream(S.getInputStream());
 dos=new DataOutputStream(S.getOutputStream());
 int q;
 while(true){
 q=dis.readInt();
 switch(q){
 case 1:{S.close();SS.close();dlm.addElement("Closing Socket...");}
 case 2:{afterReceivingPacket();break;}
 case 3:{resetApplication();break;}
 case 4:{;break;}
 }//switch end
 }//while end

 }//main end

}//class end