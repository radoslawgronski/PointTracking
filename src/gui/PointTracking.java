package gui;

import tools.*;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JSlider;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class PointTracking {

	private JFrame frame;
	private PointsPanel panel;
	private JLabel labelDescription;
	private JLabel labelTrackingPoints;
	private JLabel labelSlider;
	private JFormattedTextField textFieldTrackingPoints;
	private JButton buttonSet;
	private JSlider slider;
	private int numberOfPoints;
	private PointMove[] runnables;
	private Thread[] threads;

	public PointTracking() {
		initialize();
	}

	// Konstrukcja aplikacji.
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Wielowatkowe sledzenie punktu");
		frame.setBounds(100, 100, 800, 600);
		frame.setMinimumSize(new Dimension(600, 400));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		numberOfPoints = 3;
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.1, 0.1, 0.8};
		gridBagLayout.rowWeights = new double[]{0.1, 0.8, 0.05, 0.05};
		
		frame.getContentPane().setLayout(gridBagLayout);
		
		labelDescription = new JLabel("Kliknij na pole, by wystartowac/zatrzymac gre.");
		GridBagConstraints gbc_labelDescription = new GridBagConstraints();
			gbc_labelDescription.insets = new Insets(0, 0, 5, 0);
			gbc_labelDescription.gridx = 0;
			gbc_labelDescription.gridy = 0;
			gbc_labelDescription.gridwidth = 3;
		frame.getContentPane().add(labelDescription, gbc_labelDescription);
		
		labelTrackingPoints = new JLabel("Liczba punktow: ");
		GridBagConstraints gbc_labelTrackingPoints = new GridBagConstraints();
			gbc_labelTrackingPoints.insets = new Insets(5, 10, 0, 10);
			gbc_labelTrackingPoints.gridx = 0;
			gbc_labelTrackingPoints.gridy = 2;
		frame.getContentPane().add(labelTrackingPoints, gbc_labelTrackingPoints);
		
		labelSlider = new JLabel("Predkosc punktow: ");
		GridBagConstraints gbc_labelSlider = new GridBagConstraints();
			gbc_labelSlider.insets = new Insets(5, 10, 0, 0);
			gbc_labelSlider.gridx = 2;
			gbc_labelSlider.gridy = 2;
			gbc_labelSlider.anchor = GridBagConstraints.WEST;
		frame.getContentPane().add(labelSlider, gbc_labelSlider);

		textFieldTrackingPoints = new JFormattedTextField(numberOfPoints);
		GridBagConstraints gbc_textFieldTrackingPoints = new GridBagConstraints();
			gbc_textFieldTrackingPoints.insets = new Insets(0, 20, 0, 20);
			gbc_textFieldTrackingPoints.gridx = 0;
			gbc_textFieldTrackingPoints.gridy = 3;
			gbc_textFieldTrackingPoints.fill = GridBagConstraints.HORIZONTAL;
		frame.getContentPane().add(textFieldTrackingPoints, gbc_textFieldTrackingPoints);
		
		buttonSet = new JButton("Ustaw");
		GridBagConstraints gbc_buttonSet = new GridBagConstraints();
			gbc_buttonSet.insets = new Insets(0, 0, 0, 15);
			gbc_buttonSet.gridx = 1;
			gbc_buttonSet.gridy = 3;
			gbc_buttonSet.fill = GridBagConstraints.HORIZONTAL;
		frame.getContentPane().add(buttonSet, gbc_buttonSet);
		
		slider = new JSlider();
		GridBagConstraints gbc_slider = new GridBagConstraints();
			gbc_slider.insets = new Insets(0, 5, 0, 20);
			gbc_slider.gridx = 2;
			gbc_slider.gridy = 3;
			gbc_slider.fill = GridBagConstraints.HORIZONTAL;
		frame.getContentPane().add(slider, gbc_slider);
		
		setNumberOfPoints();
		GridBagConstraints gbc_panel = new GridBagConstraints();
			gbc_panel.insets = new Insets(0, 20, 0, 20);
			gbc_panel.gridx = 0;
			gbc_panel.gridy = 1;
			gbc_panel.gridwidth = 3;
			gbc_panel.fill = GridBagConstraints.BOTH;
		frame.getContentPane().add(panel, gbc_panel);
		
		panel.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				panelClicked();
			}
		});
		
		panel.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				panelMouseMoved();
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {}
		});
		
		buttonSet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setNumberOfPoints();
			}
		});
	}
	
	// Klikniece na panel - start/stop.
	private void panelClicked() {
		if (panel.isRunning()) {
			panel.stop();
		} else {
			panel.start();
		}
	}
	
	// Wprowadzenie zmian do pola okreslajacego liczbe punktow.
	private void setNumberOfPoints() {
		int value = (int) textFieldTrackingPoints.getValue();
		
		if(value < 1) {
			textFieldTrackingPoints.setValue(numberOfPoints);
		} else {
			numberOfPoints = value;
			if(panel == null) {
				panel = new PointsPanel(numberOfPoints, slider);
				newThreads(panel.getNumberOfPoints());		
				startThreads();
			} else {
				panel.reset(numberOfPoints);
				panel.repaint();
				killThreads();
				newThreads(numberOfPoints);		
				startThreads();
			}
		}
	}
	
	// Poruszanie kursorem nad panelem - przesuwanie glownego punktu i ponowne rysowanie.
	private void panelMouseMoved() {
		if(panel.isRunning()) {
			Point position = panel.getMousePosition();
			panel.setMainX((int) position.getX());
			panel.setMainY((int) position.getY());
			panel.repaint();
		}
	}
	
	// Startuje dzialanie watkow punktow sledzacych.
	private void startThreads() {
		if (threads != null) {
			for (int i = 0; i < threads.length; i++) {
				threads[i].start();
			}
		}
	}
	
	// Zabija watki punktow sledzacych.
	private void killThreads() {
		for (int i = 0; i < threads.length; i++) {
			try {
				runnables[i].terminate();
				threads[i].join();
			} catch (InterruptedException e) {
				JOptionPane.showMessageDialog(null, "Nie mozana zatrzymac watku.");
			}
		}
	}
	
	// Tworzy nowy zestaw watkow punktow sledzacych.
	private void newThreads(int numberOfThreads) {
		runnables = new PointMove[numberOfThreads];
		threads = new Thread[numberOfThreads];
		for (int i = 0; i < numberOfThreads; i++) {
			runnables[i] = new PointMove(panel, i);
			threads[i] = new Thread(runnables[i]);
		}
	}
	
	// Uruchomienie aplikacji.
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PointTracking window = new PointTracking();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
