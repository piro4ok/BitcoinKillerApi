import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class SHA {
    /**
     * Хеш-функция с добавлением соли
     * @param message сообщение для хеширования
     * @return хеш сообщения с солью
     */
    protected String getHash(String message, int seed) {
        String hash = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(getSalt(seed));
            byte[] bytes = messageDigest.digest(message.getBytes());
            StringBuilder string = new StringBuilder();
            for (byte aByte : bytes) {
                string.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            hash = string.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }

    /**
     * Функция создания соли
     * @param seed "семечко" для генератора случайной последовательности
     * @return 16-байтовую соль
     */
    private byte[] getSalt(int seed) {
        Random random = new Random(seed);
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        SecureRandom secureRandom = new SecureRandom(bytes);
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return salt;
    }
}
