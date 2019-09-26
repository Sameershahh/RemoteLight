package de.lars.remotelightclient.ui.comps;

import java.awt.Image;

import javax.swing.JPanel;

import de.lars.remotelightclient.ui.Style;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JLabel;

public class ImagePanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5453343680456337041L;
	private Image img;
	private JLabel lbl;
	
	public ImagePanel(Image img, Dimension size) {
		this(img, "", size);
	}

	public ImagePanel(Image img, String text, Dimension size) {
		this.img = img;
		setPreferredSize(size);
		setSize(size);
		setMinimumSize(size);
		setBackground(Color.BLACK);
		setLayout(new BorderLayout(0, 0));
		
		lbl = new JLabel(text);
		lbl.setHorizontalAlignment(JLabel.CENTER);
		lbl.setFont(Style.getFontBold(14));
		lbl.setForeground(Style.accent);
		add(lbl, BorderLayout.CENTER);
		
	}
	
	public JLabel getLabel() {
		return lbl;
	}
	
	public void setImage(Image img) {
		this.img = img;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(this.resize(img, getWidth(), getHeight()), 0, 0, null);
		repaint();
	}
	
	private Image resize(Image im, int width, int height) {
		return im.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	}

}
