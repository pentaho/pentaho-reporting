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

package org.pentaho.reporting.engine.classic.core.style.css;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ResolverStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.css.namespaces.NamespaceCollection;
import org.pentaho.reporting.engine.classic.core.style.css.selector.SelectorWeight;
import org.pentaho.reporting.engine.classic.core.style.resolver.SimpleStyleResolver;
import org.pentaho.reporting.engine.classic.core.style.resolver.StyleResolver;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A cascading style resolver. This resolver follows the cascading rules as outlined by the Cascading Stylesheet
 * Standard.
 *
 * @author Thomas Morgner
 */
public class CSSStyleResolver implements StyleResolver, Cloneable {
  private static final Log logger = LogFactory.getLog( CSSStyleResolver.class );

  private StyleRuleMatcher styleRuleMatcher;
  private DocumentContext documentContext;
  private NamespaceCollection namespaces;
  private SimpleStyleResolver simpleStyleResolver;

  public CSSStyleResolver() {
    this( false );
  }

  public CSSStyleResolver( final boolean designTime ) {
    this.simpleStyleResolver = new SimpleStyleResolver( designTime );
  }

  public static StyleResolver createDesignTimeResolver( final ReportDefinition report,
      final ResourceManager resourceManager, final ResourceKey contentBase, final boolean designTime ) {
    final ElementStyleDefinition styleDefinition = createStyleDefinition( report, resourceManager, contentBase );

    if ( styleDefinition.getRuleCount() == 0 && styleDefinition.getStyleSheetCount() == 0 ) {
      return new SimpleStyleResolver( designTime );
    } else {
      final CSSStyleResolver resolver = new CSSStyleResolver( designTime );
      final NamespaceCollection namespaceCollection = StyleSheetParserUtil.getInstance().getNamespaceCollection();
      final DefaultDocumentContext documentContext =
          new DefaultDocumentContext( namespaceCollection, resourceManager, contentBase, null, styleDefinition );
      resolver.initialize( documentContext );
      return resolver;
    }
  }

  private static ElementStyleDefinition createStyleDefinition( final ReportDefinition reportDefinition,
      final ResourceManager resourceManager, final ResourceKey contentBase ) {
    final ElementStyleDefinition styleDefinition;
    final Object maybeStyleSheet =
        reportDefinition.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.STYLE_SHEET );
    if ( maybeStyleSheet instanceof ElementStyleDefinition ) {
      final ElementStyleDefinition sd = (ElementStyleDefinition) maybeStyleSheet;
      styleDefinition = sd.clone();
    } else {
      styleDefinition = new ElementStyleDefinition();
    }

    final Object styleSheetRefs =
        reportDefinition.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.STYLE_SHEET_REFERENCE );
    final ArrayList<Object> styleRefs = new ArrayList<Object>();
    if ( styleSheetRefs instanceof Object[] ) {
      final Object[] styleArray = (Object[]) styleSheetRefs;
      for ( int i = 0; i < styleArray.length; i++ ) {
        styleRefs.add( styleArray[i] );
      }
    } else if ( styleSheetRefs instanceof Collection ) {
      final Collection c = (Collection) styleSheetRefs;
      styleRefs.addAll( c );
    } else if ( styleSheetRefs != null ) {
      styleRefs.add( styleSheetRefs );
    }

    for ( int i = 0; i < styleRefs.size(); i++ ) {
      final Object o = styleRefs.get( i );
      try {
        final ResourceKey key = resourceManager.createOrDeriveKey( contentBase, o, null );
        final Resource resource = resourceManager.create( key, key, ElementStyleDefinition.class );
        final ElementStyleDefinition definition = (ElementStyleDefinition) resource.getResource();
        styleDefinition.addStyleSheet( definition );
      } catch ( ResourceKeyCreationException e ) {
        logger.debug( "Failed to load referenced style-sheet: " + o, e );
      } catch ( ResourceException e ) {
        logger.info( "Failed to load referenced style-sheet: " + o, e );
      }
    }
    return styleDefinition;
  }

  public void initialize( final DocumentContext layoutProcess ) {
    this.documentContext = layoutProcess;
    this.namespaces = documentContext.getNamespaces();

    this.styleRuleMatcher = new SimpleStyleRuleMatcher();
    this.styleRuleMatcher.initialize( layoutProcess );
  }

  protected DocumentContext getDocumentContext() {
    return documentContext;
  }

  protected NamespaceCollection getNamespaces() {
    return namespaces;
  }

  public void resolve( final ReportElement element, final ResolverStyleSheet resolverTarget ) {
    resolverTarget.clear();
    resolverTarget.setId( element.getStyle().getId() );

    // Stage 1a: Add the parent styles (but only the one marked as inheritable).

    // If our element has a parent, get the parent's style information
    // so we can "inherit" the styles that support that kind of thing
    simpleStyleResolver.resolveParent( element, resolverTarget );

    // At this point, the parentStyle contains the "foundation" from which
    // the current element's style information will come....

    // Stage 1b: Find all matching stylesheet styles for the given element.
    performSelectionStep( element, resolverTarget );

    // Stage 2: Compute the 'specified' set of values.
    // Find all explicitly inherited styles and add them from the parent.
    // does not apply, we have no ability to specify an explicit INHERIT value.
    resolverTarget.addAll( element.getStyle() );
    resolverTarget.addDefault( element.getDefaultStyleSheet() );
  }

  /*
   * Todo: Make sure that the 'activeStyles' are sorted and then apply them with the lowest style first. All Matching
   * styles have to be added.
   */
  private void performSelectionStep( final ReportElement element, final ElementStyleSheet target ) {
    final StyleRuleMatcher.MatcherResult[] activeStyleRules = styleRuleMatcher.getMatchingRules( element );

    final SelectorWeight[] weights = new SelectorWeight[target.getPropertyKeys().length];
    for ( int i = 0; i < activeStyleRules.length; i++ ) {
      final StyleRuleMatcher.MatcherResult activeStyleRule = activeStyleRules[i];
      final ElementStyleRule rule = activeStyleRule.getRule();
      final SelectorWeight weight = activeStyleRule.getWeight();

      final StyleKey[] definedPropertyNamesArray = rule.getDefinedPropertyNamesArray();
      for ( int j = 0; j < definedPropertyNamesArray.length; j++ ) {
        final StyleKey styleKey = definedPropertyNamesArray[j];
        if ( styleKey == null ) {
          continue;
        }

        final SelectorWeight selectorWeight = weights[j];
        if ( selectorWeight == null || ( selectorWeight.compareTo( weight ) > 0 ) ) {
          final Object styleProperty = rule.getStyleProperty( styleKey );
          if ( styleProperty != null ) {
            target.setStyleProperty( styleKey, styleProperty );
            weights[j] = weight;
          }
        }
      }
    }
  }

  public StyleResolver clone() {
    try {
      return (StyleResolver) super.clone();
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }

}
