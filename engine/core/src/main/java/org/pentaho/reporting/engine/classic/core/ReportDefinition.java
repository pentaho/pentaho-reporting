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

/**
 * A report definition. This the working copy of the JFreeReport object. During the report processing not all properties
 * of the original JFreeReport object will be availble.
 *
 * @author Thomas Morgner.
 */
public interface ReportDefinition extends ReportElement {
  /**
   * Returns the query name that should be used when processing the report.
   *
   * @return the query string.
   */
  public String getQuery();

  public int getQueryLimit();

  public int getQueryTimeout();

  /**
   * Returns the list of groups for the report.
   *
   * @return The list of groups.
   */
  public Group getRootGroup();

  /**
   * Returns the report header.
   *
   * @return The report header.
   */
  public ReportHeader getReportHeader();

  /**
   * Returns the report footer.
   *
   * @return The report footer.
   */
  public ReportFooter getReportFooter();

  /**
   * Returns the page header.
   *
   * @return The page header.
   */
  public PageHeader getPageHeader();

  /**
   * Returns the page footer.
   *
   * @return The page footer.
   */
  public PageFooter getPageFooter();

  /**
   * Returns the details header band.
   *
   * @return The details header band or <code>null</code> if this is not a relational report.
   */
  public DetailsHeader getDetailsHeader();

  /**
   * Returns the details footer band.
   *
   * @return The details footer band or <code>null</code> if this is not a relational report.
   */
  public DetailsFooter getDetailsFooter();

  /**
   * Returns the item band.
   *
   * @return The details band or <code>null</code> if this is not a relational report.
   */
  public ItemBand getItemBand();

  public CrosstabCellBody getCrosstabCellBody();

  /**
   * Returns the watermark band.
   *
   * @return The watermark band.
   */
  public Watermark getWatermark();

  /**
   * Returns the "no-data" band, which is displayed if there is no data available.
   *
   * @return The no-data band or <code>null</code> if this is not a relational report.
   */
  public NoDataBand getNoDataBand();

  /**
   * Returns the number of groups in this report.
   * <P>
   * Every report has at least one group defined.
   *
   * @return the group count.
   */
  public int getGroupCount();

  /**
   * Returns the group at the specified index or null, if there is no such group.
   *
   * @param count
   *          the group index.
   * @return the requested group.
   * @throws IllegalArgumentException
   *           if the count is negative.
   * @throws IndexOutOfBoundsException
   *           if the count is greater than the number of defined groups.
   */
  public Group getGroup( int count );

  /**
   * Returns the page definition assigned to the report definition. The page definition defines the report area and how
   * the report is subdivided by the child pages.
   *
   * @return the page definition.
   */
  public PageDefinition getPageDefinition();
}
