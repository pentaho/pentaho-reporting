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


package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;

/**
 * Hides the page header and footer if the export type is not pageable. Repeated groupheaders can be disabled by this
 * function as well.
 *
 * @author Thomas Morgner
 * @deprecated Use style expressions instead.
 */
public class HidePageBandForTableExportFunction extends AbstractFunction implements LayoutProcessorFunction {
  /**
   * A flag indicating whether page bands should be hidden.
   */
  private boolean hidePageBands;
  /**
   * A flag indicating whether repeating group header and footer should made non-repeating.
   */
  private boolean disableRepeatingHeader;

  private String exportDescriptor;

  /**
   * Default Constructor.
   */
  public HidePageBandForTableExportFunction() {
    hidePageBands = true;
    exportDescriptor = "table/";
  }

  /**
   * Applies the defined flags to the report.
   *
   * @param event
   *          the report event.
   */
  public void reportInitialized( final ReportEvent event ) {
    final boolean isTable =
        exportDescriptor != null && getRuntime().getExportDescriptor().startsWith( exportDescriptor );

    final ReportDefinition report = event.getReport();
    if ( isHidePageBands() ) {
      report.getPageHeader().setVisible( isTable == false );
      report.getPageFooter().setVisible( isTable == false );
    }
    if ( isDisableRepeatingHeader() ) {
      final int gc = report.getGroupCount();
      for ( int i = 0; i < gc; i++ ) {
        final Group g = report.getGroup( i );
        if ( g instanceof RelationalGroup ) {
          final RelationalGroup rg = (RelationalGroup) g;
          if ( rg.getHeader().isRepeat() ) {
            rg.getHeader().setRepeat( isTable == false );
          }
        }
      }
    }
  }

  public String getExportDescriptor() {
    return exportDescriptor;
  }

  public void setExportDescriptor( final String exportDescriptor ) {
    this.exportDescriptor = exportDescriptor;
  }

  /**
   * Returns whether page bands should be hidden.
   *
   * @return true, if page bands should be hidden, false otherwise.
   */
  public boolean isHidePageBands() {
    return hidePageBands;
  }

  /**
   * Defines whether page bands should be hidden.
   *
   * @param hidePageBands
   *          true, if page bands should be hidden, false otherwise.
   */
  public void setHidePageBands( final boolean hidePageBands ) {
    this.hidePageBands = hidePageBands;
  }

  /**
   * Returns whether repeating group header and footer should made non-repeating.
   *
   * @return true, if repeating header and footer will be disabled, false otherwise.
   */
  public boolean isDisableRepeatingHeader() {
    return disableRepeatingHeader;
  }

  /**
   * Defines whether repeating group header and footer should made non-repeating.
   *
   * @param disableRepeatingHeader
   *          true, if repeating header and footer will be disabled, false otherwise.
   */
  public void setDisableRepeatingHeader( final boolean disableRepeatingHeader ) {
    this.disableRepeatingHeader = disableRepeatingHeader;
  }

  /**
   * This method returns null, as formatting functions have no computed return value.
   *
   * @return null.
   */
  public Object getValue() {
    return null;
  }
}
