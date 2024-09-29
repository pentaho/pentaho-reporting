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

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;

public class SequenceRegistry {
  private static final String PREFIX = "org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.";
  private ArrayList<SequenceDescription> sequences;

  public SequenceRegistry() {
    sequences = new ArrayList<SequenceDescription>();
    final Configuration config = ClassicEngineBoot.getInstance().getGlobalConfig();
    final Iterator<String> keys = config.findPropertyKeys( PREFIX );
    while ( keys.hasNext() ) {
      final String key = keys.next();
      final String clazz = config.getConfigProperty( key );
      final SequenceDescription sequenceDescription =
          ObjectUtilities.loadAndInstantiate( clazz, SequenceRegistry.class, SequenceDescription.class );
      if ( sequenceDescription != null ) {
        sequences.add( sequenceDescription );
      }
    }
  }

  public String[] getSequenceGroups( final Locale locale ) {
    final TreeSet<String> sequenceGroups = new TreeSet<String>();
    for ( final SequenceDescription sd : sequences ) {
      sequenceGroups.add( sd.getSequenceGroup( locale ) );
    }
    return sequenceGroups.toArray( new String[sequenceGroups.size()] );
  }

  public SequenceDescription[] getSequences() {
    return sequences.toArray( new SequenceDescription[sequences.size()] );
  }

  public SequenceDescription[] getSequencesForGroup( final String groupName, final Locale locale ) {
    final ArrayList<SequenceDescription> sequenceGroups = new ArrayList<SequenceDescription>();
    for ( final SequenceDescription sd : sequences ) {
      if ( ObjectUtilities.equal( groupName, sd.getSequenceGroup( locale ) ) ) {
        sequenceGroups.add( sd );
      }
    }
    return sequenceGroups.toArray( new SequenceDescription[sequenceGroups.size()] );
  }

}
