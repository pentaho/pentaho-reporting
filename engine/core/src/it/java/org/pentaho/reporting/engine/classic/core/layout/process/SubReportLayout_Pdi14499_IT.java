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


package org.pentaho.reporting.engine.classic.core.layout.process;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.pentaho.reporting.engine.classic.core.layout.process.LayoutValidationUtils.*;
import static org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility.toInternalValue;

/**
 * This test is dedicated to <a href="http://jira.pentaho.com/browse/PDI-14499">PDI-14499</a>. The tested report has
 * the following structure:<br/>
 * <ul>
 *  <li>Page header</li>
 *  <ul>
 *    <li>Group header, containing a label and a subreport</li>
 *    <ul>
 *      <li>Details header</li>
 *      <li>Details rows</li>
 *    </ul>
 *  </ul>
 *  <li>Page footer</li>
 * </ul>
 *
 * There are three pages, the first and the third contain one row, the second has nine.
 *
 * @author Andrey Khayrutdinov
 */
public class SubReportLayout_Pdi14499_IT {

  private static final String ROW_TEMPLATE = "This-is-an-example%d.";

  @BeforeClass
  public static void init() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void performLayout() throws Exception {
    final String file = "subreport-pdi-14499.prpt";
    List<LogicalPageBox> pages = loadPages( file, 3 );
    validateFirstPage( pages.get( 0 ) );
    validateSecondPage( pages.get( 1 ) );
    validateThirdPage( pages.get( 2 ) );
  }

  private void validateFirstPage( LogicalPageBox page ) {
    ExpectedContent expected = new ExpectedContent( "1.0", generateRows( 0, 0 ) );
    validatePage( page, expected );
  }

  private void validateSecondPage( LogicalPageBox page ) {
    ExpectedContent expected = new ExpectedContent( "2.0", generateRows( 1, 9 ) );
    validatePage( page, expected );
  }

  private void validateThirdPage( LogicalPageBox page ) {
    ExpectedContent expected = new ExpectedContent( "3.0", generateRows( 10, 10 ) );
    validatePage( page, expected );
  }


  private static List<String> generateRows( int start, int end ) {
    List<String> result = new ArrayList<>();
    for ( int i = start; i <= end; i++ ) {
      result.add( String.format( ROW_TEMPLATE, i ) );
    }
    return result;
  }

  private static void validatePage( LogicalPageBox page, ExpectedContent expected ) {
    assertPageHeader( page, "page-header", "Header", 15 );
    assertPageFooter( page, "page-footer", "Footer", 12 );

    final int groupHeaderY = 15;
    RenderNode groupHeader = findParagraph( page, "number-field", expected.groupHeader );
    assertEquals( toInternalValue( groupHeaderY ), groupHeader.getY() );

    final int detailsHeaderY = 15 + 78;
    RenderNode detailsHeader = findParagraph( page, "details-header", "Text" );
    assertEquals( toInternalValue( detailsHeaderY ), detailsHeader.getY() );

    validateRows( page, expected );
  }

  private static void validateRows( LogicalPageBox page, ExpectedContent expected ) {
    final int rowHeight = 18;
    final int firstRowY = 15 + 78 + 22;
    RenderNode[] rows = MatchFactory.findElementsByName( page, "details-row" );
    assertEquals( expected.rows.size() * 2, rows.length );

    int index = 0;
    int y = firstRowY;
    for ( String row : expected.rows ) {
      assertEquals( toInternalValue( y ), rows[ index++ ].getY() );
      assertEquals( row, ( (RenderableText) rows[ index++ ] ).getRawText() );
      y += rowHeight;
    }
  }

  private static class ExpectedContent {
    public final String groupHeader;
    public final List<String> rows;

    public ExpectedContent( String groupHeader, List<String> rows ) {
      this.groupHeader = groupHeader;
      this.rows = rows;
    }
  }
}
