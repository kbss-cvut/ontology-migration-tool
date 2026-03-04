import cz.cvut.kbss.exception.ChangeLogValidationException;
import cz.cvut.kbss.utils.ChangeLogValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChangeLogValidatorTest {
    private ChangeLogValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ChangeLogValidator();
    }

    @Test
    void validateCorrectChangeLog() throws IOException {
        try (InputStream input = getClass().getResourceAsStream("/valid-changelog.yaml")) {
            assertNotNull(input);
            final String str = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertDoesNotThrow(() -> validator.validate(str));
        }
    }

    @Test
    void throwExceptionForInvalidChangeLog() throws IOException {
        try (InputStream input = getClass().getResourceAsStream("/invalid-changelog.yaml")) {
            assertNotNull(input);
            final String str = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertThrows(ChangeLogValidationException.class, () -> validator.validate(str));
        }
    }
}
