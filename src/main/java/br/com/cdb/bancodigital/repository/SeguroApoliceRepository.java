package br.com.cdb.bancodigital.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.cdb.bancodigital.entity.SeguroApolice;

public interface SeguroApoliceRepository extends JpaRepository<SeguroApolice, Long> {
	Optional<SeguroApolice> findByCodigoApolice(Long codigoApolice);

}
