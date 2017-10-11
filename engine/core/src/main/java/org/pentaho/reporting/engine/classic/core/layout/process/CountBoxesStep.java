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

package org.pentaho.reporting.engine.classic.core.layout.process;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.filter.types.AutoLayoutBoxType;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;

import java.text.MessageFormat;

public class CountBoxesStep extends IterateSimpleStructureProcessStep {
  private static final Log logger = LogFactory.getLog( CountBoxesStep.class );
  private int totalCount;
  private int finishedBoxes;
  private int deepDirtyBoxes;
  private int autoBoxes;
  private boolean enabled;
  private int maxBoxSize;
  private boolean validating;

  public CountBoxesStep() {
    enabled =
        "true".equals( ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.layout.process.EnableCountBoxesStep", "false" ) );
  }

  public int countChildren( final RenderBox box ) {
    validating = true;
    totalCount = 0;
    finishedBoxes = 0;
    autoBoxes = 0;
    deepDirtyBoxes = 0;
    if ( box instanceof LogicalPageBox ) {
      totalCount = 1;
      processBoxChilds( box );
    } else {
      startProcessing( box );
    }
    return totalCount;
  }

  public int getTotalCount() {
    return totalCount;
  }

  public void process( final LogicalPageBox box ) {
    if ( !enabled ) {
      return;
    }

    validating = false;
    totalCount = 0;
    finishedBoxes = 0;
    autoBoxes = 0;
    deepDirtyBoxes = 0;
    startProcessing( box );
    logger.debug( MessageFormat.format(
        "CountBoxes: Total={0}; finished={1}; auto={2}; deepDirty={6} - maxWeight={5} - Finished-Ratio: {3} "
            + "AutoRatio: {4}", totalCount, finishedBoxes, autoBoxes, finishedBoxes / (double) totalCount * 100f,
        autoBoxes / (double) totalCount * 100f, maxBoxSize, deepDirtyBoxes ) );
  }

  protected void count( final RenderNode node ) {
    totalCount += 1;
    if ( node.getNodeType() == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE ) {
      finishedBoxes += 1;
    }
    if ( node.getElementType() instanceof AutoLayoutBoxType ) {
      autoBoxes += 1;
    }
    if ( node.getCacheState() == RenderNode.CacheState.DEEP_DIRTY ) {
      deepDirtyBoxes += 1;
    }
    maxBoxSize = Math.max( maxBoxSize, node.getChildCount() );
  }

  protected boolean startBox( final RenderBox box ) {
    count( box );
    return true;
  }

  protected void processBoxChilds( final RenderBox box ) {
    if ( box.getNodeType() == LayoutNodeTypes.TYPE_BOX_PARAGRAPH ) {
      final ParagraphRenderBox paragraphRenderBox = (ParagraphRenderBox) box;
      processBoxChilds( paragraphRenderBox.getPool() );
    } else {
      super.processBoxChilds( box );
    }
  }

  protected void processOtherNode( final RenderNode node ) {
    count( node );
  }
}
