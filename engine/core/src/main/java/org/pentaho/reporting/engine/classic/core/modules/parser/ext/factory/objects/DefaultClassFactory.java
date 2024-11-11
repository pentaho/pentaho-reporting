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


package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.objects;

import java.awt.Shape;
import java.awt.geom.GeneralPath;

import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.JavaBaseClassFactory;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.BoxSizing;
import org.pentaho.reporting.engine.classic.core.style.FontDefinition;
import org.pentaho.reporting.engine.classic.core.style.FontSmooth;
import org.pentaho.reporting.engine.classic.core.style.TextWrap;
import org.pentaho.reporting.engine.classic.core.style.VerticalTextAlign;
import org.pentaho.reporting.engine.classic.core.style.WhitespaceCollapse;

/**
 * A default implementation of the
 * {@link org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base .ClassFactory} interface.
 *
 * @author Thomas Morgner
 */
public class DefaultClassFactory extends JavaBaseClassFactory {
  /**
   * Creates a new factory.
   */
  public DefaultClassFactory() {
    registerClass( BorderStyle.class, new BorderStyleObjectDescription() );
    registerClass( VerticalTextAlign.class, new VerticalAlignmentObjectDescription() );
    registerClass( ElementAlignment.class, new AlignmentObjectDescription() );
    registerClass( FontDefinition.class, new FontDefinitionObjectDescription() );
    registerClass( PathIteratorSegment.class, new PathIteratorSegmentObjectDescription() );
    registerClass( Shape.class, new GeneralPathObjectDescription( Shape.class ) );
    registerClass( GeneralPath.class, new GeneralPathObjectDescription() );
    registerClass( FontSmooth.class, new FontSmoothObjectDescription() );
    registerClass( TextWrap.class, new TextWrapObjectDescription() );
    registerClass( BoxSizing.class, new BoxSizingObjectDescription() );
    registerClass( WhitespaceCollapse.class, new WhitespaceCollapseObjectDescription() );
  }
}
