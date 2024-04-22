package br.com.cdb.bancodigital.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.cdb.bancodigital.entity.CartaoCredito;
import br.com.cdb.bancodigital.entity.Cliente;
import br.com.cdb.bancodigital.entity.ClientePlano;
import br.com.cdb.bancodigital.entity.Conta;
import br.com.cdb.bancodigital.entity.ContaCorrente;
import br.com.cdb.bancodigital.entity.SeguroApolice;
import br.com.cdb.bancodigital.repository.ContaCorrenteRepository;
import br.com.cdb.bancodigital.repository.CreditoRepository;
import br.com.cdb.bancodigital.repository.SeguroApoliceRepository;

@Service
public class CreditoService {

	@Autowired
	private ContaCorrenteRepository contaCorrenteRepository;

	@Autowired
	private CreditoRepository creditoRepository;

	@Autowired
	private SeguroApoliceRepository seguroApoliceRepository;

	public CartaoCredito emitirCartaoCredito(Long numeroDaConta) {
		Optional<ContaCorrente> contaCorrenteOptional = contaCorrenteRepository.findByNumeroDaConta(numeroDaConta);
		if (!contaCorrenteOptional.isPresent()) {
			throw new IllegalArgumentException("Conta corrente não encontrada");
		}

		ContaCorrente contaCorrente = contaCorrenteOptional.get();

		if (creditoRepository.existsByConta(contaCorrente)) {
			throw new IllegalStateException("Cliente já possui um cartão de crédito vinculado");
		}

		ClientePlano plano = contaCorrente.getClientePlano();
		String numeroCartao = "5-" + contaCorrente.getCliente().getId() + numeroDaConta;

		CartaoCredito cartaoCredito = new CartaoCredito();
		cartaoCredito.setConta(contaCorrente);
		cartaoCredito.setNumeroCartao(numeroCartao);

		BigDecimal limiteCredito = determinarLimiteDeCredito(plano);
		cartaoCredito.setLimiteCredito(limiteCredito);
		cartaoCredito.setSaldo(limiteCredito);
		LocalDateTime dataEmissao = LocalDateTime.now();
		cartaoCredito.setDataEmissao(dataEmissao);

		if (plano == ClientePlano.PREMIUM) {
			cartaoCredito.setSeguroViagemAtivo(true);
		}

		return creditoRepository.save(cartaoCredito);
	}

	private BigDecimal determinarLimiteDeCredito(ClientePlano plano) {
		switch (plano) {
		case COMUM:
			return new BigDecimal("1000.00");
		case SUPER:
			return new BigDecimal("5000.00");
		case PREMIUM:
			return new BigDecimal("10000.00");
		default:
			return BigDecimal.ZERO;
		}
	}

	public void desbloquearCartaoCredito(String numeroCartao, Boolean ativo, String senha, Boolean seguroViagemAtivo) {
		// Validação da senha
		if (senha == null || senha.isEmpty()) {
			throw new IllegalArgumentException("Senha não pode estar em branco");
		}
		if (senha.length() != 4) {
			throw new IllegalArgumentException("A senha deve ter 4 dígitos");
		}

		Optional<CartaoCredito> cartaoCreditoOptional = creditoRepository.findByNumeroCartao(numeroCartao);
		if (!cartaoCreditoOptional.isPresent()) {
			throw new IllegalArgumentException("Número de cartão de crédito não encontrado");
		}

		CartaoCredito cartaoCredito = cartaoCreditoOptional.get();
		ClientePlano clientePlano = cartaoCredito.getConta().getClientePlano();

		if (clientePlano == ClientePlano.PREMIUM) {

			if (seguroViagemAtivo != null && !seguroViagemAtivo) {
				throw new IllegalArgumentException("Seu plano já cobre o seguro viagem");
			}

		} else {

			if (seguroViagemAtivo != null && seguroViagemAtivo) {
				BigDecimal taxaSeguroViagem = new BigDecimal("50.00");
				cartaoCredito.setSaldo(cartaoCredito.getSaldo().subtract(taxaSeguroViagem));
			}
		}

		cartaoCredito.setAtivo(ativo);
		cartaoCredito.setSenha(senha);

		SeguroApolice apoliceSeguro = new SeguroApolice();
		apoliceSeguro.setDataContratacao(LocalDateTime.now());
		apoliceSeguro.setDetalhesCartao(gerarDetalhesCartao(cartaoCredito));
		apoliceSeguro.setValorApolice(cartaoCredito.getSeguroFraude());
		apoliceSeguro.setDescricaoCondicoes("Condições de acionamento do seguro de fraude.");

		SeguroApolice apoliceSalva = seguroApoliceRepository.save(apoliceSeguro);

		cartaoCredito.setSeguroApolice(apoliceSalva);

		creditoRepository.save(cartaoCredito);
	}

	private String gerarDetalhesCartao(CartaoCredito cartaoCredito) {
		Conta conta = cartaoCredito.getConta();
		if (conta instanceof ContaCorrente) {
			ContaCorrente contaCorrente = (ContaCorrente) conta;
			Cliente cliente = contaCorrente.getCliente();

			StringBuilder detalhes = new StringBuilder();
			detalhes.append("Nome: ").append(cliente.getNome()).append("\n");
			detalhes.append("CPF: ").append(cliente.getCpf()).append("\n");
			detalhes.append("Endereço: ").append(cliente.getEnderecoCompleto()).append("\n");
			detalhes.append("Número do cartão: ").append(cartaoCredito.getNumeroCartao()).append("\n");
			detalhes.append("Tipo de conta: ").append(contaCorrente.getTipoConta()).append("\n");

			return detalhes.toString();
		} else {
			throw new IllegalArgumentException("Conta não é do tipo ContaCorrente");
		}
	}

	public void pagarContaComCredito(String numeroCartao, String senha, BigDecimal valor) {
		Optional<CartaoCredito> cartaoCreditoOptional = creditoRepository.findByNumeroCartao(numeroCartao);

		if (cartaoCreditoOptional.isPresent()) {
			CartaoCredito cartaoCredito = cartaoCreditoOptional.get();

			if (senha.equals(cartaoCredito.getSenha())) {
				BigDecimal saldoDisponivel = cartaoCredito.getSaldo();
				BigDecimal limiteCredito = cartaoCredito.getLimiteCredito();
				BigDecimal novoSaldo = saldoDisponivel.subtract(valor);

				if (novoSaldo.compareTo(BigDecimal.ZERO) >= 0 && novoSaldo.compareTo(limiteCredito) <= 0) {
					cartaoCredito.setSaldo(novoSaldo);
					creditoRepository.save(cartaoCredito);
				} else {
					throw new IllegalArgumentException("Limite de crédito excedido!");
				}
			} else {
				throw new IllegalArgumentException("Senha incorreta!");
			}
		} else {
			throw new IllegalArgumentException("Cartão de crédito não encontrado com o número: " + numeroCartao);
		}
	}

	public BigDecimal exibeFaturaCartao(String numeroCartao) {
		Optional<CartaoCredito> cartaoCreditoOptional = creditoRepository.findByNumeroCartao(numeroCartao);

		return cartaoCreditoOptional
				.map(cartaoCredito -> cartaoCredito.getLimiteCredito().subtract(cartaoCredito.getSaldo()))
				.orElseThrow(() -> new IllegalArgumentException(
						"Cartão de crédito não encontrado com o número: " + numeroCartao));
	}

}
