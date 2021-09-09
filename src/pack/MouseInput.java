package pack;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
/*
 * Jannis Mattlage 
 */

public class MouseInput implements MouseListener, MouseMotionListener, MouseWheelListener {

	private KontinentLocation kontinentLocation;
	private boolean mouseIsPressed = false;

	public MouseInput(KontinentLocation kontinentLocation) {
		this.kontinentLocation = kontinentLocation;
	}

	public int x, y;

	@Override
	public void mouseDragged(MouseEvent e) {
		KontinentLocation.mouseXY = e.getPoint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		KontinentLocation.mouseXY = e.getPoint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		x = e.getX();
		y = e.getY();
		if (!kontinentLocation.checkKontinentBox(new Point(x, y), true))
			for (Btn tempBtn : kontinentLocation.btnList) {
				if (tempBtn.checkBox(new Point(x, y)) != null) {
					mouseIsPressed = true;
					kontinentLocation.triggerBtn(tempBtn.checkBox(new Point(x, y)));
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							while (mouseIsPressed) {
								kontinentLocation.triggerBtn(tempBtn.checkBox(new Point(x, y)));
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					});
					if (tempBtn.getID().contains("rotate") || tempBtn.getID().contains("scale"))
						thread.start();
					return;
				}
			}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseIsPressed = false;
		kontinentLocation.checkKontinentBox(new Point(x, y), false);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		e.getX();
		e.getY();
		kontinentLocation.scroll(e.getScrollAmount() * e.getWheelRotation());
	}

}
