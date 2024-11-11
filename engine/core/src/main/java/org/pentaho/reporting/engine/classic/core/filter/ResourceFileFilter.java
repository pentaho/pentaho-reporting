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


package org.pentaho.reporting.engine.classic.core.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

import java.util.ResourceBundle;

/**
 * Lookup a key from a datasource using a ResourceBundle.
 * <p/>
 * Filters a given datasource and uses the datasource value as key for a ResourceBundle.
 *
 * @author Thomas Morgner
 */
public class ResourceFileFilter implements DataFilter {
  private static final Log logger = LogFactory.getLog( ResourceFileFilter.class );
  /**
   * the used resource bundle.
   */
  private String resourceIdentifier;

  /**
   * the filtered data source.
   */
  private DataSource dataSource;

  /**
   * Creates a new ResourceFileFilter.
   */
  public ResourceFileFilter() {
  }

  /**
   * Returns the name of the used resource bundle.
   *
   * @return the name of the resourcebundle
   * @see org.pentaho.reporting.engine.classic.core.ResourceBundleFactory#getResourceBundle(String)
   */
  public String getResourceIdentifier() {
    return resourceIdentifier;
  }

  /**
   * Defines the name of the used resource bundle. If undefined, all calls to
   * {@link DataSource#getValue(ExpressionRuntime, org.pentaho.reporting.engine.classic.core.ReportElement)} will result
   * in <code>null</code> values.
   *
   * @param resourceIdentifier
   *          the resource bundle name
   */
  public void setResourceIdentifier( final String resourceIdentifier ) {
    this.resourceIdentifier = resourceIdentifier;
  }

  /**
   * Returns the current value for the data source. The method will return null, if no datasource or no resource bundle
   * is defined or if the datasource's value is null.
   * <p/>
   * The value read from the dataSource is looked up in the given resourcebundle using the
   * <code>ResourceBundle.getObject()</code> method. If the lookup fails, null is returned.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   * @return the value or null, if the value could not be looked up.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    if ( dataSource == null ) {
      return null;
    }
    if ( runtime == null ) {
      return null;
    }
    final Object value = dataSource.getValue( runtime, element );
    if ( value == null ) {
      return null;
    }
    final String svalue = String.valueOf( value );

    try {
      final String resourceId;
      if ( resourceIdentifier != null ) {
        resourceId = resourceIdentifier;
      } else {
        resourceId =
            runtime.getConfiguration().getConfigProperty( ResourceBundleFactory.DEFAULT_RESOURCE_BUNDLE_CONFIG_KEY );
      }

      if ( resourceId == null ) {
        return null;
      }

      final ResourceBundleFactory resourceBundleFactory = runtime.getResourceBundleFactory();
      final ResourceBundle bundle = resourceBundleFactory.getResourceBundle( resourceId );
      if ( bundle != null ) {
        return bundle.getObject( svalue );
      }
    } catch ( Exception e ) {
      // on errors return null.
      ResourceFileFilter.logger.warn( "Failed to retrive the value for key " + svalue );
    }
    return null;
  }

  /**
   * Clones this <code>DataSource</code>.
   *
   * @return the clone.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public ResourceFileFilter clone() throws CloneNotSupportedException {
    final ResourceFileFilter filter = (ResourceFileFilter) super.clone();
    filter.dataSource = dataSource.clone();
    return filter;
  }

  /**
   * Returns the assigned DataSource for this Target.
   *
   * @return The datasource.
   */
  public DataSource getDataSource() {
    return dataSource;
  }

  /**
   * Assigns a DataSource for this Target.
   *
   * @param ds
   *          The data source.
   */
  public void setDataSource( final DataSource ds ) {
    this.dataSource = ds;
  }

}
