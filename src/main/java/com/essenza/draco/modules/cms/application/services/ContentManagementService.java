//package com.essenza.draco.modules.cms.application.services;
//
//import com.essenza.draco.modules.cms.domain.dto.page.PageDto;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.regex.Pattern;
//
//@Service
//public class ContentManagementService {
//
//    private final SEOOptimizationService seoService;
//    private final ContentSchedulerService schedulerService;
//
//    public ContentManagementService(SEOOptimizationService seoService,
//                                  ContentSchedulerService schedulerService) {
//        this.seoService = seoService;
//        this.schedulerService = schedulerService;
//    }
//
//    /**
//     * Procesa y optimiza contenido antes de guardarlo
//     */
//    public PageDto processContent(PageDto page) {
//        // 1. Generar slug automático si no existe
//        if (page.getSlug() == null || page.getSlug().isEmpty()) {
//            page.setSlug(generateSlug(page.getTitle()));
//        }
//
//        // 2. Generar excerpt automático si no existe
//        if (page.getExcerpt() == null || page.getExcerpt().isEmpty()) {
//            page.setExcerpt(generateExcerpt(page.getContent()));
//        }
//
//        // 3. Optimizar SEO automáticamente
//        page = seoService.optimizePage(page);
//
//        // 4. Procesar contenido multimedia
//        page.setContent(processMediaContent(page.getContent()));
//
//        // 5. Validar y limpiar HTML
//        page.setContent(sanitizeHTML(page.getContent()));
//
//        // 6. Programar publicación si es necesario
//        if (page.getScheduledAt() != null && page.getScheduledAt().isAfter(LocalDateTime.now())) {
//            schedulerService.schedulePublication(page);
//            page.setStatus("SCHEDULED");
//        }
//
//        return page;
//    }
//
//    /**
//     * Genera slug SEO-friendly a partir del título
//     */
//    private String generateSlug(String title) {
//        if (title == null) return "";
//
//        return title.toLowerCase()
//                   .replaceAll("[áàäâ]", "a")
//                   .replaceAll("[éèëê]", "e")
//                   .replaceAll("[íìïî]", "i")
//                   .replaceAll("[óòöô]", "o")
//                   .replaceAll("[úùüû]", "u")
//                   .replaceAll("[ñ]", "n")
//                   .replaceAll("[^a-z0-9\\s-]", "")
//                   .replaceAll("\\s+", "-")
//                   .replaceAll("-+", "-")
//                   .replaceAll("^-|-$", "");
//    }
//
//    /**
//     * Genera excerpt automático del contenido
//     */
//    private String generateExcerpt(String content) {
//        if (content == null) return "";
//
//        // Remover HTML tags
//        String plainText = content.replaceAll("<[^>]+>", "");
//
//        // Tomar primeras 160 caracteres
//        if (plainText.length() <= 160) {
//            return plainText;
//        }
//
//        String excerpt = plainText.substring(0, 160);
//        int lastSpace = excerpt.lastIndexOf(" ");
//
//        if (lastSpace > 0) {
//            excerpt = excerpt.substring(0, lastSpace);
//        }
//
//        return excerpt + "...";
//    }
//
//    /**
//     * Procesa contenido multimedia (imágenes, videos)
//     */
//    private String processMediaContent(String content) {
//        if (content == null) return "";
//
//        // Optimizar imágenes
//        content = optimizeImages(content);
//
//        // Procesar videos
//        content = processVideos(content);
//
//        // Añadir lazy loading
//        content = addLazyLoading(content);
//
//        return content;
//    }
//
//    /**
//     * Optimiza imágenes en el contenido
//     */
//    private String optimizeImages(String content) {
//        // Añadir atributos alt automáticamente
//        Pattern imgPattern = Pattern.compile("<img([^>]*?)src=[\"']([^\"']*)[\"']([^>]*?)>");
//
//        return imgPattern.matcher(content).replaceAll(matchResult -> {
//            String beforeSrc = matchResult.group(1);
//            String src = matchResult.group(2);
//            String afterSrc = matchResult.group(3);
//
//            // Si no tiene alt, añadirlo
//            if (!afterSrc.contains("alt=") && !beforeSrc.contains("alt=")) {
//                String filename = src.substring(src.lastIndexOf("/") + 1);
//                String altText = filename.replaceAll("\\.[^.]*$", "").replaceAll("[_-]", " ");
//                afterSrc += " alt=\"" + altText + "\"";
//            }
//
//            // Añadir loading lazy
//            if (!afterSrc.contains("loading=") && !beforeSrc.contains("loading=")) {
//                afterSrc += " loading=\"lazy\"";
//            }
//
//            return "<img" + beforeSrc + "src=\"" + src + "\"" + afterSrc + ">";
//        });
//    }
//
//    /**
//     * Procesa videos embebidos
//     */
//    private String processVideos(String content) {
//        // Convertir URLs de YouTube a embeds responsivos
//        Pattern youtubePattern = Pattern.compile("https?://(?:www\\.)?youtube\\.com/watch\\?v=([a-zA-Z0-9_-]+)");
//
//        content = youtubePattern.matcher(content).replaceAll(matchResult -> {
//            String videoId = matchResult.group(1);
//            return "<div class=\"video-responsive\">" +
//                   "<iframe src=\"https://www.youtube.com/embed/" + videoId + "\" " +
//                   "frameborder=\"0\" allowfullscreen loading=\"lazy\"></iframe>" +
//                   "</div>";
//        });
//
//        return content;
//    }
//
//    /**
//     * Añade lazy loading a elementos multimedia
//     */
//    private String addLazyLoading(String content) {
//        // Ya se maneja en optimizeImages, pero se puede extender para otros elementos
//        return content;
//    }
//
//    /**
//     * Sanitiza HTML para seguridad
//     */
//    private String sanitizeHTML(String content) {
//        if (content == null) return "";
//
//        // Lista de tags permitidos
//        Set<String> allowedTags = Set.of(
//            "p", "br", "strong", "b", "em", "i", "u", "h1", "h2", "h3", "h4", "h5", "h6",
//            "ul", "ol", "li", "a", "img", "div", "span", "blockquote", "code", "pre",
//            "table", "thead", "tbody", "tr", "td", "th", "iframe"
//        );
//
//        // Remover scripts y otros elementos peligrosos
//        content = content.replaceAll("<script[^>]*>.*?</script>", "");
//        content = content.replaceAll("<style[^>]*>.*?</style>", "");
//        content = content.replaceAll("javascript:", "");
//        content = content.replaceAll("on\\w+\\s*=", "");
//
//        return content;
//    }
//
//    /**
//     * Genera contenido dinámico basado en plantillas
//     */
//    public String renderDynamicContent(PageDto page, Map<String, Object> context) {
//        String content = page.getContent();
//
//        // Procesar variables de plantilla
//        content = processTemplateVariables(content, context);
//
//        // Procesar shortcodes
//        content = processShortcodes(content, context);
//
//        // Procesar contenido personalizado por usuario
//        content = processPersonalizedContent(content, context);
//
//        return content;
//    }
//
//    /**
//     * Procesa variables de plantilla como {{variable}}
//     */
//    private String processTemplateVariables(String content, Map<String, Object> context) {
//        Pattern variablePattern = Pattern.compile("\\{\\{([^}]+)\\}\\}");
//
//        return variablePattern.matcher(content).replaceAll(matchResult -> {
//            String variableName = matchResult.group(1).trim();
//            Object value = context.get(variableName);
//            return value != null ? value.toString() : "";
//        });
//    }
//
//    /**
//     * Procesa shortcodes como [gallery] [products category="electronics"]
//     */
//    private String processShortcodes(String content, Map<String, Object> context) {
//        // Shortcode de galería
//        Pattern galleryPattern = Pattern.compile("\\[gallery\\s+([^\\]]+)\\]");
//        content = galleryPattern.matcher(content).replaceAll(matchResult -> {
//            String attributes = matchResult.group(1);
//            return generateGalleryHTML(attributes, context);
//        });
//
//        // Shortcode de productos
//        Pattern productsPattern = Pattern.compile("\\[products\\s+([^\\]]+)\\]");
//        content = productsPattern.matcher(content).replaceAll(matchResult -> {
//            String attributes = matchResult.group(1);
//            return generateProductsHTML(attributes, context);
//        });
//
//        // Shortcode de testimonios
//        Pattern testimonialsPattern = Pattern.compile("\\[testimonials\\s*([^\\]]*)\\]");
//        content = testimonialsPattern.matcher(content).replaceAll(matchResult -> {
//            String attributes = matchResult.group(1);
//            return generateTestimonialsHTML(attributes, context);
//        });
//
//        return content;
//    }
//
//    /**
//     * Procesa contenido personalizado por usuario
//     */
//    private String processPersonalizedContent(String content, Map<String, Object> context) {
//        Long customerId = (Long) context.get("customerId");
//        if (customerId == null) return content;
//
//        // Personalizar saludos
//        String customerName = (String) context.get("customerName");
//        if (customerName != null) {
//            content = content.replace("[customer_name]", customerName);
//        }
//
//        // Mostrar productos recomendados
//        content = content.replace("[recommended_products]", generateRecommendedProductsHTML(customerId));
//
//        return content;
//    }
//
//    // Métodos auxiliares para generar HTML de shortcodes
//
//    private String generateGalleryHTML(String attributes, Map<String, Object> context) {
//        // Parsear atributos y generar HTML de galería
//        return "<div class=\"gallery\"><!-- Gallery content --></div>";
//    }
//
//    private String generateProductsHTML(String attributes, Map<String, Object> context) {
//        // Parsear atributos y generar HTML de productos
//        return "<div class=\"products-grid\"><!-- Products content --></div>";
//    }
//
//    private String generateTestimonialsHTML(String attributes, Map<String, Object> context) {
//        // Generar HTML de testimonios
//        return "<div class=\"testimonials\"><!-- Testimonials content --></div>";
//    }
//
//    private String generateRecommendedProductsHTML(Long customerId) {
//        // Generar HTML de productos recomendados
//        return "<div class=\"recommended-products\"><!-- Recommended products --></div>";
//    }
//
//    /**
//     * Genera sitemap XML automáticamente
//     */
//    public String generateSitemap(List<PageDto> pages) {
//        StringBuilder sitemap = new StringBuilder();
//        sitemap.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
//        sitemap.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
//
//        for (PageDto page : pages) {
//            if ("PUBLISHED".equals(page.getStatus())) {
//                sitemap.append("  <url>\n");
//                sitemap.append("    <loc>https://yourstore.com/").append(page.getSlug()).append("</loc>\n");
//                sitemap.append("    <lastmod>").append(page.getUpdatedAt()).append("</lastmod>\n");
//                sitemap.append("    <changefreq>weekly</changefreq>\n");
//                sitemap.append("    <priority>0.8</priority>\n");
//                sitemap.append("  </url>\n");
//            }
//        }
//
//        sitemap.append("</urlset>");
//        return sitemap.toString();
//    }
//}
