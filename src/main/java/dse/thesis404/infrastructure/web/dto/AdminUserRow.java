package dse.thesis404.infrastructure.web.dto;

public record AdminUserRow(
        long id,
        String name,
        String role,
        String githubId,
        String modules
) {}
