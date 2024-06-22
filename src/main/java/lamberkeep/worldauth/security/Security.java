package lamberkeep.worldauth.security;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Security class.
 *
 * <p>Provides password encryption methods.
 */
public class Security {

  // Private constructor to prevent instantiation
  private Security() {
    throw new UnsupportedOperationException();
  }

  /**
   * Password encryption methods. Uses server's MAC address as salt.
   *
   * @param password unsecured password string.
   * @return encrypted password.
   */
  public static String hashPassword(String password)
      throws NoSuchAlgorithmException, SocketException, UnknownHostException {
    if (password == null) {
      return null;
    }

    MessageDigest md = MessageDigest.getInstance("SHA-256");
    md.update(getMacAddress());
    byte[] hashedPassword = md.digest(password.getBytes());

    return bytesToHex(hashedPassword);
  }

  private static String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();

    for (byte b : bytes) {
      sb.append(String.format("%02x", b));
    }

    return sb.toString();
  }

  public static byte[] getMacAddress() throws SocketException, UnknownHostException {
    return NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
  }

  public static boolean checkPassword(String password, String hashedPassword)
      throws NoSuchAlgorithmException, SocketException, UnknownHostException {

    String inputHashedPassword = hashPassword(password);

    return !hashedPassword.equals(inputHashedPassword);
  }
}