package br.com.cdb.bancodigital.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cdb.bancodigital.DTO.ContaDTO;
import br.com.cdb.bancodigital.entity.TipoConta;
import br.com.cdb.bancodigital.service.ContaCorrenteService;
import br.com.cdb.bancodigital.service.ContaPoupancaService;

@RestController
@RequestMapping("/conta")
public class ContaController {

	private final ContaCorrenteService contaCorrenteService;
	private final ContaPoupancaService contaPoupancaService;

	@Autowired
	public ContaController(ContaCorrenteService contaCorrenteService, ContaPoupancaService contaPoupancaService) {
		this.contaCorrenteService = contaCorrenteService;
		this.contaPoupancaService = contaPoupancaService;
	}

	@PostMapping("/criar")
	public ResponseEntity<String> criarConta(@RequestBody ContaDTO contaDTO) {

		try {
			if (contaDTO.getTipoConta() == TipoConta.CORRENTE) {
				contaCorrenteService.saveAccount(contaDTO.getClienteId(), contaDTO.getClientePlano(),
						contaDTO.getTipoConta());
			}

			if (contaDTO.getTipoConta() == TipoConta.POUPANCA) {
				contaPoupancaService.saveAccount(contaDTO.getClienteId(), contaDTO.getClientePlano(),
						contaDTO.getTipoConta());
			}

			return ResponseEntity.status(HttpStatus.CREATED)
					.body("Conta " + contaDTO.getTipoConta() + " criada com sucesso!");

		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}