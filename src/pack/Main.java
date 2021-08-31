package pack;

import java.awt.Color;
import java.awt.FlowLayout;
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
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Image.BufferedImageLoader;

public class Main {
	public static final String version = "0.1.3";
	private JLabel labelImg;
	private JTextField tf;
	JLabel labelRichtig, labelFalsch;
	private BufferedImageLoader imgLoader;
	private int richtig = 0;
	private int falsch = 0;

	private String[] kontinente = { "afrika", "antarktis", "asien", "australien", "europa", "nordamerika",
			"südamerika", };
	private String kontinent = null;

	public static void main(String[] args) {

		// https://api.github.com/repos/jannis1602/KontinentQuiz/releases

		// System.out.println(getLastReleaseVersion("https://api.github.com/repos/jannis1602/KontinentQuiz/releases"));

		int v = Integer.parseInt(version.replaceAll("[^0-9]", ""));
		int rv = Integer
				.parseInt(getLastReleaseVersion("https://api.github.com/repos/jannis1602/KontinentQuiz/releases")
						.replaceAll("[^0-9]", ""));
		System.out.println("check for updates " + v + " -> " + rv);

//TODO request update window
		if (v < rv) {
			String versionTag = getLastReleaseVersion("https://api.github.com/repos/jannis1602/KontinentQuiz/releases");
			System.out.println("versionTag: " + versionTag);
			try (BufferedInputStream in = new BufferedInputStream(
					new URL("https://github.com/jannis1602/KontinentQuiz/releases/download/" + versionTag
							+ "/KontinentQuiz.jar").openStream());
					FileOutputStream fileOutputStream = new FileOutputStream(
							getJarExecutionDirectory() + "\\KontinentQuiz.jar")) {
				byte dataBuffer[] = new byte[1024];
				int bytesRead;
				while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
					fileOutputStream.write(dataBuffer, 0, bytesRead);
				}
				System.exit(0);
				Runtime.getRuntime().exec("cmd /c start " + getJarExecutionDirectory() + "\\KontinentQuiz.jar");

			} catch (IOException e) {
			}
		}

		// new Main();
		new KontinentLocation();
	}

	private static String replaceAll(String string, String string2) {
		// TODO Auto-generated method stub
		return null;
	}

	public static String getLastReleaseVersion(String webseite) {
		String releaseVersion = null;
		try {
			URL url = new URL(webseite);
			Scanner scanner = new Scanner(url.openStream());
			String s = null;
			while (scanner.hasNext()) {
				s = scanner.next();
				if (s.contains("html_url")) {
					releaseVersion = s.split("/tag/v.")[1].split("\",")[0];
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return releaseVersion;
	}

	public static String getJarExecutionDirectory() {
		String jarFile = null;
		String jarDirectory = null;
		int cutFileSeperator = 0;
		int cutSemicolon = -1;
		jarFile = System.getProperty("java.class.path");
		System.out.println(jarFile);
		cutFileSeperator = jarFile.lastIndexOf(System.getProperty("file.separator"));
		jarDirectory = jarFile.substring(0, cutFileSeperator);
		cutSemicolon = jarDirectory.lastIndexOf(';');
		jarDirectory = jarDirectory.substring(cutSemicolon + 1, jarDirectory.length());
		return jarDirectory + System.getProperty("file.separator");
	}

	public Main() {
		imgLoader = new BufferedImageLoader();
		JFrame frame = new JFrame("Kontinent Quiz");
		frame.setSize(800, 450);
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BufferedImage img = imgLoader.loadImage("afrika.png");

		labelRichtig = new JLabel("Richtig:0");
		labelFalsch = new JLabel("Falsch:0");

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
//		labelImg.setBackground(Color.DARK_GRAY);

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
		panelText.add(labelQuestion);
		tf = new JTextField(20);
		tf.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					checkAnswere();
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
		btnOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				checkAnswere();
			}
		});
		panelText.add(btnOK);
		panelText.setBackground(Color.LIGHT_GRAY);

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
		System.out.println("check");
		System.out.println(tf.getText() + " >>> " + kontinent);
		if (tf.getText().toLowerCase().replace(" ", "").equals(kontinent)) {
			System.out.println("True");
			richtig++;
			labelRichtig.setText("Richtig:" + richtig);
			loadImage();
		} else {
			falsch++;
			labelFalsch.setText("Falsch:" + falsch);
		}
		tf.setText(null);
	}

	private void loadImage() {
		Random r = new Random();
		String kont = kontinente[r.nextInt(6)];
		while (kont == kontinent)
			kont = kontinente[r.nextInt(6)];
		BufferedImage img = imgLoader.loadImage(kont + ".png");
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

	public static int getWidth() {
		return 1;
	}

	public static int getHight() {
		return 1;
	}

}
