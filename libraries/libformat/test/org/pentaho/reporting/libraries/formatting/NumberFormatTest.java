package org.pentaho.reporting.libraries.formatting;

import java.math.BigDecimal;
import java.util.Locale;

import junit.framework.TestCase;

public class NumberFormatTest extends TestCase
{
  public NumberFormatTest(final String name)
  {
    super(name);
  }

  public NumberFormatTest()
  {
  }

  public void testFormatting()
  {
    final FastDecimalFormat decimalFormat = new FastDecimalFormat("#,###", Locale.US);
    assertEquals("20", decimalFormat.format(new BigDecimal(19.937)));
    assertEquals("21", decimalFormat.format(new BigDecimal(20.999999999999999999999999999)));
    assertEquals("19", decimalFormat.format(new BigDecimal(19.0371)));
    assertEquals("19", decimalFormat.format(new BigDecimal(19.0375)));
    assertEquals("19", decimalFormat.format(new BigDecimal(19.0377)));

    assertEquals("-20", decimalFormat.format(new BigDecimal(-19.937)));
    assertEquals("-21", decimalFormat.format(new BigDecimal(-20.999999999999999999999999999)));
    assertEquals("-19", decimalFormat.format(new BigDecimal(-19.0371)));
    assertEquals("-19", decimalFormat.format(new BigDecimal(-19.0375)));
    assertEquals("-19", decimalFormat.format(new BigDecimal(-19.0377)));

    final FastDecimalFormat percentFormat = new FastDecimalFormat("####.00%", Locale.US);
    assertEquals("1993.70%", percentFormat.format(new BigDecimal(19.937)));
    assertEquals("2100.00%", percentFormat.format(new BigDecimal(20.999999999999999999999999999)));
    assertEquals("2099.99%", percentFormat.format(new BigDecimal(20.9999)));
    assertEquals("1903.71%", percentFormat.format(new BigDecimal(19.0371)));
    assertEquals("1903.75%", percentFormat.format(new BigDecimal(19.0375)));
    assertEquals("1903.77%", percentFormat.format(new BigDecimal(19.0377)));

    final FastDecimalFormat format = new FastDecimalFormat("####.00%", Locale.US);
    assertEquals("2099.99%", format.format(new BigDecimal(20.9999)));
    assertEquals("2100.00%", format.format(new BigDecimal(20.999999999)));

    final FastDecimalFormat fmt2 = new FastDecimalFormat("####.00", Locale.US);
    assertEquals("1234.56", fmt2.format(new BigDecimal(1234.564)));
    assertEquals("1234.57", fmt2.format(new BigDecimal(1234.565)));
    assertEquals("1234.57", fmt2.format(new BigDecimal(1234.566)));
  }

  public void testCritical()
  {
    final FastDecimalFormat fmt2 = new FastDecimalFormat("####.00", Locale.US);
    assertEquals("1234.57", fmt2.format(new BigDecimal(1234.565)));

  }

}
