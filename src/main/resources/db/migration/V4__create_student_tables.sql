-- Student (Identität = GitHub User ID, wie Betreuer)
CREATE TABLE student (
    profile_id     BIGINT       PRIMARY KEY,
    display_name   VARCHAR(255),
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Besuchte Veranstaltungen pro Student
CREATE TABLE student_veranstaltung (
    profile_id     BIGINT       NOT NULL REFERENCES student(profile_id) ON DELETE CASCADE,
    veranstaltung  VARCHAR(255) NOT NULL,
    PRIMARY KEY (profile_id, veranstaltung)
);

-- Interessensgebiete pro Student
CREATE TABLE student_fachgebiet (
    profile_id     BIGINT       NOT NULL REFERENCES student(profile_id) ON DELETE CASCADE,
    fachgebiet     VARCHAR(255) NOT NULL,
    PRIMARY KEY (profile_id, fachgebiet)
);

-- Student fragt Thema an (für Accept/Decline durch Betreuer)
CREATE TABLE student_theme_request (
    profile_id     BIGINT       NOT NULL REFERENCES student(profile_id) ON DELETE CASCADE,
    theme_id       UUID         NOT NULL REFERENCES theme(theme_id) ON DELETE CASCADE,
    status         VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    requested_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    PRIMARY KEY (profile_id, theme_id)
);

-- Indizes für Abfragen
CREATE INDEX idx_student_theme_request_theme ON student_theme_request(theme_id);
CREATE INDEX idx_student_theme_request_profile ON student_theme_request(profile_id);
CREATE INDEX idx_student_veranstaltung_profile ON student_veranstaltung(profile_id);
CREATE INDEX idx_student_fachgebiet_profile ON student_fachgebiet(profile_id);
