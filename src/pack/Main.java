package pack;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * Jannis Mattlage 
 */

public class Main {
	public static final String version = "0.2.5";
	public static KontinentLocation kontinentLocation = null;
	static KontinenteErkennen kontinenteErkennen = null;
	static JFrame modiFrame;

	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		if (kontinentLocation != null) {
			kontinentLocation.stop();
			kontinentLocation = null;
		}
		if (kontinenteErkennen != null) {
			kontinenteErkennen.frame.setVisible(false);
			kontinenteErkennen = null;
		}

// Launcher
		modiFrame = new JFrame("Kontinent Quiz Launcher");
		JButton jb1 = new JButton("Kontinente erkennen");
		JButton jb2 = new JButton("Kontinente platzieren");
		jb1.setFont(new Font("ROBOTO", Font.PLAIN, 40));
		jb2.setFont(new Font("ROBOTO", Font.PLAIN, 40));
		jb1.setActionCommand("erkennen");
		jb2.setActionCommand("platzieren");

		ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (e.getActionCommand()) {
				case "erkennen":
					kontinenteErkennen = new KontinenteErkennen();
					modiFrame.setVisible(false);
					break;
				case "platzieren":
					kontinentLocation = new KontinentLocation();
					modiFrame.setVisible(false);
					break;
				default:
					System.exit(0);
					break;
				}
			}
		};

		jb1.addActionListener(al);
		jb2.addActionListener(al);
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 1));
		panel.add(jb1);
		panel.add(jb2);
		modiFrame.add(panel);
		modiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		modiFrame.pack();
		modiFrame.setLocationRelativeTo(null);
		modiFrame.setVisible(true);
		panel.requestFocus();

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

}
