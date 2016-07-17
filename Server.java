import	java.util.*;
import	java.io.*;
import	java.net.*;

public class Server {

	private static ServerSocket		serverSocket;
	private static Socket	socket;
	private static int total = 20;// change this to 3 or 4 for testing. . . . 
	private static SessionThread[] sessionThreads = new SessionThread[total];//create array of SessionThread pointers (size 20)
	
	
	
	public static void main( String [] arg ) throws Exception
	{
		System.out.println( "Starting Server. . . " );
		int p = Integer.parseInt(arg[0]);
		serverSocket = new ServerSocket( p, 20 );//20 incoming connections (queue length 20)
		serverSocket.setReuseAddress( true );
		
		while(true){
			socket = serverSocket.accept();
			boolean success = false;
			for(int i = 0; i< total; i++){
				
				if(sessionThreads[i]==null){
					(sessionThreads[i] = new SessionThread(socket, sessionThreads)).start();
					System.out.println( "Server connection to client established." );
					success = true;
					break;
				}
				
					
			}//end for loop
			if(!success){
				PrintWriter msg = new PrintWriter(socket.getOutputStream());//
				msg.println("Chat session full. Disconnecting. . . . .");
				msg.close();
				socket.close();
		}
			
			
		}//end while loop
		
	 
	}//end main
}//end class