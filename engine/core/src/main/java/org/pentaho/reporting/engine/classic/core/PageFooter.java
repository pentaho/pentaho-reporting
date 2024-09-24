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

package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.filter.types.bands.PageFooterType;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.RootLevelBandDefaultStyleSheet;

/**
 * A report band that appears at the bottom of every page. The page-footer is the last band that is printed on a page.
 * There is an option to suppress the page footer on the first page, and another option does the same for the last page.
 * If the footer is marked sticky, the footer will even be printed for all sub-report pages.
 * <p/>
 * A page header or footer cannot have subreports.
 *
 * @author David Gilbert
 */
public class PageFooter extends Band implements RootLevelBand {
  /**
   * A empty array defined here for performance reasons.
   */
  private static final SubReport[] EMPTY_REPORTS = new SubReport[0];

  /**
   * Constructs a page footer containing no elements.
   */
  public PageFooter() {
    setElementType( new PageFooterType() );
  }

  /**
   * Constructs a page footer containing no elements.
   *
   * @param onFirstPage
   *          defines, whether the page header will be printed on the first page
   * @param onLastPage
   *          defines, whether the page footer will be printed on the last page.
   */
  public PageFooter( final boolean onFirstPage, final boolean onLastPage ) {
    super();
    setDisplayOnFirstPage( onFirstPage );
    setDisplayOnLastPage( onLastPage );
  }

  /**
   * Returns true if the footer should be shown on page 1, and false otherwise.
   *
   * @return true or false.
   */
  public boolean isDisplayOnFirstPage() {
    return getStyle().getBooleanStyleProperty( BandStyleKeys.DISPLAY_ON_FIRSTPAGE, false );
  }

  /**
   * Defines whether the footer should be shown on the first page.
   *
   * @param b
   *          a flag indicating whether or not the footer is shown on the first page.
   */
  public void setDisplayOnFirstPage( final boolean b ) {
    getStyle().setBooleanStyleProperty( BandStyleKeys.DISPLAY_ON_FIRSTPAGE, b );
    notifyNodePropertiesChanged();
  }

  /**
   * Returns true if the footer should be shown on the last page, and false otherwise.
   *
   * @return true or false.
   */
  public boolean isDisplayOnLastPage() {
    return getStyle().getBooleanStyleProperty( BandStyleKeys.DISPLAY_ON_LASTPAGE, false );
  }

  /**
   * Defines whether the footer should be shown on the last page.
   *
   * @param b
   *          a flag indicating whether or not the footer is shown on the first page.
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
    throw new IndexOutOfBoundsException( "PageFooter cannot have subreports." );
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
    return PageFooter.EMPTY_REPORTS;
  }

  public ElementStyleSheet getDefaultStyleSheet() {
    return RootLevelBandDefaultStyleSheet.getRootLevelBandDefaultStyle();
  }
}
