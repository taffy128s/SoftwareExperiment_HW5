package client;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class MyWindow extends JFrame {
	
	private GameStage gs = new GameStage();
	private Typing tp = new Typing(gs);
	
	MyWindow() {
		// set basic values
		setLayout(null);
		setResizable(false);
		setTitle("HumanOCRun");
		add(gs);
		add(tp);
		gs.setBounds(300, 0, 1117, 550);
		tp.setBounds(0, 0, 300, 550);
		// start the threads
		Thread tpThread = new Thread(tp);
		tp.setIPAddress("127.0.0.1").setPort(6666).connect();
		tpThread.start();
		Thread gsThread = new Thread(gs);
		gsThread.start();
	}
}
