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


package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence;

public class NumberSequenceDescription extends AbstractSequenceDescription {
  public NumberSequenceDescription() {
    super( "org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.NumberSequenceBundle",
        NumberSequence.class );
  }
}
