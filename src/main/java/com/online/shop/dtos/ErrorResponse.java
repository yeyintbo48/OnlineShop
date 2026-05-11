package com.online.shop.dtos;

import java.time.LocalDateTime;

public record ErrorResponse(
    LocalDateTime timeStamp,
    int status,
    String error,
    String message,
    String path
) {}
