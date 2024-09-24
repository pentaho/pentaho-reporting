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
