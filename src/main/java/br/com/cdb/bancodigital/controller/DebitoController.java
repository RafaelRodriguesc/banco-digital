package br.com.cdb.bancodigital.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cdb.bancodigital.DTO.DesbloqueiaCartaoDTO;
import br.com.cdb.bancodigital.DTO.PagamentoDTO;
import br.com.cdb.bancodigital.service.DebitoService;

@RestController
@RequestMapping("/cartao-debito")
public class DebitoController {
	
	@Autowired
	private DebitoService debitoService;

	@PostMapping("/desbloquear")
	public ResponseEntity<String> desbloquearCartaoDebito(@RequestBody DesbloqueiaCartaoDTO request) {
		try {
			debitoService.desbloquearCartaoDebito(request.getNumeroCartao(), request.getAtivo(), request.getSenha());
			return ResponseEntity.ok("Cartão de débito desbloqueado!");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PutMapping("/atualizar-limite-diario/{numeroCartao}")
	public ResponseEntity<String> atualizarLimiteDiario(@PathVariable("numeroCartao") String numeroCartao,
			@RequestBody BigDecimal novoLimite) {
		try {
			debitoService.atualizarLimiteDiario(numeroCartao, novoLimite);
			return ResponseEntity.ok("Limite diário atualizado com sucesso!");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/pagar-com-debito")
	public ResponseEntity<String> pagarContaComDebito(@RequestBody PagamentoDTO pagamentoRequest) {
		try {
			debitoService.pagarContaComDebito(pagamentoRequest.getNumeroCartao(), pagamentoRequest.getSenha(),
					pagamentoRequest.getValor());
			return ResponseEntity.ok("Pagamento efetuado com sucesso!");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Ocorreu um erro durante o processamento do pagamento.");
		}
	}
}
