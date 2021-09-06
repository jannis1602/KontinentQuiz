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
	private float scale = 1f;

	private Kontinent selectedKontinent = null;
	private boolean draggingKontinent = false;

	private BufferedImage temp;

	private String end = null;

	public static Point mouseXY = new Point(0, 0);

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
		// Image customImage = iconload.loadImage("cursor2.png");
		// Cursor customCursor =
		// Toolkit.getDefaultToolkit().createCustomCursor(customImage, new Point(0, 0),
		// "customCursor");
		// frame.setCursor(customCursor);

//		frame.setSize(1280, 720);
		frame.setSize(1920, 1080);

		btnList.add(new Btn("Rotate L", "rotateBtnL", new Rectangle(400, 25, 175, 175)));
		btnList.add(new Btn("Rotate R", "rotateBtnR", new Rectangle(200, 25, 175, 175)));
		btnList.add(new Btn("Scale +", "scaleBtnBigger", new Rectangle(400, 225, 175, 175)));
		btnList.add(new Btn("Scale -", "scaleBtnSmaller", new Rectangle(200, 225, 175, 175)));
		btnList.add(new Btn("Check", "checkBtn", new Rectangle(350, 425, 300, 100)));
//		btnList.add(new Btn(temp, "checkBtn", new Rectangle(frame.getSize().width - 250, 475, 225, 200)));

//		frame.setSize(1920 / 2, 1080 / 2);

		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//		frame.setSize(1280, 720);

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

		int rec = frame.getWidth() / 7;
		for (int i = 0; i < 7; i++) {
			kontinentList.get(i).size = (frame.getWidth() / 7f) / kontinentList.get(i).image.getWidth() * 1f;
			kontinentList.get(i).rect = new Rectangle(w() / 7 * i, h() - rec, 10, 10);
		}

//		btnList.add(new Btn("Rotate", "rotateBtn", new Rectangle(frame.getWidth() - 275, 25, 225, 200)));
//		btnList.add(new Btn("Check", "checkBtn", new Rectangle(frame.getWidth() - 275, 250, 225, 200)));

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
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				render2d();
				delta--;
			}
			if (System.currentTimeMillis() - timer > 1000) {
				timer = System.currentTimeMillis();
			}
		}
		stop();
	}

	private void render2d() { // TODO scale 2d ???
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		scale = w() / 1920f;
		if (h() / 1080f < scale && h() / 1080f < 1)
			scale = h() / 1080f;

		// hintergrund
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
		if (end != null)
			g.drawImage(temp, 50, 20, (int) (1348 * scale), (int) (720 * scale), null);
		g.drawImage(rand, 50, 20, (int) (1348 * scale), (int) (720 * scale), null);

		// Kontinentauswahl
		g.setColor(Color.GRAY);
		g.fillRect(0, h() - w() / 7 - 10, frame.getWidth(), w() / 7);
		g.setColor(Color.BLACK);
		int rec = frame.getWidth() / 7;
		for (int i = 0; i < 7; i++) {
//			g.drawRect(w() / 7 * i, h() - rec, w() / 7, rec);
//			g.drawImage(kontinentList.get(i).image, w() / 7 * i, h() - rec, w() / 7, rec, null);
			kontinentList.get(i).moveInObjectScreen(new Rectangle(0, 0, w(), h()));
			kontinentList.get(i).render(g, w() / 7 * i, h() - rec, w() / 7, rec);

		}
		if (selectedKontinent != null) {
			selectedKontinent.render(g, w() / 7, h() - rec, w() / 7, rec);
			g.setColor(Color.BLUE);
			g.drawRect(selectedKontinent.rect.x, selectedKontinent.rect.y, selectedKontinent.rect.width,
					selectedKontinent.rect.height);
		}

//		PointerInfo a = MouseInfo.getPointerInfo();
//		Point b = a.getLocation();
		if (selectedKontinent != null && draggingKontinent) {
			Point b = mouseXY;
			int mx = (int) b.getX();// + Game.frame.getLocationOnScreen().x;
			int my = (int) b.getY();// + Game.frame.getLocationOnScreen().y;

			selectedKontinent.mouse(mx, my); // - 23
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

	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();

		scale = w() / 1920f;
		if (h() / 1080f < scale && h() / 1080f >= 1)
			scale = 1080f / h();

		// hintergrund
		g.setColor(Color.GRAY);
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
		g.setColor(Color.GRAY);

		// SideMenu
		g.setColor(Color.GRAY.darker());
		g.fillRect(frame.getWidth() - (int) (300 * scale), 0, (int) (300 * scale),
				frame.getHeight() - (int) (300 * scale));
		g.setColor(Color.BLACK);
		g.drawRect(frame.getWidth() - (int) (300 * scale), 0, (int) (300 * scale),
				frame.getHeight() - (int) (300 * scale));
		// g.drawRect(frame.getWidth() - 300, 300, 300, frame.getHeight() - 300);
		// g.drawRect(frame.getWidth() - 300, 600, 300, frame.getHeight() - 300);
		// rotate Btn
		// g.fillRect(frame.getWidth() - 250, 50, 200, 200);

		for (Btn tempBtn : btnList)
			tempBtn.render(g, w(), h(), scale);

//		for (Btn tempBtn : btnList) {
//			tempBtn.renderBtn(g, scale);
//		}
//TODO: render BTN

		// check btn!

		// Weltkarte
		g.setColor(new Color(0, 105, 153));
		// g.fillOval(50, 20, 1400, 700);
		int s = 12;
		if (end != null)
			g.drawImage(temp, 50, 20, 1124 * s / 10, 600 * s / 10, null);
		g.drawImage(rand, 50, 20, 1124 * s / 10, 600 * s / 10, null);

		// Kontinentauswahl
		g.setColor(Color.GRAY);
		g.fillRect(0, h() - w() / 7, frame.getWidth(), w() / 7);
		g.setColor(Color.BLACK);
		int rec = frame.getWidth() / 7;
		for (int i = 0; i < 7; i++) {
//			g.drawRect(w() / 7 * i, h() - rec, w() / 7, rec);
//			g.drawImage(kontinentList.get(i).image, w() / 7 * i, h() - rec, w() / 7, rec, null);
			kontinentList.get(i).moveInObjectScreen(new Rectangle(0, 0, w(), h()));
			kontinentList.get(i).render(g, w() / 7 * i, h() - rec, w() / 7, rec);
		}
		if (selectedKontinent != null)
			selectedKontinent.render(g, w() / 7, h() - rec, w() / 7, rec);

		PointerInfo a = MouseInfo.getPointerInfo();
		Point b = a.getLocation();
//		 + 16 * 2, + 24 * 2 + 15
		int mx = (int) b.getX();// + Game.frame.getLocationOnScreen().x;
		int my = (int) b.getY();// + Game.frame.getLocationOnScreen().y;

		if (selectedKontinent != null && draggingKontinent) {
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
//		System.out.println(id);
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
			if (!draggingKontinent) {// if (selectedKontinent == null) {
				selectedKontinent = null;
				for (Kontinent k : kontinentList) {
					System.out.println(k.name + " => " + k.rect.x + "|" + k.rect.y + " - " + k.size);
				}
				checkAnswere();
			}
			break;

		default:
			break;
		}

	}

	public void checkKontinentBox(Point p, boolean selectMode) {
		if (selectMode) {
			for (Kontinent k : kontinentList) {
				if (k.boxIsTouched(p.x, p.y) && getPixelInImage(new Point(p.x - k.rect.x, p.y - k.rect.y), k.image)) {
					// && getPixelInImage(new Point(p.x - k.p.x, p.y - k.p.y), k.image)
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
//			selectedKontinent = null;
			draggingKontinent = false;
		}
	}

	private boolean getPixelInImage(Point p, BufferedImage image) {
		int clr = image.getRGB(p.x, p.y);
//		int alpha = (clr & 0xff) >> 24;
		int red = (clr & 0x00ff0000) >> 16;
		int green = (clr & 0x0000ff00) >> 8;
		int blue = clr & 0x000000ff;

//		System.out.println("RGB Color: " + p.x + "|" + p.y + " -> " + alpha + "-" + red + "-" + green + "-" + blue);
		if (red != 0 || green != 0 || blue != 0)
			return true;
		else
			return false;
	}

//	private Color getColorAt(Point p) {
//		Rectangle rect = frame.getContentPane().getBounds();
//		BufferedImage img = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_ARGB);
//		frame.getContentPane().paintAll(img.createGraphics());
//		return new Color(img.getRGB(p.x, p.y), true);
//	}

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
		for (Kontinent k : kontinentList) {
			if (getSolution(k.name, new Point(k.rect.x, k.rect.y), k.size) == false) {
				end = "Verloren! ->" + k.name;
				return;
			}
			end = "Gewonnen!";

		}

	}

//TODO NEU!!!

	private boolean getSolution(String name, Point p, float s) { // kontinent
		Point point = new Point(0, 0);
		Float cScale = 0.0f;
		switch (name) { // *1,047559
		case "australien":
			point = new Point(1053, 391);
			cScale = 0.42f;
			break;
		case "südamerika":
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
//nordamerika - 268|21 - 0.78017837
//südamerika - 325|341 - 0.5601786
//europa - 662|65 - 0.4201786
//asien - 765|62 - 0.7601784
//afrika - 598|251 - 0.5601786
//australien - 1057|398 - 0.4201786
//antarktis - 416|351 - 1.260178
