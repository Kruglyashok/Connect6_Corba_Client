/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;

/**
 *
 * @author One
 */
 public class GridElem {
        boolean checked = false;
        Color elemColor;
        Point pos = new Point(); //position of the filed, imaginary
        Point coord = new Point(); //coords on the grid
        Point length = new Point(); //length of lines
        GridElem(int i, int j,int startPosX,int startPosY,int lengthX, int lengthY, Color elemColor) {
            this.elemColor = elemColor;
            pos.x = i;
            pos.y = j;
            coord.x = startPosX;
            coord.y = startPosY;
            length.x = lengthX;
            length.y = lengthY;
        }
        public void update(int startPosX,int startPosY,int lengthX, int lengthY) {
            coord.x = startPosX;
            coord.y = startPosY;
            length.x = lengthX;
            length.y = lengthY;
        }
        public void drawElem(Graphics2D g2d) {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(coord.x + pos.x*length.x,
                        pos.y*length.y,
                        coord.x + pos.x*length.x,
                        pos.y*length.y + length.y);
            g2d.drawLine(pos.x*length.x,
                        coord.y + pos.y*length.y,
                        pos.x*length.x + length.x,
                        coord.y + pos.y*length.y                        
            );
            if (checked) {
                g2d.setColor(elemColor);
                Ellipse2D circle = new Ellipse2D.Float(coord.x + pos.x*length.x - length.x/4,
                                            coord.y + pos.y*length.y - length.y/4,
                                            length.x/2,
                                            length.y/2);
                g2d.fill(circle);
            }
        }
        
    }