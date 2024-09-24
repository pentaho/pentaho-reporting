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

package org.pentaho.reporting.engine.classic.core.layout.process.alignment;

import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.InlineSequenceElement;

public class JustifyAlignmentProcessor extends LeftAlignmentProcessor {
  public JustifyAlignmentProcessor() {
  }

  protected int iterate( final InlineSequenceElement[] elements, final int maxPos ) {
    final int index = super.iterate( elements, maxPos );
    if ( index <= 0 ) {
      return index;
    }

    performTextJustify( index );
    return index;
  }

  private void performTextJustify( final int index ) {
    final long[] elementPositions = getElementPositions();
    final long[] elementDimensions = getElementDimensions();
    final int lastIndex = index - 1;
    final long endPos = elementPositions[lastIndex] + elementDimensions[lastIndex];

    final long extraSpace = Math.max( 0, getEndOfLine() - endPos );
    final RenderNode[] nodes = getNodes();
    final InlineSequenceElement[] sequenceElements = getSequenceElements();
    if ( extraSpace > 0 && isLastLineAlignment() == false ) {
      long spacerCount = 0;
      for ( int i = 0; i < index; i++ ) {
        // justify text
        final InlineSequenceElement ise = sequenceElements[i];
        final RenderNode node = nodes[i];
        if ( node.getNodeType() == LayoutNodeTypes.TYPE_NODE_SPACER ) {
          spacerCount += ise.getMaximumWidth( node );
        }
      }

      if ( spacerCount > 0 ) {
        final double extraSpacePerSpacerUnit = extraSpace / (double) spacerCount;
        long shift = 0;
        for ( int i = 0; i < index; i++ ) {
          elementPositions[i] += shift;

          // justify text
          final InlineSequenceElement ise = sequenceElements[i];
          final RenderNode node = nodes[i];
          if ( node.getNodeType() == LayoutNodeTypes.TYPE_NODE_SPACER ) {
            final long width = ise.getMaximumWidth( node );
            final long extraSpaceHere = (long) ( extraSpacePerSpacerUnit * width );
            elementDimensions[i] += extraSpaceHere;
            shift += extraSpaceHere;
          }
        }
      }
    }
  }
}
