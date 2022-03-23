package com.mach.core.model.repository;

import com.mach.core.model.Address;
import com.mach.core.util.EnumData;
import data.TestData;

import java.util.List;
import java.util.function.Predicate;

public class AddressesRepository {

    public AddressesRepository() {
        // empty
    }

    /**
     * Get a specific address
     *
     * @param addressPredicate
     * @return An address of {@link Address}
     */
    public Address getAddress(Predicate<Address> addressPredicate) {
        List<Address> addresses = TestData.getObjects(EnumData.ADDRESSES);
        return addresses.stream().filter(addressPredicate).findAny().orElse(null);
    }
}