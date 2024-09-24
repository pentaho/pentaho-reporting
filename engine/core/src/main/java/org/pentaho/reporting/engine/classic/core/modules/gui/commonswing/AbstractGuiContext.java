/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.gui.commonswing;

import java.awt.Window;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Locale;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.DefaultIconTheme;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.IconTheme;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusListener;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusType;
import org.pentaho.reporting.libraries.base.config.Configuration;

public abstract class AbstractGuiContext implements SwingGuiContext, StatusListener, ReportEventSource {
  public static final String STATUS_TYPE_PROPERTY = "statusType";
  public static final String STATUS_TEXT_PROPERTY = "statusText";
  public static final String ERROR_PROPERTY = "error";

  private IconTheme iconTheme;
  private StatusType statusType;
  private String statusText;
  private Throwable error;
  private int pageNumber;
  private int numberOfPages;
  private boolean paginating;
  private boolean paginated;
  private PropertyChangeSupport propertyChangeSupport;

  protected AbstractGuiContext() {
    this.iconTheme = new DefaultIconTheme();
    this.propertyChangeSupport = new PropertyChangeSupport( this );
  }

  public abstract Window getWindow();

  public Locale getLocale() {
    final MasterReport report = getReportJob();
    if ( report != null ) {
      final Locale bundleLocale = report.getResourceBundleFactory().getLocale();
      if ( bundleLocale != null ) {
        return bundleLocale;
      }
      return report.getReportEnvironment().getLocale();
    }
    return Locale.getDefault();
  }

  public IconTheme getIconTheme() {
    return iconTheme;
  }

  public Configuration getConfiguration() {
    final MasterReport report = getReportJob();
    if ( report != null ) {
      return report.getConfiguration();
    }
    return ClassicEngineBoot.getInstance().getGlobalConfig();
  }

  public StatusListener getStatusListener() {
    return this;
  }

  public void setStatus( final StatusType type, final String text, final Throwable cause ) {
    final StatusType oldStatusType = this.statusType;
    final String oldStatusText = this.statusText;
    final Throwable oldError = this.error;
    this.statusType = type;
    this.statusText = text;
    this.error = cause;
    propertyChangeSupport.firePropertyChange( STATUS_TYPE_PROPERTY, oldStatusType, statusType );
    propertyChangeSupport.firePropertyChange( STATUS_TEXT_PROPERTY, oldStatusText, statusText );
    propertyChangeSupport.firePropertyChange( ERROR_PROPERTY, oldError, error );

  }

  public Throwable getError() {
    return error;
  }

  public StatusType getStatusType() {
    return statusType;
  }

  public String getStatusText() {
    return statusText;
  }

  public ReportEventSource getEventSource() {
    return this;
  }

  public void setPageNumber( final int pageNumber ) {
    final int oldPageNumber = this.pageNumber;
    this.pageNumber = pageNumber;

    propertyChangeSupport.firePropertyChange( ReportEventSource.PAGE_NUMBER_PROPERTY, oldPageNumber, numberOfPages );
  }

  public int getPageNumber() {
    return pageNumber;
  }

  public void setNumberOfPages( final int numberOfPages ) {
    final int oldNumberOfPages = this.numberOfPages;
    this.numberOfPages = numberOfPages;

    propertyChangeSupport.firePropertyChange( ReportEventSource.NUMBER_OF_PAGES_PROPERTY, oldNumberOfPages,
        numberOfPages );
  }

  public int getNumberOfPages() {
    return numberOfPages;
  }

  public void setPaginating( final boolean paginating ) {
    final boolean oldPginating = this.paginating;
    this.paginating = paginating;

    propertyChangeSupport.firePropertyChange( ReportEventSource.PAGINATING_PROPERTY, oldPginating, paginating );
  }

  public boolean isPaginating() {
    return paginating;
  }

  public void setPaginated( final boolean paginated ) {
    final boolean oldPaginated = this.paginated;
    this.paginated = paginated;
    propertyChangeSupport.firePropertyChange( ReportEventSource.PAGINATED_PROPERTY, oldPaginated, paginated );
  }

  public boolean isPaginated() {
    return paginated;
  }

  public abstract MasterReport getReportJob();

  public void addPropertyChangeListener( final PropertyChangeListener propertyChangeListener ) {
    propertyChangeSupport.addPropertyChangeListener( propertyChangeListener );
  }

  public void addPropertyChangeListener( final String property, final PropertyChangeListener propertyChangeListener ) {
    propertyChangeSupport.addPropertyChangeListener( property, propertyChangeListener );
  }

  public void removePropertyChangeListener( final PropertyChangeListener propertyChangeListener ) {
    propertyChangeSupport.removePropertyChangeListener( propertyChangeListener );
  }

  public void removePropertyChangeListener( final String property, final PropertyChangeListener propertyChangeListener ) {
    propertyChangeSupport.removePropertyChangeListener( property, propertyChangeListener );
  }

  protected void firePropertyChange( final String propertyName, final Object oldValue, final Object newValue ) {
    propertyChangeSupport.firePropertyChange( propertyName, oldValue, newValue );
  }
}
