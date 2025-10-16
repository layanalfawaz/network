
package network1;

import java.io.*; 
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;


public class NewServer {
        private static ArrayList<NewClient> clients=new ArrayList<>();
        static ArrayList<String> usernames = new ArrayList<>();
        static ArrayList<String> passwords = new ArrayList<>();
    public static void main(String[] args) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(9090);

        while (true){
         System.out.println("Waiting for client connection");
         Socket client=serverSocket.accept();
         System.out.println("Connected to client");
         NewClient clientThread=new NewClient(client,clients); // new thread 
         clients.add(clientThread);
         new Thread (clientThread).start();
         
        }
    }
}
