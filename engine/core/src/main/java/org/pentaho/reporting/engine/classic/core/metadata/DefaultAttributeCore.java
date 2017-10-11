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

package org.pentaho.reporting.engine.classic.core.metadata;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.MessageFormatSupport;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.formula.parser.ParseException;
import org.pentaho.reporting.libraries.formula.util.FormulaUtil;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.util.ArrayList;

public class DefaultAttributeCore implements AttributeCore {
  private static final String[] EMPTY = new String[0];
  private static final ResourceReference[] EMPTY_RESOURCES = new ResourceReference[0];

  public DefaultAttributeCore() {
  }

  public String[] getReferencedFields( final AttributeMetaData metaData, final ReportElement element,
      final Object attributeValue ) {
    if ( element == null ) {
      throw new NullPointerException();
    }

    if ( attributeValue == null ) {
      return EMPTY;
    }
    final String valueRole = metaData.getValueRole();
    if ( "Field".equals( valueRole ) ) {
      if ( attributeValue instanceof String[] ) {
        final String[] vals = (String[]) attributeValue;
        return vals.clone();
      }
      return new String[] { String.valueOf( attributeValue ) };
    } else if ( "Message".equals( valueRole ) ) {
      final String message = String.valueOf( attributeValue );
      final MessageFormatSupport messageFormatSupport = new MessageFormatSupport();
      messageFormatSupport.setFormatString( message );
      return messageFormatSupport.getFields();
    } else if ( "Formula".equals( valueRole ) ) {
      final String formula = String.valueOf( attributeValue );
      try {
        final Object[] objects = FormulaUtil.getReferences( formula );
        final ArrayList<String> list = new ArrayList<String>();
        for ( int i = 0; i < objects.length; i++ ) {
          final Object object = objects[i];
          if ( object instanceof String ) {
            list.add( (String) object );
          }
        }
        return list.toArray( new String[list.size()] );
      } catch ( ParseException e ) {
        return EMPTY;
      }
    }
    return EMPTY;
  }

  public String[] getReferencedGroups( final AttributeMetaData metaData, final ReportElement element,
      final Object attributeValue ) {
    final String valueRole = metaData.getValueRole();
    if ( element == null ) {
      throw new NullPointerException();
    }

    if ( attributeValue == null ) {
      return EMPTY;
    }
    if ( "Group".equals( valueRole ) ) {
      if ( attributeValue instanceof String[] ) {
        final String[] attrVal = (String[]) attributeValue;
        return attrVal.clone();
      }
      return new String[] { String.valueOf( attributeValue ) };
    }
    return EMPTY;
  }

  public ResourceReference[] getReferencedResources( final AttributeMetaData metaData, final ReportElement element,
      final ResourceManager resourceManager, final Object attributeValue ) {
    if ( element == null ) {
      throw new NullPointerException();
    }
    if ( resourceManager == null ) {
      throw new NullPointerException();
    }

    final String valueRole = metaData.getValueRole();
    if ( "Content".equals( valueRole ) ) {
      final ResourceKey contentBase = getContentBase( element );
      final ResourceKey elementSource = getDefinitionSource( element );
      if ( attributeValue instanceof ResourceKey ) {
        final ResourceKey path = (ResourceKey) attributeValue;
        final boolean linked = BundleUtilities.isSameBundle( elementSource, path );
        return new ResourceReference[] { new ResourceReference( path, linked ) };
      } else if ( attributeValue instanceof String && contentBase != null ) {
        try {
          // not a resource-key, so try to make one ..
          final ResourceKey path = resourceManager.deriveKey( contentBase, String.valueOf( attributeValue ) );
          // the content base may not point to a bundle location, so that the path does not point to a
          // bundle location as well. If linked is computed to false, we can be sure that the resource is loaded
          // from within the bundle, which means that the attribute value was a relative path inside the bundle.
          final boolean linked = BundleUtilities.isSameBundle( elementSource, path ) == false;
          return new ResourceReference[] { new ResourceReference( path, linked ) };
        } catch ( ResourceKeyCreationException rce ) {
          // ignore ..
        }
      } else if ( attributeValue != null ) {
        try {
          // not a resource-key, so try to make one ..
          final ResourceKey path = resourceManager.createKey( attributeValue );
          final boolean linked = BundleUtilities.isSameBundle( elementSource, path ) == false;
          return new ResourceReference[] { new ResourceReference( path, linked ) };
        } catch ( ResourceKeyCreationException rce ) {
          // ignore ..
        }
      }

    }
    return EMPTY_RESOURCES;
  }

  protected ResourceKey getDefinitionSource( final ReportElement element ) {
    final Object o = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.SOURCE );
    if ( o instanceof ResourceKey ) {
      return (ResourceKey) o;
    }
    final ReportElement parent = element.getParentSection();
    if ( parent != null ) {
      return getDefinitionSource( parent );
    }
    return null;
  }

  protected ResourceKey getContentBase( final ReportElement element ) {
    final Object o = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.CONTENT_BASE );
    if ( o instanceof ResourceKey ) {
      return (ResourceKey) o;
    }
    final ReportElement parent = element.getParentSection();
    if ( parent != null ) {
      return getContentBase( parent );
    }
    return null;
  }

  public String[] getExtraCalculationFields( final AttributeMetaData metaData ) {
    return new String[0];
  }
}
