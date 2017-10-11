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

package org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.helper;

import java.awt.Color;
import java.io.IOException;

import org.pentaho.reporting.engine.classic.core.ImageContainer;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableComplexText;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContent;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.layout.output.RenderUtility;
import org.pentaho.reporting.engine.classic.core.layout.process.text.RichTextSpec;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.DefaultTextExtractor;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.util.FastStack;
import org.pentaho.reporting.libraries.fonts.itext.BaseFontFontMetrics;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.TextElementArray;
import com.lowagie.text.pdf.BaseFont;

/**
 * Todo: On Block-Level elements, apply the block-level styles like text-alignment and vertical-alignment.
 *
 * @author Thomas Morgner
 */
public class RTFTextExtractor extends DefaultTextExtractor {
  private static class StyleContext {
    private TextElementArray target;
    private RTFOutputProcessorMetaData metaData;
    private String fontName;
    private double fontSize;
    private boolean bold;
    private boolean italic;
    private boolean underline;
    private boolean strikethrough;
    private Color textColor;
    private Color backgroundColor;

    protected StyleContext( final TextElementArray target, final StyleSheet styleSheet,
        final RTFOutputProcessorMetaData metaData ) {
      this.target = target;
      this.metaData = metaData;
      this.fontName = (String) styleSheet.getStyleProperty( TextStyleKeys.FONT );
      this.fontSize = styleSheet.getDoubleStyleProperty( TextStyleKeys.FONTSIZE, 0 );
      this.bold = styleSheet.getBooleanStyleProperty( TextStyleKeys.BOLD );
      this.italic = styleSheet.getBooleanStyleProperty( TextStyleKeys.ITALIC );
      this.underline = styleSheet.getBooleanStyleProperty( TextStyleKeys.UNDERLINED );
      this.strikethrough = styleSheet.getBooleanStyleProperty( TextStyleKeys.STRIKETHROUGH );
      this.textColor = (Color) styleSheet.getStyleProperty( ElementStyleKeys.PAINT );
      this.backgroundColor = (Color) styleSheet.getStyleProperty( ElementStyleKeys.BACKGROUND_COLOR );
    }

    public TextElementArray getTarget() {
      return target;
    }

    public String getFontName() {
      return fontName;
    }

    public double getFontSize() {
      return fontSize;
    }

    public boolean isBold() {
      return bold;
    }

    public boolean isItalic() {
      return italic;
    }

    public boolean isUnderline() {
      return underline;
    }

    public boolean isStrikethrough() {
      return strikethrough;
    }

    public Color getTextColor() {
      return textColor;
    }

    public Color getBackgroundColor() {
      return backgroundColor;
    }

    public void add( final Element element ) {
      target.add( element );
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final StyleContext that = (StyleContext) o;

      if ( bold != that.bold ) {
        return false;
      }
      if ( that.fontSize != fontSize ) {
        return false;
      }
      if ( italic != that.italic ) {
        return false;
      }
      if ( strikethrough != that.strikethrough ) {
        return false;
      }
      if ( underline != that.underline ) {
        return false;
      }
      if ( backgroundColor != null ? !backgroundColor.equals( that.backgroundColor ) : that.backgroundColor != null ) {
        return false;
      }
      if ( fontName != null ? !fontName.equals( that.fontName ) : that.fontName != null ) {
        return false;
      }
      if ( textColor != null ? !textColor.equals( that.textColor ) : that.textColor != null ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result = ( fontName != null ? fontName.hashCode() : 0 );
      final long temp = fontSize != +0.0d ? Double.doubleToLongBits( fontSize ) : 0L;
      result = 29 * result + (int) ( temp ^ ( temp >>> 32 ) );
      result = 29 * result + ( bold ? 1 : 0 );
      result = 29 * result + ( italic ? 1 : 0 );
      result = 29 * result + ( underline ? 1 : 0 );
      result = 29 * result + ( strikethrough ? 1 : 0 );
      result = 29 * result + ( textColor != null ? textColor.hashCode() : 0 );
      result = 29 * result + ( backgroundColor != null ? backgroundColor.hashCode() : 0 );
      return result;
    }

    public void add( final String text ) {
      int style = Font.NORMAL;
      if ( bold ) {
        style |= Font.BOLD;
      }
      if ( italic ) {
        style |= Font.ITALIC;
      }
      if ( strikethrough ) {
        style |= Font.STRIKETHRU;
      }
      if ( underline ) {
        style |= Font.UNDERLINE;
      }

      final BaseFontFontMetrics fontMetrics =
          metaData.getBaseFontFontMetrics( fontName, fontSize, bold, italic, "utf-8", false, false );
      final BaseFont baseFont = fontMetrics.getBaseFont();
      final Font font = new Font( baseFont, (float) fontSize, style, textColor );
      final Chunk c = new Chunk( text, font );
      if ( backgroundColor != null ) {
        c.setBackground( backgroundColor );
      }
      target.add( c );
    }
  }

  private RTFImageCache imageCache;
  private FastStack<StyleContext> context;
  private RTFOutputProcessorMetaData metaData;
  private boolean handleImages;

  public RTFTextExtractor( final RTFOutputProcessorMetaData metaData ) {
    super( metaData );
    this.metaData = metaData;
    this.handleImages = metaData.isFeatureSupported( RTFOutputProcessorMetaData.IMAGES_ENABLED );
    context = new FastStack<StyleContext>( 50 );
  }

  private StyleContext getCurrentContext() {
    return context.peek();
  }

  public void compute( final RenderBox box, final TextElementArray cell, final RTFImageCache imageCache ) {
    this.context.clear();
    this.context.push( new StyleContext( cell, box.getStyleSheet(), metaData ) );
    this.imageCache = imageCache;
    super.compute( box );
  }

  protected boolean startInlineBox( final InlineRenderBox box ) {
    if ( box.getStaticBoxLayoutProperties().isVisible() == false ) {
      return false;
    }

    // Compare the text style ..
    final StyleContext currentContext = getCurrentContext();
    final StyleContext boxContext = new StyleContext( currentContext.getTarget(), box.getStyleSheet(), metaData );
    if ( currentContext.equals( boxContext ) == false ) {
      if ( getTextLength() > 0 ) {
        final String text = getText();
        currentContext.add( text );
        clearText();
      }
      this.context.pop();
      this.context.push( boxContext );
    }
    return true;
  }

  protected void finishInlineBox( final InlineRenderBox box ) {
    final StyleContext currentContext = getCurrentContext();
    if ( getTextLength() > 0 ) {
      final String text = getText();
      currentContext.add( text );
      clearText();
    }
  }

  protected void processOtherNode( final RenderNode node ) {
    final StrictBounds paragraphBounds = getParagraphBounds();
    if ( isTextLineOverflow() && node.isNodeVisible( paragraphBounds, isOverflowX(), isOverflowY() ) == false ) {
      return;
    }

    super.processOtherNode( node );
    if ( node.getNodeType() == LayoutNodeTypes.TYPE_NODE_TEXT ) {
      if ( node.isVirtualNode() ) {
        return;
      }

      if ( ( node.getX() + node.getWidth() ) > ( paragraphBounds.getX() + paragraphBounds.getWidth() ) ) {
        // This node will only be partially visible. The end-of-line marker will not apply.
        return;
      }
      final RenderableText text = (RenderableText) node;
      if ( text.isForceLinebreak() ) {
        final StyleContext currentContext = getCurrentContext();
        if ( getTextLength() > 0 ) {
          currentContext.add( getText() );
          clearText();
        }
        context.pop();
        final StyleContext cellContext = getCurrentContext();
        cellContext.add( currentContext.getTarget() );

        context.push( new StyleContext( new Paragraph(), text.getStyleSheet(), metaData ) );
      }
    } else if ( node.getNodeType() == LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT ) {
      // todo: check if special text processing is required for RenderableComplexText nodes
      // return;
      if ( node.isVirtualNode() ) {
        return;
      }

      if ( ( node.getX() + node.getWidth() ) > ( paragraphBounds.getX() + paragraphBounds.getWidth() ) ) {
        // This node will only be partially visible. The end-of-line marker will not apply.
        return;
      }
      final RenderableComplexText text = (RenderableComplexText) node;
      if ( text.isForceLinebreak() ) {
        final StyleContext currentContext = getCurrentContext();
        if ( getTextLength() > 0 ) {
          currentContext.add( getText() );
          clearText();
        }
        context.pop();
        final StyleContext cellContext = getCurrentContext();
        cellContext.add( currentContext.getTarget() );

        context.push( new StyleContext( new Paragraph(), text.getStyleSheet(), metaData ) );
      }
    }
  }

  protected void processRenderableContent( final RenderableReplacedContentBox node ) {
    float targetWidth = (float) StrictGeomUtility.toExternalValue( node.getWidth() );
    float targetHeight = (float) StrictGeomUtility.toExternalValue( node.getHeight() );

    try {
      final RenderableReplacedContent rpc = node.getContent();
      final Object rawObject = rpc.getRawObject();
      if ( rawObject instanceof ImageContainer ) {
        final Image image = imageCache.getImage( (ImageContainer) rawObject );
        if ( image == null ) {
          return;
        }
        final StyleContext currentContext = getCurrentContext();
        if ( getTextLength() > 0 ) {
          currentContext.add( getText() );
          clearText();
        }

        image.scaleToFit( targetWidth, targetHeight );
        currentContext.add( image );
      } else if ( rawObject instanceof DrawableWrapper ) {
        final StrictBounds rect = new StrictBounds( node.getX(), node.getY(), node.getWidth(), node.getHeight() );
        final ImageContainer ic =
            RenderUtility.createImageFromDrawable( (DrawableWrapper) rawObject, rect, node, metaData );
        if ( ic == null ) {
          return;
        }
        final Image image = imageCache.getImage( ic );
        if ( image == null ) {
          return;
        }

        final StyleContext currentContext = getCurrentContext();
        if ( getTextLength() > 0 ) {
          currentContext.add( getText() );
          clearText();
        }
        image.scaleToFit( targetWidth, targetHeight );
        currentContext.add( image );
      }
    } catch ( DocumentException ioe ) {
      throw new InvalidReportStateException( "Failed to extract text", ioe );
    } catch ( IOException e ) {
      // double ignore ..
      throw new InvalidReportStateException( "Failed to extract text", e );
    }

  }

  protected void processParagraphChilds( final ParagraphRenderBox box ) {
    context.push( new StyleContext( new Paragraph(), box.getStyleSheet(), metaData ) );
    clearText();

    super.processParagraphChilds( box );

    final StyleContext currentContext = getCurrentContext();
    if ( getTextLength() > 0 ) {
      currentContext.add( getText() );
      clearText();
    }
    context.pop();
    getCurrentContext().add( currentContext.getTarget() );
  }

  protected void drawComplexText( final RenderableComplexText renderableComplexText ) {
    if ( renderableComplexText.getRawText().length() == 0 ) {
      // This text is empty.
      return;
    }
    if ( renderableComplexText.isNodeVisible( getParagraphBounds(), isOverflowX(), isOverflowY() ) == false ) {
      return;
    }

    // check if we have to process inline text elements
    if ( renderableComplexText.getRichText().getStyleChunks().size() > 1 ) {
      // iterate through all inline elements
      for ( final RichTextSpec.StyledChunk styledChunk : renderableComplexText.getRichText().getStyleChunks() ) {
        // Add style for current styled chunk
        final StyleContext boxContext =
            new StyleContext( getCurrentContext().getTarget(), styledChunk.getStyleSheet(), metaData );
        if ( styledChunk.getText().length() > 0 ) {
          final String text = styledChunk.getText();
          boxContext.add( text );
          clearText();
        }
        context.pop();
        context.push( boxContext );
      }
    } else {
      super.drawComplexText( renderableComplexText );
    }
  }

}
