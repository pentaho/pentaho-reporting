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
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */


package org.pentaho.reporting.engine.classic.core.modules.output.fast.xls;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.testng.PowerMockTestCase;

import org.junit.runner.RunWith;

// import org.testng.annotations.Test;
// import org.testng.annotations.BeforeTest;
// import org.testng.annotations.AfterTest;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.BandDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Luis Martins
 */
@RunWith( PowerMockRunner.class )
@PrepareForTest( { SimpleStyleSheet.class, StyleKey.class, BandDefaultStyleSheet.class } )
public class FastExcelPrinterTest extends PowerMockTestCase {

  private FastExcelPrinter mockFastExcelPrinter;

  @Before
  public void setUp() {
    mockFastExcelPrinter = PowerMockito.mock( FastExcelPrinter.class );
  }

  @After
  public void cleanup() {
  }

  @Test
  public void getValueIfVisible_NotAnElement() {
    ReportElement mockElement = PowerMockito.mock( ReportElement.class );

    when( mockFastExcelPrinter.getValueIfVisible( mockElement, "X" ) ).thenCallRealMethod();

    assertEquals( "X", mockFastExcelPrinter.getValueIfVisible( mockElement, "X" ) );
  }

  @Test
  public void getValueIfVisible_True() {
    StyleKey[] keys = new StyleKey[] { StyleKey.getStyleKey( "string", String.class ) };
    List<StyleKey> styleKeys = Collections.unmodifiableList( Arrays.asList( keys ) );

    PowerMockito.mockStatic( BandDefaultStyleSheet.class );
    when( BandDefaultStyleSheet.getBandDefaultStyle() ).thenReturn( PowerMockito.mock( BandDefaultStyleSheet.class ) );

    Element mockElement = PowerMockito.mock( Element.class );
    SimpleStyleSheet mockSimpleStyleSheet = PowerMockito.mock( SimpleStyleSheet.class );

    doReturn( mockSimpleStyleSheet ).when( mockElement ).getComputedStyle();
    doReturn( true ).when( mockSimpleStyleSheet ).getStyleProperty( ElementStyleKeys.VISIBLE, true );

    when( mockFastExcelPrinter.getValueIfVisible( mockElement, "X" ) ).thenCallRealMethod();

    assertEquals( "X", mockFastExcelPrinter.getValueIfVisible( mockElement, "X" ) );
  }

  @Test
  public void getValueIfVisible_False() {
    StyleKey[] keys = new StyleKey[] { StyleKey.getStyleKey( "string", String.class ) };
    List<StyleKey> styleKeys = Collections.unmodifiableList( Arrays.asList( keys ) );

    PowerMockito.mockStatic( BandDefaultStyleSheet.class );
    when( BandDefaultStyleSheet.getBandDefaultStyle() ).thenReturn( PowerMockito.mock( BandDefaultStyleSheet.class ) );

    Element mockElement = PowerMockito.mock( Element.class );
    SimpleStyleSheet mockSimpleStyleSheet = PowerMockito.mock( SimpleStyleSheet.class );

    doReturn( mockSimpleStyleSheet ).when( mockElement ).getComputedStyle();
    doReturn( false ).when( mockSimpleStyleSheet ).getStyleProperty( ElementStyleKeys.VISIBLE, true );

    when( mockFastExcelPrinter.getValueIfVisible( mockElement, "X" ) ).thenCallRealMethod();

    assertEquals( null, mockFastExcelPrinter.getValueIfVisible( mockElement, "X" ) );
  }
}
