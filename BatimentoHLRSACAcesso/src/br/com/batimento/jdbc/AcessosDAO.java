package br.com.batimento.jdbc;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import br.com.accenture.batimento.ExecutaLevantamento;

public class AcessosDAO {

	private DBConnection con;
	private DBConnection conAtualizadora;
	private Properties properties;
	String banco;
	
	private static String PROC_LEVANTAMENTO = "call p_ext_sub_simcard_pre_pago()";
	private static String PROC_BATIMENTO = "call p_batimento_movel_pre()";
	private static String SELECT_ACESSO_GSM = "select acesso from tb_acesso where THREADC = ?";
	private static String INSERT_SERVICO = "INSERT INTO T_EXTRACAO_SERVICO (ACESSO,SERVICO) VALUES(?,?)";
	private static String SELECT_ACESSO_CLY = "SELECT	x_valor_atrib"
			+ " FROM	table_x_selec_atrib@dl_sif_crm"
			+ " WHERE	x_selec_atrib2contr_itm IN"
			+ " (SELECT	 objid	FROM	 table_contr_itm@dl_sif_crm"
			+ "  WHERE	 child2contr_itm IN (select objid from table_contr_itm@dl_sif_crm where quote_sn in(?)))"
			+ " AND x_cod_atrib = 'simcard_imsi'";


	private static String SELECT_ACESSO_SISGEN = "SELECT	 s.simc_imsi 	FROM	 SISGEN.MSISDN_SIMCARD SM, SISGEN.SIMCARD S WHERE	 S.simc_iccid = sm.msim_iccid AND SM.msim_msisdn IN (?)";
	private Boolean debug;

	public Boolean getDebug() {
		return debug;
	}

	public void setDebug(Boolean debug) {
		this.debug = debug;
	}

	public AcessosDAO(String banco, Properties properties) {
		this.properties = properties;
		this.banco = banco;
		con = new DBConnection(banco, properties);
	}

	public void newConnection() {
		con = new DBConnection(banco, properties);
	}

	public boolean testConnection() {

		try {
			ExecutaLevantamento.fos.write(Calendar.getInstance().getTime()
					+ "|Teste|DBConnection|Abrindo conexão no Banco:"
					+ properties.getProperty(banco + "user") + "@"
					+ properties.getProperty(banco + "url"));
			ExecutaLevantamento.fos.newLine();
		} catch (IOException e1) {
			
		}
		
		
		System.out.println(Calendar.getInstance().getTime()
				+ "|Teste|DBConnection|Abrindo conexão no Banco:"
				+ properties.getProperty(banco + "user") + "@"
				+ properties.getProperty(banco + "url"));
		PreparedStatement pst = null;
		try {
			try {
				ExecutaLevantamento.fos.write(Calendar.getInstance().getTime()
						+ "|Teste|DBConnection|Executando comando de teste:"
						+ properties.getProperty(banco + "url"));
				ExecutaLevantamento.fos.newLine();
			} catch (IOException e1) {
				
			}
			
			System.out.println(Calendar.getInstance().getTime()
					+ "|Teste|DBConnection|Executando comando de teste:"
					+ properties.getProperty(banco + "url"));
			pst = con.getCon().prepareStatement("SELECT sysdate from dual");
			pst.execute();
			ExecutaLevantamento.fos.write(Calendar.getInstance().getTime()
					+ "|Teste|SSHConnection|Teste Executado com sucesso:"
					+ properties.getProperty(banco + "url"));
			ExecutaLevantamento.fos.newLine();
			return true;
		} catch (SQLException e) {
			try {
				ExecutaLevantamento.fos.write(Calendar.getInstance().getTime()
						+ "|Teste|SSHConnection|Falha ao Realizar Teste:Banco "
						+ properties.getProperty(banco + "url") + ". Erro "
						+ e.getMessage());
				ExecutaLevantamento.fos.newLine();
				return false;
			} catch (IOException e1) {
				
			}
			System.out.println(Calendar.getInstance().getTime()
					+ "|Teste|SSHConnection|Falha ao Realizar Teste:Banco "
					+ properties.getProperty(banco + "url") + ". Erro "
					+ e.getMessage());
			return false;
		}catch(Exception h){
			try {
				ExecutaLevantamento.fos.write(h.getMessage());
				ExecutaLevantamento.fos.newLine();
			} catch (IOException e) {
				
			}
			
			return false;
		}finally {
			try {

				pst.close();
				con.closeCommit();
			} catch (SQLException h) {
				System.out.println(h.getMessage());
				return false;
			}
		}
	}

	public List<String> listarAcessos(String t) {

		newConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> resultado = new ArrayList<String>();
		if (debug) {
			System.out.println(Calendar.getInstance().getTime()
					+ "|Ação|AcessosDAO|Listando acessos para consulta");
		}
		try {
			pst = con.getCon().prepareStatement(SELECT_ACESSO_GSM);
			pst.setString(1, t);
			rs = pst.executeQuery();

			while (rs.next()) {
				resultado.add(rs.getString(1));

			}

		} catch (SQLException e) {
			System.out.println(Calendar.getInstance().getTime()
					+ "|Erro na execução da Query:Listagem Acesso." + "\n"
					+ e.getMessage());

		} finally {
			try {
				rs.close();
				pst.close();
				con.closeCommit();
			} catch (SQLException e) {
				System.out.println(Calendar.getInstance().getTime()
						+ "|Erro na execução da Query:Listagem Acesso." + "\n"
						+ e.getMessage());
			}

		}

		return resultado;

	}

	public boolean executaLevantamento() {

		newConnection();
		Boolean retorno;
		PreparedStatement pst = null;
		
		
		try {
			pst = con.getCon().prepareCall(PROC_LEVANTAMENTO);
			retorno = pst.execute();

		} catch (SQLException e) {
			System.out.println(Calendar.getInstance().getTime()
					+ "|Erro na execução da Query:PROC_LEVANTAMENTO " + e.getSQLState() + "\n"
					+ e.getMessage());
			retorno = false;
		} finally {
			try {
				pst.close();
				con.closeCommit();
			} catch (SQLException e) {
				System.out.println(Calendar.getInstance().getTime()
						+ "|Erro na execução da Query:PROC_LEVANTAMENTO " + e.getSQLState() + "\n"
						+ e.getMessage());
			}

		}
		return retorno;

	}

	public boolean executaBatimento() {

		newConnection();
		Boolean retorno = true;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> resultado = new ArrayList<String>();
		
		try {
			pst = con.getCon().prepareCall(PROC_BATIMENTO);
			retorno = pst.execute();

		} catch (SQLException e) {
			System.out.println(Calendar.getInstance().getTime()
					+ "|Erro na execução da Query:PROC_BATIMENTO " + e.getSQLState() + "\n"
					+ e.getMessage());
			retorno = false;
		} finally {
			try {
				pst.close();
				con.closeCommit();
			} catch (SQLException e) {
				System.out.println(Calendar.getInstance().getTime()
						+ "|Erro na execução da Query:PROC_BATIMENTO " + e.getSQLState() + "\n"
						+ e.getMessage());
			}

		}
		return retorno;

	}

	
	public String getIMSICLY(String acesso) {
		newConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;
		String resultado = "";

		try {
			pst = con.getCon().prepareStatement(SELECT_ACESSO_CLY);
			pst.setString(1, acesso);
			rs = pst.executeQuery();

			while (rs.next()) {
				resultado = rs.getString(1);

			}

		} catch (SQLException e) {
			resultado = "";
			System.out.println(e.getMessage());
			System.out.println(Calendar.getInstance().getTime()
					+ "|Erro na execução da Query:SELECT_ACESSO_CLY.'"+acesso+"'" + e.getSQLState() + "\n"
					+ e.getMessage());

		} finally {
			try {
				rs.close();
				pst.close();
				con.closeCommit();
			} catch (SQLException e) {
				System.out.println(Calendar.getInstance().getTime()
						+ "|Erro na execução da Query:SELECT_ACESSO_CLY.'"+acesso+"'"+ e.getSQLState() + "\n"
						+ e.getMessage());			}

		}
		return resultado;
	}

	public String getIMSISISGEN(String acesso) {
		newConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;
		String resultado = "";

		try {
			pst = con.getCon().prepareStatement(SELECT_ACESSO_SISGEN);
			pst.setString(1, acesso);
			rs = pst.executeQuery();

			while (rs.next()) {
				resultado = rs.getString(1);

			}

		} catch (SQLException e) {
			resultado = "";
			System.out.println(e.getMessage());
			System.out.println(Calendar.getInstance().getTime()
					+ "|Erro na execução da Query:SELECT_ACESSO_SISGEN.'"+acesso+"'"+ e.getSQLState() + "\n"
					+ e.getMessage());

		} finally {
			try {
				rs.close();
				pst.close();
				con.closeCommit();
			} catch (SQLException e) {
				System.out.println(Calendar.getInstance().getTime()
						+ "|Erro na execução da Query:SELECT_ACESSO_SISGEN.'"+acesso+"'"+e.getSQLState() + "\n"
						+ e.getMessage());
			}

		}
		return resultado;
	}

	public void atualizarCampo(String campo, String valor, String criterio) {

		newConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> resultado = new ArrayList<String>();
		String update = "Update tb_ext_s_simcard_acesso set " + campo + " = ?  where acesso = '" + criterio + "'";

		try {
			
			pst = con.getCon().prepareStatement(update);
			pst.setString(1,valor );
			rs = pst.executeQuery();

		} catch (SQLException e) {
			System.out.println(Calendar.getInstance().getTime()
					+ "|Erro na execução da Query:ATUALIZAR_CAMPO." +"Campo='"+campo+"',valor='"+valor+"',acesso='"+criterio+"'"+ e.getSQLState() + "\n"
					+ e.getMessage());
		} finally {
			try {
				rs.close();
				pst.close();
				con.closeCommit();
			} catch (SQLException e) {
				System.out.println(Calendar.getInstance().getTime()
						+ "|Erro na execução da Query:ATUALIZAR_CAMPO." +"Campo='"+campo+"',valor='"+valor+"',acesso='"+criterio+"'"+ e.getSQLState() + "\n"
						+ e.getMessage());
			}

		}
		
		
		
	}

	
	public void insereServico(String acesso, String servico) {

		newConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> resultado = new ArrayList<String>();
		
		try {
			pst = con.getCon().prepareStatement(INSERT_SERVICO);
			pst.setString(1,acesso);
			pst.setString(2,servico.replace("'", ""));
			rs = pst.executeQuery();

		} catch (SQLException e) {
			System.out.println(Calendar.getInstance().getTime()
					+ "|Erro na execução da Query:INSERT_SERVICO."+"Acesso='"+acesso+"',Serviço='"+servico+"'"+ e.getSQLState() + "\n"
					+ e.getMessage());
		} finally {
			try {
				rs.close();
				pst.close();
				con.closeCommit();
			} catch (SQLException e) {
				System.out.println(Calendar.getInstance().getTime()
						+ "|Erro na execução da Query:INSERT_SERVICO."+"Acesso='"+acesso+"',Serviço='"+servico+"'"+ e.getSQLState() + "\n"
						+ e.getMessage());
			}

		}
		
		
		
	}


	
	
}