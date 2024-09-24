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

package org.pentaho.reporting.engine.classic.core.metadata;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.DataSource;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

import java.util.Locale;

/**
 * A element type is a data-source/data-filter implementation much like the templates. But instead of having own
 * getter/setter properties, a element type implementation provides a structured meta-data object to describe the
 * purpose and properties of the element.
 *
 * @author Thomas Morgner
 */
public interface ElementType extends DataSource {
  /**
   * Returns the element metadata for this element type.
   *
   * @return the element meta-data.
   */
  public ElementMetaData getMetaData();

  /**
   * Compute a design-time value. This value will be displayed when editing the element in the Pentaho Report Designer.
   * If there is not enough data available to produce a sensible output, return a mock-object so that the user can
   * interact with the element in a sensible fashion.
   * <p/>
   * When the element is called, all style and attribute expressions have been resolved and can be accessed via the
   * normal static style and attribute accessor methods. If the content of your element depends on style information,
   * the fully computed style must be retrieved via the
   * {@link org.pentaho.reporting.engine.classic.core .Element#getComputedStyle()} method. The local style-sheet for the
   * element may not have all information.
   *
   * @param runtime
   *          the expression runtime holding the current report state.
   * @param element
   *          the element.
   * @return the computed value.
   */
  public Object getDesignValue( ExpressionRuntime runtime, final ReportElement element );

  /**
   * Compute the design-time defaults that should be applied to an element after it was created by a design-tool. Only
   * declare explicit values. If you assume defaults when an attribute or style is undefined, then there is no need to
   * set it here.
   * <p/>
   * Any value set here will be treated as user-defined value afterwards and will be explicitly stored in the XML files.
   *
   * @param element
   *          the element that should be configured.
   * @param locale
   *          the locale.
   */
  public void configureDesignTimeDefaults( ReportElement element, Locale locale );

  /**
   * Creates the element implementation. Return a new {@link org.pentaho.reporting.engine.classic.core.Element} for
   * data-items, {@link org.pentaho.reporting.engine.classic.core.Band} for compound data items or the appropriate
   * Element implementation for complex element types.
   *
   * @return the new element instance.
   */
  public ReportElement create();
}
