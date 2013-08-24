import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

//add a way for the player to improve skills. (make it very easy for testing purposes.)
//in order to ensure that the screen refreshes properly, store one SkillScreen object in the accessing class and call "open" every time this needs to be opened.

//NOTE: this could probably inherit from another screen type if necessary. Consider creating an inheritance hierarchy.
public class SkillScreen {		//the first of many screen classes which will be separate from the GUI.

	private static final JLabel titleLabel=new JLabel("SKILLS:");
	
	private static final Dimension TITLE_LABEL_SIZE=new Dimension(120,20);		//TODO: organize sizes better. Check for sizes shared by many components.
	private static final Dimension CATEGORY_LABEL_SIZE=new Dimension(65,20);
	private static final Dimension OTHER_LABEL_SIZE=new Dimension(100,16);
	private static final Dimension SKILL_VALUE_SIZE=new Dimension(16,16);
	
	private static final Font TITLE_LABEL_FONT=new Font("Dialog",Font.BOLD,11);
	private static final Font CATEGORY_LABEL_FONT=new Font("Dialog",Font.BOLD,11);
	private static final Font SKILL_LABEL_FONT=new Font("Dialog",Font.PLAIN,11);
	
	private GridBagLayout layout=new GridBagLayout();
	private GridBagConstraints constraints=new GridBagConstraints();

	private JLabel[] categoryLabels;
	private JLabel[][] skillLabels;
	private JLabel[][] skillValues;
	
	private static RogueLikeGui frame;
	private Player player;
	
	public SkillScreen(RogueLikeGui frame, Player player){
		
		SkillScreen.frame=frame;
		this.player=player;
	}
	
	public void open(){
		
		titleLabel.setPreferredSize(TITLE_LABEL_SIZE);
		titleLabel.setFont(TITLE_LABEL_FONT);
		
		categoryLabels=new JLabel[Skill.CATEGORIES.length];
		skillLabels=new JLabel[Skill.ALL_SKILLS.length][20];
		skillValues=new JLabel[Skill.ALL_SKILLS.length][20];
		
		for(int i=0;i<categoryLabels.length;i++){
			categoryLabels[i]=new JLabel(Skill.CATEGORIES[i]);
			categoryLabels[i].setPreferredSize(CATEGORY_LABEL_SIZE);
			categoryLabels[i].setFont(CATEGORY_LABEL_FONT);
			
			int categorySize=Skill.ALL_SKILLS[i].length;
			for(int j=0;j<categorySize&&Skill.ALL_SKILLS[i][j]!=null;j++){
				skillLabels[i][j]= new JLabel(Skill.ALL_SKILLS[i][j]);
				skillLabels[i][j].setPreferredSize(OTHER_LABEL_SIZE);
				skillLabels[i][j].setFont(SKILL_LABEL_FONT);
			}
		}
		
		frame.getContentPane().setLayout(layout);
		addLabels();
		addSkills();
		frame.pack();
		frame.getContentPane().addKeyListener(new SkillKeyListener());
		frame.getContentPane().requestFocus();
	}
	
	private void addSkills(){	
		int labelIndex=1;
		for(int i=0;i<Skill.CATEGORIES.length;i++){
			for(int j=0;j<Skill.ALL_SKILLS[i].length;j++){
				skillValues[i][j]=new JLabel(""+player.skillLevel(Skill.CATEGORIES[i],Skill.ALL_SKILLS[i][j]));
				skillValues[i][j].setSize(SKILL_VALUE_SIZE);
				setConstraints(GridBagConstraints.HORIZONTAL,GridBagConstraints.EAST,1,		
						5.0, 0.5, 2, labelIndex+1);
				frame.getContentPane().add(skillValues[i][j],constraints);
				labelIndex++;
			}
			labelIndex++;
		}
	}
	
	private void addLabels(){
		
		setConstraints(GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTH,0,
				5.0, 0.5, 0,0);
		frame.getContentPane().add(titleLabel,constraints);
		
		int labelIndex=0;
		int segmentIndex=0;
		int categoryIndex=0;
		
		while(categoryIndex<categoryLabels.length&&categoryLabels[categoryIndex]!=null){
			setConstraints(GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1,		
					5.0, 0.5, 0, labelIndex+1);
			frame.getContentPane().add(categoryLabels[categoryIndex],constraints);
			
			segmentIndex=0;
			labelIndex++;
			while(segmentIndex<skillLabels[categoryIndex].length&&skillLabels[categoryIndex][segmentIndex]!=null){
				setConstraints(GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1,		
						1.0, 0.5, 0, labelIndex+1);
				frame.getContentPane().add(skillLabels[categoryIndex][segmentIndex],constraints);
				segmentIndex++;
				labelIndex++;
			}
			categoryIndex++;	
		}
	}
	
	private void setConstraints(int fill, int anchor, int gridWidth, 
								double weightx, double weighty,int gridx, int gridy){
		
		constraints.fill=fill;
		constraints.anchor=anchor;
		constraints.gridwidth=gridWidth;
		
		constraints.weightx=weightx;
		constraints.weighty=weighty;
		
		constraints.gridx = gridx;
		constraints.gridy =gridy;
	}
	
	private class SkillKeyListener implements KeyListener{	//if the only possible button is esc, consider making an "escapeActionlistener" class

		@Override
		public void keyPressed(KeyEvent e) {
			handleKey(e);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			handleKey(e);
		}

		@Override
		public void keyTyped(KeyEvent e) {
			handleKey(e);
		}
		
		public void handleKey(KeyEvent e){
			if(e.getID()==KeyEvent.KEY_RELEASED){
				int keyCode=e.getKeyCode();
				switch(keyCode){
				case(KeyEvent.VK_ESCAPE):
					frame.closeScreen();
					frame.openMainScreen();
					break;
				}
			}
		}
	}
}
