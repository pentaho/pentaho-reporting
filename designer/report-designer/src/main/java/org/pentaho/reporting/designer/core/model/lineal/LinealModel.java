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


package org.pentaho.reporting.designer.core.model.lineal;

import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;

import javax.swing.event.EventListenerList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The lineal model is a collection of immutable guideline objects.
 * <p/>
 * PRD-622: Move the undo stuff out of the data-model. It belongs into the GUI
 */
public class LinealModel implements Serializable {
  private class GlobalUpdateHandler implements SettingsListener {
    private boolean globalState;

    private GlobalUpdateHandler() {
      this.globalState = WorkspaceSettings.getInstance().isSnapToGuideLines();
    }

    public void settingsChanged() {
      final boolean newState = WorkspaceSettings.getInstance().isSnapToGuideLines();
      if ( globalState != newState ) {
        globalState = newState;
        setGlobalState( globalState );
      }
    }
  }

  private ArrayList<GuideLine> guideLines;
  private transient EventListenerList linealModelListeners;
  private long modificationCount;
  /**
   * A strong reference is needed to make sure that the listener gets garbage collected with the model and not earlier.
   *
   * @noinspection FieldCanBeLocal
   */
  private transient GlobalUpdateHandler updateHandler;

  public LinealModel() {
    guideLines = new ArrayList<GuideLine>();
    linealModelListeners = new EventListenerList();
    updateHandler = new GlobalUpdateHandler();
    WorkspaceSettings.getInstance().addSettingsListener( updateHandler );
  }

  public long getModificationCount() {
    return modificationCount;
  }

  public GuideLine[] getGuideLines() {
    return guideLines.toArray( new GuideLine[ guideLines.size() ] );
  }

  public boolean removeGuideLine( final GuideLine guideLine ) {
    if ( guideLines.remove( guideLine ) ) {
      modificationCount += 1;
      fireModelChanged();
      return true;
    }
    return false;
  }


  public boolean addGuidLine( final GuideLine guideLine ) {
    if ( guideLines.add( guideLine ) ) {
      modificationCount += 1;
      fireModelChanged();
      return true;
    }
    return false;
  }

  public void updateGuideLine( final int position, final GuideLine guideLine ) {
    guideLines.set( position, guideLine );
    modificationCount += 1;
    fireModelChanged();
  }

  public int getGuideLineCount() {
    return guideLines.size();
  }

  public GuideLine getGuideLine( final int index ) {
    return guideLines.get( index );
  }

  private void fireModelChanged() {
    final LinealModelEvent event = new LinealModelEvent( this );
    final LinealModelListener[] lml = linealModelListeners.getListeners( LinealModelListener.class );
    for ( final LinealModelListener linealModelListener : lml ) {
      linealModelListener.modelChanged( event );
    }
  }

  public void addLinealModelListener( final LinealModelListener linealModelListener ) {
    linealModelListeners.add( LinealModelListener.class, linealModelListener );
  }

  public void removeLinealModelListener( final LinealModelListener linealModelListener ) {
    linealModelListeners.remove( LinealModelListener.class, linealModelListener );
  }

  public void parse( final String model ) {
    try {
      final String number = "\\d*(?:\\.\\d*)?";//NON-NLS
      final Pattern fullPattern = Pattern.compile( "\\((\\s*\\w*\\s*,\\s*" + number + "\\s*)\\)" );//NON-NLS
      final Matcher m = fullPattern.matcher( model );
      final ArrayList<GuideLine> matches = new ArrayList<GuideLine>();
      while ( m.find() ) {
        final String guildeLineDef = m.group( 1 );
        final String[] strings = guildeLineDef.split( "," );
        if ( strings.length != 2 ) {
          return;
        }
        final boolean active = "true".equals( strings[ 0 ] );//NON-NLS
        final double pos = Double.parseDouble( strings[ 1 ] );
        matches.add( new GuideLine( pos, active ) );
      }
      this.guideLines.clear();
      this.guideLines.addAll( matches );
    } catch ( Exception e ) {
      UncaughtExceptionsModel.getInstance().addException( e );
    }
  }

  public String externalize() {
    final GuideLine[] guidelines = getGuideLines();
    if ( guidelines.length > 0 ) {
      final StringBuffer b = new StringBuffer( 100 );
      for ( int i = 0; i < guidelines.length; i++ ) {
        final GuideLine guideline = guidelines[ i ];
        if ( i != 0 ) {
          b.append( ' ' );
        }
        b.append( guideline.externalize() );
      }
      return b.toString();
    }
    return null;
  }

  public void setGlobalState( final boolean active ) {
    final GuideLine[] lines = getGuideLines();
    for ( int i = 0; i < lines.length; i++ ) {
      final GuideLine line = lines[ i ];
      guideLines.set( i, line.updateActive( active ) );
      modificationCount += 1;
    }
    fireModelChanged();
  }
}
