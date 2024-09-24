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

package org.pentaho.reporting.engine.classic.core.function.sys;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractElementFormatFunction;
import org.pentaho.reporting.engine.classic.core.function.FunctionUtilities;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.layout.style.DefaultStyleCache;
import org.pentaho.reporting.engine.classic.core.layout.style.StyleCache;
import org.pentaho.reporting.engine.classic.core.states.ReportState;
import org.pentaho.reporting.engine.classic.core.states.process.ReportProcessStore;
import org.pentaho.reporting.engine.classic.core.style.ResolverStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.css.CSSStyleResolver;
import org.pentaho.reporting.engine.classic.core.style.resolver.StyleResolver;
import org.pentaho.reporting.engine.classic.core.util.DoubleKeyedCounter;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.config.ExtendedConfiguration;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class StyleResolvingEvaluator extends AbstractElementFormatFunction implements StructureFunction {
  private static class StyleResolverCacheEntry implements Serializable {
    private long elementChangeTracker;
    private long styleChangeHash;
    private long styleModificationCount;

    private StyleResolverCacheEntry( final long elementChangeTracker,
                                     final long styleChangeHash,
                                     final long styleModificationCount ) {
      this.elementChangeTracker = elementChangeTracker;
      this.styleChangeHash = styleChangeHash;
      this.styleModificationCount = styleModificationCount;
    }

    public StyleResolverCacheEntry( final StyleResolverCacheEntry parentEntry, final ReportElement e ) {
      if ( parentEntry == null ) {
        this.elementChangeTracker = e.getChangeTracker();
        this.styleChangeHash = e.getStyle().getChangeTrackerHash();
        this.styleModificationCount = e.getStyle().getModificationCount();
      } else {
        this.elementChangeTracker = parentEntry.getElementChangeTracker() * 31 + e.getChangeTracker();
        this.styleChangeHash = parentEntry.getStyleChangeHash() * 31 + e.getStyle().getChangeTrackerHash();
        this.styleModificationCount =
          parentEntry.getStyleModificationCount() * 31 + e.getStyle().getModificationCount();
      }
    }

    public long getElementChangeTracker() {
      return elementChangeTracker;
    }

    public long getStyleChangeHash() {
      return styleChangeHash;
    }

    public long getStyleModificationCount() {
      return styleModificationCount;
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final StyleResolverCacheEntry that = (StyleResolverCacheEntry) o;

      if ( elementChangeTracker != that.elementChangeTracker ) {
        return false;
      }
      if ( styleChangeHash != that.styleChangeHash ) {
        return false;
      }
      if ( styleModificationCount != that.styleModificationCount ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result = (int) ( elementChangeTracker ^ ( elementChangeTracker >>> 32 ) );
      result = 31 * result + (int) ( styleChangeHash ^ ( styleChangeHash >>> 32 ) );
      result = 31 * result + (int) ( styleModificationCount ^ ( styleModificationCount >>> 32 ) );
      return result;
    }

    public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append( "StyleResolverCacheEntry" );
      sb.append( "{elementChangeTracker=" ).append( elementChangeTracker );
      sb.append( ", styleChangeHash=" ).append( styleChangeHash );
      sb.append( ", styleModificationCount=" ).append( styleModificationCount );
      sb.append( '}' );
      return sb.toString();
    }
  }

  private static final Log logger = LogFactory.getLog( StyleResolvingEvaluator.class );
  private static final StyleResolverCacheEntry INVALID = new StyleResolverCacheEntry( 0, 0, 0 );
  private static final String DETAILED_STATISTICS_CONFIG =
    StyleResolvingEvaluator.class.getName() + ".CollectDetailedStatistics";

  private transient StyleResolver resolver;
  private transient ResolverStyleSheet styleSheet;
  private transient StyleCache styleCache;
  private DoubleKeyedCounter<String, Long> statisticsHit;
  private DoubleKeyedCounter<String, Long> statisticsMiss;
  private boolean collectDetailedStatistics;

  public StyleResolvingEvaluator() {
    statisticsHit = new DoubleKeyedCounter<String, Long>();
    statisticsMiss = new DoubleKeyedCounter<String, Long>();
    ExtendedConfiguration config = ClassicEngineBoot.getInstance().getExtendedConfig();
    collectDetailedStatistics = config.getBoolProperty( DETAILED_STATISTICS_CONFIG );
  }

  public void reportInitialized( final ReportEvent event ) {
    if ( FunctionUtilities.isLayoutLevel( event ) == false ) {
      // dont do anything if there is no printing done ...
      return;
    }

    ReportDefinition reportDefinition = locateMasterReport( event.getState() );
    resolver = createStyleResolver( reportDefinition,
                                    getRuntime().getProcessingContext(), event.getState().getProcessStore() );
    styleCache = createCache( event.getReport(), event.getState().getProcessStore() );
    styleSheet = createSharedResolverStyleSheet( reportDefinition, event.getState().getProcessStore() );
    super.reportInitialized( event );
  }

  /**
   * The resolver style sheet is a temporary, reused style sheet. We always create a copy at the
   * end, and thus this object can be safely shared across StyleResolvingEvaluator instances across
   * the same report process.
   *
   * @return the shared resolver stylesheet.
   */
  private ResolverStyleSheet createSharedResolverStyleSheet( ReportDefinition report,
                                                             ReportProcessStore store ) {
    Map<InstanceID, ResolverStyleSheet> cache = store.get( StyleResolvingEvaluator.class.getName() + "#ResolverStyleSheet" );
    ResolverStyleSheet c = cache.get( report.getObjectID() );
    if ( c == null ) {
      c = new ResolverStyleSheet();
      cache.put( report.getObjectID(), c );
    }
    return c;
  }

  /**
   * The style cache is stored per defined (sub)report. This keeps similar report elements together,
   * without creating too many parallel cache instances when the same subreports get instantiated over
   * and over again.
   *
   * @return the cache for this report.
   */
  private StyleCache createCache( ReportDefinition report, ReportProcessStore store ) {
    Map<InstanceID, StyleCache> cache = store.get( StyleResolvingEvaluator.class.getName() + "#Cache" );
    StyleCache c = cache.get( report.getObjectID() );
    if ( c == null ) {
      c = new DefaultStyleCache( "StyleResolver" );
      cache.put( report.getObjectID(), c );
    }
    return c;
  }

  private ReportDefinition locateMasterReport( final ReportState state ) {
    if ( state.isSubReportEvent() ) {
      ReportState parentState = state.getParentState();
      if ( parentState != null ) {
        return locateMasterReport( parentState );
      }
    }

    return state.getReport();
  }

  /**
   * Returns a potentially shared instance (per report process, thus thread-safe) of the style resolver.
   * The stylesheets are defined on the master-report level, and thus only need to be loaded once. Subreports
   * that want to resolve styles can all share the same definitions.
   *
   * @param reportDefinition
   * @param pc
   * @param store
   * @return
   */
  private StyleResolver createStyleResolver( final ReportDefinition reportDefinition,
                                             final ProcessingContext pc,
                                             final ReportProcessStore store ) {

    Map<InstanceID, StyleResolver> cache = store.get( StyleResolvingEvaluator.class.getName() + "#Resolver" );
    StyleResolver o = cache.get( reportDefinition.getObjectID() );
    if ( o != null ) {
      return o;
    }

    final ResourceManager resourceManager = pc.getResourceManager();
    final ResourceKey contentBase = pc.getContentBase();

    final StyleResolver res =
      CSSStyleResolver.createDesignTimeResolver( reportDefinition, resourceManager, contentBase, false );
    cache.put( reportDefinition.getObjectID(), res );
    return res;
  }

  protected void recordCacheHit( final ReportElement e ) {
    super.recordCacheHit( e );
    if ( collectDetailedStatistics ) {
      statisticsHit.increaseCounter( e.getElementType().getMetaData().getName(), e.getChangeTracker() );
    }
  }

  protected void recordCacheMiss( final ReportElement e ) {
    super.recordCacheMiss( e );
    if ( collectDetailedStatistics ) {
      statisticsMiss.increaseCounter( e.getElementType().getMetaData().getName(), e.getChangeTracker() );
    }
  }

  protected void reportCachePerformance() {
    super.reportCachePerformance();
    if ( collectDetailedStatistics ) {
      logger.debug( statisticsHit.printStatistic() + "\n" + statisticsMiss.printStatistic() );
    }
  }

  protected boolean evaluateElement( final ReportElement e ) {
    final StyleResolverCacheEntry parentEntry = get( e.getParentSection() );
    final StyleResolverCacheEntry existingEntry = get( e );
    final StyleResolverCacheEntry currentEntry = new StyleResolverCacheEntry( parentEntry, e );
    if ( currentEntry.equals( existingEntry ) ) {
      return false;
    }

    try {
      e.setAttribute( AttributeNames.Internal.NAMESPACE, "style-resolver-change-tracker", currentEntry, false );
      resolver.resolve( e, styleSheet );
      e.setComputedStyle( styleCache.getStyleSheet( styleSheet ) );
      return true;
    } catch ( Exception ex ) {
      throw new InvalidReportStateException( "Failed to resolve style.", ex );
    }
  }

  private StyleResolverCacheEntry get( final ReportElement element ) {
    if ( element == null ) {
      return null;
    }

    final Object attribute = element.getAttribute( AttributeNames.Internal.NAMESPACE, "style-resolver-change-tracker" );
    final StyleResolverCacheEntry cacheEntry;
    if ( attribute instanceof StyleResolverCacheEntry ) {
      cacheEntry = (StyleResolverCacheEntry) attribute;
    } else {
      cacheEntry = null;
    }
    return cacheEntry;
  }

  public int getProcessingPriority() {
    // run after the style-expressions have been evaluated. Hard-coded styles on the element have the effect
    // of a style-attribute in HTML - they override everything that has been resolved elsewhere.
    return 50000;
  }

  public void reportDone( final ReportEvent event ) {
    if ( FunctionUtilities.isLayoutLevel( event ) == false ) {
      // dont do anything if there is no printing done ...
      return;
    }

    super.reportDone( event );
    //    logger.info(styleCache.printPerformanceStats() + "\n" + resolver.toString());
  }


  /**
   * Helper method for serialization.
   *
   * @param in the input stream from where to read the serialized object.
   * @throws java.io.IOException    when reading the stream fails.
   * @throws ClassNotFoundException if a class definition for a serialized object could not be found.
   */
  private void readObject( final ObjectInputStream in )
    throws IOException, ClassNotFoundException {
    in.defaultReadObject();
  }

  public StyleResolvingEvaluator getInstance() {
    final StyleResolvingEvaluator expression = (StyleResolvingEvaluator) super.getInstance();
    expression.statisticsHit = new DoubleKeyedCounter<String, Long>();
    expression.statisticsMiss = new DoubleKeyedCounter<String, Long>();
    return expression;
  }
}
