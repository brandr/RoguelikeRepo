
//NOTE: remember that you can add containers within containers, allowing GUIs to display a large number of components more easily. (may improve column
//displays.
 
//TODO: current goal: replace each screen with a container, and store these containers in an organized way.
//instead of adding and removing each individual component to and from "frame", just switch containers.

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
//import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;
//import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLEditorKit;
 
public class RogueLikeGui extends JFrame
     
{
	//game screens
	
	static RogueLikeGui frame;
	static Timer screenTimer;
	static Graphics rogueGraphics;
	private static final long serialVersionUID = 1L;
	static final String newline = System.getProperty("line.separator");

	static JTextArea[][] screenList = new JTextArea[20][20];	//might not be necessary.
	
	static Container startScreen=new Container();		//TODO: find a way to have components within these containers, OR just make them all separate classes.
	static Container characterCreationScreen=new Container();
	static Container mainScreen=new Container();
	static Container inventoryScreen=new Container();
	static Container deathScreen=new Container();
	
	//screen components
	
	//character creation screen
	
	JScrollPane scrollPane;			//probably need a more descriptive name for this, along with varying scroll panes
	
	//static JTextArea infoDisplayArea;		//main screen
	
	static JTextArea infoDisplayColumn1;
	static JTextArea infoDisplayColumn2;
	static JTextArea infoDisplayColumn3;
	
	static JTextPane mapDisplayArea;
	static JTextArea actionDisplayArea;
	
	static JTextArea instructionsDisplayArea;
	
	static JTextArea deathScreenArea;		//death screen
	
	static JTextArea inventoryLabel;		//inventory screen
	static JTextArea inventoryDisplayArea;	
	static JTextArea inventoryColumns1;
	static JTextArea inventoryColumns2;
	static JTextArea equipmentDisplayArea;
	
	private static SkillScreen skillScreen;	//skill screen
	
	//screen component properties
	
	static int currentScreenIndex=1; //this index may eventually be unneccessary, but I am not ready to get rid of it yet. (I want to clean up this class all in one sitting.)
	
		//main screen
	public static final Dimension SCREEN_DISPLAY_SIZE=new Dimension(400,650);
	public static final Dimension ACTION_DISPLAY_SIZE=new Dimension(SCREEN_DISPLAY_SIZE.width,60);
	public static final Dimension INFO_DISPLAY_COLUMN_SIZE=new Dimension(SCREEN_DISPLAY_SIZE.width/3,120);
	
		//inventory
	public static final Dimension INVENTORY_COLUMN_SIZE=new Dimension(200,300);
	public static final Dimension EQUIPMENT_DISPLAY_SIZE=new Dimension(SCREEN_DISPLAY_SIZE.width,SCREEN_DISPLAY_SIZE.height);
	
		//instructions
	public static final Dimension INSTRUCTIONS_DISPLAY_SIZE=new Dimension(SCREEN_DISPLAY_SIZE.width,SCREEN_DISPLAY_SIZE.height);
	
		//death
	public static final Dimension DEATH_DISPLAY_SIZE=new Dimension(SCREEN_DISPLAY_SIZE.width,SCREEN_DISPLAY_SIZE.height);
	
	//action listeners (register key presses)

	MainScreenListener mainScreenL = new MainScreenListener();
	InventoryScreenListener inventoryScreenL = new InventoryScreenListener();
	ItemSelectListener itemSelectL = new ItemSelectListener();
	DirectionSelectListener directionSelectL = new DirectionSelectListener();
	SpellSelectListener spellSelectL = new SpellSelectListener();
	YNListener YNL=new YNListener();
	EscapeListener escapeL=new EscapeListener();		//TODO: decide whether or not this should replace inventory screen listener.
	
	public static final int TICK_TIME = 200; //# of milliseconds each frame is shown during an animated event, like an object being thrown.
	
	String itemType="Item";		//this will be used with the item select listener to determine what type of item is valid to select.
	String itemAction="";		//this will set the action to be performed on the item shown in "itemType".
	
	static Death playerDeath;	//an object that handles the player's death
	
	static boolean playerDead=false;	//booleans that are toggled true and false by various game events
	static boolean autoPickUp=true;
	static boolean anyKeyPressed=false;
	
    //game objects declared below
    
    static Dungeon d = new Dungeon();
    
	static Player player1;
	
	public static String currentMessage = "";
     
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
private static void createAndShowGUI() {
    	
        //Create and set up the window.
        frame = new RogueLikeGui("Roguelike");
        ActionListener screenRefresher=new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.repaint();
			}
        };
        screenTimer=new Timer(20, screenRefresher);
        screenTimer.start();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
        //Set up the content pane.
        frame.addComponentsToPane();
         
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
        //not sure this are necessary, but not ready to get rid of them yet.
        screenList[0][0]=deathScreenArea;
        screenList[1][0]=infoDisplayColumn1;
    	screenList[1][2]=actionDisplayArea;
    	screenList[2][0]=inventoryDisplayArea;
    	
    }
    
private static void setStartConditions(){		//set items, monsters, etc. on the level
		
		playerDead=false;
		d.setMap();
		Level firstFloor=d.getLevel(1,1);		
		firstFloor.addPlayer(player1);	
		
		player1.identifyAllItems();
		player1.setPotionColors(d.dungeonPotionColors);	//TODO: make this method and have it set the potion colors to whatever they have been randomized to for this dungeon.
		
		player1.spells=new Spell[100];	//spells must be reset at the beginning of each game.
	    player1.learnSpell(new Spell("Magic Missile", Skill.EVOCATIONS,"missile",5, 0));//temporary for testing.
	    player1.learnSpell(new Spell("Poison Missile", Skill.EVOCATIONS,"missile",5, 1));//temporary for testing.
		
	    player1.gameStarted=true;
		player1.setCurrentLevel(firstFloor);		//the "level" is the floor, and d is the dungeon.
		player1.fov.refreshFOV();
	 
		//player1.setBaseDamage(2);		//TODO: is player's base damage ever used? consider removing this.	
		currentMessage="";	
    }
     
private void startMap() {
    	
		mapDisplayArea.setText(mapDisplay());			//opens the first level of the dungeon on the main screen.
		mapDisplayArea.setFont(new Font("Courier New", Font.PLAIN, 2));
		infoDisplayColumn1.setText(player1.primaryInfoDisplay());	
		infoDisplayColumn2.setText(player1.secondaryInfoDisplay());
		infoDisplayColumn3.setText(player1.statsDisplay());
		playerDeath=new Death();
	}

private void addComponentsToPane() {    
	
		Container contentPane = frame.getContentPane();
        BorderLayout borderLayout = new BorderLayout();
        contentPane.setLayout(borderLayout);
        
       //character creation screen
		
		//main screen
        infoDisplayColumn1 = new JTextArea();
        infoDisplayColumn1.setEditable(false);
        infoDisplayColumn1.setPreferredSize(INFO_DISPLAY_COLUMN_SIZE);
        
        infoDisplayColumn2 = new JTextArea();
        infoDisplayColumn2.setEditable(false);
        infoDisplayColumn2.setPreferredSize(INFO_DISPLAY_COLUMN_SIZE);
        
        infoDisplayColumn3 = new JTextArea();
        infoDisplayColumn3.setEditable(false);
        infoDisplayColumn3.setPreferredSize(INFO_DISPLAY_COLUMN_SIZE);
        
        //mapDisplayArea = new JTextArea();
        mapDisplayArea = new JTextPane();
        mapDisplayArea.setEditorKit(new HTMLEditorKit());
        mapDisplayArea.setEditable(false);
        
        actionDisplayArea = new JTextArea();
        actionDisplayArea.setEditable(false);
        
        mapDisplayArea.addKeyListener(mainScreenL);
        mapDisplayArea.requestFocus();
        
        JScrollPane scrollPane = new JScrollPane(mapDisplayArea);
        
        //inventory screen
       inventoryLabel = new JTextArea("Inventory:");
       inventoryLabel.setEditable(false);
       inventoryLabel.setFont(new Font("Arial Bold", Font.BOLD, 12));
       inventoryLabel.addKeyListener(inventoryScreenL);
       
       inventoryColumns1 = new JTextArea();
       inventoryColumns1.setEditable(false);
       
       inventoryColumns2 = new JTextArea();
       inventoryColumns2.setEditable(false);
       
       equipmentDisplayArea = new JTextArea();
       equipmentDisplayArea.setEditable(false);
       
       //instruction screen
       
       instructionsDisplayArea=new JTextArea();
       instructionsDisplayArea.setEditable(false);
       
       //death screen
		deathScreenArea = new JTextArea();
		deathScreenArea.setEditable(false);
      
       //set all screen attributes
       
       mainScreen.setPreferredSize(SCREEN_DISPLAY_SIZE);
       mainScreen.setVisible(true);
       
       mapDisplayArea.setFont(new Font("Courier New", Font.PLAIN, 10));
       
       actionDisplayArea.setLineWrap(true);
       actionDisplayArea.setWrapStyleWord(true);
       
       mapDisplayArea.setBorder(BorderFactory.createLineBorder(Color.black));
       scrollPane.setBorder(BorderFactory.createLineBorder(Color.black));
       actionDisplayArea.setBorder(BorderFactory.createLineBorder(Color.black));
       
       mainScreen.add(infoDisplayColumn1,BorderLayout.PAGE_START);		//TODO: replace these with gridbag variants (call a separate method)
       mainScreen.add(scrollPane, BorderLayout.CENTER);
       mainScreen.add(actionDisplayArea, BorderLayout.SOUTH);
       
       instructionsDisplayArea.setPreferredSize(INSTRUCTIONS_DISPLAY_SIZE);
       
       deathScreenArea.setPreferredSize(DEATH_DISPLAY_SIZE);		//might be able to set sizes faster using a for-loop, array, custom method, etc.
       
       CharacterCreator.openCharacterCreationScreen();	//TODO: we want a start screen of some kind before the character creation screen.
 
  }
     
	public RogueLikeGui(String name) {
        super(name);
    }
    //do I want a projectile class?
public void createProjectile(Monster thrower, Item thrownItem, char direction, Tile startTile){		//TODO: will want to add magnitude (max distance thrown) to the list of args later
	ProjectileWorker projectileMover=new ProjectileWorker(); 
	projectileMover.setStartConditions(thrower, thrownItem, direction, startTile);
    projectileMover.execute();
}

public void createProjectile(Monster caster, Spell missile, char direction, Tile startTile){	//shoots a  spell in a direction
	ProjectileWorker projectileMover=new ProjectileWorker(); 
	projectileMover.setStartConditions(caster, missile, direction, startTile);
    projectileMover.execute();
}
    
public static void wait (int milliseconds){
        
        long start, end;
        start =  System.currentTimeMillis();
        do
        	end = System.currentTimeMillis();
        while ((end - start) < (milliseconds));
    }

	public static void refreshScreen(){		//refreshes main screen display
		
		if(!SwingUtilities.isEventDispatchThread()){try {
			SwingUtilities.invokeAndWait(new Runnable() {		//invokeAndWait necessary to avoid screen flickering.
				  public void run() {
					  mapDisplayArea.setText(mapDisplay());
					  actionDisplayArea.setText(currentMessage);
				      infoDisplayColumn1.setText(player1.primaryInfoDisplay());	
				      infoDisplayColumn2.setText(player1.secondaryInfoDisplay());
				      infoDisplayColumn3.setText(player1.statsDisplay());
				      actionDisplayArea.setPreferredSize(ACTION_DISPLAY_SIZE);
				  }
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return;
		}
		mapDisplayArea.setText(mapDisplay());
		actionDisplayArea.setText(currentMessage);
        infoDisplayColumn1.setText(player1.primaryInfoDisplay());	
		infoDisplayColumn2.setText(player1.secondaryInfoDisplay());
		infoDisplayColumn3.setText(player1.statsDisplay());
		actionDisplayArea.setPreferredSize(ACTION_DISPLAY_SIZE);	
    }
	
	private static String mapDisplay() {
		Level level=player1.currentLevel;
		String levelDisplay ="<html><p style=\"font-size:9px\" " +
				"face= \"Courier new\",courier,monospace>";
		Tile currentTile;
			
			for (int i = 0; i < level.ySize; i++) {
				for (int j = 0; j < level.xSize; j++) {		
					currentTile=level.layout[j][i];
					//colorCode=currentTile.color;		//only use colorCode if we end up deciding to have different tiles different colors
					//levelDisplay+="<span style=\"color:#"+colorCode+"\">";
					char icon = player1.fov.getTileIcon(j,i);				
					if(icon=='<') //HTML can't display < or >, so these characters must be read with these special cases.
						levelDisplay+="&lt;";
					else if(icon=='>')
						levelDisplay+="&gt;";
					else if(currentTile.monster!=null
						 &&!Tile.isEmptySpaceIcon(icon)){		//if there is a monster in the tile and the tile is visible to the player, use its color. (may use item colors in the future)
						levelDisplay+="<span style=\"color:#"+currentTile.monster.color+"\">"+icon
									+"<span style=\"color:#000000\">";
					}else if(icon==' ')
						levelDisplay+="&nbsp;";
					else
					levelDisplay+=icon;
				}
				
				levelDisplay+=("<br>");
			}
			levelDisplay+="</p></html>";
			return levelDisplay;
	}
	
	public void openMainScreen(){
		scrollPane = new JScrollPane(mapDisplayArea);
		mainScreen.setVisible(true);
		
        mapDisplayArea.setVisible(true);
        actionDisplayArea.setVisible(true);
        infoDisplayColumn1.setVisible(true);
        infoDisplayColumn2.setVisible(true);
        infoDisplayColumn3.setVisible(true);
       
        mapDisplayArea.setBorder(BorderFactory.createLineBorder(Color.black));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.black));
        actionDisplayArea.setBorder(BorderFactory.createLineBorder(Color.black));
        actionDisplayArea.setPreferredSize(ACTION_DISPLAY_SIZE);
        actionDisplayArea.setWrapStyleWord(true);
        
        GridBagLayout gridBagLayout = new GridBagLayout();
        getContentPane().setLayout(gridBagLayout);
   	 	GridBagConstraints c = new GridBagConstraints();

   	 	c.fill=GridBagConstraints.BOTH;
   	 	c.weightx=1.5;
   	 	c.weighty=0.05;
   	 	c.anchor=GridBagConstraints.NORTHWEST;
   	 	c.gridx = 0;
   	 	c.gridy = 0;
   	 	getContentPane().add(infoDisplayColumn1,c);
   	 	c.fill=GridBagConstraints.BOTH;
   	 	c.weightx=1.5;
   	 	c.weighty=0.05;
   	 	c.anchor=GridBagConstraints.NORTH;
   	 	c.gridx = 1;
   	 	c.gridy = 0;
   	 	getContentPane().add(infoDisplayColumn2,c);
   	 	c.fill=GridBagConstraints.BOTH;
   	 	c.weightx=1.5;
   	 	c.weighty=0.05;
   	 	c.anchor=GridBagConstraints.NORTHEAST;
   	 	c.gridx = 2;
   	 	c.gridy = 0;
   	 	getContentPane().add(infoDisplayColumn3,c);
   	 	c.fill=GridBagConstraints.BOTH;
   	 	c.weightx=0.6;
   	 	c.weighty=1.0;
   	 	c.anchor=GridBagConstraints.CENTER;
   	 	c.gridwidth=3;
   	 	c.gridx = 0;
   	 	c.gridy = 2;
   	 	getContentPane().add(scrollPane,c);
   	 	c.fill=GridBagConstraints.BOTH;
   	 	c.weightx=0.5;
   	 	c.weighty=2.0;
   	 	c.anchor=GridBagConstraints.SOUTH;
   	 	c.gridwidth=3;
   	 	c.gridx = 0;
   	 	c.gridy = 3;
   	 	getContentPane().add(actionDisplayArea,c);
   	 	mapDisplayArea.requestFocus();

        currentScreenIndex=1;
        refreshScreen();
	}
	
	private void openInstructions(){
		
		getContentPane().removeKeyListener(mainScreenL);
		
		JScrollPane instructionsPane= new JScrollPane(instructionsDisplayArea);
		instructionsPane.setBorder(BorderFactory.createLineBorder(Color.black));
		getContentPane().add(instructionsPane);
		
		instructionsPane.setSize(INSTRUCTIONS_DISPLAY_SIZE);
		instructionsDisplayArea.setPreferredSize(new Dimension(INSTRUCTIONS_DISPLAY_SIZE.width,INSTRUCTIONS_DISPLAY_SIZE.height+2100));	//TODO: replace the height (and maybe width) with a getter based on the file's length.

		instructionsPane.setWheelScrollingEnabled(true);
		instructionsPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		instructionsDisplayArea.setCaretPosition(0);
		
		instructionsDisplayArea.setWrapStyleWord(true);
		instructionsDisplayArea.setText(TextFileReader.instructionManual());
		instructionsDisplayArea.setCaretPosition(0);
		instructionsDisplayArea.addKeyListener(escapeL);
		instructionsDisplayArea.requestFocusInWindow();
	}
	
	private void openSkillScreen(){
		skillScreen=new SkillScreen(frame,player1);
		getContentPane().removeKeyListener(mainScreenL);
		skillScreen.open();
	}
	
	private void openInventoryScreen(){	
		if(currentScreenIndex!=2){
		BorderLayout borderLayout=new BorderLayout();
		getContentPane().setLayout(borderLayout);
			
		getContentPane().add(inventoryLabel,BorderLayout.PAGE_START);
		getContentPane().add(inventoryColumns1,BorderLayout.CENTER);
		getContentPane().add(inventoryColumns2,BorderLayout.LINE_END);
		getContentPane().add(equipmentDisplayArea,BorderLayout.SOUTH);
		
		inventoryLabel.setVisible(true);
		inventoryColumns1.setVisible(true);	
		inventoryColumns2.setVisible(true);
		equipmentDisplayArea.setVisible(true);
		
		String[][] columns = player1.inventory.columnString(player1,2);
		
		inventoryLabel.setPreferredSize(new Dimension(400,20));
		inventoryLabel.setBorder(BorderFactory.createLineBorder(Color.black));
		inventoryColumns1.setPreferredSize(INVENTORY_COLUMN_SIZE);
		inventoryColumns2.setPreferredSize(INVENTORY_COLUMN_SIZE);
		equipmentDisplayArea.setSize(EQUIPMENT_DISPLAY_SIZE);
		
		inventoryLabel.setText("Inventory:");
		inventoryColumns1.setText(player1.inventory.columnToString(columns[0],player1,0));
		inventoryColumns2.setText(player1.inventory.columnToString(columns[1],player1,1));
		equipmentDisplayArea.setText("     "+player1.showEquipment());
		equipmentDisplayArea.setBorder(BorderFactory.createLineBorder(Color.black));
		
		inventoryLabel.requestFocusInWindow();
		currentScreenIndex=2;
		}
	}
	
	
private static class CharacterCreator{
	
	static JTextField nameEntry=new JTextField(15);
	static String[] races = Race.RACES;	
	static String[] classes = CharacterClass.CLASSES;	//change to +1 if the first item is (Select class)
	
	static JComboBox<String> raceOptions=new JComboBox<String>(races);
	static JComboBox<String> classOptions=new JComboBox<String>(classes);
	
	static JTextField STRfield=new JTextField(2);
	static JTextField DEXfield=new JTextField(2);
	static JTextField FORfield=new JTextField(2);
	static JTextField PERfield=new JTextField(2);
	static JTextField WILfield=new JTextField(2);
	static JTextField INTfield=new JTextField(2);
	static JTextField LCKfield=new JTextField(2);
	
	static JTextField[] statFields={STRfield,DEXfield,FORfield,PERfield,WILfield,INTfield,LCKfield};
	
	public static int startingPoints=30; //player's starting stats
	
	public static void openCharacterCreationScreen(){		//TODO: make a method to go with this once which takes args and adds all the fields except name and title.
		
		startingPoints=30;
		
		raceOptions.setSelectedIndex(0);
		classOptions.setSelectedIndex(0);
		
		Container contentPane = frame.getContentPane();
		SpringLayout springLayout = new SpringLayout();
		contentPane.setLayout(springLayout);
		contentPane.setPreferredSize(SCREEN_DISPLAY_SIZE);
		
		JLabel titleLabel =new JLabel("Create your hero!");
		titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
		
		JLabel nameLabel =new JLabel("Name:");
		
		JLabel statPoolLabel =new JLabel("Points remaining:");
		statPoolLabel.setFont(new Font("Times New Roman", Font.ITALIC, 12));
		JLabel statsRemainingLabel =new JLabel(""+startingPoints);
		
		JButton createCharacter = new JButton("Begin your quest!");	
		createCharacter.addMouseListener(new CharacterCreateListener());
		
		JLabel raceLabel=new JLabel("Race:");		//TODO: a race and class must be selected before the game can begin. (consider making them random if none is chosen)
		JLabel classLabel=new JLabel("Class:");
		
		//stats
		JLabel STRLabel =new JLabel("Strength:");		//strength
		
		JButton STRplus=new JButton("+"); STRplus.setFont(new Font("Times New Roman", Font.BOLD, 11));
		JButton STRminus=new JButton("-"); STRminus.setFont(new Font("Times New Roman", Font.BOLD, 11));
		
		JLabel DEXLabel =new JLabel("Dexterity:");		//dexterity
		JButton DEXplus=new JButton("+"); DEXplus.setFont(new Font("Times New Roman", Font.BOLD, 11));
		JButton DEXminus=new JButton("-"); DEXminus.setFont(new Font("Times New Roman", Font.BOLD, 11));
		
		JLabel FORLabel =new JLabel("Fortitude:");		//fortitude
		JButton FORplus=new JButton("+"); FORplus.setFont(new Font("Times New Roman", Font.BOLD, 11));
		JButton FORminus=new JButton("-"); FORminus.setFont(new Font("Times New Roman", Font.BOLD, 11));
		
		JLabel PERLabel =new JLabel("Perception:");		//perception
		JButton PERplus=new JButton("+"); PERplus.setFont(new Font("Times New Roman", Font.BOLD, 11));
		JButton PERminus=new JButton("-"); PERminus.setFont(new Font("Times New Roman", Font.BOLD, 11));
		
		JLabel WILLabel =new JLabel("Willpower:");		//willpower
		JButton WILplus=new JButton("+"); WILplus.setFont(new Font("Times New Roman", Font.BOLD, 11));
		JButton WILminus=new JButton("-"); WILminus.setFont(new Font("Times New Roman", Font.BOLD, 11));
		
		JLabel INTLabel =new JLabel("Intelligence:");		//intelligence
		JButton INTplus=new JButton("+"); INTplus.setFont(new Font("Times New Roman", Font.BOLD, 11));
		JButton INTminus=new JButton("-"); INTminus.setFont(new Font("Times New Roman", Font.BOLD, 11));
		
		JLabel LCKLabel =new JLabel("Luck:");		//luck
		JButton LCKplus=new JButton("+"); LCKplus.setFont(new Font("Times New Roman", Font.BOLD, 11));
		JButton LCKminus=new JButton("-"); LCKminus.setFont(new Font("Times New Roman", Font.BOLD, 11));
		
		titleLabel.setPreferredSize(new Dimension(400,40));
		
		nameLabel.setPreferredSize(new Dimension(200,40));
		nameEntry.setPreferredSize(new Dimension(200,40));
		nameEntry.setText("");
		
		statPoolLabel.setPreferredSize(new Dimension(120,30));
		statsRemainingLabel.setPreferredSize(new Dimension(30,30));
		
		createCharacter.setPreferredSize(new Dimension(150,40));
		
		raceLabel.setPreferredSize(new Dimension(150,40));
		classLabel.setPreferredSize(new Dimension(150,40));
		
		raceOptions.setPreferredSize(new Dimension(150,30));
		classOptions.setPreferredSize(new Dimension(150,30));
		
		setStatDisplaySizes(STRLabel, STRfield, STRplus, STRminus);
		setStatDisplaySizes(DEXLabel, DEXfield, DEXplus, DEXminus);
		setStatDisplaySizes(FORLabel, FORfield, FORplus, FORminus);
		setStatDisplaySizes(PERLabel, PERfield, PERplus, PERminus);
		setStatDisplaySizes(WILLabel, WILfield, WILplus, WILminus);
		setStatDisplaySizes(INTLabel, INTfield, INTplus, INTminus);
		setStatDisplaySizes(LCKLabel, LCKfield, LCKplus, LCKminus);
		
		springLayout.putConstraint(SpringLayout.NORTH, nameLabel, 5, SpringLayout.SOUTH, titleLabel);
		springLayout.putConstraint(SpringLayout.EAST, nameEntry, -10, SpringLayout.EAST, contentPane);
		springLayout.putConstraint(SpringLayout.NORTH, nameEntry, 5, SpringLayout.SOUTH, titleLabel);
		
		springLayout.putConstraint(SpringLayout.NORTH, statPoolLabel, 15, SpringLayout.SOUTH,nameLabel);
		springLayout.putConstraint(SpringLayout.EAST, statPoolLabel, -5, SpringLayout.EAST,contentPane);
		
		springLayout.putConstraint(SpringLayout.NORTH,statsRemainingLabel, 15, SpringLayout.SOUTH,nameLabel);
		springLayout.putConstraint(SpringLayout.WEST,statsRemainingLabel, -15, SpringLayout.EAST,statPoolLabel);
		
		springLayout.putConstraint(SpringLayout.SOUTH,createCharacter, -20, SpringLayout.SOUTH,contentPane);
		springLayout.putConstraint(SpringLayout.EAST,createCharacter, -15, SpringLayout.EAST,contentPane);
		
		springLayout.putConstraint(SpringLayout.SOUTH,classLabel, -85, SpringLayout.SOUTH,contentPane);
		springLayout.putConstraint(SpringLayout.WEST,classLabel, 155, SpringLayout.WEST,contentPane);
		
		springLayout.putConstraint(SpringLayout.SOUTH,raceLabel, -5, SpringLayout.NORTH,classLabel);
		springLayout.putConstraint(SpringLayout.WEST,raceLabel, 155, SpringLayout.WEST,contentPane);
		
		springLayout.putConstraint(SpringLayout.SOUTH,classOptions, -85, SpringLayout.SOUTH,contentPane);
		springLayout.putConstraint(SpringLayout.EAST,classOptions, -10, SpringLayout.EAST,contentPane);
		
		springLayout.putConstraint(SpringLayout.SOUTH,raceOptions, -15, SpringLayout.NORTH,classOptions);
		springLayout.putConstraint(SpringLayout.EAST,raceOptions, -10, SpringLayout.EAST,contentPane);
		
		//stats
		setStatDisplayConstraints(contentPane, 0, springLayout, STRLabel, STRfield, STRminus, STRplus);
		setStatDisplayConstraints(contentPane, 1, springLayout, DEXLabel, DEXfield, DEXminus, DEXplus);
		setStatDisplayConstraints(contentPane, 2, springLayout, FORLabel, FORfield, FORminus, FORplus);
		setStatDisplayConstraints(contentPane, 3, springLayout, PERLabel, PERfield, PERminus, PERplus);
		setStatDisplayConstraints(contentPane, 4, springLayout, WILLabel, WILfield, WILminus, WILplus);
		setStatDisplayConstraints(contentPane, 5, springLayout, INTLabel, INTfield, INTminus, INTplus);
		setStatDisplayConstraints(contentPane, 6, springLayout, LCKLabel, LCKfield, LCKminus, LCKplus);
		
		titleLabel.setBorder(BorderFactory.createLineBorder(Color.black));
	
		for(int i=0;i<statFields.length;i++){
			statFields[i].setEditable(false);
			statFields[i].setText("10");
			StatScrollListener statListener=new StatScrollListener(statFields[i]);
			statListener.linkToStatPool(statsRemainingLabel);
			statFields[i].addMouseWheelListener(statListener);
		}
		
		addStatAdjustListeners(STRminus, STRplus, STRfield, statsRemainingLabel);
		addStatAdjustListeners(DEXminus, DEXplus, DEXfield, statsRemainingLabel);
		addStatAdjustListeners(FORminus, FORplus, FORfield, statsRemainingLabel);
		addStatAdjustListeners(PERminus, PERplus, PERfield, statsRemainingLabel);
		addStatAdjustListeners(WILminus, WILplus, WILfield, statsRemainingLabel);
		addStatAdjustListeners(INTminus, INTplus, INTfield, statsRemainingLabel);
		addStatAdjustListeners(LCKminus, LCKplus, LCKfield, statsRemainingLabel);
		
		contentPane.add(titleLabel, SpringLayout.NORTH);
        contentPane.add(nameLabel);
        contentPane.add(nameEntry);
        contentPane.add(statPoolLabel);
        contentPane.add(statsRemainingLabel);
        
        contentPane.add(STRLabel); contentPane.add(STRfield); contentPane.add(STRminus); contentPane.add(STRplus);
        contentPane.add(DEXLabel); contentPane.add(DEXfield); contentPane.add(DEXminus); contentPane.add(DEXplus);
        contentPane.add(FORLabel); contentPane.add(FORfield); contentPane.add(FORminus); contentPane.add(FORplus);
        contentPane.add(PERLabel); contentPane.add(PERfield); contentPane.add(PERminus); contentPane.add(PERplus);
        contentPane.add(WILLabel); contentPane.add(WILfield); contentPane.add(WILminus); contentPane.add(WILplus);
        contentPane.add(INTLabel); contentPane.add(INTfield); contentPane.add(INTminus); contentPane.add(INTplus);
        contentPane.add(LCKLabel); contentPane.add(LCKfield); contentPane.add(LCKminus); contentPane.add(LCKplus);
        
        contentPane.add(raceLabel);
        contentPane.add(classLabel);
        
        contentPane.add(raceOptions);
        contentPane.add(classOptions);
        
        contentPane.add(createCharacter);
	}
	
	private static void setStatDisplaySizes(JLabel label, JTextField field,JButton minus, JButton plus){
		label.setPreferredSize(new Dimension(200,40));
		field.setPreferredSize(new Dimension(60,40));
		plus.setPreferredSize(new Dimension(40,40));
		minus.setPreferredSize(new Dimension(40,40));
	}
	
	private static void setStatDisplayConstraints(Container contentPane, int index, SpringLayout springLayout, 
			JLabel label, JTextField field,JButton minus, JButton plus){
		
		int height=140+index*50;
		
		springLayout.putConstraint(SpringLayout.NORTH, label, height, SpringLayout.NORTH, contentPane);
		
		springLayout.putConstraint(SpringLayout.EAST, field, -60, SpringLayout.EAST, contentPane);
		springLayout.putConstraint(SpringLayout.NORTH, field, height, SpringLayout.NORTH, contentPane);
		
		springLayout.putConstraint(SpringLayout.EAST, minus, -10, SpringLayout.WEST, field);
		springLayout.putConstraint(SpringLayout.NORTH, minus, height, SpringLayout.NORTH, contentPane);
		
		springLayout.putConstraint(SpringLayout.WEST, plus, 10, SpringLayout.EAST, field);
		springLayout.putConstraint(SpringLayout.NORTH, plus, height, SpringLayout.NORTH, contentPane);
	}
	
	private static void addStatAdjustListeners(JButton minus, JButton plus, JTextField statField, JLabel statPool){
		MinusListener statMinus=new MinusListener(statField);
		PlusListener statPlus=new PlusListener(statField);
		
		statMinus.linkToStatPool(statPool);
		statPlus.linkToStatPool(statPool);
		
		minus.addActionListener(statMinus);
		plus.addActionListener(statPlus);
	}
	
	private static class MinusListener implements ActionListener{
		JLabel statPool=new JLabel();
		JTextField statField;
			
		private void linkToStatPool(JLabel statPool){
			this.statPool=statPool;
		}
		
		private void incrementPool(){
			startingPoints++;
			statPool.setText(""+startingPoints);
		}
		
		public MinusListener(JTextField statField){
			this.statField=statField;
		}
		
		@Override
		public void actionPerformed(ActionEvent click) {
				int currentStat=Integer.parseInt(statField.getText());
				if(currentStat>7){
					statField.setText(""+(currentStat-1));
					incrementPool();
				}
			}
		}
	
	private static class PlusListener implements ActionListener{
		JLabel statPool=new JLabel();
		JTextField statField;
		
		private void linkToStatPool(JLabel statPool){
			this.statPool=statPool;
		}
		
		private void decrementPool(){
			startingPoints--;
			statPool.setText(""+startingPoints);
		}
		
		
		
		public PlusListener(JTextField statField){
			this.statField=statField;
		}
		@Override
		public void actionPerformed(ActionEvent click) {
			if(startingPoints>0){
				int currentStat=Integer.parseInt(statField.getText());
				statField.setText(""+(currentStat+1));
				decrementPool();
			}
		}
	}
	
	private static class StatScrollListener implements MouseWheelListener{
		JLabel statPool=new JLabel();
		JTextField statField;
		
		private void linkToStatPool(JLabel statPool){
			this.statPool=statPool;
		}
		
		private void decrementPool(){
			startingPoints--;
			statPool.setText(""+startingPoints);
		}
		
		private void incrementPool(){
			startingPoints++;
			statPool.setText(""+startingPoints);
		}
		
		public StatScrollListener(JTextField statField){
			this.statField=statField;
		}
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent scroll) {
			int notches = scroll.getWheelRotation();
			int statChange=Math.abs(notches);
			if(notches<0){
				for(int i=0;i<statChange&&startingPoints>0;i++){
					int currentStat=Integer.parseInt(statField.getText());	
					statField.setText(""+(currentStat+1));
					decrementPool();
				}
			}
			else if(notches>0){
				int currentStat=Integer.parseInt(statField.getText());	
				for(int i=0;i<statChange&&currentStat>7;i++){
					currentStat=Integer.parseInt(statField.getText());	
					statField.setText(""+(currentStat-1));
					incrementPool();
				}
			}
		}
	}
	
	private static class CharacterCreateListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent click) {
			attemptCharacterCreation();
		}

		@Override
		public void mouseEntered(MouseEvent evt) {}

		@Override
		public void mouseExited(MouseEvent evt) {}

		@Override
		public void mousePressed(MouseEvent evt) {
			attemptCharacterCreation();
		}

		@Override
		public void mouseReleased(MouseEvent evt) {}
		
		public void attemptCharacterCreation(){
			if(nameEntry.getText().trim().equals(""))
				JOptionPane.showMessageDialog(frame, "Please enter a name.");
			else if(raceOptions.getSelectedItem().toString()=="(Select race)"){
				JOptionPane.showMessageDialog(frame, "Please select a race.");
			}
			else if(startingPoints!=0){
				final JOptionPane optionPane = new JOptionPane(
						"You still have unspent skill points.\n"+
						"Are you sure you want to enter the dungeon?",
		                JOptionPane.QUESTION_MESSAGE,
		                JOptionPane.YES_NO_OPTION);

		final JDialog dialog = new JDialog(frame, "Click a button", true);
		dialog.setContentPane(optionPane);
		optionPane.addPropertyChangeListener(
			    new PropertyChangeListener() {
			        public void propertyChange(PropertyChangeEvent e) {
			            String prop = e.getPropertyName();

			            if (dialog.isVisible() 
			             && (e.getSource() == optionPane)
			             && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
			                dialog.setVisible(false);
			            }
			        }
			    });
			dialog.pack();
			dialog.setVisible(true);

			int value = ((Integer)optionPane.getValue()).intValue();
			if (value == JOptionPane.YES_OPTION) {
				confirmCharacterCreation();
			} 
		}
			else
				confirmCharacterCreation();
		}
		private void confirmCharacterCreation(){
		
			String playerRace=raceOptions.getSelectedItem().toString();		//TODO: do race and class affect starting stats?
			String playerClass=classOptions.getSelectedItem().toString();
			int[] stats={Integer.parseInt(STRfield.getText()),Integer.parseInt(DEXfield.getText()),Integer.parseInt(FORfield.getText()),
						Integer.parseInt(PERfield.getText()),Integer.parseInt(WILfield.getText()),Integer.parseInt(INTfield.getText()),
						Integer.parseInt(LCKfield.getText())};
			
			player1=new Player(nameEntry.getText().trim());
			player1.createCharacter(playerRace,playerClass,stats);
			
			frame.closeScreen();
			frame.beginGame();
			frame.startMap();
		}
	}
}
public void closeScreen() {		
		getContentPane().removeAll();
	}
private void beginGame(){

	 setStartConditions();
	 openMainScreen();
}
	
	
public class Death{		//exists to get around issues with static vs. nonstatic functions
	
	public void playerDies(String causeOfDeath){
		currentMessage=("You were killed by "+causeOfDeath+"! Press any key to continue.");
		refreshScreen();
	}
	
	public void goToDeathScreen(){
		currentScreenIndex=0;
		getContentPane().removeAll();
		frame.remove(infoDisplayColumn1);		//TODO: these "remove" steps may be unnecessary. Test dying multiple times with both lines commented out.
		frame.remove(actionDisplayArea);
		frame.setLayout(new BorderLayout());
		frame.add(deathScreenArea,BorderLayout.CENTER);
		deathScreenArea.setText("You have died!"+"\n"+"Do you want to continue? (y/n)");	//TODO: improve death screen (consult nick)
		deathScreenArea.setFont(new Font("Courier New", Font.BOLD, 16));
		deathScreenArea.setVisible(true);		
		deathScreenArea.setPreferredSize(DEATH_DISPLAY_SIZE);
		deathScreenArea.addKeyListener(YNL);
		YNL.event="death";
		deathScreenArea.requestFocus();
	}
	
	public void goToDeathScreen(String message){		//may replace the above class, or be useful for special deaths.
		currentScreenIndex=0;
		getContentPane().removeAll();
		frame.remove(infoDisplayColumn1);
		frame.remove(actionDisplayArea);
		frame.setLayout(new BorderLayout());
		deathScreenArea.setWrapStyleWord(true);
		frame.add(deathScreenArea,BorderLayout.CENTER);
		
		deathScreenArea.setText(message+"\n\n"+
				"You have died!"+"\n"+"Do you want to try again? (y/n)");	//TODO: improve death screen (consult nick)
		
		deathScreenArea.setFont(new Font("Courier New", Font.BOLD, 16));
		deathScreenArea.setVisible(true);		
		deathScreenArea.setPreferredSize(DEATH_DISPLAY_SIZE);
		deathScreenArea.addKeyListener(YNL);
		YNL.event="death";
		deathScreenArea.requestFocus();
	}
}

//all classes below here are keylisteners. (could potentially move them all into a separate file)

private class MainScreenListener implements KeyListener{		//takes in key commands while on the main map screen.

		@Override
		public void keyPressed(KeyEvent e) {
			mainKeyPress(e);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			mainKeyPress(e);
		}

		@Override
		public void keyTyped(KeyEvent e) {
			mainKeyPress(e);
		}
		
		private void mainKeyPress(KeyEvent e){
			
			if(player1.currentHp()<=0){		//this checks for pressing any key after the player has died
				if(playerDead){
					mapDisplayArea.removeKeyListener(this);
					playerDeath.goToDeathScreen();
				}
				else
					playerDead=true;
				return;		
				}
			
			//if(currentScreenIndex==1){
			int id = e.getID();
			
			if(id==KeyEvent.KEY_RELEASED){

				
				//currentMessage="";
				
				int keyCode=e.getKeyCode();
				switch (keyCode){
				case(KeyEvent.VK_UP):
					player1.move('8');
					player1.endPlayerTurn();
					return;
				case(KeyEvent.VK_DOWN):
					player1.move('2');
        			player1.endPlayerTurn();
        			return;
				case(KeyEvent.VK_LEFT):
        			player1.move('4');
        			player1.endPlayerTurn();
        			return;
				case(KeyEvent.VK_RIGHT):
					player1.move('6');
        			player1.endPlayerTurn();
        			return;
				}
			}
        	
	        if (id == KeyEvent.KEY_TYPED) {	//the second condition is specific to the main screen.
	            char c = e.getKeyChar();
	            if(Character.isDigit(c)){
	            	player1.move(c);
	            	player1.endPlayerTurn();
	            }
	            else //if(Character.isLetter(c))
	            	playerCommand(c);
	            refreshScreen();
	        }
		//}
	}
		
		public void playerCommand(char keyPressed){	//there are many player commands. consider moving them to another class, and simply calling them with this method.
			
			itemType=Item.itemNameForLetterCommand(keyPressed);
				//NOTE: whenever a new key command is added, add a corresponding one in (itemNameForLetterCommand) if appropriate.
			switch(keyPressed){		//a lot of this is likely to be repeated for other screens
				case('i'):		//open inventory
	    			getContentPane().removeAll();	//store commands that open other screens here.
	    			openInventoryScreen();
	    			return;
				case('?'):
					getContentPane().removeAll();
					openInstructions();	
					return;
				case('S'):
					getContentPane().removeAll();
					openSkillScreen();
					return;
				case('.'):							//store commands that interact with tiles (or wait) here.
					currentMessage="";
					player1.endPlayerTurn();
					return;
				case('s'):
					currentMessage="";
					player1.search();
					player1.endPlayerTurn();
					return;
				case('o'):							
					currentMessage="Open or shut door in which direction?";
					directionSelectL.event="open door";
					mapDisplayArea.removeKeyListener(this);
					mapDisplayArea.addKeyListener(directionSelectL);
					break;			
				case('>'):		//go down stairs
					player1.goDownStairs();
					return;
				case('<'):		//go up stairs
					player1.goUpStairs();
					return;
				case('d'):		//drop an item			//store item-related commands here.
					currentMessage= ("Drop which item?");
					itemAction="Drop";
					showItemOptions('d');
					selectItem();
					break;
				case('D'):
					currentMessage= ("Drop which item?");
					itemAction="Drop multiple";
					showItemOptions('D');
					selectItem();
					break;
				case(','):		//pick up an item off the ground
					int itemsInTile=player1.currentTile.tileItems.getItemCount();
					if(itemsInTile>1){	//if there are multiple items, choose which one to pick up.
						currentMessage= ("Pick up which item?");
						itemAction="Pick up";
						showItemOptions(',');
						selectItem();
					}
					else if(itemsInTile==0)	//if there are no items, nothing happens.
					{
						if(!player1.currentTile.tileItems.noGold()){
							currentMessage="";
							player1.pickUpGold();
						}
						break;
					}	
					else if(itemsInTile==1){	//if there is only one item, pick it up.{
						currentMessage="";
						if(player1.currentTile.tileItems.noGold())
							player1.pickUpItem(0);
						else{
							currentMessage= ("Pick up what?");
							itemAction="Pick up";
							showItemOptions(',');
							selectItem();
						}
					break;		
					}
				break;
				case('I'):	//identify an item. (NOTE: this is temporary for testing. The player should not be able to identify any item at will in the final version.)
					currentMessage= ("Identify which item?");
					itemAction="Identify";
					showItemOptions('I');
					selectItem();
					break;
				case('t'):	//throw an item
					if(player1.inventory.isEmpty())
						currentMessage= ("Nothing to throw.");
					else{
					currentMessage= ("Throw or shoot which item?");
					itemAction="Throw";
					showItemOptions('t');
					selectItem();
					}
					break;
				case('@'):		//toggle autopickup
					if(autoPickUp)
						currentMessage="Autopickup turned off.";
					else
						currentMessage="Autopickup turned on.";
					autoPickUp=!autoPickUp;
					break;
				case('e'):		//eat a food item
					currentMessage=("Eat what?");
					showItemOptions('e');
					selectItem();
					break;
	    		case('q'):		//quaff a potion
	    			currentMessage= ("Drink which potion?");
	    			showItemOptions('q');
    				selectItem();
	    			break;
	    		case('E'):		//Equip an item
	    			currentMessage= ("Equip or unequip which item?");
	    			showItemOptions('E');
	    			selectItem();
	    			break;
	    		case('Z'):		//cast a spell								//store miscellaneous commands here. (might make more categories as more commands are added.)
	    			if(!player1.knowsSpells())
						currentMessage= ("You don't know any spells.");
					else{
					currentMessage= ("Cast which spell?");
					showSpellOptions();
					selectSpell();
					}
	    			break;
	    		default:
	    			currentMessage="";
					refreshScreen();
	    			return;
	    		}
			
		}

		public void showItemOptions(char keyPressed){
			Inventory itemSource;	//determines where the player is choosing an item from. Right now, only "pick up" chooses items from somewhere other than the player's inventory.
			switch(keyPressed){
			case(','):
				itemSource=player1.currentTile.tileItems;
			break;
			default:
				itemSource=player1.inventory;
			}
			if(itemSource.getItemsOfType(itemType).isEmpty()){	//if there are no item options, say so instead of asking the player to choose an item.
				currentMessage=Item.noOptionsMessage(itemType);			
				return;
			}
			itemType=Item.itemNameForLetterCommand(keyPressed);
			String []itemOptions=new String[itemSource.getItemCount()];
			itemOptions=itemSource.showItemsOfType(itemType,player1);
			
			if(itemOptions[0]!=null){
				currentMessage+=" (";
				if(keyPressed==','&&!player1.currentTile.tileItems.noGold()){	
					currentMessage+="($)"+player1.currentTile.getGold()+" gold";
				}
				currentMessage+=itemOptions[0];
				int index=1;
				while(index<itemOptions.length
					&&itemOptions[index]!=null){
					currentMessage+=", "+itemOptions[index];
					index++;
				}
				currentMessage+=")";
			}
		}
		
		public void selectItem(){		//from the main screen, select an item to do something with.
			mapDisplayArea.removeKeyListener(this);
			mapDisplayArea.addKeyListener(itemSelectL);
		}
		
		//spell methods
		
		private void showSpellOptions() {
			Spell[] spells=player1.spells;
			
			  if(player1.knowsSpells()){	
				currentMessage+=" ((a)"+spells[0];
				int index=1;
				while(index<spells.length
					&&spells[index]!=null){
					currentMessage+=", ("+Item.ALPHABET[index]+")"+spells[index];
					index++;
				}
				currentMessage+=")";
			}		
		}
		
		private void selectSpell(){
			mapDisplayArea.removeKeyListener(this);
			mapDisplayArea.addKeyListener(spellSelectL);
		}
	}
//consider putting these action listeners in separate files to avoid a super long file
private class InventoryScreenListener implements KeyListener{		//button commands on inventory screen
	
	@Override
	public void keyPressed(KeyEvent e) {		//TODO: find a way to avoid this sort of repetition
		inventoryKeyPress(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		inventoryKeyPress(e);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		inventoryKeyPress(e);		
	}

	private void inventoryKeyPress(KeyEvent e) {	//control scheme on inventory screen
		if(currentScreenIndex==2){	//might not need this check. can instead use "removeKeyListener" in appropriate places.
			int id = e.getID();
			if(id==KeyEvent.KEY_RELEASED){
				int keyCode=e.getKeyCode();
				
				switch(keyCode){
				case(KeyEvent.VK_ESCAPE):
					inventoryCommand("escape");
					break;
				default:
					break;
				}
			}
			
			if (id == KeyEvent.KEY_TYPED) {	//TODO: implement more inventory commands
				
				char c = e.getKeyChar();
				if(Character.isLetter(c))
					inventoryCommand(""+c);
        	}
		}
	}

	private void inventoryCommand(String command) {
		switch(command){
		case("escape"):			//this is the "close to main screen" command, which will probably be on every screen and therefore should be placed elsewhere.
			currentScreenIndex = 1;
			closeScreen();
			openMainScreen();
			break;
		default:
			break;
		}
	}	
}

private class ItemSelectListener implements KeyListener{	//maybe this should be in a separate file? it's going to get very long
	@Override
	public void keyPressed(KeyEvent e) {		//TODO: find a way to avoid this sort of repetition
		itemKeyPress(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		itemKeyPress(e);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		itemKeyPress(e);		
	}
	
	private void itemKeyPress(KeyEvent e) {	//control scheme for choosing items
		if(currentScreenIndex==1){
			int id = e.getID();
			
			if (id == KeyEvent.KEY_TYPED) {	
				char c = e.getKeyChar();
				if(Character.isDigit(c)){
					returnToMainListener();
					currentMessage="";
					refreshScreen();
					return;
				}
				int itemIndex=Item.getNumForCharacter(c);
				
				switch(itemType){
				case("Item"):		//NOTE: as more actions are added, there may be multiple things to do with a single item type. I will either need to add more possibilities for this string, or make getters which return different item types (other methods dictate what actions are taken from there.)
					switch(itemAction){
					case("Drop"):
						dropItem(itemIndex);
						break;
					case("Drop multiple"):
						dropMultipleItems(itemIndex);
						break;
					case("Identify"):
						identifyItem(itemIndex);
						break;
					case("Pick up"):
						if(c=='$'&&!player1.currentTile.tileItems.noGold())
							pickUpGold();
						else
							pickUpItem(itemIndex);
						break;
					case("Throw"):
						playerThrowItem(itemIndex);
						break;
					}
					break;
				case("Food"):
					eatItem(itemIndex);
					break;
				case("Potion"):
					quaffItem(itemIndex);
					break;
				case("Equipment"):
					equipItem(itemIndex);
					break;
				default:
					break;
				}
        	}
		}
	}

	private void dropItem(int itemIndex){
		if(playerHasItem(itemIndex)){
			Item item=player1.inventory.getItem(itemIndex);
			if((item.getClass()==Weapon.class||item.getClass()==Armor.class)
			&&(((Equipment)item).equipped)){
				currentMessage="You can't drop that "+item.toString()+ " while it's equipped.";
				returnToMainListener();
				return;
			}
				player1.dropItem(itemIndex);
		}
			
		returnToMainListener();
	}
	
	//TODO: whatever changes are made to "drop" should be made here, too.
	private void dropMultipleItems(int itemIndex){					//player gets to choose how much of a stack to drop.
		if(playerHasItem(itemIndex)){
			if(!player1.inventory.getItem(itemIndex).stackable())	//if the item is not stackable, drop it normally.
				dropItem(itemIndex);
			else{
				String droppedStackInput=JOptionPane.showInputDialog(actionDisplayArea,"Drop how many of "+player1.inventory.getItem(itemIndex)+"?",null);
				int droppedStackSize=0;
				if(droppedStackInput==null){
					currentMessage="";
					returnToMainListener();
					return;
				}
				if(isNumeric(droppedStackInput)&&droppedStackInput.length()<=3)
					droppedStackSize=Integer.parseInt(droppedStackInput);
				while(!isNumeric(droppedStackInput)
					|| droppedStackInput.length()>3 ||
						(isNumeric(droppedStackInput)  
					&&Integer.parseInt(droppedStackInput)<0)){
					if(droppedStackInput==null){
						currentMessage="";
						returnToMainListener();
						return;
					}
					droppedStackInput=JOptionPane.showInputDialog(actionDisplayArea,"Not a valid number. \n Drop how many of "+player1.inventory.getItem(itemIndex)+"?",null);
				}
				droppedStackSize=Integer.parseInt(droppedStackInput);
				if(player1.inventory.getItem(itemIndex).enoughInStack(droppedStackSize))	//make sure player has enough of that item to drop.
					player1.dropItemAmount(itemIndex,droppedStackSize);
				else
					currentMessage="You don't have that many of that item.";
			}
		}
		returnToMainListener();
	}
	
	private void identifyItem(int index){	//consider a "you don't have that item" for invalid letters, and possibly for other situations.
		if(playerHasItem(index))
			player1.identify(player1.inventory.getItem(index));
		returnToMainListener();
	}
	
	private boolean isNumeric(String str)
	{
		if(str==null)
			return false;
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	
	private void pickUpItem(int itemIndex){
		currentMessage="";
		if(player1.currentTile.tileItems.containsItem(itemIndex))
			player1.pickUpItem(itemIndex);
		returnToMainListener();
	}
	
	private void pickUpGold(){
		currentMessage="";		//NOTE: there may be a better (more general) place to put this, but I'm not sure.
		player1.pickUpGold();
		returnToMainListener();
	}
	
	private void playerThrowItem(int itemIndex) {
		
		if(playerHasItem(itemIndex)){
			if(player1.inventory.getItem(itemIndex).equippable()
				&& ((Equipment)player1.inventory.getItem(itemIndex)).equipped){
				currentMessage="Can't throw "+player1.displayItemName(player1.inventory.getItem(itemIndex),false) + ". Unequip it first.";
				returnToMainListener();
			}
			else
				{
				Item thrownItem=player1.inventory.getItem(itemIndex);
				if(thrownItem.getClass()==Ammo.class
				&&player1.canShootProperly((Ammo)thrownItem))
					currentMessage="Shoot ";
				else
					currentMessage="Throw ";
				currentMessage+= player1.displayItemName(player1.inventory.getItem(itemIndex),false) + " in which direction?";
				directionSelectL.event="throw";
				directionSelectL.itemIndex=itemIndex;
				refreshScreen();
				selectDirection();
				}
			}
	}
	
	private void eatItem(int itemIndex) {	
		if(playerHasItem(itemIndex)){	
			if((player1.inventory.getItem(itemIndex)).getClass()==Food.class){
				Food consumingItem=(Food) player1.inventory.getItem(itemIndex);
				player1.eat(consumingItem);
			}
			else
				currentMessage="You can't eat that "+player1.displayItemName(player1.inventory.getItem(itemIndex),false)+".";
		}
		returnToMainListener();
	}

	private void quaffItem(int itemIndex) {	//TODO: figure out how to keep this as one class while still including consumable food.
										// TODO: look for commonalities between consumeItem() and equipItem() so that some actions can be combined into one class.
		if(playerHasItem(itemIndex)){	
				if((player1.inventory.getItem(itemIndex)).getClass()==Potion.class){
				Potion quaffingPotion=(Potion) player1.inventory.getItem(itemIndex);
				player1.quaff(quaffingPotion);
				player1.endPlayerTurn();	//only end turn if an item was consumed.
				}
				else
					currentMessage="You can't drink that "+player1.displayItemName(player1.inventory.getItem(itemIndex),false)+".";
			}
			returnToMainListener();
	}

	private void equipItem(int itemIndex) {
		if(playerHasItem(itemIndex))
			player1.attemptEquip(itemIndex);		
		returnToMainListener();
	}
	
	private boolean playerHasItem(int itemIndex){
		return player1.hasItem(itemIndex);
	}
	
	public void selectDirection(){ //for an action other than moving, select a numpad direction.	//TODO
		mapDisplayArea.removeKeyListener(this);
		mapDisplayArea.addKeyListener(directionSelectL);
	}
	
	private void returnToMainListener(){
		refreshScreen();
        mapDisplayArea.removeKeyListener(this);
        mapDisplayArea.addKeyListener(mainScreenL);	
	}
	
}

private class DirectionSelectListener implements KeyListener{

	String event="";
	int itemIndex=-1;//this is used in case an item is being used/thrown/etc. in a specific direction.
	Spell missile = null;	//used in case a spell is being shot in a direction
	
	@Override
	public void keyPressed(KeyEvent e) {
		directionEvent(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		directionEvent(e);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		directionEvent(e);
	}

	private void directionEvent(KeyEvent e) {
		int id = e.getID();
		if(id==KeyEvent.KEY_RELEASED){
			int keyCode=e.getKeyCode();
			switch (keyCode){
			case(KeyEvent.VK_UP):
				chooseDirection('8');
				return;
			case(KeyEvent.VK_DOWN):
				chooseDirection('2');
    			return;
			case(KeyEvent.VK_LEFT):
				chooseDirection('4');
    			return;
			case(KeyEvent.VK_RIGHT):
				chooseDirection('6');
    			return;
    		default:
    			break;
			}
		}
		if(id==KeyEvent.KEY_TYPED){
			 char c = e.getKeyChar();
	            if(Character.isDigit(c)){
	            	chooseDirection(c);
	            	return;
	            }
	            	mapDisplayArea.removeKeyListener(this);
	            	mapDisplayArea.addKeyListener(mainScreenL);
	            	currentMessage="";
	            	refreshScreen();
	            }
	            
			}
		
	
	private void chooseDirection(char direction) {
		switch(event){
		case "":
			return;
		case "throw":
			mapDisplayArea.removeKeyListener(this);
			player1.throwItem(itemIndex, direction);
			return;
		case "cast missile":
			mapDisplayArea.removeKeyListener(this);
			player1.castSpell(missile, direction);
			return;
		case "open door":
			mapDisplayArea.removeKeyListener(this);
			player1.openDoor(direction);
			refreshScreen();
			mapDisplayArea.addKeyListener(mainScreenL);
			return;
		default:
			break;
		}	
	}

	private void returnToMainListener(){		//not sure if I need this, but holding on to it in case I do
		refreshScreen();
        mapDisplayArea.removeKeyListener(this);
        mapDisplayArea.addKeyListener(mainScreenL);	
	}
}

private class SpellSelectListener implements KeyListener{
	@Override
	public void keyPressed(KeyEvent e) {		//TODO: find a way to avoid this sort of repetition
		spellKeyPress(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		spellKeyPress(e);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		spellKeyPress(e);		
	}

	private void spellKeyPress(KeyEvent e) {
		int id = e.getID();
		
		if (id == KeyEvent.KEY_TYPED) {	
			char c = e.getKeyChar();
			if(Character.isDigit(c)){
				returnWithMessage("");
				return;
			}
			int spellIndex=Item.getNumForCharacter(c);		//not a valid spell choice
			if(!player1.knowsSpell(spellIndex)){
				returnWithMessage("You don't know that spell.");
				return;
			}
			Spell castSpell=player1.getSpell(spellIndex);	//not enough MP
			if(!player1.enoughMP(castSpell)){
				returnWithMessage("Not enough MP.");
				return;
			}
			
			//at this point, the spell will almost definitely be cast.
			
			switch(castSpell.castStyle){
			case("missile"):
				castMissile(castSpell);
				break;
			default:
				break;
			}
		}
	}
	
	public void castMissile(Spell missile){
		currentMessage="Shoot ";
		currentMessage+= missile + " in which direction?";
		directionSelectL.event="cast missile";
		directionSelectL.missile=missile;
		refreshScreen();
		selectDirection();	
	}
	
	private void returnWithMessage(String message){
		returnToMainListener();
		currentMessage=message;
		refreshScreen();
	}
	
	public void selectDirection(){ 
		mapDisplayArea.removeKeyListener(this);
		mapDisplayArea.addKeyListener(directionSelectL);
	}
	
	private void returnToMainListener(){
		refreshScreen();
        mapDisplayArea.removeKeyListener(this);
        mapDisplayArea.addKeyListener(mainScreenL);	
	}
}

private class YNListener implements KeyListener{
	
	String event = "";
	@Override
	public void keyPressed(KeyEvent e) {
		yesOrNoEvent(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		yesOrNoEvent(e);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		yesOrNoEvent(e);
	}

	private void yesOrNoEvent(KeyEvent e) {
		int id = e.getID();
		if(id==KeyEvent.KEY_TYPED){
		switch (e.getKeyChar()){
		case('y'):
			makeDecision('y');
		break;
		case('Y'):
			makeDecision('y');
		break;
		case('n'):
			makeDecision('n');
		break;
		case('N'):
			makeDecision('n');
		break;
			}
		}
	}
	
	public void makeDecision(char decision){	//add more decisions here as necessary
		switch(event){
		case(""):	//no event
			return;
		case("death"):
			deathEvent(decision);
		break;
		default:
			break;
		}
	}

	private void deathEvent(char decision) {	//TODO: make sure the HP display shows properly upon death, and make the death screen look nicer.
		switch(decision){
		case('y'):
			//deathScreenArea.addWindowListener
			RogueLikeGui.currentScreenIndex=1;
			RogueLikeGui.frame.dispose();
			RogueLikeGui.frame.startMap();
			autoPickUp=true;
			d.setMap();
			RogueLikeGui.createAndShowGUI();
		break;
		case('n'):
			System.exit(0);
		break;
		}	
	}
}

private class EscapeListener implements KeyListener{		//this only takes ESC as commmand, and goes to main screen. (consider replacing inventory screen listener with this if there turn out to be no other inventory commands.)
	
	@Override
	public void keyPressed(KeyEvent e) {		//TODO: find a way to avoid this sort of repetition
		keyPress(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keyPress(e);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		keyPress(e);		
	}

	private void keyPress(KeyEvent e) {	
			int id = e.getID();
			if(id==KeyEvent.KEY_RELEASED){
				int keyCode=e.getKeyCode();
				
				switch(keyCode){
				case(KeyEvent.VK_ESCAPE):
					command("escape");
					break;
				default:
					break;
				}
			}	
			if (id == KeyEvent.KEY_TYPED) {					
				char c = e.getKeyChar();
				if(Character.isLetter(c))
					command(""+c);
        	}
	}

	private void command(String command) {
		switch(command){
		case("escape"):			//this is the "close to main screen" command, which will probably be on every screen and therefore should be placed elsewhere.
			mainScreen.removeKeyListener(this);
			closeScreen();
			openMainScreen();
			break;
		default:
			break;
		}
	}	
}

private class ProjectileWorker extends SwingWorker<Object, Object>{

	Monster thrower;
	Item thrownItem;
	char direction;
	Tile startTile;
	
	Spell missile=null; 	//null unless the projectile is actually a spell.
	
	public void setStartConditions(Monster thrower, Item thrownItem, char direction,
			Tile startTile) {
		this.thrower=thrower;
		this.thrownItem=thrownItem;
		this.direction=direction;
		this.startTile=startTile;		
			//states whether a player threw an item, or a monster did. TODO: later, make sure monster throws display correctly.
	}
	
	public void setStartConditions(Monster caster, Spell missile,
			char direction, Tile startTile) {
		this.thrower=caster;
		this.missile=missile;
		this.direction=direction;
		this.startTile=startTile;	
	}

	@Override
	protected Object doInBackground() {	//shows an item traveling through the air. TODO: call collisions here.
		if(thrownItem!=null)
			excecuteThrow();
		else if(missile!=null)
			executeMissile();
		return null;
	}

	private void excecuteThrow() {
		currentMessage="";
		startTile.addItem(thrownItem);
		Tile nextTile=null;
    	while(Movement.tileInDirection(player1.currentLevel,startTile,direction).isPassable
    		&& thrownItem.getThrownDistance()>0){	//while the next tile is passable and the thrown item still has airtime, it keeps moving across the screen.
    		nextTile=Movement.tileInDirection(player1.currentLevel,startTile,direction);
    		refreshScreen();
    		mapDisplayArea.repaint();
    		nextTile.addItem(startTile.takeItem(thrownItem));
    		RogueLikeGui.wait(TICK_TIME);
    		startTile=nextTile;
    		thrownItem.decrementThrownDistance();
    	}
    	
    	Tile endTile=Movement.tileInDirection(player1.currentLevel,startTile,direction);	//assumes that the player is throwing-- may need a monster version.
    	refreshScreen();
		mapDisplayArea.repaint();
		
		if(endTile.monster!=null){	//decide what happens if the item hits a monster
			endTile.addItem(startTile.takeItem(thrownItem));
    		RogueLikeGui.wait(TICK_TIME);
    		thrownItem.collide(thrower, endTile.monster);
    		if(thrownItem.getClass().equals(Potion.class)){	//consider calling a separate method to check for item breaking if it gets more complicated.
    			endTile.monster.changeCurrentMessage("The potion shattered!", endTile, false);
    			endTile.removeItem(thrownItem);
    		}
    		else if(thrownItem.getClass().equals(Ammo.class)
    		&& ((Ammo)thrownItem).breakRoll()){
    			endTile.monster.changeCurrentMessage("The "+thrownItem.genericName()+" broke!", endTile, false);
    			endTile.removeItem(thrownItem);
    		}
    	}
    	else {	//if the item doesn't hit a monster, it can still break in some cases. TODO: Consider making this a separate method, or rewriting to avoid redundancy.
    		endTile=startTile;
    		if(thrownItem.getClass().equals(Potion.class)){
    			player1.changeCurrentMessage("The potion shattered!", startTile, false);
    			endTile.removeItem(thrownItem);
    		}
    		else if(thrownItem.getClass().equals(Ammo.class)
    	    	&& ((Ammo)thrownItem).breakRoll()){
    	    	player1.changeCurrentMessage("The "+thrownItem.genericName()+" broke!", endTile, false);
    	    	endTile.removeItem(thrownItem);
    		}
    	}
    	refreshScreen();
    	if(thrower.getClass().equals(Player.class)){
    		mapDisplayArea.addKeyListener(mainScreenL);
    		player1.endPlayerTurn();
	    	}
	}
	
	private void executeMissile() {		//TODO: if possible/useful, make projectiles more general so this and throw are one method
		currentMessage="";				//TODO: replace * with a getter for a spell icon.
		char spellIcon=missile.getIcon();
		//startTile.addItem(thrownItem);
    	while(Movement.tileInDirection(player1.currentLevel,startTile,direction).isPassable){
    		Tile nextTile=Movement.tileInDirection(player1.currentLevel,startTile,direction);
    		refreshScreen();
    		mapDisplayArea.repaint();	//might not be necessary
    		nextTile.setIcon(spellIcon);
    		startTile.displayIcon();
    		RogueLikeGui.wait(TICK_TIME);
    		startTile=nextTile;
    	}
    	
    	Tile endTile=Movement.tileInDirection(player1.currentLevel,startTile,direction);
    	startTile.setIcon(spellIcon);
    	refreshScreen();
    	RogueLikeGui.wait(TICK_TIME);
    	startTile.displayIcon();
    	endTile.displayIcon();
    	refreshScreen();
		mapDisplayArea.repaint();		//might not be necessary
    	if(endTile.monster!=null){	//decide what happens if the item hits a monster	
    		RogueLikeGui.wait(TICK_TIME);
    		currentMessage=endTile.monster+" was hit by a "+missile+"!";	//TODO: replace this in a spell's collide method as necessary
    		missile.collide(thrower, endTile.monster);
    	}
    	refreshScreen();
    	if(thrower.getClass()==Player.class){
    		mapDisplayArea.addKeyListener(mainScreenL);
    		player1.endPlayerTurn();
    	}
    	startTile.displayIcon();
	}
}
}
