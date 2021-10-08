//Stefani Hermanto

import java.awt.Color; 
import java.awt.Graphics; 
import java.awt.Font; 

public class User
{
    //declaring the instance variables
    private int x, y, w, h, orbs, lives;
    private int step;
    private boolean jump, jumpDone;
    
    //implementing the default constructor
    public User()
    {
        x = 100; //x and y represent the top left
        y = 250;
        w = 50;
        h = 100;
        orbs = 0;
        lives = 3;
        step = 1;
        jump = false;
        jumpDone = true; //allows user to jump again
    }
    
    //implementing the toString method
    public String toString()
    {
        return "user's location: (" + x + ", " + y + ")";
    }
    
    //implementing the accessor methods
    public int getX()
    {
        return x;
    }
    
    public int getY()
    {
        return y;
    }
    
    public int getWidth()
    {
        return w;
    }
    
    public int getHeight()
    {
        return h;
    }
    
    public int getLives()
    {
        return lives;
    }
    
    public boolean shouldJump()
    {
        return jump;
    }
    
    public boolean jumpDone()
    {
        return jumpDone;
    }
    
    public int getNumOrbs()
    {
        return orbs;
    }
    
    //implementing a mutator method
    public void setJump(boolean j)
    {
        jump = j;
    }
    
    //implementing the drawSelf method
    public void drawSelf(Graphics g)
    {
        //drawing the user-controlled rectangle
        g.setColor(Color.WHITE);
        g.fillRect(x, y, w, h);
        
        //creating a font
        Font f = new Font("Arial", Font.PLAIN, 20);
        g.setFont(f);
        
        //drawing the user's number of lives
        if(lives == 0)
            g.setColor(Color.RED);
        else
            g.setColor(Color.GREEN);
        g.drawString("" + lives, x, y-5);
    }
    
    //implementing the jump method
    public void jump()
    {
        jumpDone = false;
        if(step == 1)
        {
            y-=10;
            if(y < 175)
                step++;
        }
        else if(step == 2)
        {
            y-=5;
            if(y < 150)
                step++;
        }
        else if(step == 3)
        {
            y+=5;
            if(y >= 175)
                step++;
        }
        else if(step == 4)
        {
            y+=10;
            if(y >= 250)
            {
                jump = false;
                jumpDone = true;
                step++;
            }
        }
    }
    
    //implementing a helper method for jump that will reset step
    public void resetStep()
    {
        step = 1;
    }
    
    //implementing the crawl method
    public void crawl()
    {
        w = 100;
        h = 50;
        y = 300;
    }
    
    //implementing the crawlRelease method
    public void crawlRelease()
    {
        w = 50;
        h = 100;
        y = 250;
    }
    
    //implementing the collect method
    public void collect()
    {
        orbs++;
    }
    
    //implementing the hurt method
    public void hurt()
    {
        lives--;
    }
    
    //implementing the restore method, which is called when the user enters the next stage in story mode
    public void restore()
    {
        lives = 3;
        orbs = 0;
    }
}