package com.combustify.api.dto;

import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
    @Size(max = 100, message = "Nome não pode exceder 100 caracteres")
    String displayName
) {}
