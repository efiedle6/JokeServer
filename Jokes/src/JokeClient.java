/* Edward Fiedler  9/27/2015
 * Java Version 8 Update 51 (build 1.8.0_51-b16)
 * javac JokeClient.java
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
import java.util.Scanner; //for reading from user
import java.util.HashMap; //to store name and Unique User ID (UUID)
import java.util.Random; //to get UUID

public class JokeClient {
	
	public static void main(String args[]){
		String serverName; //create variable holding the name of the serverName
		if (args.length < 1) 
			serverName = "localhost"; //if no arguments then serverName defaults to localhost
		else
			serverName = args[0];
		Socket sock; //create a socket for use
		BufferedReader fromServer; //prepare a buffer to hold inputs
		PrintStream toServer; //prepare an output
		String textFromServer; //to hold information from server
		HashMap<String, Integer> nameID = new HashMap<String, Integer>(); //Symbol table to store user name and corresponding UUID
		
		/*print client's about information*/
		System.out.println("Edward Fiedler's Joke Client, version 1.0.\n");
		System.out.println("Using server: " + serverName + ", Port: 45001");		
		
		String name; //string for user's username 
		
		
		System.out.print("Enter your username\n");
		System.out.flush();   //prompt user for input
		
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			name = in.readLine();
		
		
		if (!nameID.containsKey(name)){ //check if name already corresponds to a UUID
			Random getUUID = new Random();//create randomizer
			int UUID = getUUID.nextInt(250000000); //assign the user a UUID between 0 - 249,999,999
			Integer IUUID = (Integer) UUID;
			nameID.put(name, IUUID); //assign the UUID to the name
		}
		
		String namePlus = new String();
		namePlus = name + nameID.get(name);
		System.out.println("Hello, " + name + ", press Enter:");

		try{
			while(true){			
				
				sock = new Socket(serverName, 45001); //open connection socket
			
				fromServer = new BufferedReader( new InputStreamReader(sock.getInputStream()));//get input	
				toServer = new PrintStream(sock.getOutputStream()); //create output
				
				toServer.println(namePlus); //send name/userID string to Server
				toServer.println(name);
				
				
				
				String enter = in.readLine(); //get users Enter stroke
				
				toServer.print("\n");
				toServer.flush();	
					
				for(int i = 1; i <=3; i++){ //read (up to 3) lines from the server
					textFromServer = fromServer.readLine();
						if (textFromServer !=null)
							System.out.println(textFromServer);//print to user not server
					}
				sock.close();
				}						
			} catch (IOException except) {
					except.printStackTrace(); //inform user of any errors
				}
		}catch (IOException except) {
					except.printStackTrace(); //inform user of any errors
			}
		
	}
}

