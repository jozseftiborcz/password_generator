import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;

public class PasswordGenerator {
    private static String PASSWORD_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static String DIGIT_CHARS = "0123456789";

    // default password length. Can be overwrite in constructor
    private int passwordLength = 10;
    // default password policy: two uppercase, two lowercase two digits.
    private String passwordPolicy = "uulldd";
    private SecureRandom sr;

    /**
     * Constructor for the default password policy.
     */
    public PasswordGenerator() {
    }

    /**
     * Initialize password generator with policy settings.
     *
     * @param passwordLength how long should the password be
     * @param passwordPolicy string containing password policy. The policy is a string with policy control characters.
     *                       the following control characters can be used
     *                       <ul>
     *                       <li>u is for a uppercase character
     *                       <li>l is for a lowercase character
     *                       <li>d is for a digit
     *                       </ul>
     *                       The order doesn't matter but control character counts does. For example the following will 
     *                       all generate a password with one uppercase, three lowercase and one digit character: 
     *                       <ul>
     *                       <li><code>ullld</code>
     *                       <li><code>dlull</code>
     *                       </ul>
     *                       <p>
     *                       The rest of the password is filled from the general password charset (<code>[a-zA-Z0-9]</code>)
     */
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

    /**
     * Fix positions where policy mandated characters are placed. 
     */
    private char[] calculatePolicyPositions() {
        final char[] fixedPolicyPositions = new char[passwordLength];
        final StringBuilder passwordPolicy = new StringBuilder(this.passwordPolicy);
        int stepsToGo = this.passwordPolicy.length();

        while (stepsToGo>0) {
            int i = sr.nextInt(passwordLength);
            while (fixedPolicyPositions[i] != '\u0000') i = (i+1)%passwordLength;

            fixedPolicyPositions[i] = passwordPolicy.charAt(0);
            passwordPolicy.deleteCharAt(0);
            stepsToGo--;
        }
        return fixedPolicyPositions;
    }

    /**
     * Generate a password which conforms to password policy
     */
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

    /**
     * Test drive to print some password with the default policy.
     */
    public static void main(String[] args) {
        try {
            PasswordGenerator pg = new PasswordGenerator();

            for(int i=0; i<10000000; i++)
                System.out.println(pg.generate());
        }
        catch (NoSuchAlgorithmException e) {
            System.out.println("Shit");
            System.out.println(e);
        }
    }
}
