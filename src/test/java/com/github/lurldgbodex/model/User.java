package com.github.lurldgbodex.model;

import com.github.lurldgbodex.annotations.FieldMapping;
import com.github.lurldgbodex.annotations.ValidationRule;

public record User(
        @FieldMapping(column = "name")
        String name,
        String email,
        boolean active
) {}
