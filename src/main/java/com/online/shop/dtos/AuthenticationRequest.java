package com.online.shop.dtos;

public record AuthenticationRequest(
    String email,
    String password
) {}
