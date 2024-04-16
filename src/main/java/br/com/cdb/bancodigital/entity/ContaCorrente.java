package br.com.cdb.bancodigital.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class ContaCorrente extends Conta {
	@Column(name = "TAXA_MANUTENCAO")
	private BigDecimal taxaManutencao = BigDecimal.ZERO;

	public ContaCorrente() {

	}

	@Override
	public BigDecimal consultarSaldo() {
		return this.getSaldo();
	}

	public BigDecimal getTaxaManutencao() {
		return taxaManutencao;
	}

	public void setTaxaManutencao(BigDecimal taxaManutencao) {
		this.taxaManutencao = taxaManutencao;
	}

	public String getTaxaRendimentoFormatada() {
		return "Não aplicável";
	}

}
