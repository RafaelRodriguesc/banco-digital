package br.com.cdb.bancodigital.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
		Cliente clienteAdicionado = clienteService.saveClient(cliente.getNome(), cliente.getCpf(), cliente.getEndereco(), cliente.getDataDeNascimento());
		if(clienteAdicionado != null) {
			return new ResponseEntity<>("Cliente - " + cliente.getNome() + " adicionado com sucesso!", HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		
	}
	
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleValidationException(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		
		return errors;
		
	}
	
	@GetMapping("/listAll")
	public ResponseEntity<List<Cliente>> getAllCliente() {
		List<Cliente> clientes = clienteService.getClients();
		return new ResponseEntity<List<Cliente>>(clientes, HttpStatus.OK);
	}

}
