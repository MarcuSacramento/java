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

public class ExecutaLevantamento {

	public static BufferedWriter fos;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Batimento bat = new Batimento();
		bat.setArgs(args);

		try {
			bat.setThread("1");
			Thread threadConsulta1 = new Thread(bat);
			threadConsulta1.start();
			Thread.sleep(50 * 1000);

			bat.setThread("2");
			Thread threadConsulta2 = new Thread(bat);
			threadConsulta2.start();
			Thread.sleep(19 * 1000);

			bat.setThread("3");
			Thread threadConsulta3 = new Thread(bat);
			threadConsulta3.start();
			Thread.sleep(25 * 1000);

			bat.setThread("4");
			Thread threadConsulta4 = new Thread(bat);
			threadConsulta4.start();
			Thread.sleep(40 * 1000);

			bat.setThread("5");
			Thread threadConsulta5 = new Thread(bat);
			threadConsulta5.start();
			Thread.sleep(11 * 1000);

			bat.setThread("6");
			Thread threadConsulta6 = new Thread(bat);
			threadConsulta6.start();
			Thread.sleep(15 * 1000);

			bat.setThread("7");
			Thread threadConsulta7 = new Thread(bat);
			threadConsulta7.start();
			Thread.sleep(5 * 1000);

			bat.setThread("8");
			Thread threadConsulta8 = new Thread(bat);
			//threadConsulta8.start();
			//Thread.sleep(15 * 1000);
			bat.setThread("9");
			Thread threadConsulta9 = new Thread(bat);
			//threadConsulta9.start();
			//Thread.sleep(15 * 1000);
			bat.setThread("10");
			Thread threadConsulta10 = new Thread(bat);
			//threadConsulta10.start();
			//Thread.sleep(15 * 1000);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}