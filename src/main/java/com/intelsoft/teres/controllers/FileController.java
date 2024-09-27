package com.intelsoft.teres.controllers;

import com.intelsoft.exceptions.FileNotFoundException;
import com.intelsoft.responses.ErrorResponse;
import com.intelsoft.responses.SuccessResponse;
import com.intelsoft.teres.services.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Try;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @Operation(
            summary = "Upload a file by path and send it to НАП",
            description = "Provide a file path and signature. The file will be read from the given path and sent to НАП.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    type = "object",
                                    requiredProperties = {"filePath", "signature"},
                                    description = "Request body containing the file path and its signature",
                                    example = "{ \"filePath\": \"/path/to/your/file.txt\", \"signature\": \"example-signature\" }"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File sent successfully",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file path or signature",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/uploadByPath", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadFileByPath(
            @RequestParam("filePath") String filePath,
            @RequestParam("signature") String signature) {

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException(filePath);
            }
            String fileContent = new String(Files.readAllBytes(file.toPath()));
            String result = fileService.processAndSendFile(fileContent, signature);

            return ResponseEntity.ok(new SuccessResponse(result));
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("File not found", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("File read error", "Error reading file: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("File processing error", "Error processing request: " + e.getMessage()));
        }
    }
}