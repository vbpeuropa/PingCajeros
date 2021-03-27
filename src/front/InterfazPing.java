package front;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
	private static final String CABECERA_CSV = "CAJERO;SISTEMA_OPERATIVO;SERVICE_PACK;ARQUITECTURA;PROCESADOR;MEMORIA;FABRICANTE;MODELO;NUMERO_SERIE;PANELOP;LIBRETAS;INGRESADOR;CONTACTLESS;MASTER";

	private JLabel labelCajero;
	private JTextField inputURLCajeros;
	private JLabel labelURLSalida;
	private JTextField inputURLSalida;
	private JLabel labelPingTime;
	private JFormattedTextField PingTime;
	private JTextArea display;
	private JScrollPane scrollPanel;
	private JRadioButton radioTxt;
	private JRadioButton radioBBDD;
	private JRadioButton radioFichero;
	private JRadioButton radioPing;
	private JRadioButton radioInformacion;
	private JCheckBox checkApagados;
	private JCheckBox checkEncendidos;
	private ButtonGroup grupoCarga;
	private ButtonGroup radioGrupo;
	private JButton botonStart;
	private JButton botonPause;
	private JButton botonCerrar;
	private JButton botonImport;
	private JButton botonExport;
	private JFileChooser frame;
	private JPanel panel;
	private JProgressBar jProgressBar;
	private boolean flagPause;
	private int numeroHilos = 10;
	private BufferedWriter bw;
	
	public InterfazPing(int x, int y, int width, int height, boolean resizable, boolean visible) {
		
		setBounds( x, y, width, height);
		setResizable(resizable);
		setVisible(visible);
		setLocationRelativeTo(null);
		
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
		setPingTime(new JFormattedTextField(amountFormat));
		getPingTime().setBounds(10, 130, 295, 20);
		add(getPingTime());
		getPingTime().setText("3000");

		radioGrupo = new ButtonGroup();
		radioPing = new JRadioButton("Sólo Ping");
		radioPing.setBounds(10, 155, 80, 20);
		radioPing.addChangeListener(null);
		add(radioPing);
		radioGrupo.add(radioPing);
		radioPing.doClick();

		setRadioFichero(new JRadioButton("Fichero"));
		getRadioFichero().setBounds(210, 155, 195, 20);

		getRadioFichero().addChangeListener(null);
		add(getRadioFichero());
		radioGrupo.add(getRadioFichero());

		setRadioInformacion(new JRadioButton("Información"));
		getRadioInformacion().setBounds(100, 155, 100, 20);
		getRadioInformacion().addChangeListener(null);
		add(getRadioInformacion());
		radioGrupo.add(getRadioInformacion());

		setCheckApagados(new JCheckBox("Apagados"));
		getCheckApagados().setBounds(10, 175, 90, 20);
		getCheckApagados().addChangeListener(null);
		add(getCheckApagados());
		getCheckApagados().doClick();

		setCheckEncendidos(new JCheckBox("Encendidos"));
		getCheckEncendidos().setBounds(210, 175, 95, 20);
		getCheckEncendidos().addChangeListener(null);
		add(getCheckEncendidos());
		getCheckEncendidos().doClick();

		frame = new JFileChooser();

		setBotonStart(new JButton("Analizar"));
		getBotonStart().setBounds(10, 225, 80, 30);
		add(getBotonStart());
		getBotonStart().addActionListener(this);

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
		setjProgressBar(new JProgressBar(0, 100));
		getjProgressBar().setBounds(12, 200, 290, 20);
		getjProgressBar().setValue(0);
		getjProgressBar().setStringPainted(true);
		add(getjProgressBar());

		display = new JTextArea(14, 24);

		scrollPanel = new JScrollPane(display);
		scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(scrollPanel);
	}
	
	
	
	public void actionPerformed(ActionEvent evento) {
		LocalDateTime date = java.time.LocalDateTime.now();
		String urlCajeros = inputURLCajeros.getText();
		String urlFinal = "" + inputURLSalida.getText() + date.getMonth() + "-"
				+ date.getDayOfMonth() + " "
				+ date.getHour() + "-" + date.getMinute()
				+ ".csv";
		
		//Creamos hilo para la barra de progreso
		new Painter(getjProgressBar()).run();
		
		try {
			if (evento.getSource() == getBotonStart()) {
				
				/*TODO: este bw no se cierra, 
				 * deberia cerrarlo cuando hayan terminado todos los hilos,
				 * si lo cierras ahora los hilos no podrán escribir
				 */
				bw = new BufferedWriter(new FileWriter(urlFinal));
				bw.write(CABECERA_CSV);

				// tb Habilitamos el imprimir la consola
				display.setText("");
				Utils.salidaTextarea(display);
				
				List<Cajero> cajeros = new ArrayList<Cajero>();
				
				if (radioTxt.isSelected()) {
					//cargamos cajeros del txt
					cajeros.addAll(Cajero.cargaTxtCajeros(urlCajeros));
					
				} else {
					// O bien cargamos todos los cajeros de bbdd
					cajeros.addAll(Utils.cargaDBCajeros("where 0=0"));
				}
				
				System.out.println("Nº de Cajeros cargados: " + cajeros.size());
				for (int i = 1; i <= numeroHilos; i++) {
					new Worker(evento, cajeros, this, numeroHilos, i, urlFinal, bw)
						.run();
				}

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



	public JButton getBotonStart() {
		return botonStart;
	}



	public void setBotonStart(JButton botonStart) {
		this.botonStart = botonStart;
	}



	public JProgressBar getjProgressBar() {
		return jProgressBar;
	}



	public void setjProgressBar(JProgressBar jProgressBar) {
		this.jProgressBar = jProgressBar;
	}



	public JFormattedTextField getPingTime() {
		return PingTime;
	}



	public void setPingTime(JFormattedTextField pingTime) {
		PingTime = pingTime;
	}



	public JCheckBox getCheckEncendidos() {
		return checkEncendidos;
	}



	public void setCheckEncendidos(JCheckBox checkEncendidos) {
		this.checkEncendidos = checkEncendidos;
	}



	public JRadioButton getRadioFichero() {
		return radioFichero;
	}



	public void setRadioFichero(JRadioButton radioFichero) {
		this.radioFichero = radioFichero;
	}



	public JRadioButton getRadioInformacion() {
		return radioInformacion;
	}



	public void setRadioInformacion(JRadioButton radioInformacion) {
		this.radioInformacion = radioInformacion;
	}



	public JCheckBox getCheckApagados() {
		return checkApagados;
	}



	public void setCheckApagados(JCheckBox checkApagados) {
		this.checkApagados = checkApagados;
	}
}
