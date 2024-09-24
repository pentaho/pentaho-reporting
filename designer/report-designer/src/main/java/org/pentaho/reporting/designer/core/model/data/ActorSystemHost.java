/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.designer.core.model.data;

import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.TypedActor;
import org.apache.pekko.actor.TypedProps;
import org.apache.pekko.util.Timeout;

import java.util.concurrent.TimeUnit;

public class ActorSystemHost {
  public static final ActorSystemHost INSTANCE = new ActorSystemHost();

  private ActorSystem system;

  protected ActorSystemHost() {
    system = ActorSystem.create( "Pentaho-Report-Designer" );
  }

  public ActorSystem getSystem() {
    return system;
  }

  public <IFace, Impl extends IFace> IFace createActor( final Class<IFace> iface, final Class<Impl> impl ) {
    final TypedProps<Impl> queryMetaDataActorTypedProps =
      new TypedProps<Impl>( iface, impl ).withTimeout( Timeout.apply( 30, TimeUnit.MINUTES ) );
    return TypedActor.get( system ).typedActorOf( queryMetaDataActorTypedProps );
  }

  public void stopNow( final Object actor ) {
    TypedActor.get( system ).stop( actor );
  }

  public void shutdown( final Object actor ) {
    TypedActor.get( system ).poisonPill( actor );
  }
}
