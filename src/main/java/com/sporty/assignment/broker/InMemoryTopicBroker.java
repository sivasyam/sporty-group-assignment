package com.sporty.assignment.broker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public abstract class InMemoryTopicBroker {
    private final Map<String, CopyOnWriteArrayList<Consumer<String>>> subscribers = new ConcurrentHashMap<>();
    private final Map<String, CopyOnWriteArrayList<String>> history = new ConcurrentHashMap<>();

    public void subscribe(String topic, Consumer<String> consumer) {
        subscribers.computeIfAbsent(topic, ignored -> new CopyOnWriteArrayList<>()).add(consumer);
    }

    public void publish(String topic, String payload) {
        history.computeIfAbsent(topic, ignored -> new CopyOnWriteArrayList<>()).add(payload);
        List<Consumer<String>> listeners = new ArrayList<>(subscribers.getOrDefault(topic, new CopyOnWriteArrayList<>()));
        for (Consumer<String> listener : listeners) {
            listener.accept(payload);
        }
    }

    public List<String> messages(String topic) {
        return List.copyOf(history.getOrDefault(topic, new CopyOnWriteArrayList<>()));
    }
}

