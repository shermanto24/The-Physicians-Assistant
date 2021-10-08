//Stefani Hermanto

import java.awt.Color; 
import java.awt.Dimension; 
import java.awt.Graphics; 
import java.awt.event.KeyEvent; 
import java.awt.event.KeyListener; 
import java.awt.event.MouseEvent; 
import java.awt.event.MouseListener; 
import java.awt.event.MouseMotionListener; 
import javax.swing.JComponent; 
import javax.swing.JFrame;
import java.awt.Font; 
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class CollectorDriver extends JComponent implements KeyListener, MouseListener, MouseMotionListener
{
    //instance variables 
    private int WIDTH, HEIGHT, stage, speed, spawnTime, quota;
    private double dialogue;
    private String name;
    private User user;
    private long start, now, future, cdStart;
    private ArrayList<LifeEssence> LEs;
    private ArrayList<Obstacle> obstacles;
    private boolean crawling, inStory, countdown, btwnStages;
    private boolean[] strikes;
    private JButton tut, stage1, stage2, stage3, stage4, fullDay;

    //Default Constructor 
    public CollectorDriver()
    {
        //initializing instance variables 
        WIDTH = 1000;
        HEIGHT = 500;
        stage = 0; //main menu
        speed = 5; //the speed at which the LEs and obstacles move. 5 is for easy mode
        spawnTime = 750; //the time in ms between each spawning of a LE/obstacle, will vary depending on the stage
        quota = 25; //the quota of LEs you need to win/go on to the next stage, easy = 25
        strikes = new boolean[3];
        dialogue = -1;
        name = "";
        user = new User();
        start = System.currentTimeMillis();
        now = System.currentTimeMillis();
        future = -1;
        cdStart = -1;
        LEs = new ArrayList<LifeEssence>();
        obstacles = new ArrayList<Obstacle>();
        crawling = false;
        inStory = false;
        countdown = false;
        btwnStages = false;
        createButtons(); //initializes the buttons!

        //Setting up the GUI 
        JFrame gui = new JFrame(); //This makes the gui box 
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Makes sure program can close 
        gui.setTitle("Physician's Assistant"); //This is the title of the game, you can change it 
        gui.setPreferredSize(new Dimension(WIDTH + 5, HEIGHT + 30)); //Setting the size for gui 
        gui.setResizable(false); //Makes it so the gui cant be resized 
        gui.getContentPane().add(this); //Adding this class to the gui 
        
        /*If after you finish everything, you can declare your buttons or other things 
         *at this spot. AFTER gui.getContentPane().add(this) and BEFORE gui.pack(); 
         */
        
        gui.pack(); //Packs everything together 
        gui.setLocationRelativeTo(null); //Makes so the gui opens in the center of screen 
        gui.setVisible(true); //Makes the gui visible 
        gui.addKeyListener(this);//stating that this object will listen to the keyboard 
        gui.addMouseListener(this); //stating that this object will listen to the Mouse 
        gui.addMouseMotionListener(this); //stating that this object will acknowledge when the Mouse moves 
    }

    //This method will acknowledge user input 
    public void keyPressed(KeyEvent e) 
    {
        if(stage != 0 && !btwnStages && !countdown)
        {
            //getting the key pressed
            int key = e.getKeyCode();

            //moving the user according to the key
            if(key == 67 && !user.shouldJump()) //c & user is not jumping
            {
                user.crawl();
                crawling = true;
            }
            else if(key == 32 && user.jumpDone() && !crawling) //space & user has finished jumping & user is not crawling
            {
                user.setJump(true); //allows the jump method to be called in loop
                user.resetStep(); 
            }
        }
    }

    //All your UI drawing goes in here 
    public void paintComponent(Graphics g) 
    {
        //drawing a black rectangle to be the background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        if(!btwnStages)
        {
            if(stage == 0)
            {
                //drawing the title
                Font f1 = new Font("Arial", Font.BOLD, 50);
                g.setFont(f1);
                g.setColor(Color.YELLOW);
                g.drawString("Physician's Assistant", 75, HEIGHT/2 - 100);

                //drawing my name
                Font f2 = new Font("Arial", Font.PLAIN, 25);
                g.setFont(f2);
                g.setColor(Color.WHITE);
                g.drawString("By Stefani Hermanto", 200, HEIGHT/2 - 60);  
            }
            else if(countdown)
            {
                //declaring and instantiating fonts for the countdown
                Font f = new Font("Arial", Font.PLAIN, 150);
                g.setFont(f);
                g.setColor(Color.WHITE);

                //drawing different strings based on the ms that passed since cdStart
                if(now > cdStart + 4000)
                    countdown = false;
                else if(now > cdStart + 3000)
                {
                    g.setColor(Color.YELLOW);
                    g.drawString("START!", WIDTH/2 - 275, HEIGHT/2 + 35);
                }
                else if(now > cdStart + 2000)
                    g.drawString("1", WIDTH/2 - 35, HEIGHT/2 + 40);
                else if(now > cdStart + 1000)
                    g.drawString("2", WIDTH/2 - 35, HEIGHT/2 + 40);
                else if(now > cdStart)
                    g.drawString("3", WIDTH/2 - 35, HEIGHT/2 + 40);

            }
            else
            {
                //drawing the quota at the top right of the screen
                Font f1 = new Font("Arial", Font.ITALIC, 50);
                g.setFont(f1);
                g.setColor(Color.YELLOW);

                int numLEs = user.getNumOrbs();
                if(numLEs < 10)
                    g.drawString("" + numLEs + "/" + quota, WIDTH - 110, 50);
                else
                    g.drawString("" + numLEs + "/" + quota, WIDTH - 137, 50);

                //drawing the life essence orbs
                for(LifeEssence curr : LEs)
                    curr.drawSelf(g);

                //drawing the obstacles
                for(Obstacle curr : obstacles)
                    curr.drawSelf(g);

                //drawing the user
                user.drawSelf(g);
            }
        }
    }

    public void loop() 
    {
        now = System.currentTimeMillis() - start;

        if(user.getLives() == 0) //user is dead, f
        {
            JOptionPane.showMessageDialog(null, "You lost all your lives so ur kinda dead ):", "Oh no!", 0);
            dialogue = -1;
            btwnStages = false;
            resetScreen();
            returnToMenu();
        }
        else if(inStory && btwnStages) //bewteen stages aka in story mode
        {
            //displaying dialogue based on where the player is in the story
            if(dialogue == 0.1) //before stage 1
            {
                name = JOptionPane.showInputDialog("Enter your name.");
                JOptionPane.showMessageDialog(null, "Where... am I?", "You wake up in an unfamiliar setting...", 1);
                JOptionPane.showMessageDialog(null, "Oh... that's right... I work for the Royal Physician. How could I\nhave forgotten? OH NO! Am I late for work?", "You remember.", 1);
                JOptionPane.showMessageDialog(null, "I got ready in a hurry. I hope I didn't forget anything...\n\nWait... is that a note on my desk?", "Hmm....", 1);
                JOptionPane.showMessageDialog(null, "Dear " + name + ",\n\nI hope you are well rested. The king is in terrible condition, so I am\ncurrently attending to his side. I will be back in the afternoon with a report,\nbut for now, please gather 25 life essence orbs for the king's medicine.\nYou can find them in the enchanted forest and I have left a special bag in\nthe living room to help you collect them, but beware! Tricky obstacles will\ncome in your way. For that reason, I have also supplied you with three\nhealth potions. Use them wisely.\n\n                                                                                                  The Royal Physician", "The note reads:", 1);
                JOptionPane.showMessageDialog(null, "The enchanted forest, huh... My head hurts for some reason,\nbut I should hurry to complete this as soon as possible.", "You come to a decision.", 1);
                stage = 1;
                nextStage();
                dialogue = 1.1;
            }
            else if(dialogue == 1.1) //between stages 1 and 2
            {
                JOptionPane.showMessageDialog(null, "The Royal Physician enters. She is an elderly woman\nand cannot walk without a cane.", "Narrator:", 1);
                String[] strs1 = {"Good afternoon.", "How is the king?"};
                
                int c1 = JOptionPane.showOptionDialog(null, "You're back.", "Royal Physician:", 0, 1, null, strs1, null);
                if(c1 == 1)
                    JOptionPane.showMessageDialog(null, "Barely alive, I'm afraid, but...", "Royal Physician:", 2);
                
                JOptionPane.showMessageDialog(null, "I see you've gotten what I asked for. Splendid. The king\nshould be fine with the medicine we'll make from this.", "Royal Physician:", 1);
                JOptionPane.showMessageDialog(null, "We need more to fully cure him, but how about you take a break for now?\nWhen you're ready, come back to me and I'll give you your task.", "Royal Physician:", 3);
                
                String[] strs2 = {"Go for a walk", "Clean the house", "Return to RP"};
                int c2 = JOptionPane.showOptionDialog(null, "What would you like to do, " + name + "?", "Narrator:", 0, 3, null, strs2, null);
                
                if(c2 == 0) //walk
                {
                    JOptionPane.showMessageDialog(null, "It's so nice out today.", "You go for a walk.", 1);
                }
                else if(c2 == 1) //clean
                {
                    JOptionPane.showMessageDialog(null, "Huh? A bottle of poison?\n\n... She's probably studying it to find an\nantidote. I shouldn't jump to conclusions.\n\nMy head hurts...", "You clean the house.", 2);
                    strikes[0] = true;
                }
                
                JOptionPane.showMessageDialog(null, "I'll go back to the Physician now.", "You decide to return.", 1);
                JOptionPane.showMessageDialog(null, "Well, that was fast. I guess I'll give you the instructions now.\n\nThis time, I need you to collect 50 life essence orbs. I will give you\nthree more health potions. Come back before it gets dark.\n\nGood luck! :)", "Royal Physician:", 1);
                stage = 2;
                nextStage();
                dialogue = 2.1;
            }
            else if(dialogue == 2.1) //between stages 2 and 3
            {
                JOptionPane.showMessageDialog(null, "I'm back! ... Miss? Are you here?", "You return.", 3);
                String[] strs3 = {"lol don't mind if I do"};
                JOptionPane.showOptionDialog(null, "Just a second! Help yourself to some berries.\nThey're freshly picked!", "Royal Physician:", 0, 1, null, strs3, null);
                
                String[] strs4 = {"nom nom", "What ARE these?"};
                int c = JOptionPane.showOptionDialog(null, "They're good.", "You eat the berries.", 0, 1, null, strs4, null);
                if(c == 1) //what are these
                {
                    JOptionPane.showMessageDialog(null, "Miss, where'd you get these from? I've never\nseen them growing anywhere near here.", "You decide to ask.", 3);
                    JOptionPane.showMessageDialog(null, "[A pause.]\n\nA farmer was selling them at the town while\nI was sending medicine to the king. Why do\nyou ask?", "Royal Physician:", 1);
                    JOptionPane.showMessageDialog(null, "Just curious.", "You consider.", 1);
                    JOptionPane.showMessageDialog(null, "... How could she have made it to town\nand back in the time I was gone with those legs\nof hers? After all, we live in the mountains...\nWait, why do we live so far from the castle in\nthe first place?\n\nMy head...", "To yourself:", 2);
                    strikes[1] = true;
                }
                
                JOptionPane.showMessageDialog(null, "The Royal Physician enters. She is gripping her cane tightly,\nbut her aura seems more lively.", "Narrator:", 1);
                JOptionPane.showMessageDialog(null, "It looks like you've found more life essence. Good job.\n[She peers into the bag] Unfortunately, it's still not enough!\nCould you run out and get 25 more before the sun sets?\nHurry! The king needs his medicine by tonight.", "Royal Physician:", 1);
                
                stage = 3;
                nextStage();
                dialogue = 3.1;
            }
            else if(dialogue == 3.1) //between stages 3 and 4
            {
                JOptionPane.showMessageDialog(null, "It's late... why are there so many people wandering\naround these parts? That's odd...", "You are walking home when...", 1);
                
                String[] strs5 = {"Avoid them. They look shady.", "See what they're up to."};
                int c = JOptionPane.showOptionDialog(null, "What would you like to do?", "Narrator:", 0, 3, null, strs5, null);
                
                if(c == 1) //see what they're up to
                {
                    JOptionPane.showMessageDialog(null, "It wouldn't hurt to spy on them a bit, right?\n\nNow that I'm closer to them, I can see what they're dressed in all black...\nThey've even got pointy hats... Wait, what?! Witches??", "You've got guts.", 2);
                    JOptionPane.showMessageDialog(null, "Are the preparations ready?", "Witch 1:", 1);
                    JOptionPane.showMessageDialog(null, "Yes. With the poison she administered,\nthe king should be dead soon, and we\nwill have our revolution.", "Witch 2:", 2);
                    JOptionPane.showMessageDialog(null, "What is she talking about? The king should\nbe healing! What poison..? What revolution?!", "You wonder...", 3);
                    if(strikes[0]) //found poison
                    {
                        JOptionPane.showMessageDialog(null, "Wait... poison? No... she wouldn't...", "You realize:", 2);
                        strikes[2] = true;
                    }
                }
                
                JOptionPane.showMessageDialog(null, "I've almost died multiple times today. I don't want to\ntake any chances... Who are those people, anyway?", "You run home.", 2);
                JOptionPane.showMessageDialog(null, "I think I need to work out more...\n\nMiss, I'm back!", "You arrive home, out of breath.", 1);
                JOptionPane.showMessageDialog(null, "Oh, " + name + ", just in time!\n\n[She runs up to you and grabs the bag from your hands.\nYou're taken aback by her sudden vitality. She laughs\nmaniacally, exhilarated.]\n\nNow I can finally be complete!", "Royal Physician:", 1);
                JOptionPane.showMessageDialog(null, "You are face-to-face with her, and suddenly you\nrealize that she is not an elderly woman after all.\nYou are certain that there were wrinkles on her\nface earlier this morning, but now you see none.\n\nSuddenly, she reaches into the bag of orbs, and...\neats them? Her skin begins to glow...\n\nShe transformed into a young woman. No... she\nIS a young woman!", "Narrator:", 2);
               
                if(strikes[0] && strikes[1] && strikes[2]) //found the poison, asked her abt the berries, spied on the witches
                {
                    JOptionPane.showMessageDialog(null, "You begin to piece everything together...", "Narrator:", 2);
                    JOptionPane.showMessageDialog(null, "You were using them for yourself all along... That\nexplains how you got to town in the afternoon!\nYou're a witch... and you're using the life essence\nto make yourself young and healthy again! You\nmust've used poison to make the king's condition\nworse so that you could take over the kingdom\nwith other witches... \n\nYou're... a monster!!", "You are horrified.", 2);
                    JOptionPane.showMessageDialog(null, "Well! I prefer the term Enchantresss... but this was\nunprecedented. I certainly hadn't expected the\nsuccessor to the foolish king to be this smart.", "Enchantress:", 2);
                    JOptionPane.showMessageDialog(null, "Wh... what?", "Your head aches intensely.", 2);
                    JOptionPane.showMessageDialog(null, "Your memories must be returning now... not that that matters\nanyway. You're just a hostage now, hah!\n\n[Her words sink in and you try to make a run for it, but suddenly\nyou freeze in your tracks. She is using magic on you!]\n\nYou might've been able to live peacefully if only you had kept\nquiet. But now I have no choice... I suppose I should keep\nmy promise. Your father surrendered his power as monarch\nto me in exchange for being able to see you again. And now he\nwill... in the afterlife!", "Enchantress:", 2);
                    JOptionPane.showMessageDialog(null, "TRUE END\n\nThe Enchantress wins. Maybe it wouldn't have come\nto this if you hadn't tried to handle everything on your\nown...", "Oh?", 1);
                    
                    user.restore();
                    resetScreen();
                    dialogue = -1;
                    btwnStages = false;
                    returnToMenu();
                }
                else //did not fulfill those 3 conditions
                {
                    JOptionPane.showMessageDialog(null, "Miss... what's going on?", "Your head aches intensely.", 2);
                    String[] strs6 = {"Why would you do that?"};
                    JOptionPane.showOptionDialog(null, "Call me Enchantress. Or, better yet, Queen, because\nthat's what I'll be in the near future! After all, this was\nall part of a setup to put me on the throne and other\nwitches into positions of power. I had no intention of\ncuring the king; in fact, I was poisoning him!", "Enchantress:", 0, 2, null, strs6, null);
                    JOptionPane.showMessageDialog(null, "The king's policies have oppressed us for years on end.\nYou're not one of us, so you wouldn't understand. But\nsince you've helped me return to my prime, I'd feel bad\ndiscarding you. What do you say? Will you join us? Not\nthat there's much of a decision to make... We will rule\nover this kingdom regardless of your choice.", "Enchantress:", 3);
                    
                    String[] strs7 = {"Okay. I'll follow you.", "I need some time to think...", "No way!"};
                    int c2 = JOptionPane.showOptionDialog(null, "What would you like to do?", "Narrator:", 0, 3, null, strs7, null);
                    
                    if(c2 == 0) //follow
                    {
                        JOptionPane.showMessageDialog(null, "I think she's telling the truth... What's the point in resisting?\nIt's not like the king was anyone important to me anyway.\nCome to think of it... I can't remember what I was doing\nbefore I came to work here...", "You consider.", 1);
                        JOptionPane.showMessageDialog(null, "You made the right decision. I'm proud! In return, I'll\ncontinue to provide you with food, a stable job, and\nhousing. Maybe even a high position if you prove to\nbe worthy. Oh, how exciting! I can't wait... all the\nriches, power, and glory will be mine!\n\n[Her peals of laughter echo into the night. You\nwonder if you really did make the right decision...\nbut it's too late to turn back now.]", "Enchantress:", 1);
                        JOptionPane.showMessageDialog(null, "EVIL? END\n\nYou both win, I guess... or did you?", "Hmm...", 3);
                    }
                    else if(c2 == 1) //need some time
                    {
                        JOptionPane.showMessageDialog(null, "Alright, fair enough. But I'm warning you, you won't\nget anything out of turning against me.", "Enchantress", 1);
                        JOptionPane.showMessageDialog(null, "The odds are stacked against me, and maybe she and the\nwitches are justified in taking over this way, but that doesn't\nchange the fact that killing the king is wrong. I need to make my\nown judgment, but... agh, my head! I can't remember anything\nfrom before I worked for the Enchantress... that makes her all the\nmore suspicious. I shouldn't trust her! I need to stop her somehow...", "You think to yourself:", 1);
                        
                        if(strikes[0]) //found the poison
                        {
                            JOptionPane.showMessageDialog(null, "That night, you wait for the Enchantress to go to sleep. Then, you look\nfor the bottle of poison you found earlier. You pour some into a small jar,\nand then the rest down the Enchantress' throat, not to kill her but to\nkeep her out of commission for a while. You use the bag the\nEnchantress gave you to extract life essence from her. After you've\nsecured your belongings, you run to town to find a messenger.", "Narrator:", 1);
                            JOptionPane.showMessageDialog(null, "When you find a messenger, he bows to you?! Apparently you're the king's\nonly child that had gone missing a couple of weeks ago. No way! Then you\nexplain to them the urgency of the situation, and he agrees to send the\nlife essence to the king, but only if you will go with him. So you do.", "Narrator:", 1);
                            JOptionPane.showMessageDialog(null, "The messenger was telling the truth, because you regain your memories on the way\nto the palace. You end up saving the king! Hooray!\n\nYou also become more aware of the societal problems that exist outside the palace,\n and work to resolve them. In the end, you succeed your father and become a great\nmonarch who starts an era of peace and prosperity. Wow, who would've thought?", "Narrator:", 1);
                            JOptionPane.showMessageDialog(null, "GOOD END\n\nYou did it! Good job! :D", "YAY!", 1);
                        }
                        else //did not
                        {
                            JOptionPane.showMessageDialog(null, "But ultimately, you cave. You don't see any way out of this situation,\nand you end up becoming one of the Enchantress' many followers.\nThe rest of your life is quite monotonous.", "Narrator:", 1);
                            JOptionPane.showMessageDialog(null, "BORING END\n\nYou could've made some better decisions...", "lol", 0);
                        }
                    }
                    else //no way!
                    {
                        JOptionPane.showMessageDialog(null, "Huh. How disappointing. And here I thought you could've been more\nuseful to me. Well, no matter. I can always find other servants. But I\ncan't just let you go with that knowledge... who knows what'll happen?\n\n[You stare at her in fear. Her eyes begin to glow.]\n\n[Coldly.] Goodbye, " + name + ".", "Enchantress:", 2);
                        JOptionPane.showMessageDialog(null, "BAD END...\n\nThe Enchantress wins. Better luck next time!", "oof ):", 0);
                    }
                    
                    resetScreen();
                    dialogue = -1;
                    returnToMenu();
                }
            }
        }
        else if(user.getNumOrbs() == quota) //they passed the stage
        {
            user.restore();
            resetScreen();
            if(inStory && stage < 4) //in story mode so there should be dialogue
            {
                JOptionPane.showMessageDialog(null, "You passed! Returning home...", "Whoa!", 1);
                btwnStages = true;
            }
            else if(stage < 3) //not in story mode, playing stages individually
            {
                int choice = JOptionPane.showConfirmDialog(null, "You passed! Would you like to move onto the next stage?", "Wow!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(choice == 0) //yes
                {
                    prepareCountdown();
                    stage++;
                }
                else //no
                {
                    returnToMenu();
                }
            }
            else //passed evening
            {
                JOptionPane.showMessageDialog(null, "You passed! Returning to menu...", "Wow!", 1);
                returnToMenu();
            }
            
        }
        else if(!countdown && stage > 0 && stage < 4) //stage is not 0 (main menu), and stage is not the boss battle (< 4)
        {                                             //aka in game!
            int obsPercent;
            //determining the percent of obstacles that should appear depending on the difficulty
            if(stage == 1)
            {
                obsPercent = 1;
                //no change to speed, it is originally 5
                //no change to spawnTime, it is originally 750
                //no change to quota, it is originally 25
            }
            else if(stage == 2)
            {
                obsPercent = 2;
                speed = 10;
                spawnTime = 600;
                quota = 50;
            }
            else
            {
                obsPercent = 3;
                speed = 20;
                spawnTime = 350; //before was 600
                quota = 25;
            }
            
            //declaring and assigning an int that will determine whether or not an obstacle should spawn
            int rand = (int)(Math.random()*4 + 1); //produces a range of 1-4

            //periodically adding more life essence orbs to LEs
            //as well as more obstacles!
            if(now > future)
            {
                LifeEssence anotherLE = new LifeEssence();
                LEs.add(anotherLE);

                //spawning an obstacle
                if(rand <= obsPercent)
                {
                    Obstacle anotherOb = new Obstacle();
                    obstacles.add(anotherOb);
                    if(anotherOb.getType() == 0) //if anotherOb is a ground type (haha)
                    {
                        anotherLE.setY(175); //then change the y of anotherLE to be above anotherOb so that they don't overlap
                    }
                }
                future = now + spawnTime;
            }

            //calling the act and handleCollision methods on each life essence orb
            for(int i = 0; i < LEs.size(); i++)
            {
                LifeEssence curr = LEs.get(i);
                curr.setSpeed(speed);
                curr.act(); //makes the life essence orb move!
                curr.handleCollision(user);
                if(curr.wasCollected())
                {
                    LEs.remove(i);
                    i--;
                }
            }

            //calling the act and handleCollision methods on each obstacle
            for(int i = 0; i < obstacles.size(); i++)
            {
                Obstacle curr = obstacles.get(i);
                curr.setSpeed(speed);
                curr.act(); //makes the obstacle move!
                curr.handleCollision(user);
                if(curr.shouldReset())
                {
                    obstacles.remove(i);
                    i--;
                }
            }

            //making the user jump
            if(user.shouldJump())
            {
                user.jump();
            }
        }
        
        //Do not write below this 
        repaint();
    }
    
    //implementing the createButtons method
    public void createButtons()
    {
        //drawing the tutorial button
        tut = new JButton();
        this.add(tut);
        tut.setBounds(WIDTH-300, 100, 200, 40);
        //tut.setForeground(Color.WHITE);, can also set font and icon
        tut.setText("Tutorial");
        tut.setFocusable(false);

        //drawing the stage1 button
        stage1 = new JButton();
        this.add(stage1);
        stage1.setBounds(WIDTH-300, 150, 200, 40);
        stage1.setText("Morning (EASY)");
        stage1.setFocusable(false);
        
        //drawing the stage2 button
        stage2 = new JButton();
        this.add(stage2);
        stage2.setBounds(WIDTH-300, 200, 200, 40);
        stage2.setText("Afternoon (NORMAL)");
        stage2.setFocusable(false);
        
        //drawing the stage3 button
        stage3 = new JButton();
        this.add(stage3);
        stage3.setBounds(WIDTH-300, 250, 200, 40);
        stage3.setText("Evening (HARD)");
        stage3.setFocusable(false);
        
        //drawing the fullDay button
        fullDay = new JButton();
        this.add(fullDay);
        fullDay.setBounds(WIDTH-300, 350, 200, 40);
        fullDay.setText("Full Day (STORY)");
        fullDay.setFocusable(false);
        
        //this would've been for the boss battle if i had time to figure out the context + the battle itself
        //drawing the stage4 button
        /*
        stage4 = new JButton();
        this.add(stage4);
        stage4.setBounds(WIDTH-300, 300, 200, 40);
        stage4.setText("Night (BOSS)");
        stage4.setFocusable(false);
        
        //implementing code that runs when the stage4 button is clicked
        
        stage4.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //This runs when the button is clicked
                //starting the game at stage 4
                JOptionPane.showMessageDialog(null, "Oops! This area is under construction!\n\n(SORRY! By the time I finished everything\nelse I didn't have enough time for this...)", "", 0);
            }
        });
        */
        
        //implementing code that runs when the tutorial button is clicked
        tut.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                //This runs when the button is clicked
                //displaying a tutorial message using JOptionPane
                JOptionPane.showMessageDialog(null, "The objective of this game is to collect life essence orbs (yellow)\nand fill the quota that the Physician gives you at each time of day.\nHowever, you must avoid obstacles (red) while doing so! You get\nthree lives in each stage, and each obstacle you hit depletes your\nlives by one, so be careful!\n\nCONTROLS\nSpace = jump\nC = crawl\n\nGood luck! :-)", "Tutorial!", 1);
            }
        });
        
        //implementing code that runs when the stage1 button is clicked
        stage1.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                //This runs when the button is clicked
                //starting the game at stage 1
                stage = 1;
                inStory = false;
                prepareCountdown();
                hideButtons();
            }
        });
        
        //implementing code that runs when the stage2 button is clicked
        stage2.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                //This runs when the button is clicked
                //starting the game at stage 2
                stage = 2;
                inStory = false;
                prepareCountdown();
                hideButtons();
            }
        });
        
        //implementing code that runs when the stage3 button is clicked
        stage3.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //This runs when the button is clicked
                //starting the game at stage 3
                stage = 3;
                inStory = false;
                prepareCountdown();
                hideButtons();
            }
        });
        
        //implementing code that runs when the fullDay button is clicked
        fullDay.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                //This runs when the button is clicked
                //starting the game with dialogue 0.1, which then leads stage to be 1
                btwnStages = true;
                dialogue = 0.1;
                inStory = true;
                hideButtons();
            }
        });
    }

    //implementing the hideButtons method, a helper method for createButtons
    public void hideButtons()
    {
        tut.setVisible(false);
        stage1.setVisible(false);
        stage2.setVisible(false);
        stage3.setVisible(false);
        //stage4.setVisible(false);
        fullDay.setVisible(false);
    }
    
    //implementing the prepareCountdown method
    public void prepareCountdown()
    {
        countdown = true;
        cdStart = System.currentTimeMillis() - start;
    }
    
    //implementing the showButtons method, which is called when the player returns to the menu
    public void showButtons()
    {
        tut.setVisible(true);
        stage1.setVisible(true);
        stage2.setVisible(true);
        stage3.setVisible(true);
        //stage4.setVisible(true);
        fullDay.setVisible(true);
    }
    
    //implementing the returnToMenu method
    public void returnToMenu()
    {
        stage = 0;
        user.restore();
        showButtons();
    }
    
    //implementing the resetScreen method
    public void resetScreen()
    {
        //removing the LEs
        for(int i = 0; i < LEs.size(); i++)
        {
            LEs.remove(i);
            i--;
        }
        
        //removing the obstacles
        for(int i = 0; i < obstacles.size(); i++)
        {
            obstacles.remove(i);
            i--;
        }
    }
    
    //implementing the nextStage method
    public void nextStage()
    {
        user.restore();
        prepareCountdown();
        btwnStages = false;
    }
    
    //These methods are required by the compiler.   
    //You might write code in these methods depending on your goal. 
    public void keyTyped(KeyEvent e) 
    {

    }

    public void keyReleased(KeyEvent e) 
    {
        if(stage != 0 && !btwnStages && !countdown)
        {
            //determining the key released
            int key = e.getKeyCode();
            if(key == 67) //c
            {
                user.crawlRelease();
                crawling = false;
            }
        }
    }

    public void mousePressed(MouseEvent e) 
    {

    }

    public void mouseReleased(MouseEvent e) 
    {

    }

    public void mouseClicked(MouseEvent e) 
    {
        
    }

    public void mouseEntered(MouseEvent e) 
    {

    }

    public void mouseExited(MouseEvent e) 
    {

    }

    public void mouseMoved(MouseEvent e) 
    {

    }

    public void mouseDragged(MouseEvent e) 
    {

    }

    public void start(final int ticks) 
    {
        Thread gameThread = new Thread() 
        {
            public void run() 
            {
                while (true) 
                {
                    loop();
                    try 
                    {
                        Thread.sleep(1000 / ticks);
                    }
                    catch (Exception e) 
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
        gameThread.start();
    }

    public static void main(String[] args) 
    {
        CollectorDriver g = new CollectorDriver();
        g.start(60);
    }
}
