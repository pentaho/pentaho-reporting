package org.pentaho.reporting.libraries.parameter;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Arrays;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.parameter.defaults.DefaultListParameter;
import org.pentaho.reporting.libraries.parameter.defaults.DefaultParameterContext;
import org.pentaho.reporting.libraries.parameter.defaults.DefaultParameterDefinition;
import org.pentaho.reporting.libraries.parameter.defaults.DefaultPlainParameter;
import org.pentaho.reporting.libraries.parameter.defaults.ParameterParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class ParameterParseTest extends TestCase
{
  public ParameterParseTest()
  {
  }

  protected void setUp() throws Exception
  {
    LibParameterBoot.getInstance().start();
  }

  public void testWriteAndRead() throws Exception
  {
    final DefaultListParameter listParameter =
        new DefaultListParameter("test", String.class, "test-query", "key");
    listParameter.setAllowResetOnInvalidValue(true);
    listParameter.setAllowMultiSelection(false);
    listParameter.setStrictValueCheck(true);
    listParameter.setAutoSelectFirstValue(true);
    listParameter.setMandatory(true);
    listParameter.setParameterAttribute("duda", "daudau", "valueuua!");

    final DefaultPlainParameter plainParameter = new DefaultPlainParameter("plain", BigDecimal.class);
    plainParameter.setParameterAttribute("duda", "daudau", "valueuua!");

    final DefaultParameterDefinition def = new DefaultParameterDefinition();
    def.addParameter(listParameter);
    def.addParameter(plainParameter);

    final String xmlString = def.toXml();
    System.out.println(xmlString);

    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    final DocumentBuilder db = dbf.newDocumentBuilder();

    final Document doc = db.parse(new InputSource(new StringReader(xmlString)));
    final ParameterParser parser = new ParameterParser();
    final ParameterDefinition parameterDefinition = parser.parseDefinition(doc.getDocumentElement());

    final DefaultParameterContext context = new DefaultParameterContext();

    assertEquals(2, parameterDefinition.getParameterCount());
    final ListParameter parsedList = (ListParameter) parameterDefinition.getParameterDefinition(0);
    assertEquals(listParameter.getName(), parsedList.getName());
    assertEquals(listParameter.getValueType(), parsedList.getValueType());
    assertEquals(listParameter.getKeyColumn(), parsedList.getKeyColumn());
    assertEquals(listParameter.getTextColumn(), parsedList.getTextColumn());
    assertEquals(listParameter.getDefaultValue(), parsedList.getDefaultValue(context));
    assertAttributesSame(listParameter, parsedList);

    final Parameter parsedPlain = parameterDefinition.getParameterDefinition(1);
    assertEquals(plainParameter.getName(), parsedPlain.getName());
    assertEquals(plainParameter.getValueType(), parsedPlain.getValueType());
    assertEquals(plainParameter.getDefaultValue(), parsedPlain.getDefaultValue(context));
    assertAttributesSame(plainParameter, parsedPlain);
  }

  private void assertAttributesSame(final Parameter p1, final Parameter p2)
  {
    final String[] p1NS = p1.getParameterAttributeNamespaces();
    final String[] p2NS = p2.getParameterAttributeNamespaces();
    assertTrue(Arrays.equals(p1NS, p2NS));
    final DefaultParameterContext context = new DefaultParameterContext();

    for (int i = 0; i < p2NS.length; i++)
    {
      final String ns = p2NS[i];
      final String[] p1N = p1.getParameterAttributeNames(ns);
      final String[] p2N = p2.getParameterAttributeNames(ns);
      assertTrue(Arrays.equals(p1N, p2N));

      for (int j = 0; j < p2N.length; j++)
      {
        final String name = p2N[j];
        final Object o1 = p1.getParameterAttribute(ns, name, context);
        final Object o2 = p2.getParameterAttribute(ns, name, context);
        assertEquals(o1, o2);
      }
    }
  }
}
