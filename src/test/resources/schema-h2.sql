-- Test-Schema für H2 (NUR Test-Scope, Migrations bleiben unverändert)
CREATE TABLE IF NOT EXISTS betreuer_profil (
    profile_id BIGINT PRIMARY KEY,
    github_id BIGINT NOT NULL UNIQUE,
    titel VARCHAR(20) NOT NULL DEFAULT 'NONE',
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    raum VARCHAR(20) NOT NULL
);
CREATE TABLE IF NOT EXISTS betreuer_fachgebiet (
    profile_id BIGINT NOT NULL,
    fachgebiet VARCHAR(255) NOT NULL,
    PRIMARY KEY (profile_id, fachgebiet)
);
CREATE TABLE IF NOT EXISTS betreuer_link (
    profile_id BIGINT NOT NULL,
    url VARCHAR(2000) NOT NULL,
    link_text VARCHAR(255) NOT NULL,
    PRIMARY KEY (profile_id, url, link_text)
);
CREATE TABLE IF NOT EXISTS datei (
    file_id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    beschreibung VARCHAR(4000),
    uploader_id VARCHAR(255) NOT NULL,
    uploaded_at TIMESTAMP NOT NULL,
    type VARCHAR(20) NOT NULL,
    size_bytes BIGINT NOT NULL,
    stored_filename VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255)
);
CREATE TABLE IF NOT EXISTS betreuer_profil_datei (
    profile_id BIGINT NOT NULL,
    file_id UUID NOT NULL,
    PRIMARY KEY (profile_id, file_id)
);
CREATE TABLE IF NOT EXISTS theme (
    theme_id UUID PRIMARY KEY,
    owner_profile_id BIGINT NOT NULL,
    titel VARCHAR(255) NOT NULL,
    beschreibung VARCHAR(4000) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);
CREATE TABLE IF NOT EXISTS theme_veranstaltung (
    theme_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (theme_id, name)
);
CREATE TABLE IF NOT EXISTS theme_fachgebiet (
    theme_id UUID NOT NULL,
    fachgebiet VARCHAR(255) NOT NULL,
    PRIMARY KEY (theme_id, fachgebiet)
);
CREATE TABLE IF NOT EXISTS theme_link (
    theme_id UUID NOT NULL,
    url VARCHAR(2000) NOT NULL,
    link_text VARCHAR(255) NOT NULL,
    PRIMARY KEY (theme_id, url, link_text)
);
CREATE TABLE IF NOT EXISTS theme_datei (
    theme_id UUID NOT NULL,
    file_id UUID NOT NULL,
    PRIMARY KEY (theme_id, file_id)
);
CREATE TABLE IF NOT EXISTS student (
    profile_id BIGINT PRIMARY KEY,
    display_name VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS student_veranstaltung (
    profile_id BIGINT NOT NULL,
    veranstaltung VARCHAR(255) NOT NULL,
    PRIMARY KEY (profile_id, veranstaltung)
);
CREATE TABLE IF NOT EXISTS student_fachgebiet (
    profile_id BIGINT NOT NULL,
    fachgebiet VARCHAR(255) NOT NULL,
    PRIMARY KEY (profile_id, fachgebiet)
);
CREATE TABLE IF NOT EXISTS student_theme_request (
    profile_id BIGINT NOT NULL,
    theme_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (profile_id, theme_id)
);
