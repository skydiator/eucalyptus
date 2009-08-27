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
 *
 * Author: chris grzegorczyk <grze@eucalyptus.com>
 */

package edu.ucsb.eucalyptus.cloud.entities;

import edu.ucsb.eucalyptus.msgs.CacheImageType;
import edu.ucsb.eucalyptus.msgs.CheckImageType;
import edu.ucsb.eucalyptus.msgs.FlushCachedImageType;
import edu.ucsb.eucalyptus.msgs.ImageDetails;
import com.eucalyptus.ws.util.Messaging;
import com.eucalyptus.util.WalrusProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.eucalyptus.util.EntityWrapper;
import com.eucalyptus.util.EucalyptusCloudException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table( name = "Images" )
@Cache( usage = CacheConcurrencyStrategy.READ_WRITE )
public class ImageInfo {

  @Id
  @GeneratedValue
  @Column( name = "image_id" )
  private Long id = -1l;
  @Column( name = "image_name" )
  private String imageId;
  @Column( name = "image_path" )
  private String imageLocation;
  @Column( name = "image_availability" )
  private String imageState;
  @Column( name = "image_owner_id" )
  private String imageOwnerId;
  @Column( name = "image_arch" )
  private String architecture;
  @Column( name = "image_type" )
  private String imageType;
  @Column( name = "image_kernel_id" )
  private String kernelId;
  @Column( name = "image_ramdisk_id" )
  private String ramdiskId;
  @Column( name = "image_is_public" )
  private Boolean isPublic;
  @Lob
  @Column( name = "image_signature" )
  private String signature;
  @ManyToMany( cascade = CascadeType.PERSIST )
  @JoinTable(
      name = "image_has_groups",
      joinColumns = { @JoinColumn( name = "image_id" ) },
      inverseJoinColumns = @JoinColumn( name = "user_group_id" )
  )
  @Cache( usage = CacheConcurrencyStrategy.READ_WRITE )
  private List<UserGroupInfo> userGroups = new ArrayList<UserGroupInfo>();
  @ManyToMany()
  @JoinTable(
      name = "image_has_perms",
      joinColumns = { @JoinColumn( name = "image_id" ) },
      inverseJoinColumns = @JoinColumn( name = "user_id" )
  )
  @Cache( usage = CacheConcurrencyStrategy.READ_WRITE )
  private List<UserInfo> permissions = new ArrayList<UserInfo>();
  @ManyToMany( cascade = CascadeType.PERSIST )
  @JoinTable(
      name = "image_has_product_codes",
      joinColumns = { @JoinColumn( name = "image_id" ) },
      inverseJoinColumns = @JoinColumn( name = "image_product_code_id" )
  )
  @Cache( usage = CacheConcurrencyStrategy.READ_WRITE )
  private List<ProductCode> productCodes = new ArrayList<ProductCode>();

  public static ImageInfo deregistered() {
    ImageInfo img = new ImageInfo();
    img.setImageState( "deregistered" );
    return img;
  }

  public static ImageInfo byOwnerId( String ownerId ) {
    ImageInfo img = new ImageInfo();
    img.setImageOwnerId( ownerId );
    return img;
  }

  public ImageInfo() {}

  public ImageInfo( final String imageId ) {
    this.imageId = imageId.substring( 0, 4 ).toLowerCase( ) + imageId.substring( 4 ).toUpperCase();
  }

  public ImageInfo( final String imageLocation, final String imageOwnerId, final String imageState, final Boolean aPublic ) {
    this.imageLocation = imageLocation;
    this.imageOwnerId = imageOwnerId;
    this.imageState = imageState;
    this.isPublic = aPublic;
  }

  public ImageInfo( String architecture, String imageId, String imageLocation, String imageOwnerId, String imageState, String imageType, Boolean aPublic, String kernelId, String ramdiskId ) {
    this.architecture = architecture;
    this.imageId = imageId;
    this.imageLocation = imageLocation;
    this.imageOwnerId = imageOwnerId;
    this.imageState = imageState;
    this.imageType = imageType;
    this.isPublic = aPublic;
    this.kernelId = kernelId;
    this.ramdiskId = ramdiskId;
  }

  public Long getId() {
    return this.id;
  }

  public String getArchitecture() {
    return architecture;
  }

  public void setArchitecture( String architecture ) {
    this.architecture = architecture;
  }

  public String getImageId() {
    return imageId;
  }

  public void setImageId( String imageId ) {
    this.imageId = imageId;
  }

  public String getImageLocation() {
    return imageLocation;
  }

  public void setImageLocation( String imageLocation ) {
    this.imageLocation = imageLocation;
  }

  public String getImageOwnerId() {
    return imageOwnerId;
  }

  public void setImageOwnerId( String imageOwnerId ) {
    this.imageOwnerId = imageOwnerId;
  }

  public String getImageState() {
    return imageState;
  }

  public void setImageState( String imageState ) {
    this.imageState = imageState;
  }

  public String getImageType() {
    return imageType;
  }

  public void setImageType( String imageType ) {
    this.imageType = imageType;
  }

  public Boolean getPublic() {
    return isPublic;
  }

  public void setPublic( Boolean aPublic ) {
    isPublic = aPublic;
  }

  public String getKernelId() {
    return kernelId;
  }

  public void setKernelId( String kernelId ) {
    this.kernelId = kernelId;
  }

  public String getRamdiskId() {
    return ramdiskId;
  }

  public void setRamdiskId( String ramdiskId ) {
    this.ramdiskId = ramdiskId;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature( final String signature ) {
    this.signature = signature;
  }

  public List<UserGroupInfo> getUserGroups() {
    return userGroups;
  }

  public void setUserGroups( final List<UserGroupInfo> userGroups ) {
    this.userGroups = userGroups;
  }

  public List<UserInfo> getPermissions() {
    return permissions;
  }

  public void setPermissions( final List<UserInfo> permissions ) {
    this.permissions = permissions;
  }

  public ImageDetails getAsImageDetails() {
    ImageDetails i = new ImageDetails();
    i.setArchitecture( this.getArchitecture() );
    i.setImageId( this.getImageId() );
    i.setImageLocation( this.getImageLocation() );
    i.setImageOwnerId( this.getImageOwnerId() );
    i.setImageState( this.getImageState() );
    i.setImageType( this.getImageType() );
    i.setIsPublic( this.getPublic() );
    i.setKernelId( this.getKernelId() );
    i.setRamdiskId( this.getRamdiskId() );
    return i;
  }

  public List<ProductCode> getProductCodes() {
    return productCodes;
  }

  public void setProductCodes( final List<ProductCode> productCodes ) {
    this.productCodes = productCodes;
  }

  @Override
  public boolean equals( final Object o ) {
    if ( this == o ) return true;
    if ( o == null || getClass() != o.getClass() ) return false;

    ImageInfo imageInfo = ( ImageInfo ) o;

    if ( !imageId.equals( imageInfo.imageId ) ) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return imageId.hashCode();
  }

  public boolean isAllowed( UserInfo user ) {
    if ( user.isAdministrator() || user.getUserName().equals( this.getImageOwnerId() ) )
      return true;
    for ( UserGroupInfo g : this.getUserGroups() )
      if ( "all".equals( g.getName() ) )
        return true;
    return this.getPermissions().contains( user );
  }

  public static ImageInfo named( String imageId ) throws EucalyptusCloudException {
    EntityWrapper<ImageInfo> db = new EntityWrapper<ImageInfo>();
    ImageInfo image = null;
    try {
      image = db.getUnique( new ImageInfo( imageId ) );
    } finally {
      db.commit();
    }
    return image;
  }

  @Override
  public String toString() {
    return this.imageId;
  }
}
