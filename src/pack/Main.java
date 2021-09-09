package pack;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import image.BufferedImageLoader;

/*
 * Jannis Mattlage 
 */

public class Main {
	public static final String version = "0.2.0";
	private JLabel labelImg;
	private JTextField tf;
	JLabel labelRichtig, labelFalsch;
	private BufferedImageLoader imgLoader;
	private int richtig = 0;
	private int falsch = 0;
	private int versuche = 0;
	private static String serverReleaseVersion;
	private static String updateUrl = null;

	static JFrame modiFrame;

	private String[] kontinente = { "afrika", "antarktis", "asien", "australien", "europa", "nordamerika",
			"südamerika", };
	private String kontinent = null;

	public static void main(String[] args) {
		if (args.length >= 1) {
			try {
				File file = new File(args[0]);
				file.delete();
				System.out.println("File deleted: " + args[0]);
			} catch (Exception e) {
				System.out.println("File Error!");
			}
		}

// Launcher

		modiFrame = new JFrame("Kontinent Quiz Launcher");
		JButton jb1 = new JButton("Kontinente erkennen");
		JButton jb2 = new JButton("Kontinente platzieren");
		JButton jb3 = new JButton("Kontinentnamen zuordnen");
		jb1.setFont(new Font("Arial", Font.PLAIN, 40));
		jb2.setFont(new Font("Arial", Font.PLAIN, 40));
		jb3.setFont(new Font("Arial", Font.PLAIN, 40));
		jb1.setActionCommand("erkennen");
		jb2.setActionCommand("platzieren");
		jb3.setActionCommand("zuordnen");

		ActionListener al = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				switch (e.getActionCommand()) {
				case "erkennen":
					new Main();
					modiFrame.setVisible(false);
					break;
				case "platzieren":
					new KontinentLocation();
					modiFrame.setVisible(false);
					break;
				case "zuordnen":
					JOptionPane.showMessageDialog(modiFrame, "noch in Arbeit");
					break;
				default:
					System.exit(0);
					break;
				}
			}
		};

		jb1.addActionListener(al);
		jb2.addActionListener(al);
		jb3.addActionListener(al);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3, 1));
		panel.add(jb1);
		panel.add(jb2);
		panel.add(jb3);
		modiFrame.add(panel);
		modiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		modiFrame.setVisible(true);
		modiFrame.pack();
		modiFrame.setLocationRelativeTo(null);
		panel.requestFocus();

// request update			
		if (true) {
			getLastReleaseVersion("https://api.github.com/repos/jannis1602/KontinentQuiz/releases");

			int v = Integer.parseInt(version.replaceAll("[^0-9]", ""));
			int rv = Integer.parseInt(serverReleaseVersion.replaceAll("[^0-9]", ""));
			System.out.println("check for updates " + v + " -> " + rv);

			File filePath = null;
			try {
				filePath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI() + "\\"
						+ updateUrl.split("/")[updateUrl.split("/").length - 1]);
				System.out.println("FilePath: " + filePath);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			if (v < rv) {
				int option = JOptionPane.showConfirmDialog(modiFrame,
						"Update to v." + serverReleaseVersion.replaceAll("[^0-9.]", ""), "Update?",
						JOptionPane.YES_NO_OPTION);
				System.out.println(option);
				if (option == 0) {
					System.out.println("Server versionTag: " + serverReleaseVersion);
					try {
						System.out.println(updateUrl);
						InputStream in = new URL(updateUrl).openStream();
						Files.copy(in,
								Paths.get(getJarExecutionDirectory()
										+ updateUrl.split("/")[updateUrl.split("/").length - 1]),
								StandardCopyOption.REPLACE_EXISTING);
						Runtime.getRuntime()
								.exec("cmd /c start " + getJarExecutionDirectory()
										+ updateUrl.split("/")[updateUrl.split("/").length - 1] + " "
										+ System.getProperty("java.class.path"));
						Thread.sleep(200);
						System.exit(0);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	public static void getLastReleaseVersion(String webseite) {
		try {
			URL url = new URL(webseite);
			Scanner scanner = new Scanner(url.openStream());
			String s = null;
			while (scanner.hasNext()) {
				s = scanner.nextLine();
				if (s.contains("html_url") && serverReleaseVersion == null)
					serverReleaseVersion = s.split("/tag/v.")[1].split("\",")[0];
				if (s.contains("browser_download_url") && updateUrl == null) {
					updateUrl = "https" + s.split("browser_download_url\":\"https")[1].split("jar")[0] + "jar";
				}
				if (serverReleaseVersion != null && updateUrl != null) {
					System.out.println("updateUrl: " + updateUrl);
					scanner.close();
					return;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(modiFrame, "Konnte nicht auf Updates prüfen.\n Aktuelle Version: " + version);
			Desktop desktop = java.awt.Desktop.getDesktop();
			try {
				URI uri = new URI("https://github.com/jannis1602/KontinentQuiz/releases/latest");
				desktop.browse(uri);
			} catch (Exception e2) {
			}
		}
	}

	public static String getJarExecutionDirectory() {
		String jarFile = null;
		String jarDirectory = null;
		int cutFileSeperator = 0;
		int cutSemicolon = -1;
		jarFile = System.getProperty("java.class.path");
		cutFileSeperator = jarFile.lastIndexOf(System.getProperty("file.separator"));
		jarDirectory = jarFile.substring(0, cutFileSeperator);
		cutSemicolon = jarDirectory.lastIndexOf(';');
		jarDirectory = jarDirectory.substring(cutSemicolon + 1, jarDirectory.length());
		return jarDirectory + System.getProperty("file.separator");
	}

// Kontinente erkennen - Game	

	public Main() {
		imgLoader = new BufferedImageLoader();
		JFrame frame = new JFrame("Kontinent Quiz");
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
