package com.github.lurldgbodex.validation;

import com.github.lurldgbodex.annotations.ValidationRule;
import com.github.lurldgbodex.core.Validator;
import com.github.lurldgbodex.exceptions.ValidatorException;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

public class RuleBasedValidator implements Validator {

    @Override
    public <T> void validate(T obj) throws ValidatorException {
        for (Field field: obj.getClass().getDeclaredFields()) {
            ValidationRule rule = field.getAnnotation(ValidationRule.class);

            if (rule != null) {
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);

                    if (rule.required() && value == null) {
                        throw new ValidatorException("Field " + field.getName() + " is required");
                    }
                    if (value != null) {

                        Class<?> fieldType = field.getType();

                        if (fieldType == String.class) {
                            String strValue = value.toString();
                            if (strValue.length() < rule.minLength()) {
                                throw new ValidatorException("Field " + field.getName()
                                        + " must be at least " + rule.minLength() + " characters long");
                            }

                            if (strValue.length() > rule.maxLength()) {
                                throw new ValidatorException("Field " + field.getName()
                                        + " must not exceed " + rule.maxLength() + " characters");
                            }

                            if (!rule.regex().isEmpty() && !Pattern.matches(rule.regex(), strValue)) {
                                throw new ValidatorException("Field " + field.getName()
                                        + " does not match the required pattern");
                            }
                        }
                    }
                } catch (IllegalAccessException ex) {
                    throw new ValidatorException("Failed to validate field", ex);
                }
            }
        }

    }
}
