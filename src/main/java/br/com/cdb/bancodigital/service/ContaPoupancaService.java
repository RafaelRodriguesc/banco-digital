package br.com.cdb.bancodigital.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.cdb.bancodigital.entity.Cliente;
import br.com.cdb.bancodigital.entity.ClientePlano;
import br.com.cdb.bancodigital.entity.ContaPoupanca;
import br.com.cdb.bancodigital.entity.TipoConta;
import br.com.cdb.bancodigital.repository.ClienteRepository;
import br.com.cdb.bancodigital.repository.ContaPoupancaRepository;

@Service
public class ContaPoupancaService {

	private ClienteRepository clienteRepository;
	private ContaPoupancaRepository contaPoupancaRepository;

	@Autowired
	public ContaPoupancaService(ContaPoupancaRepository contaPoupancaRepository, ClienteRepository clienteRepository) {
		this.contaPoupancaRepository = contaPoupancaRepository;
		this.clienteRepository = clienteRepository;
	}

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

	private BigDecimal calcularTaxaRendimentoMensal(BigDecimal taxaRendimentoAnual) {
		BigDecimal mesesNoAno = BigDecimal.valueOf(12);
		return taxaRendimentoAnual.divide(mesesNoAno, 4, RoundingMode.HALF_UP);
	}

	private BigDecimal calcularRendimentoMensal(BigDecimal saldo, BigDecimal taxaRendimentoMensal) {
		return saldo.multiply(taxaRendimentoMensal).setScale(2, RoundingMode.HALF_UP);
	}

}
