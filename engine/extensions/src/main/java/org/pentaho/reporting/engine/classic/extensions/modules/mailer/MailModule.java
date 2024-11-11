/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.extensions.modules.mailer;

import org.pentaho.reporting.engine.classic.extensions.modules.mailer.parser.MailDefinitionXmlFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.modules.mailer.parser.MailDefinitionXmlResourceFactory;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

public class MailModule extends AbstractModule {
  public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/engine/classic/mail-definition/1.0";

  public MailModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    MailDefinitionXmlResourceFactory.register( MailDefinitionXmlFactoryModule.class );
  }
}
