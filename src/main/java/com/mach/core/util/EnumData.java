package com.mach.core.util;

import com.mach.core.model.Address;
import com.mach.core.model.Bill;
import com.mach.core.model.Contract;
import com.mach.core.model.CreditCard;
import com.mach.core.model.Instructions;
import com.mach.core.model.Tax;
import com.mach.core.model.TermsAndConditions;
import com.mach.core.model.Text;
import com.mach.core.model.Transaction;
import com.mach.core.model.User;

public enum EnumData {

    CREDITCARDS("/CreditCards.json", CreditCard.class),
    TAXES("/Taxes.json", Tax.class),
    USERS("/Users.json", User.class),
    TRANSACTIONS("/Transactions.json", Transaction.class),
    INSTRUCTIONS("/Instructions.json", Instructions.class),
    TERMS_AND_CONDITIONS("/TermsAndConditions.json", TermsAndConditions.class),
    ADDRESSES("/Addresses.json", Address.class),
    TEXT("/Texts.json", Text.class),
    CONTRACTS("/Contracts.json", Contract.class),
    BILLS("/Bills.json", Bill.class);

    private final String file;
    private final Class<?> aClass;

    EnumData(String nameDriver, Class<?> aClass) {
        this.file = nameDriver;
        this.aClass = aClass;
    }

    public String getFile() {
        return file;
    }

    public Class<?> getaClass() {
        return aClass;
    }
}
