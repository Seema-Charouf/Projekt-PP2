-- BetreuerProfil (ProfileId = GitHub User ID, long)
CREATE TABLE betreuer_profil (
    profile_id     BIGINT       PRIMARY KEY,
    titel          VARCHAR(20)  NOT NULL DEFAULT 'NONE',
    name           VARCHAR(255) NOT NULL,
    email          VARCHAR(255) NOT NULL,
    phone          VARCHAR(50),
    raum           VARCHAR(20)  NOT NULL
);

-- Fachgebiete pro Betreuer
CREATE TABLE betreuer_fachgebiet (
    profile_id     BIGINT       NOT NULL REFERENCES betreuer_profil(profile_id) ON DELETE CASCADE,
    fachgebiet     VARCHAR(255) NOT NULL,
    PRIMARY KEY (profile_id, fachgebiet)
);

-- Links pro Betreuer
CREATE TABLE betreuer_link (
    profile_id     BIGINT       NOT NULL REFERENCES betreuer_profil(profile_id) ON DELETE CASCADE,
    url            TEXT         NOT NULL,
    link_text      VARCHAR(255) NOT NULL,
    PRIMARY KEY (profile_id, url, link_text)
);

-- Datei-Metadaten (shared; Betreuer + Theme verknüpfen über Zuordnungstabellen)
CREATE TABLE datei (
    file_id          UUID         PRIMARY KEY,
    title            VARCHAR(255) NOT NULL,
    beschreibung     TEXT,
    uploader_id      VARCHAR(255) NOT NULL,
    uploaded_at      TIMESTAMPTZ  NOT NULL,
    type             VARCHAR(20)  NOT NULL,
    size_bytes       BIGINT       NOT NULL,
    stored_filename  VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255)
);

-- Dateien pro Betreuer-Profil
CREATE TABLE betreuer_profil_datei (
    profile_id     BIGINT       NOT NULL REFERENCES betreuer_profil(profile_id) ON DELETE CASCADE,
    file_id        UUID         NOT NULL REFERENCES datei(file_id) ON DELETE CASCADE,
    PRIMARY KEY (profile_id, file_id)
);

-- Theme (ThemeId = UUID, Owner = ProfileId)
CREATE TABLE theme (
    theme_id         UUID         PRIMARY KEY,
    owner_profile_id BIGINT       NOT NULL REFERENCES betreuer_profil(profile_id) ON DELETE CASCADE,
    titel            VARCHAR(255) NOT NULL,
    beschreibung     TEXT         NOT NULL
);

-- Veranstaltungen pro Theme
CREATE TABLE theme_veranstaltung (
    theme_id        UUID         NOT NULL REFERENCES theme(theme_id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    PRIMARY KEY (theme_id, name)
);

-- Fachgebiete pro Theme
CREATE TABLE theme_fachgebiet (
    theme_id        UUID         NOT NULL REFERENCES theme(theme_id) ON DELETE CASCADE,
    fachgebiet      VARCHAR(255) NOT NULL,
    PRIMARY KEY (theme_id, fachgebiet)
);

-- Links pro Theme
CREATE TABLE theme_link (
    theme_id        UUID         NOT NULL REFERENCES theme(theme_id) ON DELETE CASCADE,
    url             TEXT         NOT NULL,
    link_text       VARCHAR(255) NOT NULL,
    PRIMARY KEY (theme_id, url, link_text)
);

-- Dateien pro Theme
CREATE TABLE theme_datei (
    theme_id        UUID         NOT NULL REFERENCES theme(theme_id) ON DELETE CASCADE,
    file_id         UUID         NOT NULL REFERENCES datei(file_id) ON DELETE CASCADE,
    PRIMARY KEY (theme_id, file_id)
);

-- Indizes für häufige Abfragen
CREATE INDEX idx_theme_owner ON theme(owner_profile_id);
CREATE INDEX idx_betreuer_fachgebiet_profile ON betreuer_fachgebiet(profile_id);
CREATE INDEX idx_theme_veranstaltung_theme ON theme_veranstaltung(theme_id);
CREATE INDEX idx_theme_fachgebiet_theme ON theme_fachgebiet(theme_id);
