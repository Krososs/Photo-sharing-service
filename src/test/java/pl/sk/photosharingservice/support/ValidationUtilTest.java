package pl.sk.photosharingservice.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {

    @Test
    void checkUsername() {
        assertFalse(ValidationUtil.checkUsername(""));
        assertFalse(ValidationUtil.checkUsername(null));
        assertTrue(ValidationUtil.checkUsername("SimpleUsername"));

    }

    @Test
    void checkPassword() {
        assertFalse(ValidationUtil.checkPassword("12345"));
        assertFalse(ValidationUtil.checkPassword(null));
        assertFalse(ValidationUtil.checkPassword("jJp1"));
        assertTrue(ValidationUtil.checkPassword("1ABCdefg2"));
    }

    @Test
    void checkEmail() {

        assertTrue(ValidationUtil.checkEmail("simpleEmail@gmail.com"));
        assertTrue(ValidationUtil.checkEmail("x@x.x"));
        assertFalse(ValidationUtil.checkEmail("false.com"));
        assertFalse(ValidationUtil.checkEmail("false@falsecom"));

    }
}