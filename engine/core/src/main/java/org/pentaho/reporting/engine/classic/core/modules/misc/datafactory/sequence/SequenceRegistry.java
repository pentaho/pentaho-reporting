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
