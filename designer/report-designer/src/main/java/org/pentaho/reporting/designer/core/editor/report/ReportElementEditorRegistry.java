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

package org.pentaho.reporting.designer.core.editor.report;

import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ReportElementEditorRegistry {
  private static final String PREFIX = "org.pentaho.reporting.designer.core.report-element-editor.";
  private static final String DEFAULTEDITOR = "org.pentaho.reporting.designer.core.report-element-editor";

  private static ReportElementEditorRegistry instance;
  private HashMap<String, ReportElementEditor> factories;

  public static synchronized ReportElementEditorRegistry getInstance() {
    if ( instance == null ) {
      instance = new ReportElementEditorRegistry();
      instance.register();
    }
    return instance;
  }

  public ReportElementEditorRegistry() {
    factories = new HashMap();
  }

  private void register() {
    final Configuration configuration = ReportDesignerBoot.getInstance().getGlobalConfig();
    final Iterator keys = configuration.findPropertyKeys( PREFIX );
    while ( keys.hasNext() ) {
      final String key = (String) keys.next();
      final String elementType = key.substring( PREFIX.length() );
      final String className = configuration.getConfigProperty( key );
      final ReportElementEditor plugin = (ReportElementEditor)
        ObjectUtilities.loadAndInstantiate( className, ReportElementEditorRegistry.class, ReportElementEditor.class );
      if ( plugin != null ) {
        addPlugin( elementType, plugin );
      }
    }

    final String className = configuration.getConfigProperty( DEFAULTEDITOR );
    final ReportElementEditor plugin = (ReportElementEditor)
      ObjectUtilities.loadAndInstantiate( className, ReportElementEditorRegistry.class, ReportElementEditor.class );
    if ( plugin != null ) {
      addPlugin( null, plugin );
    }
  }

  public void addPlugin( final String elementType, final ReportElementEditor plugin ) {
    if ( plugin == null ) {
      throw new NullPointerException();
    }
    factories.put( elementType, plugin );
  }

  public String[] getPluginKeys() {
    return factories.keySet().toArray( new String[ factories.size() ] );
  }

  public ReportElementEditor getPlugin( final String elementType ) {
    return factories.get( elementType );
  }
}
