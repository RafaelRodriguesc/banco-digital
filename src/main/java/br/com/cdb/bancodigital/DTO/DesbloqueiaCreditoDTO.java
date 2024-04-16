package br.com.cdb.bancodigital.DTO;

import lombok.Data;

@Data
public class DesbloqueiaCreditoDTO {
    private String numeroCartao;
    private Boolean ativo;
    private Boolean seguroViagemAtivo;
    private String senha;

}
