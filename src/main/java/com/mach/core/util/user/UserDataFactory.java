package com.mach.core.util.user;

import com.mach.core.db.EmailVerificationServiceDAO;
import com.mach.core.db.PhoneVerificationServiceDAO;
import com.mach.core.model.User;
import com.mach.core.util.RandomValues;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static data.TestData.createNewPIN;
import static data.TestData.getPINAsString;

@Component
public class UserDataFactory {

    private UserDataValues userDataValues = new UserDataValues();
    private EmailValues emailValues = new EmailValues();
    private static final Logger LOG = LoggerFactory.getLogger(UserDataFactory.class);

    /**
     * Get a randomly-generated {@link User} which is not registered.
     *
     * @return An user of {@link User}
     */
    public User getRandomUser() {
        User user = new User();
        user.setName(getFirstNameRandom());
        user.setLastName(getLastNameRandom());
        user.setAccountRUT(RutValues.getValidRut());
        user.setAccountSerialNumber("A023907888");
        user.setEmail(getRandomEmail(user));
        user.setZoneCode("9");
        user.setCellNumber(getRandomCellphone());
        user.setPin(getPINAsString(createNewPIN()));
        return user;
    }

    /**
     * Get a randomly-generated {@link User} which is not registered with a validated email
     *
     * @return An user of {@link User}
     */
    public User getRandomValidatedUser() {
        User user = getRandomUser();
        user.setEmail((user.getName().charAt(0) + "." + user.getLastName().substring(0, 4) + "@automation-internal.soymach.com").toLowerCase());
        return user;
    }

    /**
     * @param user
     * @return An user email random of {@link String}
     */
    public String getRandomUserEmail(User user) {
        return new StringBuilder()
                .append(RandomStringUtils.random(8, Boolean.TRUE, Boolean.FALSE))
                .append(".")
                .append(user.getLastName())
                .append(getEmailDomainRandom())
                .toString()
                .toLowerCase();
    }

    /**
     * @return A random first name
     */
    private String getFirstNameRandom() {
        return getItem(userDataValues.getFirstNames());
    }

    /**
     * @return A random Last name
     */
    private String getLastNameRandom() {
        return getItem(userDataValues.getLastNames());
    }

    /**
     * @return A random first name
     */
    private String getEmailDomainRandom() {
        return getItem(emailValues.getEmailDominios());
    }

    /**
     * Returns a random item from an array of items
     *
     * @param <T>   Array item type and the type to return
     * @param items Array of items to choose from
     * @return Item from the array
     */
    private <T> T getItem(final T[] items) {
        return getItem(items, null);
    }

    /**
     * Returns a random item from an array of items or the defaultItem depending on the probability parameter. The
     * probability determines the chance (in %) of returning an item from the array versus the default value.
     *
     * @param <T>         Array item type and the type to return
     * @param items       Array of items to choose from
     * @param defaultItem value to return if the probability test fails
     * @return Item from the array or the default value
     */
    private <T> T getItem(final T[] items, final T defaultItem) {
        if (items == null || items.length == 0) {
            LOG.error("Item array cannot be null or empty, return defaultItem: {}", defaultItem);
            return defaultItem;
        }
        return items[RandomValues.getValue(items.length-1)];
    }

    /**
     * @return A random cell phone
     */
    public static String getRandomCellphone() {
        return getUniqueCellphone(String.valueOf(RandomValues.getValue((99999999 - 10000000) + 1) + 10000000));
    }

    private static String getUniqueCellphone(String randomCellphone) {
        PhoneVerificationServiceDAO phoneVerificationServiceDAO = new PhoneVerificationServiceDAO();
        try {
            if (phoneVerificationServiceDAO.isPhoneNumberExist(randomCellphone)) {
                randomCellphone = getUniqueCellphone(getRandomCellphone());
            }
        } catch (Exception e) {
            LOG.error("Error at getUniqueCellphone, e: ", e);
        }
        return randomCellphone;
    }

    public String getRandomEmail(User user) {
        return getUniqueEmail((user.getName().charAt(0) + "." + user.getLastName() + getEmailDomainRandom()).toLowerCase());
    }

    private String getUniqueEmail(String randomEmail) {
        EmailVerificationServiceDAO emailVerificationServiceDAO = new EmailVerificationServiceDAO();
        if (emailVerificationServiceDAO.isEmailExist(randomEmail)) {
            randomEmail = randomEmail.replace(".com", ".cl");
        }
        return randomEmail;
    }

}
