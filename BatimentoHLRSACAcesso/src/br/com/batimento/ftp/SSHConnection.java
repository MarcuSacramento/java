package br.com.batimento.ftp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class SSHConnection {

	Connection con;
	Properties prop;
	private Session session;

	/*
	 * Construtor
	 */
	public SSHConnection(String maquina) {

		/*
		 * Carregando arquivo de Propriedades
		 */
		prop = new Properties();
		try {
			prop.load(new FileInputStream("C:\\workspace\\prop.properties"));
		} catch (FileNotFoundException e) {
			System.out.println("Arquivo de Configuração não encontrado:"
					+ e.getMessage());

		} catch (IOException e) {
			System.out.println("Erro ao Carregar Configuração:"
					+ e.getMessage());

		}

		/*
		 * Abrindo Conexão
		 */
		try {

			System.out.println("Iniciando conexão com a máquina " + maquina
					+ " no IP " + prop.getProperty(maquina + "Host")
					+ ".Usuário:" + prop.getProperty(maquina + "User"));
			con = new Connection(prop.getProperty(maquina + "Host"));
			con.connect();
			con.authenticateWithPassword(prop.getProperty(maquina + "User"),
					prop.getProperty(maquina + "Password"));
			System.out.println("Sucesso na conexão com a máquina " + maquina
					+ " no IP " + prop.getProperty(maquina + "Host")
					+ ".Usuário:" + prop.getProperty(maquina + "User"));
		} catch (IOException e) {
			System.out.println("Erro ao Abrir Conexão no Servidor " + maquina
					+ ":" + prop.getProperty(maquina + "Host")
					+ " para o usuário " + prop.getProperty(maquina + "User")
					+ ": " + e.getMessage());

		}

		session = null;
	}

	public Connection getCon() {
		return con;
	}

	public void setCon(Connection con) {
		this.con = con;
	}

	public Properties getProp() {
		return prop;
	}

	public void setProp(Properties prop) {
		this.prop = prop;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public String FTPExecute(String[] cmd) {

		String retorno = "";
		String texto = "";
		BufferedReader br = null;
		java.io.InputStream stdout = null;
		Connection con;
		try {
			this.session = null;

			con = getCon();
			System.out.println("Abrindo Sessão");
			this.session = con.openSession();
			session.startShell();
			OutputStream outt = session.getStdin();
			outt.write("cd\\ \n".getBytes());

			for (String comando : cmd) {
				System.out.print("Comando a executar:");
				outt.write(comando.getBytes());
				System.out.println("\n");
			}
			outt.write("finger \n".getBytes());

			stdout = new StreamGobbler(session.getStdout());
			br = new BufferedReader(new InputStreamReader(stdout, "UTF-8"));

			export: for (int i = 0; i < 100; i++) {
				String line = br.readLine() + "\n";
				texto += line;
				if (((texto.indexOf("Bldg.") > -1)
						&& (texto.indexOf("TTY Idle") > -1) && (texto
						.indexOf("Login") > -1))
						|| (texto.indexOf("logged") > -1)) {
					break export;
				}
			}
		} catch (IOException e) {
			System.out.println("Erro ao executar Comando:" + e.getMessage());

		} catch (Exception h) {
			System.out.println(h.getMessage());
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				stdout.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.con.close();
			session.close();
		}

		return texto;

	}

	public String FTPExecute(Connection connection, String[] cmd) {

		String retorno = "";
		String texto = "";
		BufferedReader br = null;
		java.io.InputStream stdout = null;

		try {
			this.session = null;
			System.out.println("Abrindo Sessão:"+con.getHostname());
			this.session = connection.openSession();
			
			session.startShell();
			OutputStream outt = session.getStdin();
			outt.write("cd\\ \n".getBytes());

			for (String comando : cmd) {
				System.out.print("Comando a executar:");
				outt.write(comando.getBytes());
				System.out.println("\n");
			}
			outt.write("finger \n".getBytes());

			stdout = new StreamGobbler(session.getStdout());
			br = new BufferedReader(new InputStreamReader(stdout, "UTF-8"));

			export: for (int i = 0; i < 100; i++) {
				String line = br.readLine() + "\n";
				texto += line;
				if (((texto.indexOf("Bldg.") > -1)
						&& (texto.indexOf("TTY Idle") > -1) && (texto
						.indexOf("Login") > -1))
						|| (texto.indexOf("logged") > -1)) {
					break export;
				}
			}
		} catch (ch.ethz.ssh2.SFTPException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println("Erro ao executar Comando:" + e.getMessage());

		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				stdout.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.con.close();
			session.close();
		}

		return texto;

	}

}
