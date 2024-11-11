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


package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.stylekey;

import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;

/**
 * A default implementation of the {@link StyleKeyFactory} interface. This implementation contains all stylekeys from
 * the ElementStyleSheet, the BandStyleSheet and the ShapeElement stylesheet.
 * <p/>
 * If available, the excel stylesheets will also be loaded.
 *
 * @author Thomas Morgner
 */
public class DefaultStyleKeyFactory extends AbstractStyleKeyFactory {
  /**
   * Creates a new factory.
   */
  public DefaultStyleKeyFactory() {
    loadFromClass( ElementStyleKeys.class );
    loadFromClass( TextStyleKeys.class );
    loadFromClass( BandStyleKeys.class );
  }

}
