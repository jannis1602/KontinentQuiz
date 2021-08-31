package pack;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JFrame;

import Image.BufferedImageLoader;

public class KontinentLocation extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	private boolean running;
	private Thread thread;
	public static JFrame frame;
	private BufferedImageLoader imgLoader;
	private String[] kontinente = { "afrika", "antarktis", "asien", "australien", "europa", "nordamerika",
			"südamerika", };

	public LinkedList<Btn> btnList;
	public LinkedList<Kontinent> kontinentList;
	private BufferedImage rand;

	private Kontinent selectedKontinent = null;

	private BufferedImage temp;

	private String end = null;

	public KontinentLocation() {
//		System.out.println(getPixelInImage(new Point(100, 100), new BufferedImageLoader().loadImage("afrika2.png")));
//		System.out.println(new BufferedImageLoader().loadImage("afrika2.png").getColorModel().hasAlpha() + " - "
//				+ isTransparent(new BufferedImageLoader().loadImage("afrika2.png"), 50, 50));
		start();
	}

	private void init() {
		imgLoader = new BufferedImageLoader();
		temp = new BufferedImageLoader().loadImage("Kontinente.png");
		// load Images
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

		// System.out.println(Toolkit.getDefaultToolkit().getScreenSize().width + " x "
		// + Toolkit.getDefaultToolkit().getScreenSize().height);
		// width = Toolkit.getDefaultToolkit().getScreenSize().width;
		// height = Toolkit.getDefaultToolkit().getScreenSize().height;
		frame = new JFrame("Kontinent Quiz");
		// BufferedImageLoader iconload = new BufferedImageLoader();
		// Image frameicon = iconload.loadImage("GameMap10Level1.png");
		// frame.setIconImage(frameicon);
		frame.add(this);
		// frame.setCursor(Cursor.MOVE_CURSOR);
//		frame.setCursor(Cursor.WAIT_CURSOR);
//		frame.setCursor(Txtoptions.optionCursor);
//		frame.setCursor(Txtoptions.optionCursor);
		// Image customImage = iconload.loadImage("cursor2.png");
		// Cursor customCursor =
		// Toolkit.getDefaultToolkit().createCustomCursor(customImage, new Point(0, 0),
		// "customCursor");
		// frame.setCursor(customCursor);
		frame.setSize(1920 / 2, 1080 / 2);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//		frame.setResizable(true);
//		frame.setUndecorated(true);
		frame.setVisible(true);
		this.setFocusTraversalKeysEnabled(false); // ???
		this.start();
		frame.setVisible(true);

		for (int i = 0; i < 14; i++) {
			Kontinent k = kontinentList.get(r.nextInt(6));
			kontinentList.remove(k);
			kontinentList.add(k);
		}

		System.out.println(frame.getWidth() / 7);
		int rec = frame.getWidth() / 7;
		for (int i = 0; i < 7; i++) {
//			System.out.println((frame.getWidth() / 7f) / kontinentList.get(i).image.getWidth() * 1f);
//			kontinentList.get(i).size = (frame.getWidth() / 7f) / kontinentList.get(i).image.getWidth() * 1f;
//			kontinentList.get(i).image = scale(kontinentList.get(i).originalImage, frame.getWidth() / 7,
//					frame.getWidth() / 7);
//			kontinentList.get(i).image = scale(kontinentList.get(i).originalImage, frame.getWidth() / 7,
//					frame.getWidth() / 7);
			kontinentList.get(i).size = (frame.getWidth() / 7f) / kontinentList.get(i).image.getWidth() * 1f;
//			System.out.println((frame.getWidth() / 7f) + " - " + kontinentList.get(i).image.getWidth() * 1f);
//			System.out.println("size: " + kontinentList.get(i).size);
			kontinentList.get(i).p = new Point(w() / 7 * i, h() - rec);
		}

		btnList.add(new Btn("Rotate", "rotateBtn", new Rectangle(frame.getWidth() - 275, 25, 225, 200)));
		btnList.add(new Btn("Check", "checkBtn", new Rectangle(frame.getWidth() - 275, 250, 225, 200)));

//		selectedKontinent = kontinentList.getFirst();

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
//		int frames = 0;
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				render();
//				frames++;
				delta--;
			}
			if (System.currentTimeMillis() - timer > 1000) {
				timer = System.currentTimeMillis();
//				frames = 0;
			}
		}
		stop();
	}

	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		// hintergrund
		g.setColor(Color.GRAY);
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
		g.setColor(Color.GRAY);

		// Kontinentauswahl
		g.setColor(Color.GRAY);
		g.fillRect(0, h() - w() / 7, frame.getWidth(), w() / 7);
		g.setColor(Color.BLACK);
		int rec = frame.getWidth() / 7;
		for (int i = 0; i < 7; i++) {
//			g.drawRect(w() / 7 * i, h() - rec, w() / 7, rec);
//			g.drawImage(kontinentList.get(i).image, w() / 7 * i, h() - rec, w() / 7, rec, null);
			kontinentList.get(i).render(g, w() / 7 * i, h() - rec, w() / 7, rec); // Box

		}

		// SideMenu
		g.setColor(Color.GRAY.darker());
		g.fillRect(frame.getWidth() - 300, 0, 300, frame.getHeight() - 300);
		g.setColor(Color.BLACK);
		g.drawRect(frame.getWidth() - 300, 0, 300, frame.getHeight() - 300);
		// g.drawRect(frame.getWidth() - 300, 300, 300, frame.getHeight() - 300);
		// g.drawRect(frame.getWidth() - 300, 600, 300, frame.getHeight() - 300);
		// rotate Btn
		// g.fillRect(frame.getWidth() - 250, 50, 200, 200);

		for (Btn tempBtn : btnList)
			tempBtn.render(g);
//TODO: render BTN

		// check btn!

		// Weltkarte
		g.setColor(new Color(0, 105, 153));
		// g.fillOval(50, 20, 1400, 700);
		int s = 12;
		if (end != null)
			g.drawImage(temp, 50, 20, 1124 * s / 10, 600 * s / 10, null);
		g.drawImage(rand, 50, 20, 1124 * s / 10, 600 * s / 10, null);

//temp	
		for (int i = 0; i < 7; i++) {
			kontinentList.get(i).render(g, w() / 7 * i, h() - rec, w() / 7, rec);
		}
		if (selectedKontinent != null)
			selectedKontinent.render(g, w() / 7, h() - rec, w() / 7, rec);

		PointerInfo a = MouseInfo.getPointerInfo();
		Point b = a.getLocation();
//		 + 16 * 2, + 24 * 2 + 15
		int mx = (int) b.getX();// + Game.frame.getLocationOnScreen().x;
		int my = (int) b.getY();// + Game.frame.getLocationOnScreen().y;

		if (selectedKontinent != null) {
			selectedKontinent.mouse(mx, my - 23);
			// selectedKontinent.p = new Point(mx, my);
			// selectedKontinent.render(g, mx, my, 300, 300);

		}
		if (end != null) {
			g.setColor(Color.RED);
			g.drawString(end, 20, 20);
		}

		g.dispose();
		bs.show();
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
//		System.out.println("btnTrigger: " + id);
		switch (id) {
		case "rotateBtn":
			if (selectedKontinent != null)
				selectedKontinent.originalImage = rotateImage(selectedKontinent.originalImage, 90.0);
			break;
		case "checkBtn":
			if (selectedKontinent == null) {
				for (Kontinent k : kontinentList) {
					System.out.println(k.name + " => " + k.p.x + "|" + k.p.y + " - " + k.size);
				}
				checkAnswere();
			}
			break;

		default:
			break;
		}

	}

	public void checkKontinentBox(Point p) {
		if (selectedKontinent == null) {
			for (Kontinent k : kontinentList) {
				if (k.boxIsTouched(p.x, p.y) && getPixelInImage(new Point(p.x - k.p.x, p.y - k.p.y), k.image)) {
					// && getPixelInImage(new Point(p.x - k.p.x, p.y - k.p.y), k.image)
					selectedKontinent = k;
					k.select(p.x, p.y);
					end = null;
				}
			}
		} else {
			Kontinent k = selectedKontinent;
			System.out.println(k.name + " - " + k.p.x + "|" + k.p.y + " - " + k.size);
			selectedKontinent = null;
		}
	}

	private boolean getPixelInImage(Point p, BufferedImage image) {
		int clr = image.getRGB(p.x, p.y);
		int alpha = (clr & 0xff) >> 24;
		int red = (clr & 0x00ff0000) >> 16;
		int green = (clr & 0x0000ff00) >> 8;
		int blue = clr & 0x000000ff;

		System.out.println("RGB Color: " + p.x + "|" + p.y + " -> " + alpha + "-" + red + "-" + green + "-" + blue);
		if (red != 0 || green != 0 || blue != 0)
			return true;
		else
			return false;
	}

	private Color getColorAt(Point p) {
		Rectangle rect = frame.getContentPane().getBounds();
		BufferedImage img = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_ARGB);
		frame.getContentPane().paintAll(img.createGraphics());
		return new Color(img.getRGB(p.x, p.y), true);
	}

	public void scroll(int amount) {
		if (selectedKontinent != null) {
			if (amount > 0)
				selectedKontinent.size += 0.02f;
			else
				selectedKontinent.size -= 0.02f;
		}
	}

	private int h() {
		return frame.getHeight() - 25;
	}

	private int w() {
		return frame.getWidth();
	}

	private void checkAnswere() {
		for (Kontinent k : kontinentList) {
			if (getSolution(k.name, k.p, k.size) == false) {
				end = "Verloren! ->" + k.name;
				return;
			}
			end = "Gewonnen!";
		}

	}

//TODO NEU!!!

	private boolean getSolution(String name, Point p, float s) { // kontinent
		Point point = new Point(0, 0);
		Float scale = 0.0f;
		switch (name) {
		case "australien":
			point = new Point(1053, 391);
			scale = 0.82f;
			break;
		case "südamerika":
			point = new Point(316, 334);
			scale = 1.10f;
			break;
		case "afrika":
			point = new Point(595, 253);
			scale = 1.02f;
			break;
		case "asien":
			point = new Point(762, 57);
			scale = 1.44f;
			break;
		case "nordamerika":
			point = new Point(260, 16);
			scale = 1.48f;
			break;
		case "antarktis":
			point = new Point(421, 352);
			scale = 2.36f;
			break;
		case "europa":
			point = new Point(676, 68);
			scale = 0.78f;
			break;
		default:
			break;
		}
		Float sc = (s - scale);
		System.out.println(
				name + " - distance(+-20) " + Point.distance(p.x, p.y, point.x, point.y) + " diff scale(+-8): " + sc);
		if (Point.distance(p.x, p.y, point.x, point.y) < 20 && (s - scale < 0.08f && s - scale > -0.08f))
			return true;
		else
			return false;
	}

}

//australien => 1053|391 - 0.8200002
//südamerika => 316|334 - 1.0999999
//afrika => 595|253 - 1.02
//asien => 762|57 - 1.4399996
//nordamerika => 260|16 - 1.4799995
//antarktis => 421|352 - 2.3199987
//europa => 666|68 - 0.7800002
