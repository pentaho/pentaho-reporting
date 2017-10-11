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

package org.pentaho.reporting.libraries.fonts;

import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;
import org.pentaho.reporting.libraries.fonts.cache.FontCache;
import org.pentaho.reporting.libraries.fonts.cache.LeastFrequentlyUsedCache;


/**
 * Creation-Date: 06.11.2005, 18:25:11
 *
 * @author Thomas Morgner
 */
public class LibFontBoot extends AbstractBoot {
  private static LibFontBoot instance;

  public static synchronized LibFontBoot getInstance() {
    if ( instance == null ) {
      instance = new LibFontBoot();
    }
    return instance;
  }

  private LibFontBoot() {
  }

  protected Configuration loadConfiguration() {
    return createDefaultHierarchicalConfiguration
      ( "/org/pentaho/reporting/libraries/fonts/libfont.properties",
        "/libfont.properties", true, LibFontBoot.class );

  }

  protected void performBoot() {
    //    Log.debug ("LibFonts ..");
  }

  protected ProjectInformation getProjectInfo() {
    return LibFontInfo.getInstance();
  }

  public FontCache createDefaultCache() {
    // we should make this configurable in some way ..
    return new LeastFrequentlyUsedCache( 30 );
  }
}
