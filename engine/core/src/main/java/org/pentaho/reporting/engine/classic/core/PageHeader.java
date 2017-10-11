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

import org.pentaho.reporting.engine.classic.core.filter.types.bands.PageHeaderType;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.RootLevelBandDefaultStyleSheet;

/**
 * A report band used to print information at the top of every page in the report. The page header is the first band
 * that is printed on each page. There is an option to suppress the page header on the first page and the last page of
 * the report (this is often useful if you are using a report header and/or report footer). If the header is marked
 * sticky, the header will even be printed for all sub-report pages.
 * <p/>
 * A page header or footer cannot have subreports.
 *
 * @author David Gilbert
 */
public class PageHeader extends Band implements RootLevelBand {
  /**
   * A helper array to prevent unnecessary object creation.
   */
  private static final SubReport[] EMPTY_SUB_REPORTS = new SubReport[0];

  /**
   * Constructs a page header.
   */
  public PageHeader() {
    setElementType( new PageHeaderType() );
  }

  /**
   * Constructs a page footer containing no elements.
   *
   * @param onFirstPage
   *          defines, whether the page header will be printed on the first page
   * @param onLastPage
   *          defines, whether the page footer will be printed on the last page.
   */
  public PageHeader( final boolean onFirstPage, final boolean onLastPage ) {
    super();
    setDisplayOnFirstPage( onFirstPage );
    setDisplayOnLastPage( onLastPage );
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
  public final int getSubReportCount() {
    return 0;
  }

  /**
   * Throws an IndexOutOfBoundsException as page-footer cannot have sub-reports.
   *
   * @param index
   *          the index.
   * @return nothing, as an exception is thrown instead.
   */
  public final SubReport getSubReport( final int index ) {
    throw new IndexOutOfBoundsException( "PageHeader cannot have subreports" );
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

  /**
   * Returns an empty array, as page-footer cannot have subreports.
   *
   * @return the sub-reports as array.
   */
  public SubReport[] getSubReports() {
    return PageHeader.EMPTY_SUB_REPORTS;
  }

  public ElementStyleSheet getDefaultStyleSheet() {
    return RootLevelBandDefaultStyleSheet.getRootLevelBandDefaultStyle();
  }
}
