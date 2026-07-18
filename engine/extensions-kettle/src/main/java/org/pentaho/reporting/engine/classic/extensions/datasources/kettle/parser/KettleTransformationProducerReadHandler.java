/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.engine.classic.extensions.datasources.kettle.parser;

import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaArgument;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaParameter;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransformationProducer;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

public interface KettleTransformationProducerReadHandler extends XmlReadHandler {
  public String getName();

  public String getStepName();

  public String getUsername();

  public String getPassword();

  public String getRepositoryName();

  public FormulaArgument[] getDefinedArgumentNames();

  public FormulaParameter[] getDefinedVariableNames();

  public KettleTransformationProducer getTransformationProducer() throws SAXException;
}
