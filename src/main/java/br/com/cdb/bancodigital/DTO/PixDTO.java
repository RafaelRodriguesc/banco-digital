package br.com.cdb.bancodigital.DTO;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PixDTO {
    private Long numeroDaContaOrigem;
    private String cpfDestino;
    private BigDecimal valor;
	

}
