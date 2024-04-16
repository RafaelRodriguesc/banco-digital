package br.com.cdb.bancodigital.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Conta {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long numeroDaConta;
	@ManyToOne
	private Cliente cliente;
	@Enumerated(EnumType.STRING)
	private TipoConta tipoConta;
	@Enumerated(EnumType.STRING)
	@NotNull(message = "Campo clientePlano em branco! Por favor escolha entre COMUM, SUPER ou PREMIUM.")
	private ClientePlano clientePlano;
	private BigDecimal saldo = BigDecimal.ZERO;

	public BigDecimal consultarSaldo() {
		return saldo;
	}

}
