package br.com.cdb.bancodigital.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class CartaoCredito extends Cartao {

	private BigDecimal limiteCredito;

	private BigDecimal seguroFraude = BigDecimal.valueOf(5000.00);

	@OneToOne
	private SeguroApolice seguroApolice;

	private LocalDateTime dataEmissao;

	private Boolean seguroViagemAtivo = false;

}
