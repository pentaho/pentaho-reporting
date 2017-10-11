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

package org.pentaho.reporting.ui.datasources.pmd.util;

import org.pentaho.metadata.repository.IMetadataDomainRepository;
import org.pentaho.metadata.repository.InMemoryMetadataDomainRepository;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeUtil;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.PmdConnectionProvider;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class LoadRepositoryRunnable implements Runnable {
  private IMetadataDomainRepository repository;
  private DesignTimeContext context;
  private String domainId;
  private String fileName;

  public LoadRepositoryRunnable( final DesignTimeContext context,
                                 final String domainId,
                                 final String fileName ) {
    this.context = context;
    this.domainId = domainId;
    this.fileName = fileName;
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread causes
   * the object's <code>run</code> method to be called in that separately executing thread.
   * <p/>
   * The general contract of the method <code>run</code> is that it may take any action whatsoever.
   *
   * @see Thread#run()
   */
  public void run() {
    repository = buildDomainRepository();
  }

  private IMetadataDomainRepository buildDomainRepository() {
    try {
      final AbstractReportDefinition report = context.getReport();
      final MasterReport masterReport = DesignTimeUtil.getMasterReport( report );
      final ResourceKey contentBase;
      if ( masterReport == null ) {
        contentBase = null;
      } else {
        contentBase = masterReport.getContentBase();
      }

      final ResourceManager resourceManager = DesignTimeUtil.getResourceManager( report );
      return new PmdConnectionProvider()
        .getMetadataDomainRepository( domainId, resourceManager, contentBase, fileName );
    } catch ( Exception e ) {
      context.error( e );
    }
    return new InMemoryMetadataDomainRepository();
  }

  public IMetadataDomainRepository getRepository() {
    return repository;
  }
}
