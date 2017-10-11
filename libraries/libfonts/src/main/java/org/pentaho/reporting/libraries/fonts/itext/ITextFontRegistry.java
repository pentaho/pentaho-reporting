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

package org.pentaho.reporting.libraries.fonts.itext;

import org.pentaho.reporting.libraries.fonts.LibFontBoot;
import org.pentaho.reporting.libraries.fonts.afm.AfmFontRegistry;
import org.pentaho.reporting.libraries.fonts.cache.FontCache;
import org.pentaho.reporting.libraries.fonts.merge.CompoundFontRegistry;
import org.pentaho.reporting.libraries.fonts.pfm.PfmFontRegistry;
import org.pentaho.reporting.libraries.fonts.registry.FontMetricsFactory;
import org.pentaho.reporting.libraries.fonts.truetype.TrueTypeFontRegistry;

/**
 * This class provides access to the iText font system. The IText registry does not actually use iText to register the
 * fonts (as iText does not provide all information we need for that task).
 *
 * @author Thomas Morgner
 */
public class ITextFontRegistry extends CompoundFontRegistry {
  private static FontCache secondLevelCache;

  protected static synchronized FontCache internalGetSecondLevelCache() {
    if ( secondLevelCache == null ) {
      secondLevelCache = LibFontBoot.getInstance().createDefaultCache();
    }
    return secondLevelCache;
  }

  public ITextFontRegistry() {
    addRegistry( new ITextBuiltInFontRegistry() );
    addRegistry( new TrueTypeFontRegistry() );
    addRegistry( new AfmFontRegistry() );
    addRegistry( new PfmFontRegistry() );
  }

  public FontCache getSecondLevelCache() {
    return internalGetSecondLevelCache();
  }

  public FontMetricsFactory createMetricsFactory() {
    return new ITextFontMetricsFactory( this );
  }
}
