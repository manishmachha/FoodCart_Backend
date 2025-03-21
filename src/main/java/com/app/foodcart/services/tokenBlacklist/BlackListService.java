package com.app.foodcart.services.tokenBlacklist;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BlackListService {
    // Using ConcurrentHashMap for thread safety
    private Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    public Set<String> getBlacklistedTokens() {
        return blacklistedTokens;
    }

    @Async("taskExecutor")
    public CompletableFuture<Void> blacklistTokenAsync(String token) {
        blacklistedTokens.add(token);
        return CompletableFuture.completedFuture(null);
    }

    // For backward compatibility
    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}