package br.com.cdb.bancodigital.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoConta {
	CORRENTE, POUPANCA;

	@JsonCreator
	public static TipoConta FormString(String value) {
		for (TipoConta conta : TipoConta.values()) {
			if (conta.name().equalsIgnoreCase(value)) {
				return conta;
			}
		}
		throw new IllegalArgumentException("Tipo de conta " + value + " inválida! Escolha entre CORRENTE ou POUPANCA.");

	}

}
