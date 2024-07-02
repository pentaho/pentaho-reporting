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
 * Copyright (c) 2002-2018 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.richtext;

import java.awt.Color;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.types.ContentType;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.layout.richtext.html.RichTextHtmlStyleBuilderFactory;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextWrap;
import org.pentaho.reporting.engine.classic.core.style.VerticalTextAlign;
import org.pentaho.reporting.engine.classic.core.style.WhitespaceCollapse;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;

/**
 * This handles HTML 3.2 with some CSS support. It uses the Swing HTML parser to process the document.
 *
 * @author Thomas Morgner.
 */
public class HtmlRichTextConverter implements RichTextConverter {
  private HTMLEditorKit editorKit;
  private static final Set BLOCK_ELEMENTS;

  static {
    final HashSet<HTML.Tag> blockElements = new HashSet<HTML.Tag>();
    blockElements.add( HTML.Tag.IMPLIED );
    blockElements.add( HTML.Tag.APPLET );
    blockElements.add( HTML.Tag.BODY );
    blockElements.add( HTML.Tag.BLOCKQUOTE );
    blockElements.add( HTML.Tag.DIV );
    blockElements.add( HTML.Tag.FORM );
    blockElements.add( HTML.Tag.FRAME );
    blockElements.add( HTML.Tag.FRAMESET );
    blockElements.add( HTML.Tag.H1 );
    blockElements.add( HTML.Tag.H2 );
    blockElements.add( HTML.Tag.H3 );
    blockElements.add( HTML.Tag.H4 );
    blockElements.add( HTML.Tag.H5 );
    blockElements.add( HTML.Tag.H6 );
    blockElements.add( HTML.Tag.HR );
    blockElements.add( HTML.Tag.HTML );
    blockElements.add( HTML.Tag.LI );
    blockElements.add( HTML.Tag.NOFRAMES );
    blockElements.add( HTML.Tag.OBJECT );
    blockElements.add( HTML.Tag.OL );
    blockElements.add( HTML.Tag.P );
    blockElements.add( HTML.Tag.PRE );
    blockElements.add( HTML.Tag.TABLE );
    blockElements.add( HTML.Tag.TR );
    blockElements.add( HTML.Tag.UL );

    BLOCK_ELEMENTS = Collections.unmodifiableSet( blockElements );
  }

  public HtmlRichTextConverter() {
    editorKit = new HTMLEditorKit();
  }

  public boolean isRecognizedType( final String mimeType ) {
    if ( "text/html".equals( mimeType ) ) {
      return true;
    }
    return false;
  }

  public Object convert( final ReportElement source, final Object value ) {
    try {
      final Document doc = RichTextConverterUtilities.parseDocument( editorKit, value );
      if ( !( doc instanceof HTMLDocument ) ) {
        return value;
      }

      HTMLDocument docHTML = (HTMLDocument) doc;

      SimpleStyleSheet simpleStyle = source.getComputedStyle();
      RichTextHtmlStyleBuilderFactory richTextBuilder = new RichTextHtmlStyleBuilderFactory();
      String codeCss = richTextBuilder.produceTextStyle( null, simpleStyle ).toString();

      docHTML.getStyleSheet().addRule( "body { " + codeCss + ";}" );

      final Element element = process( doc.getDefaultRootElement(), null );
      return RichTextConverterUtilities.convertToBand( StyleKey.getDefinedStyleKeysList(), source, element );
    } catch ( Exception e ) {
      return value;
    }
  }

  private static AttributeSet computeStyle( final javax.swing.text.Element elem, final StyleSheet styles ) {
    final AttributeSet a = elem.getAttributes();
    final AttributeSet htmlAttr = styles.translateHTMLToCSS( a );
    final ArrayList<AttributeSet> muxList = new ArrayList<AttributeSet>();

    if ( htmlAttr.getAttributeCount() != 0 ) {
      muxList.add( htmlAttr );
    }

    if ( elem.isLeaf() ) {
      // The swing-parser has a very weird way of storing attributes for the HTML elements. The
      // tag-name is used as key for the attribute set, so you have to know the element type before
      // you can do anything sensible with it. Or as we do here, you have to search for the HTML.Tag
      // object. Arghh.
      final Enumeration keys = a.getAttributeNames();
      while ( keys.hasMoreElements() ) {
        final Object key = keys.nextElement();
        if ( !( key instanceof HTML.Tag ) ) {
          continue;
        }

        if ( key == HTML.Tag.A ) {
          final Object o = a.getAttribute( key );
          if ( o instanceof AttributeSet ) {
            final AttributeSet attr = (AttributeSet) o;
            if ( attr.getAttribute( HTML.Attribute.HREF ) == null ) {
              continue;
            } else {
              SimpleAttributeSet hrefAttributeSet = new SimpleAttributeSet();
              hrefAttributeSet.addAttribute( HTML.Attribute.HREF, attr.getAttribute( HTML.Attribute.HREF ) );
              muxList.add( hrefAttributeSet );
            }
          }
        }

        final AttributeSet cssRule = styles.getRule( (HTML.Tag) key, elem );
        if ( cssRule != null ) {
          muxList.add( cssRule );
        }
      }
    } else {
      final HTML.Tag t = (HTML.Tag) a.getAttribute( StyleConstants.NameAttribute );
      final AttributeSet cssRule = styles.getRule( t, elem );
      if ( cssRule != null ) {
        muxList.add( cssRule );
      }
    }

    final MutableAttributeSet retval = new SimpleAttributeSet();
    for ( int i = muxList.size() - 1; i >= 0; i-- ) {
      final AttributeSet o = muxList.get( i );
      retval.addAttributes( o );
    }
    return retval;
  }

  private Object convertURL( final String srcAttr ) {
    try {
      return new URL( srcAttr );
    } catch ( MalformedURLException e ) {
      // ignore ..
      return srcAttr;
    }
  }

  private Element process( final javax.swing.text.Element textElement, final String liNum ) throws BadLocationException {
    if ( isInvisible( textElement ) ) {
      return null;
    }

    if ( textElement.isLeaf() ) {
      final AttributeSet attributes = textElement.getAttributes();
      if ( HTML.Tag.IMG.equals( attributes.getAttribute( StyleConstants.NameAttribute ) ) ) {
        final Element result = new Element();
        result.setName( textElement.getName() );
        result.setElementType( new ContentType() );
        final String src = (String) attributes.getAttribute( HTML.Attribute.SRC );
        final String alt = (String) attributes.getAttribute( HTML.Attribute.TITLE );
        result.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, convertURL( src ) );
        result.setAttribute( AttributeNames.Html.NAMESPACE, AttributeNames.Html.TITLE, alt );
        result.setAttribute( AttributeNames.Html.NAMESPACE, AttributeNames.Swing.TOOLTIP, alt );
        if ( attributes.isDefined( HTML.Attribute.WIDTH ) && attributes.isDefined( HTML.Attribute.HEIGHT ) ) {
          result.getStyle().setStyleProperty( ElementStyleKeys.SCALE, Boolean.TRUE );
          result.getStyle().setStyleProperty( ElementStyleKeys.KEEP_ASPECT_RATIO, Boolean.FALSE );
          result.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH,
              parseLength( String.valueOf( attributes.getAttribute( HTML.Attribute.WIDTH ) ) ) );
          result.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT,
              parseLength( String.valueOf( attributes.getAttribute( HTML.Attribute.HEIGHT ) ) ) );
        } else if ( attributes.isDefined( HTML.Attribute.WIDTH ) ) {
          result.getStyle().setStyleProperty( ElementStyleKeys.SCALE, Boolean.TRUE );
          result.getStyle().setStyleProperty( ElementStyleKeys.KEEP_ASPECT_RATIO, Boolean.TRUE );
          result.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH,
              parseLength( String.valueOf( attributes.getAttribute( HTML.Attribute.WIDTH ) ) ) );
          result.getStyle().setStyleProperty( ElementStyleKeys.DYNAMIC_HEIGHT, Boolean.TRUE );
        } else if ( attributes.isDefined( HTML.Attribute.HEIGHT ) ) {
          result.getStyle().setStyleProperty( ElementStyleKeys.SCALE, Boolean.TRUE );
          result.getStyle().setStyleProperty( ElementStyleKeys.KEEP_ASPECT_RATIO, Boolean.TRUE );
          result.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT,
              parseLength( String.valueOf( attributes.getAttribute( HTML.Attribute.HEIGHT ) ) ) );
          result.getStyle().setStyleProperty( ElementStyleKeys.DYNAMIC_HEIGHT, Boolean.TRUE );
        } else {
          result.getStyle().setStyleProperty( ElementStyleKeys.SCALE, Boolean.FALSE );
          result.getStyle().setStyleProperty( ElementStyleKeys.KEEP_ASPECT_RATIO, Boolean.TRUE );
          result.getStyle().setStyleProperty( ElementStyleKeys.DYNAMIC_HEIGHT, Boolean.TRUE );
        }
        configureStyle( textElement, result );
        return result;
      }

      final javax.swing.text.Element parent = textElement.getParentElement();
      final int endOffset = textElement.getEndOffset();
      final int startOffset = textElement.getStartOffset();
      final String text = textElement.getDocument().getText( startOffset, endOffset - startOffset );

      if ( parent != null ) {
        final HTML.Tag tag = findTag( parent.getAttributes() );
        if ( "\n".equals( text ) ) {
          if ( BLOCK_ELEMENTS.contains( tag ) || "paragraph".equals( textElement.getName() )
              || "section".equals( textElement.getName() ) ) {
            if ( parent.getElementCount() > 0 && parent.getElement( parent.getElementCount() - 1 ) == textElement ) {
              // Skipping an artificial \n at the end of paragraph element. This is generated by the swing
              // parser and really messes things up here.
              return null;
            }
          }
        }
      }
      final Element result = new Element();
      result.setName( textElement.getName() );
      result.setElementType( LabelType.INSTANCE );
      result.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, text );
      configureStyle( textElement, result );
      if ( HTML.Tag.BR.equals( textElement.getAttributes().getAttribute( StyleConstants.NameAttribute ) ) ) {
        result.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, "\n" );
        result.getStyle().setStyleProperty( TextStyleKeys.TRIM_TEXT_CONTENT, Boolean.FALSE );
        result.getStyle().setStyleProperty( TextStyleKeys.WHITE_SPACE_COLLAPSE, WhitespaceCollapse.PRESERVE );
      }
      return result;
    }

    // we need to intercept for <UL> and <OL> here

    final Band band = new Band();

    configureStyle( textElement, band );
    configureBand( textElement, band );
    final boolean bandIsInline = isInlineElement( band );
    final int size = textElement.getElementCount();
    Band inlineContainer = null;
    for ( int i = 0; i < size; i++ ) {

      String listSign = liNum;
      if ( HTML.Tag.OL.equals( textElement.getAttributes().getAttribute( StyleConstants.NameAttribute ) ) ) {
        listSign = Integer.toString( i + 1 ) + ". ";
      }
      if ( HTML.Tag.UL.equals( textElement.getAttributes().getAttribute( StyleConstants.NameAttribute ) ) ) {
        listSign = "\u00B7";
      }

      final Element element = process( textElement.getElement( i ), listSign );
      if ( element == null ) {
        continue;
      }

      if ( "li".equals(  textElement.getElement( i ).getName() ) ) {
        band.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "block" );
        Band elemlistband = new Band();
        elemlistband.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "block" );
        elemlistband.getStyle().setStyleProperty( ElementStyleKeys.PADDING_LEFT, 10f );
        elemlistband.addElement( element );
        band.addElement( elemlistband );
        continue;
      }

      if ( isInlineElement( element ) == bandIsInline ) {
        if ( "li".equals( textElement.getName() ) ) {
          if ( textElement.getElementCount() == 1
              && ( ( HTML.Tag.OL.equals( textElement.getElement( 0 ).getAttributes().getAttribute( StyleConstants.NameAttribute ) )
                  || ( HTML.Tag.UL.equals( textElement.getElement( 0 ).getAttributes().getAttribute( StyleConstants.NameAttribute ) ) ) ) ) ) {
            band.addElement( createLiNumElement( liNum ) );
          }
        }
        band.addElement( element );
        continue;
      }

      if ( band.getElementCount() == 0 ) {
        inlineContainer = new Band();
        inlineContainer.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "inline" );
        if ( "li".equals( textElement.getParentElement().getName() ) ) {
          inlineContainer.addElement( createLiNumElement( liNum ) );
        }
        inlineContainer.addElement( element );
        band.addElement( inlineContainer );
        continue;
      }

      final Element maybeInlineContainer = (Element) band.getElement( band.getElementCount() - 1 );
      if ( maybeInlineContainer == inlineContainer ) {
        // InlineContainer cannot be null at this point, as band.getElement never returns null.
        // noinspection ConstantConditions
        inlineContainer.addElement( element );
        continue;
      }

      inlineContainer = new Band();
      inlineContainer.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "inline" );
      inlineContainer.addElement( element );
      band.addElement( inlineContainer );
    }
    return band;
  }

  private Element createLiNumElement( final String _liNum ) {
    final Element linum = new Element();
    linum.setName( "point" );
    linum.setElementType( LabelType.INSTANCE );
    linum.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, _liNum );
    return linum;
  }

  private boolean isInlineElement( final Element element ) {
    if ( element instanceof Band ) {
      if ( "inline".equals( element.getStyle().getStyleProperty( BandStyleKeys.LAYOUT, "inline" ) ) ) {
        return true;
      }
      return false;
    }

    return true;
  }

  private boolean isInvisible( final javax.swing.text.Element textElement ) {
    final HTMLDocument htmlDocument = (HTMLDocument) textElement.getDocument();
    final StyleSheet sheet = htmlDocument.getStyleSheet();
    final AttributeSet attr = computeStyle( textElement, sheet );
    final Object o = attr.getAttribute( CSS.Attribute.DISPLAY );
    if ( "none".equals( String.valueOf( o ) ) ) {
      return true;
    }
    final Object tag = findTag( textElement.getAttributes() );
    if ( tag == HTML.Tag.COMMENT ) {
      return true;
    }
    if ( tag == HTML.Tag.SCRIPT ) {
      return true;
    }
    if ( tag == HTML.Tag.HEAD ) {
      return true;
    }
    return false;
  }

  private void configureStyle( final javax.swing.text.Element textElement, final Element result ) {
    final HTMLDocument htmlDocument = (HTMLDocument) textElement.getDocument();
    final StyleSheet sheet = htmlDocument.getStyleSheet();
    final AttributeSet attr = computeStyle( textElement, sheet );

    if ( attr instanceof SimpleAttributeSet && ( (SimpleAttributeSet) attr ).getAttributeCount() == 0 ) {
      return;
    }

    parseBorderAndBackgroundStyle( result, sheet, attr );
    parseBoxStyle( result, attr );

    final Object fontFamily = attr.getAttribute( CSS.Attribute.FONT_FAMILY );
    if ( fontFamily != null ) {
      result.getStyle().setStyleProperty( TextStyleKeys.FONT, String.valueOf( fontFamily ) );
    }

    final Object fontSize = attr.getAttribute( CSS.Attribute.FONT_SIZE );
    if ( fontSize != null ) {
      result.getStyle().setStyleProperty( TextStyleKeys.FONTSIZE, Math.round( parseLength( String.valueOf( fontSize ) ) ) );
    }

    final Object fontWeight = attr.getAttribute( CSS.Attribute.FONT_WEIGHT );
    if ( fontWeight != null ) {
      String fontWeightStr = String.valueOf( fontWeight );
      result.getStyle().setStyleProperty( TextStyleKeys.BOLD, fontWeightStr.toLowerCase().equals( "bold" ) );
    }

    final Object fontStyle = attr.getAttribute( CSS.Attribute.FONT_STYLE );
    if ( fontStyle != null ) {
      String fontStyleStr = String.valueOf( fontStyle );
      result.getStyle().setStyleProperty( TextStyleKeys.ITALIC, fontStyleStr.toLowerCase().equals( "italic" ) );
    }

    final Object letterSpacing = attr.getAttribute( CSS.Attribute.LETTER_SPACING );
    if ( letterSpacing != null ) {
      result.getStyle().setStyleProperty( TextStyleKeys.X_OPTIMUM_LETTER_SPACING,
          parseLength( String.valueOf( letterSpacing ) ) );
    }

    final Object wordSpacing = attr.getAttribute( CSS.Attribute.WORD_SPACING );
    if ( wordSpacing != null ) {
      result.getStyle().setStyleProperty( TextStyleKeys.WORD_SPACING, parseLength( String.valueOf( wordSpacing ) ) );
    }

    final Object lineHeight = attr.getAttribute( CSS.Attribute.LINE_HEIGHT );
    if ( lineHeight != null ) {
      result.getStyle().setStyleProperty( TextStyleKeys.LINEHEIGHT, parseLength( String.valueOf( lineHeight ) ) );
    }
    final Object textAlign = attr.getAttribute( CSS.Attribute.TEXT_ALIGN );
    if ( textAlign != null ) {
      try {
        result.getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT,
            ReportParserUtil.parseHorizontalElementAlignment( String.valueOf( textAlign ), null ) );
      } catch ( ParseException e ) {
        // ignore ..
      }
    }

    final Object textDecoration = attr.getAttribute( CSS.Attribute.TEXT_DECORATION );
    if ( textDecoration != null ) {
      final String[] strings = StringUtils.split( String.valueOf( textDecoration ) );
      result.getStyle().setStyleProperty( TextStyleKeys.STRIKETHROUGH, Boolean.FALSE );
      result.getStyle().setStyleProperty( TextStyleKeys.UNDERLINED, Boolean.FALSE );

      for ( int i = 0; i < strings.length; i++ ) {
        final String value = strings[i];
        if ( "line-through".equals( value ) ) {
          result.getStyle().setStyleProperty( TextStyleKeys.STRIKETHROUGH, Boolean.TRUE );
        }
        if ( "underline".equals( value ) ) {
          result.getStyle().setStyleProperty( TextStyleKeys.UNDERLINED, Boolean.TRUE );
        }
      }
    }

    final Object valign = attr.getAttribute( CSS.Attribute.VERTICAL_ALIGN );
    if ( valign != null ) {
      final VerticalTextAlign valignValue = VerticalTextAlign.valueOf( String.valueOf( valign ) );
      result.getStyle().setStyleProperty( TextStyleKeys.VERTICAL_TEXT_ALIGNMENT, valignValue );
      try {
        result.getStyle().setStyleProperty( ElementStyleKeys.VALIGNMENT,
            ReportParserUtil.parseVerticalElementAlignment( String.valueOf( valign ), null ) );
      } catch ( ParseException e ) {
        // ignore ..
      }
    }

    final Object whitespaceText = attr.getAttribute( CSS.Attribute.WHITE_SPACE );
    if ( whitespaceText != null ) {
      final String value = String.valueOf( whitespaceText );
      if ( "pre".equals( value ) ) {
        result.getStyle().setStyleProperty( TextStyleKeys.WHITE_SPACE_COLLAPSE, WhitespaceCollapse.PRESERVE );
        result.getStyle().setStyleProperty( TextStyleKeys.TEXT_WRAP, TextWrap.NONE );
      } else if ( "nowrap".equals( value ) ) {
        result.getStyle().setStyleProperty( TextStyleKeys.WHITE_SPACE_COLLAPSE, WhitespaceCollapse.PRESERVE_BREAKS );
        result.getStyle().setStyleProperty( TextStyleKeys.TEXT_WRAP, TextWrap.NONE );
      } else {
        result.getStyle().setStyleProperty( TextStyleKeys.WHITE_SPACE_COLLAPSE, WhitespaceCollapse.COLLAPSE );
        result.getStyle().setStyleProperty( TextStyleKeys.TEXT_WRAP, TextWrap.WRAP );
      }
    } else {
      result.getStyle().setStyleProperty( TextStyleKeys.WHITE_SPACE_COLLAPSE, WhitespaceCollapse.COLLAPSE );
      result.getStyle().setStyleProperty( TextStyleKeys.TEXT_WRAP, TextWrap.WRAP );
    }

    final Object alignAttribute = attr.getAttribute( HTML.Attribute.ALIGN );
    if ( alignAttribute != null ) {
      try {
        result.getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT,
            ReportParserUtil.parseHorizontalElementAlignment( String.valueOf( alignAttribute ), null ) );
      } catch ( ParseException e ) {
        // ignore ..
      }
    }

    final Object titleAttribute = attr.getAttribute( HTML.Attribute.TITLE );
    if ( titleAttribute != null ) {
      result.setAttribute( AttributeNames.Html.NAMESPACE, AttributeNames.Html.TITLE, String.valueOf( titleAttribute ) );
    }

    final Object hrefAttribute = attr.getAttribute( HTML.Attribute.HREF );
    if ( hrefAttribute != null ) {
      result.getStyle().setStyleProperty( ElementStyleKeys.HREF_TARGET, String.valueOf( hrefAttribute ) );
    }

    final Object textIndentStyle = attr.getAttribute( CSS.Attribute.TEXT_INDENT );
    if ( textIndentStyle != null ) {
      result.getStyle().setStyleProperty( TextStyleKeys.FIRST_LINE_INDENT,
          parseLength( String.valueOf( textIndentStyle ) ) );
    }

    // attr.getAttribute(CSS.Attribute.LIST_STYLE_TYPE);
    // attr.getAttribute(CSS.Attribute.LIST_STYLE_TYPE);
    // attr.getAttribute(CSS.Attribute.LIST_STYLE_POSITION);
  }

  private HTML.Tag findTag( final AttributeSet attr ) {
    final Enumeration names = attr.getAttributeNames();
    while ( names.hasMoreElements() ) {
      final Object name = names.nextElement();
      final Object o = attr.getAttribute( name );
      if ( o instanceof HTML.Tag ) {
        if ( HTML.Tag.CONTENT == o ) {
          continue;
        }
        if ( HTML.Tag.COMMENT == o ) {
          continue;
        }
        return (HTML.Tag) o;
      }
    }
    return null;
  }

  private void parseBoxStyle( final Element result, final AttributeSet attr ) {
    final Object paddingText = attr.getAttribute( CSS.Attribute.PADDING );
    if ( paddingText != null ) {
      final Float padding = parseLength( String.valueOf( paddingText ) );
      result.getStyle().setStyleProperty( ElementStyleKeys.PADDING_TOP, padding );
      result.getStyle().setStyleProperty( ElementStyleKeys.PADDING_LEFT, padding );
      result.getStyle().setStyleProperty( ElementStyleKeys.PADDING_BOTTOM, padding );
      result.getStyle().setStyleProperty( ElementStyleKeys.PADDING_RIGHT, padding );
    }

    final Object paddingTop = attr.getAttribute( CSS.Attribute.PADDING_TOP );
    if ( paddingTop != null ) {
      final Float padding = parseLength( String.valueOf( paddingTop ) );
      result.getStyle().setStyleProperty( ElementStyleKeys.PADDING_TOP, padding );
    }
    final Object paddingLeft = attr.getAttribute( CSS.Attribute.PADDING_LEFT );
    if ( paddingLeft != null ) {
      final Float padding = parseLength( String.valueOf( paddingLeft ) );
      result.getStyle().setStyleProperty( ElementStyleKeys.PADDING_LEFT, padding );
    }
    final Object paddingBottom = attr.getAttribute( CSS.Attribute.PADDING_BOTTOM );
    if ( paddingBottom != null ) {
      final Float padding = parseLength( String.valueOf( paddingBottom ) );
      result.getStyle().setStyleProperty( ElementStyleKeys.PADDING_BOTTOM, padding );
    }
    final Object paddingRight = attr.getAttribute( CSS.Attribute.PADDING_RIGHT );
    if ( paddingRight != null ) {
      final Float padding = parseLength( String.valueOf( paddingRight ) );
      result.getStyle().setStyleProperty( ElementStyleKeys.PADDING_RIGHT, padding );
    }

    final Object heightText = attr.getAttribute( CSS.Attribute.HEIGHT );
    if ( heightText != null ) {
      result.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, parseLength( String.valueOf( heightText ) ) );
    }
    final Object widthText = attr.getAttribute( CSS.Attribute.WIDTH );
    if ( widthText != null ) {
      result.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, parseLength( String.valueOf( widthText ) ) );
    }
  }

  private void parseBorderAndBackgroundStyle( final Element result, final StyleSheet sheet, final AttributeSet attr ) {
    final Object backgroundColor = attr.getAttribute( CSS.Attribute.BACKGROUND_COLOR );
    if ( backgroundColor != null ) {
      result.getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR,
          sheet.stringToColor( String.valueOf( backgroundColor ) ) );
    }
    final Object borderStyleText = attr.getAttribute( CSS.Attribute.BORDER_STYLE );
    if ( borderStyleText != null ) {
      final BorderStyle borderStyle = BorderStyle.getBorderStyle( String.valueOf( borderStyleText ) );
      if ( borderStyle != null ) {
        result.getStyle().setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_STYLE, borderStyle );
        result.getStyle().setStyleProperty( ElementStyleKeys.BORDER_TOP_STYLE, borderStyle );
        result.getStyle().setStyleProperty( ElementStyleKeys.BORDER_LEFT_STYLE, borderStyle );
        result.getStyle().setStyleProperty( ElementStyleKeys.BORDER_RIGHT_STYLE, borderStyle );
      }
    }
    final Object borderWidthText = attr.getAttribute( CSS.Attribute.BORDER_WIDTH );
    if ( borderWidthText != null ) {
      final Float borderWidth = parseLength( String.valueOf( borderWidthText ) );
      result.getStyle().setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_WIDTH, borderWidth );
      result.getStyle().setStyleProperty( ElementStyleKeys.BORDER_TOP_WIDTH, borderWidth );
      result.getStyle().setStyleProperty( ElementStyleKeys.BORDER_LEFT_WIDTH, borderWidth );
      result.getStyle().setStyleProperty( ElementStyleKeys.BORDER_RIGHT_WIDTH, borderWidth );
    }

    final Object borderBottomWidthText = attr.getAttribute( CSS.Attribute.BORDER_BOTTOM_WIDTH );
    if ( borderBottomWidthText != null ) {
      final Float borderWidth = parseLength( String.valueOf( borderBottomWidthText ) );
      result.getStyle().setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_WIDTH, borderWidth );
    }

    final Object borderRightWidthText = attr.getAttribute( CSS.Attribute.BORDER_RIGHT_WIDTH );
    if ( borderRightWidthText != null ) {
      final Float borderWidth = parseLength( String.valueOf( borderRightWidthText ) );
      result.getStyle().setStyleProperty( ElementStyleKeys.BORDER_RIGHT_WIDTH, borderWidth );
    }

    final Object borderTopWidthText = attr.getAttribute( CSS.Attribute.BORDER_TOP_WIDTH );
    if ( borderTopWidthText != null ) {
      final Float borderWidth = parseLength( String.valueOf( borderTopWidthText ) );
      result.getStyle().setStyleProperty( ElementStyleKeys.BORDER_TOP_WIDTH, borderWidth );
    }

    final Object borderLeftWidth = attr.getAttribute( CSS.Attribute.BORDER_LEFT_WIDTH );
    if ( borderLeftWidth != null ) {
      final Float borderWidth = parseLength( String.valueOf( borderLeftWidth ) );
      result.getStyle().setStyleProperty( ElementStyleKeys.BORDER_LEFT_WIDTH, borderWidth );
    }

    final Object colorText = attr.getAttribute( CSS.Attribute.COLOR );
    if ( colorText != null ) {
      final Color color = sheet.stringToColor( String.valueOf( colorText ) );
      result.getStyle().setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_COLOR, color );
      result.getStyle().setStyleProperty( ElementStyleKeys.BORDER_TOP_COLOR, color );
      result.getStyle().setStyleProperty( ElementStyleKeys.BORDER_LEFT_COLOR, color );
      result.getStyle().setStyleProperty( ElementStyleKeys.BORDER_RIGHT_COLOR, color );
      result.getStyle().setStyleProperty( ElementStyleKeys.PAINT, color );
    }

    final Object borderColorText = attr.getAttribute( CSS.Attribute.BORDER_COLOR );
    if ( borderColorText != null ) {
      final Color borderColor = sheet.stringToColor( String.valueOf( borderColorText ) );
      result.getStyle().setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_COLOR, borderColor );
      result.getStyle().setStyleProperty( ElementStyleKeys.BORDER_TOP_COLOR, borderColor );
      result.getStyle().setStyleProperty( ElementStyleKeys.BORDER_LEFT_COLOR, borderColor );
      result.getStyle().setStyleProperty( ElementStyleKeys.BORDER_RIGHT_COLOR, borderColor );
    }
  }

  private Float parseLength( final String value ) {
    if ( value == null ) {
      return null;
    }

    try {
      final StreamTokenizer strtok = new StreamTokenizer( new StringReader( value ) );
      strtok.parseNumbers();
      final int firstToken = strtok.nextToken();
      if ( firstToken != StreamTokenizer.TT_NUMBER ) {
        return null;
      }
      final double nval = strtok.nval;
      final int nextToken = strtok.nextToken();
      if ( nextToken != StreamTokenizer.TT_WORD ) {
        // yeah, this is against the standard, but we are dealing with deadly ugly non-standard documents here
        // maybe we will be able to integrate a real HTML processor at some point.
        return new Float( nval );
      }

      final String unit = strtok.sval;
      if ( "%".equals( unit ) ) {
        return new Float( -nval );
      }
      if ( "cm".equals( unit ) ) {
        return new Float( nval * 25.4 / 72 );
      }

      if ( "mm".equals( unit ) ) {
        return new Float( nval * 2.54 / 72 );
      }
      if ( "pt".equals( unit ) ) {
        return new Float( nval );
      }
      if ( "in".equals( unit ) ) {
        return new Float( nval * 72 );
      }
      if ( "px".equals( unit ) ) {
        return Float.valueOf((float) (nval * 0.75));
      }
      if ( "pc".equals( unit ) ) {
        return new Float( nval * 12 );
      }
      return null;
    } catch ( IOException ioe ) {
      return null;
    }
  }

  private void configureBand( final javax.swing.text.Element textElement, final Band band ) {
    final HTML.Tag tag = findTag( textElement.getAttributes() );
    if ( tag == null ) {
      if ( "paragraph".equals( textElement.getName() ) || "section".equals( textElement.getName() ) ) {
        band.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "block" );
        band.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( -100 ) );
        band.setName( textElement.getName() );
      } else {
        band.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "inline" );
        band.setName( textElement.getName() );
      }
      return;
    }

    if ( BLOCK_ELEMENTS.contains( tag ) ) {
      band.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "block" );
      band.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( -100 ) );
      band.setName( String.valueOf( tag ) );
    } else {
      band.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "inline" );
      band.setName( String.valueOf( tag ) );
    }
  }
}
