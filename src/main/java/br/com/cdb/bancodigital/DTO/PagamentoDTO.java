package br.com.cdb.bancodigital.DTO;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PagamentoDTO {
    private String numeroCartao;
    private String senha;
    private BigDecimal valor;

}
