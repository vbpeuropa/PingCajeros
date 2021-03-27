package modelo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class Cajero {
	private String ip;
	private SO so;
	private String SOVersion;
	private Marcas marca;
	private String Modelo;
	private Tipo tipo;

	public Cajero(String ip) {
		super();
		this.ip = ip;
	}

	public Cajero(String ip, SO so, String sOVersion, Marcas marca, String modelo, Tipo tipo, Entidad entidad) {
		super();
		this.ip = ip;
		this.so = so;
		SOVersion = sOVersion;
		this.marca = marca;
		Modelo = modelo;
		this.tipo = tipo;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public SO getSo() {
		return so;
	}

	public void setSo(SO so) {
		this.so = so;
	}

	public String getSOVersion() {
		return SOVersion;
	}

	public void setSOVersion(String sOVersion) {
		SOVersion = sOVersion;
	}

	public Marcas getMarca() {
		return marca;
	}

	public void setMarca(Marcas marca) {
		this.marca = marca;
	}

	public String getModelo() {
		return Modelo;
	}

	public void setModelo(String modelo) {
		Modelo = modelo;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	/**
	 * @ Crea cajeros a partir de un txt con las ips separadas por saltos de linea
	 * 
	 * @return Areraylisrt<Cajero>
	 * @throws IOException
	 */
	@Deprecated
	public static ArrayList<Cajero> cargaCajeros7() throws IOException {
		ArrayList<Cajero> cajero = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\u029903\\Desktop\\txts Java\\ips.txt"));
		String text;
		while ((text = br.readLine()) != null) {
			cajero.add(new Cajero(text));
		}
		br.close();
		return cajero;
	}

	/**
	 * Crea cajeros a partir de un txt con las ips separadas por saltos de linea
	 * 
	 * @return Areraylisrt<Cajero>
	 * @throws IOException
	 */
	public static ArrayList<Cajero> cargaTxtCajeros(String URL) throws IOException {

		ArrayList<Cajero> cajero = new ArrayList<>();
		// Path filePath = Paths.get("C:\\\\Users\\\\u029903\\\\Desktop\\\\txts Java",
		// "ips.txt");
		Path filePath = Paths.get(URL);
		Stream<String> lines = Files.lines(filePath);
		lines.forEach(n -> cajero.add(new Cajero(n)));
		lines.close();
		return cajero;
	}
}
