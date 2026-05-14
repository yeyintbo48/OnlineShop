package com.online.shop.dtos;

public record RegisterRequest(
    String username,
    String email,
    String password
) {}
