package br.com.cdb.bancodigital.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class CartaoDebito extends Cartao {

	private BigDecimal limiteDiario = BigDecimal.valueOf(1000);

}
