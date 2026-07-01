package com.mycompany.knstore.service;

/**
 * Excepción de negocio lanzada cuando el checkout no puede completarse.
 */
public class CheckoutException extends RuntimeException {

    public CheckoutException(String message) {
        super(message);
    }
}
