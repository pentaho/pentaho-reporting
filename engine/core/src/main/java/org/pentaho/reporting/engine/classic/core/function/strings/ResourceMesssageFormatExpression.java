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

package org.pentaho.reporting.engine.classic.core.function.strings;

import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.filter.MessageFormatSupport;
import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;
import org.pentaho.reporting.engine.classic.core.function.ExpressionUtilities;

import java.util.ResourceBundle;

/**
 * Formats a message read from a resource-bundle using named parameters. The parameters are resolved against the current
 * data-row.
 * <p/>
 * This performs the same task as the ResourceMessageFormatFilter does inside a text-element.
 *
 * @author Thomas Morgner
 */
public class ResourceMesssageFormatExpression extends AbstractExpression {
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
  public ResourceMesssageFormatExpression() {
    messageFormatSupport = new MessageFormatSupport();
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
   * {@link ResourceMesssageFormatExpression#getValue()} will result in <code>null</code> values.
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

  /**
   * Returns the formatted message.
   *
   * @return the formatted message.
   */
  public Object getValue() {
    final ResourceBundleFactory resourceBundleFactory = getResourceBundleFactory();
    final ResourceBundle bundle;
    if ( resourceIdentifier == null ) {
      bundle = ExpressionUtilities.getDefaultResourceBundle( this );
    } else {
      bundle = resourceBundleFactory.getResourceBundle( resourceIdentifier );
    }

    final String newFormatString = bundle.getString( formatKey );
    messageFormatSupport.setFormatString( newFormatString );
    messageFormatSupport.setLocale( resourceBundleFactory.getLocale() );
    return messageFormatSupport.performFormat( getDataRow() );
  }
}
