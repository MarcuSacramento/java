package br.com.accenture.batimento.movel.utilitario;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class SSHGabarito {

	
	String nome;
	String campoInicio;
	String campoFim;
	Properties properties;

	public SSHGabarito(Properties properties) {

		this.properties = properties;

	}

	public String getNome() {
		return getProperties().getProperty(nome+"nome");
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCampoInicio() {
		return getProperties().getProperty(nome+"campoInicio");
	}

	public String getCampoFim() {
		return getProperties().getProperty(nome+"campoFim");
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

}
