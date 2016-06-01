/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2016 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base;

import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClassFactoryImplTest {
  @Test
  public void getDescriptionForClass() throws Exception {

    final ClassFactoryImpl classFactory = new ClassFactoryImpl() {

    };
    assertNull( classFactory.getDescriptionForClass( this.getClass() ) );
    final ObjectDescription mock = mock( ObjectDescription.class );
    when( mock.getInstance() ).thenReturn( mock );
    classFactory.registerClass( this.getClass(), mock );
    assertEquals( mock, classFactory.getDescriptionForClass( this.getClass() ) );

  }

  @Test( expected = NullPointerException.class )
  public void getSuperClassObjectDescriptionNull() throws Exception {
    final ClassFactoryImpl classFactory = new ClassFactoryImpl() {

    };
    classFactory.getSuperClassObjectDescription( null, mock( ObjectDescription.class ) );
  }


  @Test
  public void getRegisteredClasses() throws Exception {
    final ClassFactoryImpl classFactory = new ClassFactoryImpl() {

    };
    final ObjectDescription mock = mock( ObjectDescription.class );
    classFactory.registerClass( this.getClass(), mock );
    classFactory.registerClass( String.class, mock );
    final Iterator iterator = classFactory.getRegisteredClasses();
    int i = 0;
    while ( iterator.hasNext() ) {
      iterator.next();
      i++;
    }
    assertEquals( i, 2 );
  }

  @Test
  public void testEqHc() {

    Map<String, ClassFactoryImpl> map = new HashMap<>();
    final ClassFactoryImpl classFactory1 = new ClassFactoryImpl() {

    };
    final ClassFactoryImpl classFactory2 = new ClassFactoryImpl() {

    };

    map.put( "first", classFactory1 );
    map.put( "second", classFactory2 );
    assertEquals( classFactory1, map.get( "first" ) );
    assertEquals( classFactory2, map.get( "second" ) );

  }


}
