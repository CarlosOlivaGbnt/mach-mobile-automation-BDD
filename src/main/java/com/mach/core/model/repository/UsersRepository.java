package com.mach.core.model.repository;

import com.mach.core.db.UsersMongoRepository;
import com.mach.core.exception.MachException;
import com.mach.core.model.User;
import com.mach.core.util.EnumIdentifiers;
import com.mach.core.util.user.UserDataFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UsersRepository {

    private static UsersRepository instance;

    private Map<String, User> userMap;

    private UserDataFactory userDataFactory;

    private UsersMongoRepository usersMongoRepository;

    private UsersRepository() {
        this.userMap = new HashMap<>();
        userDataFactory = new UserDataFactory();
        usersMongoRepository = UsersMongoRepository.getInstance();
    }

    public static synchronized UsersRepository getInstance() {
        if (instance == null) {
            instance = new UsersRepository();
        }
        return instance;
    }
    
    /**
     * Get a specific user
     *
     * @param userPredicate
     * @return An user of {@link User}
     */
    public User getUser(Predicate<User> userPredicate) {
        List<User> users = usersMongoRepository.findAll();
        return users.stream().filter(userPredicate).findAny().orElse(null);
    }

    public List<User> getUsers(List<String> names) {
        return names.stream()
                .map(name -> getStoredUserByName(name))
                .collect(Collectors.toList());
    }

    /**
     * @param userPredicate
     * @return An user of {@link User}
     */
    public synchronized User getStoredUser(Predicate<User> userPredicate) {
        User user = Optional.ofNullable(this.getUser(userPredicate)).orElseThrow(() -> new MachException("Invalid user."));
        if (userMap.containsKey(user.getAccountRUT())) {
            return userMap.get(user.getAccountRUT());
        } else {
            userMap.put(user.getAccountRUT(), user);
            return userMap.get(user.getAccountRUT());
        }
    }

    /**
     * Get the {@link User} indentified by userIdentifier, or create a new one according to identifier type.
     *
     * @param userIdentifier the identifier for the user, starting with one of {@link EnumIdentifiers}.
     * @return An user of {@link User}
     * @throws Exception if identifier is invalid or user data cannot be retrieved from file.
     */
    public synchronized User getStoredUser(String userIdentifier, String name) {
        if (userMap.containsKey(userIdentifier)) {
            return userMap.get(userIdentifier);
        }

        if (userIdentifier.startsWith(EnumIdentifiers.RANDOM_USER.getValue())) {
            userMap.put(userIdentifier, userDataFactory.getRandomUser());
            return userMap.get(userIdentifier);

        } else if (userIdentifier.startsWith(EnumIdentifiers.REGISTERED_USER.getValue())) {
            if (StringUtils.isEmpty(name)) {
                throw new MachException("Cannot get a registered user without providing its name.");
            }
            userMap.put(userIdentifier, getStoredUserByName(name));
            return userMap.get(userIdentifier);
        } else if (userIdentifier.startsWith(EnumIdentifiers.RANDOM_VALIDATED_USER.getValue())) {
            userMap.put(userIdentifier, userDataFactory.getRandomValidatedUser());
            return userMap.get(userIdentifier);

        }

        throw new MachException("Invalid user identifier.");
    }

   /*
        @param name
        return first user found by name
   */
    public User getStoredUserByName(String name){
        return  UsersMongoRepository.getInstance().findFirstByName(name);
    }

    /*
        @param name
        return first user found by rut
    */
    public User getStoredUserByRUT(String accountRUT){
        return UsersMongoRepository.getInstance().findFirstByAccountRUT(accountRUT);
    }

}