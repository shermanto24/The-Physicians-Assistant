//Stefani Hermanto

import java.awt.Graphics;
import java.awt.Color;

public class LifeEssence
{
    //declaring the instance variables
    private int x, y, vx, diam;
    private Color col;
    private boolean collected;
    
    //implementing the default constructor
    public LifeEssence()
    {
        x = 1000;
        y = 300;
        vx = 5;
        diam = 30;
        col = Color.YELLOW;
        collected = false;
    }
    
    //implementing the toString method
    public String toString()
    {
        return "location: (" + x + ", " + y + ")";
    }
    
    //implementing an accessor method
    public boolean wasCollected()
    {
        return collected;
    }
    
    //implementing the mutator methods
    public void setY(int y2)
    {
        y = y2;
    }
    
    public void setSpeed(int s)
    {
        vx = s;
    }
    
    //implementing the drawSelf method
    public void drawSelf(Graphics g)
    {
        g.setColor(col);
        g.fillOval(x, y, diam, diam);
    }
    
    //implementing the act method
    public void act()
    {
        x-=vx;
    }
    
    //implementing the handleCollision method
    public void handleCollision(User u)
    {
        int nextX = x - vx;
        int uX = u.getX();
        int uY = u.getY();
        //x and y always represent top left, both circle & rectangle
        if(nextX >= uX && nextX <= uX + u.getWidth()/4 && y >= uY && y <= uY + u.getHeight()) //if the orb has collided with the user
        {
            collected = true;
            u.collect();
        }
        else if(nextX <= -diam) //if the orb has reached the end of the screen
            collected = true; //still make collected true to allow for it to be removed
    }
}