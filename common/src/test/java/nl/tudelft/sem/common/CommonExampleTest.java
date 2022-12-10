package nl.tudelft.sem.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CommonExampleTest {
    @Test
    void testExample() {
        assertThat(CommonExample.example()).isEqualTo(3);
    }
}
