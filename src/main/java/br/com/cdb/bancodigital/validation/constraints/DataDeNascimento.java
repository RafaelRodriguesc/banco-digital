package br.com.cdb.bancodigital.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.cdb.bancodigital.validation.DataNascimentoValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = DataNascimentoValidation.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataDeNascimento {
	
	 String message() default "Data de Nascimento inválida! Por favor digite no formato (yyyy-MM-dd)";
	    Class<?>[] groups() default {};
	    Class<? extends Payload>[] payload() default {};

}
