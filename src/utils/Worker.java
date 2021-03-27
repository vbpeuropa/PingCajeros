package utils;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import front.InterfazPing;
import modelo.Cajero;

/**
 * deberia poderse implementar como varios Hilos para agilizar el calculo sobre
 * cajeros EN DESARROLLO
 * 
 * @author U029903
 *
 */
public class Worker implements Runnable {
	public JProgressBar jProgressBar1;
	public ActionEvent evento;
	public List<Cajero> cajeros;
	public InterfazPing interfazPing;
	public int maxHilos;
	public int numHilo;
	public String URLFinal;
	public static int cajerosTratados;
	public static BufferedWriter bw;
	static int posicion = 1;
	private int contbad = 0;

	private List<String> resultadoBw;
	private List<String> resultadoSyso;
	static boolean flagEnUso = false;
	private static String PROP_OK = " Encendido";

	public Worker(int maxHilos, int numHilo) {
		super();
		this.maxHilos = maxHilos;
		this.numHilo = numHilo;
		cajeros = null;
	}

	public synchronized void run() {
		int maxCajeros = 0;
		int fraccionCajeros = 0;
		resultadoBw = new ArrayList<String>();
		resultadoSyso = new ArrayList<String>();

		if (evento.getSource() == interfazPing.botonStart) {
			try {
				// fraccionamos el arraylist maxHilos veces
				maxCajeros = cajeros.size();
				fraccionCajeros = (int) Math.floorDiv(cajeros.size(), maxHilos);
				int restoCajeros = Math.floorMod(cajeros.size(), maxHilos);

				// Si la división no es exacta los cajeros del resto van al último hilo
				if (numHilo == maxHilos && restoCajeros != 0) {
					restoCajeros = Math.floorMod(cajeros.size(), maxHilos) * maxHilos;
					cajeros = cajeros.subList((numHilo - 1) * fraccionCajeros, maxCajeros);
				} else {
					cajeros = cajeros.subList((numHilo - 1) * fraccionCajeros, (numHilo) * fraccionCajeros);
				}
				for (Cajero c : cajeros) {
					posicion++;
					jProgressBar1.setValue((int) Math.floor(((double) posicion / maxCajeros) * 100));
					String ipCajero = c.getIp();
					if (Utils.comprobarIp(c,
							Integer.parseInt(interfazPing.PingTime.getText().replace(".", "").replace(",", "")))) {

						if (interfazPing.checkEncendidos.isSelected()) {
							resultadoSyso.add(ipCajero + PROP_OK);
							if (interfazPing.radioFichero.isSelected()) {
								if (Utils.comprobarDatosFichero(c)) {
									resultadoSyso.add(ipCajero + ";SP3;");
									resultadoBw.add(ipCajero + ";SP3;");
								} else {
									resultadoBw.add(ipCajero + ";SP2;");
									resultadoSyso.add(ipCajero + ";SP2;");
								}
								resultadoSyso.add(" \n ----------------------- \n");
								resultadoBw.add("");
							} else if (interfazPing.radioInformacion.isSelected()) {
								try {
									// formateamos a CSV
									Utils.formateaCsv(resultadoBw, resultadoSyso, c);
								} catch (NullPointerException e) {
									;
									resultadoSyso.add("Cajero inestable, no se tratarán sus datos");
									resultadoBw.add(
											"ERROR;ERROR;ERROR;ERROR;ERROR;ERROR;ERROR;ERROR;ERROR;ERROR;ERROR;ERROR;ERROR");
								}
								resultadoSyso.add(" \n ----------------------- \n");
								resultadoBw.add("");
							} else {
								resultadoBw.add(ipCajero
										+ ";ENCENDIDO;ENCENDIDO;ENCENDIDO;ENCENDIDO;ENCENDIDO;ENCENDIDO;ENCENDIDO;ENCENDIDO;ENCENDIDO;ENCENDIDO;ENCENDIDO;ENCENDIDO;ENCENDIDO");
							}
							resultadoSyso.add(" \n");
						}
					} else {
						if (interfazPing.checkApagados.isSelected()) {
							contbad++;
							resultadoBw.add(ipCajero
									+ ";APAGADO;APAGADO;APAGADO;APAGADO;APAGADO;APAGADO;APAGADO;APAGADO;APAGADO;APAGADO;APAGADO;APAGADO;APAGADO");
							resultadoSyso.add(" \n");
						}
					}
					resultadoBw.add("\n");
					interfazPing.checkForPaused();

					while (flagEnUso) {
						// te duermes un segundo y vuelves a intentarlo
						Thread.sleep(1000);
					}

					flagEnUso = true;
					// imprimimos todo, pero sincronizado

					resultadoSyso.forEach(System.out::println);

					for (int i = 0; i < resultadoBw.size(); i++) {
						bw.write(resultadoBw.get(i));
					}

					resultadoBw.clear();
					resultadoSyso.clear();
					flagEnUso = false;
				}
				cajerosTratados = cajerosTratados + fraccionCajeros;

				// si no han terminado todos los hilos esperamos.
				if (cajerosTratados + restoCajeros < maxCajeros || jProgressBar1.getValue() != 100) {
					wait();
				} else {
					notifyAll();
					System.out.println(contbad + " Cajeros caídos");
					Runtime.getRuntime().exec("explorer.exe /select," + URLFinal);
					bw.close();
				}

			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Fichero no encontrado", "Error", 2);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (IndexOutOfBoundsException e1) {
				e1.printStackTrace();
			}
		}
	}
}