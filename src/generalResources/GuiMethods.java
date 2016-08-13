package generalResources;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicScrollBarUI;

import main.ShokuhinMain;

public class GuiMethods {
	
	public static JLabel getStyledJLabel(int fontSize){
		JLabel label = new JLabel();
		label.setFont(new Font(ShokuhinMain.themeFontName, Font.BOLD, fontSize));
		label.setForeground(ShokuhinMain.FIFTY_SHADES_OF_WHITE);
		label.setOpaque(false);
		return label;
	}

	public static JButton getStyledJButton(String title, int fontSize){
		JButton c = new JButton(title);
		c.setOpaque(false);
		c.setFont(new Font(ShokuhinMain.themeFontName, Font.BOLD, fontSize));
		c.setForeground(ShokuhinMain.FIFTY_SHADES_OF_WHITE);
		c.setContentAreaFilled(false);
		c.setBorderPainted(true);
		c.addMouseListener(new HoverMouseListener());
		
		return c;
	}

	public static JPanel getStyledJPanel(){
		JPanel panel = new JPanel(){
			
			private static final long serialVersionUID = 6309736538686170226L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(ShokuhinMain.themeColour);
				g.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
			}
		};
		
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		return panel;
	}
	
	public static JScrollPane getStyledJScrollPane(){
		JScrollPane pane = new JScrollPane(){
			private static final long serialVersionUID = -4823426376560198438L;

			@Override
			public void setBorder(Border border) {}
		};
		
		//Override the Vertical Scrollbar, to produce a custom scrollbar
		pane.getVerticalScrollBar().setUI(new BasicScrollBarUI(){
			//As per http://stackoverflow.com/questions/23037433/changing-the-thumb-color-and-background-color-of-a-jscrollpane
			protected void configureScrollBarColors(){
				thumbColor = Color.BLACK;
				trackColor = ShokuhinMain.themeColour;
				
			};
			//As per http://stackoverflow.com/questions/7633354/how-to-hide-the-arrow-buttons-in-a-jscrollbar
			@Override
			protected JButton createIncreaseButton(int orientation) {
				JButton b = new JButton();
				b.setPreferredSize(new Dimension(0, 0));
				return b;
			}
			protected JButton createDecreaseButton(int orientation) {
				JButton b = new JButton();
				b.setPreferredSize(new Dimension(0, 0));
				return b;
			}
		});
			
			pane.getHorizontalScrollBar().setUI(new BasicScrollBarUI(){
				//As per http://stackoverflow.com/questions/23037433/changing-the-thumb-color-and-background-color-of-a-jscrollpane
				protected void configureScrollBarColors(){
					thumbColor = Color.BLACK;
					trackColor = ShokuhinMain.themeColour;
					
				}
				//As per http://stackoverflow.com/questions/7633354/how-to-hide-the-arrow-buttons-in-a-jscrollbar
				@Override
				protected JButton createIncreaseButton(int orientation) {
					JButton b = new JButton();
					b.setPreferredSize(new Dimension(0, 0));
					return b;
				}
				protected JButton createDecreaseButton(int orientation) {
					JButton b = new JButton();
					b.setPreferredSize(new Dimension(0, 0));
					return b;
				}
			});
			
			return pane;
	}
}


/**
 * Helper Class to provide a white-black mouse on-off feature to any button with this listener
 * @author Shaylen Pastakia
 *
 */
class HoverMouseListener implements MouseListener {
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {
		((JButton)e.getSource()).setForeground(ShokuhinMain.FIFTY_SHADES_OF_WHITE);
	}	
	@Override
	public void mouseEntered(MouseEvent e) {
		((JButton)e.getSource()).setForeground(ShokuhinMain.ANY_COLOUR_SO_LONG_AS_IT_IS_BLACK);
	}
	@Override
	public void mouseClicked(MouseEvent e) {}
}