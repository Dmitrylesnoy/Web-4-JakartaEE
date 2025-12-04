package com.lab.web.API.records;

public record LoginRequest(String username, String hashedPassword) {
}