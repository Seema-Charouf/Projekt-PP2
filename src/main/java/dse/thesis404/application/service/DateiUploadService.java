package dse.thesis404.application.service;

import dse.thesis404.application.command.UploadInfoFileCommand;
import dse.thesis404.application.port.FileStoragePort;
import dse.thesis404.domain.model.shared.Datei;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class DateiUploadService {

    private final FileStoragePort fileStorage;
    private final Clock clock;

    private static final Pattern SCRIPT_BLOCK =
            Pattern.compile("(?is)<\\s*script\\b[^>]*>.*?<\\s*/\\s*script\\s*>");
    private static final Pattern SCRIPT_SELF_CLOSING =
            Pattern.compile("(?is)<\\s*script\\b[^>]*/\\s*>");
    private static final Pattern ANY_SCRIPT_TAG =
            Pattern.compile("(?i)<\\s*script\\b");

    public DateiUploadService(FileStoragePort fileStorage, Clock clock) {
        this.fileStorage = Objects.requireNonNull(fileStorage, "fileStorage");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    
    public StoredFileResult storeAndCreateMeta(String uploaderId, UploadInfoFileCommand cmd) {
        Objects.requireNonNull(uploaderId, "uploaderId");
        Objects.requireNonNull(cmd, "cmd");

        if (cmd.sizeBytes() > Datei.MAX_BYTES) {

            try (InputStream ignored = cmd.data()) {

            } catch (Exception ignore) {

            }
            throw new IllegalArgumentException("Datei ist zu groß (max 10 MB)!");
        }

        Datei.FileType type = Datei.FileType.fromOriginalFilename(cmd.originalFilename());

        String storedFilename = UUID.randomUUID() + "." + type.extension().toLowerCase(Locale.ROOT);

        Instant now = Instant.now(clock);

        final long storedSizeBytes;
        try (InputStream in = cmd.data()) {

            if (type == Datei.FileType.MARKDOWN) {
                byte[] raw = readAllBytesWithLimit(in, Datei.MAX_BYTES);
                byte[] sanitized = sanitizeMarkdown(raw);
                storedSizeBytes = sanitized.length;

                try (ByteArrayInputStream sanitizedIn = new ByteArrayInputStream(sanitized)) {
                    fileStorage.store(storedFilename, sanitizedIn);
                }

            } else {

                storedSizeBytes = cmd.sizeBytes();
                fileStorage.store(storedFilename, in);
            }

        } catch (FileStoragePort.FileStorageException e) {
            throw e;
        } catch (Exception e) {
            throw new FileStoragePort.FileStorageException("Datei konnte nicht gespeichert werden.", e);
        }

        Datei meta = Datei.createForUpload(
                cmd.title(),
                cmd.description(),
                uploaderId,
                now,
                type,
                storedSizeBytes,
                storedFilename,
                cmd.originalFilename()
        );

        return new StoredFileResult(meta, storedFilename);
    }

    
    public boolean deleteFromStorage(Datei datei) {
        Objects.requireNonNull(datei, "datei");
        return fileStorage.delete(datei.storedFilename());
    }

    
    private static byte[] readAllBytesWithLimit(InputStream in, long maxBytes) {
        Objects.requireNonNull(in, "inputStream");
        try (InputStream input = in; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            long total = 0;
            int read;
            while ((read = input.read(buffer)) != -1) {
                total += read;
                if (total > maxBytes) {
                    throw new IllegalArgumentException("Datei ist zu groß (max 10 MB)!");
                }
                out.write(buffer, 0, read);
            }
            byte[] bytes = out.toByteArray();
            if (bytes.length == 0) {
                throw new IllegalArgumentException("Datei ist leer!");
            }
            return bytes;
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException iae) throw iae;
            throw new FileStoragePort.FileStorageException("Datei konnte nicht gelesen werden.", e);
        }
    }

    
    private static byte[] sanitizeMarkdown(byte[] rawBytes) {
        String md = new String(rawBytes, StandardCharsets.UTF_8);

        String sanitized = SCRIPT_BLOCK.matcher(md).replaceAll("");
        sanitized = SCRIPT_SELF_CLOSING.matcher(sanitized).replaceAll("");

        if (ANY_SCRIPT_TAG.matcher(sanitized).find()) {
            throw new IllegalArgumentException("Markdown darf kein <script>-Tag enthalten!");
        }

        return sanitized.getBytes(StandardCharsets.UTF_8);
    }

    
    public record StoredFileResult(Datei meta, String storedFilename) { }
}
