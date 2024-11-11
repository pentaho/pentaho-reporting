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
