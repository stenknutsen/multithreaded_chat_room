import	java.util.*;
import java.util.concurrent.TimeUnit;
import	java.io.*;
import	java.net.*;

//java Client localhost 3000 or 127.0.0.1 3000;

public class Client implements Runnable {
	
	
	private static boolean openConnection = true;
	private static BufferedReader	fromServer;
	private static BufferedReader	stdIn;
	private static Socket		socket;
	private static PrintWriter	toServer;
	
	private static Socket connect( String host, int prt ) throws Exception
	{
		try
		{
			return new Socket( host, prt );
		}
		catch ( ConnectException ce )
		{
			System.out.println("Attempting again in three seconds. . . .");
			TimeUnit.SECONDS.sleep(3);
			return null;
		}
	}
	
	
	
	
	public static void main( String [] arg ) throws Exception
	{
		System.out.println("Client Started.");
		System.out.println("Attempting connection to server. . . .. ");
		
		int prt = Integer.parseInt(arg[1]);
		
		do {
			
			socket = connect( arg[0], prt );
		} while ( socket == null );
		
		System.out.println("Client connected.");
	
		stdIn = new BufferedReader( new InputStreamReader( System.in ) );//takes input from user
		
		fromServer = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );//
		toServer = new PrintWriter( new OutputStreamWriter( socket.getOutputStream() ), true );//sends to socket
		
		//toServer.println("bababaabbaab");<++ this is how CLA username willl be sent, or maybe not
		
		
		      try {

		        
		        new Thread(new Client()).start();
		        
		        
		        while (openConnection) {
		          toServer.println(stdIn.readLine().trim());
		        }
		        
		     
		        toServer.close();
		        fromServer.close();
		        socket.close();
		        
		        
		      } 
		      
		      catch (IOException e) {
		        System.err.println("IOException:  " + e);
		      }
		    
		
		
	}//end main
	
public void run(){
	String chatFeed;
	try {
	   while ((chatFeed = fromServer.readLine()) != null) {
	      
		   System.out.println(chatFeed);
	     
	     }//end while
	      
	   openConnection = false;
	   
	} catch (IOException e) {
	      System.err.println("IOException:  " + e);
	    }
	System.out.println("Connection to server lost.");
	
	   try {
		socket.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	   System.exit(0);
	   
	}//end run

	
}//end class



