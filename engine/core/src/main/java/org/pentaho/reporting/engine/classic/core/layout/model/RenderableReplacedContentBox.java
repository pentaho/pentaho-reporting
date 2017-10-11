/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.model;

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.filter.types.AutoLayoutBoxType;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class RenderableReplacedContentBox extends RenderBox {
  private RenderableReplacedContent content;

  public RenderableReplacedContentBox( final RenderableReplacedContent content ) {
    super( RenderNode.VERTICAL_AXIS, RenderNode.HORIZONTAL_AXIS, SimpleStyleSheet.EMPTY_STYLE, new InstanceID(),
        BoxDefinition.EMPTY, AutoLayoutBoxType.INSTANCE, ReportAttributeMap.EMPTY_MAP, null );
    if ( content == null ) {
      throw new NullPointerException();
    }
    this.content = content;
    close();
  }

  public RenderableReplacedContentBox( final StyleSheet styleSheet, final InstanceID instanceId,
      final BoxDefinition boxDefinition, final ElementType elementType, final ReportAttributeMap attributes,
      final ReportStateKey stateKey, final RenderableReplacedContent content ) {
    super( RenderNode.VERTICAL_AXIS, RenderNode.HORIZONTAL_AXIS, styleSheet, instanceId, boxDefinition, elementType,
        attributes, stateKey );
    if ( content == null ) {
      throw new NullPointerException();
    }
    this.content = content;
    close();
  }

  public RenderableReplacedContent getContent() {
    return content;
  }

  public boolean isEmpty() {
    return false;
  }

  public int getNodeType() {
    return LayoutNodeTypes.TYPE_BOX_CONTENT;
  }
}
