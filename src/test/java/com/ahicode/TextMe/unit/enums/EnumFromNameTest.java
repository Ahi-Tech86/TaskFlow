package com.ahicode.TextMe.unit.enums;

import com.ahicode.TextMe.exception.AppException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public abstract class EnumFromNameTest<E extends Enum<E>> {

    protected abstract E fromName(String name);
    protected abstract E getValidEnumValue();
    protected abstract String getInvalidEnumName();
    protected abstract String getExpectedExceptionMessage(String name);

    @Test
    void testFromName_validValue() {
        E enumValue = fromName(getValidEnumValue().name());
        assertThat(enumValue).isEqualTo(getValidEnumValue());
    }

    @Test
    void testFromName_validValueLowerCase() {
        E enumValue = fromName(getValidEnumValue().name().toLowerCase());
        assertThat(enumValue).isEqualTo(getValidEnumValue());
    }

    @Test
    void testFromName_invalidValue() {
        assertThatThrownBy(() -> fromName(getInvalidEnumName()))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(getExpectedExceptionMessage(getInvalidEnumName()))
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
    }
}
