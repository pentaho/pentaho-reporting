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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import org.junit.runner.RunWith;

// import org.testng.annotations.Test;
// import org.testng.annotations.BeforeTest;
// import org.testng.annotations.AfterTest;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import org.mockito.MockedStatic;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.BandDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

/**
 * @author Luis Martins
 */
@RunWith( MockitoJUnitRunner.class )
public class FastExcelPrinterTest {

  private FastExcelPrinter mockFastExcelPrinter;

  @Before
  public void setUp() {
    mockFastExcelPrinter = mock( FastExcelPrinter.class );
  }

  @After
  public void cleanup() {
  }

  @Test
  public void getValueIfVisible_NotAnElement() {
    ReportElement mockElement = mock( ReportElement.class );

    when( mockFastExcelPrinter.getValueIfVisible( mockElement, "X" ) ).thenCallRealMethod();

    assertEquals( "X", mockFastExcelPrinter.getValueIfVisible( mockElement, "X" ) );
  }

  @Test
  public void getValueIfVisible_True() {
    try ( MockedStatic<BandDefaultStyleSheet> mockedStatic = mockStatic( BandDefaultStyleSheet.class ) ) {
      mockedStatic.when( () -> BandDefaultStyleSheet.getBandDefaultStyle() ).thenReturn( mock( BandDefaultStyleSheet.class ) );

      Element mockElement = mock( Element.class );
      SimpleStyleSheet mockSimpleStyleSheet = mock( SimpleStyleSheet.class );

      doReturn( mockSimpleStyleSheet ).when( mockElement ).getComputedStyle();
      doReturn( true ).when( mockSimpleStyleSheet ).getStyleProperty( ElementStyleKeys.VISIBLE, true );

      when( mockFastExcelPrinter.getValueIfVisible( mockElement, "X" ) ).thenCallRealMethod();

      assertEquals( "X", mockFastExcelPrinter.getValueIfVisible( mockElement, "X" ) );
    }
  }

  @Test
  public void getValueIfVisible_False() {
    try ( MockedStatic<BandDefaultStyleSheet> mockedStatic = mockStatic( BandDefaultStyleSheet.class ) ) {
      mockedStatic.when( () -> BandDefaultStyleSheet.getBandDefaultStyle() ).thenReturn( mock(BandDefaultStyleSheet.class ) );

      Element mockElement = mock( Element.class );
      SimpleStyleSheet mockSimpleStyleSheet = mock( SimpleStyleSheet.class );

      doReturn( mockSimpleStyleSheet ).when( mockElement ).getComputedStyle();
      doReturn( false ).when( mockSimpleStyleSheet ).getStyleProperty( ElementStyleKeys.VISIBLE, true );

      when( mockFastExcelPrinter.getValueIfVisible( mockElement, "X" ) ).thenCallRealMethod();

      assertEquals( null, mockFastExcelPrinter.getValueIfVisible(mockElement, "X" ) );
    }
  }
}
