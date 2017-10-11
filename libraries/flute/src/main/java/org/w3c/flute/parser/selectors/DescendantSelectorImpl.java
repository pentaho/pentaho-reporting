/*
 * Copyright (c) 2000 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de
 * Recherche en Informatique et en Automatique, Keio University). All
 * Rights Reserved. This program is distributed under the W3C's Software
 * Intellectual Property License. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.
 * See W3C License http://www.w3.org/Consortium/Legal/ for more details.
 *
 * $Id: DescendantSelectorImpl.java 1830 2006-04-23 14:51:03Z taqua $
 */
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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, World Wide Web Consortium,.  All rights reserved.
 */

package org.w3c.flute.parser.selectors;

import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;

/**
 * @author Philippe Le Hegaret
 * @version $Revision$
 */
public class DescendantSelectorImpl implements DescendantSelector {

  Selector parent;
  SimpleSelector simpleSelector;

  /**
   * An integer indicating the type of <code>Selector</code>
   */
  public short getSelectorType() {
    return Selector.SAC_DESCENDANT_SELECTOR;
  }

  /**
   * Creates a new DescendantSelectorImpl
   */
  public DescendantSelectorImpl( Selector parent, SimpleSelector simpleSelector ) {
    this.parent = parent;
    this.simpleSelector = simpleSelector;
  }


  /**
   * Returns the parent selector.
   */
  public Selector getAncestorSelector() {
    return parent;
  }

  /*
   * Returns the simple selector.
   */
  public SimpleSelector getSimpleSelector() {
    return simpleSelector;
  }
}
