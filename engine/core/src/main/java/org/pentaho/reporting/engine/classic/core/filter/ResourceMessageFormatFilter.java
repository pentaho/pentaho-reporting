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

package org.pentaho.reporting.engine.classic.core.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * A filter that formats values from the datarow using a message format object. The message format string is looked up
 * from a Resource-Bundle.
 *
 * @author Thomas Morgner
 * @since 2006-01-24
 */
public class ResourceMessageFormatFilter implements DataSource {
  private static final Log logger = LogFactory.getLog( ResourceMessageFormatFilter.class );
  /**
   * The format key that has been applied to the message format. This variable is used to track changes to the original
   * format key and to update the message format if necessary.
   */
  private transient String appliedFormatKey;

  /**
   * The key that gets used to lookup the message format string from the resource bundle.
   */
  private String formatKey;

  /**
   * The name of the resource bundle used to lookup the message.
   */
  private String resourceIdentifier;

  /**
   * The message format support translates raw message strings into useable MessageFormat parameters and read the
   * necessary input data from the datarow.
   */
  private MessageFormatSupport messageFormatSupport;

  /**
   * Default constructor.
   */
  public ResourceMessageFormatFilter() {
    messageFormatSupport = new MessageFormatSupport();
  }

  /**
   * Returns the current value for the data source.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   * @return the value.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    if ( runtime == null ) {
      return null;
    }
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

    try {
      final ResourceBundleFactory resourceBundleFactory = runtime.getResourceBundleFactory();
      final ResourceBundle bundle = resourceBundleFactory.getResourceBundle( resourceId );

      // update the format string, if neccessary ...
      if ( ObjectUtilities.equal( formatKey, appliedFormatKey ) == false ) {
        final String newFormatString = bundle.getString( formatKey );
        messageFormatSupport.setFormatString( newFormatString );
        appliedFormatKey = formatKey;
      }

      messageFormatSupport.setLocale( resourceBundleFactory.getLocale() );
      messageFormatSupport.setTimeZone( resourceBundleFactory.getTimeZone() );
      return messageFormatSupport.performFormat( runtime.getDataRow() );
    } catch ( MissingResourceException mre ) {
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Failed to format the value for resource-id " + resourceId + ", was '" + mre.getMessage() + "'" );
      }
      return null;
    } catch ( Exception e ) {
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Failed to format the value for resource-id " + resourceId, e );
      }
      return null;
    }
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
   * Defines the key that is used to lookup the format string used in the message format in the resource bundle.
   *
   * @param format
   *          a resourcebundle key for the message format lookup.
   */
  public void setFormatKey( final String format ) {
    this.formatKey = format;
  }

  /**
   * Returns the key that is used to lookup the format string used in the message format in the resource bundle.
   *
   * @return the resource bundle key.
   */
  public String getFormatKey() {
    return formatKey;
  }

  /**
   * Clones this <code>DataSource</code>.
   *
   * @return the clone.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public ResourceMessageFormatFilter clone() throws CloneNotSupportedException {
    final ResourceMessageFormatFilter mf = (ResourceMessageFormatFilter) super.clone();
    mf.messageFormatSupport = (MessageFormatSupport) messageFormatSupport.clone();
    return mf;
  }

  /**
   * Returns the replacement text that is used if one of the referenced message parameters is null.
   *
   * @return the replacement text for null-values.
   */
  public String getNullString() {
    return messageFormatSupport.getNullString();
  }

  /**
   * Defines the replacement text that is used if one of the referenced message parameters is null.
   *
   * @param nullString
   *          the replacement text for null-values.
   */
  public void setNullString( final String nullString ) {
    this.messageFormatSupport.setNullString( nullString );
  }
}
