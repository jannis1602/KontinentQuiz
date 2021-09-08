package pack;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Btn {

	private String text;
	private BufferedImage image;
	private Rectangle rect; // scaled rect
	private Rectangle oRect; // original rect
	private String ID;

// TODO: create image once -> render image
	public Btn(String text, String ID, Rectangle rect) {
		this.text = text;
		this.ID = ID;
		this.rect = rect;
		this.oRect = rect;
	}

	public Btn(BufferedImage image, String ID, Rectangle rect) {
		this.image = image;
		this.ID = ID;
		this.rect = rect;
		this.oRect = rect;

	}

	public void render(Graphics g, int w, int h, float scale) {
		g.setColor(Color.LIGHT_GRAY);
		rect = new Rectangle((int) (w - oRect.x * scale), (int) (oRect.y * scale), (int) (oRect.width * scale),
				(int) (oRect.height * scale));
		g.fillRect(rect.x, rect.y, rect.width, rect.height);
		g.setColor(Color.GRAY);
		g.fillRect(rect.x + rect.width / 20, rect.y + rect.width / 20, rect.width - rect.width / 20 * 2,
				rect.height - rect.width / 20 * 2);
		if (image != null) {
			g.drawImage(image, rect.x + rect.width / 20 + 4, rect.y + rect.width / 20 + 4,
					rect.width - rect.width / 20 * 2 - 8, rect.height - rect.width / 20 * 2 - 8, null);
		}
		if (text != null) {
			g.setColor(Color.BLACK);
			drawCenteredString(g, text, rect, new Font("ROBOTO", Font.PLAIN, 20));
		}
	}

	public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
		FontMetrics metrics = g.getFontMetrics(font);
		int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
		int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
		g.setFont(font);
		g.drawString(text, x, y);
	}

	public String checkBox(Point p) {
		if (p.x > rect.x && p.x < rect.x + rect.width && p.y > rect.y && p.y < rect.y + rect.height)
			return ID;
		else
			return null;
	}

}
