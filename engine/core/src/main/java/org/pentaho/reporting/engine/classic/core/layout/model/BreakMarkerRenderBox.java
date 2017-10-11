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

package org.pentaho.reporting.engine.classic.core.layout.model;

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * A special box that simply marks the position of an automatic pagebreak. This is needed in the process of the event
 * ordering and rollback processing.
 *
 * @author Thomas Morgner
 */
public final class BreakMarkerRenderBox extends BlockRenderBox {
  private long validityRange;

  public BreakMarkerRenderBox( final StyleSheet styleSheet, final InstanceID instanceID,
      final BoxDefinition boxDefinition, final ElementType elementType, final ReportAttributeMap attributeMap,
      final ReportStateKey stateKey, final long validityRange ) {
    super( styleSheet, instanceID, boxDefinition, elementType, attributeMap, stateKey );
    this.validityRange = validityRange;
  }

  /**
   * The page-offset of the page where this break-marker is valid. Note that the page-offset denotes the start of the
   * page, not the end.
   *
   * @return the validity of this break marker.
   */
  public long getValidityRange() {
    return validityRange;
  }

  public boolean isIgnorableForRendering() {
    return false;
  }

  public int getNodeType() {
    return LayoutNodeTypes.TYPE_BOX_BREAKMARK;
  }

}
