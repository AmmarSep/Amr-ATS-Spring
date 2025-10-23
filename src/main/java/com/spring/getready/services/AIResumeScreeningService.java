package com.spring.getready.services;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AIResumeScreeningService {

    public Map<String, Object> analyzeResume(String resumeText, String requiredSkills) {
        Map<String, Object> result = new HashMap<>();
        
        String resumeLower = resumeText.toLowerCase();
        String[] skillsArray = requiredSkills.toLowerCase().split(",");
        
        List<String> matchedSkills = new ArrayList<>();
        int matchCount = 0;
        
        for (String skill : skillsArray) {
            String trimmedSkill = skill.trim();
            if (resumeLower.contains(trimmedSkill)) {
                matchedSkills.add(trimmedSkill);
                matchCount++;
            }
        }
        
        double score = skillsArray.length > 0 ? 
            (matchCount * 100.0 / skillsArray.length) : 0.0;
        
        result.put("score", Math.round(score * 100.0) / 100.0);
        result.put("matchedSkills", String.join(", ", matchedSkills));
        result.put("totalSkills", skillsArray.length);
        result.put("matchedCount", matchCount);
        
        return result;
    }

    public String extractKeywords(String text) {
        String[] commonWords = {"the", "is", "at", "which", "on", "a", "an", "and", "or", "but", "in", "with", "to", "for", "of", "as", "by"};
        Set<String> stopWords = new HashSet<>(Arrays.asList(commonWords));
        
        return Arrays.stream(text.toLowerCase().split("\\W+"))
            .filter(word -> word.length() > 3 && !stopWords.contains(word))
            .distinct()
            .limit(20)
            .collect(Collectors.joining(", "));
    }

    public int calculateExperienceScore(String resumeText) {
        String lower = resumeText.toLowerCase();
        int score = 0;
        
        if (lower.contains("years") || lower.contains("experience")) score += 20;
        if (lower.contains("project") || lower.contains("developed")) score += 15;
        if (lower.contains("team") || lower.contains("lead")) score += 10;
        if (lower.contains("managed") || lower.contains("coordinated")) score += 10;
        
        return Math.min(score, 50);
    }
}
