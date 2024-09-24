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

package org.pentaho.reporting.designer.core.editor;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.ReportDesignerDocumentContext;
import org.pentaho.reporting.designer.core.ReportDesignerView;
import org.pentaho.reporting.designer.core.actions.global.PasteAction;
import org.pentaho.reporting.designer.core.actions.report.AddDataFactoryAction;
import org.pentaho.reporting.designer.core.editor.structuretree.ReportFunctionNode;
import org.pentaho.reporting.designer.core.editor.structuretree.ReportParametersNode;
import org.pentaho.reporting.designer.core.editor.structuretree.ReportQueryNode;
import org.pentaho.reporting.designer.core.editor.structuretree.SubReportParametersNode;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.CrosstabCellBody;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabElement;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.GroupedMetaDataComparator;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ContextMenuUtility {
  private ContextMenuUtility() {
  }

  public static JPopupMenu getMenu( final ReportDesignerContext context, final Object selectedElement ) {
    ReportDesignerView view = context.getView();
    if ( selectedElement == null || selectedElement instanceof MasterReport ) { // This check assumes that we've click on a report band see JIRA
      // PRD-1076
      return view.getPopupMenu( "popup-ReportDefinition" ); // NON-NLS
    }
    final ReportDesignerDocumentContext activeContext = context.getActiveContext();
    ReportRenderContext doc = null;
    if ( activeContext instanceof ReportRenderContext ) {
      doc = (ReportRenderContext) activeContext;
      if ( selectedElement == doc.getReportDefinition() ) {
        if ( selectedElement instanceof CrosstabElement ) {
          return view.getPopupMenu( "popup-CrosstabElement" ); // NON-NLS
        }
        return view.getPopupMenu( "popup-ReportDefinition" ); // NON-NLS
      }
    }
    if ( selectedElement instanceof SubReport ) {
      return view.getPopupMenu( "popup-SubReport" ); // NON-NLS
    }
    if ( selectedElement instanceof CompoundDataFactory ) {
      return createDataSourcePopup( context );
    }
    if ( selectedElement instanceof DataFactory ) {
      return view.getPopupMenu( "popup-DataSource" ); // NON-NLS
    }
    if ( selectedElement instanceof ReportFunctionNode ) {
      return view.getPopupMenu( "popup-Expressions" ); // NON-NLS
    }
    if ( selectedElement instanceof ReportQueryNode ) {
      final ReportQueryNode rqn = (ReportQueryNode) selectedElement;
      JPopupMenu popupMenu;
      if ( rqn.isAllowEdit() ) {
        popupMenu = view.getPopupMenu( "popup-Query" ); // NON-NLS
      } else {
        popupMenu = view.getPopupMenu( "popup-Inherited-Query" ); // NON-NLS
      }
      final MenuElement activationItem = popupMenu.getSubElements()[0];
      toggleActivationItem( doc, rqn, activationItem );
      return popupMenu;
    }
    if ( selectedElement instanceof Expression ) {
      return view.getPopupMenu( "popup-Expression" ); // NON-NLS
    }
    if ( selectedElement instanceof RootLevelBand ) {
      return view.getPopupMenu( "popup-RootLevelBand" ); // NON-NLS
    }
    if ( selectedElement instanceof RelationalGroup ) {
      return view.getPopupMenu( "popup-RelationalGroup" ); // NON-NLS
    }
    if ( selectedElement instanceof CrosstabGroup ) {
      return view.getPopupMenu( "popup-CrosstabGroup" ); // NON-NLS
    }
    if ( selectedElement instanceof CrosstabOtherGroup ) {
      return view.getPopupMenu( "popup-CrosstabOtherGroup" ); // NON-NLS
    }
    if ( selectedElement instanceof CrosstabRowGroup ) {
      return view.getPopupMenu( "popup-CrosstabRowGroup" ); // NON-NLS
    }
    if ( selectedElement instanceof CrosstabColumnGroup ) {
      return view.getPopupMenu( "popup-CrosstabColumnGroup" ); // NON-NLS
    }
    if ( selectedElement instanceof CrosstabCellBody ) {
      return view.getPopupMenu( "popup-CrosstabCellBody" ); // NON-NLS
    }
    if ( selectedElement instanceof Group ) {
      return view.getPopupMenu( "popup-Group" ); // NON-NLS
    }
    if ( selectedElement instanceof Band ) {
      return view.getPopupMenu( "popup-Band" ); // NON-NLS
    }
    if ( selectedElement instanceof Element ) {
      final Element element = (Element) selectedElement;
      final JPopupMenu popup = view.getPopupMenu( "popup-" + element.getElementTypeName() ); // NON-NLS
      if ( popup != null ) {
        return popup;
      }
      return view.getPopupMenu( "popup-Element" ); // NON-NLS
    }
    if ( selectedElement instanceof ReportParameterDefinition ) {
      return view.getPopupMenu( "popup-Parameters" ); // NON-NLS
    }
    if ( selectedElement instanceof ParameterDefinitionEntry ) {
      return view.getPopupMenu( "popup-Parameter" ); // NON-NLS
    }
    if ( selectedElement instanceof ReportParametersNode ) {
      return view.getPopupMenu( "popup-Parameters" ); // NON-NLS
    }
    if ( selectedElement instanceof SubReportParametersNode ) {
      return view.getPopupMenu( "popup-SubReportParameters" ); // NON-NLS
    }
    return null;
  }

  public static JPopupMenu createDataSourcePopup( final ReportDesignerContext designerContext ) {
    final JPopupMenu insertDataSourcesMenu = new JPopupMenu();
    final PasteAction action = new PasteAction();
    action.setReportDesignerContext( designerContext );
    insertDataSourcesMenu.add( action );
    insertDataSourcesMenu.addSeparator();
    createDataSourceMenu( designerContext, insertDataSourcesMenu );
    return insertDataSourcesMenu;
  }

  public static void createDataSourceMenu( final ReportDesignerContext designerContext,
                                           final JComponent insertDataSourcesMenu ) {
    JMenu subMenu = null;

    final Map<String, Boolean> groupingMap = new HashMap<>();
    final DataFactoryMetaData[] datas = DataFactoryRegistry.getInstance().getAll();
    for ( int i = 0; i < datas.length; i++ ) {
      final DataFactoryMetaData data = datas[i];
      if ( data.isHidden() ) {
        continue;
      }
      if ( !WorkspaceSettings.getInstance().isVisible( data ) ) {
        continue;
      }
      if ( data.isEditorAvailable() == false ) {
        continue;
      }

      final String currentGrouping = data.getGrouping( Locale.getDefault() );
      groupingMap.put( currentGrouping, groupingMap.containsKey( currentGrouping ) );
    }

    Arrays.sort( datas, new GroupedMetaDataComparator() );
    Object grouping = null;
    boolean firstElement = true;
    for ( int i = 0; i < datas.length; i++ ) {
      final DataFactoryMetaData data = datas[i];
      if ( data.isHidden() ) {
        continue;
      }
      if ( !WorkspaceSettings.getInstance().isVisible( data ) ) {
        continue;
      }

      if ( data.isEditorAvailable() == false ) {
        continue;
      }

      final String currentGrouping = data.getGrouping( Locale.getDefault() );
      final boolean isMultiGrouping = groupingMap.get( currentGrouping );
      if ( firstElement == false ) {
        if ( ObjectUtilities.equal( currentGrouping, grouping ) == false ) {
          grouping = currentGrouping;
          if ( isMultiGrouping ) {
            subMenu = new JMenu( currentGrouping );
            insertDataSourcesMenu.add( subMenu );
          }
        }
      } else {
        firstElement = false;
        grouping = currentGrouping;
        if ( isMultiGrouping ) {
          subMenu = new JMenu( currentGrouping );
          insertDataSourcesMenu.add( subMenu );
        }
      }
      final AddDataFactoryAction action = new AddDataFactoryAction( data );
      action.setReportDesignerContext( designerContext );
      if ( isMultiGrouping ) {
        //noinspection ConstantConditions
        subMenu.add( new JMenuItem( action ) );
      } else {
        insertDataSourcesMenu.add( new JMenuItem( action ) );
      }
    }
  }

  protected static void toggleActivationItem( final ReportRenderContext doc,
                                              final ReportQueryNode rqn,
                                              final MenuElement activationItem ) {
    if ( activationItem instanceof JMenuItem && doc != null ) {
      final DataFactory dataFactory = doc.getContextRoot().getDataFactory();
      boolean disabled = false;
      if ( dataFactory instanceof CompoundDataFactory ) {
        final CompoundDataFactory compound = (CompoundDataFactory) dataFactory;
        int count = countQueriesWithName( rqn.getQueryName(), compound );
        disabled = count > 1;
      }
      if ( disabled ) {
        ( (JMenuItem) activationItem ).setEnabled( false );
      } else {
        ( (JMenuItem) activationItem ).setEnabled( true );
      }
    }
  }

  private static int countQueriesWithName( final String queryName, final CompoundDataFactory compound ) {
    int count = 0;
    if ( compound.size() > 1 ) {
      for ( int i = 0; i < compound.size(); i++ ) {
        final DataFactory innerFactory = compound.get( i );
        final String[] queryNames = innerFactory.getQueryNames();
        for ( int j = 0; j < queryNames.length; j++ ) {
          if ( ObjectUtilities.equal( queryName, queryNames[j] ) ) {
            count++;
            break;
          }
        }
      }
    }
    return count;
  }
}
