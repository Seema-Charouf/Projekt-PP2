package dse.thesis404.domain.model.shared;

import java.util.Objects;

public record ProfileId(long value) {

    public ProfileId {
        if (value <= 0) {
            throw new IllegalArgumentException("ProfileId muss > 0 sein!");
        }
    }

    public static ProfileId ofGithubUserId(long githubUserId) {
        return new ProfileId(githubUserId);
    }

    public String externalForm() {
        return "gh:" + value;
    }

    public static ProfileId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        String s = raw.trim();
        if (!s.startsWith("gh:")) {
            throw new IllegalArgumentException("ProfileId muss mit 'gh:' anfangen!");
        }
        long id;
        try {
            id = Long.parseLong(s.substring(3));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ungültige GitHub-Id in ProfileId: " + raw);
        }
        return new ProfileId(id);
    }

    @Override
    public String toString() {
        return externalForm();
    }
}
