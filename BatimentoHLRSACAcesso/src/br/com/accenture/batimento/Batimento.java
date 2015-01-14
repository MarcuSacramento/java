package br.com.accenture.batimento;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import br.com.accenture.batimento.movel.ssh.SSHConnection;
import br.com.accenture.batimento.movel.utilitario.SSHGabarito;
import br.com.accenture.batimento.movel.utilitario.SSHUtilities;
import br.com.batimento.jdbc.AcessosDAO;
public class Batimento implements Runnable{

	public static BufferedWriter fos;
	String[] args;
	String thread;
	public String batimento(String[] args,String t){
		
		Properties properties = new Properties();
		Boolean inicia = false;
		Boolean debug = true;
		Calendar dataAtual = Calendar.getInstance();
		String dataAtualArq = "";
		 
		File arquivo;
		try {
			properties.load(new FileInputStream(args[0]));
		} catch (FileNotFoundException e) {
			System.out.println("Erro ao Carregar Arquivo de Configuração:"
					+ e.getMessage());

		} catch (IOException e) {
			System.out.println("Erro ao Carregar Configuração:"
					+ e.getMessage());

		}
		dataAtualArq = "" + dataAtual.get(Calendar.YEAR)
				+ String.format("%02d", dataAtual.get(Calendar.MONTH) + 1)
				+ String.format("%02d", dataAtual.get(Calendar.DAY_OF_MONTH));
		arquivo = new File(properties.getProperty("logfile")
				+ "LOG_LEVANTAMENTO" + dataAtualArq + ".csv");
		
		try {
			fos = new BufferedWriter(new FileWriter(arquivo));	
			// FileOutputStream fos = new FileOutputStream(arquivo);
			fos.write(("--------------------------"
					+ " Batimento HLR x GPP x SISGEN x Clarify "
					+ "--------------------------\n"));
			fos.newLine();

			System.out.println("--------------------------"
					+ " Batimento HLR x GPP x SISGEN x Clarify "
					+ "--------------------------");
			fos.write(("-------------------------- "
					+ Calendar.getInstance().getTime() + " --------------------------"));
			fos.newLine();
			System.out.println("-------------------------- "
					+ Calendar.getInstance().getTime()
					+ " --------------------------");
			SSHConnection hlrConnection = new SSHConnection(
					properties.getProperty("hlrHost"),
					properties.getProperty("hlrUser"),
					properties.getProperty("hlrPassword"));
			SSHConnection gppConnection = new SSHConnection(
					properties.getProperty("gppHost"),
					properties.getProperty("gppUser"),
					properties.getProperty("gppPassword"));
			hlrConnection.setDebug(debug);
			gppConnection.setDebug(debug);
			AcessosDAO dbglobalDAO = new AcessosDAO("dbglobal", properties);
			AcessosDAO sisgenDAO = new AcessosDAO("sisgen", properties);
			if (debug) {

				fos.write((Calendar.getInstance().getTime() + "|Ação|Executa Levantamento|Execução de Testes"));
				fos.newLine();
				System.out.println(Calendar.getInstance().getTime()
						+ "|Ação|Executa Levantamento|Execução de Testes");
			}
			if (hlrConnection.testConnection()
					&& gppConnection.testConnection()
					&& dbglobalDAO.testConnection()
					&& sisgenDAO.testConnection()) {
				fos.write(("------------------------------------------------------------------------------------"));
				fos.newLine();
				fos.write(("------------------------------------------------------------------------------------"));
				fos.newLine();
				System.out
						.println("------------------------------------------------------------------------------------");
				System.out
						.println("------------------------------------------------------------------------------------");
				if (debug) {
					fos.write((Calendar.getInstance().getTime() + "|Ação|Executa Levantamento|Realizando levantamento dos acessos"));
					fos.newLine();
					System.out
							.println(Calendar.getInstance().getTime()
									+ "|Ação|Executa Levantamento|Realizando levantamento dos acessos");
				}
				fos.flush();
				inicia = true;
			}
			fos.write(("------------------------------------------------------------------------------------"));
			fos.newLine();
			fos.write(("------------------------------------------------------------------------------------"));
			fos.newLine();
			fos.write((Calendar.getInstance().getTime() + "|Execução|Batimento HLR|Levantando Acessos Clarify"));
			fos.newLine();
			System.out.println(Calendar.getInstance().getTime()
					+ "|Execução|Batimento HLR|Levantando Acessos Clarify");

			fos.write(("------------------------------------------------------------------------------------"));
			fos.newLine();
			fos.write(("------------------------------------------------------------------------------------"));
			fos.newLine();

			//inicia = dbglobalDAO.executaLevantamento();
			if (inicia = true) {
				fos.write(("------------------------------------------------------------------------------------"));
				fos.newLine();
				fos.write(("------------------------------------------------------------------------------------"));
				fos.newLine();

				System.out
						.println("------------------------------------------------------------------------------------");
				System.out
						.println("------------------------------------------------------------------------------------");

				fos.write((Calendar.getInstance().getTime() + "|Execução|Batimento HLR|Iniciando Batimento"));
				fos.newLine();

				System.out.println(Calendar.getInstance().getTime()
						+ "|Execução|Batimento HLR|Iniciando Batimento");
				dbglobalDAO.setDebug(debug);
				hlrConnection.setDebug(false);
				gppConnection.setDebug(false);

				List<String> listaAcessos = dbglobalDAO.listarAcessos(t);
				SSHGabarito campo = new SSHGabarito(properties);

				System.out
						.println("------------------------------------------------------------------------------------");
				System.out
						.println("------------------------------------------------------------------------------------");
				fos.write((Calendar.getInstance().getTime() + "|Execução|Batimento HLR|Iterando Acessos para get de Informação.Thread:"+t));
				fos.newLine();
				System.out
						.println(Calendar.getInstance().getTime()
								+ "|Execução|Batimento HLR|Iterando Acessos para get de Informação");
				fos.write(("------------------------------------------------------------------------------------"));
				fos.newLine();
				fos.write(("------------------------------------------------------------------------------------"));
				fos.newLine();

				System.out
						.println("------------------------------------------------------------------------------------");
				System.out
						.println("------------------------------------------------------------------------------------");
				for (String acesso : listaAcessos) {
					fos.write((Calendar.getInstance().getTime()
							+ "|Execução Thread:"+t+"|Batimento HLR|Levantando Informações para o acesso:" + acesso));
					fos.newLine();
					System.out
							.println(Calendar.getInstance().getTime()
									+ "|Execução Thread:"+t+"|Batimento HLR|Levantando Informações para o acesso:"
									+ acesso);
					String imsiCLY, imsiSISGEN, retornoGPP, retornoHLR;
					imsiCLY = dbglobalDAO.getIMSICLY(acesso);
					dbglobalDAO.atualizarCampo("imsi_cly", imsiCLY, acesso);
					imsiSISGEN = sisgenDAO.getIMSISISGEN(acesso);
					dbglobalDAO.atualizarCampo("imsi_sisgen", imsiSISGEN,
							acesso);

					String[] cmd = { "", "", "", "" };
					cmd[0] = "sudo /usr/bin/su - seasm24\n";
					cmd[1] = "cd " + properties.getProperty("gppPath") + "\n";
					cmd[2] = properties.getProperty("gppCmd") + " " + acesso
							+ "\n";
					gppConnection.makeConnection();
					
					Thread.sleep(9 * 1000);
					retornoGPP = gppConnection.executeCommand(cmd);

					cmd[0] = "cd " + properties.getProperty("HLRPath") + "\n";
					cmd[1] = properties.getProperty("HLRCmd") + " " + acesso
							+ "\n";
					hlrConnection.makeConnection();
					retornoHLR = hlrConnection.executeCommand(cmd);
					Thread.sleep(15 * 1000);
					SSHUtilities util = new SSHUtilities();
					util.setCampo("IMSI");
					util.setGabarito(campo);
					dbglobalDAO.atualizarCampo("imsi_hlr",
							util.getValor(retornoHLR), acesso);

					util.setCampo("ESTADO");
					util.setGabarito(campo);
					
					
					
					dbglobalDAO.atualizarCampo("estado_hlr",
							util.getValor(retornoHLR), acesso);

					util.setCampo("CATEGORIA");
					util.setGabarito(campo);
					dbglobalDAO.atualizarCampo("categoria_hlr",
							util.getValor(retornoHLR), acesso);

					util.setCampo("TECH");
					util.setGabarito(campo);
					dbglobalDAO.atualizarCampo("tecnologia_hlr",
							util.getValor(retornoHLR), acesso);

					util.setCampo("IMSIGPP");
					util.setGabarito(campo);
					dbglobalDAO.atualizarCampo("imsi_gpp",
							util.getValor(retornoGPP), acesso);

					util.setCampo("MSISDNGPP");
					util.setGabarito(campo);
					dbglobalDAO.atualizarCampo("msisdn_gpp",
							util.getValor(retornoGPP), acesso);
					dbglobalDAO.atualizarCampo("SERVICO_HLR",
							retornoHLR, acesso);
					fos.flush();
				}
				fos.write(("------------------------------------------------------------------------------------"));
				fos.newLine();
				fos.write(("------------------------------------------------------------------------------------"));
				fos.newLine();
				fos.write((Calendar.getInstance().getTime() + "|Execução|Batimento HLR|Levantamento HLR x GPP x SISGEN x Clarify Finalizado"));
				fos.newLine();
				System.out
						.println("------------------------------------------------------------------------------------");
				System.out
						.println("------------------------------------------------------------------------------------");
				System.out
						.println(Calendar.getInstance().getTime()
								+ "|Execução|Batimento HLR|Levantamento HLR x GPP x SISGEN x Clarify Finalizado");
				System.out
						.println("------------------------------------------------------------------------------------");
				System.out
						.println("------------------------------------------------------------------------------------");

				fos.write(("------------------------------------------------------------------------------------"));
				fos.newLine();
				fos.write(("------------------------------------------------------------------------------------"));
				fos.newLine();
				fos.write((Calendar.getInstance().getTime() + "|Execução|Batimento HLR|Realizando Batimento"));
				fos.newLine();
				System.out.println(Calendar.getInstance().getTime()
						+ "|Execução|Batimento HLR|Realizando Batimento");

				fos.write(("------------------------------------------------------------------------------------"));
				fos.newLine();
				fos.write(("------------------------------------------------------------------------------------"));
				fos.newLine();

				inicia = dbglobalDAO.executaBatimento();
			}

			if (inicia) {
				System.out
						.println("------------------------------------------------------------------------------------");
				System.out
						.println("------------------------------------------------------------------------------------");
				System.out
						.println(Calendar.getInstance().getTime()
								+ "|Execução|Batimento HLR|Batimento realizado com sucesso");

				fos.write(("------------------------------------------------------------------------------------"));
				fos.newLine();
				fos.write(("------------------------------------------------------------------------------------"));
				fos.newLine();
				fos.write((Calendar.getInstance().getTime() + "|Execução|Batimento HLR|Batimento realizado com sucesso"));
				fos.newLine();
				fos.write(("------------------------------------------------------------------------------------"));
				fos.newLine();
				fos.write(("------------------------------------------------------------------------------------"));
				fos.newLine();
				System.out
						.println("------------------------------------------------------------------------------------");
				System.out
						.println("------------------------------------------------------------------------------------");
			} else {
				System.out
						.println("------------------------------------------------------------------------------------");
				System.out
						.println("------------------------------------------------------------------------------------");
				System.out
						.println(Calendar.getInstance().getTime()
								+ "|Execução|Batimento HLR|Erro no Levantamento das Informações");
				System.out
						.println("------------------------------------------------------------------------------------");
				System.out
						.println("------------------------------------------------------------------------------------");

				fos.write(("------------------------------------------------------------------------------------"));
				fos.newLine();
				fos.write(("------------------------------------------------------------------------------------"));
				fos.newLine();
				fos.write((Calendar.getInstance().getTime() + "|Execução|Batimento HLR|Erro no Levantamento das Informações"));
				fos.newLine();
				fos.write(("------------------------------------------------------------------------------------"));
				fos.newLine();
				fos.write(("------------------------------------------------------------------------------------"));
				fos.newLine();
				fos.flush();

			}
			fos.close();
		} catch (Exception e) {
			return null;
		}
		try {
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static BufferedWriter getFos() {
		return fos;
	}


	public static void setFos(BufferedWriter fos) {
		Batimento.fos = fos;
	}


	public String[] getArgs() {
		return args;
	}


	public void setArgs(String[] args) {
		this.args = args;
	}


	public String getThread() {
		return thread;
	}


	public void setThread(String thread) {
		this.thread = thread;
	}


	@Override
	public void run() {
		batimento(getArgs(),getThread());
		
	}

	
	
}
