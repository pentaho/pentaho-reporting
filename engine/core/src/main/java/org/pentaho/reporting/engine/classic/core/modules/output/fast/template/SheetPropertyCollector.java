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


package org.pentaho.reporting.engine.classic.core.modules.output.fast.template;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.SheetPropertySource;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.AbstractStructureVisitor;

public class SheetPropertyCollector extends AbstractStructureVisitor implements SheetPropertySource {
  private String sheetName;
  private String pageHeaderCenter;
  private String pageFooterCenter;
  private String pageHeaderLeft;
  private String pageFooterLeft;
  private String pageHeaderRight;
  private String pageFooterRight;
  private Integer freezeTop;
  private Integer freezeLeft;

  public SheetPropertyCollector() {
  }

  public String compute( Band band ) {
    sheetName = null;
    inspectElement( band );
    traverseSection( band );
    if ( sheetName == null ) {
      Section parentSection = band.getParentSection();
      while ( parentSection != null ) {
        inspectElement( parentSection );
        parentSection = parentSection.getParentSection();
      }
    }
    return sheetName;
  }

  protected void traverseSection( final Section section ) {
    traverseSectionWithoutSubReports( section );
  }

  protected void inspectElement( final ReportElement element ) {
    if ( sheetName != null ) {
      return;
    }

    Object styleProperty = element.getComputedStyle().getStyleProperty( BandStyleKeys.COMPUTED_SHEETNAME );
    if ( styleProperty != null ) {
      sheetName = String.valueOf( styleProperty );
    }

    this.pageHeaderCenter = lookup( element, AttributeNames.Excel.PAGE_HEADER_CENTER, this.pageHeaderCenter );
    this.pageHeaderLeft = lookup( element, AttributeNames.Excel.PAGE_HEADER_LEFT, this.pageHeaderLeft );
    this.pageHeaderRight = lookup( element, AttributeNames.Excel.PAGE_HEADER_RIGHT, this.pageHeaderRight );
    this.pageFooterCenter = lookup( element, AttributeNames.Excel.PAGE_FOOTER_CENTER, this.pageFooterCenter );
    this.pageFooterLeft = lookup( element, AttributeNames.Excel.PAGE_FOOTER_LEFT, this.pageFooterLeft );
    this.pageFooterRight = lookup( element, AttributeNames.Excel.PAGE_FOOTER_RIGHT, this.pageFooterRight );

    final Integer freezeTop =
        (Integer) element.getAttribute( AttributeNames.Excel.NAMESPACE, AttributeNames.Excel.FREEZING_TOP_POSITION );
    if ( this.freezeTop == null && freezeTop != null ) {
      this.freezeTop = freezeTop;
    }

    final Integer freezeLeft =
        (Integer) element.getAttribute( AttributeNames.Excel.NAMESPACE, AttributeNames.Excel.FREEZING_LEFT_POSITION );
    if ( this.freezeLeft == null && freezeLeft != null ) {
      this.freezeLeft = freezeLeft;
    }
  }

  public int getFreezeTop() {
    if ( freezeTop == null ) {
      return 0;
    }
    return freezeTop;
  }

  public int getFreezeLeft() {
    if ( freezeLeft == null ) {
      return 0;
    }
    return freezeLeft;
  }

  public String getPageHeaderCenter() {
    return pageHeaderCenter;
  }

  public String getPageFooterCenter() {
    return pageFooterCenter;
  }

  public String getPageHeaderLeft() {
    return pageHeaderLeft;
  }

  public String getPageFooterLeft() {
    return pageFooterLeft;
  }

  public String getPageHeaderRight() {
    return pageHeaderRight;
  }

  public String getPageFooterRight() {
    return pageFooterRight;
  }

  private String lookup( final ReportElement box, final String attribute, final String defaultValue ) {
    final Object value = box.getAttribute( AttributeNames.Excel.NAMESPACE, attribute );
    if ( value != null && defaultValue == null ) {
      return String.valueOf( value );
    }
    return defaultValue;
  }
}
