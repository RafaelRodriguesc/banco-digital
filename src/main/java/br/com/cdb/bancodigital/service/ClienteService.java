package br.com.cdb.bancodigital.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.cdb.bancodigital.entity.Cliente;
import br.com.cdb.bancodigital.repository.ClienteRepository;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;

	public Cliente saveClient(String nome, String cpf, String endereco, LocalDate dataDeNascimento) {
		if (cpfExist(cpf)) {
			throw new IllegalArgumentException("CPF já cadastrado");
		}
		if (!isMaiorDeIdade(dataDeNascimento)) {
			throw new IllegalArgumentException("O cliente deve ter pelo menos 18 anos de idade para criar uma conta.");
		}
		Cliente cliente = new Cliente();
		cliente.setCpf(cpf);
		cliente.setNome(nome);
		cliente.setDataDeNascimento(dataDeNascimento);
		cliente.setEndereco(endereco);
		return clienteRepository.save(cliente);
	}

	public List<Cliente> getClients() {
		return clienteRepository.findAll();
	}

	public Cliente updateClient(Long id, Cliente clienteAtualizadoRequest) {
		Optional<Cliente> clienteOptional = clienteRepository.findById(id);
		if (clienteOptional.isPresent()) {
			Cliente clienteExistente = clienteOptional.get();
			if (clienteAtualizadoRequest.getNome() != null) {
				clienteExistente.setNome(clienteAtualizadoRequest.getNome());
			}

			if (clienteAtualizadoRequest.getCpf() != null) {
				throw new IllegalArgumentException("Você não tem permissão para alterar o campo CPF!");
			}

			if (clienteAtualizadoRequest.getDataDeNascimento() != null) {
				throw new IllegalArgumentException("Você não tem permissão para alterar o campo dataDeNscimento!");
			}

			if (clienteAtualizadoRequest.getEndereco() != null) {
				clienteExistente.setEndereco(clienteAtualizadoRequest.getEndereco());
			}

			return clienteRepository.save(clienteExistente);
		} else {
			throw new IllegalArgumentException("Cliente Id " + id + " não encontrado!");
		}
	}

	public boolean deleteClient(Long id) {
		Optional<Cliente> clienteOptional = clienteRepository.findById(id);
		if (clienteOptional.isPresent()) {
			clienteRepository.deleteById(id);
			return true;
		} else {
			throw new IllegalArgumentException("Cliente Id " + id + " não encontrado!");
		}
	}

	public boolean cpfExist(String cpf) {
		List<Cliente> clientes = getClients();
		for (Cliente cliente : clientes) {
			if (cliente.getCpf().equals(cpf)) {
				return true;
			}
		}
		return false;
	}

	public boolean isMaiorDeIdade(LocalDate dataDeNascimento) {
		LocalDate hoje = LocalDate.now();
		Period periodo = Period.between(dataDeNascimento, hoje);
		return periodo.getYears() >= 18;
	}

}
