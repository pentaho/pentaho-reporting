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

package org.pentaho.reporting.engine.classic.core.modules.output.fast.html;

import java.awt.Shape;
import java.io.IOException;
import java.util.HashMap;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ImageContainer;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinitionFactory;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.FastTextExtractor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlContentGenerator;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.HtmlTagHelper;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.HtmlTextExtractorHelper;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.HtmlTextExtractorState;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.StyleBuilder;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.RotatedTextDrawable;
import org.pentaho.reporting.engine.classic.core.util.ShapeDrawable;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;
import org.pentaho.reporting.libraries.xmlns.writer.CharacterEntityParser;
import org.pentaho.reporting.libraries.xmlns.writer.HtmlCharacterEntities;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

public class FastHtmlTextExtractor extends FastTextExtractor {
  private final CharacterEntityParser characterEntityParser;
  private final XmlWriter xmlWriter;
  private final StyleBuilder styleBuilder;
  private final HtmlTextExtractorHelper textExtractorHelper;
  private final BoxDefinitionFactory boxDefinitionFactory;
  private HashMap<InstanceID, FastHtmlImageBounds> recordedBounds;
  private boolean result;
  private HtmlTextExtractorState processStack;

  public FastHtmlTextExtractor( final OutputProcessorMetaData metaData, final XmlWriter xmlWriter,
      final HtmlContentGenerator contentGenerator, final HtmlTagHelper tagHelper ) {
    this.characterEntityParser = HtmlCharacterEntities.getEntityParser();
    this.xmlWriter = xmlWriter;
    this.styleBuilder = tagHelper.getStyleBuilder();
    this.textExtractorHelper = new HtmlTextExtractorHelper( tagHelper, xmlWriter, metaData, contentGenerator );
    this.boxDefinitionFactory = new BoxDefinitionFactory();
  }

  public boolean performOutput( final ReportElement content, final StyleBuilder.StyleCarrier[] cellStyle,
      final HashMap<InstanceID, FastHtmlImageBounds> recordedBounds, final ExpressionRuntime runtime )
    throws IOException, ContentProcessingException {
    this.recordedBounds = recordedBounds;
    styleBuilder.clear();
    clearText();
    setRawResult( null );
    result = false;
    processStack = new HtmlTextExtractorState( null, false, cellStyle );
    textExtractorHelper.setFirstElement( content.getObjectID(), processStack );

    try {
      setRuntime( runtime );
      processInitialBox( content );
    } finally {
      setRuntime( null );
      processStack = null;
    }
    return result;
  }

  /**
   * Prints a paragraph cell. This is a special entry point used by the processContent method and is never called from
   * elsewhere. This method assumes that the attributes of the paragraph have been processed as part of the table-cell
   * processing.
   *
   * @param box
   *          the paragraph box
   * @throws java.io.IOException
   *           if an IO error occured.
   */
  private void processInitialBox( final ReportElement box ) throws IOException, ContentProcessingException {
    if ( box.getComputedStyle().getBooleanStyleProperty( ElementStyleKeys.VISIBLE ) == false ) {
      return;
    }

    final StyleSheet styleSheet = box.getComputedStyle();
    final String target = (String) styleSheet.getStyleProperty( ElementStyleKeys.HREF_TARGET );
    if ( target != null ) {
      textExtractorHelper.handleLinkOnElement( styleSheet, target );
      processStack = new HtmlTextExtractorState( processStack, true );
    } else {
      processStack = new HtmlTextExtractorState( processStack, false );
    }

    if ( Boolean.TRUE.equals( box.getAttributes().getAttribute( AttributeNames.Html.NAMESPACE,
        AttributeNames.Html.SUPPRESS_CONTENT ) ) == false ) {
      if ( box instanceof Section ) {
        traverseSection( (Section) box );
      } else {
        inspectElement( box, true );
      }
    }

    if ( processStack.isWrittenTag() ) {
      xmlWriter.writeCloseTag();
    }
    processStack = processStack.getParent();
  }

  protected boolean inspectStartSection( final ReportElement box, final boolean inlineSection ) {
    BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition( box.getComputedStyle() );
    if ( inlineSection == false ) {
      return textExtractorHelper.startBox( box.getObjectID(), box.getAttributes(), box.getComputedStyle(),
          boxDefinition, true );
    } else {
      return textExtractorHelper.startInlineBox( box.getObjectID(), box.getAttributes(), box.getComputedStyle(),
          boxDefinition );
    }
  }

  protected void inspectEndSection( final ReportElement section, final boolean inlineSection ) {
    textExtractorHelper.finishBox( section.getObjectID(), section.getAttributes() );
  }

  @Override
  protected void handleValueContent( final ReportElement element, final Object value, final boolean inlineSection )
    throws ContentProcessingException {
    super.handleValueContent( element, value, inlineSection );

    if ( value instanceof Shape ) {
      handleShape( element, (Shape) value );
    } else if ( value instanceof ImageContainer || value instanceof DrawableWrapper || value instanceof RotatedTextDrawable ) {
      handleImage( element, value );
    } else {
      handleText( element, String.valueOf( value ) );
    }
  }

  private void handleText( final ReportElement element, final String text ) throws ContentProcessingException {
    try {
      xmlWriter.writeText( characterEntityParser.encodeEntities( text ) );
      if ( text.trim().length() > 0 ) {
        result = true;
      }
    } catch ( IOException e ) {
      throw new ContentProcessingException( e );
    }
  }

  protected void handleImage( final ReportElement element, Object rawObject ) throws ContentProcessingException {
    try {
      FastHtmlImageBounds cb = recordedBounds.get( element.getObjectID() );
      if ( cb == null ) {
        return;
      }

      ReportAttributeMap<Object> attributes = element.getAttributes();
      SimpleStyleSheet computedStyle = element.getComputedStyle();
      long width = cb.getWidth();
      long height = cb.getHeight();
      if ( textExtractorHelper.processRenderableReplacedContent( attributes, computedStyle, width, height, cb
          .getContentWidth(), cb.getContentHeight(), rawObject ) ) {
        result = true;
      }
    } catch ( ContentIOException e ) {
      throw new ContentProcessingException( e );
    } catch ( IOException e ) {
      throw new ContentProcessingException( e );
    }
  }

  protected void handleShape( final ReportElement element, Shape image ) throws ContentProcessingException {
    boolean keepAr = element.getComputedStyle().getBooleanStyleProperty( ElementStyleKeys.KEEP_ASPECT_RATIO );
    handleImage( element, new ShapeDrawable( image, keepAr ) );
  }

}
