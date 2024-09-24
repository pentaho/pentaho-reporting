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

package org.pentaho.reporting.libraries.css.selectors;

import org.w3c.css.sac.CharacterDataSelector;

/**
 * Creation-Date: 30.11.2005, 16:04:27
 *
 * @author Thomas Morgner
 */
public class CSSCharacterDataSelector extends AbstractSelector
  implements CharacterDataSelector {
  private String data;

  public CSSCharacterDataSelector( final String data ) {
    this.data = data;
  }

  protected SelectorWeight createWeight() {
    return new SelectorWeight( 0, 0, 0, 1 );
  }

  /**
   * Returns the character data.
   */
  public String getData() {
    return data;
  }

  /**
   * An integer indicating the type of <code>Selector</code>
   */
  public short getSelectorType() {
    return SAC_CDATA_SECTION_NODE_SELECTOR;
  }
}
