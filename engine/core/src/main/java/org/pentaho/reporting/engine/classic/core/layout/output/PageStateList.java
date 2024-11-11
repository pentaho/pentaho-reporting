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


package org.pentaho.reporting.engine.classic.core.layout.output;

/**
 * A page state list contains stored page states. Page States are generated as layouter-save-points from where the
 * report processing can restart later on.
 *
 * @author Thomas Morgner.
 */
public interface PageStateList {
  public int size();

  public void add( PageState state );

  public void clear();

  public PageState get( int index );
}
