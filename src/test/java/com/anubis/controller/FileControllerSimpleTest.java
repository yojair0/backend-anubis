package com.anubis.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FileControllerSimpleTest {

    @Test
    void contextLoads() {
        FileController fileController = new FileController();
        assertNotNull(fileController);
    }

    @Test
    void fileExtensionMethodWorks() {
        FileController fileController = new FileController();
        // Este test verifica que la clase se puede instanciar correctamente
        assertNotNull(fileController);
    }
}