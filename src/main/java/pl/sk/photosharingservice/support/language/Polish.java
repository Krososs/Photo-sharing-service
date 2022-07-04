package pl.sk.photosharingservice.support.language;

import pl.sk.photosharingservice.support.ValidationUtil;
import pl.sk.photosharingservice.support.ResponseUtil;



public class Polish implements Language {
    @Override
    public String translate(ResponseUtil response) {

        return switch (response) {
            case WRONG_PASSWORD -> "Wprowadzono niepoprawne hasło";
            case WRONG_USERNAME -> "Wprowadzono niepoprawną nazwę użytkownika";
            case PASSWORD_CONTAINS_NAME -> "Hasło nie może zawierać części Twojej nazwy";
            case WRONG_CREDENTIALS -> "Wprowadzono złe dane. Sprawdź swoją nazwę użytkownika i hasło";
            case USERNAME_TAKEN -> "Nazwa użytkownika jest już zajęta";
            case PASSWORD_TOO_WEAK -> "Wprowadzone hasło jest zbyt słabe. Poprawne hasło powinno zawierać conajmniej jedną cyfrę, conajmniej jedną wielką literę oraz conajmniej jedną małą literę";
            case WRONG_EMAIL -> "Wprowadzony email jest nieprawidłowy";
            case EMAIL_TAKEN -> "Wprowadzony email jest zajęty";
            case USER_IS_NOT_RIGHT_OWNER -> "Użytkownik nie jest właścicielem zdjęcia";
            case IMAGE_SIZE_TOO_LARGE -> "Rozmiar zdjęcia jest zbyt duży";
            case IMAGE_DOES_NOT_EXISTS -> "Zdjęcie nie istnieje";
            case IMAGE_DESCRIPTION_TOO_LONG -> "Opis może zawierać maksymalnie "+ ValidationUtil.IMAGE_DESCRIPTION_MAX_LENGTH +" znaków";
            case PROFILE_DESCRIPTION_TOO_LONG -> "Opis może zawierać maksymalnie "+ ValidationUtil.PROFILE_DESCRIPTION_MAX_LENGTH +" znaków";
            case PASSWORDS_DO_NOT_MATCH -> "Hasła nie są identyczne";
            case PASSWORD_TOO_SHORT -> "Wprowadzone hasło jest zbyt krótkie. Prawidłowe hasło powinno zawierać więcej niż " + ValidationUtil.PASSWORD_MIN_LENGTH + " znaków";
        };
    }


}
