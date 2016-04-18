package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class Typing extends JPanel implements Runnable, KeyListener {

	private Socket socket;
	private String input, servRep, ipAddress;
	private PrintWriter writer;
	private BufferedReader reader;
	private int drawStat, port; // drawStat == 0 -> wait, 1 -> wrong, 2 -> move
	
	private int y = 0;
	private BufferedImage img;
	private JTextField textField = new JTextField();
	private GameStage gs;
	
	// set IP address
	public Typing setIPAddress(String input) {
		ipAddress = input;
		return this;
	}
	
	// set port number
	public Typing setPort(int port) {
		this.port = port;
		return this;
	}
	
	// connect to server
	public void connect() {
		try {
			socket = new Socket(this.ipAddress, this.port);
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(String string) {
		writer.println(string);
		writer.flush();
	}
	
	Typing(GameStage gs) {
		this.gs = gs;
		setLayout(null);
		add(textField);
		textField.setBounds(0, 500, 300, 20);
		textField.addKeyListener(this);
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		// when the enter is pressed, record the input
		if (arg0.getKeyCode() == KeyEvent.VK_ENTER) { 
			input = textField.getText();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		try {
			// when the enter is release and drawStat == 2, send text to server
			// and set text to "", set drawStat to zero
			if (arg0.getKeyCode() == KeyEvent.VK_ENTER && drawStat == 2) {
				sendMessage(input);
				textField.setText("");
				drawStat = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) { 
		// if any key is typed, set drawStat to 2
		drawStat = 2;
	}

	@Override
	public void run() {
		try {
			// at first we get a string line from server, read the image and start the game
			servRep = reader.readLine().replace("\n", "");
			img = ImageIO.read(new File("unknown/" + servRep));
			drawStat = 2;
		} catch (Exception e) {
			e.printStackTrace();
		}
		while (true) {
			try {
				// if drawStat is at stop status, read a line from server
				if (drawStat == 0) {
					servRep = reader.readLine().replace("\n", "");
					// if server replies error message, just show "wrong answer"
					// if server replies with a new file name, open it, and start again
					if (!servRep.equals("ERROR")) {
						gs.setNowScore(gs.getNowScore() + 5);
						img = ImageIO.read(new File("unknown/" + servRep));
						drawStat = 2;
						y = 0;
					} else drawStat = 1;
				}
				Thread.sleep(20);
				// move the picture
				if (drawStat == 2 && ++y > 500) y = 0;
				repaint();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// if scores >= 100, game over
			if (gs.getNowScore() >= 100) {
				textField.setEditable(false);
				textField.removeKeyListener(this);;
				break;
			}
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// set font
		g.setFont(new Font("Consolas", Font.BOLD, 26)); 
		setBackground(Color.lightGray);
		// if drawStat == 2, draw the image
		// if drawStat == 1, show error
		// if drawStat == 0, show wait
		if (drawStat == 2) g.drawImage(img, 10, y, img.getWidth(), img.getHeight(), null);
		if (drawStat == 1) {
			g.drawString("Wrong answer!", 60, 237);
			g.drawString("Input again!", 63, 263);
		}
		if (drawStat == 0) g.drawString("Wait...", 90, 250);
	}
	
}
