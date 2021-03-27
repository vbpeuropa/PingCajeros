package modelo;

public class Entidad {
 private String idEntidad;
 private String entidadRSI;
 private String nombre;
 private boolean esHija;
 
public Entidad(String idEntidad, String entidadRSI, String nombre, boolean esHija) {
	super();
	this.idEntidad = idEntidad;
	this.entidadRSI = entidadRSI;
	this.nombre = nombre;
	this.esHija = esHija;
}

public Entidad(String idEntidad, String entidadRSI) {
	this.idEntidad = idEntidad;
	this.entidadRSI = entidadRSI;
}

public String getIdEntidad() {
	return idEntidad;
}



public void setIdEntidad(String idEntidad) {
	this.idEntidad = idEntidad;
}



public String getEntidadRSI() {
	return entidadRSI;
}



public void setEntidadRSI(String entidadRSI) {
	this.entidadRSI = entidadRSI;
}

public String getNombre() {
	return nombre;
}

public void setNombre(String nombre) {
	this.nombre = nombre;
}

public boolean isEsHija() {
	return esHija;
}

public void setEsHija(boolean esHija) {
	this.esHija = esHija;
}
 

}
