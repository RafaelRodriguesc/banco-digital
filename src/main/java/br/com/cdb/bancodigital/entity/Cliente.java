package br.com.cdb.bancodigital.entity;

import java.time.LocalDate;

import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
public class Cliente {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@NotBlank(message = "Campo nome em branco! Por favor preencha todos os campos")
	@Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 50 caracteres")
	@Pattern(regexp = "^[A-Z][a-zA-ZÀ-ú\\s]*$", message = "O nome deve começar com letra maiúscula e pode conter apenas letras e espaços")
	private String nome;
	@NotBlank(message = "Campo endereco em branco! Por favor preencha todos os campos")
	private String cep;
	@NotBlank(message = "Campo cpf em branco! Por favor preencha todos os campos")
	@CPF(message = "CPF inválido!")
	@Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$", message = "O cpf deve estar no formato ###.###.###-##")
	private String cpf;
	@Past(message = "Data inválida! A data deve estar no passado")
	@NotNull(message = "A data de nascimento não pode estar vazia!")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate dataDeNascimento;
	private String enderecoCompleto;

	public void setEnderecoCompleto(String enderecoCompleto) {
		this.enderecoCompleto = enderecoCompleto;
	}

	public String getEnderecoCompleto() {
		return enderecoCompleto;
	}

	public void exibeDados() {
		System.out.println(
				getId() + " - " + getCpf() + ", " + getNome() + ", " + getDataDeNascimento() + ", " + getCep());
	}

}
