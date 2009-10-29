package jeliot.annotation;

import java.awt.*; 
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
public class InfoPanel extends JPanel {
		 
        public Component first;
        protected SpinWidget spin;
        public Component second;

        public static final int SPIN_WIDGET_HEIGHT = 14;

        public InfoPanel (Component top, Component bottom) {
            first = top;
            spin = new SpinWidget( );
            second = bottom;
            myLayout( );
        }

        protected void myLayout( ) {
            setLayout (new BoxLayout (this, BoxLayout.Y_AXIS));
            add (first);
            add (spin);
            add (second);
            controlVisibility( );
        }

         protected void controlVisibility( ) {
             if ((second == null) ||
                (spin == null))
                 return;
             second.setVisible (spin.isOpen( ));
             revalidate( );
             if (isShowing( )) {
                 Container ancestor = getTopLevelAncestor( ); 
                 if ((ancestor != null) && (ancestor instanceof Window))        
                     ((Window) ancestor).pack( );
             repaint( );
             }
         }
			
         public void showSecond (boolean b) {
             spin.setOpen (b);
         }

          public boolean isSecondShowing ( ) {
              return spin.isOpen( );
          }
          
  // 
      public class SpinWidget extends JPanel{
      boolean open;
      Dimension mySize = new Dimension (SPIN_WIDGET_HEIGHT,
                                        SPIN_WIDGET_HEIGHT);
      final int HALF_HEIGHT = SPIN_WIDGET_HEIGHT / 2;
      
      //coordinate when the triangle is open
      int[] openXPoints = { 1, HALF_HEIGHT, SPIN_WIDGET_HEIGHT-1};
      int[] openYPoints = { HALF_HEIGHT, SPIN_WIDGET_HEIGHT-1, HALF_HEIGHT};
     
      //coordinate when the triangle is closed
      int[] closedXPoints = { 1, 1, HALF_HEIGHT};
      int[] closedYPoints = { 1, SPIN_WIDGET_HEIGHT-1, HALF_HEIGHT };
     
      Polygon openTriangle =
          new Polygon (openXPoints, openYPoints, 3);
      Polygon closedTriangle =
          new Polygon (closedXPoints, closedYPoints, 3);

      public SpinWidget( ) {
          setOpen (false);
          addMouseListener (new MouseAdapter( ) {

                  public void mouseClicked (MouseEvent e) {
                      handleClick( );
                  }
              });
      }

      public void handleClick( ) {
          setOpen (! isOpen( ));
      }

      public boolean isOpen( ) {
          return open;
      }

      public void setOpen (boolean o) {
          open = o;
          controlVisibility( );
      }

      public Dimension getMinimumSize( ) { return mySize; }
      public Dimension getPreferredSize( ) { return mySize; }

      //set the triangle
      public void paint (Graphics g) {    
          if (isOpen( )) 
              g.fillPolygon (openTriangle); 
          else 
              g.fillPolygon (closedTriangle); 
      } 
}

}


