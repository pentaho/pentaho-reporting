package org.pentaho.reporting.libraries.xmlns.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.xmlns.LibXmlBoot;

public class AttributeMapTest extends TestCase
{
  public AttributeMapTest()
  {
    super();
  }

  public AttributeMapTest(final String name)
  {
    super(name);
  }

  protected void setUp() throws Exception
  {
    LibXmlBoot.getInstance().start();
  }

  public void testNamespaceOrder()
  {
    final AttributeMap<String> e = new AttributeMap<String>();
    e.setAttribute("Namespace1", "Attr1", "value1");
    e.setAttribute("Namespace1", "Attr2", "value1");
    e.setAttribute("Namespace1", "Attr3", "value1");
    e.setAttribute("Namespace2", "Attr1", "value1");
    e.setAttribute("Namespace2", "Attr2", "value1");
    e.setAttribute("Namespace2", "Attr3", "value1");
    e.setAttribute("Namespace3", "Attr1", "value1");

    final AttributeMap<String> e2 = new AttributeMap<String>(e);
    final String[] attributeNamespaces = e2.getNameSpaces();
    assertEquals(Arrays.asList(attributeNamespaces), Arrays.asList(e.getNameSpaces()));
    for (int i = 0; i < attributeNamespaces.length; i++)
    {
      final String namespace = attributeNamespaces[i];
      final String[] names = e2.getNames(namespace);
      assertEquals(Arrays.asList(names), Arrays.asList(e.getNames(namespace)));
    }
  }

  public void testNamespaceOrderClone()
  {
    final AttributeMap<String> e = new AttributeMap<String>();
    e.setAttribute("Namespace1", "Attr1", "value1");
    e.setAttribute("Namespace1", "Attr2", "value1");
    e.setAttribute("Namespace1", "Attr3", "value1");
    e.setAttribute("Namespace2", "Attr1", "value1");
    e.setAttribute("Namespace2", "Attr2", "value1");
    e.setAttribute("Namespace2", "Attr3", "value1");
    e.setAttribute("Namespace3", "Attr1", "value1");

    final AttributeMap<String> e2 = (AttributeMap<String>) (e.clone());
    final String[] attributeNamespaces = e2.getNameSpaces();
    assertEquals(Arrays.asList(attributeNamespaces), Arrays.asList(e.getNameSpaces()));
    for (int i = 0; i < attributeNamespaces.length; i++)
    {
      final String namespace = attributeNamespaces[i];
      final String[] names = e2.getNames(namespace);
      assertEquals(Arrays.asList(names), Arrays.asList(e.getNames(namespace)));
    }
  }


  public void testNamespaceOrderSerialize() throws ClassNotFoundException, IOException
  {
    final AttributeMap<String> e = new AttributeMap<String>();
    e.setAttribute("Namespace1", "Attr1", "value1");
    e.setAttribute("Namespace1", "Attr2", "value1");
    e.setAttribute("Namespace1", "Attr3", "value1");
    e.setAttribute("Namespace2", "Attr1", "value1");
    e.setAttribute("Namespace2", "Attr2", "value1");
    e.setAttribute("Namespace2", "Attr3", "value1");
    e.setAttribute("Namespace3", "Attr1", "value1");

    final AttributeMap<String> e2 = serializeAndDeserialize(e);
    final String[] attributeNamespaces = e2.getNameSpaces();
    assertEquals(Arrays.asList(attributeNamespaces), Arrays.asList(e.getNameSpaces()));
    for (int i = 0; i < attributeNamespaces.length; i++)
    {
      final String namespace = attributeNamespaces[i];
      final String[] names = e2.getNames(namespace);
      assertEquals(Arrays.asList(names), Arrays.asList(e.getNames(namespace)));
    }
  }

  private <T> AttributeMap<T> serializeAndDeserialize(final AttributeMap<T> e) throws IOException, ClassNotFoundException
  {
    final ByteArrayOutputStream bo = new ByteArrayOutputStream();
    final ObjectOutputStream out = new ObjectOutputStream(bo);
    out.writeObject(e);

    final ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(bo.toByteArray()));
    return (AttributeMap<T>) oin.readObject();
  }

}
