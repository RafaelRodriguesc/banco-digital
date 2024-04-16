package br.com.cdb.bancodigital.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.com.cdb.bancodigital.entity.Cliente;
import br.com.cdb.bancodigital.entity.ClientePlano;
import br.com.cdb.bancodigital.entity.Conta;
import br.com.cdb.bancodigital.entity.ContaCorrente;
import br.com.cdb.bancodigital.entity.ContaPoupanca;
import br.com.cdb.bancodigital.entity.TipoConta;
import br.com.cdb.bancodigital.repository.ClienteRepository;
import br.com.cdb.bancodigital.repository.ContaCorrenteRepository;
import br.com.cdb.bancodigital.repository.ContaPoupancaRepository;

@Service
public class ContaCorrenteService {

	private ContaCorrenteRepository contaCorrenteRepository;
	private ContaPoupancaRepository contaPoupancaRepository;
	private ClienteRepository clienteRepository;
	private DebitoService debitoService;

	@Autowired
	public ContaCorrenteService(ContaCorrenteRepository contaCorrenteRepository, ClienteRepository clienteRepository,
			ContaPoupancaRepository contaPoupancaRepository, DebitoService debitoService) {
		this.contaCorrenteRepository = contaCorrenteRepository;
		this.contaPoupancaRepository = contaPoupancaRepository;
		this.clienteRepository = clienteRepository;
		this.debitoService = debitoService;
	}

	public ContaCorrente saveAccount(Long clienteId, ClientePlano clientePlano, TipoConta tipoConta) {
		Optional<Cliente> clienteOptional = clienteRepository.findById(clienteId);
		if (!clienteOptional.isPresent()) {
			throw new IllegalArgumentException("Cliente não encontrado");
		}
		Cliente cliente = clienteOptional.get();
		Optional<ContaCorrente> contaCorrenteExistente = contaCorrenteRepository.findByCliente(cliente);
		if (tipoConta == TipoConta.CORRENTE && contaCorrenteExistente.isPresent()) {
			throw new RuntimeException("Cliente já possui uma conta corrente");
		}
		ContaCorrente conta = new ContaCorrente();
		conta.setCliente(cliente);
		conta.setClientePlano(clientePlano);
		conta.setTipoConta(tipoConta);
		conta.setTaxaManutencao(clientePlano.getTaxaManutencao());
		conta = contaCorrenteRepository.save(conta);
		debitoService.saveCartaoDebito(conta.getNumeroDaConta());
		return conta;
	}

	public BigDecimal consultarSaldo(Long numeroDaConta) {
		Optional<ContaCorrente> contaCorrenteOptional = contaCorrenteRepository.findByNumeroDaConta(numeroDaConta);
		if (contaCorrenteOptional.isPresent()) {
			Conta contaCorrente = contaCorrenteOptional.get();
			return contaCorrente.getSaldo();
		} else {
			throw new RuntimeException("Conta corrente não encontrada");
		}
	}

	public boolean depositarDinheiro(Long numeroDaConta, BigDecimal valorDeposito) {
		Optional<ContaCorrente> contaCorrenteOptional = contaCorrenteRepository.findByNumeroDaConta(numeroDaConta);
		if (!contaCorrenteOptional.isPresent()) {
			throw new IllegalArgumentException("Conta corrente não encontrada");
		}
		ContaCorrente contaCorrente = contaCorrenteOptional.get();
		BigDecimal novoSaldo = contaCorrente.getSaldo().add(valorDeposito);

		if (valorDeposito.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Valor de depósito inválido");
		}

		contaCorrente.setSaldo(novoSaldo);
		contaCorrenteRepository.save(contaCorrente);
		debitoService.atualizaSaldoCartaoDebito(numeroDaConta);

		return true;
	}

	public List<ContaCorrente> getContaCorrente() {
		return contaCorrenteRepository.findAll();
	}

	public boolean transferirViaPix(Long numeroDaContaOrigem, String cpfDestino, BigDecimal valorTransferencia) {

		Optional<ContaCorrente> contaOrigemOptional = contaCorrenteRepository.findByNumeroDaConta(numeroDaContaOrigem);
		if (!contaOrigemOptional.isPresent() || contaOrigemOptional.get().getTipoConta() != TipoConta.CORRENTE) {
			throw new IllegalArgumentException("Conta corrente origem não encontrada");
		}

		Optional<ContaCorrente> contaDestinoOptional = contaCorrenteRepository.findByCliente_CpfAndTipoConta(cpfDestino,
				TipoConta.CORRENTE);
		if (!contaDestinoOptional.isPresent()) {
			throw new IllegalArgumentException("CPF destino não encontrado");
		}

		ContaCorrente contaOrigem = contaOrigemOptional.get();
		if (contaOrigem.getCliente().getCpf().equals(cpfDestino)) {
			throw new IllegalArgumentException("Impossível transferir para o próprio cpf");
		}

		BigDecimal saldoAtual = contaOrigem.getSaldo();
		if (saldoAtual.compareTo(valorTransferencia) < 0) {
			throw new IllegalStateException("Saldo insuficiente!");
		}

		if (valorTransferencia.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Valor de transfêrencia inválido!");
		}

		ContaCorrente contaDestino = contaDestinoOptional.get();

		BigDecimal novoSaldoOrigem = contaOrigem.getSaldo().subtract(valorTransferencia);
		BigDecimal novoSaldoDestino = contaDestino.getSaldo().add(valorTransferencia);

		contaOrigem.setSaldo(novoSaldoOrigem);
		contaDestino.setSaldo(novoSaldoDestino);
		contaCorrenteRepository.save(contaOrigem);
		contaCorrenteRepository.save(contaDestino);
		debitoService.atualizaSaldoCartaoDebito(numeroDaContaOrigem);
		debitoService.atualizaSaldoCartaoDebito(contaDestino.getNumeroDaConta());

		return true;

	}

	public boolean depositarNaContaPoupanca(Long clienteId, BigDecimal valorDeposito) {
		Optional<Cliente> clienteOptional = clienteRepository.findById(clienteId);
		if (!clienteOptional.isPresent()) {
			throw new IllegalArgumentException("Cliente não encontrado");
		}

		Optional<ContaCorrente> contaCorrenteOptional = contaCorrenteRepository.findByCliente_Id(clienteId);
		if (!contaCorrenteOptional.isPresent()) {
			throw new IllegalStateException("Cliente não possui uma conta corrente associada");
		}

		Optional<ContaPoupanca> contaPoupancaOptional = contaPoupancaRepository.findByCliente_Id(clienteId);
		if (!contaPoupancaOptional.isPresent()) {
			throw new IllegalStateException("Cliente não possui uma conta poupanca associada");
		}

		ContaCorrente contaCorrente = contaCorrenteOptional.get();
		ContaPoupanca contaPoupanca = contaPoupancaOptional.get();

		BigDecimal saldoCorrenteAtual = contaCorrente.getSaldo();
		if (saldoCorrenteAtual.compareTo(valorDeposito) < 0) {
			throw new IllegalStateException("Saldo insuficiente na conta corrente");
		}

		if (valorDeposito.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Valor de transfêrencia inválido!");
		}

		BigDecimal novoSaldoPoupanca = contaPoupanca.getSaldo().add(valorDeposito);
		contaPoupanca.setSaldo(novoSaldoPoupanca);
		contaPoupancaRepository.save(contaPoupanca);

		BigDecimal novoSaldoCorrente = saldoCorrenteAtual.subtract(valorDeposito);
		contaCorrente.setSaldo(novoSaldoCorrente);
		contaCorrenteRepository.save(contaCorrente);
		debitoService.atualizaSaldoCartaoDebito(contaCorrente.getNumeroDaConta());

		return true;
	}

	public void descontarTaxaManutencaoParaTodosPlanos() {
		for (ClientePlano plano : ClientePlano.values()) {
			List<ContaCorrente> contasPlano = contaCorrenteRepository.findByClientePlano(plano);

			if (!contasPlano.isEmpty()) {
				for (ContaCorrente conta : contasPlano) {
					BigDecimal saldoAtual = conta.getSaldo();
					BigDecimal taxaManutencao = plano.getTaxaManutencao();
					BigDecimal novoSaldo = saldoAtual.subtract(taxaManutencao);
					conta.setSaldo(novoSaldo);

					contaCorrenteRepository.save(conta);
					debitoService.atualizaSaldoCartaoDebito(conta.getNumeroDaConta());
				}
			}
			if (contaCorrenteRepository.findAll().isEmpty()) {
				throw new RuntimeException("Nenhuma conta corrente ativa para realizar a taxa de manutenção");
			}
		}
	}

	@Scheduled(cron = "0 0 0 5 * ?")
	public void descontarTaxaPeriodica() {
		descontarTaxaManutencaoParaTodosPlanos();
	}

}