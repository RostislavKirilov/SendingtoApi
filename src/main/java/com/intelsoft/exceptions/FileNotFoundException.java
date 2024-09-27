package com.intelsoft.exceptions;

public class FileNotFoundException extends FileProcessingException {
    public FileNotFoundException(String filePath) {
        super("File not found: " + filePath);
    }
}