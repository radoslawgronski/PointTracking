package tools;

public class PointMove implements Runnable {

	private PointsPanel panel;
	private int index;
	private volatile boolean running;
	private final int maxVelocity = 100;
	
	public PointMove(PointsPanel panel, int index) {
		this.panel = panel;
		
		if(index < 0 || index > panel.getNumberOfPoints()-1) {
			throw new IndexOutOfBoundsException();
		}
		
		this.index = index;
		running = true;
	}
	
	public void terminate() {
		running = false;
	}
	
	@Override
	public void run() {
		
		while (running) {
			if (panel.isRunning()) {
				synchronized(panel) {
					int x, y;
					int currentX = panel.getPointX(index);
					int currentY = panel.getPointY(index);
					int mainX = panel.getMainX();
					int mainY = panel.getMainY();
					
					int stepRange = (int) (((double) panel.sliderValue())/100 * maxVelocity);
					int xDistance = Math.abs(currentX - mainX);
					int yDistance = Math.abs(currentY - mainY);
					
					if (xDistance + yDistance <= stepRange) {
						x = mainX;
						y = mainY;
					} else {
						int xStep = (int) ((((double)xDistance)/(xDistance + yDistance) ) * stepRange);
						int yStep = stepRange - xStep;
						
						if (xStep == 0) {
							x = currentX;
						} else {
							if (mainX - currentX < 0) {
								x = currentX - xStep;
							} else {
								x = currentX + xStep;
							}
						}
						
						if (yStep == 0) {
							y = currentY;
						} else {
							if (mainY - currentY < 0) {
								y = currentY - yStep;
							} else {
								y = currentY + yStep;
							}
						}
					}
					
					panel.setPoint(index, x, y);
					panel.repaint();
				}
			}
			
			try {
				Thread.sleep(20);
			} catch(InterruptedException e) {}
		}
		
	}
	
}
