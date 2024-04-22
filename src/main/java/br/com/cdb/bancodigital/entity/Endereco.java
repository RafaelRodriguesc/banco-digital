package br.com.cdb.bancodigital.entity;

import lombok.Data;

@Data
public class Endereco {
	private String cep;
	private String logradouro;
	private String complemento;
	private String bairro;
	private String localidade;
	private String uf;
	
	@Override
	public String toString() {
	    return logradouro + ", " +
	            bairro + ", " +
	            localidade + ", " +
	            uf;
	}

}
