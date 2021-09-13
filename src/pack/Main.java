package pack;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/*
 * Jannis Mattlage 
 */

public class Main {
	public static final String version = "0.2.4";
	public static KontinentLocation kontinentLocation = null;
	static KontinenteErkennen kontinenteErkennen = null;
	static JFrame modiFrame;

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
		JButton jb3 = new JButton("Kontinentnamen zuordnen");
		jb1.setFont(new Font("ROBOTO", Font.PLAIN, 40));
		jb2.setFont(new Font("ROBOTO", Font.PLAIN, 40));
		jb3.setFont(new Font("ROBOTO", Font.PLAIN, 40));
		jb1.setActionCommand("erkennen");
		jb2.setActionCommand("platzieren");
		jb3.setActionCommand("zuordnen");

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
		panel.setLayout(new GridLayout(2, 1));// 3
		panel.add(jb1);
		panel.add(jb2);
//				panel.add(jb3);
		modiFrame.add(panel);
		modiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		modiFrame.setVisible(true);
		modiFrame.pack();
		modiFrame.setLocationRelativeTo(null);
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
