package com.essenza.draco.modules.reviews.application.services;

import com.essenza.draco.modules.reviews.domain.dto.product_review.ProductReviewDto;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ReviewModerationService {

    private static final List<String> INAPPROPRIATE_WORDS = Arrays.asList(
        "spam", "fake", "scam", "terrible", "worst", "hate", "awful"
    );

    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\b\\d{3}[-.]?\\d{3}[-.]?\\d{4}\\b");
    private static final Pattern URL_PATTERN = Pattern.compile("https?://[\\w\\.-]+");

    /**
     * Modera automáticamente una reseña
     */
    public ModerationResult moderateReview(ProductReviewDto review) {
        ModerationResult result = new ModerationResult();
        result.setReviewId(review.getId());
        result.setAutoApproved(true);
        result.setModerationNotes("");

        // Verificar palabras inapropiadas
        if (containsInappropriateContent(review.getComment()) || 
            containsInappropriateContent(review.getTitle())) {
            result.setAutoApproved(false);
            result.setModerationNotes("Contains inappropriate language");
            result.setStatus("REJECTED");
            return result;
        }

        // Verificar información personal
        if (containsPersonalInfo(review.getComment())) {
            result.setAutoApproved(false);
            result.setModerationNotes("Contains personal information (email, phone, URL)");
            result.setStatus("PENDING");
            return result;
        }

        // Verificar longitud sospechosa (muy corta o muy larga)
        if (review.getComment().length() < 10) {
            result.setAutoApproved(false);
            result.setModerationNotes("Review too short");
            result.setStatus("PENDING");
            return result;
        }

        // Verificar patrones de spam
        if (isSpamPattern(review.getComment())) {
            result.setAutoApproved(false);
            result.setModerationNotes("Detected spam pattern");
            result.setStatus("REJECTED");
            return result;
        }

        // Verificar rating vs contenido
        if (isRatingInconsistent(review.getRating(), review.getComment())) {
            result.setAutoApproved(false);
            result.setModerationNotes("Rating inconsistent with comment content");
            result.setStatus("PENDING");
            return result;
        }

        result.setStatus("APPROVED");
        result.setModerationNotes("Auto-approved by system");
        return result;
    }

    private boolean containsInappropriateContent(String text) {
        if (text == null) return false;
        String lowerText = text.toLowerCase();
        return INAPPROPRIATE_WORDS.stream().anyMatch(lowerText::contains);
    }

    private boolean containsPersonalInfo(String text) {
        if (text == null) return false;
        return EMAIL_PATTERN.matcher(text).find() || 
               PHONE_PATTERN.matcher(text).find() || 
               URL_PATTERN.matcher(text).find();
    }

    private boolean isSpamPattern(String text) {
        if (text == null) return false;
        
        // Verificar repetición excesiva de caracteres
        if (text.matches(".*([a-zA-Z])\\1{4,}.*")) {
            return true;
        }
        
        // Verificar exceso de mayúsculas
        long upperCaseCount = text.chars().filter(Character::isUpperCase).count();
        if (upperCaseCount > text.length() * 0.7) {
            return true;
        }
        
        return false;
    }

    private boolean isRatingInconsistent(Integer rating, String comment) {
        if (rating == null || comment == null) return false;
        
        String lowerComment = comment.toLowerCase();
        List<String> positiveWords = Arrays.asList("excellent", "great", "amazing", "perfect", "love", "best");
        List<String> negativeWords = Arrays.asList("terrible", "awful", "worst", "hate", "bad", "horrible");
        
        boolean hasPositiveWords = positiveWords.stream().anyMatch(lowerComment::contains);
        boolean hasNegativeWords = negativeWords.stream().anyMatch(lowerComment::contains);
        
        // Rating alto (4-5) con palabras negativas
        if (rating >= 4 && hasNegativeWords && !hasPositiveWords) {
            return true;
        }
        
        // Rating bajo (1-2) con palabras positivas
        if (rating <= 2 && hasPositiveWords && !hasNegativeWords) {
            return true;
        }
        
        return false;
    }

    public static class ModerationResult {
        private Long reviewId;
        private boolean autoApproved;
        private String status; // APPROVED, PENDING, REJECTED
        private String moderationNotes;

        // Getters and setters
        public Long getReviewId() { return reviewId; }
        public void setReviewId(Long reviewId) { this.reviewId = reviewId; }
        
        public boolean isAutoApproved() { return autoApproved; }
        public void setAutoApproved(boolean autoApproved) { this.autoApproved = autoApproved; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getModerationNotes() { return moderationNotes; }
        public void setModerationNotes(String moderationNotes) { this.moderationNotes = moderationNotes; }
    }
}
