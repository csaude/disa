package org.openmrs.module.disa.extension.util;

import org.openmrs.User;
import org.openmrs.api.context.Context;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SearchFormUtils {

    // NOME DA PROPRIEDADE NO USER (alterar se necessário)
    private static final String SISMA_CODES_PROPERTY = "sismaCodes";

    /**
     * Retorna uma lista de códigos SISMA associados ao utilizador logado.
     * Se não existir, retorna lista vazia.
     */
    public static List<String> getSismaCodesForLoggedUser() {
        User user = Context.getAuthenticatedUser();

        if (user == null) {
            return Collections.emptyList(); // Sem usuário logado
        }

        // Buscar a propriedade "sismaCodes"
        String sismaCodes = user.getUserProperty(SISMA_CODES_PROPERTY);

        if (sismaCodes == null || sismaCodes.trim().isEmpty()) {
            return Collections.emptyList(); // Usuário sem SISMA codes
        }

        // Separação por vírgulas: "123,456,789"
        List<String> result = Arrays.stream(sismaCodes.split(","))
                .map(String::trim)
                .filter(code -> !code.isEmpty())
                .collect(Collectors.toList()); // <-- CORRIGIDO para Java 8+

        return result;
    }

    /**
     * Retorna o primeiro SISMA code do utilizador logado.
     * Se houver múltiplos, devolve apenas o primeiro.
     */
    public static String getFirstSismaCode() {
        List<String> codes = getSismaCodesForLoggedUser();
        if (codes.isEmpty()) {
            return "ALL"; // ou null, depende do requisito
        }
        return codes.get(0);
    }

    /**
     * Retorna true se o utilizador tem pelo menos 1 SISMA code associado.
     */
    public static boolean userHasSismaCodes() {
        return !getSismaCodesForLoggedUser().isEmpty();
    }
}
