import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class ColorManagement {
	
	public static boolean isInColorDatabase(String user) throws IOException{
		 
		 try{
				BufferedReader reader = new BufferedReader(new FileReader("colors.txt"));
				String line;
				
				while ((line = reader.readLine()) != null)
			    {
				//System.out.println(line.split(",")[0].trim());
			      if(line.split(",")[0].trim().equals(user.trim())){
			      reader.close();
			      return true;
			      }
			      
			    }
				
				reader.close();
				
		 }//end try
				catch (Exception e){
					System.err.format("Exception occurred trying to read history.");
				    e.printStackTrace();
				    
				}//end catch
		return false;
	 }//end  isInColorDatabase method
	
	
	
	public static int getColorFromDatabase(String user) throws IOException{
		
		 try{
				BufferedReader reader = new BufferedReader(new FileReader("colors.txt"));
				String line;
				
				while ((line = reader.readLine()) != null)
			    {
				
			      if(line.split(",")[0].trim().equals(user.trim())){
			      reader.close();
			      return Integer.parseInt(line.split(",")[1].trim());
			      }
			      
			    }
				
				reader.close();
				
		 }//end try
				catch (Exception e){
					System.err.format("Exception occurred trying to read history.");
				    e.printStackTrace();
				    
				}//end catch
	 return 0;
	 }//end  getColorFromDatabase method
	
	
	
	
	public static void assignColor(String user) throws IOException{
	
		Random randomGenerator = new Random();
	    
	    PrintWriter out = new PrintWriter(new FileWriter("colors.txt", true), true);
	    out.write(user.trim()+","+randomGenerator.nextInt(22)+"\n");
	    out.close();
	  
		
	}//end assignColor
	
	
	
}//end class
