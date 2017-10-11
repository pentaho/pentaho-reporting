/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.designer.core.editor.crosstab;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.CrosstabCell;
import org.pentaho.reporting.engine.classic.core.CrosstabCellBody;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabHeader;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabSummaryHeader;
import org.pentaho.reporting.engine.classic.core.CrosstabTitleHeader;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.elementfactory.CrosstabBuilder;
import org.pentaho.reporting.engine.classic.core.elementfactory.CrosstabDetail;
import org.pentaho.reporting.engine.classic.core.elementfactory.CrosstabDimension;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.base.util.HashNMap;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * A simple support class to preserve some existing information when editing crosstabs. This class preserves details
 * cells if the details definitions have not changed. This class preserves row and column dimensions if the details of
 * these dimensions have not changed.
 */
public class CrosstabEditorBuilder extends CrosstabBuilder {
  // todo: v2 - try to be more granular on how dimensions are restored.
  // A change in a label should only regenerate the particular header.


  private static final Log logger = LogFactory.getLog( CrosstabEditorBuilder.class );

  private static class Tuple {
    private CrosstabDimension dimension;
    private Group group;

    private Tuple( final CrosstabDimension dimension, final Group group ) {
      this.dimension = dimension;
      this.group = group;
    }

    public CrosstabDimension getDimension() {
      return dimension;
    }

    public Group getGroup() {
      return group;
    }
  }

  private final HashNMap<String, Tuple> predefinedGroups;
  private final CrosstabCellBody cellBody;
  private final LinkedHashMap<String, CrosstabEditSupport.DetailsDefinition> details;
  private Boolean detailsChanged;

  public CrosstabEditorBuilder( final ContextAwareDataSchemaModel dataSchemaModel,
                                final CrosstabCellBody cellBody,
                                final LinkedHashMap<String, CrosstabEditSupport.DetailsDefinition> details ) {
    super( dataSchemaModel );
    ArgumentNullException.validate( "cellBody", cellBody );
    ArgumentNullException.validate( "details", details );

    this.details = details;
    this.predefinedGroups = new HashNMap<String, Tuple>();
    this.cellBody = cellBody;
  }

  protected boolean isDetailsChanged() {
    if ( detailsChanged != null ) {
      return detailsChanged.booleanValue();
    }

    final Iterator<CrosstabDetail> detailsFromBuilder = getDetails().iterator();
    final Iterator<CrosstabEditSupport.DetailsDefinition> detailsFromPast = details.values().iterator();
    while ( detailsFromBuilder.hasNext() && detailsFromPast.hasNext() ) {
      final CrosstabDetail next = detailsFromBuilder.next();
      final CrosstabDetail detail = detailsFromPast.next().createDetail();
      if ( ObjectUtilities.equal( next, detail ) == false ) {
        logger.debug( String.format( "Details do not match up: [%s] vs [%s]", next, detail ) );
        detailsChanged = true;
        return true;
      }
    }
    if ( detailsFromBuilder.hasNext() ) {
      logger.debug( String.format( "Detail count does not fit: More current details than past ones." ) );
      detailsChanged = true;
      return true;
    }
    if ( detailsFromPast.hasNext() ) {
      logger.debug( String.format( "Detail count does not fit: More past details than current ones." ) );
      detailsChanged = true;
      return true;
    }
    logger.debug( String.format( "Details have not changed. This is good, we can preserve the cells." ) );
    detailsChanged = false;
    return false;
  }

  protected CrosstabOtherGroup createOtherGroup( final GroupBody body, final String column ) {
    final Tuple tuple = predefinedGroups.getLast( column );
    final Group other = tuple.getGroup();
    if ( other instanceof CrosstabOtherGroup ) {
      predefinedGroups.remove( column, tuple );

      logger.debug( String.format( "Preserving existing other group " + column ) );
      // 1:1 mapping, this should be ok as it is ..
      final CrosstabOtherGroup g = (CrosstabOtherGroup) other.derive( true );
      g.setBody( body );
      return g;
    } else {
      return super.createOtherGroup( body, column );
    }
  }

  protected CrosstabRowGroup createRowGroup( final CrosstabCellBody cellBody,
                                             final GroupBody innerBody,
                                             final CrosstabDimension rowDimension ) {
    final String column = rowDimension.getField();
    final Tuple tuple = predefinedGroups.getLast( column );
    if ( tuple == null ) {
      return super.createRowGroup( cellBody, innerBody, rowDimension );
    }

    final Group other = tuple.group;
    if ( ObjectUtilities.equal( tuple.dimension, rowDimension ) ) {
      if ( other instanceof CrosstabRowGroup ) {
        predefinedGroups.remove( column, tuple );
        logger.debug( String.format( "Preserving existing row group " + column ) );

        final CrosstabRowGroup og = (CrosstabRowGroup) other.derive( true );
        og.setBody( innerBody );
        createSummaryCells( cellBody, rowDimension );
        return og;
      } else if ( other instanceof CrosstabColumnGroup ) {
        predefinedGroups.remove( column, tuple );
        logger.debug( String.format( "Mapping column group into row group " + column ) );

        final CrosstabColumnGroup oc = (CrosstabColumnGroup) other;
        final CrosstabRowGroup cg = new CrosstabRowGroup( innerBody );
        cg.setHeader( (CrosstabHeader) oc.getHeader().derive( true ) );
        cg.setTitleHeader( (CrosstabTitleHeader) oc.getTitleHeader().derive( true ) );
        cg.setSummaryHeader( (CrosstabSummaryHeader) oc.getSummaryHeader().derive( true ) );
        createSummaryCells( cellBody, rowDimension );
        return cg;
      }
    } else {
      logger.debug( String.format( "Dimension definition has changed on row dimension " + column ) );
    }

    return super.createRowGroup( cellBody, innerBody, rowDimension );
  }

  protected CrosstabColumnGroup createColumnGroup( final CrosstabCellBody cellBody,
                                                   final GroupBody innerBody,
                                                   final CrosstabDimension colDimension ) {
    final String column = colDimension.getField();
    final Tuple tuple = predefinedGroups.getLast( column );
    if ( tuple == null ) {
      return super.createColumnGroup( cellBody, innerBody, colDimension );
    }

    final Group other = tuple.group;
    if ( ObjectUtilities.equal( tuple.dimension, colDimension ) ) {
      if ( other instanceof CrosstabColumnGroup ) {
        predefinedGroups.remove( column, tuple );
        logger.debug( String.format( "Preserving existing column group " + column ) );

        final CrosstabColumnGroup og = (CrosstabColumnGroup) other.derive( true );
        og.setBody( innerBody );
        createSummaryCells( cellBody, colDimension );
        return og;
      } else if ( other instanceof CrosstabRowGroup ) {
        predefinedGroups.remove( column, tuple );
        logger.debug( String.format( "Mapping row group into column group " + column ) );

        final CrosstabRowGroup oc = (CrosstabRowGroup) other;
        final CrosstabColumnGroup cg = new CrosstabColumnGroup( innerBody );
        cg.setHeader( (CrosstabHeader) oc.getHeader().derive( true ) );
        cg.setTitleHeader( (CrosstabTitleHeader) oc.getTitleHeader().derive( true ) );
        cg.setSummaryHeader( (CrosstabSummaryHeader) oc.getSummaryHeader().derive( true ) );
        createSummaryCells( cellBody, colDimension );
        return cg;
      }
    } else {
      logger.debug( String.format( "Dimension definition has changed on column dimension " + column ) );
    }

    return super.createColumnGroup( cellBody, innerBody, colDimension );
  }

  protected CrosstabCell createDetailsCell( final String name, final String rowDim, final String colDim ) {
    if ( !isDetailsChanged() ) {
      logger.debug( String.format( "Found existing details cell " + name ) );
      final CrosstabCell element = cellBody.findElement( rowDim, colDim );
      if ( element != null ) {
        return element;
      }
    }
    return super.createDetailsCell( name, rowDim, colDim );
  }

  protected CrosstabCellBody createCellBody() {
    if ( !isDetailsChanged() ) {
      return cellBody.derive( true );
    } else {
      return super.createCellBody();
    }
  }

  public void addOtherDimension( final CrosstabOtherGroup other ) {
    addOtherDimension( other.getField() );
    predefinedGroups.add( other.getField(), new Tuple( new CrosstabDimension( other.getField() ), other ) );
  }

  public void addRowDimension( final CrosstabDimension dimension, final CrosstabRowGroup rowGroup ) {
    addRowDimension( dimension );
    predefinedGroups.add( rowGroup.getField(), new Tuple( dimension.clone(), rowGroup ) );
  }

  public void addColumnDimension( final CrosstabDimension dimension, final CrosstabColumnGroup rowGroup ) {
    addColumnDimension( dimension );
    predefinedGroups.add( rowGroup.getField(), new Tuple( dimension.clone(), rowGroup ) );
    detailsChanged = null;
  }

  public CrosstabBuilder clearDimensions() {
    final CrosstabBuilder crosstabBuilder = super.clearDimensions();
    detailsChanged = null;
    return crosstabBuilder;
  }
}
