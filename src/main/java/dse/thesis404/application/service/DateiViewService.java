package dse.thesis404.application.service;

import dse.thesis404.application.port.FileStoragePort;
import dse.thesis404.application.port.MarkdownRendererPort;
import dse.thesis404.domain.model.shared.Datei;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
public class DateiViewService {

    private final FileStoragePort fileStorage;
    private final MarkdownRendererPort markdownRenderer;


    public DateiViewService(FileStoragePort fileStorage, MarkdownRendererPort markdownRenderer) {
        this.fileStorage = Objects.requireNonNull(fileStorage, "fileStorage");
        this.markdownRenderer = Objects.requireNonNull(markdownRenderer, "markdownRenderer");
    }

    
    public DownloadResult loadForDownload(Datei datei) {
        Objects.requireNonNull(datei, "datei");

        if (!datei.isDownloadable()) {
            throw new IllegalArgumentException("Datei ist nicht downloadbar: " + datei.type());
        }

        byte[] bytes = loadBytesFromStorage(datei.storedFilename(), Datei.MAX_BYTES);

        return new DownloadResult(
                bytes,
                guessContentType(datei),
                preferredDownloadFilename(datei)
        );
    }

    
    public String renderMarkdownAsHtml(Datei datei) {
        Objects.requireNonNull(datei, "datei");

        if (!datei.isInlineViewable()) {
            throw new IllegalArgumentException("Datei ist nicht inline viewable: " + datei.type());
        }

        byte[] raw = loadBytesFromStorage(datei.storedFilename(), Datei.MAX_BYTES);

        String md = new String(raw, StandardCharsets.UTF_8);

        // Sicherheit: Der MarkdownRendererPort muss jedes HTML im Text escapen.
        // Nur weil der Renderer escaped, darf sein Ergebnis überhaupt mit th:utext ausgegeben werden.
        return markdownRenderer.toHtml(md);
    }

    private byte[] loadBytesFromStorage(String storedFilename, long maxBytes) {
        Objects.requireNonNull(storedFilename, "storedFilename");

        Optional<InputStream> opt = fileStorage.load(storedFilename);
        InputStream stream = opt.orElseThrow(() ->
                new NoSuchElementException("Datei nicht im Dateisystem gefunden: " + storedFilename)
        );

        try (InputStream in = stream; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            long total = 0;
            int read;
            while ((read = in.read(buffer)) != -1) {
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

    private static String preferredDownloadFilename(Datei datei) {

        String orig = datei.originalFilename();
        if (orig != null && !orig.isBlank()) return orig;

        String title = datei.title();
        if (title != null && !title.isBlank()) {

            String safe = title.trim().replaceAll("\\s+", "_");
            return safe + "." + datei.type().extension();
        }

        return datei.storedFilename();
    }

    private static String guessContentType(Datei datei) {
        return switch (datei.type()) {
            case PDF -> "application/pdf";
            case ZIP -> "application/zip";
            case MARKDOWN -> "text/html; charset=UTF-8";
            default -> "application/octet-stream";
        };
    }

    
    public record DownloadResult(byte[] bytes, String contentType, String filename) {}
}
