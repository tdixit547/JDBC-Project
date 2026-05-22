package util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Lightweight JWT helper using HMAC-SHA256. No external libraries.
 */
public class JwtHelper {

    private static final String SECRET = "my_temporary_development_secret_key";
    private static final String ALGORITHM = "HmacSHA256";

    private static String base64UrlEncode(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    private static byte[] base64UrlDecode(String s) {
        return Base64.getUrlDecoder().decode(s);
    }

    /**
     * Create a JWT token containing the user ID.
     */
    public static String createToken(int userId) {
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = "{\"id\":" + userId + "}";

        String headerB64 = base64UrlEncode(header.getBytes(StandardCharsets.UTF_8));
        String payloadB64 = base64UrlEncode(payload.getBytes(StandardCharsets.UTF_8));
        String data = headerB64 + "." + payloadB64;

        String signature = sign(data);
        return data + "." + signature;
    }

    /**
     * Verify and extract user ID from token. Returns -1 if invalid.
     */
    public static int verifyToken(String token) {
        if (token == null || token.isEmpty()) return -1;
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return -1;

            String data = parts[0] + "." + parts[1];
            String expectedSig = sign(data);

            if (!expectedSig.equals(parts[2])) return -1;

            String payload = new String(base64UrlDecode(parts[1]), StandardCharsets.UTF_8);
            // Extract "id" field from JSON
            int idStart = payload.indexOf("\"id\":");
            if (idStart == -1) return -1;
            String rest = payload.substring(idStart + 5).trim();
            int end = rest.length();
            for (int i = 0; i < rest.length(); i++) {
                char c = rest.charAt(i);
                if (c == ',' || c == '}') { end = i; break; }
            }
            return Integer.parseInt(rest.substring(0, end).trim());
        } catch (Exception e) {
            return -1;
        }
    }

    private static String sign(String data) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), ALGORITHM));
            return base64UrlEncode(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign JWT", e);
        }
    }
}
