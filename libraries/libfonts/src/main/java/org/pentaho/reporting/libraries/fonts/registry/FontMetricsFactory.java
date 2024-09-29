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


package org.pentaho.reporting.libraries.fonts.registry;

/**
 * An abstract method to load font metrics.
 * <p/>
 * Implementations of this class fully manage the creation of resources and should make sure that no unneccessary
 * metrics are loaded.
 *
 * @author Thomas Morgner
 */
public interface FontMetricsFactory {
  /**
   * Loads the font metrics for the font identified by the given identifier.
   *
   * @param identifier
   * @param context
   * @return
   */
  public FontMetrics createMetrics( final FontIdentifier identifier,
                                    final FontContext context );
}
