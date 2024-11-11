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


package org.pentaho.reporting.libraries.css.selectors;

import org.w3c.css.sac.Selector;

import java.io.Serializable;

/**
 * Creation-Date: 05.12.2005, 19:50:03
 *
 * @author Thomas Morgner
 */
public interface CSSSelector extends Selector, Serializable {
  public SelectorWeight getWeight();
}
