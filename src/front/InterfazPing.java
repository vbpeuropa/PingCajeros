package front;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import modelo.Cajero;
import utils.Utils;
import utils.Painter;
import utils.Worker;

public class InterfazPing extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	public JLabel labelCajero;
	public JTextField inputURLCajeros;
	public JLabel labelURLSalida;
	public JTextField inputURLSalida;
	public JLabel labelPingTime;
	public JFormattedTextField PingTime;
	public JTextArea display;
	public JScrollPane scrollPanel;
	public JRadioButton radioTxt;
	public JRadioButton radioBBDD;
	public JRadioButton radioFichero;
	public JRadioButton radioPing;
	public JRadioButton radioInformacion;
	public JCheckBox checkApagados;
	public JCheckBox checkEncendidos;
	public ButtonGroup grupoCarga;
	public ButtonGroup radioGrupo;
	public JButton botonStart;
	public JButton botonPause;
	public JButton botonCerrar;
	public JButton botonImport;
	public JButton botonExport;
	public JFileChooser frame;
	public JPanel panel;
	public JProgressBar jProgressBar;
	boolean flagPause;
	int numeroHilos = 10;
	BufferedWriter bw;

	public InterfazPing() {
		setLayout(null);
		setTitle("Inventario de Cajeros");

		grupoCarga = new ButtonGroup();
		radioTxt = new JRadioButton("Cargar desde TXT");
		radioTxt.setBounds(10, 5, 140, 20);
		radioTxt.addChangeListener(null);
		add(radioTxt);
		grupoCarga.add(radioTxt);
		radioTxt.doClick();

		radioBBDD = new JRadioButton("Cargar desde BD");
		radioBBDD.setBounds(155, 5, 125, 20);

		radioBBDD.addChangeListener(null);
		add(radioBBDD);
		grupoCarga.add(radioBBDD);

		labelCajero = new JLabel("Ruta de fichero de carga de cajeros:");
		labelCajero.setBounds(10, 25, 350, 20);
		add(labelCajero);

		inputURLCajeros = new JTextField();
		inputURLCajeros.setBounds(10, 45, 295, 20);
		add(inputURLCajeros);
		inputURLCajeros.setText("C:\\Users\\u029903\\Desktop\\txts Java\\ips.txt");

		labelURLSalida = new JLabel("Ruta para el fichero de salida");
		labelURLSalida.setBounds(10, 70, 350, 20);
		add(labelURLSalida);

		inputURLSalida = new JTextField();
		inputURLSalida.setBounds(10, 90, 295, 20);
		add(inputURLSalida);
		inputURLSalida.setText("C:\\Users\\u029903\\Desktop\\txts Java\\trazasPreventivo\\tabla ");

		labelPingTime = new JLabel("Tiempo de consulta por cajero (ms)");
		labelPingTime.setBounds(10, 110, 350, 20);
		add(labelPingTime);

		NumberFormat amountFormat = NumberFormat.getNumberInstance();
		PingTime = new JFormattedTextField(amountFormat);
		PingTime.setBounds(10, 130, 295, 20);
		add(PingTime);
		PingTime.setText("3000");

		radioGrupo = new ButtonGroup();
		radioPing = new JRadioButton("Sólo Ping");
		radioPing.setBounds(10, 155, 80, 20);
		radioPing.addChangeListener(null);
		add(radioPing);
		radioGrupo.add(radioPing);
		radioPing.doClick();

		radioFichero = new JRadioButton("Fichero");
		radioFichero.setBounds(210, 155, 195, 20);

		radioFichero.addChangeListener(null);
		add(radioFichero);
		radioGrupo.add(radioFichero);

		radioInformacion = new JRadioButton("Información");
		radioInformacion.setBounds(100, 155, 100, 20);
		radioInformacion.addChangeListener(null);
		add(radioInformacion);
		radioGrupo.add(radioInformacion);

		checkApagados = new JCheckBox("Apagados");
		checkApagados.setBounds(10, 175, 90, 20);
		checkApagados.addChangeListener(null);
		add(checkApagados);
		checkApagados.doClick();

		checkEncendidos = new JCheckBox("Encendidos");
		checkEncendidos.setBounds(210, 175, 95, 20);
		checkEncendidos.addChangeListener(null);
		add(checkEncendidos);
		checkEncendidos.doClick();

		frame = new JFileChooser();

		botonStart = new JButton("Analizar");
		botonStart.setBounds(10, 225, 80, 30);
		add(botonStart);
		botonStart.addActionListener(this);

		botonPause = new JButton("Pausar");
		botonPause.setBounds(110, 225, 90, 30);
		add(botonPause);
		botonPause.addActionListener(this);

		botonCerrar = new JButton("Cerrar");
		botonCerrar.setBounds(220, 225, 80, 30);
		add(botonCerrar);
		botonCerrar.addActionListener(this);

		// botones de urls

		botonImport = new JButton("...");
		botonImport.setBounds(280, 23, 25, 20);
		add(botonImport);
		botonImport.addActionListener(this);

		botonExport = new JButton("...");
		botonExport.setBounds(280, 68, 25, 20);
		add(botonExport);
		botonExport.addActionListener(this);

		panel = new JPanel();
		panel.setBounds(10, 260, 300, 260);
		panel.setBorder(new TitledBorder(new EtchedBorder(), "Resultado"));
		add(panel);

		// progressbar
		jProgressBar = new JProgressBar(0, 100);
		jProgressBar.setBounds(12, 200, 290, 20);
		jProgressBar.setValue(0);
		jProgressBar.setStringPainted(true);
		add(jProgressBar);

		display = new JTextArea(14, 24);

		scrollPanel = new JScrollPane(display);
		scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(scrollPanel);
	}
	
	public void actionPerformed(ActionEvent evento) {
		
		String URLCajeros = inputURLCajeros.getText();
		String URLFinal = "" + inputURLSalida.getText() + java.time.LocalDateTime.now().getMonth() + "-"
				+ java.time.LocalDateTime.now().getDayOfMonth() + " "
				+ java.time.LocalDateTime.now().getHour() + "-" + java.time.LocalDateTime.now().getMinute()
				+ ".csv";
		Painter p = new Painter();
		p.jProgressBar1 = jProgressBar;
		//Creamos hilo para la barra de progreso
		new Thread(p);
		List<Cajero> cajerosInt = null;
		try {
			if (evento.getSource() == botonStart) {
					//este bw se queda abierto?
					bw = new BufferedWriter(new FileWriter(URLFinal));
				//	bw.write("CAJERO;SISTEMA_OPERATIVO;SERVICE_PACK;ARQUITECTURA;PROCESADOR;MEMORIA;FABRICANTE;MODELO;PANELOP;LIBRETAS;INGRESADOR;CONTACTLESS;MASTER\n");
				bw.write("CAJERO;SISTEMA_OPERATIVO;SERVICE_PACK;ARQUITECTURA;PROCESADOR;MEMORIA;FABRICANTE;MODELO;NUMERO_SERIE;PANELOP;LIBRETAS;INGRESADOR;CONTACTLESS;MASTER\n");

					// tb Habilitamos el imprimir la consola
				display.setText("");
				Utils.salidaTextarea(display);
					if (radioTxt.isSelected()) {
						//cargamos cajeros del txt
						cajerosInt = Cajero.cargaTxtCajeros(URLCajeros);
						
					} else {
						// O bien cargamos todos los cajeros de bbdd
						cajerosInt = Utils.cargaDBCajeros("where 0=0");
					}
					
					for (int i = 1; i <= numeroHilos; i++) {
						Worker w  = new Worker(numeroHilos,i);
						w.jProgressBar1 = jProgressBar;
						w.evento = evento;
						w.interfazPing = this;
						w.cajeros = cajerosInt;
						w.URLFinal = URLFinal;
						Worker.bw = this.bw;
						Thread t2 = new Thread(w);
						t2.start();
					}
					System.out.println("Nº de Cajeros cargados: " + cajerosInt.size());
				}

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Fichero no encontrado", "Error", 2);
		} catch (ClassNotFoundException | SQLException e) {
			JOptionPane.showMessageDialog(null, "Error en conexion con BBDD \n" + e.getMessage(), "Error", 2);
		}

		if (evento.getSource() == botonImport || evento.getSource() == botonExport) {
			int seleccion = frame.showOpenDialog(this);
			if (seleccion == JFileChooser.APPROVE_OPTION) {
				File fichero = frame.getSelectedFile();
				if (evento.getSource() == botonImport) {
					inputURLCajeros.setText(fichero.getAbsolutePath());
				} else if (evento.getSource() == botonExport) {
					inputURLSalida.setText(fichero.getAbsolutePath());
				}
			}
		} else if (evento.getSource() == botonCerrar) {
			System.exit(0);

		} else if (evento.getSource() == botonPause) {
			if (flagPause) {
				resumeThread();
				flagPause = false;
				botonPause.setText("Pausar");
				botonPause.repaint();
			} else {
				flagPause = true;
				botonPause.setText("Continuar");
				botonPause.repaint();
			}
		}
	}

	public void checkForPaused() {
		synchronized (this) {
			while (flagPause) {
				try {
					this.wait();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void resumeThread() {
		synchronized (this) {
			flagPause = true;
			this.notify();
		}
	}

	public static void main(String args[]) {
		InterfazPing formulario = new InterfazPing();
		formulario.setBounds(0, 0, 320, 555);
		formulario.setResizable(false);
		formulario.setVisible(true);
		formulario.setLocationRelativeTo(null);
	}
}
