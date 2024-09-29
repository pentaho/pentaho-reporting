/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/
package org.pentaho.reporting.libraries.base.boot;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.libraries.base.LibBaseBoot;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class ObjectFactoryTest {

  private ObjectFactory objectFactory;

  @Before
  public void setUp() {
    objectFactory = LibBaseBoot.getInstance().getObjectFactory();
  }

  @Test
  public void testSingletonCreation() {
    ObjectFactorySingleton objectFactorySingleton1 = objectFactory.get( ObjectFactorySingleton.class );
    ObjectFactorySingleton objectFactorySingleton2 = objectFactory.get( ObjectFactorySingleton.class );
    assertTrue( objectFactorySingleton1 == objectFactorySingleton2 );
  }

  @Test
  public void testNotSingletonCreation() {
    ArrayList objectFactorySingleton1 = objectFactory.get( ArrayList.class );
    ArrayList objectFactorySingleton2 = objectFactory.get( ArrayList.class );
    assertTrue( objectFactorySingleton1 != objectFactorySingleton2 );
  }


  @Test( expected = ObjectFactoryException.class )
  public void throwsException_WhenCannotCast_SingletonToList() {
    objectFactory.get( ObjectFactorySingleton.class, ArrayList.class.getName() );
  }
}
