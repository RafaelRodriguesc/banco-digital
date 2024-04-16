package br.com.cdb.bancodigital.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import br.com.cdb.bancodigital.entity.Cliente;
import br.com.cdb.bancodigital.service.ClienteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cliente")
public class ClienteController {
	@Autowired
	ClienteService clienteService;

	@PostMapping("/add")
	public ResponseEntity<String> addClient(@RequestBody @Valid Cliente cliente) {
		try {
			clienteService.saveClient(cliente.getNome(), cliente.getCpf(), cliente.getEndereco(),
					cliente.getDataDeNascimento());
			return new ResponseEntity<>("Cliente adicionado ao banco de dados!", HttpStatus.CREATED);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/listAll")
	public ResponseEntity<List<Cliente>> getAllCliente() {
		List<Cliente> clientes = clienteService.getClients();
		return new ResponseEntity<List<Cliente>>(clientes, HttpStatus.OK);
	}

	@PutMapping("/{id}")
	public ResponseEntity<String> atualizaCliente(@PathVariable @Valid Long id,
			@RequestBody Cliente clienteAtualizadoRequest) {
		try {
			Cliente clienteAtualizado = clienteService.updateClient(id, clienteAtualizadoRequest);
			return ResponseEntity.ok().body("Cliente ID - " + clienteAtualizado.getId() + " atualizado com sucesso!");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Ocorreu um erro ao processar a requisição.");
		}

	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deletaCliente(@PathVariable Long id) {
		try {
			boolean clienteExcluido = clienteService.deleteClient(id);

			if (clienteExcluido) {
				return ResponseEntity.ok().body("Cliente Id " + id + " excluído com sucesso!");
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}
