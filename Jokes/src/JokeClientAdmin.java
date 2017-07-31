/* Edward Fiedler  9/27/2015
 * Java Version 8 Update 51 (build 1.8.0_51-b16)
 * javac JokeClientAdmin.java
 * precise instructions to run this program
 >javac JokeServer.java
 >javac JokeClient.java
 >javac JokeClientAdmin.java
 
 then in seperate shell windows:
 >java JokeServer
 >java JokeClient
 >java JokeClientAdmin
 Then follow prompts for further instructions for each application
 
 To run on different computers you must enter the Server's IP to the clients as below:
 >java JokeClient 192.168.1.2
 >java JokeClientAdmin 192.168.1.2
 
 *List of files needed for running program 
 JokeServer.java
 JokeClient.java
 JokeClientAdmin.java
 
 * Notes
 N/A
 */

import java.io.*; // input output libraries
import java.net.*; // networking libraries

public class JokeClientAdmin {
//The administration commands are: "j", "p" and "m"
	public static void main(String args[]){
		String serverName; //create variable holding the name of the serverName
		if (args.length < 1) 
			serverName = "localhost"; //if no arguments then serverName defaults to localhost
		else
			serverName = args[0];
		
		System.out.println("Edward Fiedler's Joke Admin Client, 1.0.\n");
		System.out.println("Using server: " + serverName + ", Port: 45002");
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));//create a buffer for input from user
		try{
			String name;
			do{
				System.out.println("Which mode would you like the Joke Server in? (Enter: 'j', 'p', 'm' or 'quit')\n");
				System.out.flush();   //prompt user for mode of operation
				
				name = in.readLine(); //assign user's input to name
				
				if(name.indexOf("quit")< 0){
					Socket sock; //create a socket for use
					
					BufferedReader fromServer; //prepare a buffer to hold inputs
					PrintStream toServer; //prepare an output
					String textFromServer; //to hold information from server
					
					sock = new Socket(serverName, 45002);
					
					fromServer = new BufferedReader( new InputStreamReader(sock.getInputStream())); //get input
					toServer = new PrintStream(sock.getOutputStream()); //create output
			
					toServer.println(name);
					toServer.flush(); //Send mode selection to ModeWorker
										
					textFromServer = fromServer.readLine();
					System.out.println(textFromServer);
				sock.close(); //close the socket
				}
			} while(name.indexOf("quit")<0); //continue to run until quit is entered
			
			System.out.println("Cancelled by user request."); //inform user loop has ended
			
			} catch (IOException exc) {
			exc.printStackTrace(); //inform user of any errors
			}
	}

}
