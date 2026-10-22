package br.com.mmmodas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Ponto de entrada da API do MM Modas.
 *
 * As datas de criacao/alteracao sao preenchidas pelas anotacoes do Hibernate
 * (@CreationTimestamp / @UpdateTimestamp), entao nao ha necessidade de
 * @EnableJpaAuditing aqui.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class MmModasApplication {

    public static void main(String[] args) {
        SpringApplication.run(MmModasApplication.class, args);
    }
}
