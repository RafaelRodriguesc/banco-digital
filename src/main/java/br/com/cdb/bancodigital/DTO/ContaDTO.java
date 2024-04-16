package br.com.cdb.bancodigital.DTO;

import br.com.cdb.bancodigital.entity.ClientePlano;
import br.com.cdb.bancodigital.entity.TipoConta;
import lombok.Data;
@Data
public class ContaDTO {
    private Long clienteId;
    private TipoConta tipoConta;
    private ClientePlano clientePlano;
}
