//Stefani Hermanto

import java.awt.Graphics;
import java.awt.Color;

public class Obstacle
{
    //declaring the instance variables
    private int x, y, w, h, vx, type;
    private Color col;
    private boolean reset;
    
    //implementing the default constructor
    public Obstacle()
    {
        x = 1000;
        vx = 5;
        type = (int)(Math.random()*2);
        col = Color.RED;
        reset = false;
        
        if(type == 0) //ground obstacle
        {
            y = 310;
            w = 40;
            h = 40;
        }
        else //type must be 1, indicating that this obstacle is a flying obstacle
        {
            y = 255;
            w = 40;
            h = 10;
        }
    }
    
    //implementing the accessor methods
    public int getType()
    {
        return type;
    }
    
    public boolean shouldReset()
    {
        return reset;
    }
    
    //implementing a mutator method
    public void setSpeed(int s)
    {
        vx = s;
    }
    
    //implementing the drawSelf methods
    public void drawSelf(Graphics g)
    {
        g.setColor(col);
        g.fillRect(x, y, w, h);
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
        if(nextX >= uX && nextX <= uX + u.getWidth()/4 && y >= uY && y <= uY + u.getHeight()) //if the obstacle has collided with the user
        {
            reset = true;
            u.hurt();
        }
        else if(nextX <= -w) //if the obstacle has reached the end of the screen
            reset = true; //still make reset true to allow for it to be removed
    }
}