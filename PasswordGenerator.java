import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;

public class PasswordGenerator {
    static String PasswordChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    static String UppercaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    static String LowercaseChars = "abcdefghijklmnopqrstuvwxyz";
    static String DigitChars = "0123456789";

    private int ReinitializeGeneratorAfter = 10;
    private int GeneratedPasswordsCount = 0;
    private SecureRandom sr;
    private int passwordLength = 10;
    private String passwordPolicy = "uulldd";

    public PasswordGenerator() {
    }

    public PasswordGenerator(int passwordLength, String passwordPolicy) {
        this.passwordLength = passwordLength;
        this.passwordPolicy = passwordPolicy;
    }


    private String getCharset(char charsetType) {
        switch (charsetType) {
        case 'u': return UppercaseChars;
        case 'l': return LowercaseChars;
        case 'd': return DigitChars;
        case '\u0000': return PasswordChars;
        default:
            throw new RuntimeException("Invalid program state");
        }
    }

    private char[] calculatePolicyPositions() {
        char[] fixedPolicyPositions = new char[passwordLength];
        StringBuffer passwordPolicy = new StringBuffer(this.passwordPolicy);
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
        if (sr==null || ++GeneratedPasswordsCount > ReinitializeGeneratorAfter) {
            GeneratedPasswordsCount = 0;
            sr = SecureRandom.getInstance("SHA1PRNG");
        }

        char[] fixedPolicyPositions = calculatePolicyPositions();

        StringBuffer password = new StringBuffer();
        for(int i = 0; i<passwordLength; i++) {
            String passwordChars = getCharset(fixedPolicyPositions[i]);
            password.append(passwordChars.charAt(sr.nextInt(passwordChars.length())));
        }
        return password.toString();
    }

    public static void main(String[] args) {
        try {
            PasswordGenerator pg = new PasswordGenerator();

            for(int i=0; i<100000; i++)
                System.out.println(pg.generate());
        }
        catch (NoSuchAlgorithmException e) {
            System.out.println("Shit");
            System.out.println(e);
        }
    }
}
