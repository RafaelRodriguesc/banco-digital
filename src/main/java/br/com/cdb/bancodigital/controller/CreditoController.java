package br.com.cdb.bancodigital.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cdb.bancodigital.DTO.DesbloqueiaCreditoDTO;
import br.com.cdb.bancodigital.DTO.PagamentoDTO;
import br.com.cdb.bancodigital.entity.CartaoCredito;
import br.com.cdb.bancodigital.service.CreditoService;

@RestController
@RequestMapping("/cartao-credito")
public class CreditoController {

	private final CreditoService creditoService;

	@Autowired
	public CreditoController(CreditoService creditoService) {
		this.creditoService = creditoService;
	}

	@PostMapping("/emitir/{numeroDaConta}")
	public ResponseEntity<String> emitirCartaoCredito(@PathVariable Long numeroDaConta) {
		try {
			CartaoCredito cartaoCredito = creditoService.emitirCartaoCredito(numeroDaConta);
			return ResponseEntity
					.ok("Cartão de crédito emitido com sucesso! Número do cartão: " + cartaoCredito.getNumeroCartao());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PostMapping("/desbloquear")
	public ResponseEntity<String> desbloquearCartaoCredito(@RequestBody DesbloqueiaCreditoDTO request) {
		try {
			creditoService.desbloquearCartaoCredito(request.getNumeroCartao(), request.getAtivo(), request.getSenha(),
					request.getSeguroViagemAtivo());
			return ResponseEntity.ok("Cartão de crédito desbloqueado!");
		} catch (IllegalArgumentException | IllegalStateException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/pagar-com-credito")
	public ResponseEntity<String> pagarContaComDebito(@RequestBody PagamentoDTO pagamentoRequest) {
		try {
			creditoService.pagarContaComCredito(pagamentoRequest.getNumeroCartao(), pagamentoRequest.getSenha(),
					pagamentoRequest.getValor());
			return ResponseEntity.ok("Pagamento efetuado com sucesso!");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Ocorreu um erro durante o processamento do pagamento.");
		}
	}

	@GetMapping("/fatura/{numeroCartao}")
	public ResponseEntity<BigDecimal> exibeFaturaCartao(@PathVariable String numeroCartao) {
		BigDecimal fatura = creditoService.exibeFaturaCartao(numeroCartao);
		return ResponseEntity.ok(fatura);
	}

}