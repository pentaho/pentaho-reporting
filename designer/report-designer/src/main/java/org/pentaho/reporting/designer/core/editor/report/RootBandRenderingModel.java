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

package org.pentaho.reporting.designer.core.editor.report;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.report.layouting.CrosstabRenderer;
import org.pentaho.reporting.designer.core.editor.report.layouting.ElementRenderer;
import org.pentaho.reporting.designer.core.editor.report.layouting.RootBandRenderer;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroupBody;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.GroupDataBody;
import org.pentaho.reporting.engine.classic.core.PageDefinition;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.SubGroupBody;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.util.ArrayList;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner
 */
public class RootBandRenderingModel {
  private class ModelUpdateListener implements ReportModelListener {
    public void nodeChanged( final ReportModelEvent event ) {
      if ( event.isNodeDeleteEvent() || event.isNodeAddedEvent() ) {
        final Object o = event.getParameter();
        if ( o instanceof Group ||
          o instanceof GroupBody ||
          o instanceof RootLevelBand ) {
          refresh();
          return;
        }
      }
      if ( event.getType() == ReportModelEvent.NODE_STRUCTURE_CHANGED ) {
        if ( event.getElement() instanceof RootLevelBand ) {
          refresh();
        }
      }

      if ( event.getType() == ReportModelEvent.NODE_PROPERTIES_CHANGED ) {
        final Object element = event.getElement();
        if ( element instanceof RootLevelBand ) {
          final RootLevelBand band = (RootLevelBand) element;
          final boolean modelHide = ModelUtility.isHideInLayoutGui( band );
          final boolean stateShow = isShown( band );
          if ( modelHide == stateShow ) {
            refresh();
          }
        }
      }
    }
  }

  private class RendererChangeHandler implements ChangeListener {
    public void stateChanged( final ChangeEvent e ) {
      fireReportRenderEvent();
    }
  }

  private ArrayList<ElementRenderer> rootBandComponents;
  private ReportDocumentContext renderContext;
  private AbstractReportDefinition report;
  private EventListenerList eventListenerList;
  private RendererChangeHandler rendererChangeHandler;

  public RootBandRenderingModel( final ReportRenderContext renderContext ) {
    if ( renderContext == null ) {
      throw new NullPointerException();
    }

    this.rendererChangeHandler = new RendererChangeHandler();
    this.eventListenerList = new EventListenerList();
    this.rootBandComponents = new ArrayList<ElementRenderer>();
    this.renderContext = renderContext;
    this.report = renderContext.getReportDefinition();
    this.report.addReportModelListener( new ModelUpdateListener() );

    refresh();
  }

  protected boolean isShown( final ReportElement element ) {
    if ( element == null ) {
      throw new NullPointerException();
    }
    final int length = rootBandComponents.size();
    for ( int i = 0; i < length; i++ ) {
      final ElementRenderer renderer = rootBandComponents.get( i );
      if ( renderer.getRepresentationId() == element.getObjectID() ) {
        return true;
      }
    }
    return false;
  }

  public void addChangeListener( final ChangeListener changeListener ) {
    eventListenerList.add( ChangeListener.class, changeListener );
  }

  public void removeChangeListener( final ChangeListener changeListener ) {
    eventListenerList.remove( ChangeListener.class, changeListener );
  }

  public void addReportRenderListener( final ReportRenderListener changeListener ) {
    eventListenerList.add( ReportRenderListener.class, changeListener );
  }

  public void removeReportRenderListener( final ReportRenderListener changeListener ) {
    eventListenerList.remove( ReportRenderListener.class, changeListener );
  }


  protected void fireReportRenderEvent() {
    final ReportRenderEvent ce = new ReportRenderEvent( this );
    final ReportRenderListener[] changeListeners = eventListenerList.getListeners( ReportRenderListener.class );
    for ( int i = 0; i < changeListeners.length; i++ ) {
      final ReportRenderListener listener = changeListeners[ i ];
      listener.layoutChanged( ce );
    }
  }

  protected void fireChangeEvent() {
    final ChangeEvent ce = new ChangeEvent( this );
    final ChangeListener[] changeListeners = eventListenerList.getListeners( ChangeListener.class );
    for ( int i = 0; i < changeListeners.length; i++ ) {
      final ChangeListener listener = changeListeners[ i ];
      listener.stateChanged( ce );
    }
  }

  // todo Codesmell
  public ReportDocumentContext getRenderContext() {
    return renderContext;
  }

  public ElementRenderer[] getAllRenderers() {
    return rootBandComponents.toArray( new ElementRenderer[ rootBandComponents.size() ] );
  }

  protected void refresh() {
    if ( report == null ) {
      clearRenderers();
      fireChangeEvent();
      return;
    }

    final ArrayList<ElementRenderer> rootBandComponents = new ArrayList<ElementRenderer>( 20 );
    if ( renderContext.isBandedContext() ) {
      if ( ModelUtility.isHideInLayoutGui( report.getPageHeader() ) == false ) {
        rootBandComponents.add( new RootBandRenderer( report.getPageHeader(), renderContext ) );
      }
    }
    if ( ModelUtility.isHideInLayoutGui( report.getReportHeader() ) == false ) {
      rootBandComponents.add( new RootBandRenderer( report.getReportHeader(), renderContext ) );
    }

    final Group[] groups = collectGroups();
    for ( int i = 0; i < groups.length; i++ ) {
      final Group group = groups[ i ];
      if ( group instanceof RelationalGroup ) {
        final RelationalGroup relationalGroup = (RelationalGroup) group;
        if ( ModelUtility.isHideInLayoutGui( relationalGroup.getHeader() ) == false ) {
          rootBandComponents.add( new RootBandRenderer( relationalGroup.getHeader(), renderContext ) );
        }
      }

      final Element bodyElement = group.getBody();
      if ( bodyElement instanceof GroupDataBody ) {
        final GroupDataBody body = (GroupDataBody) bodyElement;
        if ( ModelUtility.isHideInLayoutGui( body.getDetailsHeader() ) == false ) {
          rootBandComponents.add( new RootBandRenderer( body.getDetailsHeader(), renderContext ) );
        }
        if ( ModelUtility.isHideInLayoutGui( body.getNoDataBand() ) == false ) {
          rootBandComponents.add( new RootBandRenderer( body.getNoDataBand(), renderContext ) );
        }
        if ( ModelUtility.isHideInLayoutGui( body.getItemBand() ) == false ) {
          rootBandComponents.add( new RootBandRenderer( body.getItemBand(), renderContext ) );
        }
        if ( ModelUtility.isHideInLayoutGui( body.getDetailsFooter() ) == false ) {
          rootBandComponents.add( new RootBandRenderer( body.getDetailsFooter(), renderContext ) );
        }
        break;
      } else if ( group instanceof CrosstabGroup ) {
        rootBandComponents.add( new CrosstabRenderer( (CrosstabGroup) group, renderContext ) );
        break;
      }
    }

    for ( int i = groups.length - 1; i >= 0; i -= 1 ) {
      final Group group = groups[ i ];
      if ( group instanceof RelationalGroup ) {
        final RelationalGroup relationalGroup = (RelationalGroup) group;
        if ( ModelUtility.isHideInLayoutGui( relationalGroup.getFooter() ) == false ) {
          rootBandComponents.add( new RootBandRenderer( relationalGroup.getFooter(), renderContext ) );
        }
      }
    }

    if ( ModelUtility.isHideInLayoutGui( report.getReportFooter() ) == false ) {
      rootBandComponents.add( new RootBandRenderer( report.getReportFooter(), renderContext ) );
    }
    if ( renderContext.isBandedContext() ) {
      if ( ModelUtility.isHideInLayoutGui( report.getPageFooter() ) == false ) {
        rootBandComponents.add( new RootBandRenderer( report.getPageFooter(), renderContext ) );
      }
      if ( ModelUtility.isHideInLayoutGui( report.getWatermark() ) == false ) {
        rootBandComponents.add( new RootBandRenderer( report.getWatermark(), renderContext ) );
      }
    }

    if ( isChange( rootBandComponents ) ) {
      clearRenderers();
      this.rootBandComponents.addAll( rootBandComponents );
      for ( int i = 0; i < rootBandComponents.size(); i++ ) {
        final ElementRenderer renderer = rootBandComponents.get( i );
        renderer.addChangeListener( rendererChangeHandler );
      }
      fireChangeEvent();
    }

  }

  private boolean isChange( final ArrayList<ElementRenderer> rootBandComponents ) {
    if ( rootBandComponents.size() != this.rootBandComponents.size() ) {
      return true;
    }

    for ( int i = 0; i < rootBandComponents.size(); i++ ) {
      final ElementRenderer newRenderer = rootBandComponents.get( i );
      final ElementRenderer oldRenderer = this.rootBandComponents.get( i );
      if ( newRenderer.getRepresentationId() != oldRenderer.getRepresentationId() ) {
        return true;
      }

      if ( oldRenderer.isHideInLayout() != newRenderer.isHideInLayout() ) {
        return true;
      }
    }
    return false;
  }

  private Group[] collectGroups() {
    Group group = report.getRootGroup();
    final ArrayList<Group> list = new ArrayList<Group>();
    while ( group != null ) {
      list.add( group );

      final Element bodyElement = group.getBody();
      if ( bodyElement instanceof SubGroupBody ) {
        final SubGroupBody body = (SubGroupBody) bodyElement;
        group = body.getGroup();
        continue;
      } else if ( bodyElement instanceof CrosstabOtherGroupBody ) {
        final CrosstabOtherGroupBody body = (CrosstabOtherGroupBody) bodyElement;
        group = body.getGroup();
        continue;
      }
      group = null;
    }
    return list.toArray( new Group[ list.size() ] );
  }

  public void dispose() {
    clearRenderers();
  }

  private void clearRenderers() {
    final ElementRenderer[] allRenderers =
      rootBandComponents.toArray( new ElementRenderer[ rootBandComponents.size() ] );
    for ( int i = 0; i < allRenderers.length; i++ ) {
      final ElementRenderer renderer = allRenderers[ i ];
      renderer.removeChangeListener( rendererChangeHandler );
    }

    this.rootBandComponents.clear();
  }

  public PageDefinition getPageDefinition() {
    return renderContext.getContextRoot().getPageDefinition();
  }
}
