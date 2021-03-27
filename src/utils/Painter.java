package utils;

import javax.swing.JProgressBar;

/**
 * Clase para usar hilos que actualizan un componente swing en un hilo distinto al principal
 * @author U029903
 *
 */
public class Painter implements Runnable {
	public JProgressBar jProgressBar1;

	public void run() {
		while (true) {
			try {
				Thread.sleep(50);
				jProgressBar1.repaint();
			} catch (InterruptedException ex) {
				break;
			}
		}
	}
}