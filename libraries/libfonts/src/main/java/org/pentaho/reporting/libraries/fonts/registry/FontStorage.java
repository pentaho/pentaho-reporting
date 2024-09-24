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
 * The base for application specific font managment.
 *
 * @author Thomas Morgner
 */
public interface FontStorage {
  public FontRegistry getFontRegistry();

  public FontMetrics getFontMetrics( final FontIdentifier record,
                                     final FontContext context );

  /**
   * Mark the processing to be finished; commit any caches to the global storage, if applicable, clean up and return
   * into a sane state.
   */
  public void commit();
}
