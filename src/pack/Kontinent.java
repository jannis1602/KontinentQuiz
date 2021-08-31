package pack;

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
		box = new Rectangle(p.x, p.y, (int) (w * size), (int) (h * size));
//		g.drawRect(p.x, p.y, (int) (w * size), (int) (h * size));
		g.drawImage(image, p.x, p.y, (int) (w * size), (int) (h * size), null);
	}

//	public void render(Graphics g, int x, int y, int w, int h) {
//		g.drawRect(x, y, w, h);
//		g.drawImage(image, x, y, w, h, null);
//	}

	public void renderBox(Graphics g, int x, int y, int w, int h) {
		box = new Rectangle(x, y, w, h);
		g.drawRect(x, y, w, h);
		g.drawImage(image, x, y, w, h, null);
	}

	public boolean boxIsTouched(int x, int y) {
		if (x > box.x && x < box.x + box.width && y > box.y && y < box.y + box.height)
			return true;
		return false;
	}

}
