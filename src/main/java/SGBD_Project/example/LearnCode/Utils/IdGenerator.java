package SGBD_Project.example.LearnCode.Utils;

import java.security.SecureRandom;

public class IdGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int ID_LENGTH = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateId() {
        StringBuilder id = new StringBuilder(ID_LENGTH);
        for (int i = 0; i < ID_LENGTH; i++) {
            id.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return id.toString();
    }
}



//Using generated security password: 3c656dbf-5b74-4b99-a877-e0d6e93ef6fa
