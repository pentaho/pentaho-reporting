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
 *  Copyright (c) 2006 - 2024 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.fast.xls;

import org.apache.poi.ss.usermodel.CellStyle;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackground;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.CellStyleProducer;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

import java.awt.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

/**
 * @author Andrey Khayrutdinov
 */
public class FastExcelCellStyleProducerTest implements Answer<CellStyle> {

  private CellStyleProducer backend;
  private StyleSheet element;
  private FastExcelCellStyleProducer producer;

  @Before
  public void setUp() {
    backend = mock( CellStyleProducer.class );
    when( backend.createCellStyle( Mockito.<InstanceID>any(), Mockito.<StyleSheet>any(), Mockito.<CellBackground>any() ) )
        .thenAnswer( this );

    element = mock( StyleSheet.class );
    producer = spy( new FastExcelCellStyleProducer( backend ) );
  }

  @Test
  public void caches_WhenSameElementId() {
    when( element.getId() ).thenReturn( null );

    InstanceID elementId = new InstanceID();

    CellStyle style = producer.createCellStyle( elementId, element, null );
    CellStyle style2 = producer.createCellStyle( elementId, element, null );

    assertCached( style, style2 );
  }

  @Test
  public void cachesByBackground_WhenElementIdIsNull() {
    when( element.getId() ).thenReturn( null );

    CellBackground bg = new CellBackground();

    CellStyle style = producer.createCellStyle( null, element, bg );
    CellStyle style2 = producer.createCellStyle( null, element, bg );

    assertCached( style, style2 );
  }

  @Test
  public void doesNotCache_WhenDifferentStyleId() {
    when( element.getId() ).thenReturn( new InstanceID() );

    StyleSheet element2 = mock( StyleSheet.class );
    when( element2.getId() ).thenReturn( new InstanceID() );

    CellBackground bg = new CellBackground();

    InstanceID elementId = new InstanceID();

    CellStyle style = producer.createCellStyle( elementId, element, bg );
    CellStyle style2 = producer.createCellStyle( elementId, element2, bg );

    assertNotCached( style, style2 );
  }

  @Test
  public void doesNotCache_WhenDifferentBackgrounds() {
    when( element.getId() ).thenReturn( new InstanceID() );

    CellBackground bg = new CellBackground();
    bg.setBottom( new BorderEdge( BorderStyle.DASHED, Color.BLACK, 1 ) );
    CellBackground bg2 = new CellBackground();
    bg2.setBottom( new BorderEdge( BorderStyle.DASHED, Color.BLACK, 2 ) );

    InstanceID elementId = new InstanceID();

    CellStyle style = producer.createCellStyle( elementId, element, bg );
    CellStyle style2 = producer.createCellStyle( elementId, element, bg2 );

    assertNotCached( style, style2 );
  }

  private void assertCached( final CellStyle style, final CellStyle style2 ) {
    assertTrue( style == style2 );
    verify( backend, only() ).createCellStyle( Mockito.<InstanceID>any( ), Mockito.<StyleSheet>any(),
            Mockito.<CellBackground>any() );
  }

  private void assertNotCached( final CellStyle style, final CellStyle style2 ) {
    assertFalse( style == style2 );
    verify( backend, atLeast( 2 ) ).createCellStyle( Mockito.<InstanceID>any(), Mockito.<StyleSheet>any(),
            Mockito.<CellBackground>any() );
  }

  @Override
  public CellStyle answer( final InvocationOnMock invocation ) throws Throwable {
    // ensure each time a new instance will be returned
    return mock( CellStyle.class );
  }
}
