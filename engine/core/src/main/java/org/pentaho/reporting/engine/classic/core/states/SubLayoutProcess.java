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

package org.pentaho.reporting.engine.classic.core.states;

import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.states.datarow.ExpressionEventHelper;
import org.pentaho.reporting.engine.classic.core.states.datarow.InlineDataRowRuntime;
import org.pentaho.reporting.engine.classic.core.states.datarow.LevelStorage;
import org.pentaho.reporting.engine.classic.core.states.datarow.LevelStorageBackend;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class SubLayoutProcess extends ExpressionEventHelper implements LayoutProcess {
  private InlineDataRowRuntime inlineDataRowRuntime;
  private StructureFunction[] collectionFunctions;
  private boolean hasPageListener;
  private LayoutProcess parent;
  private LevelStorageBackend[] levelData;
  private InstanceID reportDefinitionId;

  public SubLayoutProcess( final LayoutProcess parent, final StructureFunction[] structureFunctions,
      final InstanceID reportDefinitionId ) {
    if ( structureFunctions == null ) {
      throw new NullPointerException();
    }
    if ( parent == null ) {
      throw new NullPointerException();
    }

    this.reportDefinitionId = reportDefinitionId;
    this.parent = parent;
    this.collectionFunctions = structureFunctions.clone();
    this.reinit();
  }

  public LayoutProcess getParent() {
    return parent;
  }

  private void reinit() {
    this.levelData = LevelStorageBackend.revalidate( this.collectionFunctions, collectionFunctions.length, false );
    this.hasPageListener = parent.isPageListener();
    for ( int i = 0; i < levelData.length; i++ ) {
      final LevelStorageBackend backend = levelData[i];
      if ( backend.hasPageEventListeners() ) {
        this.hasPageListener = true;
      }
    }
  }

  public boolean isPageListener() {
    return hasPageListener;
  }

  public OutputFunction getOutputFunction() {
    return parent.getOutputFunction();
  }

  public void restart( final ReportState state ) throws ReportProcessingException {
    parent.restart( state );
  }

  public StructureFunction[] getCollectionFunctions() {
    return collectionFunctions.clone();
  }

  protected int getRunLevelCount() {
    return levelData.length;
  }

  protected LevelStorage getRunLevel( final int index ) {
    final LevelStorageBackend backend = levelData[index];
    return LevelStorageBackend.getLevelStorage( backend, collectionFunctions );
  }

  protected ExpressionRuntime getRuntime() {
    return inlineDataRowRuntime;
  }

  public LayoutProcess deriveForStorage() {
    try {
      final SubLayoutProcess lp = (SubLayoutProcess) super.clone();
      if ( parent != null ) {
        lp.parent = parent.deriveForStorage();
      }
      lp.inlineDataRowRuntime = null;
      lp.collectionFunctions = collectionFunctions.clone();
      for ( int i = 0; i < collectionFunctions.length; i++ ) {
        collectionFunctions[i] = (StructureFunction) collectionFunctions[i].clone();
      }
      return lp;
    } catch ( final CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }

  public LayoutProcess deriveForPagebreak() {
    try {
      final SubLayoutProcess lp = (SubLayoutProcess) super.clone();
      if ( parent != null ) {
        lp.parent = parent.deriveForPagebreak();
      }
      lp.inlineDataRowRuntime = null;
      lp.collectionFunctions = collectionFunctions.clone();
      lp.inlineDataRowRuntime = null;
      for ( int i = 0; i < collectionFunctions.length; i++ ) {
        collectionFunctions[i] = (StructureFunction) collectionFunctions[i].clone();
      }
      return lp;
    } catch ( final CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }

  public Object clone() {
    try {
      final SubLayoutProcess lp = (SubLayoutProcess) super.clone();
      if ( parent != null ) {
        lp.parent = (LayoutProcess) parent.clone();
      }
      lp.inlineDataRowRuntime = null;
      lp.collectionFunctions = collectionFunctions.clone();
      lp.inlineDataRowRuntime = null;
      for ( int i = 0; i < collectionFunctions.length; i++ ) {
        collectionFunctions[i] = (StructureFunction) collectionFunctions[i].clone();
      }
      return lp;
    } catch ( final CloneNotSupportedException cne ) {
      throw new IllegalStateException( cne );
    }
  }

  public void fireReportEvent( final ReportEvent event ) {
    final InstanceID objectID = event.getReport().getObjectID();
    if ( objectID == reportDefinitionId ) {
      if ( inlineDataRowRuntime == null ) {
        inlineDataRowRuntime = new InlineDataRowRuntime();
      }
      final ReportState state = inlineDataRowRuntime.getState();
      inlineDataRowRuntime.setState( event.getState() );
      try {
        super.fireReportEvent( event );
      } catch ( final InvalidReportStateException exception ) {
        throw exception;
      } catch ( final Throwable t ) {
        throw new InvalidReportStateException( "Failed to fire report event for sub-layout-process", t );
      } finally {
        inlineDataRowRuntime.setState( state );
      }
    }

    if ( parent != null ) {
      parent.fireReportEvent( event );
    }
  }

}
