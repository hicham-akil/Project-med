package org.example.backend_med.Dto;

public record UpdateUserRequest(
        String nom,
        String prenom,
        String email,
        String telephone,
        String adresse,
        String dateNaissance
) {}