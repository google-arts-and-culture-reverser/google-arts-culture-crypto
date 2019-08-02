package io.github.gacr;

import java.security.GeneralSecurityException;
import java.util.Arrays;

import java.net.MalformedURLException;

class Main {
  public static void main(String[] args) throws GeneralSecurityException, MalformedURLException {
    String url = "https://lh3.googleusercontent.com/wGcDNN8L-2COcm9toX5BTp6HPxpMPPPuxrMU-ZL-W-nDHW8I_L4R5vlBJ6ITtlmONQ";
    String token = "KwCgJ1QIfgprHn0a93x7Q-HhJ04";
    String tile_url = MicroscopeSecurity.computeTileToken(url, token, 0, 0, 7);

    System.out.println("Encrypted tile token: " + tile_url);

    byte[] input_buf = {
        // encrypted tile
        10, 10, 10, 10, // magic bytes
        1, 2, 3, 4, // unencrypted header
        16, 0, 0, 0, // encrypted data length
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, // encrypted data
        6, 7, 8, 9, // unencrypted footer
        4, 0, 0, 0 // size of unencrypted header
    };
    MicroscopeSecurity m = new MicroscopeSecurity();
    byte[] decrypted = m.maybeDecrypt(input_buf);

    System.out.println("Tile image bytes (decrypted): " + Arrays.toString(decrypted));
  }
}