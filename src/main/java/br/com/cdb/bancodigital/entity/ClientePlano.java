package br.com.cdb.bancodigital.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.persistence.Column;

public enum ClientePlano {
	COMUM(BigDecimal.valueOf(12.00), BigDecimal.valueOf(0.005)),
	SUPER(BigDecimal.valueOf(8.00), BigDecimal.valueOf(0.007)),
	PREMIUM(BigDecimal.valueOf(0), BigDecimal.valueOf(0.009));

	@Column(name = "TAXA_MANUTENCAO")
	private BigDecimal taxaManutencao;
	@Column(name = "TAXA_RENDIMENTO")
	private BigDecimal taxaRendimentoAnual;

	ClientePlano(BigDecimal taxaManutencao, BigDecimal taxaRendimentoAnual) {
		this.taxaManutencao = taxaManutencao;
		this.taxaRendimentoAnual = taxaRendimentoAnual;
	}

	public BigDecimal getTaxaManutencao() {
		return taxaManutencao;
	}

	public BigDecimal getTaxaRendimentoAnual() {
		return taxaRendimentoAnual;
	}

	@JsonCreator
	public static ClientePlano fromString(String value) {
		if (value == null) {
			throw new IllegalArgumentException(
					"Campo clientePlano em branco! Por favor escolha entre COMUM, SUPER ou PREMIUM.");
		}
		for (ClientePlano plano : ClientePlano.values()) {
			if (plano.name().equalsIgnoreCase(value)) {
				return plano;
			}

		}
		throw new IllegalArgumentException("Plano " + value + " inválido! Escolha entre COMUM, SUPER ou PREMIUM.");

	}

}
