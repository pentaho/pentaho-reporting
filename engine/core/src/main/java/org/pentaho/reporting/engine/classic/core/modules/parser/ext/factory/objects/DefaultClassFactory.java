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
