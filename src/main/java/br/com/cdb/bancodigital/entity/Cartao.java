package br.com.cdb.bancodigital.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Cartao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "numero_da_conta")
	protected Conta conta;

	@Column(name = "NUMERO_CARTAO")
	private String numeroCartao;

	@Column(name = "SALDO")
	protected BigDecimal saldo;

	@Column(name = "ATIVO")
	private Boolean ativo = false;

	@Column(name = "Senha")
	private String senha;

}
