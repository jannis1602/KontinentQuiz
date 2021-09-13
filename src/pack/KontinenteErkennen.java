package pack;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import image.BufferedImageLoader;

public class KontinenteErkennen {
	private JLabel labelImg;
	private JTextField tf;
	JLabel labelRichtig, labelFalsch;
	private static BufferedImageLoader imgLoader;
	private int richtig = 0;
	private int falsch = 0;
	private int versuche = 0;
	private String[] kontinente = { "afrika", "antarktis", "asien", "australien", "europa", "nordamerika",
			"südamerika", };
	private String kontinent = null;
	public JFrame frame = null;

	// Kontinente erkennen - Game

	public KontinenteErkennen() {
		imgLoader = new BufferedImageLoader();
		frame = new JFrame("Kontinent Quiz");
		frame.setSize(1920, 1080);
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel.setBackground(Color.DARK_GRAY);
		BufferedImage img = imgLoader.loadImage("continents/afrika.png");

		labelRichtig = new JLabel("  Richtig: 0");
		labelFalsch = new JLabel("  Falsch: 0");
		labelRichtig.setForeground(Color.GREEN);
		labelFalsch.setForeground(Color.RED);
		labelRichtig.setFont(new Font("ROBOTO", Font.PLAIN, 18));
		labelFalsch.setFont(new Font("ROBOTO", Font.PLAIN, 18));

		GridBagConstraints c0 = new GridBagConstraints();
		c0.fill = GridBagConstraints.BOTH;
		c0.gridx = 0;
		c0.gridy = 0;
		c0.weightx = 1;
		c0.weighty = 0;
		c0.gridwidth = 1;
		panel.add(labelRichtig, c0);
		GridBagConstraints c01 = new GridBagConstraints();
		c01.fill = GridBagConstraints.BOTH;
		c01.gridx = 0;
		c01.gridy = 1;
		c01.weightx = 1;
		c01.weighty = 0;
		c01.gridwidth = 1;
		panel.add(labelFalsch, c01);

		labelImg = new JLabel(new ImageIcon(img), JLabel.CENTER);

		GridBagConstraints c1 = new GridBagConstraints();
		c1.fill = GridBagConstraints.BOTH;
		c1.gridx = 0;
		c1.gridy = 2;
		c1.weightx = 1;
		c1.weighty = 1;
		c1.gridwidth = 1;
		panel.add(labelImg, c1);

		Panel panelText = new Panel();
		panelText.setLayout(new FlowLayout());
		JLabel labelQuestion = new JLabel("Name des Kontinents: ");
		labelQuestion.setFont(new Font("ROBOTO", Font.PLAIN, 18));
		panelText.add(labelQuestion);
		tf = new JTextField(20);
		tf.setFont(new Font("ROBOTO", Font.PLAIN, 18));
		tf.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					checkAnswere();
				}
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					new Main();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
		panelText.add(tf);
		JButton btnOK = new JButton("OK");
		btnOK.setFont(new Font("ROBOTO", Font.PLAIN, 18));
		btnOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				checkAnswere();
			}
		});
		panelText.add(btnOK);
		panelText.setBackground(Color.GRAY);

		GridBagConstraints c2 = new GridBagConstraints();
		c2.fill = GridBagConstraints.BOTH;
		c2.gridx = 0;
		c2.gridy = 3;
		c2.weightx = 1;
		c2.weighty = 1;
		c2.gridwidth = 8;
		panel.add(panelText, c2);
		frame.add(panel);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		loadImage();
	}

	private void checkAnswere() {

		if (tf.getText().toLowerCase().replace(" ", "").equals(kontinent)) {
			richtig++;
			labelRichtig.setText("  Richtig: " + richtig);
			loadImage();
			versuche = 0;
		} else {
			versuche++;
			falsch++;
			labelFalsch.setText("  Falsch: " + falsch);
			if (versuche >= 3) {
				versuche = 0;
				loadImage();
			}
		}
		tf.setText(null);
	}

	private void loadImage() {
		Random r = new Random();
		String kont = kontinente[r.nextInt(6)];
		while (kont == kontinent)
			kont = kontinente[r.nextInt(6)];
		BufferedImage img = imgLoader.loadImage("continents/" + kont + ".png");
		kontinent = kont;
		img = rotate(img, r.nextInt(11) * 90.0);
		labelImg.setIcon(new ImageIcon(img));
	}

	public BufferedImage rotate(BufferedImage image, Double degrees) {
		double radians = Math.toRadians(degrees);
		double sin = Math.abs(Math.sin(radians));
		double cos = Math.abs(Math.cos(radians));
		int newWidth = (int) Math.round(image.getWidth() * cos + image.getHeight() * sin);
		int newHeight = (int) Math.round(image.getWidth() * sin + image.getHeight() * cos);
		BufferedImage rotate = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = rotate.createGraphics();
		int x = (newWidth - image.getWidth()) / 2;
		int y = (newHeight - image.getHeight()) / 2;
		AffineTransform at = new AffineTransform();
		at.setToRotation(radians, x + (image.getWidth() / 2), y + (image.getHeight() / 2));
		at.translate(x, y);
		g2d.setTransform(at);
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();
		return rotate;
	}

}
