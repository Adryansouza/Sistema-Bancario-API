package com.adryan.projetobanco.exception;

import java.time.LocalDateTime;

public record ApiError(int status, String erro, String mensagem, LocalDateTime timestamp) {
}
