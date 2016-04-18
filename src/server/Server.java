package server;

import java.util.Random;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.util.Date;
import java.util.HashMap;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

@SuppressWarnings("serial")
public class Server extends JFrame{

	private ServerSocket servSocket;
	private int connectionCount, nowInputCount;
	private ConnectionThread[] connections = new ConnectionThread[2];
	private HashMap<Socket, String> map = new HashMap<Socket, String>();
	private JTextArea textArea = new JTextArea();
	private File[] filelist = new File("unknown").listFiles();
	private File nowFile;
	private Random random = new Random();
	private Date date = new Date();
	private boolean played;
	
	Server(int portNum) {
		// server's basic layout setting
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(300, 200);
		this.setVisible(true);
		this.setResizable(false);
		
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
		this.textArea = new JTextArea();
		this.textArea.setEditable(false);
		this.setPreferredSize(new Dimension(300, 200));
		JScrollPane scrollPane = new JScrollPane(this.textArea);
		this.add(scrollPane);
		
		// random utilize a file in directory "unknown"
		nowFile = filelist[random.nextInt(filelist.length)];
		
		try {
			// setting up server and show message about port number
			servSocket = new ServerSocket(portNum);
			StringBuilder builder = new StringBuilder(date.toString() + "\n");
			builder.append("Server starts listening on port ").append(portNum).append(".\n");
			textArea.append(builder.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void runForever() {
		while (true) {
			try {
				// if connection count < 2, establish connection
				// else return
				if (connectionCount < 2) textArea.append("Server is waiting for client.\n");
				else return;
				// accept the connection
				Socket connSock = servSocket.accept();
				// show the message about the player's IP and host name
				textArea.append("Connection established.\n");
				StringBuilder builder = new StringBuilder("Player ");
				builder.append(connectionCount + 1).append("'s host name: ").append(connSock.getInetAddress().getHostName() + "\n");
				textArea.append(builder.toString());
				builder = new StringBuilder("Player ");
				builder.append(connectionCount + 1).append("'s IP address: ").append(connSock.getInetAddress().getHostAddress() + "\n");
				textArea.append(builder.toString());
				// start a new thread to handle the connection
				ConnectionThread connThread = new ConnectionThread(connSock);
				connThread.start();
				// add it to an array
				connections[connectionCount++] = connThread;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void broadcast(String string) {
		// just a broadcast method
		for (int i = 0; i < 2; i++) {
			connections[i].sendMessage(string);
		}
	}
	
	public static void play() {
		// playing music
		try{
			File file = new File("music/music.wav");
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(file));
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 	
	class ConnectionThread extends Thread {
		private Socket socket;
		private BufferedReader reader;
		private PrintWriter writer;
		
		public ConnectionThread(Socket socket) {
			// setting up socket, reader, printer
			this.socket = socket;
			try {
				reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
				writer = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public Socket getSocket() {
			return socket;
		}
		
		public void sendMessage(String string) {
			writer.println(string);
			writer.flush();
		}
		
		public synchronized void putInMap(Socket socket, String line) {
			// put the income message to a map(use socket as mapping key).
			map.put(socket, line);
		}
		
		public void run() {
			// block the first player using sleep
			while (connectionCount < 2) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// when the two players arrive, send the picture's file name
			sendMessage(nowFile.getName());
			
			// just play music
			if (!played) {
				play();
				played = true;
			}
			
			// read the input from the players
			while (true) {
				try {
					String line = reader.readLine();
					putInMap(socket, line);
					nowInputCount++;
					// when the input is bigger or equal to 2, compare the strings in map
					// if they are the same, then broadcast next file name
					// else broadcast ERROR as error message
					if (nowInputCount >= 2) {
						if (map.get(connections[0].getSocket()).equals(map.get(connections[1].getSocket()))) {
							nowFile = filelist[random.nextInt(filelist.length)];
							broadcast(nowFile.getName());
						} else broadcast("ERROR");
						nowInputCount = 0;
					}
				} catch (Exception e) {
					// catch the exception when a player disconnects
					textArea.append("Client " + socket.getInetAddress() + " terminated.\n");
					return;
				}
			}
		}
	}
	
	// create server
	public static void main(String[] args) {
		Server server = new Server(6666);
		server.runForever();
	}
	
}
