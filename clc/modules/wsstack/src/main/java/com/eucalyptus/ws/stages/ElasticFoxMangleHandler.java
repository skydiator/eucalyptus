/*******************************************************************************
*Copyright (c) 2009  Eucalyptus Systems, Inc.
* 
*  This program is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, only version 3 of the License.
* 
* 
*  This file is distributed in the hope that it will be useful, but WITHOUT
*  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
*  FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
*  for more details.
* 
*  You should have received a copy of the GNU General Public License along
*  with this program.  If not, see <http://www.gnu.org/licenses/>.
* 
*  Please contact Eucalyptus Systems, Inc., 130 Castilian
*  Dr., Goleta, CA 93101 USA or visit <http://www.eucalyptus.com/licenses/>
*  if you need additional information or have any questions.
* 
*  This file may incorporate work covered under the following copyright and
*  permission notice:
* 
*    SOFTWARE, AND IF ANY SUCH MATERIAL IS DISCOVERED THE PARTY DISCOVERING
*    IT MAY INFORM DR. RICH WOLSKI AT THE UNIVERSITY OF CALIFORNIA, SANTA
*    BARBARA WHO WILL THEN ASCERTAIN THE MOST APPROPRIATE REMEDY, WHICH IN
*    THE REGENTS’ DISCRETION MAY INCLUDE, WITHOUT LIMITATION, REPLACEMENT
*    OF THE CODE SO IDENTIFIED, LICENSING OF THE CODE SO IDENTIFIED, OR
*    WITHDRAWAL OF THE CODE CAPABILITY TO THE EXTENT NEEDED TO COMPLY WITH
*    ANY SUCH LICENSES OR RIGHTS.
 ******************************************************************************/
/*
 * Author: chris grzegorczyk <grze@eucalyptus.com>
 */
package com.eucalyptus.ws.stages;

import java.util.ArrayList;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import com.eucalyptus.ws.MappingHttpRequest;
import com.eucalyptus.ws.MappingHttpResponse;
import com.eucalyptus.ws.handlers.MessageStackHandler;
import com.google.common.collect.Lists;

import edu.ucsb.eucalyptus.msgs.DeregisterImageType;
import edu.ucsb.eucalyptus.msgs.DescribeImageAttributeType;
import edu.ucsb.eucalyptus.msgs.DescribeImagesResponseType;
import edu.ucsb.eucalyptus.msgs.DescribeImagesType;
import edu.ucsb.eucalyptus.msgs.DescribeInstancesResponseType;
import edu.ucsb.eucalyptus.msgs.ImageDetails;
import edu.ucsb.eucalyptus.msgs.ModifyImageAttributeType;
import edu.ucsb.eucalyptus.msgs.ReservationInfoType;
import edu.ucsb.eucalyptus.msgs.ResetImageAttributeType;
import edu.ucsb.eucalyptus.msgs.RunInstancesType;
import edu.ucsb.eucalyptus.msgs.RunningInstancesItemType;

public class ElasticFoxMangleHandler extends MessageStackHandler {

  @Override
  public void incomingMessage( ChannelHandlerContext ctx, MessageEvent event ) throws Exception {
    if ( event.getMessage( ) instanceof MappingHttpResponse ) {
      MappingHttpResponse message = ( MappingHttpResponse ) event.getMessage( );
      if ( message.getMessage( ) instanceof ModifyImageAttributeType ) {
        ModifyImageAttributeType pure = ( ( ModifyImageAttributeType ) message.getMessage( ) );
        pure.setImageId( purifyImageIn( pure.getImageId( ) ) );
      } else if ( message.getMessage( ) instanceof DescribeImageAttributeType ) {
        DescribeImageAttributeType pure = ( ( DescribeImageAttributeType ) message.getMessage( ) );
        pure.setImageId( purifyImageIn( pure.getImageId( ) ) );
      } else if ( message.getMessage( ) instanceof ResetImageAttributeType ) {
        ResetImageAttributeType pure = ( ( ResetImageAttributeType ) message.getMessage( ) );
        pure.setImageId( purifyImageIn( pure.getImageId( ) ) );
      } else if ( message.getMessage( ) instanceof DescribeImagesType ) {
        ArrayList<String> strs = Lists.newArrayList( );
        for ( String imgId : ( ( DescribeImagesType ) message.getMessage( ) ).getImagesSet( ) ) {
          strs.add( purifyImageIn( imgId ) );
        }
        ( ( DescribeImagesType ) message.getMessage( ) ).setImagesSet( strs );
      } else if ( message.getMessage( ) instanceof DeregisterImageType ) {
        DeregisterImageType pure = ( ( DeregisterImageType ) message.getMessage( ) );
        pure.setImageId( purifyImageIn( pure.getImageId( ) ) );
      } else if ( message.getMessage( ) instanceof RunInstancesType ) {
        RunInstancesType pure = ( ( RunInstancesType ) message.getMessage( ) );
        pure.setImageId( purifyImageIn( pure.getImageId( ) ) );
        pure.setKernelId( purifyImageIn( pure.getKernelId( ) ) );
        pure.setRamdiskId( purifyImageIn( pure.getRamdiskId( ) ) );
      }

    }

  }

  private String purifyImageIn( String id ) {
    id = "e" + id.substring( 1, 4 ) + id.substring( 4 ).toUpperCase( );
    return id;
  }

  @Override
  public void outgoingMessage( ChannelHandlerContext ctx, MessageEvent event ) throws Exception {
    if ( event.getMessage( ) instanceof MappingHttpRequest ) {
      MappingHttpRequest message = ( MappingHttpRequest ) event.getMessage( );
      if ( message.getMessage( ) instanceof DescribeImagesResponseType ) {
        DescribeImagesResponseType purify = ( DescribeImagesResponseType ) message.getMessage( );
        for ( ImageDetails img : purify.getImagesSet( ) ) {
          img.setImageId( img.getImageId( ).replaceFirst( "^e", "a" ).toLowerCase( ) );
          if ( img.getKernelId( ) != null ) img.setKernelId( img.getKernelId( ).replaceFirst( "^e", "a" ).toLowerCase( ) );
          if ( img.getRamdiskId( ) != null ) img.setRamdiskId( img.getRamdiskId( ).replaceFirst( "^e", "a" ).toLowerCase( ) );
        }
      } else if ( message.getMessage( ) instanceof DescribeInstancesResponseType ) {
        DescribeInstancesResponseType purify = ( DescribeInstancesResponseType ) message.getMessage( );
        for ( ReservationInfoType rsvInfo : purify.getReservationSet( ) ) {
          for ( RunningInstancesItemType r : rsvInfo.getInstancesSet( ) ) {
            r.setImageId( r.getImageId( ).replaceFirst( "^e", "a" ).toLowerCase( ) );
            if ( r.getKernel( ) != null ) r.setKernel( r.getKernel( ).replaceFirst( "^e", "a" ).toLowerCase( ) );
            if ( r.getRamdisk( ) != null ) r.setRamdisk( r.getRamdisk( ).replaceFirst( "^e", "a" ).toLowerCase( ) );
          }
        }
      }
    }
  }

}
