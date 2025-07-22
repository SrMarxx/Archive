package com.SrMarxx.archive.controllers.dtos;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestRecordDTO(@NotBlank String username, @NotBlank String password) {
}
