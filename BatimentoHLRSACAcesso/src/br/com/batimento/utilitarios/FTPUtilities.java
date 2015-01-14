package br.com.batimento.utilitarios;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class FTPUtilities {

	public static String pesquisaCampo(String campo, String retorno) {

		String pesquisa = "";
		String[] text = retorno.split("\n");
		Properties prop = new Properties();

		try {
			
			prop.load(new FileInputStream("C:\\workspace\\prop.properties"));
		} catch (FileNotFoundException e) {
			System.out.println("Erro ao Carregar Arquivo de Configuração:"
					+ e.getMessage());

		} catch (IOException e) {
			System.out.println("Erro ao Carregar Configuração:"
					+ e.getMessage());

		}
		try{
		String match = ".*(" + prop.getProperty(campo + "nome") + ").*";
		Integer inicio = Integer.parseInt(prop.getProperty(campo
				+ "campoInicio"));
		Integer fim = Integer.parseInt(prop.getProperty(campo + "campoFim"));
		for (int i = 0; i < text.length - 6; i++) {

			int index;

			if (text[i].matches(match)) {
				index = text[i].indexOf(prop.getProperty(campo + "nome"));
				pesquisa = text[i].subSequence(index + inicio, index + fim)
						.toString();
			}
		}
		}catch(Exception e){
			pesquisa= "";
		}
		return pesquisa;
	}
}
