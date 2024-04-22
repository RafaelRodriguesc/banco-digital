package br.com.cdb.bancodigital.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.cdb.bancodigital.entity.CartaoDebito;
import br.com.cdb.bancodigital.entity.Conta;
import br.com.cdb.bancodigital.entity.ContaCorrente;
import br.com.cdb.bancodigital.repository.ContaCorrenteRepository;
import br.com.cdb.bancodigital.repository.DebitoRepository;

@Service
public class DebitoService {

	@Autowired
	private DebitoRepository debitoRepository;
	@Autowired
	private ContaCorrenteRepository contaCorrenteRepository;

	public CartaoDebito saveCartaoDebito(Long numeroDaConta) {
		Optional<ContaCorrente> contaCorrenteOptional = contaCorrenteRepository.findByNumeroDaConta(numeroDaConta);
		if (!contaCorrenteOptional.isPresent()) {
			throw new IllegalArgumentException("Número de conta corrente não encontrado");
		}

		ContaCorrente contaCorrente = contaCorrenteOptional.get();
		Long clienteId = contaCorrente.getCliente().getId();
		String numeroCartao = clienteId.toString() + numeroDaConta.toString();

		CartaoDebito cartaoDebito = new CartaoDebito();
		cartaoDebito.setConta(contaCorrente);
		cartaoDebito.setNumeroCartao(numeroCartao);

		return debitoRepository.save(cartaoDebito);
	}

	public void atualizaSaldoCartaoDebito(Long numeroDaConta) {
		Optional<ContaCorrente> contaCorrenteOptional = contaCorrenteRepository.findByNumeroDaConta(numeroDaConta);

		if (contaCorrenteOptional.isPresent()) {
			ContaCorrente contaCorrente = contaCorrenteOptional.get();
			BigDecimal saldoContaCorrente = contaCorrente.getSaldo();
			Optional<CartaoDebito> cartaoDebitoOptional = debitoRepository.findByContaNumeroDaConta(numeroDaConta);

			if (cartaoDebitoOptional.isPresent()) {
				CartaoDebito cartaoDebito = cartaoDebitoOptional.get();
				cartaoDebito.setSaldo(saldoContaCorrente);
				debitoRepository.save(cartaoDebito);

			} else {
				throw new IllegalArgumentException(
						"Cartão de débito não encontrado para a conta corrente: " + numeroDaConta);
			}
		} else {
			throw new IllegalArgumentException("Conta corrente não encontrada para o número: " + numeroDaConta);
		}
	}

	public void desbloquearCartaoDebito(String numeroCartao, Boolean ativo, String senha) {
		if (senha == null || senha.isEmpty()) {
			throw new IllegalArgumentException("Senha não pode estar em branco");
		}
		if (senha.length() != 4) {
			throw new IllegalArgumentException("A senha deve ter 4 digitos");
		}
		Optional<CartaoDebito> cartaoDebitoOptional = debitoRepository.findByNumeroCartao(numeroCartao);
		if (!cartaoDebitoOptional.isPresent()) {
			throw new IllegalArgumentException("Numero de cartão de débito não encontrado");
		} else {
			CartaoDebito cartaoDebito = cartaoDebitoOptional.get();
			cartaoDebito.setAtivo(ativo);
			cartaoDebito.setSenha(senha);
			debitoRepository.save(cartaoDebito);
		}
	}

	public void atualizarLimiteDiario(String numeroCartao, BigDecimal novoLimite) {
		Optional<CartaoDebito> cartaoDebitoOptional = debitoRepository.findByNumeroCartao(numeroCartao);
		if (cartaoDebitoOptional.isPresent()) {
			CartaoDebito cartaoDebito = cartaoDebitoOptional.get();
			cartaoDebito.setLimiteDiario(novoLimite);
			debitoRepository.save(cartaoDebito);
		} else {
			throw new IllegalArgumentException("Cartão de débito não encontrado com o número: " + numeroCartao);
		}
	}

	public void pagarContaComDebito(String numeroCartao, String senha, BigDecimal valor) {
		Optional<CartaoDebito> cartaoDebitoOptional = debitoRepository.findByNumeroCartao(numeroCartao);

		if (cartaoDebitoOptional.isPresent()) {
			CartaoDebito cartaoDebito = cartaoDebitoOptional.get();

			if (senha.equals(cartaoDebito.getSenha())) {
				BigDecimal saldoDisponivel = cartaoDebito.getSaldo();
				BigDecimal limiteDiario = cartaoDebito.getLimiteDiario();

				if (saldoDisponivel.compareTo(valor) >= 0) {

					if (valor.compareTo(limiteDiario) <= 0) {
						BigDecimal novoSaldo = saldoDisponivel.subtract(valor);
						cartaoDebito.setSaldo(novoSaldo);
						debitoRepository.save(cartaoDebito);

						Conta contaAssociada = cartaoDebito.getConta();
						if (contaAssociada instanceof ContaCorrente) {
							ContaCorrente contaCorrente = (ContaCorrente) contaAssociada;
							BigDecimal saldoContaCorrente = contaCorrente.getSaldo();
							BigDecimal novoSaldoContaCorrente = saldoContaCorrente.subtract(valor);
							contaCorrente.setSaldo(novoSaldoContaCorrente);
							contaCorrenteRepository.save(contaCorrente);
						}

					} else {
						throw new IllegalArgumentException("O valor da conta excede o limite diário de pagamentos!");
					}
				} else {
					throw new IllegalArgumentException("Saldo insuficiente!");
				}
			} else {
				throw new IllegalArgumentException("Senha incorreta!");
			}
		} else {
			throw new IllegalArgumentException("Cartão de débito não encontrado com o número: " + numeroCartao);
		}
	}

}
