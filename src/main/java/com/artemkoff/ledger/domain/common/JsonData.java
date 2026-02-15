package com.artemkoff.ledger.domain.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;

import java.util.Objects;

public record JsonData(JsonNode value) {
    public JsonData {
        Objects.requireNonNull(value, "json value cannot be null");
    }

    public static JsonData of(JsonNode value) {
        return new JsonData(value);
    }

    public static JsonData empty() {
        return new JsonData(NullNode.instance);
    }

    public boolean isEmpty() {
        return value.isNull() || value.isEmpty();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
