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


package org.pentaho.reporting.engine.classic.core.layout;

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.text.ComplexTextFactory;
import org.pentaho.reporting.engine.classic.core.layout.text.DefaultRenderableTextFactory;
import org.pentaho.reporting.engine.classic.core.layout.text.RenderableTextFactory;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.libraries.fonts.encoding.CodePointBuffer;
import org.pentaho.reporting.libraries.fonts.encoding.manual.Utf16LE;

public class TextProducer {
  private CodePointBuffer buffer;
  private RenderableTextFactory textFactory;
  private TextCache textCache;
  private int[] bufferArray;
  private Utf16LE utf16LE;

  public TextProducer( final OutputProcessorMetaData metaData ) {
    if ( metaData.isFeatureSupported( OutputProcessorFeature.COMPLEX_TEXT ) ) {
      this.textFactory = new ComplexTextFactory();
    } else {
      this.textFactory = new DefaultRenderableTextFactory( metaData );
    }
    this.textCache = new TextCache( 500 );
    this.bufferArray = new int[500];
    this.utf16LE = Utf16LE.getInstance();
  }

  private void transformText( final String text ) {
    if ( buffer != null ) {
      buffer.setCursor( 0 );
    }

    buffer = utf16LE.decodeString( text, buffer );
    bufferArray = buffer.getBuffer( bufferArray );
  }

  public void startText() {
    textFactory.startText();
  }

  public RenderNode[] getRenderNodes( final ReportElement element, final StyleSheet elementStyle, final String text ) {
    final ReportAttributeMap attrs = element.getAttributes();
    final RenderNode[] renderNodes;
    final RenderNode[] finishNodes;

    final TextCache.Result result =
        textCache.get( elementStyle.getId(), elementStyle.getChangeTracker(), attrs.getChangeTracker(), text );
    if ( result != null ) {
      renderNodes = result.getText();
      finishNodes = result.getFinish();
    } else {
      transformText( text );

      renderNodes =
          textFactory.createText( bufferArray, 0, buffer.getLength(), elementStyle, element.getElementType(), element
              .getObjectID(), attrs );
      finishNodes = textFactory.finishText();

      textCache.store( elementStyle.getId(), elementStyle.getChangeTracker(), attrs.getChangeTracker(), text,
          elementStyle, attrs, renderNodes, finishNodes );
    }

    if ( renderNodes.length == 0 ) {
      return finishNodes;
    }
    if ( finishNodes.length == 0 ) {
      return renderNodes;
    }

    final RenderNode[] data = new RenderNode[finishNodes.length + renderNodes.length];
    System.arraycopy( renderNodes, 0, data, 0, renderNodes.length );
    System.arraycopy( finishNodes, 0, data, renderNodes.length, finishNodes.length );
    return data;
  }
}
