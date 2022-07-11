package pl.sk.photosharingservice.support.language;

import pl.sk.photosharingservice.support.ValidationUtil;
import pl.sk.photosharingservice.support.ResponseUtil;

public class English implements Language {
    @Override
    public String translate(ResponseUtil response) {

        return switch (response) {
            case WRONG_PASSWORD -> "Incorrect password";
            case WRONG_USERNAME -> "Incorrect username";
            case PASSWORD_CONTAINS_NAME -> "Password cannot contain part of your username";
            case WRONG_CREDENTIALS -> "Wrong credentials. Please check your username and password";
            case USERNAME_TAKEN -> "Username is already taken";
            case PASSWORD_TOO_WEAK -> "Entered password is too weak. Correct password should contain at least one uppercase letter, at least one lowercase letter and at least one number";
            case WRONG_EMAIL -> "Entered email is invalid";
            case EMAIL_TAKEN -> "Entered email is already taken";
            case IMAGE_SIZE_TOO_LARGE -> "Image size is too large";
            case USER_IS_NOT_RIGHT_OWNER -> "User is not the right owner";
            case USER_DOES_NOT_EXISTS -> "User does not exists";
            case IMAGE_DOES_NOT_EXISTS -> "Image does not exists";
            case IMAGE_DESCRIPTION_TOO_LONG -> "Description can contain up to " + ValidationUtil.IMAGE_DESCRIPTION_MAX_LENGTH + " characters";
            case PROFILE_DESCRIPTION_TOO_LONG -> "Description can contain up to " + ValidationUtil.PROFILE_DESCRIPTION_MAX_LENGTH + " characters";
            case PASSWORDS_DO_NOT_MATCH -> "Passwords do not match";
            case PASSWORD_TOO_SHORT -> "Entered password is to short. A valid password should contain more than " + ValidationUtil.PASSWORD_MIN_LENGTH + " characters";
        };
    }
}
