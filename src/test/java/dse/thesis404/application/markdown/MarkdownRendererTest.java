package dse.thesis404.application.markdown;

import dse.thesis404.infrastructure.persistence.inmemory.SimpleMarkdownRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Markdown-Renderer – XSS-Schutz")
class MarkdownRendererTest {

    private SimpleMarkdownRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = new SimpleMarkdownRenderer();
    }

    @Test
    void script_tag_wird_escapet() {
        String input = "Text <script>alert('xss')</script> mehr";
        String html = renderer.toHtml(input);
        assertThat(html).doesNotContain("<script>");
        assertThat(html).contains("&lt;script&gt;");
    }

    @Test
    void normaler_markdown_wird_gerendert() {
        String input = "Hallo\n\nWelt";
        String html = renderer.toHtml(input);
        assertThat(html).contains("<p>");
        assertThat(html).contains("Hallo");
    }

    @Test
    void null_oder_leer_ergibt_leeren_string() {
        assertThat(renderer.toHtml(null)).isEmpty();
        assertThat(renderer.toHtml("")).isEmpty();
        assertThat(renderer.toHtml("   ")).isEmpty();
    }
}
