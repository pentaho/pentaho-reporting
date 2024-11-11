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
