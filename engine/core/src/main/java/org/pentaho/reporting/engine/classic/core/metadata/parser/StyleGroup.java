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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.metadata.parser;

import org.pentaho.reporting.engine.classic.core.metadata.builder.StyleMetaDataBuilder;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A internal helper object to store an attribute-group definition.
 *
 * @author Thomas Morgner
 */
public class StyleGroup {
  private String name;
  private ArrayList<StyleMetaDataBuilder> metaData;

  public StyleGroup( final String name, final Collection<StyleMetaDataBuilder> metaData ) {
    ArgumentNullException.validate( "name", name );
    ArgumentNullException.validate( "metaData", metaData );

    this.name = name;
    this.metaData = new ArrayList<StyleMetaDataBuilder>( metaData );
  }

  public String getName() {
    return name;
  }

  public List<StyleMetaDataBuilder> getMetaData() {
    return (List<StyleMetaDataBuilder>) metaData.clone();
  }
}
