package utils;

import javax.swing.JProgressBar;

/**
 * Clase para usar hilos que actualizan un componente swing en un hilo distinto al principal
 * @author U029903
 *
 */
public class Painter extends Thread {
	public JProgressBar jProgressBar;

	public Painter(JProgressBar jProgressBar) {
		this.jProgressBar = jProgressBar;
	}

	@Override
	public synchronized void run() {
		while (jProgressBar.getValue() <100) {
			try {
				sleep(50);
				jProgressBar.repaint();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
}