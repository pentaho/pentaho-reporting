/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.designer.core.editor.crosstab;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.CrosstabCell;
import org.pentaho.reporting.engine.classic.core.CrosstabCellBody;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
import org.pentaho.reporting.engine.classic.core.DetailsHeader;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.GroupDataBody;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.SubGroupBody;
import org.pentaho.reporting.engine.classic.core.dom.AndMatcher;
import org.pentaho.reporting.engine.classic.core.dom.AttributeMatcher;
import org.pentaho.reporting.engine.classic.core.dom.ElementMatcher;
import org.pentaho.reporting.engine.classic.core.dom.MatcherContext;
import org.pentaho.reporting.engine.classic.core.dom.NodeMatcher;
import org.pentaho.reporting.engine.classic.core.dom.ReportStructureMatcher;
import org.pentaho.reporting.engine.classic.core.elementfactory.CrosstabBuilder;
import org.pentaho.reporting.engine.classic.core.elementfactory.CrosstabDetail;
import org.pentaho.reporting.engine.classic.core.elementfactory.CrosstabDimension;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.function.AggregationFunction;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

public final class CrosstabEditSupport {
  public static class EditGroupOnReportUndoEntry implements UndoEntry {
    private static final long serialVersionUID = -6048384734272767240L;
    private Group newRootGroup;
    private Group oldRootGroup;

    public EditGroupOnReportUndoEntry( final Group oldRootGroup, final Group newRootGroup ) {
      this.oldRootGroup = oldRootGroup.derive( true );
      this.newRootGroup = newRootGroup.derive( true );
    }

    public void undo( final ReportDocumentContext renderContext ) {
      final AbstractReportDefinition report = renderContext.getReportDefinition();
      report.setRootGroup( oldRootGroup.derive( true ) );
    }

    public void redo( final ReportDocumentContext renderContext ) {
      final AbstractReportDefinition report = renderContext.getReportDefinition();
      report.setRootGroup( newRootGroup.derive( true ) );
    }

    public UndoEntry merge( final UndoEntry newEntry ) {
      return null;
    }
  }

  public static class EditGroupOnGroupUndoEntry implements UndoEntry {
    private InstanceID target;
    private Group newRootGroup;
    private Group oldRootGroup;

    public EditGroupOnGroupUndoEntry( final InstanceID target,
                                      final Group oldRootGroup,
                                      final Group newRootGroup ) {
      ArgumentNullException.validate( "target", target );
      ArgumentNullException.validate( "oldRootGroup", oldRootGroup );
      ArgumentNullException.validate( "newRootGroup", newRootGroup );

      this.target = target;
      this.oldRootGroup = oldRootGroup.derive( true );
      this.newRootGroup = newRootGroup.derive( true );
    }

    public void undo( final ReportDocumentContext renderContext ) {
      final SubGroupBody bodyElement = (SubGroupBody)
        ModelUtility.findElementById( renderContext.getReportDefinition(), target );
      if ( bodyElement == null ) {
        throw new IllegalStateException( "Expected to find a sub-group-body on the specified ID." );
      }

      bodyElement.setGroup( oldRootGroup.derive( true ) );
    }

    public void redo( final ReportDocumentContext renderContext ) {
      final SubGroupBody bodyElement = (SubGroupBody)
        ModelUtility.findElementById( renderContext.getReportDefinition(), target );

      if ( bodyElement == null ) {
        throw new IllegalStateException( "Expected to find a sub-group-body on this report." );
      }

      bodyElement.setGroup( newRootGroup.derive( true ) );
    }

    public UndoEntry merge( final UndoEntry newEntry ) {
      return null;
    }
  }

  public static class DetailsDefinition {
    private Element labelElement;
    private Element detailElement;
    private String field;
    private Class<AggregationFunction> aggregationFunction;

    private DetailsDefinition( final Element labelElement,
                               final Element detailElement,
                               final String field,
                               final Class<AggregationFunction> aggregationFunction ) {
      this.labelElement = labelElement;
      this.detailElement = detailElement;
      this.field = field;
      this.aggregationFunction = aggregationFunction;
    }

    public Element getLabelElement() {
      return labelElement;
    }

    public Element getDetailElement() {
      return detailElement;
    }

    public String getField() {
      return field;
    }

    public Class<AggregationFunction> getAggregationFunction() {
      return aggregationFunction;
    }

    public CrosstabDetail createDetail() {
      final String label =
        (String) labelElement.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE );
      return new CrosstabDetail( field, label, aggregationFunction );
    }
  }

  private CrosstabEditSupport() {
  }

  public static GroupDataBody installCrosstabIntoLastGroup( final RelationalGroup selectedGroup,
                                                            final CrosstabGroup newGroup ) {
    final GroupDataBody oldBody = (GroupDataBody) selectedGroup.getBody();
    // install the new crosstab group into the group
    selectedGroup.setBody( new SubGroupBody( newGroup ) );
    return oldBody;
  }

  private static void populateOptions( final LinkedHashMap<String, DetailsDefinition> cellBody,
                                       final CrosstabBuilder builder ) {
    builder.setMaximumHeight( null );
    builder.setMaximumWidth( null );
    builder.setPrefHeight( null );
    builder.setPrefWidth( null );
    builder.setMinimumHeight( new Float( -100 ) );
    builder.setMinimumWidth( new Float( -100 ) );

    Collection<DetailsDefinition> values = cellBody.values();
    Boolean allowMetaAttrs = null;
    Boolean allowMetaStyle = null;

    for ( final DetailsDefinition value : values ) {
      Element detailElement = value.getDetailElement();
      if ( detailElement != null ) {
        if ( allowMetaAttrs == null ) {
          allowMetaAttrs = detailElement.getAttributeTyped( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_ATTRIBUTES, Boolean.class );
        }

        if ( allowMetaStyle == null ) {
          allowMetaStyle = detailElement.getAttributeTyped( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_STYLING, Boolean.class );
        }
      }
    }

    builder.setAllowMetaDataAttributes( allowMetaAttrs );
    builder.setAllowMetaDataStyling( allowMetaStyle );

    // todo: v2 - try to restore the active settings for width and heights
  }

  public static CrosstabBuilder populateBuilder( final CrosstabGroup editedGroup,
                                                 final ContextAwareDataSchemaModel reportDataSchemaModel ) {
    CrosstabCellBody cellBody = null;
    Group group = editedGroup.getBody().getGroup();

    ArrayList<CrosstabRowGroup> rows = new ArrayList<CrosstabRowGroup>();
    ArrayList<CrosstabColumnGroup> cols = new ArrayList<CrosstabColumnGroup>();
    ArrayList<CrosstabOtherGroup> others = new ArrayList<CrosstabOtherGroup>();

    while ( group != null ) {
      if ( group instanceof CrosstabOtherGroup ) {
        CrosstabOtherGroup otherGroup = (CrosstabOtherGroup) group;
        others.add( otherGroup );
      } else if ( group instanceof CrosstabRowGroup ) {
        CrosstabRowGroup rowGroup = (CrosstabRowGroup) group;
        rows.add( rowGroup );
      } else if ( group instanceof CrosstabColumnGroup ) {
        CrosstabColumnGroup colGroup = (CrosstabColumnGroup) group;
        cols.add( colGroup );
      } else {
        break;
      }

      GroupBody body = group.getBody();
      if ( body instanceof CrosstabCellBody ) {
        cellBody = (CrosstabCellBody) body;
        break;
      }

      group = body.getGroup();
    }

    if ( cellBody == null ) {
      throw new IllegalStateException( "A crosstab group can never be without a cell body" );
    }

    LinkedHashMap<String, DetailsDefinition> details;
    CrosstabCell element = cellBody.findElement( null, null );
    if ( element != null ) {
      details = extractFromDetailCell( element, cellBody.getHeader() );
    } else {
      details = new LinkedHashMap<String, DetailsDefinition>();
    }

    final CrosstabEditorBuilder builder = new CrosstabEditorBuilder( reportDataSchemaModel, cellBody, details );
    populateOptions( details, builder );
    for ( final CrosstabOtherGroup other : others ) {
      builder.addOtherDimension( other );
    }
    for ( final CrosstabRowGroup row : rows ) {
      builder.addRowDimension( extractFromRowGroup( row ), row );
    }
    for ( final CrosstabColumnGroup col : cols ) {
      builder.addColumnDimension( extractFromColumnGroup( col ), col );
    }
    for ( final DetailsDefinition value : details.values() ) {
      builder.addDetails( value.createDetail() );
    }
    return builder;
  }

  private static LinkedHashMap<String, DetailsDefinition> extractFromDetailCell( final CrosstabCell cell,
                                                                                 final DetailsHeader header ) {
    ReportElement[] elementsByAttribute =
      ReportStructureMatcher.findElementsByAttribute( cell, AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD );
    final LinkedHashMap<String, DetailsDefinition> d = new LinkedHashMap<String, DetailsDefinition>();
    for ( final ReportElement e : elementsByAttribute ) {
      String field = (String) e.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD );
      Class agg = (Class) e.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.AGGREGATION_TYPE );
      if ( ( agg != null ) && !AggregationFunction.class.isAssignableFrom( agg ) ) {
        agg = null;
      }
      ReportElement[] labels = ReportStructureMatcher.findElementsByAttribute( header, AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.LABEL_FOR, field );
      Element label;
      if ( labels.length > 0 ) {
        label = (Element) labels[ 0 ];
      } else {
        label = null;
      }
      d.put( field, new DetailsDefinition( label, (Element) e, field, agg ) );
    }
    return d;
  }

  private static CrosstabDimension extractFromRowGroup( final CrosstabRowGroup rowGroup ) {
    final String title = findTitle( rowGroup.getField(), rowGroup.getTitleHeader() );
    final String summaryTitle = findTitle( rowGroup.getField(), rowGroup.getSummaryHeader() );
    final boolean summary = rowGroup.isPrintSummary();
    return new CrosstabDimension( rowGroup.getField(), title, summary, summaryTitle );
  }

  private static String findTitle( final String field, final Band titleHeader ) {
    final MatcherContext context = new MatcherContext();
    context.setMatchSubReportChilds( false );

    NodeMatcher m = new AndMatcher( new ElementMatcher( LabelType.INSTANCE ),
      new AttributeMatcher( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.LABEL_FOR, field ) );
    ReportElement match = ReportStructureMatcher.match( context, titleHeader, m );
    if ( match == null ) {
      if ( titleHeader.getElementCount() > 0 ) {
        Element e = titleHeader.getElement( 0 );
        if ( e.getElementType() instanceof LabelType ) {
          return e.getAttributeTyped( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, String.class );
        }
      }
      return null;
    }

    return match.getAttributeTyped( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, String.class );
  }

  private static CrosstabDimension extractFromColumnGroup( final CrosstabColumnGroup rowGroup ) {
    final String title = findTitle( rowGroup.getField(), rowGroup.getTitleHeader() );
    final String summaryTitle = findTitle( rowGroup.getField(), rowGroup.getSummaryHeader() );
    final boolean summary = rowGroup.isPrintSummary();
    return new CrosstabDimension( rowGroup.getField(), title, summary, summaryTitle );
  }
}
