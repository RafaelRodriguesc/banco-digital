package br.com.cdb.bancodigital.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class ContaPoupanca extends Conta {
	@Column(name = "TAXA_RENDIMENTO")
	private BigDecimal taxaRendimento = BigDecimal.ZERO;

	public ContaPoupanca() {

	}

	@Override
	public BigDecimal consultarSaldo() {
		return this.getSaldo();
	}

	public BigDecimal getTaxaRendimento() {
		return taxaRendimento;
	}

	public void setTaxaRendimento(BigDecimal taxaRendimento) {
		this.taxaRendimento = taxaRendimento;
	}

	public String getTaxaManutencaoFormatada() {
		return "Não aplicável";
	}

}
