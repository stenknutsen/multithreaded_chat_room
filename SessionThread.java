import java.util.*;
import java.io.*;
import java.net.*;

public class SessionThread extends Thread {

	private Socket socket;
	private BufferedReader fromClient;
	private PrintWriter toClient;
	private final SessionThread[] sessionThreads;
	private int total;
	private String userName;
	public String pmSource = null;
	private int myColor = 0;
	private String colorReset = "\u001B[0m";
	private String[] colorArray = { "\u001B[30m", "\u001B[31m", "\u001B[32m", "\u001B[33m", "\u001B[34m", "\u001B[35m",
			"\u001B[36m", "\u001B[37m", "\u001B[30;47m", "\u001B[31;47m", "\u001B[32;47m", "\u001B[33;47m",
			"\u001B[34;47m", "\u001B[35;47m", "\u001B[36;47m", "\u001B[30;43m", "\u001B[31;43m", "\u001B[34;43m",
			"\u001B[35;43m", "\u001B[30;46m", "\u001B[31;46m", "\u001B[35;46m" };

	// constructor
	//
	public SessionThread(Socket socket, SessionThread[] sessionThreads) {
		this.socket = socket;
		this.sessionThreads = sessionThreads;
		total = sessionThreads.length;
	}
	//returns boolean: true if member is in private mode, false if not
	//
	private boolean isPrivate() {

		for (int i = 0; i < total; i++) {
			if (sessionThreads[i] != null && sessionThreads[i].pmSource != null
					&& sessionThreads[i].pmSource.equals(userName)) {
				return true;
			}
		}
		return false;
	}// end isPrivate()

	public void run() {
		SessionThread[] sessionThreads = this.sessionThreads;
		int total = this.total;

		try {
			fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			toClient = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

			String name;

			
			
			
			
			//waits for user to enter username with @name command, then validates username
			//
			//
			while (true) {

				toClient.println("Please enter username using @name command.");
				
				while(true){
					name = fromClient.readLine().trim();//
					
					if(name.startsWith("@name ")){
						
					name = name.split(" ")[1].trim();	
						
						break;
						
					}
					
					
					toClient.println("Invalid entry.");
					
				}
				
				//Start validation. . . .. 
				//
				// make sure no one else with same name active
				//
				synchronized (this) {
					boolean alreadyExists = false;

					for (int i = 0; i < total; i++) {
						if (sessionThreads[i] != null && sessionThreads[i].userName != null
								&& sessionThreads[i].userName.equals(name)) {
							toClient.println("Username already active. Please enter a different name.");
							alreadyExists = true;
						}

					} // end for

					if (alreadyExists) {
						continue;
					}
				} // end synchronized

				// @ is only a command character
				//
				if (name.startsWith("@")) {
					toClient.println("Usernames must not start with @.");
					continue;
				}

				// cannot have commas or colons in name as these are used in
				// parsing history/colors files
				//
				if (name.contains(",") || name.contains(":")) {
					toClient.println("Usernames must not contain commas or colons.");
					continue;
				}

				// names must be less than 100 chars
				//
				else if (name.length() > 100) {
					toClient.println("Usernames must be at most 100 characters.");
					continue;
				} else {
					break;
				}

			} // end while loop (collect username)----testing only

			toClient.println("Welcome to the chat session, " + name + ".\nLoading chat history:");

			// loads chat history and prints to client
			//
			//
			synchronized (this) {
				ArrayList<String> hist = ChatHistory.newRead();

				for (String n : hist) {
					String wholeLine = n.toString();
					String num = wholeLine.split(",")[0].trim();
					int colorNum = Integer.parseInt(num);
					int commaIndex = wholeLine.indexOf(",");
					int colonIndex = wholeLine.indexOf(":");

					String nm = wholeLine.substring(commaIndex + 1, colonIndex);
					String mg = wholeLine.substring(colonIndex);

					toClient.println(colorArray[colorNum] + nm + colorReset + mg);

				} //
			} // end synchronized

			// (re)assign color to user
			//
			synchronized (this) {
				if (!ColorManagement.isInColorDatabase(name.trim())) {
					ColorManagement.assignColor(name.trim());

				}

				myColor = ColorManagement.getColorFromDatabase(name.trim());
			} // end synchronized
			
			
			//assign name to userName
			//
			synchronized (this) {

				for (int i = 0; i < total; i++) {
					if (sessionThreads[i] != null && sessionThreads[i] == this) {
						userName = name;
						break;
					}
				} // end for

				// broadcast user has entered chat to all other users
				//
				for (int i = 0; i < total; i++) {

					if (sessionThreads[i] != null && sessionThreads[i] != this) {
						sessionThreads[i].toClient.println(name + " has entered the chat session.");
					}
				}

			} // end synchronized

			
			
			
			// this is where the actual "chatting" starts
			//
			//
			//
			//
			while (true) {
				String userInput = fromClient.readLine();

				// break loop and shut down thread if client quits
				//
				if (userInput.trim().equals("@quit")) {
					break;

					// print out names of all users in chat, tag those who are targets of PM as (pvt)
					//
				} else if (userInput.trim().equals("@who")) {

					synchronized (this) {
						for (int i = 0; i < total; i++) {

							if (sessionThreads[i] != null && sessionThreads[i].userName != null) {
								
								if(sessionThreads[i].pmSource != null){
									toClient.println(sessionThreads[i].userName + "(pvt)");
								}else{
									toClient.println(sessionThreads[i].userName);
								}
								
								
							}
						}
					}//end synchronized

			   // start private messaging with other client if client
			   // available. Notify if not
			   //
				} else if (userInput.startsWith("@private ")) {

					synchronized (this) {
						boolean found = false;
						String st = userInput.split(" ")[1].trim();

						for (int i = 0; i < total; i++) {
							if (sessionThreads[i] != null && sessionThreads[i].userName != null
									&& sessionThreads[i].userName.equals(st) && sessionThreads[i] != this) {
								if (sessionThreads[i].pmSource == null) {
									sessionThreads[i].pmSource = userName;
									toClient.println("PM with " + st + " establihed.");
									found = true;
									break;
								}
							}

						} // end for loop
						if (!found) {
							toClient.println(st + " not found. No PM established");
						}

					} // end synchronized

					// who is pming me?
					//
				} else if (userInput.trim().equals("@pm")) {
					if (pmSource == null) {
						toClient.println("No one is PMing you.");
					} else {
						toClient.println(pmSource);
					}

					// Who am I PM-ing (if anyone)?
					//
				} else if (userInput.trim().equals("@pwho")) {

					synchronized (this) {
						if (isPrivate()) {
							toClient.println("You are currently PM-ing:");
							for (int i = 0; i < total; i++) {
								if (sessionThreads[i] != null && sessionThreads[i].userName != null
										&& sessionThreads[i].pmSource != null
										&& sessionThreads[i].pmSource.equals(userName)) {
									toClient.println(sessionThreads[i].userName);
								}
							}

						} else {
							toClient.println("You are not currently PM-ing anyone.");
						}

					} // end synchronized

					// end PM session command
					//
				} else if (userInput.startsWith("@end ")) {

					synchronized (this) {
						boolean found = false;
						String st = userInput.split(" ")[1].trim();
						for (int i = 0; i < total; i++) {
							if (sessionThreads[i] != null && sessionThreads[i].userName != null
									&& sessionThreads[i].userName.equals(st) && sessionThreads[i].pmSource != null
									&& !(userName.equals(st))) {

								sessionThreads[i].pmSource = null;
								toClient.println("PM with " + st + " ended.");
								found = true;
								break;
							}
						} // end for

						if (!found) {
							toClient.println(st + " not found. No PM to be ended.");
						}
					} // end synchronized
					
					//if begins with @ and is not one of the above commands, return message.
					//
				}else if(userInput.startsWith("@")){	
					toClient.println("Not a valid command.");
					
				} else {

					// if user is in private mode, broadcast only to targets of
					// PM
					//
					if (isPrivate()) {
						synchronized (this) {
							for (int i = 0; i < total; i++) {
								if (sessionThreads[i] != null && sessionThreads[i].userName != null
										&& sessionThreads[i] != this && sessionThreads[i].pmSource != null
										&& sessionThreads[i].pmSource.equals(userName)) {
									sessionThreads[i].toClient.println("" + name + "(private): " + userInput);

								}
							}
							toClient.println("PM sent.");

						} // end synchronized

					} else {

						// broadcast message to all available clients. . . .
						//
						synchronized (this) {
							for (int i = 0; i < total; i++) {
								if (sessionThreads[i] != null && sessionThreads[i].userName != null) {
									sessionThreads[i].toClient
											.println(colorArray[myColor] + name + colorReset + ": " + userInput);

								}
							}

							// . . . and log message in chat history
							//
							try {
								ChatHistory.log(myColor + "," + name + ": " + userInput + "\n");
							} catch (IOException e) {
								e.printStackTrace();
							}

						} // end synchronized

					} // end little else

				} // end big else
			} // end while

			// notify chatroom user is leaving
			//
			//
			synchronized (this) {
				for (int i = 0; i < total; i++) {
					if (sessionThreads[i] != null && sessionThreads[i] != this && sessionThreads[i].userName != null) {
						sessionThreads[i].toClient.println("" + name + " has left the chat.");
					}
				}
			} // end synchronized

			// notify client connection is being terminated
			//
			toClient.println("Terminating connection. . . . .");

			// terminate all active PMs
			//
			synchronized (this) {
				for (int i = 0; i < total; i++) {
					if (sessionThreads[i] != null && sessionThreads[i] != this && sessionThreads[i].userName != null
							&& sessionThreads[i].pmSource != null && sessionThreads[i].pmSource.equals(userName)) {
						sessionThreads[i].pmSource = null;
					}
				}

			} // end synchronized

			// set this user to null in sessionThread array
			//
			synchronized (this) {
				for (int i = 0; i < total; i++) {
					if (sessionThreads[i] == this) {
						sessionThreads[i] = null;
					}
				}

			} // end synchronized

			// close all connections
			//
			fromClient.close();
			toClient.close();
			socket.close();

		} catch (Exception e) {
			System.out.println("Exception in SessionThread:" + e.toString());
			e.printStackTrace();
		}
	}
}
