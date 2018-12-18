import ServantApp.*;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

public class StartClient {
    static ORB orb;
    static Servant servantObj;
    public int gameStance;
    private int counter = 0;
    public ArrayList<GridElem> points;
    private Point fieldSize = new Point();
    public Color myColor;
    int myNumber;
    public Point[] move = new Point[2];
    
    public class ClientPanel extends JPanel {
    private Point fieldSize = new Point(); //size of the play grid
    
    private Color myColor;
    boolean isGameOver() {
        int count =0;
        Color c = myColor;
        //vertical check
        for (int i =0; i < fieldSize.x +1; i++) {
            for (int j =0; j < fieldSize.y + 1; j++) {
            if (points.get(j*20+i).checked && points.get(j*20+i).elemColor.getRGB() == c.getRGB()) {
            count++;
            }
            else {
                count = 0;
            }
            if(count == 6) return true;
            }
        }
        //horizontal check
        count = 0;
        for (int i = 0; i < fieldSize.y + 1; i++) {
            for (int j =0; j < fieldSize.x + 1; j++) {
            if (points.get(i*20+j).checked && points.get(i*20+j).elemColor.getRGB() == c.getRGB()) {
            count++;
            }
            else {
                count = 0;
            }
            if(count == 6) return true;
            }
        
        //diag check lower top-left-to-bot-right
        for (int rowStart = 0; rowStart  < fieldSize.x - 5; rowStart++) {
            count = 0;
            for (int row = rowStart, col  = 0; row < fieldSize.x+1 && col < fieldSize.y +1; row++, col++ ) {
                if (points.get(col*20 + row).checked && points.get(col*20 + row).elemColor.getRGB() == c.getRGB()) {
                count++;
                }
                else {
                count = 0;
                }
                if(count == 6) return true;
            }
        }
       
        //diag check upper top-left-to-bottom-right
        for(int colStart = 1; colStart < fieldSize.x - 5; colStart++){
        count = 0;
        int row, col;
        for( row = 0, col = colStart; row < fieldSize.y + 1 && col < fieldSize.x  +1; row++, col++ ){
        if (points.get(col*20 + row).checked && points.get(col*20 + row).elemColor.getRGB() == c.getRGB()) {
            count++;
        }
        else {
          count = 0;
        }
        if(count == 6) return true;
        }
        }
        //diag check upper bot-left-to-top-right
        for (int rowStart = fieldSize.y; rowStart > 5; rowStart--) {
            count = 0; 
            int row, col;
            for (row = rowStart, col = 0; row>0 && col < fieldSize.x+1; row--, col++) {
                if (points.get(col*20+row).checked && points.get(col*20+row).elemColor.getRGB() == c.getRGB()) {
                count++;
                }
                else {
                count = 0;
                }
                if (count == 6) return true;
            }
        }
        //diag check lower bot-left-to-top-right
        for (int colStart = 0; colStart < fieldSize.x - 5; colStart++) {
            count = 0; 
            int row, col;
            for (row = fieldSize.y, col = colStart; row > 0 && col < fieldSize.x + 1; row--, col++) {
                if (points.get(col*20+row).checked && points.get(col*20+row).elemColor.getRGB() == c.getRGB()) {
                count++;
                }
                else {
                count = 0;
                }
                if (count == 6) return true;
            }
        }
        
        }
        return false;
    }
        
    public ClientPanel(ArrayList<GridElem> points, Color myColor) {
       // if (myNumber < 2) {
        this.myColor = myColor;
        System.out.append("my number is " + myNumber + "\n");
        fieldSize.x = 19;
        fieldSize.y = 19;
        this.myColor = myColor;
        MouseAdapter mouseHandler;
        mouseHandler = new MouseAdapter() {
                @Override 
                public void mouseClicked(MouseEvent e) {
                   if (gameStance == 1) {
                    Point clicked = e.getPoint();
                    for (GridElem elem : points) {
                        if (Math.sqrt(Math.pow(elem.coord.x+elem.pos.x*elem.length.x - clicked.x, 2)
                                + Math.pow(elem.coord.y+elem.pos.y*elem.length.y - clicked.y, 2) ) <= elem.length.x/2 && (!elem.checked)) {
                        System.out.append("in our point\n");
                        if(!elem.checked) {
                        elem.checked = true;
                        elem.elemColor = myColor;
                        move[counter] = new Point(elem.pos.x, elem.pos.y);
                        counter++;
                        if(counter==2) {
                        counter =0;
                        if (isGameOver()) {
                            gameStance = 3;
                        }
                        else{
                        gameStance = 2;
                        servantObj.addPoints(move[0].x, move[0].y,move[1].x, move[1].y, myNumber);
                        System.out.append("move 0_ :" + move[0].x + "  " + move[0].y + "\n" );
                        System.out.append("move 1_ :" + move[1].x + "  " + move[1].y + "\n" );
                        }
                        }
                        }
                        }
                    }
                repaint();
                    }
                }
                
            };
        new Thread() {
            @Override
            public void run() {
            while(true) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(StartClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                Color c = new Color(0);
                MyStruct A = servantObj.getPoints();
                if (A.g == 0) c = Color.WHITE;
                if (A.g == 1) c = Color.BLACK;
                if (A.g != myNumber && A.g != -1) {
                    System.out.append("my move!\n");
                    if (gameStance != 3) gameStance = 1;
                    points.get(A.x1*20+A.y1).checked = true;
                    points.get(A.x1*20+A.y1).elemColor = c;
                    
                    points.get(A.x2*20+A.y2).checked = true;
                    points.get(A.x2*20+A.y2).elemColor = c;
                    repaint();
                }
               }
            }
            }.start();
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
            //}
      
        }
        @Override
        public Dimension getPreferredSize() {
        return new Dimension(760,760);
        }
        @Override 
        public void invalidate() {
        super.invalidate();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            //super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
                       
            int width = getWidth();
            int height = getHeight();
            
            int lengthX = width/(fieldSize.x + 1);
            int lengthY = height/(fieldSize.y + 1);
            
            int startPosX = lengthX/2;
            int startPosY = lengthY/2;
            
            g2d.setColor(new Color(16762394));
            g2d.fillRect(0,0,getWidth(), getHeight());
            
            if (points.isEmpty()) {
                System.out.append("create new grid\n");
             for (int i=0; i < fieldSize.x + 1; ++i) {
                 for (int j =0; j < fieldSize.y + 1; ++j) {
                     GridElem elem = new GridElem(i,j, startPosX, startPosY, lengthX, lengthY, myColor);
                     points.add(elem);
                    }
                }
            points.get(190).checked = true;
            points.get(190).elemColor = Color.BLACK;
            }
            else {
                for(GridElem elem: points) {
                    elem.update(startPosX, startPosY, lengthX, lengthY);
                }
            }
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            for (GridElem elem : points) {
                 elem.drawElem(g2d);
            }
            g2d.dispose(); 
        }
    }
    
    public StartClient () {
        myNumber = servantObj.getNumber();
        if (myNumber == 0)  {
            gameStance = 1;
            myColor = Color.WHITE;
        }
        if (myNumber == 1) {
            gameStance = 2;
            myColor = Color.BLACK;
        }
          
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                fieldSize.x = 19;
                fieldSize.y = 19;
                points = new ArrayList<>(fieldSize.x * fieldSize.y);   
                JFrame clientFrame = new JFrame("ClientFrame");
                clientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                clientFrame.setLayout(new BorderLayout());
                clientFrame.add(new ClientPanel(points, myColor));
                clientFrame.pack();
                clientFrame.setLocationRelativeTo(null);
                clientFrame.setVisible(true);
            }    
    });     
    }
    public static void main(String[] args) {
        try {
            orb = ORB.init(args, null);
            org.omg.CORBA.Object objRef =   orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            servantObj = (Servant) ServantHelper.narrow(ncRef.resolve_str("ABC"));  
            new StartClient();
        }
        catch (Exception e) {
          System.out.println("Hello Client exception: " + e);
	  e.printStackTrace();
       }
    }  
}
