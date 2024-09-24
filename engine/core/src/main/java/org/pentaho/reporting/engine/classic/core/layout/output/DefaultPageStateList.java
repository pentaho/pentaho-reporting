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

package org.pentaho.reporting.engine.classic.core.layout.output;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.util.WeakReferenceList;
import org.pentaho.reporting.libraries.base.config.ExtendedConfigurationWrapper;

/**
 * The ReportState list stores a report states for the beginning of every page. The list is filled on repagination and
 * read when a report or a page of the report is printed.
 * <p/>
 * Important: This list stores page start report states, not arbitary report states. These ReportStates are special:
 * they can be reproduced by calling processPage on the report.
 * <p/>
 * Internally this list is organized as a list of WeakReferenceLists, where every WeakReferenceList stores a certain
 * number of page states. The first 20 states are stored in an ordinary list with strong-references, so these states
 * never get GarbageCollected (and so they must never be restored by reprocessing them). The next 100 states are stored
 * in 4-element ReferenceLists, so if a reference is lost, only 4 states have to be reprocessed. All other states are
 * stored in 10-element lists.
 *
 * @author Thomas Morgner
 */
public class DefaultPageStateList implements PageStateList {
  private static final Log logger = LogFactory.getLog( DefaultPageStateList.class );
  /**
   * The position of the master element in the list. A greater value will reduce the not-freeable memory used by the
   * list, but restoring a single page will require more time.
   */

  /**
   * The maxmimum masterposition size.
   */
  private static final int MASTERPOSITIONS_MAX = 10;

  /**
   * The medium masterposition size.
   */
  private static final int MASTERPOSITIONS_MED = 4;

  /**
   * The max index that will be stored in the primary list.
   */
  private static final int PRIMARY_MAX = 20;

  /**
   * The max index that will be stored in the master4 list.
   */
  private static final int MASTER4_MAX = 100;

  /**
   * Internal WeakReferenceList that is capable to restore its elements. The elements in this list are page start report
   * states.
   */
  private final class MasterList extends WeakReferenceList<InternalStorageState> {
    /**
     * The master list.
     */
    private final DefaultPageStateList master;

    /**
     * Creates a new master list.
     *
     * @param list
     *          the list.
     * @param maxChildCount
     *          the maximum number of elements in this list.
     */
    private MasterList( final DefaultPageStateList list, final int maxChildCount ) {
      super( maxChildCount );
      this.master = list;
    }

    public Object clone() throws CloneNotSupportedException {
      return super.clone();
    }

    /**
     * Function to restore the state of a child after the child was garbage collected.
     *
     * @param index
     *          the index.
     * @return the restored ReportState of the given index, or null, if the state could not be restored.
     */
    protected InternalStorageState restoreChild( final int index ) {
      InternalStorageState master = (InternalStorageState) getMaster();
      if ( master == null ) {
        throw new IllegalStateException( "We cannot have a master list without a master state" );
      }
      try {
        final int max = getChildPos( index );
        logger.info( "Restoring weak state " + master.getStorePosition() + " for # " + max );

        if ( master.isValidRestorePoint() == false ) {
          // not a safe point, so restore the master first ..
          final InternalStorageState state =
              DefaultPageStateList.this.restoreState( master.getStorePosition(), master.getLastSavePosition(),
                  getInternal( master.getLastSavePosition() ) );
          setMaster( state );
          master = state;
        }

        return restoreState( max, master );
      } catch ( Exception rpe ) {
        DefaultPageStateList.logger.debug( "Caught exception while restoring a saved page-state", rpe );
        throw new IllegalStateException( "Something went wrong while trying to restore the child #" + index );
      }
    }

    /**
     * Internal handler function restore a state. Count denotes the number of pages required to be processed to restore
     * the page, when the reportstate master is used as source element.
     *
     * @param count
     *          the count.
     * @param rootstate
     *          the root state.
     * @return the report state.
     * @throws org.pentaho.reporting.engine.classic.core.ReportProcessingException
     *           if there was a problem processing the report.
     */
    private InternalStorageState restoreState( final int count, final InternalStorageState rootstate )
      throws ReportProcessingException {
      if ( rootstate == null ) {
        throw new NullPointerException( "Master is null" );
      }

      InternalStorageState state = rootstate;
      for ( int i = 0; i <= count; i++ ) {
        final ReportProcessor pageProcess = master.getPageProcess();
        final PageState pageState = pageProcess.processPage( state.getPageState(), false );
        if ( pageState == null ) {
          throw new IllegalStateException( "State returned is null: Report processing reached premature end-point." );
        }
        state = new InternalStorageState( pageState, rootstate.getStorePosition() + i, rootstate.getLastSavePosition() );
        if ( state.isValidRestorePoint() ) {
          set( state, i + 1 );
        }
      }
      return state;
    }
  }

  private static class InternalStorageState {
    private PageState pageState;
    private int storePosition;
    private int lastSavePosition;

    private InternalStorageState( final PageState pageState, final int storePosition, final int lastSavePosition ) {
      this.pageState = pageState;
      this.storePosition = storePosition;
      this.lastSavePosition = lastSavePosition;
    }

    public PageState getPageState() {
      return pageState;
    }

    public int getStorePosition() {
      return storePosition;
    }

    public int getLastSavePosition() {
      return lastSavePosition;
    }

    public boolean isValidRestorePoint() {
      if ( pageState == null ) {
        return false;
      }
      return pageState.isSafeToStoreEarly();
    }
  }

  /**
   * The list of master states. This is a list of WeakReferenceLists. These WeakReferenceLists contain their master
   * state as first child. The weakReferenceLists have a maxSize of 10, so every 10th state will protected from being
   * garbageCollected.
   */
  private ArrayList<MasterList> masterStates10; // all states > 120
  /**
   * The list of master states. This is a list of WeakReferenceLists. These WeakReferenceLists contain their master
   * state as first child. The weakReferenceLists have a maxSize of 4, so every 4th state will protected from being
   * garbageCollected.
   */
  private ArrayList<MasterList> masterStates4; // all states from 20 - 120

  /**
   * The list of primary states. This is a list of ReportStates and is used to store the first 20 elements of this state
   * list.
   */
  private ArrayList<InternalStorageState> primaryStates; // all states from 0 - 20

  /**
   * The number of elements in this list.
   */
  private int size;

  private ReportProcessor pageProcess;

  private int primaryPoolSize;
  private int secondaryPoolSize;
  private int secondaryPoolFrequency;
  private int tertiaryPoolFrequency;
  private int lastSafePosition;

  /**
   * Creates a new reportstatelist. The list will be filled using the specified report and output target. Filling of the
   * list is done elsewhere.
   *
   * @param proc
   *          the reportprocessor used to restore lost states (null not permitted).
   * @throws NullPointerException
   *           if the report processor is <code>null</code>.
   */
  public DefaultPageStateList( final ReportProcessor proc ) {
    if ( proc == null ) {
      throw new NullPointerException( "ReportProcessor null" );
    }

    this.pageProcess = proc;

    final ExtendedConfigurationWrapper config = new ExtendedConfigurationWrapper( proc.getConfiguration() );

    this.primaryPoolSize =
        config.getIntProperty( "org.pentaho.reporting.engine.classic.core.performance.pagestates.PrimaryPoolSize",
            PRIMARY_MAX );
    this.secondaryPoolFrequency =
        config.getIntProperty(
            "org.pentaho.reporting.engine.classic.core.performance.pagestates.SecondaryPoolFrequency",
            MASTERPOSITIONS_MED );
    this.secondaryPoolSize =
        config.getIntProperty( "org.pentaho.reporting.engine.classic.core.performance.pagestates.SecondaryPoolSize",
            MASTER4_MAX )
            + primaryPoolSize;
    this.tertiaryPoolFrequency =
        config.getIntProperty(
            "org.pentaho.reporting.engine.classic.core.performance.pagestates.TertiaryPoolFrequency",
            MASTERPOSITIONS_MAX );

    if ( primaryPoolSize < 1 ) {
      throw new IllegalStateException( "Invalid configuration: Primary pool must be >= 1" );
    }
    if ( secondaryPoolSize < primaryPoolSize ) {
      throw new IllegalStateException( "Invalid configuration: Secondary pool must be >= primary pool" );
    }
    if ( secondaryPoolFrequency < 1 ) {
      throw new IllegalStateException( "Invalid configuration: Secondary pool frequency must be >= 1" );
    }
    if ( tertiaryPoolFrequency < 1 ) {
      throw new IllegalStateException( "Invalid configuration: Tertiary pool frequency must be >= 1" );
    }
    primaryStates = new ArrayList<InternalStorageState>( primaryPoolSize );
    masterStates4 = new ArrayList<MasterList>( secondaryPoolSize );
    masterStates10 = new ArrayList<MasterList>();

  }

  /**
   * Returns the index of the WeakReferenceList in the master list.
   *
   * @param pos
   *          the position.
   * @param maxListSize
   *          the maximum list size.
   * @return the position within the masterStateList.
   */
  private int getMasterPos( final int pos, final int maxListSize ) {
    // return (int) Math.floor(pos / maxListSize);
    return ( pos / maxListSize );
  }

  protected ReportProcessor getPageProcess() {
    return pageProcess;
  }

  /**
   * Returns the number of elements in this list.
   *
   * @return the number of elements in the list.
   */
  public int size() {
    return this.size;
  }

  /**
   * Adds this report state to the end of the list.
   *
   * @param pageState
   *          the report state.
   */
  public void add( final PageState pageState ) {
    if ( pageState == null ) {
      throw new NullPointerException();
    }

    final int size = size();
    final InternalStorageState state;
    if ( pageState.isSafeToStoreEarly() == false ) {
      state = new InternalStorageState( null, size, lastSafePosition );
    } else {
      lastSafePosition = size;
      pageState.prepareStorage();
      state = new InternalStorageState( pageState, size, lastSafePosition );
    }

    // the first 20 Elements are stored directly into an ArrayList
    if ( size < primaryPoolSize ) {
      primaryStates.add( state );
      this.size++;
    } else if ( size < secondaryPoolSize ) {
      // the next 100 Elements are stored into a list of 4-element weakReference
      // list. So if an Element gets lost (GCd), only 4 states need to be replayed.
      final int secPos = size - primaryPoolSize;
      final int masterPos = getMasterPos( secPos, secondaryPoolFrequency );
      if ( masterPos >= masterStates4.size() ) {
        final MasterList master = new MasterList( this, secondaryPoolFrequency );
        masterStates4.add( master );
        master.add( state );
      } else {
        final MasterList master = masterStates4.get( masterPos );
        master.add( state );
      }
      this.size++;
    } else {
      // all other Elements are stored into a list of 10-element weakReference
      // list. So if an Element gets lost (GCd), 10 states need to be replayed.
      final int thirdPos = size - secondaryPoolSize;
      final int masterPos = getMasterPos( thirdPos, tertiaryPoolFrequency );
      if ( masterPos >= masterStates10.size() ) {
        final MasterList master = new MasterList( this, tertiaryPoolFrequency );
        masterStates10.add( master );
        master.add( state );
      } else {
        final MasterList master = masterStates10.get( masterPos );
        master.add( state );
      }
      this.size++;
    }
  }

  protected void set( final int index, final InternalStorageState state ) {
    if ( index >= size ) {
      throw new IndexOutOfBoundsException();
    }

    if ( index != state.getStorePosition() ) {
      throw new IllegalArgumentException();
    }

    // the first 20 Elements are stored directly into an ArrayList
    if ( index < primaryPoolSize ) {
      final InternalStorageState o = primaryStates.get( index );
      if ( o.isValidRestorePoint() ) {
        throw new IllegalArgumentException();
      }

      primaryStates.set( index, state );
    } else if ( index < secondaryPoolSize ) {
      // the next 100 Elements are stored into a list of 4-element weakReference
      // list. So if an Element gets lost (GCd), only 4 states need to be replayed.
      final int secPos = index - primaryPoolSize;
      final int masterPos = getMasterPos( secPos, secondaryPoolFrequency );
      if ( masterPos >= masterStates4.size() ) {
        throw new IllegalStateException( "Replacing an existing entry must not generate a new list slot." );
      }

      final MasterList master = masterStates4.get( masterPos );
      final InternalStorageState o = master.getRaw( secPos );
      if ( o != null && o.isValidRestorePoint() ) {
        throw new IllegalArgumentException();
      }

      master.set( state, secPos );
    } else {
      // all other Elements are stored into a list of 10-element weakReference
      // list. So if an Element gets lost (GCd), 10 states need to be replayed.
      final int thirdPos = index - secondaryPoolSize;
      final int masterPos = getMasterPos( thirdPos, tertiaryPoolFrequency );
      if ( masterPos >= masterStates10.size() ) {
        throw new IllegalStateException( "Replacing an existing entry must not generate a new list slot." );
      } else {
        final MasterList master = masterStates10.get( masterPos );
        final InternalStorageState o = master.getRaw( thirdPos );
        if ( o != null && o.isValidRestorePoint() ) {
          throw new IllegalArgumentException();
        }

        master.set( state, thirdPos );
      }
    }
  }

  /**
   * Removes all elements in the list.
   */
  public void clear() {
    masterStates10.clear();
    masterStates4.clear();
    primaryStates.clear();
    this.size = 0;
  }

  /**
   * Retrieves the element on position <code>index</code> in this list.
   *
   * @param index
   *          the index.
   * @return the report state.
   */
  public PageState get( final int index ) {
    if ( index >= size() || index < 0 ) {
      throw new IndexOutOfBoundsException( "Index is invalid. Index was " + index + "; size was " + size() );
    }
    final InternalStorageState internal = getInternal( index );
    if ( internal.isValidRestorePoint() == false ) {
      try {
        // From the 'internal' pagestate we know the worst case position where we can find a
        // page state to restore our report processing from. But maybe we have a cheaper solution
        // inbetween, maybe from previous restore runs. So we backtrack from the current target
        // to the worst case and start restoring from any earlier states.

        final int targetPageCursor = internal.getStorePosition();
        final int stateCounter = internal.getLastSavePosition();
        for ( int i = targetPageCursor - 1; i >= stateCounter; i -= 1 ) {
          final InternalStorageState startState = getInternal( i );
          if ( startState.isValidRestorePoint() ) {
            final InternalStorageState internalStorageState = restoreState( targetPageCursor, i, startState );
            return internalStorageState.getPageState();
          }
        }
        throw new IllegalStateException();
      } catch ( ReportProcessingException e ) {
        throw new IllegalStateException( e );
      }
    }
    return internal.getPageState();
  }

  /**
   * Internal handler function restore a state. Count denotes the number of pages required to be processed to restore
   * the page, when the reportstate master is used as source element.
   *
   * @param pageCursor
   *          the page cursor for the end state of the restore sequence.
   * @param lastSaveState
   *          the page cursor for the start state of the restore sequence.
   * @param rootstate
   *          the root state.
   * @return the report state.
   * @throws org.pentaho.reporting.engine.classic.core.ReportProcessingException
   *           if there was a problem processing the report.
   */
  private InternalStorageState restoreState( final int pageCursor, final int lastSaveState,
      final InternalStorageState rootstate ) throws ReportProcessingException {
    logger.info( "Restoring global state " + pageCursor + " from " + lastSaveState );
    if ( rootstate == null ) {
      throw new NullPointerException( "Master is null" );
    }

    if ( rootstate.isValidRestorePoint() == false ) {
      throw new IllegalArgumentException();
    }

    InternalStorageState state = rootstate;
    for ( int i = lastSaveState; i < pageCursor; i++ ) {
      final ReportProcessor pageProcess = getPageProcess();
      final PageState pageState = pageProcess.processPage( state.getPageState(), false );
      if ( pageState == null ) {
        throw new IllegalStateException( "State returned is null: Report processing reached premature end-point." );
      }
      state = new InternalStorageState( pageState, i + 1, rootstate.getLastSavePosition() );
      if ( pageState.isSafeToStoreEarly() ) {
        set( i + 1, state );
      }
    }
    return state;
  }

  private InternalStorageState getInternal( int index ) {
    if ( index < primaryPoolSize ) {
      return primaryStates.get( index );
    } else if ( index < secondaryPoolSize ) {
      index -= primaryPoolSize;
      final MasterList master = masterStates4.get( getMasterPos( index, secondaryPoolFrequency ) );
      return (InternalStorageState) master.get( index );
    } else {
      index -= secondaryPoolSize;
      final MasterList master = masterStates10.get( getMasterPos( index, tertiaryPoolFrequency ) );
      return (InternalStorageState) master.get( index );
    }
  }
}
