package org.example.spring_backend_clothingstore.composition.phone_number;

import java.util.Objects;

public record PhoneNumber(String phoneNumber) {
    public PhoneNumber{
        Objects.requireNonNull(phoneNumber, "Phone number cannot be null");
        if(!phoneNumber.matches("^(0|84)(2(0[3-9]|1[0-689]|2[0-25-9]|3[2-9]|4[0-9]|5[124-9]|6[0369]|7[0-7]|8[0-9]|9[012346789])|3[2-9]|5[25689]|7[06-9]|8[0-9]|9[012346789])([0-9]{7})$"))
        {
            throw new IllegalArgumentException("Phone number is not valid in VietNam");
        }
    }
}
