-- Optimistic Locking: jede Änderung erhöht die Version. Ein UPDATE mit veralteter Version
-- trifft keine Zeile mehr und wird abgelehnt, statt die Änderung eines anderen still zu überschreiben.
ALTER TABLE theme ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
