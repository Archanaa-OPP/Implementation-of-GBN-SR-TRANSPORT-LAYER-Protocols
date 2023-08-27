import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
public class receiver {
 static JButton ack,d_ack;
 static JLabel[] packett;
 static DataInputStream dis;
 static DataOutputStream dos;
 static JList<String> jl;static DefaultListModel<String> dlm;
 static JScrollBar vertical;//to controll the vertical scrollBar,of JScrollPane
 static Timer docTimer;
 static int expectedSeqNo=0;//next packet to be received

 receiver(){
 JFrame jf=new JFrame("Receiver in GBN");
 jf.setSize(1300,300);
 jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 jf.setLayout(new BorderLayout());
 //creating buttons
 ack=new JButton("send ack");ack.setEnabled(false);
 ack.addActionListener(new ActionListener(){
 public void actionPerformed(ActionEvent e5){
 try{dos.writeInt(expectedSeqNo-1);
 dlm.addElement("ack- "+(expectedSeqNo-1)+" sent");
 }
 catch(Exception ew){
 dlm.addElement("error:sending ack- "+(expectedSeqNo-1));
 }
 ack.setEnabled(false);d_ack.setEnabled(false);
 }
 });
 d_ack=new JButton("kill ack(default)");d_ack.setEnabled(false);
 d_ack.addActionListener(new ActionListener(){
 public void actionPerformed(ActionEvent e9){
 ack.setEnabled(false);
 d_ack.setEnabled(false);
 dlm.addElement("ack- "+(expectedSeqNo-1)+" killed");
 }
});
 //creating packets
 packett=new JLabel[20];
 for(int z=0;z<20;z++){packett[z]=new JLabel(" "+z+" ");
 packett[z].setOpaque(true);
 packett[z].setBackground(Color.WHITE);}
 packett[0].setForeground(Color.red);
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
 if(docTimer.isRunning()){docTimer.restart();}
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
 for(int z=0;z<20;z++){packetPane.add(packett[z]);
 packetPane.add(Box.createHorizontalStrut(5));}
 // creating pane,for heading panel
 JPanel headingPane=new JPanel();
 JLabel heading=new JLabel("Send Window Size=1");
 headingPane.setLayout(new FlowLayout());
 headingPane.add(heading);
 //adding all panes,to main frame
 jf.add(scrollArea,BorderLayout.LINE_END);
 jf.add(buttonPane,BorderLayout.PAGE_END);
 jf.add(packetPane,BorderLayout.CENTER);
 jf.add(headingPane,BorderLayout.PAGE_START);
 jf.setVisible(true);
 }
 public static void afterReceivingPacket(){
int w;
 try{w=dis.readInt();
 if(w==expectedSeqNo){
 dlm.addElement("packet no: "+w+" received");
 expectedSeqNo++;
 packett[expectedSeqNo-1].setForeground(Color.black);
 packett[expectedSeqNo-1].setBackground(Color.GREEN);
 if(expectedSeqNo<20){
 packett[expectedSeqNo].setForeground(Color.red);}
 }
 else{dlm.addElement("packet no: "+w+" received,DISCARDED");}
 ack.setEnabled(true);d_ack.setEnabled(true);
 }catch(Exception ew){}
 }// func afterReceivingPacket end
 public static void resetApplication(){
 dlm.clear();dlm.addElement("Listening at port 4040");
 dlm.addElement("TCP connection estabilished!!");
 ack.setEnabled(false);
 d_ack.setEnabled(false);
 expectedSeqNo=0;
 for(int z=0;z<20;z++){packett[z].setVisible(false);
 packett[z].setBackground(Color.WHITE);
 packett[z].setForeground(Color.black);
 packett[z].setVisible(true);
 }
 packett[0].setVisible(false);
 packett[0].setForeground(Color.red);
 packett[0].setVisible(true);
 }// func resetApplication end
 public static void main(String[] args) throws Exception{
 new receiver();
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
}