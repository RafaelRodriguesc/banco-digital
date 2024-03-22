package br.com.cdb.bancodigital.entity;

import java.time.LocalDate;

import org.hibernate.validator.constraints.br.CPF;

import br.com.cdb.bancodigital.validation.constraints.DataDeNascimento;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

@Entity
@Data
public class Cliente {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@NotBlank(message = "Campo nome em branco! Por favor preencha todos os campos")
	private String nome;
	@NotBlank(message = "Campo endereco em branco! Por favor preencha todos os campos")
	private String endereco;
	@NotBlank(message = "Campo cpf em branco! Por favor preencha todos os campos")
	@CPF(message = "CPF inválido!")
	private String cpf;
	@Past(message = "Data inválida! A data deve estar no passado")
	@NotNull(message = "A data de nascimento não pode estar vazia!")
	@DataDeNascimento(message = "Data de Nascimento inválida! Por favor digite no formato (yyyy-MM-dd)")
	private LocalDate dataDeNascimento; //(YYYY-MM-DD)

	public void exibeDados() {
		System.out.println(
				getId() + " - " + getCpf() + ", " + getNome() + ", " + getDataDeNascimento() + ", " + getEndereco());
	}

}
