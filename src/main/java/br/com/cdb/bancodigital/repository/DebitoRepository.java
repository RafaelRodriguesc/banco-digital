package br.com.cdb.bancodigital.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.cdb.bancodigital.entity.CartaoDebito;

public interface DebitoRepository extends JpaRepository<CartaoDebito, Long> {
	Optional<CartaoDebito> findByContaNumeroDaConta(Long numeroDaConta);

	Optional<CartaoDebito> findByNumeroCartao(String numeroCartao);

}
