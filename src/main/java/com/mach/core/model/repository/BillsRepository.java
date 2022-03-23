package com.mach.core.model.repository;

import com.mach.core.model.Bill;
import com.mach.core.util.EnumData;
import data.TestData;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class BillsRepository {

    public BillsRepository() {
        // empty
    }

    /**
     * Get a specific bill from the Bills.json file.
     *
     * @return A bill of {@link Bill}
     */
    public Bill getBill(Predicate<Bill> predicate) {
        List<Bill> bills = TestData.getObjects(EnumData.BILLS);
        Optional<Bill> optionalBill = bills.stream()
                .filter(predicate)
                .findAny();
        return optionalBill.orElse(null);
    }

    /**
     * Get a list of bills from the Bills.json file.
     *
     * @return A collection of bills of {@link List<Bill>}
     */
    public List<Bill> getBills(Predicate<Bill> predicate) {
        List<Bill> bills = TestData.getObjects(EnumData.BILLS);
        return bills.stream().filter(predicate).collect(Collectors.toList());
    }

    public Predicate<Bill> byDescription(String billDescription) {
        return bill -> bill.getDescription().equals(billDescription);
    }

    public Predicate<Bill> byIdentifier(String billIdentifier) {
        return bill -> bill.getIdentifier().equals(billIdentifier);
    }
}
