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


package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;

public class ExcelTableContentProducer extends TableContentProducer implements SheetPropertySource {
  private String pageHeaderCenter;
  private String pageFooterCenter;
  private String pageHeaderLeft;
  private String pageFooterLeft;
  private String pageHeaderRight;
  private String pageFooterRight;
  private Integer freezeTop;
  private Integer freezeLeft;

  public ExcelTableContentProducer( final SheetLayout sheetLayout, final OutputProcessorMetaData metaData ) {
    super( sheetLayout, metaData );
  }

  public void compute( final LogicalPageBox logicalPage, final boolean iterativeUpdate ) {
    this.pageFooterLeft = null;
    this.pageFooterCenter = null;
    this.pageFooterRight = null;
    this.pageHeaderLeft = null;
    this.pageHeaderCenter = null;
    this.pageHeaderRight = null;
    super.compute( logicalPage, iterativeUpdate );
  }

  public String getPageHeaderCenter() {
    return pageHeaderCenter;
  }

  public void setPageHeaderCenter( final String pageHeaderCenter ) {
    this.pageHeaderCenter = pageHeaderCenter;
  }

  public String getPageFooterCenter() {
    return pageFooterCenter;
  }

  public void setPageFooterCenter( final String pageFooterCenter ) {
    this.pageFooterCenter = pageFooterCenter;
  }

  public String getPageHeaderLeft() {
    return pageHeaderLeft;
  }

  public void setPageHeaderLeft( final String pageHeaderLeft ) {
    this.pageHeaderLeft = pageHeaderLeft;
  }

  public String getPageFooterLeft() {
    return pageFooterLeft;
  }

  public void setPageFooterLeft( final String pageFooterLeft ) {
    this.pageFooterLeft = pageFooterLeft;
  }

  public String getPageHeaderRight() {
    return pageHeaderRight;
  }

  public void setPageHeaderRight( final String pageHeaderRight ) {
    this.pageHeaderRight = pageHeaderRight;
  }

  public String getPageFooterRight() {
    return pageFooterRight;
  }

  public void setPageFooterRight( final String pageFooterRight ) {
    this.pageFooterRight = pageFooterRight;
  }

  protected void collectSheetStyleData( final RenderBox box ) {
    super.collectSheetStyleData( box );
    this.pageHeaderCenter = lookup( box, AttributeNames.Excel.PAGE_HEADER_CENTER, this.pageHeaderCenter );
    this.pageHeaderLeft = lookup( box, AttributeNames.Excel.PAGE_HEADER_LEFT, this.pageHeaderLeft );
    this.pageHeaderRight = lookup( box, AttributeNames.Excel.PAGE_HEADER_RIGHT, this.pageHeaderRight );
    this.pageFooterCenter = lookup( box, AttributeNames.Excel.PAGE_FOOTER_CENTER, this.pageFooterCenter );
    this.pageFooterLeft = lookup( box, AttributeNames.Excel.PAGE_FOOTER_LEFT, this.pageFooterLeft );
    this.pageFooterRight = lookup( box, AttributeNames.Excel.PAGE_FOOTER_RIGHT, this.pageFooterRight );

    final Integer freezeTop =
        (Integer) box.getAttributes().getAttribute( AttributeNames.Excel.NAMESPACE,
            AttributeNames.Excel.FREEZING_TOP_POSITION );
    if ( this.freezeTop == null && freezeTop != null ) {
      this.freezeTop = freezeTop;
    }

    final Integer freezeLeft =
        (Integer) box.getAttributes().getAttribute( AttributeNames.Excel.NAMESPACE,
            AttributeNames.Excel.FREEZING_LEFT_POSITION );
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

  private String lookup( final RenderBox box, final String attribute, final String defaultValue ) {
    final Object value = box.getAttributes().getAttribute( AttributeNames.Excel.NAMESPACE, attribute );
    if ( value != null && defaultValue == null ) {
      return String.valueOf( value );
    }
    return defaultValue;
  }
}
