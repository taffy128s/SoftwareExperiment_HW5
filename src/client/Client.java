package client;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Client {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// new a window and set the values
				MyWindow window = new MyWindow();
				window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				window.setSize(950, 550);
				window.setVisible(true);
			}
		});
	}
}
