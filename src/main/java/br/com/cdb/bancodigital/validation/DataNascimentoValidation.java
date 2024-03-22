package br.com.cdb.bancodigital.validation;

import java.time.LocalDate;

import br.com.cdb.bancodigital.validation.constraints.DataDeNascimento;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DataNascimentoValidation implements ConstraintValidator<DataDeNascimento, LocalDate> {
	
    @Override
    public void initialize(DataDeNascimento constraintAnnotation) {
    }

	@Override
	public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
	    String dateString = value.toString();
	    
		return dateString.matches("\\d{4}-\\d{2}-\\d{2}");
	}

}
