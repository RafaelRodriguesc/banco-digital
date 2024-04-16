package br.com.cdb.bancodigital.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.cdb.bancodigital.entity.CartaoCredito;
import br.com.cdb.bancodigital.entity.ContaCorrente;

public interface CreditoRepository extends JpaRepository<CartaoCredito, Long> {
	Optional<CartaoCredito> findByContaNumeroDaConta(Long numeroDaConta);

	Optional<CartaoCredito> findByNumeroCartao(String numeroCartao);

	boolean existsByConta(ContaCorrente conta);

}
