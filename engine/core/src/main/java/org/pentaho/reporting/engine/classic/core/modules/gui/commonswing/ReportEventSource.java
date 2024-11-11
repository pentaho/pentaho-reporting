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


package org.pentaho.reporting.engine.classic.core.modules.gui.commonswing;

import org.pentaho.reporting.engine.classic.core.MasterReport;

import java.beans.PropertyChangeListener;

/**
 * Creation-Date: 15.08.2007, 16:20:24
 *
 * @author Thomas Morgner
 */
public interface ReportEventSource {
  public static final String REPORT_JOB_PROPERTY = "reportJob";
  public static final String PAGE_NUMBER_PROPERTY = "pageNumber";
  public static final String NUMBER_OF_PAGES_PROPERTY = "numberOfPages";
  public static final String PAGINATED_PROPERTY = "paginated";
  public static final String PAGINATING_PROPERTY = "paginating";

  public int getPageNumber();

  public int getNumberOfPages();

  public boolean isPaginated();

  public boolean isPaginating();

  public MasterReport getReportJob();

  public void addPropertyChangeListener( final PropertyChangeListener propertyChangeListener );

  public void addPropertyChangeListener( final String property, final PropertyChangeListener propertyChangeListener );

  public void removePropertyChangeListener( final PropertyChangeListener propertyChangeListener );

  public void removePropertyChangeListener( final String property, final PropertyChangeListener propertyChangeListener );
}
