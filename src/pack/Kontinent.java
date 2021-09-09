package pack;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
/*
 * Jannis Mattlage 
 */

public class Kontinent {

	public String name;
	public BufferedImage image, originalImage;
	public Rectangle rect;
	public float size = 1f; // scale
	public int dx = 0, dy = 0;

	public Kontinent(String name, BufferedImage image) {
		this.name = name;
		this.image = image;
		this.originalImage = image;
	}

	public void select(int x, int y) {
		dx = x - rect.x;
		dy = y - rect.y;
	}

	public void mouse(int x, int y) {
		rect.x = x - dx;
		rect.y = y - dy;
	}

	public static BufferedImage scaleImage(BufferedImage imageToScale, int dWidth, int dHeight) {
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
		image = scaleImage(originalImage, (int) (originalImage.getWidth() * size),
				(int) (originalImage.getHeight() * size));
		rect = new Rectangle(rect.x, rect.y, (int) (image.getWidth()), (int) (image.getHeight()));
		g.drawImage(image, rect.x, rect.y, image.getWidth(), image.getHeight(), null);
	}

	public boolean boxIsTouched(int x, int y) {
//		System.out.println(name + " - " + originalImage.getWidth() + " - " + originalImage.getHeight());
		if (x > rect.x && x < rect.x + rect.width && y > rect.y && y < rect.y + rect.height)
			return true;
		return false;
	}

	public void moveInObjectScreen(Rectangle rScreen) {
		if (rect == null)
			return;
		if (rect.x + rect.width / 2 < rScreen.x)
			rect.x -= rect.x + rect.width / 2;
		if (rect.x + rect.width / 2 > rScreen.width)
			rect.x += rScreen.width - rect.x - rect.width / 2;
		if (rect.y + rect.height / 2 < rScreen.y)
			rect.y -= rect.y + rect.height / 2;
		if (rect.y + rect.height / 2 > rScreen.height)
			rect.y += rScreen.height - rect.y - rect.height / 2;
	}
}
