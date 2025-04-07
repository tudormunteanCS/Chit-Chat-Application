package validators;

import domain.Utilizator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilizatorValidator implements Validator<Utilizator> {
    @Override
    public void validate(Utilizator entity) throws ValidationException {


        //TODO: implement method validate
        // Regex to check valid username.
        String regex = "^[A-Za-z]+$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the username is empty
        // return false
        if (entity.getFirstName() == null) {
            throw new ValidationException("Prenume invalid!\n");
        }
        if (entity.getLastName() == null) {
            throw new ValidationException("Nume invalid!\n");
        }
        if (entity.getEmail() == null) {
            throw new ValidationException("Email invalid!\n");
        }

        // Pattern class contains matcher() method
        // to find matching between given username
        // and regular expression.
        Matcher prenume = p.matcher(entity.getFirstName());
        Matcher nume = p.matcher(entity.getLastName());

        // Return if the username
        // matched the ReGex

        if (!nume.matches() | !prenume.matches()) {
            throw new ValidationException("Nume sau prenume invalid!\n");
        }


    }
}

