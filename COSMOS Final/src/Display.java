import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;


public class Display extends JComponent implements MouseListener, MouseMotionListener {
	private final int DISPLAY_WIDTH;   
	private final int DISPLAY_HEIGHT;
	
	private int buttonLength = 100; //variable names for button parameters
	private int buttonHeight = 36;
	
	private int xOffset = 550; //offsets for display purposes
	private int yOffset = 75;
	private int bottomOffset = 200;
	private ArrayList<Fish> fish = new ArrayList<Fish>(); //list of all fish
	private ArrayList<Fish> sharks = new ArrayList<Fish>(); //list of all sharks
	
	private ToggleButton switchSpawn; //spawn sharks vs spawn fish
	private StartButton startStop; //pause
	private ClearButton clear; //clears screen
	private FishButton fButton;	//how many to spawn
	private StartSimulationButton startSimulation; //starts a simulation with 200 fish and a shark with certain settings
	private ResetButton reset; //default settings
	
	private boolean loop = true;
	private int spawnMode = 0; //0 is fish, 1 is shark
	private int numToSpawn = 1;
	
	private int timeCounter = 0;
	private int radius = 300;
	private long delay = 5; //milliseconds
	
	private boolean printTime = false; 
	private boolean spawning = false;
	
	//PARAMETERS
	private double a = 0.6; //fish to fish attraction
	private double b = 3000; //fish to shark repulsion
	private double r = 1000; //fish to fish repulsion
	private double c = 0.4; //shark strength
	private double sharkSpeed = 8; //shark speed
	private double fishSpeed = 5; //fish speed
	private double p = 3; //p
	private double reboundScalar = 0.005; //repulsion for boundaries
	
	//Sliders for all values so they can be edited in the simulation
	JSlider aSlider = new JSlider(JSlider.HORIZONTAL,
            0, 200, (int)(100*a));
	JSlider bSlider = new JSlider(JSlider.HORIZONTAL,
            0, 500, (int)(b/10));
	JSlider rSlider = new JSlider(JSlider.HORIZONTAL,
            0, 200, (int)(r/10));
	JSlider sharkSlider = new JSlider(JSlider.HORIZONTAL,
            0, 20, (int)sharkSpeed);
	JSlider pSlider = new JSlider(JSlider.HORIZONTAL,
            0, 8, (int)p);
	
	public Display(int width, int height) {
		DISPLAY_WIDTH = width;    // Width, height initialized
		DISPLAY_HEIGHT = height;
		init();
	}

	public void init() {
		setSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
		addMouseListener(this);
		addMouseMotionListener(this);	
		
		//displaying all parameters
		
		aSlider.setMajorTickSpacing(20);
		aSlider.setMinorTickSpacing(10);
		aSlider.setPaintTicks(true);
		aSlider.setPaintLabels(true);
		aSlider.setBounds(0, 100, 450, 36);
		add(aSlider);
		//10*
		
		
		bSlider.setMajorTickSpacing(50);
		bSlider.setMinorTickSpacing(25);
		bSlider.setPaintTicks(true);
		bSlider.setPaintLabels(true);
		bSlider.setBounds(0, 300, 450, 36);
		add(bSlider);
		// /10
		
		
		rSlider.setMajorTickSpacing(20);
		rSlider.setMinorTickSpacing(10);
		rSlider.setPaintTicks(true);
		rSlider.setPaintLabels(true);
		rSlider.setBounds(0, 200, 450, 36);
		add(rSlider);
		// /10
		
		
		sharkSlider.setMajorTickSpacing(2);
		//sharkSlider.setMinorTickSpacing(10);
		sharkSlider.setPaintTicks(true);
		sharkSlider.setPaintLabels(true);
		sharkSlider.setBounds(0, 400, 450, 36);
		add(sharkSlider);
		
		
		pSlider.setMajorTickSpacing(1);
		//sharkSlider.setMinorTickSpacing(10);
		pSlider.setPaintTicks(true);
		pSlider.setPaintLabels(true);
		pSlider.setBounds(0, 500, 450, 36);
		add(pSlider);
		
		//displaying all buttons
		
		startStop = new StartButton();
		startStop.setBounds(0, (DISPLAY_HEIGHT - bottomOffset+buttonHeight), buttonLength, buttonHeight);
		add(startStop);
		startStop.setVisible(true);
		
		switchSpawn = new ToggleButton();
		switchSpawn.setBounds(2*buttonLength, (DISPLAY_HEIGHT - bottomOffset+buttonHeight), 2*buttonLength, buttonHeight);
		add(switchSpawn);
		switchSpawn.setVisible(true);
	
		clear = new ClearButton();
		clear.setBounds(buttonLength, (DISPLAY_HEIGHT - bottomOffset+buttonHeight), buttonLength, buttonHeight);
		add(clear);
		clear.setVisible(true);
		
		fButton = new FishButton();
		fButton.setBounds(4*buttonLength, (DISPLAY_HEIGHT - bottomOffset+buttonHeight), buttonLength, buttonHeight);
		add(fButton);
		fButton.setVisible(true);
		
		startSimulation = new StartSimulationButton();
		startSimulation.setBounds(0, (DISPLAY_HEIGHT - bottomOffset+buttonHeight)+buttonHeight, 5*buttonLength, buttonHeight);
		add(startSimulation);
		startSimulation.setVisible(true);
		
		reset = new ResetButton();
		reset.setBounds(0, (DISPLAY_HEIGHT - bottomOffset-buttonHeight), 5*buttonLength, buttonHeight);
		add(reset);
		reset.setVisible(true);
		
		//getData();
		repaint();
	}
	/* This was for my project - I wanted to collect data to graph how changing parameters affected the lifetime of the fish
	public void getData() {
		a = 0;
		for (int i = 0; i < 100; i++) {
			fish.add(new Fish(new Point(xOffset+radius+(i/2)-20, yOffset+radius+i-40), fishSpeed));
		}
		for (int i = 0; i < 100; i++) {
			fish.add(new Fish(new Point(xOffset+radius-(i/2)-20, yOffset+radius+i-40), fishSpeed));
		}
		spawning = true;
		timeCounter = 0;
	}
	
	public void runSimulation() {
		if (a == 2) System.exit(0);
		else a = a+0.1;
		fish.removeAll(fish);
		sharks.removeAll(sharks);
		for (int i = 0; i < 100; i++) {
			fish.add(new Fish(new Point(xOffset+radius+(i/2)-20, yOffset+radius+i-40), fishSpeed));
		}
		for (int i = 0; i < 100; i++) {
			fish.add(new Fish(new Point(xOffset+radius-(i/2)-20, yOffset+radius+i-40), fishSpeed));
		}
		spawning = true;
		timeCounter = 0;
	}
	*/
	public void paintComponent(Graphics g) {	
		/* This was also for collecting data
		if (fish.size() == 0) {
			double time = (timeCounter/delay)/10.0;
			System.out.println("a: " + a + "  r: " + r + "  b: " + b + "  speed: " + sharkSpeed + "  p: " + p + "\t\t" + time + " seconds" );
			runSimulation();
		}
		*/
		parameterTesting();
		registerValues();
		drawBackground(g);
		drawFish(g);
		drawSharks(g);
		drawLabels(g);
		g.drawOval(xOffset-40, yOffset-40, 2*radius+80, 2*radius+80);
		drawButtons();

		if (loop) {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			moveFish();
			moveSharks();
			checkCollisions();
			repaint();
		}
	}

	private void parameterTesting() { //this is also for testing
		if (spawning) timeCounter++;
		if(timeCounter == 50 && spawning) {
			sharks.add(new Fish(new Point(xOffset+radius, (int)(yOffset+1.5*radius)), sharkSpeed));
		}
		if(fish.size() != 0) {
			printTime = true;
		}
		if (fish.size() == 0 && printTime) {
			//double time = (timeCounter/delay)/10.0;
			double time = (timeCounter/delay)/5.0;
			System.out.println("a: " + a + "  r: " + r + "  b: " + b + "  speed: " + sharkSpeed + "  p: " + p + "\t\t" + time + " seconds" );
			printTime = false;
		}
	}
	
	private void registerValues() { //updates values to reflect sliders
		a = (aSlider.getValue()/100.0);
		b = bSlider.getValue()*10.0;
		r = rSlider.getValue()*10.0;
		sharkSpeed = sharkSlider.getValue();
		for (Fish s : sharks) {
			s.setSpeed((int)sharkSpeed);
		}
		p = pSlider.getValue();
		
	}
	
	private void drawBackground(Graphics g) { //draws a background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT);
	}
	
	private void drawFish(Graphics g) { //this method draws all of the fish on screen
		Graphics2D g2 = (Graphics2D) g;
		for (Fish f : fish) {
			//size of fish
			int length = 3;
            g2.setStroke(new BasicStroke(1));
			
			double theta;
			if (f.getChangeX() == 0) theta = 0;
			else {
				theta = Math.atan(-1*f.getChangeY()/f.getChangeX());
				if (f.getChangeX() < 0) theta += Math.PI;
				theta = -1*theta + Math.PI/2;
			}
			
			//it is quite long because I want to draw a diamond shape, and there are no defaults for that

            g2.setStroke(new BasicStroke(1));       
            
            Point[] store = new Point[4];
            Point center = new Point(f.getX(), f.getY());
            
            
            Point[] toRotate = new Point[4];    
            toRotate[0] = new Point(f.getX()-length, f.getY()); toRotate[1] = new Point(f.getX(), f.getY()-length);
            toRotate[2] = new Point(f.getX()+length, f.getY()); toRotate[3] = new Point(f.getX(), f.getY()+2*length);
            
            for (int i = 0; i < 4; i++) {
            	store[i] = rotatePoint(toRotate[i], center, theta);
            }
            
            f.setShape(polygonize(store));
			g2.setColor(Color.WHITE);
            g2.drawPolygon(polygonize(store));
			g2.setColor(Color.BLACK);
			g2.fillPolygon(polygonize(store));
		}
	}
	
	
	private void drawSharks(Graphics g) { //this method draws the sharks - same as fish
		Graphics2D g2 = (Graphics2D) g;
		for (Fish s : sharks) {
			int length = 10;
			
			double theta;
			if (s.getChangeX() == 0) theta = 0;
			else {
				theta = Math.atan(-1*s.getChangeY()/s.getChangeX());
				if (s.getChangeX() < 0) theta += Math.PI;
				theta = -1*theta + Math.PI/2;
				
			}

            g2.setStroke(new BasicStroke(1));       
            
            Point[] store = new Point[4];
            Point center = new Point(s.getX(), s.getY());
            
            
            Point[] toRotate = new Point[4];    
            toRotate[0] = new Point(s.getX()-length, s.getY()); toRotate[1] = new Point(s.getX(), s.getY()-length);
            toRotate[2] = new Point(s.getX()+length, s.getY()); toRotate[3] = new Point(s.getX(), s.getY()+2*length);
            
            for (int i = 0; i < 4; i++) {
            	store[i] = rotatePoint(toRotate[i], center, theta);
            }
            
            s.setShape(polygonize(store));
			g2.setColor(Color.WHITE);
            g2.drawPolygon(polygonize(store));
			g2.setColor(Color.RED);
			g2.fillPolygon(polygonize(store));
		}
	}
	
	public Point rotatePoint(Point pt, Point center, double theta) 
	//this method is used for creating the diamond shape (more difficult than it should be)
	{
	    double cosAngle = Math.cos(theta);
	    double sinAngle = Math.sin(theta);
	    int x = center.x + (int) (0.5 +(pt.x-center.x)*cosAngle-(pt.y-center.y)*sinAngle);
	    int y = center.y + (int) (0.5 +(pt.x-center.x)*sinAngle+(pt.y-center.y)*cosAngle);
	    return new Point(x, y);
	}
	
	public Polygon polygonize(Point[] polyPoints){
        //a simple method that makes a new polygon out of the rotated points - also for diamond
        Polygon tempPoly = new Polygon();

         for(int  i=0; i < polyPoints.length; i++){
             tempPoly.addPoint(polyPoints[i].x, polyPoints[i].y);
        }
        return tempPoly;
    }
	
	private void drawLabels(Graphics g) {
		//draws all of the labels that are seen on screen
		g.setColor(Color.BLACK);
		//g.setFont(new Font("Times New Roman", Font.BOLD, 16));
		g.drawString("Fish Population: " + fish.size(), DISPLAY_WIDTH-150, 20);
		g.drawString("Shark Population: " + sharks.size(), DISPLAY_WIDTH-150, 40);
		Font currentFont = g.getFont();
		g.drawString("a: fish to fish attraction", 15, 85);
		g.drawString("r: fish to fish repulsion", 15, 185);
		g.drawString("b: fish to shark repulsion", 15, 285);
		g.drawString("speed of shark", 15, 385);
		g.drawString("p: shark's sensitivity to distance (strength)", 15, 485);
		Font newFont = currentFont.deriveFont(currentFont.getSize() * 3F);
		g.setFont(newFont);
		g.drawString("Swarm Simulation", 10, 40);
	}
	
	private void drawButtons() { //updates buttons
		startStop.repaint();
		switchSpawn.repaint();
		clear.repaint();
		fButton.repaint();	}
	
	private void moveFish() { //uses differential equations from a model to update the position of fish
		for (int j = 0; j < fish.size(); j++) {
			double xCounter = 0;
			double yCounter = 0;
			
			//Fish to fish attraction + repulsion
			for (int k = 0; k < fish.size(); k++) {
				if (j == k) continue;
				if (squareDistance(fish.get(j),fish.get(k)) == 0) {
					continue;
				}
				xCounter += (r/(squareDistance(fish.get(j),fish.get(k)))-a)*(fish.get(j).getX() - fish.get(k).getX());
				yCounter += (r/(squareDistance(fish.get(j),fish.get(k)))-a)*(fish.get(j).getY() - fish.get(k).getY());
			}
			xCounter /= fish.size(); yCounter /= fish.size();
			
			//Fish to shark repulsion
			for (int k = 0; k < sharks.size(); k++) {
				xCounter += b*(fish.get(j).getX()-sharks.get(k).getX())/(squareDistance(fish.get(j), sharks.get(k)));
				yCounter += b*(fish.get(j).getY()-sharks.get(k).getY())/(squareDistance(fish.get(j), sharks.get(k)));
			}
			
			//Staying inside boundaries
			Point fishLocation = new Point(fish.get(j).getX(), fish.get(j).getY());
			Point centerOfCircle = new Point(xOffset+radius, yOffset+radius);
			if (distance2(fishLocation, centerOfCircle) > radius) {
				int xDif = (int)(fishLocation.getX()-centerOfCircle.getX());
				int yDif = (int)(fishLocation.getY()-centerOfCircle.getY());
				double scalar = reboundEquation(distance2(fishLocation, centerOfCircle) - radius);
				xCounter -= xDif*scalar;
				yCounter -= yDif*scalar;
			}
			
			fish.get(j).setVel(xCounter, yCounter);	
		}

		for (Fish f : fish) {
			f.move();
		}
		
	}
	//methods designed to make typing easier
	private double squareDistance(Fish j, Fish k) {
		return (Math.pow((j.getX()-k.getX()), 2.0) + Math.pow((j.getY()-k.getY()), 2));
	}
	private double distance(Fish j, Fish k) {
		return Math.sqrt(Math.pow((j.getX()-k.getX()), 2.0) + Math.pow((j.getY()-k.getY()), 2));
	}
	private double distance2(Point j, Point k) {
		return Math.sqrt(Math.pow((j.getX()-k.getX()), 2.0) + Math.pow((j.getY()-k.getY()), 2));
	}
	
	private void moveSharks() { //same thing here as with fish
		for (Fish s: sharks) {
			double xCounter = 0;
			double yCounter = 0;
			for (Fish f : fish) {
				xCounter += (f.getX()-s.getX())/(Math.pow(distance(f, s), p));
				yCounter += (f.getY()-s.getY())/(Math.pow(distance(f, s), p));
			}
			//Staying inside boundaries
			Point sharkLocation = new Point(s.getX(), s.getY());
			Point centerOfCircle = new Point(xOffset+radius, yOffset+radius);
			if (distance2(sharkLocation, centerOfCircle) > (radius+40)) {
				int xDif = (int)(sharkLocation.getX()-centerOfCircle.getX());
				int yDif = (int)(sharkLocation.getY()-centerOfCircle.getY());
				double scalar = reboundEquation(distance2(sharkLocation, centerOfCircle) - radius);
				xCounter -= xDif*scalar;
				yCounter -= yDif*scalar;
			}
			xCounter = xCounter*c/(fish.size());
			yCounter = yCounter*c/(fish.size());
			s.setVel(xCounter, yCounter);
		}
		
		
		for (Fish s : sharks) {
			s.move();
		}
	}

	public void checkCollisions() { //if a fish touches a shark, the fish must die
		for (Fish s : sharks) {
			for (int i = 0; i < fish.size(); i++) {
				if (testIntersection(s.getShape(), fish.get(i).getShape())) {
					fish.remove(i);
					i--;
				}
			}
		}
	}
	
	public boolean testIntersection(Shape shapeA, Shape shapeB) { //used in checkCollisions
		   Area areaA = new Area(shapeA);
		   areaA.intersect(new Area(shapeB));
		   return !areaA.isEmpty();
	}
	
	public double reboundEquation(double x) { //This is the method that keeps sharks within the boundaries with a rebound
		return Math.pow(x, 1.2)*reboundScalar;
		//return (int)(Math.pow(x, 2));
	}
	
	
	// mouseClicked, mouseEntered, mouseExited all unused, but need to
	// be present because of MouseListener interface
	public void mouseClicked(MouseEvent e) { 
		
	}
	public void mouseEntered(MouseEvent arg0) {	}
	public void mouseExited(MouseEvent arg0) { }

	public void mousePressed(MouseEvent arg0) { 
	}

	public void mouseReleased(MouseEvent e) { //These methods determine spawning of fish/sharks
		if (loop) {
			Point p = e.getPoint();
			if (spawnMode == 0) {
				//spawn Fish
				for (int i = 0; i < numToSpawn; i++) {
					fish.add(new Fish (new Point((int)p.getX()+i, (int)p.getY()+i), fishSpeed));
					
				}
			}
			else {
				for (int i = 0; i < numToSpawn; i++) {
					//spawn Sharks
					sharks.add(new Fish(new Point((int)p.getX()+i, (int)p.getY()+i), sharkSpeed));
				}
			}
		}
		
	}
	
	public void mouseDragged(MouseEvent e) { //spawns many fish while dragging
		if (loop) {
			Point p = e.getPoint();
			if (spawnMode == 0) {
				//spawn Fish
				for (int i = 0; i < numToSpawn; i++) {
					fish.add(new Fish (new Point((int)p.getX()+i, (int)p.getY()+i), fishSpeed));
				}
				
			}
			else {
				for (int i = 0; i < numToSpawn; i++) {
					sharks.add(new Fish(new Point((int)p.getX()+i, (int)p.getY()+i), sharkSpeed));
				}		
			}
		}
		
	}


	public void mouseMoved(MouseEvent arg0) {
		
	}
	
	private class ToggleButton extends JButton implements ActionListener { //switches whether spawning sharks or fish
		ToggleButton() {
			super("Spawning Fish");
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent arg0) {
			if (this.getText().equals("Spawning Fish")) {
				spawnMode = 1;
				setText("Spawning Sharks");
				fButton.setText("Spawning 1");
				numToSpawn = 1;
			} else {
				spawnMode = 0;
				setText("Spawning Fish");
				fButton.setText("Spawning 1");
				numToSpawn = 1;
			}
			repaint();
		}
	}
	
	public void toggleLoop() {
		loop = !loop;
	}

	
	private class StartButton extends JButton implements ActionListener {
		StartButton() {
			super("Pause");
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent arg0) {
			// nextGeneration(); // test the start button
			if (this.getText().equals("Pause")) {
				toggleLoop();
				setText("Unpause");
			} else {
				toggleLoop();
				setText("Pause");
			}
			repaint();
		}
	}
	
	private class ClearButton extends JButton implements ActionListener { //clears everything
		ClearButton() {
			super("Clear");
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent arg0) {
			fish.removeAll(fish);
			sharks.removeAll(sharks);
			repaint();
		}
	}
	
	private class FishButton extends JButton implements ActionListener { //how many to spawn
		FishButton() {
			super("Spawning 1");
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent arg0) {
			switch(this.getText()) {
			case "Spawning 1":
				numToSpawn = 10;
				setText("Spawning 10");
				break;
			case "Spawning 10":
				numToSpawn = 1;
				setText("Spawning 1");
				break;
			}
			repaint();
		}
	}
	
	private class StartSimulationButton extends JButton implements ActionListener { //creates a default test setting with certain conditions
		StartSimulationButton() {
			super("Start 200 Fish Simulation");
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent arg0) { //clears everything, spawns many -- shark is spawned after certain time
			fish.removeAll(fish);
			sharks.removeAll(sharks);
			for (int i = 0; i < 100; i++) {
				fish.add(new Fish(new Point(xOffset+radius+(i/2)-20, yOffset+radius+i-40), fishSpeed));
			}
			for (int i = 0; i < 100; i++) {
				fish.add(new Fish(new Point(xOffset+radius-(i/2)-20, yOffset+radius+i-40), fishSpeed));
			}
			spawning = true;
			timeCounter = 0;
			repaint();
		}
	}
	
	private class ResetButton extends JButton implements ActionListener {
		ResetButton() {
			super("Reset to Default Parameters");
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent arg0) { //updates all sliders to reflect values
			a = 0.6; //fish to fish attraction
			aSlider.setValue((int)(100*a));
			b = 3000; //fish to shark repulsion
			bSlider.setValue((int)(b/10));
			r = 1000; //fish to fish repulsion
			rSlider.setValue((int)(r/10));
			c = 0.4; //shark strength
			sharkSpeed = 8; //shark speed
			sharkSlider.setValue((int)sharkSpeed);
			fishSpeed = 5; //fish speed
			p = 3; //p
			pSlider.setValue((int)p);
			repaint();
		}
	}
	
}
