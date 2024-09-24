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

package org.pentaho.reporting.designer.core.editor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.designtime.AttributeChange;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ClassicEngineFactoryParameters;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;

import java.io.IOException;
import java.util.Map;

/**
 * This listener is notified when a potential embedded resource is modified (added / updated / deleted).
 * <p/>
 * If the resource is determined to be an embedded resource, and that resource is not current embedded, then this will
 * convert the resource to an embedded resource and alter the information in the report.
 */
public class ResourceLoaderListener implements ReportModelListener {
  private static final Log logger = LogFactory.getLog( ResourceLoaderListener.class );
  private MasterReport masterReportElement;

  /**
   * Creates an embedded resource listener
   *
   * @param masterReportElement
   * @param report
   */
  public ResourceLoaderListener( final MasterReport masterReportElement, final AbstractReportDefinition report ) {
    this.masterReportElement = masterReportElement;
  }

  /**
   * Indicates that an event has occurred that this listener may be interested in
   *
   * @param event the event that triggered this listener
   */
  public void nodeChanged( final ReportModelEvent event ) {
    if ( event.getParameter() instanceof AttributeChange == false
      || event.getElement() instanceof ReportElement == false ) {
      return;
    }

    // This is an attribute change event ... see if it is one we are concerned about
    final AttributeChange attributeChange = (AttributeChange) event.getParameter();
    final ReportElement reportElement = (ReportElement) event.getElement();
    final AttributeMetaData attributeDescription =
      reportElement.getMetaData().getAttributeDescription( attributeChange.getNamespace(), attributeChange.getName() );
    if ( attributeDescription == null ||
      AttributeMetaData.VALUEROLE_RESOURCE.equals( attributeDescription.getValueRole() ) == false ) {
      return;
    }

    // See if we need to load the resource's value into the resource key
    final Object newValue = attributeChange.getNewValue();
    if ( newValue instanceof ResourceKey && shouldBeLoaded( (ResourceKey) newValue ) ) {
      try {
        // Embed the file and swap in the key which refers to the embedded resource
        final ResourceKey newKey = loadResourceIntoKey( (ResourceKey) newValue );
        if ( newKey != null ) {
          // Swap out the old key with the new key (the new key has the resource loaded)
          reportElement.setAttribute( attributeChange.getNamespace(), attributeChange.getName(), newKey );
        }
      } catch ( Exception e ) {
        reportElement.setAttribute( attributeChange.getNamespace(), attributeChange.getName(), null );
        UncaughtExceptionsModel.getInstance().addException( e );
      }
    }
  }

  /**
   * Creates a new ResourceKey with the contents of the resource loaded into the key as a byte [].
   *
   * @param key contains information about the resource to load
   * @return the new resource key that was created
   * @throws IOException                  indicates an error reading the source
   * @throws ResourceKeyCreationException indicates the file could not be loaded
   */
  private ResourceKey loadResourceIntoKey( final ResourceKey key )
    throws IOException, ResourceKeyCreationException, ResourceLoadingException {
    if ( logger.isDebugEnabled() ) {
      final Map factoryParameters = key.getFactoryParameters();
      final String mimeType = (String) factoryParameters.get( ClassicEngineFactoryParameters.MIME_TYPE );
      final String pattern = (String) factoryParameters.get( ClassicEngineFactoryParameters.PATTERN );
      final String original = (String) factoryParameters.get( ClassicEngineFactoryParameters.ORIGINAL_VALUE );
      logger.debug( "Loading resource into key: original=[" +  // NON-NLS
        original + "] mimeType=[" + mimeType + "] pattern=[" + pattern + "]" ); // NON-NLS
    }

    return ResourceKeyUtils
      .embedResourceInKey( masterReportElement.getResourceManager(), key, key.getFactoryParameters() );
  }

  /**
   * Determines if the specified resource key contains information about a resource which should be embedded
   *
   * @param key the key to test
   * @return <code>true</code> if the key contains information about embedding, <code>false</code>otherwise</code>
   */
  private static boolean shouldBeLoaded( final ResourceKey key ) {
    if ( "true".equals( key.getFactoryParameters().get( ClassicEngineFactoryParameters.EMBED ) ) ) {
      if ( false == key.getIdentifier() instanceof byte[] ) {
        return true;
      }
    }
    return false;
  }

}
