-- Korrektur: Platzhalter "—" ist kein gültiges Raum-Format (erwartet z.B. 25.12.02.03)
UPDATE betreuer_profil SET raum = '00.00.00.01' WHERE raum = '—';
