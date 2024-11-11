/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.designer.core.editor.report.layouting;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.SubReportType;
import org.pentaho.reporting.engine.classic.core.layout.TextProducer;
import org.pentaho.reporting.engine.classic.core.layout.build.DefaultLayoutModelBuilder;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.style.SubReportStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.AbstractStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class DesignerLayoutModelBuilder extends DefaultLayoutModelBuilder {
  private static class DesignerSubReportStyleSheet extends AbstractStyleSheet {
    private StyleSheet parent;
    private Float minHeight;

    public DesignerSubReportStyleSheet( final StyleSheet parent ) {
      if ( parent == null ) {
        throw new NullPointerException();
      }
      this.parent = parent;

      final float minHeightDefined = (float) parent.getDoubleStyleProperty( ElementStyleKeys.MIN_HEIGHT, 0 );
      this.minHeight = Math.max( 10f, minHeightDefined );
    }

    public StyleSheet getParent() {
      return parent;
    }

    public InstanceID getId() {
      return parent.getId();
    }

    public long getChangeTracker() {
      return parent.getChangeTracker();
    }

    public Object getStyleProperty( final StyleKey key, final Object defaultValue ) {
      if ( ElementStyleKeys.MIN_HEIGHT.equals( key ) ) {
        return minHeight;
      }
      return parent.getStyleProperty( key, defaultValue );
    }

    public Object[] toArray() {
      final Object[] objects = parent.toArray();
      objects[ ElementStyleKeys.MIN_HEIGHT.getIdentifier() ] = minHeight;
      return objects;
    }
  }

  private DesignerRenderComponentFactory renderComponentFactory;

  public DesignerLayoutModelBuilder( final String legacySectionName,
                                     final DesignerRenderComponentFactory renderComponentFactory ) {
    super( legacySectionName );
    this.renderComponentFactory = renderComponentFactory;
  }

  protected TextProducer createTextProducer() {
    return renderComponentFactory.createTextProducer();
  }

  public void startSubFlow( final ReportElement element ) {
    final StyleSheet resolverStyleSheet = element.getComputedStyle();

    final RenderBox box;
    if ( getMetaData().isFeatureSupported( OutputProcessorFeature.STRICT_COMPATIBILITY ) ) {
      final StyleSheet styleSheet = new DesignerSubReportStyleSheet( new SubReportStyleSheet
        ( resolverStyleSheet.getBooleanStyleProperty( BandStyleKeys.PAGEBREAK_BEFORE ),
          ( resolverStyleSheet.getBooleanStyleProperty( BandStyleKeys.PAGEBREAK_AFTER ) ) ) );

      final SimpleStyleSheet reportStyle = new SimpleStyleSheet( styleSheet );
      final BoxDefinition boxDefinition = getRenderNodeFactory().getBoxDefinition( reportStyle );
      box = new BlockRenderBox
        ( reportStyle, element.getObjectID(), boxDefinition, SubReportType.INSTANCE, element.getAttributes(), null );
    } else {
      box = getRenderNodeFactory().produceRenderBox
        ( element, new DesignerSubReportStyleSheet( resolverStyleSheet ), BandStyleKeys.LAYOUT_BLOCK, getStateKey() );
    }

    box.getStaticBoxLayoutProperties().setPlaceholderBox( StaticBoxLayoutProperties.PlaceholderType.SECTION );
    if ( element.getName() != null ) {
      box.setName( "Banded-SubReport-Section" + ": name=" + element.getName() );
    } else {
      box.setName( "Banded-SubReport-Section" );
    }

    pushBoxToContext( box, false );
  }
}
