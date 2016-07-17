import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

//handles all chat history functionality for chat program
//

public class ChatHistory {
	
	public static void log(String message) throws IOException { 
	      PrintWriter out = new PrintWriter(new FileWriter("history.txt", true), true);
	      out.write(message);
	      out.close();
	}//end log
	      
	 public static String read() throws IOException{
		 String totalHistory = "";
		 try{
				BufferedReader reader = new BufferedReader(new FileReader("history.txt"));
				String line;
				
				while ((line = reader.readLine()) != null)
			    {
			      
			      totalHistory = totalHistory + line + "\n";
			    }
				
				reader.close();
				
		 }//end try
				catch (Exception e){
					System.err.format("Exception occurred trying to read history.");
				    e.printStackTrace();
				    
				}//end catch
		return totalHistory;
	 }//end read method
	 
	 
	 public static ArrayList<String> newRead() throws IOException{
		 ArrayList<String> history = new ArrayList<String>();
		 try{
				BufferedReader reader = new BufferedReader(new FileReader("history.txt"));
				String line;
				
				while ((line = reader.readLine()) != null)
			    {
			      
			      history.add(line);
			    }
				
				reader.close();
				
		 }//end try
				catch (Exception e){
					System.err.format("Exception occurred trying to read history.");
				    e.printStackTrace();
				    
				}//end catch
		 return history;
		 
	 }
}//end ChatHistory class


