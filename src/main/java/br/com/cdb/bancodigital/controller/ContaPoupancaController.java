package br.com.cdb.bancodigital.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.cdb.bancodigital.service.ContaPoupancaService;

@RestController
@RequestMapping("/conta-poupanca")
public class ContaPoupancaController {

	private final ContaPoupancaService contaPoupancaService;

	@Autowired
	public ContaPoupancaController(ContaPoupancaService contaPoupancaService) {
		this.contaPoupancaService = contaPoupancaService;
	}

	@PostMapping("/aplicar-rendimento-anual")
	public ResponseEntity<String> aplicarRendimentoAnual() {
		try {
			contaPoupancaService.aplicarRendimentoAnual();
			return ResponseEntity.ok("Rendimento anual aplicado com sucesso em todas as contas poupança.");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/{clienteId}/resgatar")
	public ResponseEntity<String> resgatarInvestimento(@PathVariable("clienteId") Long clienteId, @RequestBody BigDecimal valorResgate) {
		try {
			contaPoupancaService.resgatarInvestimento(clienteId, valorResgate);
			return ResponseEntity.ok("Resgate efetuado com sucesso!");
		} catch(IllegalStateException | IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
			
		}
	}

}
