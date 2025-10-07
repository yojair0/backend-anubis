package com.anubis.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FileController {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.base.url:http://localhost:8080}")
    private String baseUrl;

    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @PostMapping("/upload")
    @PreAuthorize("hasRole('FOUNDATION') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Validaciones
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("El archivo está vacío"));
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("El archivo excede el tamaño máximo de 10MB"));
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Nombre de archivo inválido"));
            }

            String extension = getFileExtension(originalFilename);
            if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Tipo de archivo no permitido. Permitidos: " + ALLOWED_EXTENSIONS));
            }

            // Crear directorio si no existe
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generar nombre único para el archivo
            String filename = UUID.randomUUID().toString() + "." + extension;
            Path filePath = uploadPath.resolve(filename);

            // Guardar archivo
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Crear URL de acceso
            String fileUrl = baseUrl + "/api/files/images/" + filename;

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Archivo subido exitosamente");
            response.put("filename", filename);
            response.put("url", fileUrl);
            response.put("size", file.getSize());

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.status(500)
                .body(createErrorResponse("Error al guardar el archivo: " + e.getMessage()));
        }
    }

    @PostMapping("/upload/multiple")
    @PreAuthorize("hasRole('FOUNDATION') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        List<Map<String, Object>> uploadedFiles = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                if (file.isEmpty()) {
                    errors.add("Archivo vacío: " + file.getOriginalFilename());
                    continue;
                }

                if (file.getSize() > MAX_FILE_SIZE) {
                    errors.add("Archivo muy grande: " + file.getOriginalFilename());
                    continue;
                }

                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null) {
                    errors.add("Nombre inválido: " + originalFilename);
                    continue;
                }

                String extension = getFileExtension(originalFilename);
                if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
                    errors.add("Tipo no permitido: " + originalFilename);
                    continue;
                }

                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String filename = UUID.randomUUID().toString() + "." + extension;
                Path filePath = uploadPath.resolve(filename);

                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                String fileUrl = baseUrl + "/api/files/images/" + filename;

                Map<String, Object> fileInfo = new HashMap<>();
                fileInfo.put("originalName", originalFilename);
                fileInfo.put("filename", filename);
                fileInfo.put("url", fileUrl);
                fileInfo.put("size", file.getSize());
                uploadedFiles.add(fileInfo);

            } catch (IOException e) {
                errors.add("Error con " + file.getOriginalFilename() + ": " + e.getMessage());
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", uploadedFiles.size() > 0);
        response.put("uploadedFiles", uploadedFiles);
        response.put("totalUploaded", uploadedFiles.size());
        response.put("totalErrors", errors.size());
        if (!errors.isEmpty()) {
            response.put("errors", errors);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<?> getImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            byte[] fileContent = Files.readAllBytes(filePath);
            
            return ResponseEntity.ok()
                .header("Content-Type", contentType)
                .body(fileContent);

        } catch (IOException e) {
            return ResponseEntity.status(500)
                .body(createErrorResponse("Error al leer el archivo"));
        }
    }

    @DeleteMapping("/images/{filename}")
    @PreAuthorize("hasRole('FOUNDATION') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            Files.delete(filePath);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Archivo eliminado exitosamente");
            
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.status(500)
                .body(createErrorResponse("Error al eliminar el archivo"));
        }
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        return error;
    }
}