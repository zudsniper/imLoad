package cc.holstr.imLoad.gui.model;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.imgscalr.Scalr;

public class PicturePanel extends JPanel{

    /**
	 * 
	 */
	private static final long serialVersionUID = -2501708533540877918L;
	
	private BufferedImage image;
    private int w,h;
    public PicturePanel(BufferedImage img){
    	image = img;
        w = image.getWidth();
        h = image.getHeight();
    }

    public Dimension getPreferredSize() {
        return new Dimension(w,h);
    }
    //this will draw the image
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(Scalr.resize(image,w,h),0,0,this);
    }
}