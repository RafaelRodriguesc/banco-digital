package br.com.cdb.bancodigital.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cdb.bancodigital.entity.SeguroApolice;
import br.com.cdb.bancodigital.service.SeguroApoliceService;

@RestController
@RequestMapping("/seguro-apolice")
public class SeguroApoliceController {

	private final SeguroApoliceService seguroApoliceService;

	@Autowired
	public SeguroApoliceController(SeguroApoliceService seguroApoliceService) {
		this.seguroApoliceService = seguroApoliceService;
	}

	@GetMapping("/{codigoApolice}")
	public ResponseEntity<SeguroApolice> getSeguroApolice(@PathVariable Long codigoApolice) {
		Optional<SeguroApolice> seguroApoliceOptional = seguroApoliceService.getSeguroApolice(codigoApolice);
		if (seguroApoliceOptional.isPresent()) {
			return ResponseEntity.ok(seguroApoliceOptional.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}
