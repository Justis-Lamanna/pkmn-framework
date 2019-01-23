package com.github.lucbui.bytes;

import java.util.Optional;

/**
 * A three-state "bit"
 */
public enum Tribit {
    /**
     * Bit value of 0
     */
    ZERO,
    /**
     * Bit value of 1
     */
    ONE,
    /**
     * Bit value of X
     */
    DONT_CARE;

    private static Optional<Tribit> handleDontCare(Tribit one, Tribit two){
        if(one == DONT_CARE && two == DONT_CARE){
            return Optional.of(DONT_CARE);
        } else if(one == DONT_CARE){
            return Optional.of(two);
        } else if(two == DONT_CARE){
            return Optional.of(one);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Logical and of two Tribits
     * If both arguments are DONT_CARE, DONT_CARE is returned
     * If one argument is DONT_CARE and the other is not, the one that is not DONT_CARE is returned.
     * Else, values are treated as a normal AND
     * @param one The first argument
     * @param two The second argument
     * @return The logical and
     */
    public static Tribit and(Tribit one, Tribit two){
        return handleDontCare(one, two)
                .orElseGet(() -> (one == ONE && two == ONE) ? ONE : ZERO);
    }

    /**
     * Logical or of two Tribits
     * If both arguments are DONT_CARE, DONT_CARE is returned
     * If one argument is DONT_CARE and the other is not, the one that is not DONT_CARE is returned.
     * Else, values are treated as a normal OR
     * @param one The first argument
     * @param two The second argument
     * @return The logical or
     */
    public static Tribit or(Tribit one, Tribit two){
        return handleDontCare(one, two)
                .orElseGet(() -> (one == ZERO && two == ZERO) ? ZERO : ONE);
    }

    /**
     * Logical not on a bit.
     * 0 => 1
     * 1 => 0
     * X => X
     * @param bit The bit to use
     * @return A tribit representing the logical inverse
     */
    public static Tribit not(Tribit bit){
        switch(bit){
            case ZERO: return ONE;
            case ONE: return ZERO;
            default: return bit;
        }
    }
}
