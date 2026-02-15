package com.artemkoff.ledger.domain.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class JsonDataTests {
    static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void createsEmptyJsonData() {
        var obj = MAPPER.createObjectNode();
        var jd = JsonData.of(obj);

        assertThat(jd.isEmpty()).isEqualTo(true);
    }

    @Test
    void createsNonEmptyJsonData() {
        var obj = MAPPER.createObjectNode();
        obj.put("key", "value");

        var jd = new JsonData(obj);

        assertThat(jd.isEmpty()).isEqualTo(false);
    }

    @Test
    void emptyMethodCreatesEmptyJsonData() {
        var jd = JsonData.empty();
        assertThat(jd.isEmpty()).isEqualTo(true);
    }

    @Test
    void throwsOnNullJsonData() {
        assertThatThrownBy(() -> JsonData.of(null))
                .isInstanceOf(NullPointerException.class);
    }
}
