package pack;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Image.BufferedImageLoader;

public class KontinentLocation extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	private boolean running;
	private Thread thread;
	public static JFrame frame;
	private BufferedImageLoader imgLoader;
	private String[] kontinente = { "afrika", "antarktis", "asien", "australien", "europa", "nordamerika",
			"s�damerika", };

	public LinkedList<Btn> btnList;
	public LinkedList<Kontinent> kontinentList;
	private float scale = 1f;
	private Kontinent selectedKontinent = null;
	private boolean draggingKontinent = false;
	private BufferedImage rand;
	private BufferedImage solutionImage;
	private String end = null;
	private boolean showSolution = false;
	public static Point mouseXY = new Point(0, 0);

	public KontinentLocation() {
		start();
	}

	private void init() {
		imgLoader = new BufferedImageLoader();
// load Images
		solutionImage = new BufferedImageLoader().loadImage("Kontinente.png");
		kontinentList = new LinkedList<Kontinent>();
		Random r = new Random();
		for (String s : kontinente) {
			kontinentList.add(new Kontinent(s, rotateImage(loadImage(s), 90.0 * r.nextInt(11))));
		}
		rand = loadImage("rand");

		btnList = new LinkedList<Btn>();
		// Frame
		MouseInput mouseInput = new MouseInput(this);
		addMouseListener(mouseInput);
		addMouseWheelListener(mouseInput);
		addMouseMotionListener(mouseInput);
		frame = new JFrame("Kontinent Quiz");
		// BufferedImageLoader iconload = new BufferedImageLoader();
		// Image frameicon = iconload.loadImage("GameMap10Level1.png");
		// frame.setIconImage(frameicon);
		frame.add(this);
		// frame.setSize(1280, 720);
		frame.setSize(1920, 1080);

// Buttons
		btnList.add(new Btn("Rotate L", "rotateBtnL", new Rectangle(400, 25, 175, 175)));
		btnList.add(new Btn("Rotate R", "rotateBtnR", new Rectangle(200, 25, 175, 175)));
		btnList.add(new Btn("Scale +", "scaleBtnBigger", new Rectangle(400, 225, 175, 175)));
		btnList.add(new Btn("Scale -", "scaleBtnSmaller", new Rectangle(200, 225, 175, 175)));
		btnList.add(new Btn("Check", "checkBtn", new Rectangle(350, 425, 300, 100)));
		btnList.add(new Btn("L�sung", "solutionBtn", new Rectangle(350, 550, 300, 100)));
		btnList.add(new Btn("Reset", "resetBtn", new Rectangle(350, 675, 300, 100)));

		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		// frame.setSize(1280, 720);
		this.setFocusTraversalKeysEnabled(false); // ???
		this.start();
		frame.setVisible(true);

		for (int i = 0; i < 14; i++) {
			Kontinent k = kontinentList.get(r.nextInt(6));
			kontinentList.remove(k);
			kontinentList.add(k);
		}

		for (int i = 0; i < 7; i++) {
			kontinentList.get(i).size = (frame.getWidth() / 7f) / kontinentList.get(i).image.getWidth() * 1f;
			kontinentList.get(i).rect = new Rectangle(w() / 7 * i, h() - frame.getWidth() / 7, 10, 10);
		}
	}

	private BufferedImage loadImage(String name) {
		BufferedImage img = imgLoader.loadImage(name + ".png");
		return img;
	}

	private synchronized void start() {
		if (running)
			return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public synchronized void stop() {
		if (!running)
			return;
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		init();
		this.requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 30.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				render();
				delta--;
			}
			if (System.currentTimeMillis() - timer > 1000) {
				timer = System.currentTimeMillis();
			}
		}
		stop();
	}

	private void render() { // TODO scale 2d ???
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		scale = w() / 1920f;
		if (h() / 1080f < scale && h() / 1080f < 1)
			scale = h() / 1080f;

// Background
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, frame.getWidth(), frame.getHeight());

// SideMenu
		g.setColor(Color.GRAY.darker());
		g.fillRect(w() - (int) (450 * scale), 0, (int) (450 * scale), h() - w() / 7);
		g.setColor(Color.BLACK);
		g.drawRect(w() - (int) (450 * scale), 0, (int) (450 * scale), h() - w() / 7);

		for (Btn tempBtn : btnList)
			tempBtn.render(g, getWidth(), getHeight(), scale);

// Weltkarte
		g.setColor(new Color(0, 105, 153));
		if (showSolution)
			g.drawImage(solutionImage, 50, 20, (int) (1348 * scale), (int) (720 * scale), null);
		g.drawImage(rand, 50, 20, (int) (1348 * scale), (int) (720 * scale), null);

// Kontinentauswahl
		g.setColor(Color.GRAY);
		g.fillRect(0, h() - w() / 7 - 10, frame.getWidth(), w() / 7);
		g.setColor(Color.BLACK);
		int rec = frame.getWidth() / 7;
		for (int i = 0; i < 7; i++) {
			kontinentList.get(i).moveInObjectScreen(new Rectangle(0, 0, w(), h()));
			kontinentList.get(i).render(g, w() / 7 * i, h() - rec, w() / 7, rec);

		}
		if (selectedKontinent != null) {
			selectedKontinent.render(g, w() / 7, h() - rec, w() / 7, rec);
			g.setColor(Color.BLUE);
			g.drawRect(selectedKontinent.rect.x, selectedKontinent.rect.y, selectedKontinent.rect.width,
					selectedKontinent.rect.height);
		}

		if (selectedKontinent != null && draggingKontinent) {
			Point b = mouseXY;
			int mx = (int) b.getX();
			int my = (int) b.getY();
			selectedKontinent.mouse(mx, my);
		}
		if (end != null) {
			g.setColor(Color.RED);
			g.drawString(end, 20, 20);
		}

		g.dispose();
		bs.show();
	}

	public static BufferedImage scale(BufferedImage imageToScale, int dWidth, int dHeight) {
		BufferedImage scaledImage = null;
		if (imageToScale != null) {
			scaledImage = new BufferedImage(dWidth, dHeight, imageToScale.getType());
			Graphics2D graphics2D = scaledImage.createGraphics();
			graphics2D.drawImage(imageToScale, 0, 0, dWidth, dHeight, null);
			graphics2D.dispose();
		}
		return scaledImage;
	}

	public BufferedImage rotateImage(BufferedImage image, Double degrees) {
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

	public void triggerBtn(String id) {
		switch (id) {
		case "rotateBtnL":
			if (selectedKontinent != null)
				selectedKontinent.originalImage = rotateImage(selectedKontinent.originalImage, -90.0);
			break;
		case "rotateBtnR":
			if (selectedKontinent != null)
				selectedKontinent.originalImage = rotateImage(selectedKontinent.originalImage, 90.0);
			break;
		case "scaleBtnBigger":
			if (selectedKontinent != null)
				scroll(3);
			break;
		case "scaleBtnSmaller":
			if (selectedKontinent != null)
				scroll(-3);
			break;
		case "checkBtn":
			if (!draggingKontinent) {
				selectedKontinent = null;
				for (Kontinent k : kontinentList)
					System.out.println(k.name + " => " + k.rect.x + "|" + k.rect.y + " - " + k.size);
				checkAnswere();
			}
			break;
		case "solutionBtn":
			showSolution = !showSolution;
			break;
		case "resetBtn":
			Random r = new Random();
			for (int i = 0; i < 14; i++) {
				Kontinent k = kontinentList.get(r.nextInt(6));
				kontinentList.remove(k);
				kontinentList.add(k);
			}
			for (int i = 0; i < 7; i++) {
				kontinentList.get(i).size = (frame.getWidth() / 7f) / kontinentList.get(i).originalImage.getWidth();
				kontinentList.get(i).rect = new Rectangle(w() / 7 * i, h() - frame.getWidth() / 7, 10, 10);
			}
			showSolution = false;
			selectedKontinent = null;
			end = null;
			break;
		default:
			break;
		}

	}

	public void checkKontinentBox(Point p, boolean selectMode) {
		if (selectMode) {
			for (Kontinent k : kontinentList) {
				if (k.boxIsTouched(p.x, p.y) && getPixelInImage(new Point(p.x - k.rect.x, p.y - k.rect.y), k.image)) {
					// TODO if selected == touched => null
//					if (selectedKontinent != null) {
//						System.out.println(k.name + " - " + selectedKontinent.name);
//						if (k.name.contains(selectedKontinent.name)) {
//							selectedKontinent = null;
//							System.out.println(k.name + " - " + k.rect.x + "|" + k.rect.y + " - " + k.size);
//							draggingKontinent = false;
//							return;
//						}
//					}
					selectedKontinent = k;
					k.select(p.x, p.y);
					end = null;
					draggingKontinent = true;
				}
			}
		} else if (draggingKontinent) {
			Kontinent k = selectedKontinent;
			System.out.println(k.name + " - " + k.rect.x + "|" + k.rect.y + " - " + k.size);
			draggingKontinent = false;
		}
	}

	private boolean getPixelInImage(Point p, BufferedImage image) {
		int clr = image.getRGB(p.x, p.y);
		int red = (clr & 0x00ff0000) >> 16;
		int green = (clr & 0x0000ff00) >> 8;
		int blue = clr & 0x000000ff;
		if (red != 0 || green != 0 || blue != 0)
			return true;
		else
			return false;
	}

	public void scroll(int amount) {// , int mx, int my) {
		if (selectedKontinent != null) {
			if (amount > 0) { // TODO mouse Position...
//				selectedKontinent.rect.x += (selectedKontinent.rect.width * 0.02f) / 2;
//				selectedKontinent.rect.y += (selectedKontinent.rect.height * 0.02f) / 2;
//				selectedKontinent.dx += (selectedKontinent.rect.width * 0.02f) / 2;
//				selectedKontinent.dy += (selectedKontinent.rect.height * 0.02f) / 2;
//				selectedKontinent.dx = (int) (selectedKontinent.dx * 0.02f);
//				selectedKontinent.dy = (int) (selectedKontinent.dy * 0.02f);
//				System.out.println(selectedKontinent.dx + " " + selectedKontinent.dy);
				selectedKontinent.size += 0.02f;

			} else {
//				selectedKontinent.rect.x -= (selectedKontinent.rect.width * 0.02f) / 2;
//				selectedKontinent.rect.y -= (selectedKontinent.rect.height * 0.02f) / 2;
//				selectedKontinent.dx -= (selectedKontinent.rect.width * 0.02f) / 2;
//				selectedKontinent.dy -= (selectedKontinent.rect.height * 0.02f) / 2;
				selectedKontinent.size -= 0.02f;
			}
		}
	}

	private int h() {
		return (int) (frame.getHeight() - 25 * scale); // 25p = taskbar
	}

	private int w() {
		if (frame.getExtendedState() != 0)
			return (int) (frame.getWidth());
		return (int) (frame.getWidth() - 16 * scale);
	}

	private void checkAnswere() {
		LinkedList<String> wrongContinents = new LinkedList<String>();
		for (Kontinent k : kontinentList) {
			if (getSolution(k.name, new Point(k.rect.x, k.rect.y), k.size) == false) {
				wrongContinents.add(k.name);
			}
		}
		if (wrongContinents.size() >= 1) {
			end = "Verloren! Falsch: ";
			for (String s : wrongContinents) {
				end += s + " & ";
			}
			end = end.substring(0, end.length() - 2);

			JOptionPane.showMessageDialog(this, end);
			return;
		}
		end = "Gewonnen!";
		JOptionPane.showMessageDialog(this, end);
	}

	private boolean getSolution(String name, Point p, float s) { // kontinent
		Point point = new Point(0, 0);
		Float cScale = 0.0f;
		switch (name) {
		case "australien":
			point = new Point(1053, 391);
			cScale = 0.42f;
			break;
		case "s�damerika":
			point = new Point(316, 334);
			cScale = 0.56f;
			break;
		case "afrika":
			point = new Point(595, 253);
			cScale = 0.56f;
			break;
		case "asien":
			point = new Point(762, 57);
			cScale = 0.76f;
			break;
		case "nordamerika":
			point = new Point(260, 16);
			cScale = 0.78f;
			break;
		case "antarktis":
			point = new Point(421, 352);
			cScale = 1.26f;
			break;
		case "europa":
			point = new Point(676, 68);
			cScale = 0.42f;
			break;
		default:
			break;
		}
		Float sc = (s - cScale * scale);
		System.out.println(name + " - distance(+-40) " + Point.distance(p.x, p.y, point.x * scale, point.y * scale)
				+ " - diff. scale(+-0.08): " + sc);
		System.out.println(name + " - " + p.x + " -> " + point.x * scale + " || " + p.y + " -> " + point.y * scale
				+ " || " + s + " -> " + cScale * scale);
		if (Point.distance(p.x, p.y, point.x * scale, point.y * scale) < 40 // 20
				&& (sc < 0.08f && sc > -0.08f))
			return true;
		else
			return false;
	}

}
