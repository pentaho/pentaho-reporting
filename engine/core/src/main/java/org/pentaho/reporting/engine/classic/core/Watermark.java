/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.filter.types.bands.WatermarkType;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.RootLevelBandDefaultStyleSheet;

/**
 * A report band used to print information at the background of every page in the report. There is an option to suppress
 * the printing on the first page and the last page of the report (this is often useful if you are using a report header
 * and/or report footer).
 * <p/>
 * A watermark section cannot have subreports.
 *
 * @author David Gilbert
 */
public class Watermark extends Band implements RootLevelBand {
  /**
   * A empty array defined here for performance reasons.
   */
  private static final SubReport[] EMPTY_SUB_REPORT = new SubReport[0];

  /**
   * A default style sheet for shape elements. This defined a default stroke for all shapes.
   */
  private static class WatermarkDefaultStyleSheet extends RootLevelBandDefaultStyleSheet {
    /**
     * Creates a new style-sheet. The stylesheet is not modifiable
     */
    protected WatermarkDefaultStyleSheet() {
      // unlock the write protection
      setLocked( false );
      setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( -100 ) );
      setLocked( true );
    }
  }

  /**
   * A shared default style sheet for shape elements.
   */
  private static ElementDefaultStyleSheet defaultStyle;

  /**
   * Returns the default style-sheet for shape elements.
   *
   * @return a default style sheet that can be shared among shape elements.
   */
  public static synchronized ElementDefaultStyleSheet getDefaultStyle() {
    if ( defaultStyle == null ) {
      defaultStyle = new WatermarkDefaultStyleSheet();
    }
    return defaultStyle;
  }

  /**
   * Constructs a watermark band.
   */
  public Watermark() {
    setElementType( new WatermarkType() );
  }

  /**
   * Returns the global stylesheet for the watermark bands. This stylesheet provides the predefined default values for
   * some of the stylekeys.
   *
   * @return the global default stylesheet.
   */
  public ElementStyleSheet getDefaultStyleSheet() {
    return Watermark.getDefaultStyle();
  }

  /**
   * Constructs a page footer containing no elements.
   *
   * @param onFirstPage
   *          defines, whether the page header will be printed on the first page
   * @param onLastPage
   *          defines, whether the page footer will be printed on the last page.
   */
  public Watermark( final boolean onFirstPage, final boolean onLastPage ) {
    super();
    if ( onFirstPage == false ) {
      setDisplayOnFirstPage( onFirstPage );
    }
    if ( onLastPage == false ) {
      setDisplayOnLastPage( onLastPage );
    }
  }

  /**
   * Returns true if the header should be shown on page 1, and false otherwise.
   *
   * @return true or false.
   */
  public boolean isDisplayOnFirstPage() {
    return getStyle().getBooleanStyleProperty( BandStyleKeys.DISPLAY_ON_FIRSTPAGE );
  }

  /**
   * Defines whether the header should be shown on the first page.
   *
   * @param b
   *          a flag indicating whether or not the header is shown on the first page.
   */
  public void setDisplayOnFirstPage( final boolean b ) {
    getStyle().setBooleanStyleProperty( BandStyleKeys.DISPLAY_ON_FIRSTPAGE, b );
    notifyNodePropertiesChanged();
  }

  /**
   * Returns true if the header should be shown on the last page, and false otherwise.
   *
   * @return true or false.
   */
  public boolean isDisplayOnLastPage() {
    return getStyle().getBooleanStyleProperty( BandStyleKeys.DISPLAY_ON_LASTPAGE );
  }

  /**
   * Defines whether the header should be shown on the last page.
   *
   * @param b
   *          a flag indicating whether or not the header is shown on the last page.
   */
  public void setDisplayOnLastPage( final boolean b ) {
    getStyle().setBooleanStyleProperty( BandStyleKeys.DISPLAY_ON_LASTPAGE, b );
    notifyNodePropertiesChanged();
  }

  /**
   * Returns the number of subreports on this band. This returns zero, as page-bands cannot have subreports.
   *
   * @return the subreport count.
   */
  public int getSubReportCount() {
    return 0;
  }

  /**
   * Throws an IndexOutOfBoundsException as page-footer cannot have sub-reports.
   *
   * @param index
   *          the index.
   * @return nothing, as an exception is thrown instead.
   */
  public SubReport getSubReport( final int index ) {
    throw new IndexOutOfBoundsException( "Watermark bands cannot have subreports." );
  }

  /**
   * Returns an empty array, as page-footer cannot have subreports.
   *
   * @return the sub-reports as array.
   */
  public SubReport[] getSubReports() {
    return Watermark.EMPTY_SUB_REPORT;
  }

  /**
   * Returns true if the footer should be shown on all subreports.
   *
   * @return true or false.
   */
  public boolean isSticky() {
    return getStyle().getBooleanStyleProperty( BandStyleKeys.STICKY, false );
  }

  /**
   * Defines whether the footer should be shown on all subreports.
   *
   * @param b
   *          a flag indicating whether or not the footer is shown on the first page.
   */
  public void setSticky( final boolean b ) {
    getStyle().setBooleanStyleProperty( BandStyleKeys.STICKY, b );
    notifyNodePropertiesChanged();
  }
}
