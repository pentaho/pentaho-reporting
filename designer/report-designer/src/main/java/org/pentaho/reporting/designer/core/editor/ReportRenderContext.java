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
* Copyright (c) 2002-2018 Hitachi Vantara.  All rights reserved.
*/

package org.pentaho.reporting.designer.core.editor;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.auth.AuthenticationStore;
import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;
import org.pentaho.reporting.designer.core.auth.ReportAuthenticationStore;
import org.pentaho.reporting.designer.core.editor.report.layouting.SharedElementRenderer;
import org.pentaho.reporting.designer.core.inspections.AutoInspectionRunner;
import org.pentaho.reporting.designer.core.inspections.InspectionResultListener;
import org.pentaho.reporting.designer.core.inspections.InspectionRunner;
import org.pentaho.reporting.designer.core.inspections.NoOpInspectionRunner;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.model.data.AsynchronousDataSchemaManager;
import org.pentaho.reporting.designer.core.model.data.DataSchemaManager;
import org.pentaho.reporting.designer.core.model.selection.DefaultReportSelectionModel;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.undo.UndoManager;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CrosstabElement;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageDefinition;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.docbundle.ODFMetaAttributeNames;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * A render context that covers a single report-model. Report-Definition and master-report can be the same object, but
 * are not necessarily the same. ReportDef could be a subreport and the master-report is always the single
 * master-report.
 *
 * @author Thomas Morgner
 */
public class ReportRenderContext implements ReportDocumentContext {
  private static class ReportNameUpdateHandler implements ReportModelListener {
    private ReportRenderContext context;

    protected ReportNameUpdateHandler( final ReportRenderContext context ) {
      this.context = context;
      this.context.setTabName( computeTabName( this.context.getReportDefinition() ) );
    }

    public void nodeChanged( final ReportModelEvent event ) {
      AbstractReportDefinition report = context.getReportDefinition();
      if ( event.getElement() == report
        && event.getType() == ReportModelEvent.NODE_PROPERTIES_CHANGED ) {
        context.setTabName( computeTabName( report ) );
      }
    }

    private String computeTabName( final AbstractReportDefinition report ) {
      if ( report instanceof MasterReport ) {
        final MasterReport mreport = (MasterReport) report;
        final Object title = mreport.getBundle().getMetaData().getBundleAttribute(
          ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.TITLE );
        if ( title instanceof String ) {
          return (String) title;
        }
      }

      final String name = report.getName();
      if ( StringUtils.isEmpty( name ) == false ) {
        return name;
      }

      final String theSavePath =
        (String) report.getAttribute( ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.LAST_FILENAME ); // NON-NLS
      if ( !StringUtils.isEmpty( theSavePath ) ) {
        final String fileName = IOUtils.getInstance().getFileName( theSavePath );
        return IOUtils.getInstance().stripFileExtension( fileName );
      }

      if ( report instanceof MasterReport ) {
        return Messages.getString( "ReportDesignerFrame.TabName.UntitledReport" ); // NON-NLS
      } else if ( report instanceof CrosstabElement ) {
        return Messages.getString( "ReportDesignerFrame.TabName.UntitledCrosstab" ); // NON-NLS
      } else {
        return Messages.getString( "ReportDesignerFrame.TabName.UntitledSubReport" ); // NON-NLS
      }
    }
  }


  // TODO add a new delete listener that is looking for embedded resources - separate class

  private class NodeDeleteListener implements ReportModelListener {
    private NodeDeleteListener() {
    }

    public void nodeChanged( final ReportModelEvent event ) {
      if ( event.isNodeDeleteEvent() ) {
        final DocumentContextSelectionModel selectionModel = getSelectionModel();
        final AbstractReportDefinition reportDefinition = getReportDefinition();
        final Object element = event.getElement();
        if ( element instanceof Element ) {
          final List<Element> selectedElements = selectionModel.getSelectedElementsOfType( Element.class );
          for ( final Element selectedElement : selectedElements ) {
            if ( ModelUtility.isDescendant( reportDefinition, selectedElement ) == false ) {
              selectionModel.remove( element );
            }
          }
        } else {
          selectionModel.remove( element );
        }
      }
    }
  }

  private class ZoomUpdateHandler implements ZoomModelListener {
    private ZoomUpdateHandler() {
    }

    public void zoomFactorChanged() {
      //noinspection UnnecessaryBoxing
      reportDefinition.setAttribute( ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.ZOOM,
        new Float( zoomModel.getZoomAsPercentage() ), false );
    }
  }

  private class DataSchemaManagerUpdateHandler implements ChangeListener {
    public void stateChanged( final ChangeEvent e ) {
      fireDataSourceChangedEvent();
    }
  }

  private static final String AUTHENTICATION_STORE_PROPERTY = "authentication-store";

  private final PropertyChangeSupport propertyChangeSupport;
  private final SharedElementRenderer sharedRenderer;
  private final ZoomModel zoomModel;
  private final MasterReport masterReportElement;
  private final AbstractReportDefinition reportDefinition;
  private final DocumentContextSelectionModel selectionModel;
  private final InspectionRunner inspectionRunner;
  private final UndoManager undo;
  private final NodeDeleteListener deleteListener;
  private final HashMap<String, Object> properties;
  private final DataSchemaManager dataSchemaManager;
  private final boolean bandedContext;
  private final ReportDocumentContext parentContext;
  private final ArrayList<ReportDataChangeListener> dataChangeListeners;
  private String tabName;
  private Icon icon;
  private long changeTracker;

  public ReportRenderContext( final MasterReport masterReport ) {
    this( masterReport, masterReport, null, new GlobalAuthenticationStore() );
  }

  public ReportRenderContext( final MasterReport masterReportElement,
                              final AbstractReportDefinition report,
                              final ReportDocumentContext parentContext,
                              final GlobalAuthenticationStore globalAuthenticationStore ) {
    this( masterReportElement, report, parentContext, globalAuthenticationStore, false );
  }

  public ReportRenderContext( final MasterReport masterReportElement,
                              final AbstractReportDefinition report,
                              final ReportDocumentContext parentContext,
                              final GlobalAuthenticationStore globalAuthenticationStore,
                              final boolean computationTarget ) {
    if ( masterReportElement == null ) {
      throw new NullPointerException();
    }
    if ( report == null ) {
      throw new NullPointerException();
    }

    this.dataChangeListeners = new ArrayList<ReportDataChangeListener>();
    this.propertyChangeSupport = new PropertyChangeSupport( this );

    this.selectionModel = new DefaultReportSelectionModel();
    this.masterReportElement = masterReportElement;

    this.deleteListener = new NodeDeleteListener();

    this.reportDefinition = report;
    this.reportDefinition.addReportModelListener( deleteListener );
    this.reportDefinition.addReportModelListener( new ReportNameUpdateHandler( this ) );

    this.zoomModel = new ZoomModel();
    this.zoomModel.addZoomModelListener( new ZoomUpdateHandler() );

    this.bandedContext = computeBandedContext( parentContext );
    this.parentContext = parentContext;

    this.dataSchemaManager = new AsynchronousDataSchemaManager( masterReportElement, report );
    this.dataSchemaManager.addChangeListener( new DataSchemaManagerUpdateHandler() );

    if ( !computationTarget ) {
      this.inspectionRunner = new AutoInspectionRunner( this );
      this.reportDefinition.addReportModelListener( inspectionRunner );
      this.undo = createUndoManager();
    } else {
      this.inspectionRunner = NoOpInspectionRunner.INSTANCE;
      this.undo = null;
    }

    final Object f =
      this.reportDefinition.getAttribute( ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.ZOOM );
    if ( f instanceof Float ) {
      final Float zoomFactor = (Float) f;
      //noinspection UnnecessaryUnboxing
      this.zoomModel.setZoomAsPercentage( zoomFactor.floatValue() );
    }

    if ( parentContext == null ) {
      this.properties = new HashMap<String, Object>();
      this.sharedRenderer = new SharedElementRenderer( this );
    } else {
      this.sharedRenderer = parentContext.getSharedRenderer();
      this.properties = parentContext.getProperties();
    }

    prepareAuthenticationStore( globalAuthenticationStore );
    prepareIcon();
    resetChangeTracker();
  }

  private UndoManager createUndoManager() {
    final Object o = this.reportDefinition.getAttribute( ReportDesignerBoot.DESIGNER_NAMESPACE, "undo" ); // NON-NLS
    if ( o instanceof UndoManager ) {
      return (UndoManager) o;
    } else {
      return new UndoManager();
    }
  }

  private void prepareAuthenticationStore( final GlobalAuthenticationStore globalAuthenticationStore ) {
    final Object maybeAuthStore = getProperty( AUTHENTICATION_STORE_PROPERTY );
    if ( maybeAuthStore == null ) {
      setProperty( AUTHENTICATION_STORE_PROPERTY, new ReportAuthenticationStore( globalAuthenticationStore ) );
    }
  }

  private void prepareIcon() {
    final Image iconImage =
      reportDefinition.getElementType().getMetaData().getIcon( Locale.getDefault(), BeanInfo.ICON_COLOR_16x16 );
    if ( iconImage != null ) {
      icon = new ImageIcon( iconImage );
    } else {
      icon = null;
    }
  }

  public void addReportDataChangeListener( final ReportDataChangeListener l ) {
    ArgumentNullException.validate( "l", l );
    dataChangeListeners.add( l );
  }

  public void removeReportDataChangeListener( final ReportDataChangeListener l ) {
    ArgumentNullException.validate( "l", l );
    dataChangeListeners.remove( l );
  }

  protected void fireDataSourceChangedEvent() {
    if ( dataChangeListeners.isEmpty() ) {
      return;
    }
    for ( final ReportDataChangeListener listener : dataChangeListeners ) {
      listener.dataModelChanged( this );
    }
  }

  public boolean isBandedContext() {
    return bandedContext;
  }

  private boolean computeBandedContext( final ReportDocumentContext parentContext ) {
    if ( parentContext == null ) {
      return true;
    }
    if ( reportDefinition instanceof MasterReport ) {
      return true;
    }
    if ( parentContext.isBandedContext() == false ) {
      return false;
    }

    final Section parentSection = reportDefinition.getParentSection();
    if ( parentSection instanceof RootLevelBand == false ) {
      return false;
    }

    final RootLevelBand rlb = (RootLevelBand) parentSection;
    for ( int i = 0; i < rlb.getSubReportCount(); i += 1 ) {
      if ( rlb.getSubReport( i ) == reportDefinition ) {
        return true;
      }
    }
    return false;
  }

  public SharedElementRenderer getSharedRenderer() {
    return sharedRenderer;
  }

  public DocumentContextSelectionModel getSelectionModel() {
    return selectionModel;
  }

  public ContextAwareDataSchemaModel getReportDataSchemaModel() {
    return dataSchemaManager.getModel();
  }

  protected InspectionRunner getInspectionRunner() {
    return inspectionRunner;
  }

  public ZoomModel getZoomModel() {
    return zoomModel;
  }

  public MasterReport getMasterReportElement() {
    return masterReportElement;
  }

  public MasterReport getContextRoot() {
    return getMasterReportElement();
  }

  public AbstractReportDefinition getReportDefinition() {
    return reportDefinition;
  }

  @Deprecated
  public PageDefinition getPageDefinition() {
    return masterReportElement.getPageDefinition();
  }

  public boolean isChanged() {
    if ( parentContext != null ) {
      return parentContext.isChanged();
    } else {
      return getMasterReportElement().getChangeTracker() != changeTracker;
    }
  }

  public void resetChangeTracker() {
    if ( parentContext != null ) {
      parentContext.resetChangeTracker();
    } else {
      changeTracker = getMasterReportElement().getChangeTracker();
    }
  }

  public UndoManager getUndo() {
    return undo;
  }

  public ResourceManager getResourceManager() {
    return masterReportElement.getResourceManager();
  }

  public void dispose() {
    this.dataSchemaManager.close();
    this.inspectionRunner.dispose();
    this.reportDefinition.removeReportModelListener( inspectionRunner );
    this.reportDefinition.removeReportModelListener( deleteListener );
  }

  public void setProperty( final String property, final Object value ) {
    if ( value == null ) {
      properties.remove( property );
      return;
    }
    properties.put( property, value );
  }

  public Object getProperty( final String property ) {
    return properties.get( property );
  }

  public HashMap<String, Object> getProperties() {
    return properties;
  }

  public AuthenticationStore getAuthenticationStore() {
    return (AuthenticationStore) getProperty( AUTHENTICATION_STORE_PROPERTY );
  }

  public String getTabName() {
    return tabName;
  }

  public void setTabName( final String tabName ) {
    String oldName = this.tabName;
    if ( ObjectUtilities.equal( oldName, tabName ) ) {
      return;
    }

    this.tabName = tabName;
    firePropertyChange( "tabName", oldName, tabName );
  }

  public Icon getIcon() {
    return icon;
  }

  public String getDocumentFile() {
    return (String) masterReportElement.getAttribute( ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.LAST_FILENAME );
  }

  public void removePropertyChangeListener( final String propertyName, final PropertyChangeListener listener ) {
    propertyChangeSupport.removePropertyChangeListener( propertyName, listener );
  }

  public void addPropertyChangeListener( final String propertyName, final PropertyChangeListener listener ) {
    propertyChangeSupport.addPropertyChangeListener( propertyName, listener );
  }

  public void removePropertyChangeListener( final PropertyChangeListener listener ) {
    propertyChangeSupport.removePropertyChangeListener( listener );
  }

  public void addPropertyChangeListener( final PropertyChangeListener listener ) {
    propertyChangeSupport.addPropertyChangeListener( listener );
  }

  protected void firePropertyChange( final String propertyName, final Object oldValue, final Object newValue ) {
    propertyChangeSupport.firePropertyChange( propertyName, oldValue, newValue );
  }

  public void onDocumentActivated() {
    getInspectionRunner().startTimer();
  }

  public void addInspectionListener( final InspectionResultListener listener ) {
    getInspectionRunner().addInspectionListener( listener );
  }

  public void removeInspectionListener( final InspectionResultListener listener ) {
    getInspectionRunner().removeInspectionListener( listener );
  }
}
