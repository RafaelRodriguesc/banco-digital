package br.com.cdb.bancodigital.DTO;

import lombok.Data;

@Data 
public class DesbloqueiaCartaoDTO {
    private String numeroCartao;
    private Boolean ativo;
    private String senha;

}
