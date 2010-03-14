package com.eucalyptus.ws.client;

import java.net.URI;

import org.apache.log4j.Logger;
import org.mule.RequestContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import com.eucalyptus.bootstrap.Component;
import com.eucalyptus.util.EucalyptusCloudException;
import com.eucalyptus.util.NetworkUtil;

import edu.ucsb.eucalyptus.msgs.EucalyptusMessage;

public class LocalDispatcher extends ServiceDispatcher {
  private static Logger LOG = Logger.getLogger( LocalDispatcher.class );
  private MuleClient    muleClient;

  public LocalDispatcher( Component component, String name, URI address ) {
    super( component, name, address, true );
  }

  @Override
  public void dispatch( EucalyptusMessage msg ) {
    MuleEvent context = RequestContext.getEvent( );
    try {
      this.getMuleClient( ).dispatch( this.getComponent( ).getLocalAddress( ), msg, null );
    } catch ( Exception e ) {
      LOG.error( e );
    } finally {
      RequestContext.setEvent( context );
    }
  }

  @Override
  public EucalyptusMessage send( EucalyptusMessage msg ) throws EucalyptusCloudException {
    MuleEvent context = RequestContext.getEvent( );
    try {
      MuleMessage reply = this.getMuleClient( ).send( this.getComponent( ).getLocalAddress( ), msg, null );
      if ( reply.getExceptionPayload( ) != null ) {
        LOG.debug( e, e );
        throw new EucalyptusCloudException( reply.getExceptionPayload( ).getRootException( ).getMessage( ), reply.getExceptionPayload( ).getRootException( ) );
      } else {
        return ( EucalyptusMessage ) reply.getPayload( );
      }
    } catch ( Exception e ) {
      LOG.error( e, e );
      throw new EucalyptusCloudException( e );
    } finally {
      RequestContext.setEvent( context );
    }
  }

}
