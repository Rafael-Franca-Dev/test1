package br.com.mmmodas.domain;

import br.com.mmmodas.domain.enums.PerfilUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome", nullable = false)
    private String nome;

    /** Sempre gravado em minusculas pelo servico; a unicidade real esta no indice lower(email). */
    @Column(name = "email", nullable = false, length = 160)
    private String email;

    /** Hash bcrypt/argon2. Senha em claro nunca chega aqui. */
    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Column(name = "cpf", length = 11)
    private String cpf;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "aceita_marketing", nullable = false)
    private boolean aceitaMarketing = false;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @Column(name = "email_verificado_em")
    private OffsetDateTime emailVerificadoEm;

    @Enumerated(EnumType.STRING)
    @Column(name = "perfil", nullable = false, length = 20)
    private PerfilUsuario perfil = PerfilUsuario.CLIENTE;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;
}
