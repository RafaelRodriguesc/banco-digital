package br.com.cdb.bancodigital.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.cdb.bancodigital.entity.Cliente;
import br.com.cdb.bancodigital.entity.ClientePlano;
import br.com.cdb.bancodigital.entity.ContaPoupanca;
import br.com.cdb.bancodigital.entity.TipoConta;

@Repository
public interface ContaPoupancaRepository extends JpaRepository<ContaPoupanca, Long> {
	Optional<ContaPoupanca> findByNumeroDaConta(Long numeroDaConta);

	Optional<ContaPoupanca> findByCliente(Cliente cliente);

	Optional<ContaPoupanca> findByCliente_Id(Long clienteId);

	Optional<ContaPoupanca> findByCliente_CpfAndTipoConta(String cpf, TipoConta tipoConta);

	List<ContaPoupanca> findByClientePlano(ClientePlano clientePlano);

}
