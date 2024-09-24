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

package org.pentaho.reporting.designer.core.util.dnd;

import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.report.RootBandRenderComponent;
import org.pentaho.reporting.designer.core.editor.structuretree.ReportFunctionNode;
import org.pentaho.reporting.designer.core.editor.structuretree.ReportParametersNode;
import org.pentaho.reporting.designer.core.editor.structuretree.SubReportParametersNode;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.BandedSubreportEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.DataSourceEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.ElementEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.ExpressionRemoveUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.ParameterEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.SectionEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AbstractRootLevelBand;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.CrosstabCell;
import org.pentaho.reporting.engine.classic.core.CrosstabCellBody;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DetailsFooter;
import org.pentaho.reporting.engine.classic.core.DetailsHeader;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.GroupDataBody;
import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.NoDataBand;
import org.pentaho.reporting.engine.classic.core.PageFooter;
import org.pentaho.reporting.engine.classic.core.PageHeader;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ReportFooter;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubGroupBody;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.Watermark;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.BandType;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.ExpressionCollection;
import org.pentaho.reporting.engine.classic.core.parameters.ModifiableReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;

import javax.swing.FocusManager;
import java.awt.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class InsertationUtil {
  public static final String SUBREPORT_BANDED_HINT = "subreport-banded-hint";

  private InsertationUtil() {
  }

  private static final Object[] EMPTY_ARRAY = new Object[ 0 ];

  public static Object[] getFromClipboard() {
    try {
      final Object[] data = ClipboardManager.getManager().getContents();
      if ( data == null ) {
        return EMPTY_ARRAY;
      }
      return data;
    } catch ( final UnsupportedFlavorException e1 ) {
      UncaughtExceptionsModel.getInstance().addException( e1 );
      return EMPTY_ARRAY;
    } catch ( final IOException e1 ) {
      UncaughtExceptionsModel.getInstance().addException( e1 );
      return EMPTY_ARRAY;
    }
  }

  public static boolean isInsertAllowed( final Object target, final Object data ) {
    if ( target instanceof SubReport ||
      target instanceof MasterReport ) {
      return ( data instanceof Group ||
        data instanceof ReportHeader ||
        data instanceof ReportFooter ||
        data instanceof PageHeader ||
        data instanceof PageFooter );
    }
    if ( target instanceof Group ) {
      return ( data instanceof Group ||
        data instanceof GroupBody ||
        data instanceof GroupHeader ||
        data instanceof GroupFooter );
    }
    if ( target instanceof SubGroupBody ) {
      return ( data instanceof Group );
    }
    if ( target instanceof GroupDataBody ) {
      return ( data instanceof DetailsHeader ||
        data instanceof DetailsFooter ||
        data instanceof NoDataBand ||
        data instanceof ItemBand );
    }
    if ( target instanceof Band ) {
      if ( data instanceof AbstractReportDefinition ) {
        Band b = (Band) target;
        while ( b != null ) {
          if ( b instanceof PageHeader || b instanceof PageFooter ||
            b instanceof DetailsHeader || b instanceof DetailsFooter ||
            b instanceof Watermark ) {
            return false;
          }
          b = b.getParent();
        }
      } else if ( data instanceof Group || data instanceof GroupBody ) {
        // exclude groups and group-body, they cannot be a child of bands.
        return false;
      }
      return ( data instanceof Element );
    }

    return ( data instanceof Expression ||
      data instanceof ParameterDefinitionEntry ||
      data instanceof DataFactory );
  }

  public static Object getInsertationPoint( final ReportDocumentContext renderContext ) {
    final Component owner = FocusManager.getCurrentManager().getPermanentFocusOwner();
    if ( owner instanceof RootBandRenderComponent == false ) {
      final Object o = renderContext.getSelectionModel().getLeadSelection();
      if ( o == null ) {
        return null;
      }
      if ( o instanceof Band ) {
        return o;
      }
      if ( o instanceof ReportElement ) {
        final ReportElement element = (ReportElement) o;
        final Section parentSection = element.getParentSection();
        if ( parentSection instanceof Band ) {
          return parentSection;
        }
      }
      if ( o instanceof CompoundDataFactory ) {
        return o;
      }
      if ( o instanceof ReportFunctionNode ) {
        return o;
      }
      if ( o instanceof ReportParametersNode ) {
        return o;
      }
      if ( o instanceof SubReportParametersNode ) {
        return o;
      }

      return null;
    }

    final RootBandRenderComponent rootBandRenderComponent = (RootBandRenderComponent) owner;
    final Band rootBand = rootBandRenderComponent.getRootBand();

    final DocumentContextSelectionModel selectionModel = renderContext.getSelectionModel();
    for ( final Element element : selectionModel.getSelectedElementsOfType( Element.class ) ) {
      if ( element instanceof Band && ModelUtility.isDescendant( rootBand, element ) ) {
        return element;
      }
    }
    return rootBand;
  }

  public static Object insert( final Object rawLeadSelection,
                               final AbstractReportDefinition report,
                               final Object fromClipboard ) {
    if ( fromClipboard instanceof Expression ) {
      return insertExpression( report, (Expression) fromClipboard );
    }

    if ( fromClipboard instanceof ParameterDefinitionEntry ) {
      return insertParameter( report, (ParameterDefinitionEntry) fromClipboard );
    }

    if ( fromClipboard instanceof DataFactory ) {
      return insertDataFactory( report, (DataFactory) fromClipboard );
    }

    if ( fromClipboard instanceof Element == false ) {
      return false;
    }

    final Element insert = (Element) fromClipboard;

    if ( rawLeadSelection instanceof Element == false ) {
      return false;
    }

    if ( rawLeadSelection instanceof Band ) {
      final Band target = (Band) rawLeadSelection;
      if ( target == insert ) {
        return false;
      }
      if ( ModelUtility.isDescendant( target, insert ) ) {
        return false;
      }
      if ( insert.getParent() != null ) {
        // this should not happen. We should only see clones here ..
        throw new IllegalStateException();
      }

      try {
        final Element element = normalizeForInsert( insert );
        if ( element == null ) {
          return false;
        }

        if ( element instanceof SubReport ) {
          final Object subreportHint =
            insert.getAttribute( ReportDesignerBoot.DESIGNER_NAMESPACE, SUBREPORT_BANDED_HINT );
          if ( Boolean.TRUE.equals( subreportHint ) ) {
            if ( target instanceof AbstractRootLevelBand ) {
              final AbstractRootLevelBand rlb = (AbstractRootLevelBand) target;
              rlb.addSubReport( (SubReport) element );
              return element;
            } else {
              return false;
            }
          }
        }

        target.addElement( element );
        return element;
      } catch ( CloneNotSupportedException e1 ) {
        UncaughtExceptionsModel.getInstance().addException( e1 );
        return false;
      }
    }

    if ( rawLeadSelection instanceof AbstractReportDefinition ) {
      final AbstractReportDefinition g = (AbstractReportDefinition) rawLeadSelection;
      if ( insert instanceof PageHeader ) {
        final PageHeader header = (PageHeader) insert.derive();
        g.setPageHeader( header );
        return header;
      }

      if ( insert instanceof PageFooter ) {
        final PageFooter footer = (PageFooter) insert.derive();
        g.setPageFooter( footer );
        return footer;
      }

      if ( insert instanceof ReportHeader ) {
        final ReportHeader header = (ReportHeader) insert.derive();
        g.setReportHeader( header );
        return header;
      }

      if ( insert instanceof ReportFooter ) {
        final ReportFooter footer = (ReportFooter) insert.derive();
        g.setReportFooter( footer );
        return footer;
      }
      if ( insert instanceof Group ) {
        final Group group = (Group) insert.derive();
        g.setRootGroup( group );
        return group;
      }
      return null;
    }

    if ( rawLeadSelection instanceof RelationalGroup ) {
      final RelationalGroup g = (RelationalGroup) rawLeadSelection;
      if ( insert instanceof GroupHeader ) {
        final GroupHeader header = (GroupHeader) insert.derive();
        g.setHeader( header );
        return header;
      }

      if ( insert instanceof GroupFooter ) {
        final GroupFooter footer = (GroupFooter) insert.derive();
        g.setFooter( footer );
        return footer;
      }

      if ( insert instanceof GroupBody ) {
        final GroupBody body = (GroupBody) insert.derive();
        try {
          g.setBody( body );
          return body;
        } catch ( final Exception e ) {
          return null;
        }
      }
      return null;
    }

    if ( rawLeadSelection instanceof SubGroupBody ) {
      final SubGroupBody body = (SubGroupBody) rawLeadSelection;
      if ( insert instanceof RelationalGroup || insert instanceof CrosstabGroup ) {
        try {
          final Group group = (Group) insert.derive();
          body.setGroup( group );
          return group;
        } catch ( Exception cne ) {
          UncaughtExceptionsModel.getInstance().addException( cne );
          return null;
        }
      }
      return null;
    }

    if ( rawLeadSelection instanceof GroupDataBody ) {
      final GroupDataBody body = (GroupDataBody) rawLeadSelection;
      if ( insert instanceof DetailsHeader ) {
        final DetailsHeader detailsHeader = (DetailsHeader) insert.derive();
        body.setDetailsHeader( detailsHeader );
        return detailsHeader;
      }
      if ( insert instanceof DetailsFooter ) {
        final DetailsFooter footer = (DetailsFooter) insert.derive();
        body.setDetailsFooter( footer );
        return footer;
      }
      if ( insert instanceof ItemBand ) {
        final ItemBand itemBand = (ItemBand) insert.derive();
        body.setItemBand( itemBand );
        return itemBand;
      }
      if ( insert instanceof NoDataBand ) {
        final NoDataBand noDataBand = (NoDataBand) insert.derive();
        body.setNoDataBand( noDataBand );
        return noDataBand;
      }
      return null;
    }

    if ( rawLeadSelection instanceof CrosstabCellBody ) {
      final CrosstabCellBody body = (CrosstabCellBody) rawLeadSelection;
      if ( insert instanceof CrosstabCell ) {
        final CrosstabCell crosstabCell = (CrosstabCell) insert.derive();
        body.addElement( crosstabCell );
        return crosstabCell;
      }
    }

    return null;
  }

  private static Object insertDataFactory( final AbstractReportDefinition report, final DataFactory fromClipboard ) {
    final CompoundDataFactory element = (CompoundDataFactory) report.getDataFactory();
    final DataFactory df = (DataFactory) fromClipboard;
    final DataFactory dataFactory = df.derive();
    element.add( dataFactory );
    report.notifyNodeChildAdded( dataFactory );
    return dataFactory;
  }

  private static Object insertParameter( final AbstractReportDefinition report,
                                         final ParameterDefinitionEntry fromClipboard ) {
    if ( ( report instanceof MasterReport ) == false ) {
      return null;
    }

    final MasterReport masterReportElement = (MasterReport) report;
    final ParameterDefinitionEntry pe = (ParameterDefinitionEntry) fromClipboard;
    try {
      final ModifiableReportParameterDefinition definition =
        (ModifiableReportParameterDefinition) masterReportElement.getParameterDefinition();
      final ParameterDefinitionEntry definitionEntry = (ParameterDefinitionEntry) pe.clone();
      definition.addParameterDefinition( definitionEntry );
      report.notifyNodeChildAdded( definitionEntry );
      return definitionEntry;
    } catch ( CloneNotSupportedException e1 ) {
      // ignore ..
      UncaughtExceptionsModel.getInstance().addException( e1 );
      return null;
    }
  }

  private static Object insertExpression( final AbstractReportDefinition report, final Expression fromClipboard ) {
    final Expression expression = (Expression) fromClipboard;
    final Expression instance = expression.getInstance();
    report.addExpression( instance );
    report.notifyNodeChildAdded( instance );
    return instance;
  }

  private static Element normalizeForInsert( final Element insert ) throws CloneNotSupportedException {
    if ( insert instanceof Section == false ) {
      return insert.derive();
    }
    if ( insert instanceof SubReport ) {
      return insert.derive();
    }

    if ( insert instanceof Band ) {
      final Band band = (Band) insert;
      if ( insert instanceof RootLevelBand == false ) {
        return band.derive();
      }
      final Band newBand = new Band();
      band.copyInto( newBand );
      band.setElementType( BandType.INSTANCE );
      return newBand;
    }

    return null;
  }

  public static UndoEntry delete( final ReportDocumentContext context, final Object data ) {
    if ( data == context.getReportDefinition() ) {
      // we never delete the root element.
      return null;
    }

    if ( data instanceof ParameterDefinitionEntry ) {
      return deleteParameter( context, data );
    }

    if ( data instanceof Expression ) {
      return deleteExpression( context, data );
    }

    if ( data instanceof DataFactory ) {
      return deleteDataFactory( context, data );
    }

    if ( data instanceof MasterReport ) {
      // cannot remove the master report. But we can copy it.
      return null;
    }

    if ( data instanceof GroupBody ) {
      return deleteGroupBody( (GroupBody) data );
    }

    if ( data instanceof Group ) {
      return deleteGroup( (Group) data );

    }

    if ( data instanceof Element == false ) {
      return null;
    }

    final Element veElement = (Element) data;
    final Section parent = veElement.getParentSection();
    if ( data instanceof SubReport && parent instanceof AbstractRootLevelBand ) {
      final AbstractRootLevelBand re = (AbstractRootLevelBand) parent;
      final SubReport report = (SubReport) data;
      final int index = ModelUtility.findSubreportIndexOf( re, report );
      if ( index != -1 ) {
        re.removeSubreport( report );
      }
      if ( veElement.getParent() == null ) {
        // remove was a success ...
        return new BandedSubreportEditUndoEntry( re.getObjectID(), index, report, null );
      }
    }

    if ( parent instanceof Band ) {
      final Band band = (Band) parent;
      final int index = ModelUtility.findIndexOf( band, veElement );
      band.removeElement( veElement );
      return new ElementEditUndoEntry( band.getObjectID(), index, veElement, null );
    }

    if ( data instanceof GroupHeader ) {
      final RelationalGroup g = (RelationalGroup) parent;
      final GroupHeader oldHeader = g.getHeader();
      final GroupHeader newHeader = new GroupHeader();
      g.setHeader( newHeader );
      return new SectionEditUndoEntry( g.getObjectID(), ModelUtility.findIndexOf( g, newHeader ), oldHeader,
        newHeader );
    }

    if ( data instanceof GroupFooter ) {
      final RelationalGroup g = (RelationalGroup) parent;
      final GroupFooter oldFooter = g.getFooter();
      final GroupFooter newFooter = new GroupFooter();
      g.setFooter( newFooter );
      return new SectionEditUndoEntry( g.getObjectID(), ModelUtility.findIndexOf( g, newFooter ), oldFooter,
        newFooter );
    }
    if ( data instanceof ReportFooter ) {
      final AbstractReportDefinition g = (AbstractReportDefinition) parent;
      final ReportFooter oldFooter = g.getReportFooter();
      final ReportFooter newFooter = new ReportFooter();
      g.setReportFooter( newFooter );
      return new SectionEditUndoEntry( g.getObjectID(), ModelUtility.findIndexOf( g, newFooter ), oldFooter,
        newFooter );
    }
    if ( data instanceof ReportHeader ) {
      final AbstractReportDefinition g = (AbstractReportDefinition) parent;
      final ReportHeader oldHeader = g.getReportHeader();
      final ReportHeader newHeader = new ReportHeader();
      g.setReportHeader( newHeader );
      return new SectionEditUndoEntry( g.getObjectID(), ModelUtility.findIndexOf( g, newHeader ), oldHeader,
        newHeader );
    }
    if ( data instanceof PageHeader ) {
      final AbstractReportDefinition g = (AbstractReportDefinition) parent;
      final PageHeader oldHeader = g.getPageHeader();
      final PageHeader newHeader = new PageHeader();
      g.setPageHeader( newHeader );
      return new SectionEditUndoEntry( g.getObjectID(), ModelUtility.findIndexOf( g, newHeader ), oldHeader,
        newHeader );
    }
    if ( data instanceof PageFooter ) {
      final AbstractReportDefinition g = (AbstractReportDefinition) parent;
      final PageFooter oldFooter = g.getPageFooter();
      final PageFooter newFooter = new PageFooter();
      g.setPageFooter( newFooter );
      return new SectionEditUndoEntry( g.getObjectID(), ModelUtility.findIndexOf( g, newFooter ), oldFooter,
        newFooter );
    }

    if ( data instanceof ItemBand ) {
      final GroupDataBody g = (GroupDataBody) parent;
      final ItemBand oldBand = g.getItemBand();
      final ItemBand newBand = new ItemBand();
      g.setItemBand( newBand );
      return new SectionEditUndoEntry( g.getObjectID(), ModelUtility.findIndexOf( g, newBand ), oldBand, newBand );
    }
    if ( data instanceof NoDataBand ) {
      final GroupDataBody g = (GroupDataBody) parent;
      final NoDataBand oldBand = g.getNoDataBand();
      final NoDataBand newBand = new NoDataBand();
      g.setNoDataBand( newBand );
      return new SectionEditUndoEntry( g.getObjectID(), ModelUtility.findIndexOf( g, newBand ), oldBand, newBand );
    }
    if ( data instanceof DetailsHeader ) {
      final GroupDataBody g = (GroupDataBody) parent;
      final DetailsHeader oldHeader = g.getDetailsHeader();
      final DetailsHeader newHeader = new DetailsHeader();
      g.setDetailsHeader( newHeader );
      return new SectionEditUndoEntry( g.getObjectID(), ModelUtility.findIndexOf( g, newHeader ), oldHeader,
        newHeader );
    }
    if ( data instanceof DetailsFooter ) {
      final GroupDataBody g = (GroupDataBody) parent;
      final DetailsFooter oldFooter = g.getDetailsFooter();
      final DetailsFooter newFooter = new DetailsFooter();
      g.setDetailsFooter( newFooter );
      return new SectionEditUndoEntry( g.getObjectID(), ModelUtility.findIndexOf( g, newFooter ), oldFooter,
        newFooter );
    }
    return null;

  }

  private static UndoEntry deleteGroupBody( final GroupBody data ) {
    final GroupBody subgroup = (GroupBody) data;
    final RelationalGroup parent = (RelationalGroup) subgroup.getParentSection();
    if ( parent != null ) {
      final GroupBody body = parent.getBody();
      final GroupDataBody newBody = new GroupDataBody();
      parent.setBody( newBody );
      return new SectionEditUndoEntry
        ( parent.getObjectID(), ModelUtility.findIndexOf( parent, newBody ), body, newBody );
    }
    return null;
  }

  private static UndoEntry deleteDataFactory( final ReportDocumentContext context, final Object data ) {
    final AbstractReportDefinition report = context.getReportDefinition();
    // should be safe. If not, then the report-open functionality is wrong.
    final CompoundDataFactory dataFactory = (CompoundDataFactory) report.getDataFactory();
    final int count = dataFactory.size();
    for ( int i = 0; i < count; i++ ) {
      final DataFactory df = dataFactory.getReference( i );
      if ( df == data ) {
        dataFactory.remove( i );
        report.notifyNodeChildRemoved( df );
        return new DataSourceEditUndoEntry( i, df, null );
      }
    }
    return null;
  }

  private static UndoEntry deleteExpression( final ReportDocumentContext context, final Object data ) {
    final AbstractReportDefinition report = context.getReportDefinition();
    final ExpressionCollection expressionCollection = report.getExpressions();
    final int count = expressionCollection.size();
    for ( int i = 0; i < count; i++ ) {
      final Expression definitionEntry = expressionCollection.getExpression( i );
      if ( definitionEntry == data ) {
        expressionCollection.removeExpression( i );
        report.notifyNodeChildRemoved( definitionEntry );
        return new ExpressionRemoveUndoEntry( i, definitionEntry );
      }
    }
    return null;
  }

  private static UndoEntry deleteParameter( final ReportDocumentContext context, final Object data ) {
    final AbstractReportDefinition report = context.getReportDefinition();
    if ( report instanceof MasterReport == false ) {
      return null;
    }

    final MasterReport mreport = (MasterReport) report;
    final ReportParameterDefinition definition = mreport.getParameterDefinition();
    if ( definition instanceof ModifiableReportParameterDefinition == false ) {
      return null;
    }

    final ModifiableReportParameterDefinition mdef = (ModifiableReportParameterDefinition) definition;
    final int count = mdef.getParameterCount();
    for ( int i = 0; i < count; i++ ) {
      final ParameterDefinitionEntry definitionEntry = mdef.getParameterDefinition( i );
      if ( definitionEntry == data ) {
        mdef.removeParameterDefinition( i );
        report.notifyNodeChildRemoved( definitionEntry );
        return new ParameterEditUndoEntry( i, definitionEntry, null );
      }
    }
    return null;
  }

  private static UndoEntry deleteGroup( final Group groupElement ) {
    // deleting this group means, that the body moves down one level.
    final Section parent = groupElement.getParentSection();
    if ( parent instanceof SubGroupBody ) {
      final SubGroupBody parentBody = (SubGroupBody) groupElement.getParentSection();
      final Group oldGroup = parentBody.getGroup();
      final RelationalGroup newRootGroup = new RelationalGroup();
      parentBody.setGroup( newRootGroup );
      return new SectionEditUndoEntry
        ( parentBody.getObjectID(), ModelUtility.findIndexOf( parentBody, newRootGroup ), oldGroup, newRootGroup );
    }
    if ( parent instanceof AbstractReportDefinition ) {
      final AbstractReportDefinition report = (AbstractReportDefinition) parent;
      final Group oldGroup = report.getRootGroup();
      final RelationalGroup newRootGroup = new RelationalGroup();
      report.setRootGroup( newRootGroup );
      return new SectionEditUndoEntry
        ( report.getObjectID(), ModelUtility.findIndexOf( report, newRootGroup ), oldGroup, newRootGroup );
    }
    return null;
  }

  public static Object prepareForCopy( final ReportDocumentContext context, final Object data ) {
    if ( data instanceof ParameterDefinitionEntry ) {
      final AbstractReportDefinition report = context.getReportDefinition();
      if ( report instanceof MasterReport == false ) {
        return null;
      }

      final MasterReport mreport = (MasterReport) report;
      final ReportParameterDefinition definition = mreport.getParameterDefinition();
      if ( definition instanceof ModifiableReportParameterDefinition == false ) {
        return null;
      }

      final ModifiableReportParameterDefinition mdef = (ModifiableReportParameterDefinition) definition;
      final int count = mdef.getParameterCount();
      for ( int i = 0; i < count; i++ ) {
        final ParameterDefinitionEntry definitionEntry = mdef.getParameterDefinition( i );
        if ( definitionEntry == data ) {
          try {
            return definitionEntry.clone();
          } catch ( CloneNotSupportedException e ) {
            UncaughtExceptionsModel.getInstance().addException( e );
            return null;
          }
        }
      }
      return null;
    }

    if ( data instanceof Expression ) {
      final AbstractReportDefinition report = context.getReportDefinition();
      final ExpressionCollection expressionCollection = report.getExpressions();
      final int count = expressionCollection.size();
      for ( int i = 0; i < count; i++ ) {
        final Expression definitionEntry = expressionCollection.getExpression( i );
        if ( definitionEntry == data ) {
          return definitionEntry.getInstance();
        }
      }
      return null;
    }


    if ( data instanceof DataFactory ) {
      final AbstractReportDefinition report = context.getReportDefinition();
      // should be safe. If not, then the report-open functionality is wrong.
      final CompoundDataFactory dataFactory = (CompoundDataFactory) report.getDataFactory();
      final int count = dataFactory.size();
      for ( int i = 0; i < count; i++ ) {
        final DataFactory df = dataFactory.getReference( i );
        if ( df == data ) {
          return df.derive();
        }
      }
      return null;
    }

    if ( data instanceof Element == false ) {
      return null;
    }

    final Element e = (Element) data;
    final Element derived = e.derive();
    if ( e instanceof SubReport ) {
      if ( isBandedSubreport( e ) ) {
        derived.setAttribute( ReportDesignerBoot.DESIGNER_NAMESPACE, SUBREPORT_BANDED_HINT, Boolean.TRUE );
      } else {
        derived.setAttribute( ReportDesignerBoot.DESIGNER_NAMESPACE, SUBREPORT_BANDED_HINT, Boolean.FALSE );
      }
    }
    return derived;
  }

  private static boolean isBandedSubreport( final Element r ) {
    final Band parent = r.getParent();
    if ( parent instanceof RootLevelBand == false ) {
      return false;
    }
    final RootLevelBand rlb = (RootLevelBand) parent;
    final SubReport[] reports = rlb.getSubReports();
    for ( int i = 0; i < reports.length; i++ ) {
      final SubReport report = reports[ i ];
      if ( r == report ) {
        return true;
      }
    }
    return false;
  }
}
