package com.mycompany.knstore.service.util;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * Utilidades de paginacion para listados que se resuelven en memoria
 * (por ejemplo, recursos anidados bajo la cuenta del cliente).
 */
public final class InMemoryPageUtils {

    private InMemoryPageUtils() {
        // clase de utilidades
    }

    /**
     * Aplica el {@link Pageable} a una lista ya cargada, respetando offset y
     * tamanio, con el total real de elementos como metadata.
     */
    public static <T> Page<T> paginar(List<T> content, Pageable pageable) {
        if (content == null || content.isEmpty()) {
            return Page.empty(pageable);
        }
        long total = content.size();
        long from = Math.min(pageable.getOffset(), total);
        int to = (int) Math.min(from + pageable.getPageSize(), total);
        List<T> slice = content.subList((int) from, to);
        return new PageImpl<>(slice, pageable, total);
    }
}
