package br.com.eduschedule.controller;

import br.com.eduschedule.exception.ApiException;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import java.util.Set;

final class Paginacao {
    private Paginacao() {}

    static Pageable validar(Pageable pagina, String... campos) {
        var permitidos = Set.of(campos);
        for (Sort.Order ordem : pagina.getSort()) {
            if (!permitidos.contains(ordem.getProperty())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Campo de ordenacao invalido");
            }
        }
        return pagina;
    }
}
