package pl.sk.photosharingservice.support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record ValidationUtil() {

    public static final  int PASSWORD_MIN_LENGTH = 5;
    public static final  int PROFILE_DESCRIPTION_MAX_LENGTH = 150;
    public static final  int IMAGE_DESCRIPTION_MAX_LENGTH = 2200;
    private static final String EMAIL_PATTERN = ".+@.+\\..+";
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{5,}$";

    public static boolean checkUsername(String username){
        if(username==null || username.length()==0)
            return false;
        return true;
    }

    public static boolean checkPassword(String password){
        if(password==null)
            return false;

        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();


    }

    public static boolean checkEmail(String email){
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);

        if(!matcher.matches()|| email.length()==0)
            return false;
        return true;
    }

}
