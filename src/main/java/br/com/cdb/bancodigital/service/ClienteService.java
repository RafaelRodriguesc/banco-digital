package br.com.cdb.bancodigital.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.cdb.bancodigital.entity.Cliente;
import br.com.cdb.bancodigital.repository.ClienteRepository;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	public Cliente saveClient(String nome, String cpf, String endereco, LocalDate dataDeNascimento) {
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

}
