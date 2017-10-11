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

package org.pentaho.reporting.designer.core.util.undo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.io.Serializable;
import java.util.ArrayList;

public class UndoManager implements Serializable {
  private static class UndoRecord implements Serializable {
    private static final int UNDO_THREASHOLD = 250; // after 250 msecs we assume we see a new event ..

    private long timestamp;
    private UndoEntry entry;

    private UndoRecord( final UndoEntry entry ) {
      if ( entry == null ) {
        throw new NullPointerException();
      }

      this.timestamp = System.currentTimeMillis();
      this.entry = entry;
    }

    public long getTimestamp() {
      return timestamp;
    }

    public UndoEntry getEntry() {
      return entry;
    }

    public boolean isNear( final long time ) {
      if ( Math.abs( timestamp - time ) < UNDO_THREASHOLD ) {
        return true;
      }
      return false;
    }
  }

  private static final Log LOG = LogFactory.getLog( UndoManager.class );
  private static final int MAXIMUM_UNDO_SIZE = 5000;//to ensure not to be completely unbound, prevents OOME

  private transient EventListenerList undoListeners;
  private ArrayList<UndoRecord> undos;
  private ArrayList<String> undoNames;
  /**
   * The next position where to insert the undo-record. Offset -1 is the position of the next redo.
   */
  private int offset;

  public UndoManager() {
    undoListeners = new EventListenerList();
    undos = new ArrayList<UndoRecord>( MAXIMUM_UNDO_SIZE );
    undoNames = new ArrayList<String>( MAXIMUM_UNDO_SIZE );
  }

  public void addChange( final String undoName, final UndoEntry entry ) {
    while ( offset < undos.size() ) {
      undos.remove( undos.size() - 1 );
      undoNames.remove( undoNames.size() - 1 );
    }

    if ( offset > 0 ) {
      final UndoRecord lastRecord = undos.get( offset - 1 );
      if ( lastRecord.isNear( System.currentTimeMillis() ) ) {
        final UndoEntry merged = lastRecord.getEntry().merge( entry );
        if ( merged != null ) {
          undos.set( offset - 1, new UndoRecord( merged ) );
          undoNames.set( offset - 1, undoName );
          fireChangeEvent();
          return;
        }
      }
    }

    undos.add( new UndoRecord( entry ) );
    undoNames.add( undoName );
    offset = undos.size();
    while ( undos.size() > MAXIMUM_UNDO_SIZE ) {
      removeLastRecord();
    }

    fireChangeEvent();
  }

  private void fireChangeEvent() {
    final ChangeListener[] changeListeners = undoListeners.getListeners( ChangeListener.class );
    final ChangeEvent event = new ChangeEvent( this );
    for ( int i = 0; i < changeListeners.length; i++ ) {
      final ChangeListener listener = changeListeners[ i ];
      listener.stateChanged( event );
    }
  }

  private void removeLastRecord() {
    // this should remove all entries that are within the threshold 
    undos.remove( 0 );
    undoNames.remove( 0 );
  }

  public String getUndoName() {
    if ( offset == 0 ) {
      return null;
    }
    final String name = undoNames.get( offset - 1 );
    return name;
  }

  public String getRedoName() {
    if ( undos.isEmpty() ) {
      return null;
    }

    if ( offset == ( undos.size() ) ) {
      return null;
    }
    final String name = undoNames.get( offset );
    return name;
  }

  public void undo( final ReportDocumentContext context ) {
    if ( offset == 0 ) {
      return;
    }

    final UndoRecord record = undos.get( offset - 1 );
    offset -= 1;
    record.getEntry().undo( context );

    fireChangeEvent();
  }

  public void redo( final ReportDocumentContext context ) {
    if ( undos.isEmpty() ) {
      return;
    }

    if ( offset == ( undos.size() ) ) {
      return;
    }

    final UndoRecord record = undos.get( offset );
    offset += 1;
    record.getEntry().redo( context );

    fireChangeEvent();
  }

  public boolean isUndoPossible() {
    return offset > 0;
  }

  public void removeUndoListener( final ChangeListener undoAction ) {
    undoListeners.remove( ChangeListener.class, undoAction );
  }

  public void addUndoListener( final ChangeListener undoAction ) {
    undoListeners.add( ChangeListener.class, undoAction );
  }

  public boolean isRedoPossible() {
    return offset < undos.size();
  }
}

