package br.com.batimento.jdbc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Properties;

import br.com.accenture.batimento.ExecutaLevantamento;

public class DBConnection {

	Connection con;

	public DBConnection(String banco, Properties propertie) {

		try {
			
			con = DriverManager.getConnection(
					propertie.getProperty(banco + "url"),
					propertie.getProperty(banco + "user"),
					propertie.getProperty(banco + "pswd"));
		} catch (SQLException e) {
			System.out.println("Erro ao Conectar no banco " + banco + ": "
					+ e.getMessage());
			try {
				ExecutaLevantamento.fos.write(Calendar.getInstance().getTime()
						+ "|Teste|SSHConnection|Falha ao Conectar:Banco "
						+ propertie.getProperty(banco + "url") + ". Erro "
						+ e.getMessage());
				ExecutaLevantamento.fos.newLine();
				
			} catch (IOException e1) {
				
			}
		}

	}

	public Connection getCon() {
		return con;
	}

	public void setCon(Connection con) {
		this.con = con;
	}

	public void closeCommit() {
		try {
			this.con.commit();
			this.con.close();
		} catch (SQLException e) {
			System.out.println("Encerrando Transação sem commit:"
					+ e.getMessage());
			try {
				this.con.rollback();
				this.con.close();
			} catch (SQLException e1) {
				System.out
						.println("Erro ao encerrar Conexão:" + e.getMessage());
			}

		}
	}

}
