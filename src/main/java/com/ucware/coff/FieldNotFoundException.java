package com.ucware.coff;

public class FieldNotFoundException
        extends RuntimeException {
    public FieldNotFoundException() {
    }

    public FieldNotFoundException(String string) {
        super(string);
    }
}
