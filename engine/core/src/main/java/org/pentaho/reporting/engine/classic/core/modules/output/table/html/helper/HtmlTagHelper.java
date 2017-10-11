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

package org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper;

import java.awt.Color;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackground;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SlimSheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlRowBackgroundStruct;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlTableModule;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.util.HtmlColors;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;

@SuppressWarnings( "HardCodedStringLiteral" )
public class HtmlTagHelper {
  public static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";

  private Configuration configuration;
  private StyleBuilderFactory styleBuilderFactory;
  private StyleBuilder styleBuilder;
  private StyleManager styleManager;

  public HtmlTagHelper( final Configuration configuration, final StyleBuilderFactory styleBuilderFactory ) {
    this.configuration = configuration;
    this.styleBuilderFactory = styleBuilderFactory;
    this.styleBuilder = new DefaultStyleBuilder( styleBuilderFactory );
  }

  public StyleBuilder getStyleBuilder() {
    return styleBuilder;
  }

  public StyleBuilderFactory getStyleBuilderFactory() {
    return styleBuilderFactory;
  }

  public StyleManager getStyleManager() {
    return styleManager;
  }

  public void setStyleManager( final StyleManager styleManager ) {
    this.styleManager = styleManager;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  public AttributeList createCellAttributes( final int colSpan, final int rowSpan,
      final ReportAttributeMap<Object> content, final StyleSheet styleSheet, final CellBackground background,
      final StyleBuilder styleBuilder ) {

    final AttributeList attrList = new AttributeList();
    if ( content != null ) {
      // ignore for now ..
      if ( rowSpan > 1 ) {
        attrList.setAttribute( HtmlPrinter.XHTML_NAMESPACE, "rowspan", String.valueOf( rowSpan ) );
      }
      if ( colSpan > 1 ) {
        attrList.setAttribute( HtmlPrinter.XHTML_NAMESPACE, "colspan", String.valueOf( colSpan ) );
      }

      final ElementAlignment verticalAlignment =
          (ElementAlignment) styleSheet.getStyleProperty( ElementStyleKeys.VALIGNMENT );
      attrList.setAttribute( HtmlPrinter.XHTML_NAMESPACE, "valign", translateVerticalAlignment( verticalAlignment ) );
    }

    if ( background != null && content != null ) {
      final ReportAttributeMap<Object> attrs = new ReportAttributeMap<Object>( background.getAttributes() );
      attrs.putAll( content );
      applyHtmlAttributes( attrs, attrList );
    } else if ( background != null ) {
      final ReportAttributeMap attrs = background.getAttributes();
      applyHtmlAttributes( attrs, attrList );
    } else if ( content != null ) {
      applyHtmlAttributes( content, attrList );
    }
    styleManager.updateStyle( styleBuilder, attrList );
    return attrList;
  }

  /**
   * Translates the JFreeReport horizontal element alignment into a HTML alignment constant.
   *
   * @param ea
   *          the element alignment
   * @return the translated alignment name.
   */
  private String translateVerticalAlignment( final ElementAlignment ea ) {
    if ( ElementAlignment.BOTTOM.equals( ea ) ) {
      return "bottom";
    }
    if ( ElementAlignment.MIDDLE.equals( ea ) ) {
      return "middle";
    }
    return "top";
  }

  public AttributeList createRowAttributes( final double rowHeight, final HtmlRowBackgroundStruct struct ) {
    final AttributeList attrList = new AttributeList();
    StyleBuilder styleBuilder = getStyleBuilder();
    StyleBuilderFactory styleBuilderFactory = getStyleBuilderFactory();

    if ( isTableRowBorderDefinition() ) {
      styleBuilder.clear();

      if ( struct.isFailed() == false ) {
        final Color commonBackgroundColor = struct.getColor();
        final BorderEdge top = struct.getTopEdge();
        final BorderEdge bottom = struct.getBottomEdge();
        if ( commonBackgroundColor != null ) {
          styleBuilder.append( DefaultStyleBuilder.CSSKeys.BACKGROUND_COLOR, HtmlColors
              .getColorString( commonBackgroundColor ) );
        }
        if ( BorderEdge.EMPTY.equals( top ) == false ) {
          styleBuilder.appendRaw( DefaultStyleBuilder.CSSKeys.BORDER_TOP, styleBuilder.printEdgeAsCSS( top ) );
        }
        if ( BorderEdge.EMPTY.equals( bottom ) == false ) {
          styleBuilder.appendRaw( DefaultStyleBuilder.CSSKeys.BORDER_BOTTOM, styleBuilder.printEdgeAsCSS( bottom ) );
        }
      }
      styleBuilder.append( DefaultStyleBuilder.CSSKeys.HEIGHT, styleBuilder.getPointConverter().format(
          styleBuilderFactory.fixLengthForSafari( rowHeight ) ), "pt" );
      styleManager.updateStyle( styleBuilder, attrList );
    } else {
      // equally expensive and makes text more readable (helps with debugging)
      attrList.setAttribute( HtmlPrinter.XHTML_NAMESPACE, "style", "height: "
          + styleBuilder.getPointConverter().format( styleBuilderFactory.fixLengthForSafari( rowHeight ) ) + "pt" );
    }
    return attrList;
  }

  public AttributeList createSheetNameAttributes() {
    final AttributeList tableAttrList = new AttributeList();

    final String additionalStyleClass =
        getConfiguration().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.modules.output.table.html.SheetNameClass" );
    if ( additionalStyleClass != null ) {
      tableAttrList.setAttribute( HtmlPrinter.XHTML_NAMESPACE, "class", additionalStyleClass );
    }

    return tableAttrList;
  }

  protected boolean isProportionalColumnWidths() {
    return "true".equals( getConfiguration().getConfigProperty( HtmlTableModule.PROPORTIONAL_COLUMN_WIDTHS, "false" ) );
  }

  public boolean isEmptyCellsUseCSS() {
    return "true".equals( getConfiguration().getConfigProperty( HtmlTableModule.EMPTY_CELLS_USE_CSS, "false" ) );
  }

  public AttributeList createTableAttributes( final SlimSheetLayout sheetLayout, final ReportAttributeMap attr ) {
    StyleBuilder styleBuilder = getStyleBuilder();
    final int noc = sheetLayout.getColumnCount();
    styleBuilder.clear();
    if ( ( noc > 0 ) && ( isProportionalColumnWidths() == false ) ) {
      final int width = (int) StrictGeomUtility.toExternalValue( sheetLayout.getCellWidth( 0, noc ) );
      styleBuilder.append( DefaultStyleBuilder.CSSKeys.WIDTH, width + "pt" );
    } else {
      // Consume the complete width for proportional column widths
      styleBuilder.append( DefaultStyleBuilder.CSSKeys.WIDTH, "100%" );
    }

    // style += "table-layout: fixed;";
    if ( isTableRowBorderDefinition() ) {
      styleBuilder.append( DefaultStyleBuilder.CSSKeys.BORDER_COLLAPSE, "collapse" );
    }
    if ( isEmptyCellsUseCSS() ) {
      styleBuilder.append( DefaultStyleBuilder.CSSKeys.EMPTY_CELLS, "show" );
    }
    if ( isUseTableLayoutFixed() ) {
      styleBuilder.append( DefaultStyleBuilder.CSSKeys.TABLE_LAYOUT, "fixed" );
    }

    final String additionalStyleClass =
        getConfiguration().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.modules.output.table.html.StyleClass" );

    final AttributeList tableAttrList = new AttributeList();
    if ( additionalStyleClass != null ) {
      tableAttrList.setAttribute( HtmlPrinter.XHTML_NAMESPACE, "class", additionalStyleClass );
    }
    tableAttrList.setAttribute( HtmlPrinter.XHTML_NAMESPACE, "cellspacing", "0" );
    tableAttrList.setAttribute( HtmlPrinter.XHTML_NAMESPACE, "cellpadding", "0" );

    applyHtmlAttributes( attr, tableAttrList );

    styleManager.updateStyle( styleBuilder, tableAttrList );
    return tableAttrList;
  }

  public static void applyHtmlAttributes( final ReportAttributeMap attributes, final AttributeList attrList ) {
    if ( attributes == null ) {
      throw new NullPointerException( "Attributes must not be null" );
    }
    if ( attrList == null ) {
      throw new NullPointerException();
    }

    final Object name = attributes.getAttribute( AttributeNames.Html.NAMESPACE, AttributeNames.Html.NAME );
    if ( name != null ) {
      attrList.setAttribute( XHTML_NAMESPACE, "name", String.valueOf( name ) );
    }
    final Object id = attributes.getAttribute( AttributeNames.Html.NAMESPACE, AttributeNames.Html.XML_ID );
    if ( id != null ) {
      attrList.setAttribute( XHTML_NAMESPACE, "id", String.valueOf( id ) );
    }
    final Object styleClass = attributes.getAttribute( AttributeNames.Html.NAMESPACE, AttributeNames.Html.STYLE_CLASS );
    if ( styleClass != null ) {
      final String styleClassAttr = attrList.getAttribute( XHTML_NAMESPACE, "class" );
      if ( styleClassAttr == null ) {
        attrList.setAttribute( XHTML_NAMESPACE, "class", String.valueOf( styleClass ) );
      } else {
        attrList.setAttribute( XHTML_NAMESPACE, "class", styleClassAttr + ' ' + String.valueOf( styleClass ) );
      }
    }
    final Object onClick = attributes.getAttribute( AttributeNames.Html.NAMESPACE, AttributeNames.Html.ONCLICK );
    if ( onClick != null ) {
      attrList.setAttribute( XHTML_NAMESPACE, "onclick", String.valueOf( onClick ) );
    }
    final Object onDblClick = attributes.getAttribute( AttributeNames.Html.NAMESPACE, AttributeNames.Html.ONDBLCLICK );
    if ( onDblClick != null ) {
      attrList.setAttribute( XHTML_NAMESPACE, "ondblclick", String.valueOf( onDblClick ) );
    }
    final Object onKeyDown = attributes.getAttribute( AttributeNames.Html.NAMESPACE, AttributeNames.Html.ONKEYDOWN );
    if ( onKeyDown != null ) {
      attrList.setAttribute( XHTML_NAMESPACE, "onkeydown", String.valueOf( onKeyDown ) );
    }
    final Object onKeyPressed =
        attributes.getAttribute( AttributeNames.Html.NAMESPACE, AttributeNames.Html.ONKEYPRESSED );
    if ( onKeyPressed != null ) {
      attrList.setAttribute( XHTML_NAMESPACE, "onkeypressed", String.valueOf( onKeyPressed ) );
    }
    final Object onKeyUp = attributes.getAttribute( AttributeNames.Html.NAMESPACE, AttributeNames.Html.ONKEYUP );
    if ( onKeyUp != null ) {
      attrList.setAttribute( XHTML_NAMESPACE, "onkeyup", String.valueOf( onKeyUp ) );
    }
    final Object onMouseDown = attributes.getAttribute( AttributeNames.Html.NAMESPACE, AttributeNames.Html.ONMOUSEDOWN );
    if ( onMouseDown != null ) {
      attrList.setAttribute( XHTML_NAMESPACE, "onmousedown", String.valueOf( onMouseDown ) );
    }
    final Object onMouseMove = attributes.getAttribute( AttributeNames.Html.NAMESPACE, AttributeNames.Html.ONMOUSEMOVE );
    if ( onMouseMove != null ) {
      attrList.setAttribute( XHTML_NAMESPACE, "onmousemove", String.valueOf( onMouseMove ) );
    }
    final Object onMouseOver = attributes.getAttribute( AttributeNames.Html.NAMESPACE, AttributeNames.Html.ONMOUSEOVER );
    if ( onMouseOver != null ) {
      attrList.setAttribute( XHTML_NAMESPACE, "onmouseover", String.valueOf( onMouseOver ) );
    }
    final Object onMouseUp = attributes.getAttribute( AttributeNames.Html.NAMESPACE, AttributeNames.Html.ONMOUSEUP );
    if ( onMouseUp != null ) {
      attrList.setAttribute( XHTML_NAMESPACE, "onmouseup", String.valueOf( onMouseUp ) );
    }
    final Object onMouseOut = attributes.getAttribute( AttributeNames.Html.NAMESPACE, AttributeNames.Html.ONMOUSEOUT );
    if ( onMouseOut != null ) {
      attrList.setAttribute( XHTML_NAMESPACE, "onmouseout", String.valueOf( onMouseOut ) );
    }
    final Object onMouseEnter =
        attributes.getAttribute( AttributeNames.Html.NAMESPACE, AttributeNames.Html.ONMOUSEENTER );
    if ( onMouseEnter != null ) {
      attrList.setAttribute( XHTML_NAMESPACE, "onmouseenter", String.valueOf( onMouseEnter ) );
    }
    final Object title = attributes.getAttribute( AttributeNames.Html.NAMESPACE, AttributeNames.Html.TITLE );
    if ( title != null ) {
      attrList.setAttribute( XHTML_NAMESPACE, "title", String.valueOf( title ) );
    }
  }

  private boolean isUseTableLayoutFixed() {
    return "true".equals( getConfiguration().getConfigProperty( HtmlTableModule.USE_TABLE_LAYOUT_FIXED, "true" ) );
  }

  private boolean isTableRowBorderDefinition() {
    return "true".equals( getConfiguration().getConfigProperty( HtmlTableModule.TABLE_ROW_BORDER_DEFINITION, "false" ) );
  }

}
