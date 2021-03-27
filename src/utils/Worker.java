package utils;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import front.InterfazPing;
import modelo.Cajero;

/**
 * deberia poderse implementar como varios Hilos para agilizar el calculo sobre
 * cajeros EN DESARROLLO
 * 
 * @author U029903
 *
 */
public class Worker extends Thread {
	
	private static final String PROP_OK = " Encendido";
	private static final String ERROR_CSV = ";ERROR;ERROR;ERROR;ERROR;ERROR;ERROR;ERROR;ERROR;ERROR;ERROR;ERROR;ERROR;ERROR";
	private static final String ENCENDIDO_CSV = ";ENCENDIDO;ENCENDIDO;ENCENDIDO;ENCENDIDO;ENCENDIDO;ENCENDIDO;ENCENDIDO;ENCENDIDO;ENCENDIDO;ENCENDIDO;ENCENDIDO;ENCENDIDO;ENCENDIDO";
	private static final String APAGADO_CSV = ";APAGADO;APAGADO;APAGADO;APAGADO;APAGADO;APAGADO;APAGADO;APAGADO;APAGADO;APAGADO;APAGADO;APAGADO;APAGADO";

	private static int cajerosTratados;
	private static int posicion = 1;
	private static boolean flagEnUso = false;

	private ActionEvent evento;
	private List<Cajero> cajeros;
	private InterfazPing interfazPing;
	private int maxHilos;
	private int numHilo;
	private String urlFinal;
	private BufferedWriter bw;

	public Worker(ActionEvent evento, List<Cajero> cajeros, InterfazPing interfazPing, int maxHilos, int numHilo,
			String urlFinal, BufferedWriter bw) {
		super();
		this.evento = evento;
		this.cajeros = cajeros;
		this.interfazPing = interfazPing;
		this.maxHilos = maxHilos;
		this.numHilo = numHilo;
		this.urlFinal = urlFinal;
		this.bw = bw;
	}

	@Override
	public synchronized void run() {
		int maxCajeros = 0;
		int fraccionCajeros = 0;
		int contbad = 0;
		List<String> resultadoBw = new ArrayList<String>();
		List<String> resultadoSyso = new ArrayList<String>();

		if (evento.getSource() == interfazPing.getBotonStart()) {
			try {
				
				// fraccionamos el arraylist maxHilos veces
				maxCajeros = cajeros.size();
				fraccionCajeros = (int) Math.floorDiv(maxCajeros, maxHilos);
				int restoCajeros = Math.floorMod(maxCajeros, maxHilos);
				int index = (numHilo) * fraccionCajeros;
				// Si la división no es exacta los cajeros del resto van al último hilo
				if (numHilo == maxHilos && restoCajeros != 0) {
					restoCajeros *= maxHilos;
					index = maxCajeros;
				}
				cajeros = cajeros.subList((numHilo - 1) * fraccionCajeros, index);
				
				for (Cajero cajero : cajeros) {
					posicion++;
					interfazPing.getjProgressBar().setValue((int) Math.floor(((double) posicion / maxCajeros) * 100));
					String ipCajero = cajero.getIp();
					if (Utils.comprobarIp(cajero,
							Integer.parseInt(interfazPing.getPingTime().getText().replace(".", "").replace(",", "")))) {
						if (interfazPing.getCheckEncendidos().isSelected()) {
							resultadoSyso.add(ipCajero + PROP_OK);
							if (interfazPing.getRadioFichero().isSelected()) {
								if (Utils.comprobarDatosFichero(cajero)) {
									resultadoSyso.add(ipCajero + ";SP3;");
									resultadoBw.add(ipCajero + ";SP3;");
								} else {
									resultadoBw.add(ipCajero + ";SP2;");
									resultadoSyso.add(ipCajero + ";SP2;");
								}
								resultadoSyso.add(" \n ----------------------- \n");
								resultadoBw.add("");
							} else if (interfazPing.getRadioInformacion().isSelected()) {
								try {
									// formateamos a CSV
									Utils.formateaCsv(resultadoBw, resultadoSyso, cajero);
								} catch (NullPointerException e) {
									resultadoSyso.add("Cajero inestable, no se tratarán sus datos");
									resultadoBw.add(ERROR_CSV);
								}
								resultadoSyso.add(" \n ----------------------- \n");
								resultadoBw.add("");
							} else {
								resultadoBw.add(ipCajero+ ENCENDIDO_CSV);
							}
							resultadoSyso.add(" \n");
						}
					} else {
						if (interfazPing.getCheckApagados().isSelected()) {
							contbad++;
							resultadoBw.add(ipCajero+ APAGADO_CSV);
							resultadoSyso.add(" \n");
						}
					}
					resultadoBw.add("\n");
					interfazPing.checkForPaused();

					while (flagEnUso) {
						// te duermes un segundo y vuelves a intentarlo
						sleep(1000);
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
				if (cajerosTratados + restoCajeros < maxCajeros || interfazPing.getjProgressBar().getValue() != 100) {
					wait();
				} else {
					notifyAll();
					System.out.println(contbad + " Cajeros caídos");
					Runtime.getRuntime().exec("explorer.exe /select," + urlFinal);
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