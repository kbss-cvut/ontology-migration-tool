import cz.cvut.fel.exceptions.ChangeLogValidationException;
import cz.cvut.fel.utils.ChangeLogValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChangeLogValidatorTest {
    private ChangeLogValidator validator;

    @BeforeEach
    void setUp() throws IOException {
        validator = new ChangeLogValidator();
    }

    @Test
    void validateCorrectChangeLog() throws Exception {
        InputStream input = getClass()
                .getResourceAsStream("/valid-changelog.yaml");
        assertDoesNotThrow(() -> validator.validate(input));
    }

    @Test
    void throwExceptionForInvalidChangeLog() throws Exception {
        InputStream input = getClass()
                .getResourceAsStream("/invalid-changelog.yaml");
        assertThrows(
                ChangeLogValidationException.class,
                () -> validator.validate(input)
        );
    }
}
