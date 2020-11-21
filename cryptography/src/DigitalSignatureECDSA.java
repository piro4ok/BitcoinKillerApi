import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class DigitalSignatureECDSA {
    private static final String SPEC = "secp256r1";
    private static final String ALGO = "SHA256withECDSA";

    /**
     * Функция генерации пары ключей - открытого и закрытого
     * @return пару ключей в одном объекте
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     */
    private KeyPair generateKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        ECGenParameterSpec ecSpec = new ECGenParameterSpec(SPEC);
        KeyPairGenerator g = KeyPairGenerator.getInstance("EC");
        g.initialize(ecSpec, new SecureRandom());
        return g.generateKeyPair();
    }

    /**
     * Функция подписи сообщения
     * @param message сообщение, которое нужно подписать
     * @param keyPair пара ключей - открытый и закрытый
     * @return JSON объект содержащий публичный ключ, подпись, сообщение, алгоритм
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    private JsonObject signMessage(String message, KeyPair keyPair) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature ecdsaSign = Signature.getInstance(ALGO);
        ecdsaSign.initSign(keyPair.getPrivate());
        ecdsaSign.update(message.getBytes(StandardCharsets.UTF_8));
        byte[] signature = ecdsaSign.sign();
        String pub = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String sig = Base64.getEncoder().encodeToString(signature);
        //System.out.println(sig);
        //System.out.println(pub);

        JsonObject obj = new JsonObject();
        obj.addProperty("publicKey", pub);
        obj.addProperty("signature", sig);
        obj.addProperty("message", message);
        return obj;
    }

    /**
     * Функция проверки подлинности подписи
     * @param obj JSON объект содержащий публичный ключ, подпись, сообщение
     * @return true или false подтверждая или опровергая подпись
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    private boolean verifyMessage(JsonObject obj) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        Signature ecdsaVerify = Signature.getInstance(ALGO);

        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(obj.get("publicKey").getAsString()));

        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        ecdsaVerify.initVerify(publicKey);
        ecdsaVerify.update(obj.get("message").getAsString().getBytes(StandardCharsets.UTF_8));

        return ecdsaVerify.verify(Base64.getDecoder().decode(obj.get("signature").getAsString()));
    }
}

