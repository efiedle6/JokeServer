/* Edward Fiedler  9/27/2015
 * Java Version 8 Update 51 (build 1.8.0_51-b16)
 * javac JokeServer.java
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
import java.util.HashMap;// import HashMap for storing user data, the key is the user's name
						// and the array of booleans for which jokes/proverbs have been read is the value
import java.util.Random;//for randomizing the jokes/proverbs

public class JokeServer {
	
	//Global Variables 
	
	static int mode = 0;//global variable for mode, default (0) = Joke mode. Made global so that threads can access
	static HashMap<String,Boolean[]> hm = new HashMap<String,Boolean[]>(); //create a Map to store user data
		// key: UserName  | value: Array Slot: |0	|1	|2	|3	|4	|5	|6	|7	|8	|9 |
		// 				 					   |J1 	|J2 |J3 |J4 |J5	|P1 |P2 |P3 |P4 |P5|
	static String[] jokes = new String[10];
	
	public static void main(String a[]) throws IOException{
		int q_len = 6; //how many items to hold in the queue. Doesn't matter much due to
						// processing speed
		int port = 45001; //arbitrary port, typically choose ~ 45000 - 65500
		Socket sock; //create a socket for use in networking
		
		//Create 5 jokes and 5 proverbs 
		//all jokes courtesy of "http://www.jokes4us.com/miscellaneousjokes/cleanjokes.html"
		
		//Jokes are indexes 0 - 4
		JokeServer.jokes[0] = new String("Joke A: Question: Xname, what does a nosey pepper do?\n        Answer: Gets jalapeno business!\n");
		JokeServer.jokes[1] = new String("Joke B: Question: Xname, what do you call a fake noodle?\n        Answer: An Impasta!\n" );
		JokeServer.jokes[2] = new String("Joke C: Question: Xname, what's the difference between a guitar and a fish?\n"
				+ "        Answer: You can't tuna fish!\n");
		JokeServer.jokes[3] = new String("Joke D: Question: Xname, what do you call a mountain of kittens?\n        Answer: A meowntain!\n");
		JokeServer.jokes[4] = new String("Joke E: Question: Xname, did you hear about the hungry clock?\n        Answer: It went back four seconds!\n");
		
		//all proverbs courtesy of "http://www.phrasemix.com/collections/the-50-most-important-english-proverbs"
		//Proverbs are indexes 5 - 9
		JokeServer.jokes[5] = new String("Proverb A: Xname, it is important to remember that two wrongs don't make a right.\n\n");
		JokeServer.jokes[6] = new String("Proverb B: Xname, it is important to remember that when in Rome, do as the Romans.\n\n");
		JokeServer.jokes[7] = new String("Proverb C: Xname, it is important to remember that when the going gets tough, the tough get going.\n\n");
		JokeServer.jokes[8] = new String("Proverb D: Xname, it is important to remember that fortune favors the bold.\n\n");
		JokeServer.jokes[9] = new String("Proverb E: Xname, it is important to remember to hope for the best, but prepare for the worst.\n\n");

		//create a new thread to asychronously listen for admin connections
		AdminServer AS = new AdminServer();
		Thread adminThread = new Thread(AS);
		adminThread.start();
		
		//Synchronously handle non-admin clients
		ServerSocket servsock = new ServerSocket (port, q_len); //create a socket for the server
		System.out.println("Edward Fiedler's Joke Server 1.0 starting up, listening at port: " + port + "\n");
		while (true){
		
			
			sock = servsock.accept(); //waits for client to send data
			
			new Worker(sock).start(); //creates a worker thread to handle the client's data and then
									//returns to waiting for another client packet.
		}
		
	}//end main
}//End JokeServer

class AdminServer implements Runnable{   // create a class that inherits thread and implements runnable
	public static boolean indicator = true;				// class member of ModeWorker for indicating T/F
	
	public void run(){
		System.out.println("Starting the admin thread.");
		int q_len = 6; //how many items to hold in the queue. Doesn't matter much due to
		// processing speed
		int adminPort = 45002; //port for listening to admin
		Socket sock2;
		
			try{
				ServerSocket adminSock = new ServerSocket(adminPort, q_len);
				while(indicator){
					sock2 = adminSock.accept();
					new ModeWorker(sock2).start();
				}
				//adminSock.close();
			}catch (IOException ioex) {
				System.out.println(ioex); //let user know if top try doesn't work
			}
		
		
	} //end run()
}//End AdminServer

class ModeWorker extends Thread{
	Socket sock;				// class member of ModeWorker
	ModeWorker (Socket s) {
		sock = s;
		}	//constructor of ModeWorker, assigns s to sock
	public void run(){
		String modeRequest; //String to hold mode selection from admin client
		PrintStream out = null; //create a variable for output stream
		BufferedReader in = null; //create a variable for input
		
		try{
			in = new BufferedReader (new InputStreamReader(sock.getInputStream()));
			// set the input received from the client to in
			out = new PrintStream(sock.getOutputStream()); //set output to client
			
			try{
				modeRequest = in.readLine(); //take input from admin client and convert to an int
				
				if (modeRequest.equals("j")){//check if user inputed j for joke
					JokeServer.mode = 0; //set global var mode in Joke Server = 0
					out.println("Joke Server is now in Joke Mode.");
					}
				if (modeRequest.equals("p")){//check if user inputed p for proverb
					JokeServer.mode = 1;//set global var mode in Joke Server = 1
					out.println("Joke Server is now in Proverb Mode.");
					}
				if (modeRequest.equals("m")){//check if user inputed m for maintenance
					JokeServer.mode = 2;//set global var mode in Joke Server = 2
					out.println("Joke Server is now in Maintenance Mode.");
					}
				
			} catch (IOException x){
				System.out.println("Server read error 1");
				x.printStackTrace(); //if the read in doesn't work let the user know the error	
			}
			//sock.close(); //close the socket
		} catch (IOException ioex) {
			System.out.println(ioex); //let user know if top try doesn't work
		}
	} //end run()
}//End ModeWorker

class Worker extends Thread{   // create a class that inherits thread
	Socket sock;				// class member of Worker
	//static String name;			//variable global to worker  consider this
	Worker (Socket s) {
		sock = s;
		}	//constructor of worker, assigns s to sock
	
	public void run(){
		
		BufferedReader in = null; //create a variable for input
		PrintStream out = null; //create a variable for output stream
		String name;
		try {
		
			in = new BufferedReader (new InputStreamReader(sock.getInputStream()));
		
			name = in.readLine(); //sets the name equal to the name/UUID send from the client
			String nameReal = in.readLine(); //gets the real name for use in jokes
			
			//Logic for checking Server for name 
			
			if (!JokeServer.hm.containsKey(name)){//check if user has not joined client before
						JokeServer.hm.put(name, new Boolean[10]);//create a Map of key and values if user has not
						Boolean[] a = JokeServer.hm.get(name);//make sure its initialized to all false
						a[0] = false;
						a[1] = false;
						a[2] = false;
						a[3] = false;
						a[4] = false;
						a[5] = false;
						a[6] = false;
						a[7] = false;
						a[8] = false;
						a[9] = false;
						JokeServer.hm.put(name, a); //put all false Boolean into hashmap name
			}

			try{
				// set the input received from the client to in
				out = new PrintStream(sock.getOutputStream()); //set output to client

					BufferedReader in2 = new BufferedReader (new InputStreamReader(sock.getInputStream()));
					String holder = in2.readLine();
					
					//logic for checking Server for mode (maintenance joke or proverb) and give an unheard joke		
					//for Maintenance mode
					if (JokeServer.mode == 2){ //for Maintenance mode
					out.println("The server is temporarily unavailable -- check-back shortly.\n\n");
					out.flush();
					}
					
					//for Joke mode
					if (JokeServer.mode == 0){
						Boolean[] b = JokeServer.hm.get(name);
						b = giveJoke(b, nameReal, out); //call giveJoke and assign old array with updated
						JokeServer.hm.put(name,b);//now that the array has been changed replace it with the new
					}
				
				
					//for Proverb Mode
					if (JokeServer.mode == 1){
						Boolean[] b = JokeServer.hm.get(name);//get boolean list for Jokes/Proverbs heard
						b = giveProverb(b, nameReal, out);//give user an unheard Proverb
						JokeServer.hm.put(name,b); //return updated boolean array to JokeServer
					}			
					
			} catch (IOException ioex) {
			System.out.println(ioex); //let user know if top try doesn't work
				}
		}catch (IOException x){
					System.out.println("Server read error: 2");
					x.printStackTrace(); //if the read in doesn't work let the user know the error	
				}
	} //end run()

	//function to check if joke has been heard and give an unheard joke
	public Boolean[] giveJoke(Boolean[] b, String name, PrintStream out){
		Random checkIfHeard = new Random(); //create a Random Object checkIfHeard
		Boolean complete = false; //loop variable
		String nameReal = name;
		
		while (complete.equals(false)){//use while to keep running check until it reaches a joke not heard
			
			int index = checkIfHeard.nextInt(5); //use Random to get a number from 0-4
			if (b[index]==false){//check if joke not heard before
				String placeHolder = JokeServer.jokes[index];//to replace Xname with nameReal
				String placeHolder2 = placeHolder.replace("Xname", nameReal);
				out.println(placeHolder2); //send joke to client
				out.flush();
				b[index] = true; //now that its heard set to true in b
				
				//check if all are true if so reset all to false
				if (b[0]==true && b[1]==true && b[2]==true && b[3]==true && b[4]==true){
					b[0]=false;
					b[1]=false;
					b[2]=false;
					b[3]=false;
					b[4]=false;
				}
				//joke has been heard to set complete equal to true to end the loop
				complete = true;
				}
			
		}
		return b; //return so that JokeServer can be updated with new boolean array
	}//End giveJoke
	
	public Boolean[] giveProverb(Boolean[] b, String name, PrintStream out){
		Random checkIfHeard = new Random(); //create a Random Object
		Boolean complete = false;
		String nameReal = name;
		
		while (complete.equals(false)){//use while to keep running check until it reaches a joke not heard
			
			int index = 5 + checkIfHeard.nextInt(5); //use Random to get a number from 5-9
			if (b[index]==false){//check if proverb not heard before
				String placeHolder = JokeServer.jokes[index]; //to replace Xname with nameReal
				String placeHolder2 = placeHolder.replace("Xname", nameReal);
				out.println(placeHolder2); //send joke to client
				out.flush();
				b[index] = true; //now that its heard set to true in b
				
				//check if all are true if so reset all to false
				if (b[5]==true && b[6]==true && b[7]==true && b[8]==true && b[9]==true){
					b[5]=false;
					b[6]=false;
					b[7]=false;
					b[8]=false;
					b[9]=false;
				}
				
				//joke has been heard to set complete equal to true to end the loop
				complete = true;
			}
			
		}
		return b; //return so that JokeServer can be updated with new boolean array
	}//End giveProverb
	
}//End Worker
	