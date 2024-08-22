import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.tilesources.AbstractOsmTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

public class Driver {
	
	// Declare class data
	public JFrame frame;
	public JPanel panel;
	public JButton playButton;
	public JCheckBox checkBox;
	public JComboBox comboBox;
	static ArrayList<TripPoint> trip;
	static ArrayList<TripPoint> movingTrip;
	public Integer seconds = 60;
	public Boolean includeStops = false;
	public JMapViewer mapViewer;
	public Timer timer;
	public ArrayList<Coordinate> coords;
	public int timerCounter = 0;
	public ImageIcon raccoon = new ImageIcon("raccoon.png");
	public MapPolygonImpl lineSegment;

    public static void main(String[] args) throws FileNotFoundException, IOException {
    	
    	TripPoint.readFile("triplog.csv");
    	TripPoint.h1StopDetection();
    	TripPoint.h2StopDetection();
    	
    	trip = TripPoint.getTrip();
    	movingTrip = TripPoint.getMovingTrip();
    	
    	new Driver();
    	
    }

    Driver(){
    	//Create frame and set values
    	frame = new JFrame("Project 5 - Yale Gray");
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setSize(1920,1080);
    	
        panel = new JPanel();
        panel.setVisible(true);
    	
        playButton = new JButton("Play");
        playButton.addActionListener(new ButtonListener());

        checkBox = new JCheckBox("Include Stops");
        checkBox.addActionListener(new CheckBoxListener());
        
        Integer[] times = {15,30,60,90};
        comboBox = new JComboBox(times);
        comboBox.addActionListener(new ComboBoxListener());
        
        panel.add(playButton);
        panel.add(checkBox);
        panel.add(comboBox);
        
        mapViewer = new JMapViewer();
        mapViewer.setTileSource(new OsmTileSource.TransportMap());
        mapViewer.setDisplayPosition(new Coordinate(trip.get(0).getLat(), trip.get(0).getLon()), 10);
        frame.add(mapViewer);

        frame.add(panel, BorderLayout.NORTH);
        frame.setVisible(true);
        
    }
    
    class ButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(timer != null) {
				timer.stop();
			}
			
			includeStops = checkBox.isSelected();
			seconds = (Integer)comboBox.getSelectedItem();
			
			mapViewer.removeAllMapMarkers();
			mapViewer.removeAllMapPolygons();
			
			coords = new ArrayList<Coordinate>();
			
			if(includeStops) {
				for(TripPoint t: trip) {
					coords.add(new Coordinate(t.getLat(), t.getLon()));
				}
				timer = new Timer((seconds * 1000)/trip.size(), new TimerListener());
			}
			else {
				for(TripPoint t: movingTrip) {
					coords.add(new Coordinate(t.getLat(), t.getLon()));
				}
				timer = new Timer((seconds * 1000)/movingTrip.size(), new TimerListener());
			}
			
			timer.start();
			
			timerCounter = 0;
		}
    	
    }
    
    class ComboBoxListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			Integer secondsRun = (Integer) comboBox.getSelectedItem();
			
			switch(secondsRun) {
			case 15:
				seconds = secondsRun;
			case 30:
				seconds = secondsRun;
			case 60:
				seconds = secondsRun;
			case 90:
				seconds = secondsRun;
			}
		}
    	
    }
    
    class CheckBoxListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			includeStops = checkBox.isSelected();
		}
    	
    }
    
    class TimerListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(timerCounter < coords.size()) {
				if(timerCounter == 0) {
					mapViewer.addMapMarker(new IconMarker(coords.get(0), raccoon.getImage()));
				}
				else {
					mapViewer.removeAllMapMarkers();
					mapViewer.addMapMarker(new IconMarker(coords.get(timerCounter), raccoon.getImage()));
					
					Coordinate previous = coords.get(timerCounter - 1);
					Coordinate current = coords.get(timerCounter);
					
					ArrayList<Coordinate> segment = new ArrayList<Coordinate>();
					segment.add(previous);
					segment.add(current);
					segment.add(current);
					
					//lineSegment = new MapPolygonImpl(previous, current);
					lineSegment = new MapPolygonImpl(segment);
					lineSegment.setVisible(true);
					mapViewer.addMapPolygon(lineSegment);
				}
				++timerCounter;
			}
			else {
				timer.stop();
			}
		}
    	
    }
    
}