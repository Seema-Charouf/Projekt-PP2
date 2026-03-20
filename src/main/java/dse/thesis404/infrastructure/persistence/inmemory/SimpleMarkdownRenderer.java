package dse.thesis404.infrastructure.persistence.inmemory;

import dse.thesis404.application.port.MarkdownRendererPort;
import org.springframework.stereotype.Component;

@Component
public class SimpleMarkdownRenderer implements MarkdownRendererPort {

    @Override
    public String toHtml(String markdown) {
        if (markdown == null || markdown.isBlank()) {
            return "";
        }

        String escaped = markdown
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n\n", "</p><p>")
                .replace("\n", "<br>");
        return "<p>" + escaped + "</p>";
    }
}
