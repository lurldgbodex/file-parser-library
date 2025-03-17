package com.github.lurldgbodex.core;

import com.github.lurldgbodex.exceptions.ValidatorException;

public interface Validator {
   <T> void validate(T obj) throws ValidatorException;
}
