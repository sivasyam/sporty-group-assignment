package com.sporty.assignment.util;

import com.sporty.assignment.model.Bet;
import com.sporty.assignment.model.EventOutcome;
import com.sporty.assignment.model.SettlementMessage;
import com.sporty.assignment.model.SettlementStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JsonCodec {
    private static final Pattern FIELD_PATTERN = Pattern.compile(
            "\"([^\"]+)\"\\s*:\\s*(\"((?:\\\\.|[^\"])*)\"|true|false|null|-?\\d+(?:\\.\\d+)?)"
    );

    private JsonCodec() {
    }

    public static EventOutcome fromEventOutcome(String json) {
        Map<String, String> values = parseObject(json);
        return new EventOutcome(
                requireLong(values, "eventId"),
                requireString(values, "eventName"),
                requireLong(values, "eventWinnerId")
        );
    }

    public static Bet fromBet(String json) {
        Map<String, String> values = parseObject(json);
        return new Bet(
                requireLong(values, "betId"),
                requireLong(values, "userId"),
                requireLong(values, "eventId"),
                requireLong(values, "eventMarketId"),
                requireLong(values, "eventWinnerId"),
                requireBigDecimal(values, "betAmount")
        );
    }

    public static SettlementMessage fromSettlementMessage(String json) {
        Map<String, String> values = parseObject(json);
        return new SettlementMessage(
                requireLong(values, "betId"),
                requireLong(values, "userId"),
                requireLong(values, "eventId"),
                requireLong(values, "eventMarketId"),
                requireLong(values, "eventWinnerId"),
                requireBigDecimal(values, "betAmount"),
                SettlementStatus.valueOf(requireString(values, "status")),
                Instant.parse(requireString(values, "settledAt"))
        );
    }

    public static String toJson(EventOutcome outcome) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("eventId", outcome.eventId());
        map.put("eventName", outcome.eventName());
        map.put("eventWinnerId", outcome.eventWinnerId());
        return object(map);
    }

    public static String toJson(Bet bet) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("betId", bet.betId());
        map.put("userId", bet.userId());
        map.put("eventId", bet.eventId());
        map.put("eventMarketId", bet.eventMarketId());
        map.put("eventWinnerId", bet.eventWinnerId());
        map.put("betAmount", bet.betAmount());
        return object(map);
    }

    public static String toJson(SettlementMessage message) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("betId", message.betId());
        map.put("userId", message.userId());
        map.put("eventId", message.eventId());
        map.put("eventMarketId", message.eventMarketId());
        map.put("eventWinnerId", message.eventWinnerId());
        map.put("betAmount", message.betAmount());
        map.put("status", message.status().name());
        map.put("settledAt", message.settledAt().toString());
        return object(map);
    }

    public static String toJson(List<?> values) {
        List<String> items = new ArrayList<>();
        for (Object value : values) {
            if (value instanceof Bet bet) {
                items.add(toJson(bet));
            } else if (value instanceof EventOutcome outcome) {
                items.add(toJson(outcome));
            } else if (value instanceof SettlementMessage settlement) {
                items.add(toJson(settlement));
            } else if (value instanceof String string) {
                items.add('"' + escape(string) + '"');
            } else {
                items.add(String.valueOf(value));
            }
        }
        return '[' + String.join(",", items) + ']';
    }

    private static String object(Map<String, ?> values) {
        StringBuilder builder = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, ?> entry : values.entrySet()) {
            if (!first) {
                builder.append(',');
            }
            first = false;
            builder.append('"').append(escape(entry.getKey())).append('"').append(':');
            Object value = entry.getValue();
            if (value == null) {
                builder.append("null");
            } else if (value instanceof Number || value instanceof Boolean) {
                builder.append(value);
            } else {
                builder.append('"').append(escape(String.valueOf(value))).append('"');
            }
        }
        return builder.append('}').toString();
    }

    private static Map<String, String> parseObject(String json) {
        if (json == null) {
            throw new IllegalArgumentException("Request body is empty");
        }
        String trimmed = json.trim();
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
            throw new IllegalArgumentException("Expected a JSON object");
        }

        Map<String, String> values = new LinkedHashMap<>();
        Matcher matcher = FIELD_PATTERN.matcher(trimmed);
        while (matcher.find()) {
            String key = matcher.group(1);
            String raw = matcher.group(2);
            String value = raw.startsWith("\"") ? unescape(matcher.group(3)) : raw;
            values.put(key, value);
        }
        if (values.isEmpty()) {
            throw new IllegalArgumentException("No JSON fields found");
        }
        return values;
    }

    private static String requireString(Map<String, String> values, String key) {
        String value = values.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing field: " + key);
        }
        return value;
    }

    private static long requireLong(Map<String, String> values, String key) {
        try {
            return Long.parseLong(requireString(values, key));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid long value for " + key);
        }
    }

    private static BigDecimal requireBigDecimal(Map<String, String> values, String key) {
        try {
            return new BigDecimal(requireString(values, key));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid decimal value for " + key);
        }
    }

    private static String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    private static String unescape(String value) {
        return value
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }
}
