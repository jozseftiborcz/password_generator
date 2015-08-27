import java.security.Security;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

public class PasswordGenerator {
    private static String PASSWORD_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static String DIGIT_CHARS = "0123456789";

    private int passwordLength = 10;
    private String passwordPolicy = "uulldd";
    private SecureRandom sr;

    public PasswordGenerator() {
    }

    public PasswordGenerator(int passwordLength, String passwordPolicy) {
        this.passwordLength = passwordLength;
        this.passwordPolicy = passwordPolicy;
    }

    private String getCharset(char charsetType) {
        switch (charsetType) {
        case 'u': return UPPERCASE_CHARS;
        case 'l': return LOWERCASE_CHARS;
        case 'd': return DIGIT_CHARS;
        case '\u0000': return PASSWORD_CHARS;
        default:
            throw new IllegalArgumentException("Invalid program state");
        }
    }

    private char[] calculatePolicyPositions() {
        final char[] fixedPolicyPositions = new char[passwordLength];
        final StringBuilder passwordPolicy = new StringBuilder(this.passwordPolicy);
        int stepsToGo = this.passwordPolicy.length();
        while (stepsToGo>0) {
            int i = sr.nextInt(passwordLength);
            if (fixedPolicyPositions[i] == '\u0000') {
                fixedPolicyPositions[i] = passwordPolicy.charAt(0);
                passwordPolicy.deleteCharAt(0);
                stepsToGo--;
            }
        }
        return fixedPolicyPositions;
    }

    public String generate() throws NoSuchAlgorithmException {
        sr = SecureRandom.getInstance("SHA1PRNG");
        final char[] fixedPolicyPositions = calculatePolicyPositions();

        final StringBuilder password = new StringBuilder();
        for(int i = 0; i<passwordLength; i++) {
            String passwordChars = getCharset(fixedPolicyPositions[i]);
            password.append(passwordChars.charAt(sr.nextInt(passwordChars.length())));
        }
        return password.toString();
    }

    public static void main(String[] args) {
        try {
            PasswordGenerator pg = new PasswordGenerator();

            for(int i=0; i<1000; i++)
                System.out.println(pg.generate());
        }
        catch (NoSuchAlgorithmException e) {
            System.out.println("Shit");
            System.out.println(e);
        }
    }
}
