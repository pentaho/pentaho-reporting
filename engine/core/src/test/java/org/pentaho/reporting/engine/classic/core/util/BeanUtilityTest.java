/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.util;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;

import java.awt.*;
import java.beans.IntrospectionException;
import java.lang.reflect.Array;

public class BeanUtilityTest extends TestCase {
  public BeanUtilityTest() {
  }

  public BeanUtilityTest( final String tagName ) {
    super( tagName );
  }

  public void testSimpleProperties() throws IntrospectionException, BeanException {
    final BeanUtility bu = new BeanUtility( new TestBean() );
    validateProperty( bu, "simpleColor", Color.red, null );
    validateProperty( bu, "simpleString", "Hello World", null );
    validateProperty( bu, "simpleBool", Boolean.TRUE, Boolean.FALSE );
    validateProperty( bu, "simpleDouble", new Double( 100 ), new Double( 0 ) );
    validateProperty( bu, "arrayOnly", new String[] { "test" }, new String[0] );
    validateProperty( bu, "fullyIndexed", new String[] { "test" }, new String[0] );
  }

  public void testIndexedProperties() throws IntrospectionException, BeanException {
    final TestBean testBean = new TestBean();
    testBean.setArrayOnly( new String[1] );
    testBean.setFullyIndexed( new String[1] );
    testBean.setIndexOnly( 0, null );
    final BeanUtility bu = new BeanUtility( testBean );
    validateProperty( bu, "arrayOnly[0]", "Color.red", null );
    validateProperty( bu, "fullyIndexed[0]", "Hello World", null );
    validateProperty( bu, "indexOnly[0]", "Boolean.TRUE", null );
  }

  private void validateProperty( final BeanUtility bu, final String name, final Object value, final Object nullValue )
    throws BeanException {
    bu.setProperty( name, nullValue );
    assertValue( nullValue, bu.getProperty( name ) );

    bu.setProperty( name, value );
    assertValue( value, bu.getProperty( name ) );
    final String valString = bu.getPropertyAsString( name );
    assertNotNull( valString );

    bu.setProperty( name, nullValue );
    assertValue( nullValue, bu.getProperty( name ) );

    bu.setPropertyAsString( name, valString );
    assertValue( value, bu.getProperty( name ) );

    bu.setProperty( name, nullValue );
    assertValue( nullValue, bu.getProperty( name ) );
  }

  private void assertValue( final Object original, final Object comp ) {
    if ( original == comp ) {
      return;
    }

    if ( original == null ) {
      throw new AssertionError( "Original Null, but comp isnot" );
    }
    if ( comp == null ) {
      throw new AssertionError( "Original not null, but comp is" );
    }

    if ( original.getClass().isArray() ) {
      if ( comp.getClass().isArray() ) {
        final int l1 = Array.getLength( original );
        final int l2 = Array.getLength( comp );
        if ( l1 != l2 ) {
          throw new AssertionError( "Comp.length != Org.length" );
        }
        for ( int i = 0; i < l1; i++ ) {
          final Object o1 = Array.get( original, i );
          final Object o2 = Array.get( comp, i );

          assertValue( o1, o2 );
        }
      } else {
        throw new AssertionError( "Comp is no array" );
      }
    } else {
      if ( comp.getClass().isArray() ) {
        throw new AssertionError( "Original is no array" );
      } else {
        assertEquals( original, comp );
      }
    }

  }
}
