package br.com.accenture.batimento.movel.ssh;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Properties;

import br.com.accenture.batimento.ExecutaLevantamento;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class SSHConnection {

	private Connection connection;
	private Session session;
	private String machine;
	private String user;
	private String password;
	private Boolean debug;
	

	public SSHConnection(String machine, String user, String password) {
		super();
		this.machine = machine;
		this.user = user;
		this.password = password;
	}

	public void makeConnection() {

		try {
			if (debug) {
				System.out
						.println(Calendar.getInstance().getTime()+"|Ação|SSHConnection|Abrindo conexão no Host:"
								+ getMachine());
			}
			this.setConnection(new Connection(this.getMachine()));
			this.getConnection().connect();
			if (debug) {
				System.out
						.println(Calendar.getInstance().getTime()+"|Ação|SSHConnection|Logando no terminal com usuário:"
								+ getUser());
			}
			this.getConnection().authenticateWithPassword(getUser(),
					getPassword());

		} catch (Exception e) {
			if (debug) {
				System.out
						.println(Calendar.getInstance().getTime()+"|Erro|SSHConnection|Não foi Possível conectar ao host:"
								+ getMachine() + "|Causa:" + e.getMessage());
			}
		}
	}

	
	public boolean testConnection(){
		Boolean debugAux = getDebug();
		setDebug(false);
		
		
		try{
						
			ExecutaLevantamento.fos.write(Calendar.getInstance().getTime()+"|Teste|SSHConnection|Abrindo conexão no Host:"+getUser()+"@"+ getMachine());
			ExecutaLevantamento.fos.newLine();
			
			System.out.println(Calendar.getInstance().getTime()+"|Teste|SSHConnection|Abrindo conexão no Host:"+getUser()+"@"+ getMachine());
			this.makeConnection();
			ExecutaLevantamento.fos.write(Calendar.getInstance().getTime()+"|Teste|SSHConnection|Executando comando de teste:"+ getMachine());
			ExecutaLevantamento.fos.newLine();
			System.out.println(Calendar.getInstance().getTime()+"|Teste|SSHConnection|Executando comando de teste:"+ getMachine());
			executeCommand("");
			ExecutaLevantamento.fos.write(Calendar.getInstance().getTime()+"|Teste|SSHConnection|Teste efetuado com sucesso:"+ getMachine());
			ExecutaLevantamento.fos.newLine();
			System.out.println(Calendar.getInstance().getTime()+"|Teste|SSHConnection|Teste efetuado com sucesso:"+ getMachine());
			setDebug(debugAux);
			return true;
		}catch(Exception e){
			try{
			ExecutaLevantamento.fos.write(Calendar.getInstance().getTime()+"|Teste|SSHConnection|Teste efetuado com sucesso:"+ getMachine());
			ExecutaLevantamento.fos.newLine();
			}catch(Exception f){
				
			}
			System.out.println(Calendar.getInstance().getTime()+"|Teste|SSHConnection|Falha ao Realizar Teste:Máquina "+getMachine()+". Erro "+e.getMessage());
			return false;
		}
	}
	
	public String executeCommand(String[] commands) {
		String retorno = "";
		String texto = "";
		BufferedReader br = null;
		java.io.InputStream stdout = null;
		try {
			if (debug) {
				System.out
						.println(Calendar.getInstance().getTime()+"|Ação|SSHConnection|Abrindo Sessão no Host:"
								+ getMachine());
			}
			this.session = getConnection().openSession();
			session.startShell();
			OutputStream outt = session.getStdin();
			outt.write("cd\\ \n".getBytes());
			for (String comando : commands) {
				if (debug) {
					System.out
							.println(Calendar.getInstance().getTime()+"|Ação|SSHConnection|Executando Comando:"
									+ comando);
				}
				
				outt.write(comando.getBytes());
			}
			
			outt.write("finger \n".getBytes());

			stdout = new StreamGobbler(session.getStdout());
			br = new BufferedReader(new InputStreamReader(stdout, "UTF-8"));
			if (debug) {
				System.out
						.println(Calendar.getInstance().getTime()+"|Ação|SSHConnection|Recebendo cadeia de caracter de saída");
			}
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
			if (debug) {
				System.out
						.println(Calendar.getInstance().getTime()+"|Erro|SSHConnection|Não foi possível receber os dados da sessão:"+e.getMessage());
			}

		} catch (Exception h) {
			if (debug) {
				System.out
						.println(Calendar.getInstance().getTime()+"|Erro|SSHConnection|Não foi possível estabelecer uma conexão para a sessão:"+h.getMessage());
			}
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				if (debug) {
					System.out
							.println(Calendar.getInstance().getTime()+"|Erro|SSHConnection|Não foi possível encerrar a conexão para a sessão:"+e.getMessage());
				}
			}
			try {
				stdout.close();
			} catch (IOException e) {
				if (debug) {
					System.out
							.println(Calendar.getInstance().getTime()+"|Erro|SSHConnection|Não foi possível encerrar a conexão para a sessão:"+e.getMessage());
				}
			}
			getConnection().close();
			getSession().close();
		}

		return texto;

	}

	public String executeCommand(String command) {
		String retorno = "";
		String texto = "";
		BufferedReader br = null;
		java.io.InputStream stdout = null;
		try {
			if (debug) {
				System.out
						.println(Calendar.getInstance().getTime()+"|Ação|SSHConnection|Abrindo Sessão no Host:"
								+ getMachine());
			}
			this.session = getConnection().openSession();
			session.startShell();
			OutputStream outt = session.getStdin();
			outt.write("cd\\ \n".getBytes());
			
				if (debug) {
					System.out
							.println(Calendar.getInstance().getTime()+"|Ação|SSHConnection|Executando Comando:"
									+ command);
				}
				
				outt.write(command.getBytes());
			
			
			outt.write("finger \n".getBytes());

			stdout = new StreamGobbler(session.getStdout());
			br = new BufferedReader(new InputStreamReader(stdout, "UTF-8"));
			if (debug) {
				System.out
						.println(Calendar.getInstance().getTime()+"|Ação|SSHConnection|Recebendo cadeia de caracter de saída");
			}
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
			if (debug) {
				System.out
						.println(Calendar.getInstance().getTime()+"|Erro|SSHConnection|Não foi possível receber os dados da sessão:"+e.getMessage());
			}

		} catch (Exception h) {
			if (debug) {
				System.out
						.println(Calendar.getInstance().getTime()+"|Erro|SSHConnection|Não foi possível estabelecer uma conexão para a sessão:"+h.getMessage());
			}
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				if (debug) {
					System.out
							.println(Calendar.getInstance().getTime()+"|Erro|SSHConnection|Não foi possível encerrar a conexão para a sessão:"+e.getMessage());
				}
			}
			try {
				stdout.close();
			} catch (IOException e) {
				if (debug) {
					System.out
							.println(Calendar.getInstance().getTime()+"|Erro|SSHConnection|Não foi possível encerrar a conexão para a sessão:"+e.getMessage());
				}
			}
			getConnection().close();
			getSession().close();
		}

		return texto;

	}


	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public String getMachine() {
		return machine;
	}

	public void setMachine(String machine) {
		this.machine = machine;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getDebug() {
		return debug;
	}

	public void setDebug(Boolean debug) {
		this.debug = debug;
	}

	
}
