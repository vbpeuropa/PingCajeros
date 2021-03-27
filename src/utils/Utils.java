package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JTextArea;

import modelo.CDB;
import modelo.Cajero;

public class Utils {

	private static final String USER = "pruebas";
	private static final String PASSWORD = "passpruebas";

	private static final String BASE_PORT_AND_LOG_FILE = ":8080/Trazas/log.jsp?file=C:\\log\\";
	private static final String BASE_PORT_AND_LOG_INDEX = ":8080/Trazas/index.jsp?dir=C:\\log\\";
	private static final String IDCAJERO_QUERY = "select distinct(TRM_FISICO) AS TERMINALES from o02sace0.atm_versiones t ";

	/**
	 * Hace ping a la ip de un cajero
	 * 
	 * @param cajero
	 * @param time
	 */
	public static boolean comprobarIp(Cajero cajero, int time) {
		try {
			// antes veíamos si devolvía ping, ahora buscamos el código de respuesta de las
			// páginas para evitar nullpointers
			// InetAddress call = InetAddress.getByName(cajero.getIp());
			HttpURLConnection url = (HttpURLConnection) new URL(
					"http://" + cajero.getIp() + BASE_PORT_AND_LOG_FILE + "..\\config\\fabricante\\systeminfo.txt")
							.openConnection();
			url.setConnectTimeout(time);
			// Para los cajeros más cabrones se pone el readtimeout en 30 segundos
			url.setReadTimeout(30000);
			return url.getResponseCode() == 200 ? true : false;
		} catch (IOException e) {
			System.err.println(cajero.getIp() + " Inaccesible");
			return false;
		}
	}

	// esta conexion no es para consultar la ips, sino para cada una de las llamadas
	// a los ficheros.
	// es prácticamente lo anterior duplicaedo, estaría bien unificarlo
	public static HttpURLConnection connectWithTimeOut(String urlString, int time) {
		try {
			HttpURLConnection url = (HttpURLConnection) new URL(urlString).openConnection();
			url.setConnectTimeout(time);
			url.setReadTimeout(30000);
			return url;
		} catch (IOException e) {
			System.err.println(" Inaccesible");
			return null;
		}
	}

	/**
	 * Comprueba la existencia de la línea de arranque de preventivo en un fichero
	 * del cajero
	 * 
	 * @exception Si no encuentra el fichero o la url del tomcat del cajero no esta
	 *               accesible
	 */
	public static boolean comprobarDatosFichero(Cajero cajero) {
		try {
			boolean match2 = false;
			// String url = "http://" + cajero.getIp() +
			// ":8080/Trazas/index.jsp?dir=C:\\log\\..\\trustlib\\appatm\\base\\config";
			String url = "http://" + cajero.getIp() + BASE_PORT_AND_LOG_INDEX + "..\\temp";

			InputStream is = new URL(url).openStream();
			InputStream is2 = new URL(url).openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			BufferedReader reader2 = new BufferedReader(new InputStreamReader(is2));
			Stream<String> lines = reader.lines();
			Stream<String> lines2 = reader2.lines();
			// boolean match = lines.anyMatch(n -> n.contains("FileConfig.class"));
			boolean match = lines.anyMatch(n -> n.contains("XPSP3.exe"));
			// los que no tienen fecha de noviembre están obsoletos
			if (match) {
				match2 = lines2.anyMatch(n -> n.contains("FileConfig.class") && n.contains("62055 MB"));
				if (match2) {
					System.out.print("CORRECTO");

				} else {
					System.err.print("INCORRECTO");
				}
			}
			reader.close();
			reader2.close();
			lines.close();
			lines2.close();
			return match;
		} catch (MalformedURLException e) {
			System.err.println(cajero.getIp() + " cajero no válido");
			return false;
		} catch (IOException e) {
			System.err.println("  no soporta preventivo");
			return false;
		}
	}

	/**
	 * A partir de una URL a fichero lo convierte a Stream
	 * 
	 * @param conn
	 * @return
	 * @throws IOException
	 */
	public static Stream<String> urlFileToStream(HttpURLConnection conn) throws IOException {
		InputStream isConn = conn.getInputStream();
		return new BufferedReader(new InputStreamReader(isConn)).lines();
	}

	/**
	 * Toma los valores relevantes de varios ficheros de un cajero
	 * 
	 * @param cajero
	 * @return Stream con el contenido de los ficheros relevante de un cajero
	 * @throws IOException
	 * @throws MalformedURLException,IOException, NullPointerException
	 */
	public static List<String> comprobarInformacion(Cajero cajero)
			throws MalformedURLException, IOException, NullPointerException {
		String ipCajero = cajero.getIp();

		final String URL_INFO = "http://" + ipCajero + BASE_PORT_AND_LOG_FILE
				+ "..\\config\\fabricante\\systeminfo.txt";
		final String URL_FABRICANTE = "http://" + ipCajero + BASE_PORT_AND_LOG_FILE
				+ "..\\config\\fabricante\\fabricante.cfg";
		final String URL_CONFIG = "http://" + ipCajero + BASE_PORT_AND_LOG_FILE + "..\\config\\admon\\config.cfg";
		final String URL_DEVICES = "http://" + ipCajero + BASE_PORT_AND_LOG_FILE + "..\\Instala\\devices.log";
		final String URL_MASTER = "http://" + ipCajero + BASE_PORT_AND_LOG_FILE
				+ "..\\config\\fabricante\\masterInstalado.txt";
		final String URL_INFOPC = "http://" + ipCajero + BASE_PORT_AND_LOG_FILE + "..\\Instala\\systeminfoPC.log";
		final String URL_VERSION = "http://" + ipCajero + BASE_PORT_AND_LOG_INDEX + "..";

		final String URL_SERVER = "http://" + ipCajero + BASE_PORT_AND_LOG_FILE
				+ "..\\config\\server\\server-config.cfg";

		// conexiones fuera del argumento del try porque no son autocloseables
		HttpURLConnection info = connectWithTimeOut(URL_INFO, 3000);
		HttpURLConnection fabricante = connectWithTimeOut(URL_FABRICANTE, 3000);
		HttpURLConnection config = connectWithTimeOut(URL_CONFIG, 3000);
		HttpURLConnection devices = connectWithTimeOut(URL_DEVICES, 3000);
		HttpURLConnection master = connectWithTimeOut(URL_MASTER, 3000);
		HttpURLConnection infopc = connectWithTimeOut(URL_INFOPC, 3000);
		HttpURLConnection version = connectWithTimeOut(URL_VERSION, 3000);
		HttpURLConnection server = connectWithTimeOut(URL_SERVER, 3000);
		
		try (Stream<String> streamInfo = urlFileToStream(info).filter(n -> (
				n.contains("OS Name")
				|| n.contains("OS Version") 
				|| n.contains("Processor(s)") 
				|| n.contains("Total Physical Memory")
				|| n.contains("System Type") 
				|| n.contains("System type")) 
				&& !n.contains("BIOS Version"));
				
			Stream<String> streamFabricante = urlFileToStream(fabricante).filter(n -> (
					n.contains("FABRICANTE") 
					|| n.contains("MODELO") 
					|| n.contains("NUMERO_SERIE")));
			
			Stream<String> streamConfig = urlFileToStream(config).filter(n -> (
					n.contains("afe") 
					|| n.contains("pbk") 
					|| n.contains("eop") 
					|| n.contains("ccr")));
			
			Stream<String> streamMaster = urlFileToStream(master).filter(n -> (
					n.contains("Diebold") 
					|| n.contains("NCR") 
					|| n.contains("Wincor")));
			
			Stream<String> streamDevices = urlFileToStream(devices).filter(n -> (
					n.contains("(RPT") 
					|| n.contains("ZYT") 
					|| n.contains("SINA")
					|| n.contains("BARC") 
					|| n.contains("EPP") 
					&& !n.contains("[EPP")
					&& !n.contains("[BAR")));
			
			Stream<String> streamInfoPC = urlFileToStream(infopc).filter(n -> (
					n.contains("System Model:")));
			
			Stream<String> streamVersion = urlFileToStream(version).filter(n -> (
					n.contains("Version")));
			) {
			
			// merge de los streams en una única lista con los valores buenos
			// el orden es relevante de cara a que cuadren las columnas

			List<String> listaTratados = Stream.of(streamInfo, streamFabricante, streamConfig, streamMaster,
					streamVersion, streamDevices, streamInfoPC).flatMap(i -> i).collect(Collectors.toList());

			// esto es para que rellene con desconocido si esta en blanco
			boolean encontradoSerie = false;
			boolean encontradoMaster = false;

			for (String campo : listaTratados) {
				if (!encontradoSerie && campo.contains("NUMERO_SERIE")) {
					encontradoSerie = true;
				}
				if (!encontradoMaster && (campo.contains("Diebold") || campo.contains("NCR") || campo.contains("Wincor"))) {
					encontradoMaster = true;
				}
				if (encontradoMaster && encontradoSerie) {
					break;
				}
			}
			
			if (!encontradoSerie && 7 < listaTratados.size()) {
					listaTratados.add(7, "NUMERO_SERIE = DESCONOCIDO");
			}
			
			if (!encontradoMaster  && 12 < listaTratados.size()) {
					listaTratados.add(12, "DESCONOCIDO");
			}
			return listaTratados;
		} catch (MalformedURLException e) {
			System.err.println(ipCajero + " versión no encontrada");
			return null;

		} catch (IOException e) {
			System.err.println(" Error en lectura " + e.getMessage());
			return null;
		} finally {
			info.disconnect();
			fabricante.disconnect();
			config.disconnect();
			devices.disconnect();
			master.disconnect();
			infopc.disconnect();
			version.disconnect();
			server.disconnect();
		}
	}

	public static ArrayList<Cajero> cargaDBCajeros(String filtro) throws ClassNotFoundException, SQLException {
		// Creamos una conexion, probablemente debería pedir las credenciales al
		// principio
		CDB conn = new CDB(USER, PASSWORD);

		ResultSet rs = conn.ejecutarSelect(IDCAJERO_QUERY + filtro);
		// metadata para sacar nombres de los cajeros btw
		// ResultSetMetaData rsmd = rs.getMetaData();
		Stream.Builder<Cajero> builder = Stream.builder();

		while (rs.next()) {
			Cajero c = new Cajero(rs.getString("TERMINALES"));
			builder.add(c);
		}
		rs.close();
		conn.cerrarConexion();

		return (ArrayList<Cajero>) builder.build().collect(Collectors.toList());
		// Muestra todos los cajeros cargados
		// cajeros.map(p -> p.getIp()).forEach(System.out::println)
	}

	/**
	 * Ejecuta una Query pasada por parámetro
	 * 
	 * @param query
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static ResultSet cargaDBCajerosQuery(String query) throws ClassNotFoundException, SQLException {
		CDB conn = new CDB(USER, PASSWORD);
		return conn.ejecutarSelect(query);
	}

	/**
	 * Formatea el resultado de un cajero a CSV
	 * 
	 * @param resultadoBw Writer en el que se va a escribir el resultado
	 * @param c Cajero que será analizado
	 * @throws IOException
	 */
	public static void formateaCsv(List<String> resultadoBw, List<String> resultadoSyso, Cajero cajero)
			throws IOException {
		resultadoBw.add(cajero.getIp() + ";");
		Utils.comprobarInformacion(cajero).forEach(n -> {
			// A partir de aquí escribimos como CSV
			switch (n) {
			case "OS Name:                   Microsoft Windows XP Professional":
				resultadoBw.add("XP");
				break;

			case "OS Name:                   Microsoft Windows 7 Professional ":
				resultadoBw.add("W7");
				break;

			case "OS Version:                6.1.7601 Service Pack 1 Build 7601":
				resultadoBw.add("SP1");
				break;

			case "OS Version:                5.1.2600 Service Pack 2 Build 2600":
				resultadoBw.add("SP2");
				break;

			case "OS Version:                5.1.2600 Service Pack 3 Build 2600":
				resultadoBw.add("SP3");
				break;

			case "FABRICANTE = NCR":
				resultadoBw.add("NCR");
				break;

			case "FABRICANTE = WINCOR":
				resultadoBw.add("WINCOR");
				break;

			case "FABRICANTE = DIEBOLD":
				resultadoBw.add("DIEBOLD");
				break;

			default:
				if (n.contains("MODELO")) {
					resultadoBw.add(n.replace("MODELO = ", ""));
				} else if (n.contains("NUMERO_SERIE")) {
					resultadoBw.add(n.replace("NUMERO_SERIE = ", ""));
				} else if (n.contains("PLATAFORMA")) {
					resultadoBw.add(n.replace("PLATAFORMA=", ""));
				} else if (n.contains("VERSION") && !n.contains("txt")) {
					resultadoBw.add(n.replace("VERSION", ""));

				} else if (n.contains("afe")) {
					if (n.contains("1")) {
						resultadoBw.add("afe=1");
					} else {
						resultadoBw.add("afe=0");
					}
					;
				} else if (n.contains("ccr")) {
					if (n.contains("1")) {
						resultadoBw.add("ccr=1");
					} else {
						resultadoBw.add("ccr=0");
					}
				} else if (n.contains("System Model:              ")) {
					resultadoBw.add(n.replace("System Model:              ", ""));

				} else if (n.contains("EPSON")) {
					resultadoBw.add("EPSON");

				} else if (n.contains("SNOWHAVEN")) {
					resultadoBw.add("SNOWHAVEN");
				}
				else if (n.contains("Version")) {
					if (n.contains("10.txt")) {
						resultadoBw.add("10");

					} else if (n.contains("11.txt")) {
						resultadoBw.add("11");
					} else if (n.contains("8.txt")) {
						resultadoBw.add("8");
					}
				} else {
					/*
					 * los streams ya vienen filtrados, así que si no cumple estas condiciones
					 * simplemente es que no queremos darle formato
					 */
					resultadoBw.add(n);
				}
				break;
			}
			resultadoBw.add(";");
			// Imprimimos los datos en crudo
			resultadoSyso.add("\n" + n);
			// System.out.println("\n" + n);
		});
	}

	/**
	 * Contiene el código a añadir en un System outputStream para que se muestre en
	 * un textarea
	 * 
	 * @param textarea
	 * @param buf
	 * @param off
	 * @param len
	 */
	private static void outputToTextarea(JTextArea textarea, byte[] buf, int off, int len) {
		String msg = new String(buf, off, len);
		textarea.setText(textarea.getText() + msg);
		if (textarea.getText().length() >= Integer.MAX_VALUE) {
			textarea.setText("Consola Limpiada");
		}
	}

	/**
	 * Sirve para System out y system err y permite imprimir la consola en un
	 * textarea
	 * 
	 * @param s
	 * @param textarea
	 */
	public static void salidaTextarea(JTextArea textarea) {
		System.setOut(new PrintStream(System.out) {
			@Override
			public void write(byte[] buf, int off, int len) {
				super.write(buf, off, len);
				outputToTextarea(textarea, buf, off, len);
			}
		});
		System.setErr(new PrintStream(System.err) {
			@Override
			public void write(byte[] buf, int off, int len) {
				super.write(buf, off, len);
				outputToTextarea(textarea, buf, off, len);
			}
		});
	}

	// está bloqueado por la directiva CORS,NO FUNCIONARÁ
	public static void borrarFichero(Cajero cajero) throws IOException {
		URL url = new URL("http://" + cajero.getIp() + BASE_PORT_AND_LOG_FILE + "..\\Wincor\\ProDeviceStart.bat");
		HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
		httpCon.setDoOutput(true);
		httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		httpCon.setRequestMethod("DELETE");
		httpCon.connect();
	}
}
