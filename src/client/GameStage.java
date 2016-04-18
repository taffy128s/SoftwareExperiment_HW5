package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GameStage extends JPanel implements Runnable {

	private int nowScore, winScore, duckDirection, duckY, duckX, ballDirection, ballY, bgX;
	private BufferedImage bg, duck, ball, win;
	private JLabel label = new JLabel("<html><font color='red'>Score: 0</font></html>");
	private boolean over = false;
	
	GameStage() {
		setLayout(null);
		// initial values
		nowScore = 0;
		winScore = 100;
		bgX = 0;
		duckDirection = 0;
		duckX = 125;
		duckY = 340;
		ballDirection = 0;
		ballY = 360;
		add(label);
		label.setBounds(580, 0, 100, 20);
		label.setForeground(Color.red);
		setBackground(Color.CYAN);
		// read all the pictures
		try {
			bg = ImageIO.read(new File("res/h.png"));
			duck = ImageIO.read(new File("res/duck.png"));
			ball = ImageIO.read(new File("res/b.png"));
			win = ImageIO.read(new File("res/win.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setNowScore(int data) {
		// change the nowScore value, and set the text to nowScore
		nowScore = data;
		label.setText("Score: " + nowScore);
	}
	
	public int getNowScore() { return nowScore; }
	
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(80);
				// move the duck and ball up and down
				if (duckDirection < 15) duckY--;
				else duckY++;
				duckDirection = (duckDirection + 1) % 30;
				if (ballDirection < 10) ballY--;
				else ballY++;
				ballDirection = (ballDirection + 1) % 20;
				// if nowScore < 50, just move the background picture
				// else move the duck
				if (nowScore < 50) {
					if (-bgX < nowScore * 6) bgX--;
				} else if (nowScore <= winScore) {
					if (duckX < 125 + (nowScore - 45) * 4) duckX++;
				}
				repaint();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// if duck is at the final position
			if (duckX >= 125 + 55 * 4) {
				over = true;
				break;
			}
		}
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		// draw all the pictures
		super.paintComponent(g);
		g.drawImage(bg, bgX, 0, 1117, 286, null);
		g.drawImage(duck, duckX, duckY, 132, 103, null);
		g.drawImage(ball, 450, ballY, 100, 91, null);
		if (over) g.drawImage(win, 170, 170, 313, 232, null);
	}
}
