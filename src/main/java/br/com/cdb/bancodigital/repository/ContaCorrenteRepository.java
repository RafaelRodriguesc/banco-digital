package br.com.cdb.bancodigital.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.cdb.bancodigital.entity.Cliente;
import br.com.cdb.bancodigital.entity.ClientePlano;
import br.com.cdb.bancodigital.entity.ContaCorrente;
import br.com.cdb.bancodigital.entity.TipoConta;

@Repository
public interface ContaCorrenteRepository extends JpaRepository<ContaCorrente, Long> {
	Optional<ContaCorrente> findByNumeroDaConta(Long numeroDaConta);

	Optional<ContaCorrente> findByCliente(Cliente cliente);

	Optional<ContaCorrente> findByCliente_Id(Long clienteId);

	Optional<ContaCorrente> findByCliente_CpfAndTipoConta(String cpf, TipoConta tipoConta);

	Optional<ContaCorrente> findByNumeroDaContaAndTipoConta(Long numeroDaContaOrigem, TipoConta tipoConta);

	List<ContaCorrente> findByClientePlano(ClientePlano clientePlano);

	boolean existsByCliente_CpfAndTipoConta(String cpf, TipoConta tipoConta);
}
