/*
 *
 *  * This program is free software; you can redistribute it and/or modify it under the
 *  * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  * Foundation.
 *  *
 *  * You should have received a copy of the GNU Lesser General Public License along with this
 *  * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  * or from the Free Software Foundation, Inc.,
 *  * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *  *
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  * See the GNU Lesser General Public License for more details.
 *  *
 *  * Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 *
 */

package org.pentaho.reporting.engine.classic.core.layout.process.text;

import java.awt.Image;
import java.awt.font.GraphicAttribute;
import java.awt.font.ImageGraphicAttribute;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableComplexText;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.process.util.ReplacedContentUtil;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextDirection;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class RichTextSpecProducer {
  private static class AttributedStringChunk {
    private String text;
    private Map<Attribute, Object> attributes;
    private ReportAttributeMap<Object> originalAttributes;
    private StyleSheet styleSheet;
    private InstanceID instanceID;
    private RenderNode node;

    private AttributedStringChunk( final String text, final Map<Attribute, Object> attributes,
        final ReportAttributeMap<Object> originalAttributes, final StyleSheet styleSheet, final InstanceID instanceID,
        final RenderNode node ) {
      ArgumentNullException.validate( "text", text );
      ArgumentNullException.validate( "attributes", attributes );
      ArgumentNullException.validate( "node", node );
      ArgumentNullException.validate( "originalAttributes", originalAttributes );
      ArgumentNullException.validate( "styleSheet", styleSheet );
      ArgumentNullException.validate( "instanceID", instanceID );

      if ( text.length() == 0 ) {
        this.text = "\u0200";
      } else {
        this.text = text;
      }
      this.instanceID = instanceID;
      this.node = node;
      this.attributes = attributes;
      this.originalAttributes = originalAttributes;
      this.styleSheet = styleSheet;
    }

    public String getText() {
      return text;
    }

    public Map<Attribute, Object> getAttributes() {
      return attributes;
    }

    public ReportAttributeMap<Object> getOriginalAttributes() {
      return originalAttributes;
    }

    public StyleSheet getStyleSheet() {
      return styleSheet;
    }

    public InstanceID getInstanceID() {
      return instanceID;
    }

    public RenderNode getNode() {
      return node;
    }
  }

  public static RichTextSpec compute( final RenderBox lineBoxContainer, final OutputProcessorMetaData metaData,
      final ResourceManager resourceManager ) {
    return new RichTextSpecProducer( metaData, resourceManager ).computeText( lineBoxContainer );
  }

  private RichTextImageProducer imageProducer;
  private OutputProcessorMetaData metaData;

  public RichTextSpecProducer( final OutputProcessorMetaData metaData, final ResourceManager resourceManager ) {
    ArgumentNullException.validate( "metaData", metaData );
    ArgumentNullException.validate( "resourceManager", resourceManager );

    this.metaData = metaData;
    imageProducer = new RichTextImageProducer( metaData, resourceManager );
  }

  private RichTextSpec computeText( final RenderBox lineBoxContainer ) {
    List<AttributedStringChunk> attr = new ArrayList<AttributedStringChunk>();
    computeText( lineBoxContainer, attr );
    if ( attr.isEmpty() ) {
      attr.add( new AttributedStringChunk( "", computeStyle( lineBoxContainer.getStyleSheet() ), lineBoxContainer
          .getAttributes(), lineBoxContainer.getStyleSheet(), new InstanceID(), lineBoxContainer ) );
    }

    attr = processWhitespaceRules( lineBoxContainer, attr );

    StringBuilder text = new StringBuilder();
    for ( final AttributedStringChunk chunk : attr ) {
      text.append( chunk.getText() );
    }

    TextDirection direction =
        (TextDirection) lineBoxContainer.getStyleSheet().getStyleProperty( TextStyleKeys.DIRECTION, TextDirection.LTR );
    return new RichTextSpec( text.toString(), direction, convertNodes( attr ) );
  }

  public RichTextSpec computeText( final RenderableComplexText textNode, final String textChunk ) {
    List<AttributedStringChunk> attr = new ArrayList<AttributedStringChunk>();
    attr.add( new AttributedStringChunk( textChunk, computeStyle( textNode.getStyleSheet() ), textNode.getAttributes(),
        textNode.getStyleSheet(), textNode.getInstanceId(), textNode ) );

    StringBuilder text = new StringBuilder();
    for ( final AttributedStringChunk chunk : attr ) {
      text.append( chunk.getText() );
    }

    TextDirection direction =
        (TextDirection) textNode.getStyleSheet().getStyleProperty( TextStyleKeys.DIRECTION, TextDirection.LTR );
    return new RichTextSpec( text.toString(), direction, convertNodes( attr ) );
  }

  private List<RichTextSpec.StyledChunk> convertNodes( final List<AttributedStringChunk> chunks ) {
    ArrayList<RichTextSpec.StyledChunk> result = new ArrayList<RichTextSpec.StyledChunk>( chunks.size() );
    int startPosition = 0;
    for ( final AttributedStringChunk chunk : chunks ) {
      int length = chunk.getText().length();
      int endIndex = startPosition + length;
      result.add( new RichTextSpec.StyledChunk( startPosition, endIndex, chunk.getNode(), chunk.getAttributes(), chunk
          .getOriginalAttributes(), chunk.getStyleSheet(), chunk.getInstanceID(), chunk.getText() ) );
      startPosition = endIndex;
    }
    return result;
  }

  private List<AttributedStringChunk> processWhitespaceRules( final RenderBox lineBoxContainer,
      final List<AttributedStringChunk> attrs ) {
    // todo
    // Object ws = lineBoxContainer.getStyleSheet().getStyleProperty( TextStyleKeys.WHITE_SPACE_COLLAPSE );
    // if ( WhitespaceCollapse.PRESERVE_BREAKS.equals( ws ) ) {
    // linebreaks disabled
    // } else if ( WhitespaceCollapse.COLLAPSE.equals( ws ) ) {
    // normal linebreaks, but duplicate spaces removed
    // } else if ( WhitespaceCollapse.DISCARD.equals( ws ) ) {
    // all whitespaces removed
    // }
    return attrs;
  }

  private void computeText( final RenderBox box, final List<AttributedStringChunk> chunks ) {
    RenderNode node = box.getFirstChild();
    while ( node != null ) {
      if ( node.getNodeType() == LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT ) {
        final RenderableComplexText complexNode = (RenderableComplexText) node;
        chunks.add( new AttributedStringChunk( complexNode.getRawText(), computeStyle( node.getStyleSheet() ), node
            .getAttributes(), node.getStyleSheet(), node.getInstanceId(), node ) );
      } else if ( node.getNodeType() == LayoutNodeTypes.TYPE_BOX_CONTENT ) {
        final RenderableReplacedContentBox contentBox = (RenderableReplacedContentBox) node;
        final long width = ReplacedContentUtil.computeWidth( contentBox );
        final long height = ReplacedContentUtil.computeHeight( contentBox, 0, width );
        contentBox.setCachedWidth( width );
        contentBox.setCachedHeight( height );
        contentBox.setWidth( width );
        contentBox.setHeight( height );

        chunks.add( new AttributedStringChunk( "@", computeImageStyle( node.getStyleSheet(), contentBox ), node
            .getAttributes(), node.getStyleSheet(), node.getInstanceId(), node ) );
      } else if ( node instanceof RenderBox ) {
        computeText( (RenderBox) node, chunks );
      }
      node = node.getNext();
    }
  }

  private Map<Attribute, Object> computeImageStyle( final StyleSheet layoutContext,
      final RenderableReplacedContentBox content ) {
    final Image image = imageProducer.createImagePlaceholder( content );
    ImageGraphicAttribute iga = new ImageGraphicAttribute( image, GraphicAttribute.BOTTOM_ALIGNMENT );

    Map<Attribute, Object> attrs = computeStyle( layoutContext );
    attrs.put( TextAttribute.CHAR_REPLACEMENT, iga );
    return attrs;
  }

  private Map<Attribute, Object> computeStyle( final StyleSheet layoutContext ) {
    Map<Attribute, Object> result = new HashMap<Attribute, Object>();
    // Determine font style
    if ( layoutContext.getBooleanStyleProperty( TextStyleKeys.ITALIC ) ) {
      result.put( TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE );
    } else {
      result.put( TextAttribute.POSTURE, TextAttribute.POSTURE_REGULAR );
    }
    if ( layoutContext.getBooleanStyleProperty( TextStyleKeys.BOLD ) ) {
      result.put( TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD );
    } else {
      result.put( TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR );
    }

    final String fontNameRaw = (String) layoutContext.getStyleProperty( TextStyleKeys.FONT );
    final String fontName = metaData.getNormalizedFontFamilyName( fontNameRaw );
    result.put( TextAttribute.FAMILY, fontName );
    result.put( TextAttribute.SIZE, layoutContext.getIntStyleProperty( TextStyleKeys.FONTSIZE, 12 ) );
    if ( layoutContext.getBooleanStyleProperty( TextStyleKeys.UNDERLINED ) ) {
      result.put( TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON );
    }
    if ( layoutContext.getBooleanStyleProperty( TextStyleKeys.STRIKETHROUGH ) ) {
      result.put( TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON );
    }

    // character spacing
    result.put( TextAttribute.TRACKING,
        layoutContext.getDoubleStyleProperty( TextStyleKeys.X_MIN_LETTER_SPACING, 0 ) / 10 );

    return result;
  }
}
