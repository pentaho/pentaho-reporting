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

package org.pentaho.reporting.libraries.css.resolver.impl;

import org.pentaho.reporting.libraries.css.PageAreaType;
import org.pentaho.reporting.libraries.css.PseudoPage;
import org.pentaho.reporting.libraries.css.dom.DefaultLayoutStyle;
import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.model.CSSDeclarationRule;
import org.pentaho.reporting.libraries.css.model.CSSPageAreaRule;
import org.pentaho.reporting.libraries.css.model.CSSPageRule;
import org.pentaho.reporting.libraries.css.model.CSSStyleRule;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;
import org.pentaho.reporting.libraries.css.namespace.NamespaceCollection;
import org.pentaho.reporting.libraries.css.namespace.NamespaceDefinition;
import org.pentaho.reporting.libraries.css.namespace.Namespaces;
import org.pentaho.reporting.libraries.css.parser.StyleSheetParserUtil;
import org.pentaho.reporting.libraries.css.resolver.StyleResolver;
import org.pentaho.reporting.libraries.css.resolver.StyleRuleMatcher;
import org.pentaho.reporting.libraries.css.selectors.CSSSelector;
import org.pentaho.reporting.libraries.css.selectors.SelectorWeight;
import org.pentaho.reporting.libraries.css.values.CSSInheritValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A cascading style resolver. This resolver follows the cascading rules as outlined by the Cascading Stylesheet
 * Standard.
 *
 * @author Thomas Morgner
 */
public class DefaultStyleResolver extends AbstractStyleResolver {
  private boolean strictStyleMode;
  private StyleRuleMatcher styleRuleMatcher;
  private StyleKey[] inheritedKeys;

  public DefaultStyleResolver() {
  }

  public void initialize( final DocumentContext layoutProcess ) {
    super.initialize( layoutProcess );
    this.styleRuleMatcher = new SimpleStyleRuleMatcher();
    this.styleRuleMatcher.initialize( layoutProcess );
    //    this.strictStyleMode = Boolean.TRUE.equals
    //            (documentContext.getMetaAttribute(DocumentContext.STRICT_STYLE_MODE));

    loadInitialStyle( layoutProcess );
  }

  // This one is expensive too: 6%
  protected void resolveOutOfContext( final LayoutElement element ) {
    // as this styleresolver is not statefull, we can safely call the resolve
    // style method. A statefull resolver would have to find other means.
    resolveStyle( element );
  }

  /**
   * Performs tests, whether there is a pseudo-element definition for the given element. The element itself can be a
   * pseudo-element as well.
   *
   * @param element
   * @param pseudo
   * @return
   */
  public boolean isPseudoElementStyleResolvable( final LayoutElement element,
                                                 final String pseudo ) {
    return styleRuleMatcher.isMatchingPseudoElement( element, pseudo );
  }

  /**
   * Resolves the style. This is guaranteed to be called in the order of the document elements traversing the document
   * tree using the 'deepest-node-first' strategy. (8% just for the first class calls (not counting the calls comming
   * from resolveAnonymous (which is another 6%))
   *
   * @param element the elemen that should be resolved.
   */
  public void resolveStyle( final LayoutElement element ) {
    // this is a three stage process
    final StyleKey[] keys = getKeys();

    //    Log.debug ("Resolving style for " +
    //            layoutContext.getTagName() + ":" +
    //            layoutContext.getPseudoElement());

    final LayoutElement parent = element.getParentLayoutElement();
    final LayoutStyle initialStyle = getInitialStyle();
    final LayoutStyle style = element.getLayoutStyle();

    // Stage 0: Initialize with the built-in defaults
    // The copy will return false if it couldn't do the copy automatically
    if ( style.copyFrom( initialStyle ) == false ) {
      // manually copy all styles from the initial style-set..
      for ( int i = 0; i < keys.length; i++ ) {
        final StyleKey key = keys[ i ];
        style.setValue( key, initialStyle.getValue( key ) );
      }
    }

    // Stage 1a: Add the parent styles (but only the one marked as inheritable).

    // If our element has a parent, get the parent's style information
    // so we can "inherit" the styles that support that kind of thing
    if ( parent != null ) {
      final LayoutStyle parentStyle;
      parentStyle = parent.getLayoutStyle();
      final StyleKey[] inheritedKeys = getInheritedKeys();
      for ( int i = 0; i < inheritedKeys.length; i++ ) {
        final StyleKey key = inheritedKeys[ i ];
        style.setValue( key, parentStyle.getValue( key ) );
      }
    }

    // At this point, the parentStyle contains the "foundation" from which
    // the current element's style information will come....

    // Stage 1b: Find all matching stylesheet styles for the given element.
    performSelectionStep( element, style );

    // Stage 1c: Add the contents of the style attribute, if there is one ..
    // the libLayout style is always added: This is a computed style and the hook
    // for a element neutral user defined tweaking ..

    final Object libLayoutStyleValue = element.getAttribute( Namespaces.LIBLAYOUT_NAMESPACE, "style" );
    // You cannot override element specific styles with that. So an HTML-style
    // attribute has more value than a LibLayout-style attribute.
    addStyleFromAttribute( element, libLayoutStyleValue );

    if ( strictStyleMode ) {
      performStrictStyleAttr( element );
    } else {
      performCompleteStyleAttr( element );
    }

    // Stage 2: Compute the 'specified' set of values.
    // Find all explicitly inherited styles and add them from the parent.
    final CSSInheritValue inheritInstance = CSSInheritValue.getInstance();
    if ( parent == null ) {
      for ( int i = 0; i < keys.length; i++ ) {
        final StyleKey key = keys[ i ];
        final Object value = style.getValue( key );
        if ( inheritInstance.equals( value ) ) {
          style.setValue( key, initialStyle.getValue( key ) );
        }
      }
    } else {
      final LayoutStyle parentStyle = parent.getLayoutStyle();
      for ( int i = 0; i < keys.length; i++ ) {
        final StyleKey key = keys[ i ];
        final Object value = style.getValue( key );
        if ( inheritInstance.equals( value ) ) {
          final CSSValue parentValue = parentStyle.getValue( key );
          if ( parentValue == null ) {
            style.setValue( key, initialStyle.getValue( key ) );
          } else {
            style.setValue( key, parentValue );
          }
        }
      }
    }

  }

  private StyleKey[] getInheritedKeys() {
    if ( inheritedKeys == null ) {
      final StyleKey[] keys = getKeys();
      final ArrayList inheritedKeysList = new ArrayList();
      for ( int i = 0; i < keys.length; i++ ) {
        final StyleKey key = keys[ i ];
        if ( key.isInherited() ) {
          inheritedKeysList.add( key );
        }
      }
      inheritedKeys = (StyleKey[])
        inheritedKeysList.toArray( new StyleKey[ inheritedKeysList.size() ] );
    }
    return inheritedKeys;
  }

  /**
   * Check, whether there is a known style attribute for the element's namespace and if so, grab its value. This method
   * uses strict conformance to the XML rules and thus it does not evaluate foreign styles.
   * <p/>
   *
   * @param node
   */
  private void performStrictStyleAttr( final LayoutElement node ) {
    final String namespace = node.getNamespace();
    if ( namespace == null ) {
      return;
    }

    final NamespaceCollection namespaces = getNamespaces();
    final NamespaceDefinition ndef = namespaces.getDefinition( namespace );
    if ( ndef == null ) {
      return;
    }

    //final AttributeMap attributes = layoutContext.getAttributes();
    final String[] styleAttrs = ndef.getStyleAttribute
      ( node.getTagName() );
    for ( int i = 0; i < styleAttrs.length; i++ ) {
      final String attr = styleAttrs[ i ];
      final Object styleValue = node.getAttribute( namespace, attr );
      addStyleFromAttribute( node, styleValue );
    }
  }

  /**
   * Check, whether there are known style attributes and if so, import them. This method uses a relaxed syntax and
   * imports all known style attributes ignoring the element's defined namespace. This allows to add styles to elements
   * which would not support styles otherwise, but may have .. chaotic .. side effects.
   * <p/>
   *
   * @param node
   */
  private void performCompleteStyleAttr( final LayoutElement node ) {
    final NamespaceCollection namespaces = getNamespaces();
    final String[] namespaceNames = namespaces.getNamespaces();

    for ( int i = 0; i < namespaceNames.length; i++ ) {
      final String namespace = namespaceNames[ i ];
      final NamespaceDefinition ndef = namespaces.getDefinition( namespace );
      if ( ndef == null ) {
        continue;
      }

      final String[] styleAttrs = ndef.getStyleAttribute( node.getTagName() );
      for ( int x = 0; x < styleAttrs.length; x++ ) {
        final String attr = styleAttrs[ x ];
        final Object styleValue = node.getAttribute( namespace, attr );
        addStyleFromAttribute( node, styleValue );
      }
    }
  }

  private void addStyleFromAttribute( final LayoutElement node,
                                      final Object styleValue ) {
    if ( styleValue == null ) {
      return;
    }

    if ( styleValue instanceof String ) {
      final String styleText = (String) styleValue;
      final ResourceManager resourceManager = getDocumentContext().getResourceManager();
      final CSSDeclarationRule rule = StyleSheetParserUtil.getInstance().parseStyleRule
        ( null, styleText, null, null, resourceManager, StyleKeyRegistry.getRegistry() );
      if ( rule != null ) {
        copyStyleInformation( node.getLayoutStyle(), rule, node );
      }
    } else if ( styleValue instanceof CSSDeclarationRule ) {
      final CSSDeclarationRule rule = (CSSDeclarationRule) styleValue;
      copyStyleInformation( node.getLayoutStyle(), rule, node );
    }
  }

  /**
   * Todo: Make sure that the 'activeStyles' are sorted and then apply them with the lowest style first. All Matching
   * styles have to be added.
   */
  private void performSelectionStep( final LayoutElement element,
                                     final LayoutStyle parentStyle ) {
    final CSSStyleRule[] activeStyleRules = styleRuleMatcher.getMatchingRules( element );

    // sort ...
    Arrays.sort( activeStyleRules, new CSSStyleRuleComparator() );
    SelectorWeight oldSelectorWeight = null;
    for ( int i = 0; i < activeStyleRules.length; i++ ) {
      final CSSStyleRule activeStyleRule = activeStyleRules[ i ];
      final CSSSelector selector = activeStyleRule.getSelector();
      final SelectorWeight activeWeight = selector.getWeight();

      if ( oldSelectorWeight != null ) {
        if ( oldSelectorWeight.compareTo( activeWeight ) > 0 ) {
          oldSelectorWeight = activeWeight;
          continue;
        }
      }

      oldSelectorWeight = activeWeight;
      copyStyleInformation( parentStyle, activeStyleRule, element );
    }
  }

  public StyleResolver deriveInstance() {
    return this;
  }

  public LayoutStyle resolvePageStyle( final CSSValue pageName,
                                       final PseudoPage[] pseudoPages,
                                       final PageAreaType pageArea ) {
    final DefaultLayoutStyle style = new DefaultLayoutStyle();

    final CSSPageRule[] pageRule =
      styleRuleMatcher.getPageRule( pageName, pseudoPages );
    for ( int i = 0; i < pageRule.length; i++ ) {
      final CSSPageRule cssPageRule = pageRule[ i ];
      copyStyleInformation( style, cssPageRule, null );

      final int rc = cssPageRule.getRuleCount();
      for ( int r = 0; r < rc; r++ ) {
        final CSSPageAreaRule rule = cssPageRule.getRule( r );
        if ( rule.getPageArea().equals( pageArea ) ) {
          copyStyleInformation( style, rule, null );
        }
      }
    }
    return style;
  }
}
