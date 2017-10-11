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

import org.pentaho.reporting.engine.classic.core.layout.model.FinishedRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.process.util.OrphanContext;
import org.pentaho.reporting.engine.classic.core.layout.process.util.OrphanContextPool;
import org.pentaho.reporting.engine.classic.core.layout.process.util.OrphanPassThroughContext;

/**
 * Computes break positions that prevent Orphan and Widow elements, according to the definitions on the boxes
 * themselves.
 * <p/>
 * An Orphan is an element pushed on its own page, with all other elements on the previous pages. This is commonly found
 * in groups where the group-footer is pushed to the next page.
 * <p/>
 * An Widow is an element left on the current page, where all other elements are pushed to the next page. This is
 * commonly found for group-headers, where the group-body is pushed to the next page.
 * <p/>
 * This step calculates the minimum required space that an element would consume if it honours the widow and orphan
 * rules.
 * <p/>
 * When computing the rules, all children are considered, as long as they do not opt-out of the processing. A box that
 * opts out, has the 'widow-orphan-opt-out' flag set to true. In the simple set of rules, only block-level elements are
 * considered to opt-in for widow and orphan processing.
 * <p/>
 * For orphans, this step computes the minimum space the element requires to be safely placed on this page. If the
 * elements occupying that space would trigger a manual page-break, the break overrides the orphan rule, and the space
 * for the orphan processing is limited to the point of the manual break.
 * <p/>
 * For widows, this step also computes the minimum space required to satisfy the constraint. Manual breaks override the
 * widow constraint. During pagination, the pagination processor has to check all parents to see whether their widow
 * constrains are still fulfilled.
 * <p/>
 * If the sum of the widow and orphan constraints is larger than the computed size of the box, the box is considered
 * unbreakable and behaves as if the "keep-together" flag has been set.
 * <p/>
 * The widow-orphan calculation ignores the 'fixed-position' setting when calculating constraints. Combining a
 * widow-orphan constraint with the fixed-position constrained yields undefined results. The widow and orphan constraint
 * is only active for paginated reports. It has no effect on flow or streaming report outputs.
 */
public class OrphanStep extends IterateSimpleStructureProcessStep {
  private OrphanContext context;
  private OrphanContextPool contextPool;
  private OrphanPassThroughContext rootContext;
  private boolean invalidNodeFound;

  public OrphanStep() {
    contextPool = new OrphanContextPool();
    rootContext = new OrphanPassThroughContext();
  }

  public boolean processOrphanAnnotation( final LogicalPageBox box ) {
    invalidNodeFound = false;
    context = rootContext;
    startProcessing( box.getContentArea() );
    context = null;
    return invalidNodeFound;
  }

  protected void processParagraphChilds( final ParagraphRenderBox box ) {
    processBoxChilds( box );
  }

  protected boolean startBox( final RenderBox box ) {
    box.setInvalidWidowOrphanNode( false );
    box.setRestrictFinishedClearOut( RenderBox.RestrictFinishClearOut.UNRESTRICTED );

    final StaticBoxLayoutProperties properties = box.getStaticBoxLayoutProperties();
    if ( properties.isWidowOrphanOptOut() == false ) {
      context.startChild( box );
    }

    if ( box.getNodeType() == LayoutNodeTypes.TYPE_BOX_BREAKMARK ) {
      context.registerBreakMark( box );
    }

    context = contextPool.create( box, context );
    return true;
  }

  protected void processOtherNode( final RenderNode node ) {
    if ( node instanceof FinishedRenderNode ) {
      final FinishedRenderNode finNode = (FinishedRenderNode) node;
      if ( finNode.isOrphanLeaf() ) {
        context.registerFinishedNode( finNode );
        // feed information about the collapsed node into the parent to have a consistent pagination run.
      }
    }
  }

  protected void finishBox( final RenderBox box ) {
    final OrphanContext oldContext = context;
    context = oldContext.commit( box );
    contextPool.free( oldContext );

    if ( box.isInvalidWidowOrphanNode() ) {
      invalidNodeFound = true;
    }

    final StaticBoxLayoutProperties properties = box.getStaticBoxLayoutProperties();
    if ( properties.isWidowOrphanOptOut() == false ) {
      context.endChild( box );
    }
  }

}
