package org.pentaho.reporting.libraries.base.util;

import junit.framework.TestCase;

public class PasswordObscurificationTest extends TestCase
{
  public PasswordObscurificationTest()
  {
  }

  public PasswordObscurificationTest(final String name)
  {
    super(name);
  }

  public void testEncode() throws Exception
  {
    final String enc1 = PasswordObscurification.encryptPassword("test");
    assertNotNull(enc1);
    assertEquals("test", PasswordObscurification.decryptPassword(enc1));

    final StringBuilder b = new StringBuilder();
    for (int i = 0; i < 65535; i++)
    {
      if (i >= 0xD800 && i <= 0xDFFF)
      {
        // ignore surrogate space
        b.append(' ');
        continue;
      }
      if (i >= 0xE000 && i <= 0xF8FF)
      {
        // ignore private space 
        b.append(' ');
        continue;
      }
      b.append((char) i);
    }

    final String originalText = b.toString();
    final String enc2 = PasswordObscurification.encryptPassword(originalText);
    assertNotNull(enc2);

    final String decrypted = PasswordObscurification.decryptPassword(enc2);
    final char[] originalChars = originalText.toCharArray();
    final char[] decodedChars = decrypted.toCharArray();
    assertEquals(originalChars.length, decodedChars.length);
    for (int i = 0; i < decodedChars.length; i++)
    {
      final char decodedChar = decodedChars[i];
      final char orig = originalChars[i];
      assertEquals("i=" + Integer.toHexString(i), decodedChar, orig);
    }
  }
}
