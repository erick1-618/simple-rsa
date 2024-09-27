package br.com.erick.vision;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

public class Button extends JButton implements MouseListener{

	private static final long serialVersionUID = 1L;
	
	public static enum actions { GENERATE_KEYS, EXPORTS_KEYS, IMPORT_PUBLIC_KEY, IMPORT_PRIVATE_KEY, CRYPT, DECRYPT, SHOW_KEYS_INFO }
	
	private actions action;
	private MainWindow mainWindow;
	
	public Button(actions action, MainWindow mw) {
		addMouseListener(this);
		this.action = action;
		this.mainWindow = mw;
		setText(action.toString().replace('_', ' '));
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == 1) {
			mainWindow.performAction(action);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}

	
}
