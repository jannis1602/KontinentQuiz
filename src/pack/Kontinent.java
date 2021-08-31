package pack;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Kontinent {

	String name; // TODO private
	BufferedImage image, originalImage;
	private Rectangle box;
	public float size = 1f; // scale
	public Point p;
	private int dx = 0, dy = 0;

	public Kontinent(String name, BufferedImage image) {
		this.name = name;
		this.image = image;
		this.originalImage = image;
	}

	public void select(int x, int y) {
		dx = x - p.x;
		dy = y - p.y;
	}

	public void mouse(int x, int y) {
		p.x = x - dx;
		p.y = y - dy;
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

	public void render(Graphics g, int x, int y, int w, int h) {
		image = scale(originalImage, (int) (originalImage.getWidth() * size), (int) (originalImage.getHeight() * size));
// TODO scale - originalImage.width...

		box = new Rectangle(p.x, p.y, (int) (image.getWidth()), (int) (image.getHeight()));

//		g.setColor(Color.RED);
//		g.drawRect(p.x, p.y, (int) (box.width), (int) (box.height));
		g.drawImage(image, p.x, p.y, image.getWidth(), image.getHeight(), null);

		// Debug
//		g.setColor(Color.BLUE);
//		for (int yy = 0; yy < image.getHeight(); yy = yy + 10)
//			for (int xx = 0; xx < image.getWidth(); xx = xx + 10) {
//				if (getPI(new Point(xx, yy)))
//					g.drawRect(p.x + xx, p.y + yy, 2, 2);
//			}
	}

	private boolean getPI(Point p) {
		try {
			int clr = image.getRGB(p.x, p.y);
			int red = (clr & 0x00ff0000) >> 16;
			int green = (clr & 0x0000ff00) >> 8;
			int blue = clr & 0x000000ff;
			if (red != 0 || green != 0 || blue != 0)
				return true;
			else
				return false;
		} catch (Exception e) {
			return false;
		}
	}

//	public void renderBox(Graphics g, int x, int y, int w, int h) {
//		box = new Rectangle(x, y, w, h);
//		g.drawRect(x, y, w, h);
//		g.drawImage(image, x, y, w, h, null);
//	}

	public boolean boxIsTouched(int x, int y) {
		if (x > box.x && x < box.x + box.width && y > box.y && y < box.y + box.height)
			return true;
		return false;
	}

}
