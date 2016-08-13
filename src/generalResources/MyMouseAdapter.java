package generalResources;

import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

/**
 * Class Code almost entirely from: http://stackoverflow.com/questions/3804361/how-to-enable-drag-and-drop-inside-jlist,
 * answer by Grains
 * @author Grains
 *
 */
public class MyMouseAdapter<T> extends MouseInputAdapter {
    private boolean mouseDragging = false;
    private int dragSourceIndex;
    
    //Addition to pass list and listModel
    private JList<T> myList;
    private DefaultListModel<T> myListModel;
    
    public MyMouseAdapter(JList<T> list, DefaultListModel<T> model) {
		myList = list;
		myListModel = model;
	}
    //End addition
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            dragSourceIndex = myList.getSelectedIndex();
            mouseDragging = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseDragging = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (mouseDragging) {
            int currentIndex = myList.locationToIndex(e.getPoint());
            if (currentIndex != dragSourceIndex) {
                int dragTargetIndex = myList.getSelectedIndex();
                T dragElement = myListModel.get(dragSourceIndex);
                myListModel.remove(dragSourceIndex);
                myListModel.add(dragTargetIndex, dragElement);
                dragSourceIndex = currentIndex;
            }
        }
    }
}