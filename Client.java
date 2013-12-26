// Fig. 17.5: Client.java
 // Set up a Client that will read information sent
 // from a Server and display the information.

// Java core packages
 import java.io.*;
import java.net.*;
import java.awt.*;
 import java.awt.event.*;

 // Java extension packages
 import javax.swing.*;

 public class Client extends JFrame {
 private JTextField enterField;
 private JTextArea displayArea;
private ObjectOutputStream output;
 private ObjectInputStream input;
 private String message = "";
 private String chatServer;
 private Socket client;

 // initialize chatServer and set up GUI
 public Client( String host )
 {
 super( "Client" );

 // set server to which this client connects
 chatServer = host;
 Container container = getContentPane();

 // create enterField and register listener
 enterField = new JTextField();
 enterField.setEnabled( false );

 enterField.addActionListener(

 new ActionListener() {

 // send message to server
 public void actionPerformed( ActionEvent event )
 {
 sendData( event.getActionCommand() );
 }

 } // end anonymous inner class

 ); // end call to addActionListener

 container.add( enterField, BorderLayout.NORTH );

 // create displayArea
 displayArea = new JTextArea();
 container.add( new JScrollPane( displayArea ),
 BorderLayout.CENTER );

 setSize( 300, 150 );
 setVisible( true );
 }

 // connect to server and process messages from server
 public void runClient()
 {
 // connect to server, get streams, process connection
 try {

 // Step 1: Create a Socket to make connection
 connectToServer();

 // Step 2: Get the input and output streams
 getStreams();

 // Step 3: Process connection
 processConnection();

 // Step 4: Close connection
 closeConnection();
 }


 // server closed connection
 catch ( EOFException eofException ) {
 System.out.println( "Server terminated connection" );
 }

 // process problems communicating with server
 catch ( IOException ioException ) {
 ioException.printStackTrace();
 }
 }

 // get streams to send and receive data
 private void getStreams() throws IOException
 {
 // set up output stream for objects
 output = new ObjectOutputStream(
 client.getOutputStream() );

 // flush output buffer to send header information
 output.flush();

 // set up input stream for objects
 input = new ObjectInputStream(
 client.getInputStream() );

 displayArea.append( "\nGot I/O streams\n" );
 }

// connect to server
 private void connectToServer() throws IOException
 {
 displayArea.setText( "Attempting connection\n" );

 // create Socket to make connection to server
 client = new Socket(
 InetAddress.getByName( chatServer ), 5000 );

 // display connection information
displayArea.append( "Connected to: " +
 client.getInetAddress().getHostName() );
 }

 // process connection with server
 private void processConnection() throws IOException
 {
 // enable enterField so client user can send messages
enterField.setEnabled( true );

// process messages sent from server
 do {
 // read message and display it
try {
message = ( String ) input.readObject();
 displayArea.append( "\n" + message );
displayArea.setCaretPosition(
 displayArea.getText().length() );
}
 // catch problems reading from server
 catch ( ClassNotFoundException classNotFoundException ) {
 displayArea.append( "\nUnknown object type received" );
 }

 } while ( !message.equals( "SERVER>>> TERMINATE" ) );
 } // end method process connection
 // close streams and socket
 private void closeConnection() throws IOException
 {
 displayArea.append( "\nClosing connection" );
 output.close();
 input.close();
 client.close();
 }

 // send message to server
private void sendData( String message )
 {
 // send object to server
 try {
 output.writeObject( "CLIENT>>> " + message );
 output.flush();
 displayArea.append( "\nCLIENT>>>" + message );
 }
 // process problems sending object
catch ( IOException ioException ) {
 displayArea.append( "\nError writing object" );
 }
 }

// execute application
public static void main( String args[] )
{
 Client application;
 if ( args.length == 0 )
application = new Client( "127.0.0.1" );
else
 application = new Client( args[ 0 ] );
 application.setDefaultCloseOperation(
 JFrame.EXIT_ON_CLOSE );

 application.runClient();
 }

} // end class Client