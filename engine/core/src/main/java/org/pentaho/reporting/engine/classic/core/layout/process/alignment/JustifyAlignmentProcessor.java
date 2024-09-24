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
