package pack;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Kontinent {

	String name; // TODO private
	BufferedImage image;
	private Rectangle box;
	public float size = 1f; // scale
	public Point p;
	private int dx = 0, dy = 0;

	public Kontinent(String name, BufferedImage image) {
		this.name = name;
		this.image = image;
	}

	public void select(int x, int y) {
		dx = x - p.x;
		dy = y - p.y;
	}

	public void mouse(int x, int y) {
		p.x = x - dx;
		p.y = y - dy;
	}

	public void render(Graphics g, int x, int y, int w, int h) {
//		box = new Rectangle(p.x, p.y, (int) (w * size), (int) (h * size));
		box = new Rectangle(p.x, p.y, (int) (image.getWidth() * size), (int) (image.getHeight() * size));
		g.setColor(Color.RED);
		g.drawRect(p.x, p.y, (int) (w * size), (int) (h * size));
//		g.drawImage(image, p.x, p.y, (int) (w * size), (int) (h * size), null);
		g.drawImage(image, p.x, p.y, (int) (image.getWidth() * size), (int) (image.getHeight() * size), null);

		g.setColor(Color.BLUE);
		for (int yy = 0; yy < image.getHeight() * size; yy = yy + 10)
			for (int xx = 0; xx < image.getWidth() * size; xx = xx + 10) {
				if (getPI(new Point(xx, yy)))
					g.drawRect(p.x + xx, p.y + yy, 2, 2);
			}
	}

//	public void render(Graphics g, int x, int y, int w, int h) {
//		g.drawRect(x, y, w, h);
//		g.drawImage(image, x, y, w, h, null);
//	}

	private boolean getPI(Point p) {
		try {
			int clr = image.getRGB(p.x, p.y);
			int alpha = (clr & 0xff) >> 24;
			int red = (clr & 0x00ff0000) >> 16;
			int green = (clr & 0x0000ff00) >> 8;
			int blue = clr & 0x000000ff;

//			System.out.println("RGB Color: " + p.x + "|" + p.y + " -> " + alpha + "-" + red + "-" + green + "-" + blue);
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
