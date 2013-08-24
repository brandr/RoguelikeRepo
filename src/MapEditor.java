import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;


//TODO: make it impossible to resize cells.

import javax.swing.JPanel;
import javax.swing.JTextArea;

public class MapEditor extends JPanel {
    
    public static final int SCREEN_WIDTH=650;
	public static final int SCREEN_HEIGHT=500;
	
    public static final int MAP_WIDTH=35;
	public static final int MAP_HEIGHT=30;
	public static final int MAP_SCREEN_WIDTH=600;
	public static final int MAP_SCREEN_HEIGHT=600;
	
	public static final String[] tileOptions= {"blank wall ( )","wall (X)","floor (·)","tunnel (#)","door (+)",
											"down stairs (>)", "up stairs (<)"};
	public static final char[] tileIcons={' ',Level.WALL_ICON, Level.EMPTY_TILE_ICON,Level.TUNNEL_ICON,Level.CLOSED_DOOR_ICON,
		Level.DOWN_STAIRS_ICON, Level.UP_STAIRS_ICON};
	public static final String[] saveSlots={"0","1","2","3","4","5","6","7","8","9"};
	public static char currentIcon='X';
 
    public MapEditor() {

        super(new GridBagLayout());
 
        final EditorTableModel mapEditorModel=new EditorTableModel();
        final JTable mapGrid = new JTable(mapEditorModel);
        mapGrid.setPreferredScrollableViewportSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        mapGrid.setFillsViewportHeight(true);
        mapGrid.setEnabled(false);
 
        JScrollPane scrollPane = new JScrollPane(mapGrid);
        JTextArea instructions=new JTextArea("Welcome to the map editor! " +
        		"To add the currently selected tile, simply click and drag. \n" +
        		"To change the currently selected tile, use the drop-down menu" +
        		" below.");
        instructions.setWrapStyleWord(true);
        final JComboBox<String> tileMenu=new JComboBox<String>(tileOptions);
        final JComboBox<String> saveMenu=new JComboBox<String>(saveSlots);
        tileMenu.setSelectedIndex(2);
        saveMenu.setSelectedIndex(0);
        
        final JLabel saveSlotLabel=new JLabel("Save slot: ");
        final JButton saveButton=new JButton("Save map");
        
        saveButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent evt) {
				saveMap();
			}
			public void saveMap(){
			    	
			try{
				int fileIndex=saveMenu.getSelectedIndex();
			    FileOutputStream saveFile = new FileOutputStream("mapSave"+fileIndex+".sav");		//TODO: allow multiple files to be saved.
			    ObjectOutputStream save = new ObjectOutputStream(saveFile);			
			    save.writeObject(mapEditorModel.currentMap());
			    saveButton.setText("Map saved!");
			 } catch(IOException e){}
			}
        	
        });

        GridBagConstraints c = new GridBagConstraints();

        c.fill=GridBagConstraints.BOTH;
   	 	c.weightx=0.5;
   	 	c.weighty=0.12;
   	 	c.anchor=GridBagConstraints.NORTH;
   	 	c.gridwidth=4;
   	 	c.gridx = 0;
   	 	c.gridy = 0;
   	 	add(instructions,c);
        
   	 	c.fill=GridBagConstraints.HORIZONTAL;
   	 	c.weightx=0.5;
   	 	c.weighty=0.2;
   	 	c.anchor=GridBagConstraints.CENTER;
   	 	c.gridwidth=4;
   	 	c.gridx = 0;
   	 	c.gridy = 1;
   	 	add(scrollPane,c);

   	 	c.fill=GridBagConstraints.BOTH;
	 	c.weightx=0.9;
	 	c.weighty=0.2;
	 	c.anchor=GridBagConstraints.NORTH;
	 	c.gridwidth=1;
	 	c.gridx = 0;
	 	c.gridy = 2;
	 	add(tileMenu,c);
	 	
		c.fill=GridBagConstraints.NONE;
	 	c.weightx=0.4;
	 	c.weighty=0.2;
	 	c.anchor=GridBagConstraints.SOUTHEAST;
	 	c.gridx = 1;
	 	c.gridy = 2;
	 	add(saveSlotLabel,c);
	 	
	 	c.fill=GridBagConstraints.NONE;
	 	c.weightx=0.1;
	 	c.weighty=0.2;
	 	c.anchor=GridBagConstraints.SOUTHEAST;
	 	c.gridx = 2;
	 	c.gridy = 2;
	 	add(saveMenu,c);
	 	
	 	c.fill=GridBagConstraints.NONE;
	 	c.weightx=0.1;
	 	c.weighty=0.2;
	 	c.anchor=GridBagConstraints.SOUTHEAST;
	 	c.gridx = 3;
	 	c.gridy = 2;
	 	add(saveButton,c);
	 	
	 	saveMenu.setPreferredSize(new Dimension(50,20));
	 	saveButton.setPreferredSize(new Dimension(200,20));
	 	boolean mouseHeld=false;

	 	mapGrid.addMouseListener(new MouseAdapter() { 	
        public boolean mouseHeld=false;
        public boolean started=false;
        public ActionListener tileRefresher=new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent evt) {
			if(mouseHeld&&getMousePosition()!=null
					&&new Point(getMousePosition().x,getMousePosition().y-35)!=null)
				tileEvent(new Point(getMousePosition().x,getMousePosition().y-35));
			}
        };
        	
        public Timer tileTimer=new Timer(10, tileRefresher);
        public void start(){
        	if(!started){
        		tileTimer.start();
        		started=true;
        	}
        }
        	
		@Override
		public void mouseClicked(MouseEvent evt) {
		tileEvent(evt.getPoint());
		}
		    
		@Override
		public void mousePressed(MouseEvent evt){
		    start();
		    mouseHeld=true;
		    tileEvent(evt.getPoint());
		}
		@Override
		public void mouseReleased(MouseEvent evt) {
			mouseHeld=false;
		}
		@Override
		public void mouseEntered(MouseEvent evt) {
			if(mouseHeld)tileEvent(evt.getPoint());}
		@Override
		public void mouseExited(MouseEvent evt) {}
		   
			public void tileEvent(Point mousePoint) 
			{
				saveButton.setText("Save map");
		        int row = mapGrid.rowAtPoint(mousePoint);
		        int col = mapGrid.columnAtPoint(mousePoint);
		        if (row >= 0 && col >= 0) {
		           mapEditorModel.setIconAt(currentIcon(), row, col);
		           mapGrid.setFont(new Font("Courier New", Font.BOLD, 16));
		        }
		    }

			private char currentIcon() {
				return tileIcons[tileMenu.getSelectedIndex()];
			}	
		});
        
        mapGrid.setFont(new Font("Courier New", Font.PLAIN, 2));
        for(int i=0;i<MAP_HEIGHT;i++){
			for(int j=0;j<MAP_WIDTH;j++){
				mapEditorModel.setIconAt(' ',i,j);
			}
		}
    }
    
	public class EditorTableModel extends AbstractTableModel {
   
		private static final long serialVersionUID = 1L;
		private String[] columnNames = new String[MAP_WIDTH];
		int max=Math.max(MAP_WIDTH, MAP_HEIGHT);
        private char[][] data = new char[MAP_HEIGHT][MAP_WIDTH];
 
        public int getColumnCount() {
            return columnNames.length;
        }
 
        public int getRowCount() {
            return data.length;
        }
 
        public String getColumnName(int col) {
            return columnNames[col];
        }
 
        public Object getValueAt(int row, int col) {
        		return ""+data[row][col];
    
        }
 
        public boolean isCellEditable(int row, int col) {
        	return false;
        }
        
        public void setIconAt(char icon, int row, int col) {
            data[row][col] = icon;
            fireTableCellUpdated(row, col);
        }
        
        public String currentMap(){
	    	String map="";
	    	for(int i=0;i<MAP_HEIGHT;i++){
	    		for(int j=0;j<MAP_WIDTH;j++){
	        		map+=getValueAt(i,j);
	        	}
	    		map+="\n";
	    	}
	    	return map;
	    }
    }
 
    private static void createAndShowGUI() {

        JFrame frame = new JFrame("Map Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        MapEditor newContentPane = new MapEditor();
        newContentPane.setOpaque(true);
        frame.setContentPane(newContentPane);

        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }
    
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
