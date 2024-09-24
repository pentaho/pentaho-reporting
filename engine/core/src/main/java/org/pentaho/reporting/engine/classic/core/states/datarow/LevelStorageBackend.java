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

package org.pentaho.reporting.engine.classic.core.states.datarow;

import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.Function;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;
import org.pentaho.reporting.engine.classic.core.util.IntList;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;
import org.pentaho.reporting.engine.classic.core.util.LevelList;

import java.util.Iterator;

public class LevelStorageBackend {
  private static final Integer[] EMPTY_INTEGERARRAY = new Integer[0];

  private static class LevelStorageImpl implements LevelStorage {
    private LevelStorageBackend levelData;
    private Expression[] expressions;

    private LevelStorageImpl( final LevelStorageBackend levelData, final Expression[] expressions ) {
      this.levelData = levelData;
      this.expressions = expressions;
    }

    public int getLevelNumber() {
      return levelData.getLevelNumber();
    }

    public Iterator<Function> getFunctions() {
      return new IndirectIndexIterator<Function>( levelData.getFunctions(), expressions );
    }

    public Iterator<Function> getPageFunctions() {
      return new IndirectIndexIterator<Function>( levelData.getPageEventListeners(), expressions );
    }

    public Iterator<Expression> getActiveExpressions() {
      return new IndirectIndexIterator<Expression>( levelData.getActiveExpressions(), expressions );
    }
  }

  private int levelNumber;
  private int[] activeExpressions;
  private int[] functions;
  private int[] pageEventListeners;

  public LevelStorageBackend( final int levelNumber, final int[] activeExpressions, final int[] functions,
      final int[] pageEventListeners ) {
    if ( pageEventListeners == null ) {
      throw new NullPointerException();
    }
    if ( activeExpressions == null ) {
      throw new NullPointerException();
    }
    if ( functions == null ) {
      throw new NullPointerException();
    }

    this.levelNumber = levelNumber;
    this.activeExpressions = activeExpressions;
    this.functions = functions;
    this.pageEventListeners = pageEventListeners;
  }

  public int getLevelNumber() {
    return levelNumber;
  }

  public int[] getFunctions() {
    return functions;
  }

  /**
   * @return Returns the activeExpressions.
   */
  public int[] getActiveExpressions() {
    return activeExpressions;
  }

  /**
   * @return Returns the pageEventListeners.
   */
  public int[] getPageEventListeners() {
    return pageEventListeners;
  }

  public boolean hasPageEventListeners() {
    return pageEventListeners != null && pageEventListeners.length > 0;
  }

  public static LevelStorageBackend[] revalidate( final Expression[] expressions, final int length,
      final boolean includeStructuralProcessing ) {
    // recompute the level storage ..
    final LevelList levelList = new LevelList();
    int minLevel = Integer.MIN_VALUE;
    for ( int i = 0; i < length; i++ ) {
      final Expression expression = expressions[i];

      // The list maps the current position to the level ..
      final int dependencyLevel = expression.getDependencyLevel();
      levelList.add( IntegerCache.getInteger( i ), dependencyLevel );
      if ( minLevel < dependencyLevel ) {
        minLevel = dependencyLevel;
      }
    }

    if ( includeStructuralProcessing ) {
      if ( minLevel > Integer.MIN_VALUE ) {
        for ( int i = 0; i < length; i++ ) {
          final Expression expression = expressions[i];

          // The list maps the current position to the level ..
          final int dependencyLevel = expression.getDependencyLevel();
          if ( dependencyLevel == minLevel && ( expression instanceof Function == false ) ) {
            // add this expression to the structural pre-processing so that it can influence the
            // construction of the crosstab if needed.
            levelList.add( IntegerCache.getInteger( i ), LayoutProcess.LEVEL_STRUCTURAL_PREPROCESSING );
          }
        }
      }
    }

    final Integer[] levels = levelList.getLevelsDescendingArray();
    final LevelStorageBackend[] levelData = new LevelStorageBackend[levels.length];
    final int expressionsCount = levelList.size();

    final int capacity = Math.min( 20, expressionsCount );
    final IntList activeExpressions = new IntList( capacity );
    final IntList functions = new IntList( capacity );
    final IntList pageEventListeners = new IntList( capacity );

    for ( int i = 0; i < levels.length; i++ ) {
      final int currentLevel = levels[i].intValue();
      final Integer[] data = (Integer[]) levelList.getElementArrayForLevel( currentLevel, EMPTY_INTEGERARRAY );
      for ( int x = 0; x < data.length; x++ ) {
        final Integer position = data[x];
        final Expression ex = expressions[position.intValue()];
        final int globalPosition = position.intValue();

        if ( ex instanceof Function == false ) {
          if ( ex.getName() != null ) {
            activeExpressions.add( globalPosition );
          }
          continue;
        }

        activeExpressions.add( globalPosition );
        functions.add( globalPosition );
        if ( ex instanceof PageEventListener ) {
          pageEventListeners.add( globalPosition );
        }
      }

      levelData[i] =
          new LevelStorageBackend( currentLevel, activeExpressions.toArray(), functions.toArray(), pageEventListeners
              .toArray() );

      activeExpressions.clear();
      functions.clear();
      pageEventListeners.clear();
    }

    return levelData;
  }

  public static LevelStorage getLevelStorage( final LevelStorageBackend levelStorageBackend,
      final Expression[] expressions ) {
    return new LevelStorageImpl( levelStorageBackend, expressions );
  }
}
