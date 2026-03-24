package com.bettingsim.service;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class OddsService {

    @Value("${app.odds.rss.url}") private String rssUrl;

    // In-memory cache: matchKey -> {home, draw, away}
    private final Map<String, double[]> oddsCache = new ConcurrentHashMap<>();

    @Scheduled(cron = "${app.odds.refresh.cron}")
    public void refreshOdds() {
        try {
            SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(rssUrl)));
            for (SyndEntry entry : feed.getEntries()) {
                parseAndCacheEntry(entry);
            }
            log.info("Odds refreshed: {} entries cached", oddsCache.size());
        } catch (Exception e) {
            log.warn("Failed to refresh odds from RSS: {}", e.getMessage());
        }
    }

    private void parseAndCacheEntry(SyndEntry entry) {
        // Parse title like "Team A vs Team B - Home 1.80 Draw 3.40 Away 4.50"
        String title = entry.getTitle();
        if (title == null) return;
        try {
            String[] parts = title.split(" - ");
            if (parts.length < 2) return;
            String matchKey = parts[0].trim().toLowerCase();
            String oddsPart = parts[1];
            double home = extractOdds(oddsPart, "Home");
            double draw = extractOdds(oddsPart, "Draw");
            double away = extractOdds(oddsPart, "Away");
            if (home > 0) oddsCache.put(matchKey, new double[]{home, draw, away});
        } catch (Exception e) {
            log.debug("Could not parse odds entry: {}", title);
        }
    }

    private double extractOdds(String text, String label) {
        int idx = text.indexOf(label);
        if (idx < 0) return 0;
        String[] tokens = text.substring(idx + label.length()).trim().split("\\s+");
        try { return Double.parseDouble(tokens[0]); } catch (NumberFormatException e) { return 0; }
    }

    public double[] getOddsForMatch(String homeTeam, String awayTeam) {
        String key = (homeTeam + " vs " + awayTeam).toLowerCase();
        return oddsCache.getOrDefault(key, new double[]{0, 0, 0});
    }

    public List<Map<String, Object>> getAllCachedOdds() {
        List<Map<String, Object>> result = new ArrayList<>();
        oddsCache.forEach((k, v) -> result.add(Map.of(
                "match", k, "oddsHome", v[0], "oddsDraw", v[1], "oddsAway", v[2])));
        return result;
    }
}
