package com.asdf.todo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;

class JsonParsingTests {

    private static final String JSON_WITH_UNESCAPED_NEWLINES =
            """
            [
                ["1", "2", "3"],
                ["4", "5", "6"],
                ["7", "8", "9"],
                ["a", "b", "c"],
                ["apple
                cake", "banana
                coffee", "brownie"],
                ["coke", "beer", "pure water"]
            ]""";

    @Test
    void parsesJsonWithUnescapedControlChars_whenFeatureEnabled() throws JsonProcessingException {
        JsonFactory factory =
                JsonFactory.builder().enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS).build();
        ObjectMapper mapper = new ObjectMapper(factory);

        List<List<String>> result =
                mapper.readValue(JSON_WITH_UNESCAPED_NEWLINES, new TypeReference<>() {});

        assertThat(result).hasSize(6);
        assertThat(result.get(0)).containsExactly("1", "2", "3");
        assertThat(result.get(4)).hasSize(3);
        assertThat(result.get(4).get(0)).contains("apple").contains("cake");
    }

    @Test
    void failsToParseJsonWithUnescapedControlChars_byDefault() {
        ObjectMapper mapper = new ObjectMapper();

        assertThatThrownBy(
                        () ->
                                mapper.readValue(
                                        JSON_WITH_UNESCAPED_NEWLINES,
                                        new TypeReference<List<List<String>>>() {}))
                .isInstanceOf(JsonProcessingException.class);
    }
}
