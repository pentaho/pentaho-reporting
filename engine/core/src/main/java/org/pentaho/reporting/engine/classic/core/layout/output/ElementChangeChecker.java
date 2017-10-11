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

package org.pentaho.reporting.engine.classic.core.layout.output;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ElementChangeChecker {
  private static class NeedEvalResult implements Serializable {
    private boolean needToRun;
    private long changeTracker;
    private long styleChangeTracker;
    private long styleModificationCounter;
    private HashMap<String, Object> fieldsAndValues;

    private NeedEvalResult( final boolean needToRun, final ReportElement e,
        final HashMap<String, Object> fieldsAndValues ) {
      this.needToRun = needToRun;
      this.changeTracker = e.getChangeTracker();
      this.fieldsAndValues = fieldsAndValues;
      this.styleChangeTracker = e.getStyle().getChangeTracker();
      this.styleModificationCounter = e.getStyle().getModificationCount();
    }

    public boolean isNeedToRun() {
      return needToRun;
    }

    public long getChangeTracker() {
      return changeTracker;
    }

    public boolean isValid( final ReportElement e, final DataRow dataRow ) {
      if ( changeTracker != e.getChangeTracker() || styleChangeTracker != e.getStyle().getChangeTracker()
          || styleModificationCounter != e.getStyle().getModificationCount() ) {
        return false;
      }

      for ( final Map.Entry<String, Object> entry : fieldsAndValues.entrySet() ) {
        final String field = entry.getKey();
        final Object oldValue = entry.getValue();
        final Object currentValue = dataRow.get( field );
        if ( ObjectUtilities.equal( oldValue, currentValue ) == false ) {
          return false;
        }
      }

      return true;
    }
  }

  private static class ElementMetaDataEvaluationResult implements Serializable {
    private long changeTracker;
    private long styleChangeTracker;
    private long styleModificationCounter;
    private HashMap<String, Object> seenFields;

    private ElementMetaDataEvaluationResult( final ReportElement e, final HashMap<String, Object> seenFields ) {
      this.seenFields = seenFields;
      changeTracker = e.getChangeTracker();
      styleChangeTracker = e.getStyle().getChangeTracker();
      styleModificationCounter = e.getStyle().getModificationCount();
    }

    public boolean isValid( final ReportElement e, final DataRow dataRow ) {
      if ( changeTracker != e.getChangeTracker() || styleChangeTracker != e.getStyle().getChangeTracker()
          || styleModificationCounter != e.getStyle().getModificationCount() ) {
        return false;
      }

      for ( final Map.Entry<String, Object> entry : seenFields.entrySet() ) {
        final String field = entry.getKey();
        final Object oldValue = entry.getValue();
        final Object currentValue = dataRow.get( field );
        if ( ObjectUtilities.equal( oldValue, currentValue ) == false ) {
          return false;
        }
      }

      return true;
    }

  }

  private static class PerformanceCollector {
    public int totalEvaluations;
    public int evaluations;
    public int skippedEvaluations;
  }

  private final Log performanceLogger = LogFactory.getLog( getClass() );
  private PerformanceCollector performanceCollector;
  private String attrName;
  private String elementAttribute;

  private DataRow currentDataRow;
  private HashMap<String, Object> currentFieldsAndValues;

  public ElementChangeChecker() {
    performanceCollector = new PerformanceCollector();
    attrName = "ElementChangeTracker-NeedResult@" + System.identityHashCode( this );
    elementAttribute = "ElementChangeTracker-DetailResult@" + System.identityHashCode( this );
    currentFieldsAndValues = new HashMap<String, Object>();
  }

  public boolean isBandChanged( final Section b, final DataRow dataRow ) {
    this.currentFieldsAndValues.clear();
    this.currentDataRow = dataRow;
    try {
      return processRootBand( b );
    } finally {
      this.currentFieldsAndValues.clear();
      this.currentDataRow = null;
    }
  }

  /**
   * Evaluates all style expressions from all elements and updates the style-sheet if needed.
   *
   * @param b
   *          the band.
   * @return true if the element needs reprinting.
   */
  protected final boolean processRootBand( final Section b ) {
    if ( b == null ) {
      return false;
    }

    final NeedEvalResult needToRun = (NeedEvalResult) b.getAttribute( AttributeNames.Internal.NAMESPACE, attrName );
    if ( needToRun != null ) {
      if ( needToRun.isNeedToRun() == false ) {
        if ( needToRun.isValid( b, currentDataRow ) ) {
          recordCacheHit( b );
          return false;
        }
      }
    }

    recordCacheMiss( b );

    final boolean needToRunVal = processBand( b );
    b.setAttribute( AttributeNames.Internal.NAMESPACE, attrName, new NeedEvalResult( needToRunVal, b,
        (HashMap<String, Object>) currentFieldsAndValues.clone() ), false );
    return true;
  }

  protected boolean evaluateElement( final ReportElement e ) {
    final ElementMetaDataEvaluationResult evalResult =
        (ElementMetaDataEvaluationResult) e.getAttribute( AttributeNames.Internal.NAMESPACE, elementAttribute );
    if ( evalResult != null && evalResult.isValid( e, currentDataRow ) ) {
      currentFieldsAndValues.putAll( evalResult.seenFields );
      return false;
    }

    final HashMap<String, Object> values = new HashMap<String, Object>();
    final ElementMetaData metaData = e.getElementType().getMetaData();
    final AttributeMetaData[] attributeDescriptions = metaData.getAttributeDescriptions();
    for ( int i = 0; i < attributeDescriptions.length; i++ ) {
      final AttributeMetaData attributeDescription = attributeDescriptions[i];
      final Object attribute = e.getAttribute( attributeDescription.getNameSpace(), attributeDescription.getName() );
      if ( attribute == null ) {
        continue;
      }

      final String[] referencedFields = attributeDescription.getReferencedFields( e, attribute );
      for ( int j = 0; j < referencedFields.length; j++ ) {
        final String field = referencedFields[j];
        final Object value = currentDataRow.get( field );
        values.put( field, value );
        currentFieldsAndValues.put( field, value );
      }
    }

    final ElementMetaDataEvaluationResult current = new ElementMetaDataEvaluationResult( e, values );
    e.setAttribute( AttributeNames.Internal.NAMESPACE, elementAttribute, current, false );
    return true;
  }

  protected final boolean processBand( final Section b ) {
    boolean hasAttrExpressions = evaluateElement( b );

    final int length = b.getElementCount();
    for ( int i = 0; i < length; i++ ) {
      final Element element = b.getElement( i );

      final ElementMetaData.TypeClassification reportElementType = element.getMetaData().getReportElementType();
      if ( reportElementType == ElementMetaData.TypeClassification.DATA
          || reportElementType == ElementMetaData.TypeClassification.CONTROL
          || reportElementType == ElementMetaData.TypeClassification.SUBREPORT || element instanceof Section == false ) {
        if ( evaluateElement( element ) ) {
          hasAttrExpressions = true;
        }
      } else {
        final Section section = (Section) element;
        if ( processBand( section ) ) {
          hasAttrExpressions = true;
        }
      }
    }

    return hasAttrExpressions;
  }

  protected void recordCacheHit( final ReportElement e ) {
    performanceCollector.totalEvaluations += 1;
    performanceCollector.skippedEvaluations += 1;
  }

  protected void recordCacheMiss( final ReportElement e ) {
    performanceCollector.totalEvaluations += 1;
    performanceCollector.evaluations += 1;
  }

  protected void reportCachePerformance() {
    if ( performanceLogger.isInfoEnabled() ) {
      performanceLogger.info( String.format( "Performance: %s => total=%d, evaluated=%d (%f%%), avoided=%d (%f%%)",
          getClass(), performanceCollector.totalEvaluations, performanceCollector.evaluations, 100f
              * performanceCollector.evaluations / Math.max( 1.0f, performanceCollector.totalEvaluations ),
          performanceCollector.skippedEvaluations, 100f * performanceCollector.skippedEvaluations
              / Math.max( 1.0f, performanceCollector.totalEvaluations ) ) );
    }
  }

}
