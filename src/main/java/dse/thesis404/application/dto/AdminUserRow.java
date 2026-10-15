package dse.thesis404.application.dto;

public record AdminUserRow(
        long id,
        String name,
        String role,
        String githubId,
        String modules
) {}
