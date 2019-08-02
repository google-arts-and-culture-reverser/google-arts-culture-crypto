package io.github.gacr;

import java.util.Base64;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/* renamed from: com.google.android.apps.cultural.ar.microscope.MicroscopeSecurity */
final class MicroscopeSecurity {
    private static final byte[] IV_CONTAINER = {7, 49, 57, 70, 73, 113, -25, 4, 5, 53, 58, 119, -117, -6, 111, -68, 48, 50, 27, -107, -110, ParameterInitDefType.DoubleVec2Init, ParameterInitDefType.DoubleVec3Init, 23, 49, 80, 23};
    private static final byte[] KEY_CONTAINER = {40, 55, 67, 80, 83, 91, 99, -37, ParameterInitDefType.DoubleInit, 59, 122, -13, -32, -79, 67, 85, 86, -56, -7, 83, 12, 4, 50, 72, 73, 85, 39};
    private static final byte[] TOKEN_IV = {123, 43, 78, 35, -34, 44, -59, -59};
    private final Cipher cipher;

    public MicroscopeSecurity() throws GeneralSecurityException {
        String str = "AES/CBC/NoPadding";
        this.cipher = Cipher.getInstance(str);
        this.cipher.init(2,
          new SecretKeySpec(KEY_CONTAINER, 5, 16, "AES"),
          new IvParameterSpec(IV_CONTAINER, 5, 16)
        );
    }

    public static String computeTileToken(String url, String token, int x, int y, int z)
            throws NoSuchAlgorithmException, InvalidKeyException, MalformedURLException {
        String algorithm_name = "HmacSHA1";
        StringBuilder sb = new StringBuilder(new URL(url).getPath().substring(1));
        sb.append("=x");
        sb.append(x);
        sb.append("-y");
        sb.append(y);
        sb.append("-z");
        sb.append(z);
        sb.append("-t");
        sb.append(token);
        try {
            String str_to_hash = sb.toString();
            Mac instance = Mac.getInstance(algorithm_name);
            instance.init(new SecretKeySpec(TOKEN_IV, algorithm_name));
            byte[] hash = instance.doFinal(str_to_hash.getBytes());
            String encodeToString = Base64.getEncoder().encodeToString(hash);
            return encodeToString
                    .substring(0, encodeToString.length() - 1)
                    .replace('+', '_')
                    .replace('/', '_')
                    .replace('-', '_');
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            return "";
        }
    }

    public final byte[] maybeDecrypt(byte[] input_buf) throws GeneralSecurityException {
        if (getInt(input_buf, 0) != 168430090) {
            return input_buf;
        }
        int header_size = getInt(input_buf, input_buf.length - 4);
        int encrypted_size_pos = header_size + 4;
        int encrypted_pos = encrypted_size_pos + 4;
        int encrypted_size = getInt(input_buf, encrypted_size_pos);
        int encrypted_end_pos = encrypted_pos + encrypted_size;
        int header_length = input_buf.length - 4 - encrypted_end_pos;
        int outputSize = header_size + this.cipher.getOutputSize(encrypted_size);
        byte[] output_buf = new byte[outputSize + header_length];
        System.arraycopy(input_buf, 4, output_buf, 0, header_size);
        this.cipher.doFinal(input_buf, encrypted_pos, encrypted_size, output_buf, header_size);
        System.arraycopy(input_buf, encrypted_end_pos, output_buf, outputSize, header_length);
        return output_buf;
    }

    private static int getInt(byte[] bArr, int i) {
        return ((bArr[i + 3] & 255) << 24) | (bArr[i] & 255) | ((bArr[i + 1] & 255) << 8) | ((bArr[i + 2] & 255) << ParameterInitDefType.ExternalSamplerInit);
    }
}
