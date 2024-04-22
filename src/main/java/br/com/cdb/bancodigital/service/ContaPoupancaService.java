package br.com.cdb.bancodigital.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.cdb.bancodigital.entity.Cliente;
import br.com.cdb.bancodigital.entity.ClientePlano;
import br.com.cdb.bancodigital.entity.ContaCorrente;
import br.com.cdb.bancodigital.entity.ContaPoupanca;
import br.com.cdb.bancodigital.entity.TipoConta;
import br.com.cdb.bancodigital.repository.ClienteRepository;
import br.com.cdb.bancodigital.repository.ContaCorrenteRepository;
import br.com.cdb.bancodigital.repository.ContaPoupancaRepository;

@Service
public class ContaPoupancaService {

	@Autowired
	private ClienteRepository clienteRepository;
	@Autowired
	private ContaPoupancaRepository contaPoupancaRepository;
	@Autowired
	private ContaCorrenteRepository contaCorrenteRepository;
	@Autowired
	private DebitoService debitoService;

	public ContaPoupanca saveAccount(Long clienteId, ClientePlano clientePlano, TipoConta tipoConta) {
		Optional<Cliente> clienteOptional = clienteRepository.findById(clienteId);
		if (!clienteOptional.isPresent()) {
			throw new IllegalArgumentException("Cliente não encontrado");
		}
		Cliente cliente = clienteOptional.get();
		Optional<ContaPoupanca> contaPopuancaExiste = contaPoupancaRepository.findByCliente(cliente);
		if (tipoConta == TipoConta.POUPANCA && contaPopuancaExiste.isPresent()) {
			throw new RuntimeException("Cliente já possui uma conta poupança");
		}
		ContaPoupanca conta = new ContaPoupanca();
		conta.setCliente(cliente);
		conta.setClientePlano(clientePlano);
		conta.setTipoConta(tipoConta);
		conta.setTaxaRendimento(clientePlano.getTaxaRendimentoAnual());
		return contaPoupancaRepository.save(conta);

	}

	public void aplicarRendimentoAnual() {
		for (ClientePlano plano : ClientePlano.values()) {
			BigDecimal taxaRendimentoAnual = plano.getTaxaRendimentoAnual();
			BigDecimal taxaRendimentoMensal = calcularTaxaRendimentoMensal(taxaRendimentoAnual);
			List<ContaPoupanca> contasPlano = contaPoupancaRepository.findByClientePlano(plano);
			for (ContaPoupanca conta : contasPlano) {
				BigDecimal rendimentoMensal = calcularRendimentoMensal(conta.getSaldo(), taxaRendimentoMensal);
				BigDecimal novoSaldo = conta.getSaldo().add(rendimentoMensal);
				conta.setSaldo(novoSaldo);
			}
			contaPoupancaRepository.saveAll(contasPlano);
		}
	}

	public boolean resgatarInvestimento(Long clienteId, BigDecimal valorResgate) {

		Cliente cliente = clienteRepository.findById(clienteId)
				.orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

		ContaPoupanca contaPoupanca = contaPoupancaRepository.findByCliente_Id(clienteId)
				.orElseThrow(() -> new IllegalStateException("Cliente não possui uma conta poupanca associada"));

		BigDecimal saldoPoupancaAtual = contaPoupanca.getSaldo();

		if (saldoPoupancaAtual.compareTo(valorResgate) < 0) {
			throw new IllegalStateException("Saldo insuficiente na conta poupanca");
		}

		if (valorResgate.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Valor de resgate inválido!");
		}

		ContaCorrente contaCorrente = contaCorrenteRepository.findByCliente_Id(clienteId)
				.orElseThrow(() -> new IllegalStateException("Cliente não possui uma conta corrente associada"));

		BigDecimal saldoCorrenteAtual = contaCorrente.getSaldo();

		BigDecimal novoSaldoCorrente = saldoCorrenteAtual.add(valorResgate);
		BigDecimal novoSaldoPoupanca = saldoPoupancaAtual.subtract(valorResgate);

		contaCorrente.setSaldo(novoSaldoCorrente);
		contaPoupanca.setSaldo(novoSaldoPoupanca);

		contaCorrenteRepository.save(contaCorrente);
		debitoService.atualizaSaldoCartaoDebito(contaCorrente.getNumeroDaConta());
		contaPoupancaRepository.save(contaPoupanca);

		return true;
	}

	private BigDecimal calcularTaxaRendimentoMensal(BigDecimal taxaRendimentoAnual) {
		BigDecimal mesesNoAno = BigDecimal.valueOf(12);
		return taxaRendimentoAnual.divide(mesesNoAno, 4, RoundingMode.HALF_UP);
	}

	private BigDecimal calcularRendimentoMensal(BigDecimal saldo, BigDecimal taxaRendimentoMensal) {
		return saldo.multiply(taxaRendimentoMensal).setScale(2, RoundingMode.HALF_UP);
	}

}
