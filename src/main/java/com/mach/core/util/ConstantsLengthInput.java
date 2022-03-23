package com.mach.core.util;

public enum ConstantsLengthInput
{
    LENGTH_PIN(4),
    LENGTH_NUM_ADDRESS(6),
    LENGTH_ID_DOCUMENT(10),
    LENGTH_RUT(12),
    LENGTH_FIRST_NAME(20),
    LENGTH_LAST_NAME(20),
    LENGTH_EMAIL(254);

    private int number;

    public int getNumber()
    {
        return this.number;
    }

    private ConstantsLengthInput(int number)
    {
        this.number = number;
    }
}