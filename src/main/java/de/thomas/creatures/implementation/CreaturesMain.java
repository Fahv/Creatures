package de.thomas.creatures.implementation;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;
import javax.swing.UIManager;

import de.thomas.creatures.implementation.controller.WorldController;
import de.thomas.creatures.implementation.factories.WorldFactory;
import de.thomas.creatures.implementation.model.Creature;
import de.thomas.creatures.implementation.model.WorldModel;
import de.thomas.creatures.implementation.statistics.Statistics;
import de.thomas.creatures.implementation.view.MainWindow;
import de.thomas.creatures.implementation.view.WorldInputListener;
import de.thomas.creatures.implementation.view.WorldView;

public class CreaturesMain implements WorldCreator, ActionListener {
	private Timer timer;
	private WorldModel worldModel;
	private WorldView view;
	private WorldController controller;
	private WorldInputListener listener;
	private Statistics statistics;
	private MainWindow mainWindow;
	int counter;
	
	private final int REFRESH_TIME = 15;
	
	private double lastTime;
	
	public CreaturesMain() {
		initProgram();
	}
	
	private void initProgram() {
		if (timer != null && timer.isRunning()) {
			timer.stop();
		}
		
		worldModel = WorldFactory.createBasicWorld(800, 600, 20, 50);
		
		
		view = new WorldView(worldModel);
		controller = new WorldController(worldModel, view);
		listener = new WorldInputListener(controller);
		statistics = new Statistics(worldModel);
		
		view.addKeyListener(listener);
		view.addMouseListener(listener);
		view.addMouseWheelListener(listener);
		
		mainWindow = new MainWindow(view, controller, statistics, this, worldModel);
		
		controller.setMainWindow(mainWindow);
		
		timer = new Timer(REFRESH_TIME, this);
		timer.start();
		lastTime = System.nanoTime();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		double currentTime = System.nanoTime();
		double delta = (System.nanoTime() - lastTime) / 1E9;
		lastTime = currentTime;
		
		
		if (WorldModel.speedFactor == 0) {
			return;
		}
		
		for (Creature c : worldModel.getCreatures()) {
			c.update();
		}
		
		mainWindow.update(delta);
		
		
		controller.updateWorld(delta);
		listener.handlePressedKeys();
		listener.handlePressedMouseButtons(view);
		view.repaint();
		
		statistics.update(delta);
		//view.paintImmediately(0, 0, (int) (1366 * 0.8), 768);
	}
	
	public static void main(String[] args) {
		setLookAndFeel();
		
		new CreaturesMain();
	}

	private static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void setupWorld(WorldModel worldModel) {
		if (timer != null && timer.isRunning()) {
			timer.stop();
		}
		
		this.worldModel = worldModel;
		
		
		view = new WorldView(worldModel);
		controller = new WorldController(worldModel, view);
		listener = new WorldInputListener(controller);
		statistics = new Statistics(worldModel);
		
		view.addKeyListener(listener);
		view.addMouseListener(listener);
		view.addMouseWheelListener(listener);
		
		mainWindow.setView(view);
		mainWindow.setController(controller);
		mainWindow.setStatistics(statistics);
		mainWindow.setWorldCreator(this);
		mainWindow.setWorldModel(worldModel);
		
		controller.setMainWindow(mainWindow);
		
		timer = new Timer(REFRESH_TIME, this);
		timer.start();
		lastTime = System.nanoTime();
	}
}