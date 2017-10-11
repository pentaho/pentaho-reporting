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
 * Copyright (c) 2001 - 2017 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.metadata;

import java.awt.Image;
import java.awt.Toolkit;
import java.beans.BeanInfo;
import java.io.Serializable;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;

import org.pentaho.reporting.engine.classic.core.metadata.builder.MetaDataBuilder;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

public abstract class AbstractMetaData implements Serializable, MetaData {
  private transient Image[] icons;
  private transient Locale lastLocale;
  private transient Messages lastMessages;

  private String name;
  private String bundleLocation;
  private String keyPrefix;
  private boolean expert;
  private boolean preferred;
  private boolean hidden;
  private boolean deprecated;
  private String displayNameKey;
  private String descriptionKey;
  private String groupingKey;
  private String groupingOrdinalKey;
  private String itemOrdinalKey;
  private String keyPrefixAndName;
  private String deprecationKey;
  private MaturityLevel maturityLevel;
  private int compatibilityLevel;

  protected AbstractMetaData( final MetaData metaData ) {
    ArgumentNullException.validate( "metaData", metaData );

    this.name = metaData.getName();
    this.keyPrefix = metaData.getKeyPrefix();
    this.bundleLocation = metaData.getBundleLocation();
    this.expert = metaData.isExpert();
    this.preferred = metaData.isPreferred();
    this.hidden = metaData.isHidden();
    this.deprecated = metaData.isDeprecated();
    this.maturityLevel = metaData.getFeatureMaturityLevel();
    this.compatibilityLevel = metaData.getCompatibilityLevel();

    computeBundleProperties();
  }

  protected AbstractMetaData( final MetaDataBuilder builder ) {
    this( builder.getName(), builder.getBundleLocation(), builder.getKeyPrefix(), builder.isExpert(), builder
        .isPreferred(), builder.isHidden(), builder.isDeprecated(), builder.getMaturityLevel(), builder
        .getCompatibilityLevel() );
  }

  protected AbstractMetaData( final String name, final String bundleLocation, final String keyPrefix,
      final boolean expert, final boolean preferred, final boolean hidden, final boolean deprecated,
      final MaturityLevel maturityLevel, final int compatibilityLevel ) {
    ArgumentNullException.validate( "name", name );
    ArgumentNullException.validate( "bundleLocation", bundleLocation );
    ArgumentNullException.validate( "keyPrefix", keyPrefix );

    this.compatibilityLevel = compatibilityLevel;
    this.maturityLevel = maturityLevel;
    this.name = name;
    this.bundleLocation = bundleLocation;
    this.keyPrefix = keyPrefix;
    this.expert = expert;
    this.preferred = preferred;
    this.hidden = hidden;
    this.deprecated = deprecated;

    computeBundleProperties();
  }

  private void computeBundleProperties() {
    keyPrefixAndName = computePrefix( this.keyPrefix, this.name );
    if ( StringUtils.isEmpty( keyPrefixAndName, true ) ) {
      displayNameKey = "display-name";
      descriptionKey = "description";
      itemOrdinalKey = "ordinal";
      groupingKey = "grouping";
      groupingOrdinalKey = "grouping.ordinal";
      deprecationKey = "deprecated";
    } else {
      displayNameKey = keyPrefixAndName + ".display-name";
      descriptionKey = keyPrefixAndName + ".description";
      itemOrdinalKey = keyPrefixAndName + ".ordinal";
      groupingKey = keyPrefixAndName + ".grouping";
      groupingOrdinalKey = keyPrefixAndName + ".grouping.ordinal";
      deprecationKey = keyPrefixAndName + ".deprecated";
    }
  }

  public int getCompatibilityLevel() {
    return compatibilityLevel;
  }

  protected String computePrefix( final String keyPrefix, final String name ) {
    return keyPrefix + name;
  }

  public boolean isExperimental() {
    return maturityLevel.isExperimental();
  }

  public boolean isDeprecated() {
    return deprecated;
  }

  public String getName() {
    return name;
  }

  public Messages getBundle( final Locale locale ) {
    if ( locale == null ) {
      throw new NullPointerException();
    }

    if ( lastMessages == null || ObjectUtilities.equal( locale, lastLocale ) == false ) {
      lastMessages = new Messages( locale, bundleLocation, ObjectUtilities.getClassLoader( AbstractMetaData.class ) );
      lastLocale = locale;
    }
    return lastMessages;
  }

  public String getMetaAttribute( final String attributeName, final Locale locale ) {
    try {
      final String key;
      if ( StringUtils.isEmpty( keyPrefixAndName ) ) {
        key = attributeName;
      } else {
        key = keyPrefixAndName + '.' + attributeName;
      }
      return getBundle( locale ).strictString( key );
    } catch ( final MissingResourceException mre ) {
      return null;
    }
  }

  public String getDisplayName( final Locale locale ) {
    return getBundle( locale ).getString( displayNameKey );
  }

  public String getDescription( final Locale locale ) {
    return getBundle( locale ).getOptionalString( descriptionKey );
  }

  public String getGrouping( final Locale locale ) {
    final String grouping = getBundle( locale ).getOptionalString( groupingKey );
    if ( grouping == null ) {
      return "";
    }
    return grouping;
  }

  public int getGroupingOrdinal( final Locale locale ) {
    final String strOrd = getBundle( locale ).getOptionalString( groupingOrdinalKey );
    if ( strOrd == null ) {
      return 0;
    }
    return ParserUtil.parseInt( strOrd, Integer.MAX_VALUE );
  }

  public int getItemOrdinal( final Locale locale ) {
    final String strOrd = getBundle( locale ).getOptionalString( itemOrdinalKey );
    if ( strOrd == null ) {
      return 0;
    }
    return ParserUtil.parseInt( strOrd, Integer.MAX_VALUE );
  }

  public String getDeprecationMessage( final Locale locale ) {
    return getBundle( locale ).getOptionalString( deprecationKey );
  }

  public boolean isExpert() {
    return expert;
  }

  public boolean isPreferred() {
    return preferred;
  }

  public boolean isHidden() {
    return hidden;
  }

  public String getKeyPrefix() {
    return keyPrefix;
  }

  public String getBundleLocation() {
    return bundleLocation;
  }

  public synchronized Image getIcon( final Locale locale, final int iconKind ) {
    if ( iconKind < 1 || iconKind > 4 ) {
      throw new IllegalArgumentException();
    }

    if ( icons != null ) {
      final Image cachedIcon = icons[iconKind - 1];
      if ( cachedIcon != null ) {
        return cachedIcon;
      }
    }

    final String iconKey;
    if ( StringUtils.isEmpty( keyPrefixAndName ) == false ) {
      switch ( iconKind ) {
        case BeanInfo.ICON_COLOR_16x16:
          iconKey = ( keyPrefixAndName + ".icon-color-16" );
          break;
        case BeanInfo.ICON_COLOR_32x32:
          iconKey = ( keyPrefixAndName + ".icon-color-32" );
          break;
        case BeanInfo.ICON_MONO_16x16:
          iconKey = ( keyPrefixAndName + ".icon-mono-16" );
          break;
        case BeanInfo.ICON_MONO_32x32:
          iconKey = ( keyPrefixAndName + ".icon-mono-32" );
          break;
        default:
          throw new IllegalArgumentException();
      }
    } else {
      switch ( iconKind ) {
        case BeanInfo.ICON_COLOR_16x16:
          iconKey = ( "icon-color-16" );
          break;
        case BeanInfo.ICON_COLOR_32x32:
          iconKey = ( "icon-color-32" );
          break;
        case BeanInfo.ICON_MONO_16x16:
          iconKey = ( "icon-mono-16" );
          break;
        case BeanInfo.ICON_MONO_32x32:
          iconKey = ( "icon-mono-32" );
          break;
        default:
          throw new IllegalArgumentException();
      }
    }

    URL url = null;

    final String iconName = getOptionalString( locale, iconKey );
    if ( iconName != null ) {
      url = ObjectUtilities.getResource( iconName, DefaultElementMetaData.class );
    }
    if ( url == null ) {
      final String fallbackIcon = getOptionalString( locale, getKeyPrefix() + getName() + ".icon" );
      if ( fallbackIcon != null ) {
        url = ObjectUtilities.getResource( fallbackIcon, DefaultElementMetaData.class );
      }
      if ( url == null ) {
        return null;
      }
    }
    if ( icons == null ) {
      icons = new Image[4];
    }
    final Image retval = Toolkit.getDefaultToolkit().createImage( url );
    icons[iconKind - 1] = retval;
    return retval;
  }

  private String getOptionalString( final Locale locale, final String key ) {
    try {
      return getBundle( locale ).getOptionalString( key );
    } catch ( final MissingResourceException e ) {
      return null;
    }
  }

  public MaturityLevel getFeatureMaturityLevel() {
    return maturityLevel;
  }
}
