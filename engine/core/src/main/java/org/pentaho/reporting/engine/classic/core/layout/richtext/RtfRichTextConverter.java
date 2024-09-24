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

package org.pentaho.reporting.engine.classic.core.layout.richtext;

import java.awt.Color;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyleConstants;
import javax.swing.text.rtf.RTFEditorKit;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;

/**
 * This converter converts the content into a generic rich-text document. It can also be used to convert generic
 * swing-documents, as long as they adhere to the Swing StyledDocument system.
 *
 * @author Thomas Morgner.
 */
public class RtfRichTextConverter implements RichTextConverter {
  private RTFEditorKit editorKit;

  public RtfRichTextConverter() {
    editorKit = new RTFEditorKit();
  }

  public boolean isRecognizedType( final String mimeType ) {
    if ( mimeType.equals( "application/rtf" ) ) {
      return true;
    }
    if ( mimeType.equals( "text/rtf" ) ) {
      return true;
    }
    return false;
  }

  public Object convert( final ReportElement source, final Object value ) {
    try {
      final Document doc = RichTextConverterUtilities.parseDocument( editorKit, value );
      if ( doc == null ) {
        return value;
      }

      final Element element = process( doc.getDefaultRootElement() );
      final Band band = RichTextConverterUtilities.convertToBand( StyleKey.getDefinedStyleKeysList(), source, element );
      band.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "inline" );
      return band;
    } catch ( Exception e ) {
      return value;
    }
  }

  private void configureStyle( final AttributeSet attributes, final Element element ) {
    final Object alignment = attributes.getAttribute( StyleConstants.Alignment );
    if ( alignment instanceof Integer ) {
      final int alignmentValue = (Integer) alignment;
      if ( StyleConstants.ALIGN_CENTER == alignmentValue ) {
        element.getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.CENTER );
      } else if ( StyleConstants.ALIGN_RIGHT == alignmentValue ) {
        element.getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.RIGHT );
      } else if ( StyleConstants.ALIGN_JUSTIFIED == alignmentValue ) {
        element.getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.JUSTIFY );
      } else {
        element.getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.LEFT );
      }
    }
    final Object background = attributes.getAttribute( StyleConstants.Background );
    if ( background instanceof Color ) {
      element.getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, background );
    }

    // Not handled: attributes.getAttribute(StyleConstants.BidiLevel);
    // Not handled: attributes.getAttribute(StyleConstants.ComponentAttribute);
    // Not handled: attributes.getAttribute(StyleConstants.ComposedTextAttribute);
    final Object bold = attributes.getAttribute( StyleConstants.Bold );
    if ( bold instanceof Boolean ) {
      element.getStyle().setStyleProperty( TextStyleKeys.BOLD, bold );
    }

    final Object firstLineIndent = attributes.getAttribute( StyleConstants.FirstLineIndent );
    if ( firstLineIndent instanceof Float ) {
      element.getStyle().setStyleProperty( TextStyleKeys.FIRST_LINE_INDENT, firstLineIndent );
    }

    final Object family = attributes.getAttribute( StyleConstants.FontFamily );
    if ( family instanceof String ) {
      element.getStyle().setStyleProperty( TextStyleKeys.FONT, family );
    }

    final Object fontSize = attributes.getAttribute( StyleConstants.FontSize );
    if ( fontSize instanceof Integer ) {
      element.getStyle().setStyleProperty( TextStyleKeys.FONTSIZE, fontSize );
    }

    final Object foreground = attributes.getAttribute( StyleConstants.Foreground );
    if ( foreground instanceof Color ) {
      element.getStyle().setStyleProperty( ElementStyleKeys.PAINT, foreground );
    }

    // final Object iconAttribute = attributes.getAttribute( StyleConstants.IconAttribute );
    // if ( iconAttribute instanceof Icon ) {
    // not handled yet
    // }

    final Object italic = attributes.getAttribute( StyleConstants.Italic );
    if ( italic instanceof Boolean ) {
      element.getStyle().setStyleProperty( TextStyleKeys.ITALIC, italic );
    }

    final Object leftIndent = attributes.getAttribute( StyleConstants.LeftIndent );
    if ( leftIndent instanceof Float ) {
      element.getStyle().setStyleProperty( TextStyleKeys.TEXT_INDENT, leftIndent );
    }

    final Object lineSpacing = attributes.getAttribute( StyleConstants.LineSpacing );
    if ( lineSpacing instanceof Float ) {
      element.getStyle().setStyleProperty( TextStyleKeys.LINEHEIGHT, lineSpacing );
    }

    // final Object modelAttribute = attributes.getAttribute( StyleConstants.ModelAttribute );
    // if ( modelAttribute instanceof Float ) {
    // not handled yet
    // }

    // final Object nameAttribute = attributes.getAttribute( StyleConstants.NameAttribute );
    // if ( nameAttribute instanceof Float ) {
    // not handled yet
    // }

    // final Object orientation = attributes.getAttribute( StyleConstants.Orientation );
    // if ( orientation instanceof Float ) {
    // not used, also seems to be unused by Swing itself
    // }

    // final Object resolveAttribute = attributes.getAttribute( StyleConstants.ResolveAttribute );
    // if ( resolveAttribute instanceof Float ) {
    // not handled yet, maybe never needed to be handled at all.
    // }

    // final Object rightIndent = attributes.getAttribute( StyleConstants.RightIndent );
    // if ( rightIndent instanceof Float ) {
    // not handled yet
    // }

    final Object spaceAbove = attributes.getAttribute( StyleConstants.SpaceAbove );
    if ( spaceAbove instanceof Float ) {
      element.getStyle().setStyleProperty( ElementStyleKeys.PADDING_TOP, spaceAbove );
    }

    final Object spaceBelow = attributes.getAttribute( StyleConstants.SpaceBelow );
    if ( spaceBelow instanceof Float ) {
      element.getStyle().setStyleProperty( ElementStyleKeys.PADDING_BOTTOM, spaceBelow );
    }

    final Object strikeThrough = attributes.getAttribute( StyleConstants.StrikeThrough );
    if ( strikeThrough instanceof Boolean ) {
      element.getStyle().setStyleProperty( TextStyleKeys.STRIKETHROUGH, strikeThrough );
    }

    // final Object subscript = attributes.getAttribute( StyleConstants.Subscript );
    // if ( subscript instanceof Boolean ) {
    // not handled yet
    // }

    // final Object superScript = attributes.getAttribute( StyleConstants.Superscript );
    // if ( superScript instanceof Boolean ) {
    // not handled yet
    // }

    // final Object tabSet = attributes.getAttribute( StyleConstants.TabSet );
    // if ( tabSet instanceof Float ) {
    // not handled yet
    // }

    final Object underline = attributes.getAttribute( StyleConstants.Underline );
    if ( underline instanceof Boolean ) {
      element.getStyle().setStyleProperty( TextStyleKeys.UNDERLINED, underline );
    }

    element.getStyle().setStyleProperty( ElementStyleKeys.DYNAMIC_HEIGHT, Boolean.TRUE );
  }

  private Element process( final javax.swing.text.Element textElement ) throws BadLocationException {
    if ( textElement.isLeaf() ) {
      final int endOffset = textElement.getEndOffset();
      final int startOffset = textElement.getStartOffset();
      final String text = textElement.getDocument().getText( startOffset, endOffset - startOffset );
      final Element result = new Element();
      result.setElementType( LabelType.INSTANCE );
      result.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, text );
      configureStyle( textElement.getAttributes(), result );
      return result;
    }

    final Band band = new Band();
    configureStyle( textElement.getAttributes(), band );
    configureBand( textElement, band );
    final int size = textElement.getElementCount();
    for ( int i = 0; i < size; i++ ) {
      final Element element = process( textElement.getElement( i ) );
      band.addElement( element );
    }
    return band;
  }

  private void configureBand( final javax.swing.text.Element textElement, final Band band ) {
    if ( "paragraph".equals( textElement.getName() ) || "section".equals( textElement.getName() ) ) {
      band.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "block" );
      band.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( -100 ) );
    } else {
      band.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "inline" );
    }
  }
}
