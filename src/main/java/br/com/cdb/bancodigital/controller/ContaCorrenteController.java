package br.com.cdb.bancodigital.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cdb.bancodigital.DTO.PixDTO;
import br.com.cdb.bancodigital.entity.ContaCorrente;
import br.com.cdb.bancodigital.repository.ContaCorrenteRepository;
import br.com.cdb.bancodigital.service.ContaCorrenteService;

@RestController
@RequestMapping("/conta-corrente")
public class ContaCorrenteController {

	private final ContaCorrenteService contaCorrenteService;

	@Autowired
	public ContaCorrenteController(ContaCorrenteService contaCorrenteService,
			ContaCorrenteRepository contaCorrenteRepository) {
		this.contaCorrenteService = contaCorrenteService;
	}

	@GetMapping("/{numeroDaConta}/saldo")
	public ResponseEntity<?> consultarSaldo(@PathVariable("numeroDaConta") Long numeroDaConta) {
		try {
			BigDecimal saldo = contaCorrenteService.consultarSaldo(numeroDaConta);
			return ResponseEntity.ok(saldo);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Número da conta não encontrado!");
		}
	}

	@PostMapping("/{numeroDaConta}/deposito")
	public ResponseEntity<String> depositarDinheiro(@PathVariable("numeroDaConta") Long numeroDaConta,
			@RequestBody BigDecimal valorDeposito) {
		try {
			contaCorrenteService.depositarDinheiro(numeroDaConta, valorDeposito);
			return ResponseEntity.ok("Deposito efetuado com sucesso!");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Falha ao efetuar o depósito.");
		}
	}

	@GetMapping("/listAll")
	public ResponseEntity<List<ContaCorrente>> getAllContasCorrente() {
		List<ContaCorrente> contasCorrente = contaCorrenteService.getContaCorrente();
		return new ResponseEntity<List<ContaCorrente>>(contasCorrente, HttpStatus.OK);
	}

	@PostMapping("/transferencia")
	public ResponseEntity<String> transferirViaPix(@RequestBody PixDTO transferenciaRequest) {
		try {
			contaCorrenteService.transferirViaPix(transferenciaRequest.getNumeroDaContaOrigem(),
					transferenciaRequest.getCpfDestino(), transferenciaRequest.getValor());
			return ResponseEntity.ok("Transferência efetuada com sucesso.");
		} catch (IllegalArgumentException | IllegalStateException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Falha ao efetuar o depósito na conta poupança.");
		}
	}

	@PostMapping("/{clienteId}/deposito-na-poupanca")
	public ResponseEntity<String> depositarNaContaPoupanca(@PathVariable Long clienteId,
			@RequestBody BigDecimal valor) {
		try {
			contaCorrenteService.depositarNaContaPoupanca(clienteId, valor);
			return ResponseEntity.ok("Depósito na conta poupança efetuado com sucesso.");
		} catch (IllegalArgumentException | IllegalStateException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Falha ao efetuar o depósito na conta poupança.");
		}
	}

	@PostMapping("/descontar-taxa-todos-planos")
	public ResponseEntity<String> descontarTaxaManutencaoParaTodosPlanos() {
		try {
			contaCorrenteService.descontarTaxaManutencaoParaTodosPlanos();
			return ResponseEntity.ok("Taxa de manutenção descontada com sucesso para todos os planos.");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}