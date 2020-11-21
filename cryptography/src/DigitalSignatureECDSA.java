import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class DigitalSignatureECDSA {
    private static final String SPEC = "secp256r1";
    private static final String ALGO = "SHA256withECDSA";

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        ECGenParameterSpec ecSpec = new ECGenParameterSpec(SPEC);
        KeyPairGenerator g = KeyPairGenerator.getInstance("EC");
        g.initialize(ecSpec, new SecureRandom());
        return g.generateKeyPair();
    }

    private JsonObject signMessage(String message, KeyPair keyPair) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, SignatureException {
        Signature ecdsaSign = Signature.getInstance(ALGO);
        ecdsaSign.initSign(keyPair.getPrivate());
        ecdsaSign.update(message.getBytes("UTF-8"));
        byte[] signature = ecdsaSign.sign();
        String pub = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String sig = Base64.getEncoder().encodeToString(signature);
        System.out.println(sig);
        System.out.println(pub);

        JsonObject obj = new JsonObject();
        obj.addProperty("publicKey", pub);
        obj.addProperty("signature", sig);
        obj.addProperty("message", message);
        obj.addProperty("algorithm", ALGO);
        return obj;
    }

    private boolean verifyMessage(JsonObject obj) throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException, InvalidKeyException, SignatureException {
        Signature ecdsaVerify = Signature.getInstance(obj.get("algorithm").getAsString());

        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(obj.get("publicKey").getAsString()));

        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        ecdsaVerify.initVerify(publicKey);
        ecdsaVerify.update(obj.get("message").getAsString().getBytes("UTF-8"));

        return ecdsaVerify.verify(Base64.getDecoder().decode(obj.get("signature").getAsString()));
    }

    /**
     * Тест для методов ECDSA: создание ключей, подписать сообщение, проверить подпись
     */
    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, SignatureException, InvalidKeySpecException {
        DigitalSignatureECDSA signatureECDSA = new DigitalSignatureECDSA();
        KeyPair keyPair = signatureECDSA.generateKeyPair();
        String message = "Hello there.";
        JsonObject obj = signatureECDSA.signMessage(message, keyPair);
        boolean verified = signatureECDSA.verifyMessage(obj);
        System.out.println(obj);
        System.out.println(verified);
    }
}

