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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates;

/**
 * A template collection.
 *
 * @author Thomas Morgner.
 */
public class DefaultTemplateCollection extends TemplateCollection {
  /**
   * Creates a new collection.
   */
  public DefaultTemplateCollection() {
    addTemplate( new AnchorFieldTemplateDescription( "anchor-field" ) );
    addTemplate( new DateFieldTemplateDescription( "date-field" ) );
    addTemplate( new ComponentFieldTemplateDescription( "component-field" ) );
    addTemplate( new DrawableFieldTemplateDescription( "drawable-field" ) );
    addTemplate( new DrawableURLElementTemplateDescription( "drawable-url-element" ) );
    addTemplate( new DrawableURLFieldTemplateDescription( "drawable-url-field" ) );
    addTemplate( new EllipseTemplateDescription( "ellipse" ) );
    addTemplate( new HorizontalLineTemplateDescription( "horizontal-line" ) );
    addTemplate( new ImageFieldTemplateDescription( "image-field" ) );
    addTemplate( new ImageURLElementTemplateDescription( "image-url-element" ) );
    addTemplate( new ImageURLFieldTemplateDescription( "image-url-field" ) );
    addTemplate( new LabelTemplateDescription( "label" ) );
    addTemplate( new MessageFieldTemplateDescription( "message-field" ) );
    addTemplate( new NumberFieldTemplateDescription( "number-field" ) );
    addTemplate( new RectangleTemplateDescription( "rectangle" ) );
    addTemplate( new RoundRectangleTemplateDescription( "round-rectangle" ) );
    addTemplate( new ResourceFieldTemplateDescription( "resource-field" ) );
    addTemplate( new ResourceLabelTemplateDescription( "resource-label" ) );
    addTemplate( new ResourceMessageTemplateDescription( "resource-message" ) );
    addTemplate( new ShapeFieldTemplateDescription( "shape-field" ) );
    addTemplate( new StringFieldTemplateDescription( "string-field" ) );
    addTemplate( new VerticalLineTemplateDescription( "vertical-line" ) );
  }
}
