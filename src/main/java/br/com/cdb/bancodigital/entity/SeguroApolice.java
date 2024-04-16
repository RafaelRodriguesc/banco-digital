package br.com.cdb.bancodigital.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class SeguroApolice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigoApolice;

	private LocalDateTime dataContratacao;
	private String detalhesCartao;
	private BigDecimal valorApolice;
	private String descricaoCondicoes;

	public String getDetalhesCartao() {
		return detalhesCartao;
	}

	public void setDetalhesCartao(String detalhesCartao) {
		this.detalhesCartao = detalhesCartao;
	}
}
