package org.pentaho.reporting.libraries.base.boot;

import java.util.ArrayList;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.base.LibBaseBoot;

public class ObjectFactoryTest extends TestCase
{
  public void testSingletonCreation()
  {
    final ObjectFactory objectFactory = LibBaseBoot.getInstance().getObjectFactory();
    final ObjectFactorySingleton objectFactorySingleton1 = objectFactory.get(ObjectFactorySingleton.class);
    final ObjectFactorySingleton objectFactorySingleton2 = objectFactory.get(ObjectFactorySingleton.class);
    assertTrue(objectFactorySingleton1 == objectFactorySingleton2);
  }

  public void testNotSingletonCreation()
  {
    final ObjectFactory objectFactory = LibBaseBoot.getInstance().getObjectFactory();
    final ArrayList objectFactorySingleton1 = objectFactory.get(ArrayList.class);
    final ArrayList objectFactorySingleton2 = objectFactory.get(ArrayList.class);
    assertTrue(objectFactorySingleton1 != objectFactorySingleton2);
  }
}
