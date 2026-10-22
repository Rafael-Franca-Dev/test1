package br.com.mmmodas.domain.enums;

/**
 * Perfil de acesso. Guardado como texto no banco (VARCHAR + CHECK), nao como
 * tipo ENUM nativo do PostgreSQL: enum nativo precisa de
 * @JdbcTypeCode(SqlTypes.NAMED_ENUM) no mapeamento, quebra o ddl-auto=validate
 * e exige ALTER TYPE para cada valor novo.
 */
public enum PerfilUsuario {

    CLIENTE,
    ADMIN;

    public boolean isAdmin() {
        return this == ADMIN;
    }
}
