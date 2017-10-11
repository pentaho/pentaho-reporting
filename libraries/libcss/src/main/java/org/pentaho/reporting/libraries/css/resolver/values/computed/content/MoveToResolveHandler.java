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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.css.resolver.values.computed.content;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.keys.content.ContentStyleKeys;
import org.pentaho.reporting.libraries.css.keys.content.MoveToValues;
import org.pentaho.reporting.libraries.css.keys.internal.InternalStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.tokens.ContentToken;
import org.pentaho.reporting.libraries.css.resolver.tokens.computed.CounterToken;
import org.pentaho.reporting.libraries.css.resolver.tokens.computed.CountersToken;
import org.pentaho.reporting.libraries.css.resolver.tokens.resolved.ResolvedCounterToken;
import org.pentaho.reporting.libraries.css.resolver.values.ContentSpecification;
import org.pentaho.reporting.libraries.css.resolver.values.ResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSValue;

public class MoveToResolveHandler implements ResolveHandler {
  public MoveToResolveHandler() {
  }

  /**
   * This indirectly defines the resolve order. The higher the order, the more dependent is the resolver on other
   * resolvers to be complete.
   *
   * @return the array of required style keys.
   */
  public StyleKey[] getRequiredStyles() {
    // no further dependecies. (We depend on the parent, not the current element)
    return new StyleKey[ 0 ];
  }

  private boolean isCounterUsed( final LayoutElement element, final String counter ) {
    final LayoutStyle layoutContext = element.getLayoutStyle();
    final ContentSpecification contentSpecification =
      (ContentSpecification) layoutContext.getValue( InternalStyleKeys.INTERNAL_CONTENT );
    final ContentToken[] contents = contentSpecification.getContents();
    for ( int i = 0; i < contents.length; i++ ) {
      ContentToken content = contents[ i ];
      if ( content instanceof ResolvedCounterToken ) {
        // this should not happen, as the resolving of content-tokens happens
        // after the style resolving process ..
        final ResolvedCounterToken computedToken = (ResolvedCounterToken) content;
        content = computedToken.getParent();
      }

      if ( content instanceof CounterToken ) {
        final CounterToken counterToken = (CounterToken) content;
        if ( counterToken.getName().equals( counter ) ) {
          return true;
        }
      } else if ( content instanceof CountersToken ) {
        final CountersToken counterToken = (CountersToken) content;
        if ( counterToken.getName().equals( counter ) ) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Resolves a single property.
   *
   * @param currentNode
   */
  public void resolve( final DocumentContext process,
                       final LayoutElement currentNode,
                       final StyleKey key ) {
    final LayoutStyle layoutContext = currentNode.getLayoutStyle();
    final CSSValue value = layoutContext.getValue( ContentStyleKeys.MOVE_TO );

    // Maybe this is a 'normal'.
    if ( MoveToValues.NORMAL.equals( value ) ) {
      // todo: Some creepy special cases that require information about pseudo-elements.
      //      if ("alternate".equals(layoutContext.getPseudoElement()))
      //      {
      //        // For '::alternate' pseudo-elements, if the superior parent uses
      //        // the 'footnote' counter in its 'content' property then the computed
      //        // value of 'move-to' is 'footnotes'.
      //        if (isCounterUsed(currentNode.getParentLayoutElement(), "footnote"))
      //        {
      //          layoutContext.setValue(ContentStyleKeys.MOVE_TO,
      //              new CSSStringValue(CSSStringType.STRING, "footnotes"));
      //          return;
      //        }
      //
      //        // For '::alternate' pseudo-elements, if the superior parent uses
      //        // the 'endnote' counter in its 'content' property then the computed
      //        // value of 'move-to' is 'endnotes'.
      //        if (isCounterUsed(currentNode.getParentLayoutElement(), "endnote"))
      //        {
      //          layoutContext.setValue(ContentStyleKeys.MOVE_TO,
      //              new CSSStringValue(CSSStringType.STRING, "endnotes"));
      //          return;
      //        }
      //
      //        // For '::alternate' pseudo-elements, if the superior parent uses
      //        // the 'section-note' counter in its 'content' property then the
      //        // computed value of 'move-to' is 'section-notes'.
      //        if (isCounterUsed(currentNode.getParentLayoutElement(), "section-note"))
      //        {
      //          layoutContext.setValue(ContentStyleKeys.MOVE_TO,
      //              new CSSStringValue(CSSStringType.STRING, "section-notes"));
      //          return;
      //        }
      //      }
      layoutContext.setValue( ContentStyleKeys.MOVE_TO, MoveToValues.HERE );
    }
  }
}
