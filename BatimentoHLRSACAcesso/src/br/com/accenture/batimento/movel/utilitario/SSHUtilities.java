package br.com.accenture.batimento.movel.utilitario;

public class SSHUtilities {
	
	String campo;
	String valor;
	String clob;
	SSHGabarito gabarito;
	
	
	
	
	public SSHGabarito getGabarito() {
		return gabarito;
	}
	public void setGabarito(SSHGabarito gabarito) {
		this.gabarito = gabarito;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public String getCampo() {
		return campo;
	}
	public void setCampo(String campo) {
		this.campo = campo;
	}
	public String getValor(String resultado) {
		String pesquisa = "";
		String[] text = resultado.split("\n");
		getGabarito().setNome(getCampo());
		try{
			String match = ".*(" + getGabarito().getNome() + ").*";
			Integer inicio = Integer.parseInt(getGabarito().getCampoInicio());
			Integer fim = Integer.parseInt(getGabarito().getCampoFim());
			for (int i = 0; i < text.length - 6; i++) {

				int index;

				if (text[i].matches(match)) {
					index = text[i].indexOf(getGabarito().getNome());
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
