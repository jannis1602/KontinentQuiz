package pack;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Btn {

	private String text;
	private BufferedImage image;
	private Rectangle rect;
	private String ID;

	private BufferedImage rImage = null;

	// TODO: create image once -> render image
	public Btn(String text, String ID, Rectangle rect) {
		this.text = text;
		this.ID = ID;
		this.rect = rect;
	}

	public Btn(BufferedImage image, String ID, Rectangle rect) {
		this.image = image;
		this.ID = ID;
		this.rect = rect;
	}

	public void render(Graphics g) { // TODO int factor x / y
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(rect.x, rect.y, rect.width, rect.height);
		g.setColor(Color.GRAY);
		g.fillRect(rect.x + rect.width / 20, rect.y + rect.width / 20, rect.width - rect.width / 20 * 2,
				rect.height - rect.width / 20 * 2);
		if (text != null) {
			g.setColor(Color.BLACK);
			g.setFont(new Font("ROBOTO", Font.PLAIN, 20));
			g.drawString(text, rect.x + rect.width / 10 + 40, rect.y + rect.height / 2);
		}
	}

//	public BufferedImage renderImage(boolean renderNew) {
//		if (!renderNew && rImage != null)
//			return rImage;
//		int width = 1280;
//		int height = 720; // -> 720p
//		BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//		Graphics2D g2d = bImage.createGraphics();
//
//		g2d.setColor(Color.LIGHT_GRAY);
//		g2d.fillRect(rect.x, rect.y, rect.width, rect.height);
////		g2d.setColor(Color.GRAY);
////		g2d.fillRect(rect.x + rect.width / 20, rect.y + rect.width / 20, rect.width - rect.width / 20 * 2,
////				rect.height - rect.width / 20 * 2);
////		if (text != null) {
////			g2d.setColor(Color.BLACK);
////			g2d.setFont(new Font("ROBOTO", Font.PLAIN, 20));
////			g2d.drawString(text, rect.x + rect.width / 10 + 40, rect.y + rect.height / 2);
////		}
//
//		g2d.dispose();
//		rImage = bImage;
//		return bImage;
//	}
//
//	public void renderBtn(Graphics g, Float scale) {
////		System.out.println("BtnScale: " + scale);
////		System.out.println((int) (rect.x * scale) + " - " + (int) (rect.y * scale) + " - " + (int) (rect.width * scale)
////				+ " - " + (int) (rect.height * scale));
//		g.drawImage(renderImage(false), (int) (rect.x * scale), (int) (rect.y * scale), (int) (rect.width * scale),
//				(int) (rect.height * scale), null);
//
//	}

	public String checkBox(Point p) {
		if (p.x > rect.x && p.x < rect.x + rect.width && p.y > rect.y && p.y < rect.y + rect.height)
			return ID;
		else
			return null;

	}

//	public void triggerEvent(String id) {
//
//		switch (id) {
//		case "rotateBtn":
//			
//			break;
//
//		default:
//			break;
//		}
//
//	}

}
