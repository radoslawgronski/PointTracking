package tools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class PointsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private int diameter;     // srednica rysowanych kol
	private int[][] points;   // koordynaty (x, y) punktow sledzacych
	private int[] mainPoint;  // koordynaty (x, y) glownego punktu
	private boolean start;
	private JSlider slider;

	public PointsPanel(int numberOfPoints, JSlider slider) {
		diameter = 30;
		this.slider = slider;
		reset(numberOfPoints);
	}
	
	public void reset(int numberOfPoints) {
		if(numberOfPoints < 1) {
			numberOfPoints = 1;
		}
		
		start = false;
		mainPoint = new int[2];
		points = new int[numberOfPoints][2];
		
		mainPoint[0] = -1;
		mainPoint[1] = -1;
	}
	
	// Zwraca wartosc suwaka.
	public int sliderValue() {
		return slider.getValue();
	}
	
	// Zwraca liczbe punktow sledzacych.
	public int getNumberOfPoints() {
		return points.length;
	}
	
	// Zwraca wspolrzedna X punktu o wskazanym indeksie. Jesli pozadany indeks nie istnieje, zwraca -1.
	public int getPointX(int index) {
		if(index < 0 && index > points.length-1) {
			return -1;
		} else {
			return points[index][0];
		}
	}
	
	// Zwraca wspolrzedna Y punktu o wskazanym indeksie. Jesli pozadany indeks nie istnieje, zwraca -1.
	public int getPointY(int index) {
		if(index < 0 && index > points.length-1) {
			return -1;
		} else {
			return points[index][1];
		}
	}
	
	// Zwraca wspolrzedna X glownego punktu. Jesli glowny punkt nie istnieje, zwraca -1.
	public int getMainX() {
		if(mainPoint == null || mainPoint.length != 2) {
			return -1;
		} else {
			return mainPoint[0];
		}
	}
	
	// Zwraca wspolrzedna Y glownego punktu. Jesli glowny punkt nie istnieje, zwraca -1.
	public int getMainY() {
		if(mainPoint == null || mainPoint.length != 2) {
			return -1;
		} else {
			return mainPoint[1];
		}
	}
	
	// Ustawia wspolrzedna X punktu o pozadanym indeksie, jesli indeks istnieje.
	// Jesli wartosc wykracza poza granice panelu, ustawia ja na granicy.
	public void setPointX(int index, int value) {
		if(index >= 0 && index < points.length) {
			if(value < 0) {
				value = 0;
			} else if(value > getWidth()-diameter) {
				value = getWidth()-diameter;
			}
			
			points[index][0] = value;
		}
	}
	
	// Ustawia wspolrzedna Y punktu o pozadanym indeksie, jesli indeks istnieje.
	// Jesli wartosc wykracza poza granice panelu, ustawia ja na granicy.
	public void setPointY(int index, int value) {
		if(index >= 0 && index < points.length) {
			if(value < 0) {
				value = 0;
			} else if(value > getWidth()-diameter) {
				value = getWidth()-diameter;
			}
			
			points[index][1] = value;
		}
	}
	
	// Ustawia wspolrzedna X glownego punktu.
	// Jesli wartosc wykracza poza granice panelu, ustawia ja na granicy.
	public void setMainX(int value) {
		if(value < 0) {
			value = 0;
		} else if(value > getWidth()-diameter) {
			value = getWidth()-diameter;
		}
		
		mainPoint[0] = value;
	}
	
	// Ustawia wspolrzedna Y glownego punktu.
	// Jesli wartosc wykracza poza granice panelu, ustawia ja na granicy.
	public void setMainY(int value) {
		if(value < 0) {
			value = 0;
		} else if(value > getWidth()-diameter) {
			value = getWidth()-diameter;
		}
		
		mainPoint[1] = value;
	}
	
	// Ustawia wspolrzedne X i Y punktu o pozadanym indeksie, jesli indeks istnieje.
	// Jesli wartosci wykraczaja poza granice panelu, ustawia ja na granicy.
	// Jesli punkt kolo mialoby nachodzic na kolo glowne, koryguje pozycje.
	public void setPoint(int index, int valueX, int valueY) {
		if(index >= 0 && index < points.length && mainPoint[0] >= 0) {
			if(valueX < 0) {
				valueX = 0;
			} else if(valueX > getWidth()-diameter) {
				valueX = getWidth()-diameter;
			}
			
			if(valueY < 0) {
				valueY = 0;
			} else if(valueY > getWidth()-diameter) {
				valueY = getWidth()-diameter;
			}
			
			double distancePower = Math.pow(mainPoint[0] - valueX, 2) + Math.pow(mainPoint[1] - valueY, 2);
			double diameterPower = Math.pow(diameter, 2);
			
			int position; // stara pozycja punktu wzgledem glownego punktu (0 lewo, 1 prawo, 2 ponad, 3 ponizej)
			
			if(points[index][0] == mainPoint[0]) {
				if(points[index][1] >= mainPoint[1]) {
					position = 2;
				} else {
					position = 3;
				}
			} else {
				if(points[index][0] < mainPoint[0]) {
					position = 0;
				} else {
					position = 1;
				}
			}
			
			// Jesli punkt nachodzilby na glowny punkt.
			if(distancePower <= diameterPower) {
				// Obliczanie parametrow prostej pomiedzy stara pozycja
				// a punktem glownym.
				double a = (mainPoint[1]-points[index][1])/((double) mainPoint[0]-points[index][0]);
				double b = mainPoint[1] - (a*mainPoint[0]);
				
				// Dopoki punkt nachodzilby na glowny punkt.
				while(distancePower <= diameterPower) {
					if(position < 2) {
						if(position == 1) {
							valueX++;
						} else {
							valueX--;
						}
						valueY = (int) (a*valueX + b);
					} else {
						if(position == 2) {
							valueY++;
						} else {
							valueY--;
						}
					}
					
					distancePower = Math.pow(mainPoint[0] - valueX, 2) + Math.pow(mainPoint[1] - valueY, 2);
				}
			}
			
			// Powtorzenie procesu procesu unikania nachodzenia na siebie dla innych punktow.
			for(int i = 0; i < points.length; i++) {
				if(i != index) {
					distancePower = Math.pow(points[i][0] - valueX, 2) + Math.pow(points[i][1] - valueY, 2);
					
					if(distancePower <= diameterPower) {
						double a = (points[i][1]-points[index][1])/((double) points[i][0]-points[index][0]);
						double b = points[i][1] - (a*points[i][0]);
						
						while(distancePower <= diameterPower) {
							if(position < 2) {
								if(position == 1) {
									valueX++;
								} else {
									valueX--;
								}
								valueY = (int) (a*valueX + b);
							} else {
								if(position == 2) {
									valueY++;
								} else {
									valueY--;
								}
							}
							
							distancePower = Math.pow(points[i][0] - valueX, 2) + Math.pow(points[i][1] - valueY, 2);
						}
					}
				}
			}
			
			distancePower = Math.pow(mainPoint[0] - valueX, 2) + Math.pow(mainPoint[1] - valueY, 2);
			
			// Jesli punkt nachodzilby na glowny punkt.
			if(distancePower <= diameterPower) {
				// Obliczanie parametrow prostej pomiedzy stara pozycja
				// a punktem glownym.
				double a = (mainPoint[1]-points[index][1])/((double) mainPoint[0]-points[index][0]);
				double b = mainPoint[1] - (a*mainPoint[0]);
				
				// Dopoki punkt nachodzilby na glowny punkt.
				while(distancePower <= diameterPower) {
					if(position < 2) {
						if(position == 1) {
							valueX++;
						} else {
							valueX--;
						}
						valueY = (int) (a*valueX + b);
					} else {
						if(position == 2) {
							valueY++;
						} else {
							valueY--;
						}
					}
					
					distancePower = Math.pow(mainPoint[0] - valueX, 2) + Math.pow(mainPoint[1] - valueY, 2);
				}
			}
			
			points[index][0] = valueX;
			points[index][1] = valueY;
		}
	}
	
	private void paintCircle(Graphics2D g2d, int x, int y) {
		g2d.fillArc(x-diameter/2, y-diameter/2, diameter, diameter, 0, 360);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		// Rysowanie obramowania wokol panelu.
		g2d.setColor(Color.GRAY);
		g2d.drawRect(0, 0, this.getWidth()-1, this.getHeight()-1);
		
		// Jesli watki nie przekazaly jeszcze zadnych danych - domyslne pozycje.
		if(mainPoint[0] < 0) {
			mainPoint[0] = getWidth()/2;
			mainPoint[1] = getHeight()/2;
			
			Random random = new Random();
			
			for(int i = 0; i < points.length; i++) {
				int x, y;
				
				// Losowanie wspolrzednych, tak, by obszar ryskowanego kola
				// nie pokrywal sie z glownym kolem.
				do {
					x = random.nextInt(getWidth());
					y = random.nextInt(getHeight());
				} while( 
						Math.pow((x - mainPoint[0]), 2) + 
						Math.pow((y - mainPoint[1]), 2) <= 
						Math.pow(diameter/2, 2) 
				);
				
				points[i][0] = x;
				points[i][1] = y;
			}
		}
		
		// Rysowanie czerwonego punktu o rozmiarach 30x30 px na srodku panelu.
		g2d.setColor(Color.RED);
		paintCircle(g2d, mainPoint[0], mainPoint[1]);
		
		// Rysowanie zielonego punktu w losowym miejscu.
		g2d.setColor(Color.GREEN);
		for(int i = 0; i < points.length; i++) {
			paintCircle(g2d, points[i][0], points[i][1]);
		}
	}
	
	// Ustawia start na true.
	public void start() {
		start = true;
	}
	
	// Ustawia start na false.
	public void stop() {
		start = false;
	}
	
	// Zwraca wartosc start.
	public boolean isRunning() {
		return start;
	}

}
