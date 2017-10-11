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

package org.pentaho.reporting.engine.classic.core.states.crosstab;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabNormalizationMode;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroupBody;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubGroupBody;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;
import org.pentaho.reporting.engine.classic.core.states.ReportState;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.libraries.base.util.FastStack;

import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * Computes the column-axis values for all crosstabs in the current report.
 *
 * @author Thomas Morgner
 */
public class CrosstabProcessorFunction extends AbstractFunction implements StructureFunction {
  private FastStack<CrosstabSpecification> processingStack;
  private CrosstabSpecification result;

  public CrosstabProcessorFunction() {
  }

  public int getProcessingPriority() {
    return Short.MIN_VALUE;
  }

  /**
   * Receives notification that a group has started.
   *
   * @param event
   *          the event.
   */
  public void groupStarted( final ReportEvent event ) {
    final ReportState state = event.getState();
    if ( event.getLevel() == getDependencyLevel() ) {
      final Group group = event.getReport().getGroup( state.getCurrentGroupIndex() );
      if ( group instanceof CrosstabGroup ) {
        final CrosstabGroup crosstabGroup = (CrosstabGroup) group;
        // yeay! we encountered a crosstab.
        if ( processingStack == null ) {
          processingStack = new FastStack<CrosstabSpecification>();
        }
        final String[] columnSet = computeColumns( crosstabGroup );
        final String[] rowSet = computeRows( crosstabGroup );
        final ReportStateKey processKey = state.getProcessKey();

        final CrosstabNormalizationMode normalizationMode =
            (CrosstabNormalizationMode) group.getAttribute( AttributeNames.Crosstab.NAMESPACE,
                AttributeNames.Crosstab.NORMALIZATION_MODE );
        if ( CrosstabNormalizationMode.Insertation.equals( normalizationMode ) ) {
          processingStack.push( new OrderedMergeCrosstabSpecification( processKey, columnSet, rowSet ) );
        } else {
          processingStack.push( new SortedMergeCrosstabSpecification( processKey, columnSet, rowSet ) );
        }
        return;
      }

      if ( processingStack == null || processingStack.isEmpty() ) {
        return;
      }

      final CrosstabSpecification csstate = processingStack.peek();
      if ( csstate == null ) {
        return;
      }

      if ( group instanceof CrosstabRowGroup ) {
        if ( group.getBody() instanceof CrosstabColumnGroupBody ) {
          // fire this only for the inner-most row-group.
          csstate.startRow();
        }
      }
    }
  }

  /**
   * Receives notification that a group has finished.
   *
   * @param event
   *          the event.
   */
  public void groupFinished( final ReportEvent event ) {
    if ( processingStack == null || processingStack.isEmpty() ) {
      return;
    }

    final CrosstabSpecification csstate = processingStack.peek();
    if ( csstate == null ) {
      return;
    }

    final ReportState state = event.getState();
    if ( event.getLevel() == getDependencyLevel() ) {
      final Group group = event.getReport().getGroup( state.getCurrentGroupIndex() );
      if ( group instanceof CrosstabGroup ) {
        csstate.endCrosstab();
        result = processingStack.pop();
        return;
      }

      if ( group instanceof CrosstabRowGroup ) {
        if ( group.getBody() instanceof CrosstabColumnGroupBody ) {
          // fire this only for the inner-most row-group.
          csstate.endRow();
        }
      }
    }
  }

  private String[] computeColumns( final CrosstabGroup crosstabGroup ) {
    final HashSet<String> list = new HashSet<String>();
    GroupBody body = crosstabGroup.getBody();
    while ( body != null ) {
      if ( body instanceof SubGroupBody ) {
        final SubGroupBody sgBody = (SubGroupBody) body;
        final Group g = sgBody.getGroup();
        body = g.getBody();
        continue;
      }

      if ( body instanceof CrosstabOtherGroupBody ) {
        final CrosstabOtherGroupBody cogb = (CrosstabOtherGroupBody) body;
        final CrosstabOtherGroup otherGroup = cogb.getGroup();
        body = otherGroup.getBody();
        continue;
      }

      if ( body instanceof CrosstabRowGroupBody ) {
        final CrosstabRowGroupBody cogb = (CrosstabRowGroupBody) body;
        final CrosstabRowGroup otherGroup = cogb.getGroup();
        body = otherGroup.getBody();
        continue;
      }

      if ( body instanceof CrosstabColumnGroupBody ) {
        final CrosstabColumnGroupBody cogb = (CrosstabColumnGroupBody) body;
        final CrosstabColumnGroup otherGroup = cogb.getGroup();
        if ( otherGroup.getField() != null ) {
          list.add( otherGroup.getField() );
        }
        body = otherGroup.getBody();
        continue;
      }

      break;
    }
    return list.toArray( new String[list.size()] );
  }

  private String[] computeRows( final CrosstabGroup crosstabGroup ) {
    final LinkedHashSet<String> list = new LinkedHashSet<String>();
    list.addAll( crosstabGroup.getPaddingFields() );
    collectRelationalFields( crosstabGroup.getParentSection(), list );
    collectCrosstabFields( crosstabGroup, list );
    return list.toArray( new String[list.size()] );
  }

  private void collectRelationalFields( Section section, final HashSet<String> list ) {
    while ( section != null ) {
      if ( section instanceof AbstractReportDefinition ) {
        return;
      }
      if ( section instanceof RelationalGroup ) {
        final RelationalGroup group = (RelationalGroup) section;
        list.addAll( group.getFields() );
      }
      section = section.getParentSection();
    }
  }

  private void collectCrosstabFields( final CrosstabGroup crosstabGroup, final HashSet<String> list ) {
    GroupBody body = crosstabGroup.getBody();
    while ( body != null ) {
      if ( body instanceof CrosstabOtherGroupBody ) {
        final CrosstabOtherGroupBody cogb = (CrosstabOtherGroupBody) body;
        final CrosstabOtherGroup otherGroup = cogb.getGroup();
        if ( otherGroup.getField() != null ) {
          list.add( otherGroup.getField() );
        }
        body = otherGroup.getBody();
        continue;
      }

      if ( body instanceof CrosstabRowGroupBody ) {
        final CrosstabRowGroupBody cogb = (CrosstabRowGroupBody) body;
        final CrosstabRowGroup otherGroup = cogb.getGroup();
        if ( otherGroup.getField() != null ) {
          list.add( otherGroup.getField() );
        }
        body = otherGroup.getBody();
        continue;
      }

      if ( body instanceof CrosstabColumnGroupBody ) {
        final CrosstabColumnGroupBody cogb = (CrosstabColumnGroupBody) body;
        final CrosstabColumnGroup otherGroup = cogb.getGroup();
        body = otherGroup.getBody();
        continue;
      }

      break;
    }
  }

  /**
   * Receives notification that a row of data is being processed.
   *
   * @param event
   *          the event.
   */
  public void itemsAdvanced( final ReportEvent event ) {
    if ( event.getLevel() == getDependencyLevel() ) {
      if ( processingStack == null || processingStack.isEmpty() ) {
        return;
      }
      final CrosstabSpecification state = processingStack.peek();
      if ( state == null ) {
        return;
      }
      // this may throw an InvalidReportStateException that ends the report processing.
      state.add( getDataRow() );
    }
  }

  /**
   * Return the current expression value.
   * <p/>
   * The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    return result;
  }

  /**
   * Returns the dependency level for the expression (controls evaluation order for expressions and functions).
   *
   * @return the level.
   */
  public int getDependencyLevel() {
    return LayoutProcess.LEVEL_STRUCTURAL_PREPROCESSING;
  }

  /**
   * Clones the expression. The expression should be reinitialized after the cloning.
   * <P>
   * Expressions maintain no state, cloning is done at the beginning of the report processing to disconnect the
   * expression from any other object space.
   *
   * @return a clone of this expression.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public Object clone() throws CloneNotSupportedException {
    final CrosstabProcessorFunction cps = (CrosstabProcessorFunction) super.clone();
    if ( processingStack == null || processingStack.isEmpty() ) {
      return cps;
    }
    cps.processingStack = processingStack.clone();
    return cps;
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final CrosstabProcessorFunction cps = (CrosstabProcessorFunction) super.getInstance();
    cps.result = null;
    cps.processingStack = null;
    return cps;

  }
}
