package br.com.cdb.bancodigital.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.cdb.bancodigital.entity.SeguroApolice;
import br.com.cdb.bancodigital.repository.SeguroApoliceRepository;

@Service
public class SeguroApoliceService {

    private final SeguroApoliceRepository seguroApoliceRepository;

    @Autowired
    public SeguroApoliceService(SeguroApoliceRepository seguroApoliceRepository) {
        this.seguroApoliceRepository = seguroApoliceRepository;
    }

    public Optional<SeguroApolice> getSeguroApolice(Long codigoApolice) {
        return seguroApoliceRepository.findByCodigoApolice(codigoApolice);
    }
}
