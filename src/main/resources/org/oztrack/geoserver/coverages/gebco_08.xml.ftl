<?xml version="1.0" encoding="UTF-8"?>
<coverage>
  <name>gebco_08</name>
  <nativeCRS class="org.geotools.referencing.crs.DefaultEngineeringCRS">LOCAL_CS[&quot;Generic cartesian 2D&quot;, 
  LOCAL_DATUM[&quot;Unknow&quot;, 0], 
  UNIT[&quot;m&quot;, 1.0], 
  AXIS[&quot;x&quot;, EAST], 
  AXIS[&quot;y&quot;, NORTH]]</nativeCRS>
  <srs>EPSG:4326</srs>
  <nativeBoundingBox>
    <minx>-180.0</minx>
    <maxx>180.0</maxx>
    <miny>-90.0</miny>
    <maxy>90.0</maxy>
    <crs class="org.geotools.referencing.crs.DefaultEngineeringCRS">LOCAL_CS[&quot;Generic cartesian 2D&quot;, 
  LOCAL_DATUM[&quot;Unknow&quot;, 0], 
  UNIT[&quot;m&quot;, 1.0], 
  AXIS[&quot;x&quot;, EAST], 
  AXIS[&quot;y&quot;, NORTH]]</crs>
  </nativeBoundingBox>
  <latLonBoundingBox>
    <minx>-180.0</minx>
    <maxx>180.0</maxx>
    <miny>-90.0</miny>
    <maxy>90.0</maxy>
    <crs>GEOGCS[&quot;WGS84(DD)&quot;, 
  DATUM[&quot;WGS84&quot;, 
    SPHEROID[&quot;WGS84&quot;, 6378137.0, 298.257223563]], 
  PRIMEM[&quot;Greenwich&quot;, 0.0], 
  UNIT[&quot;degree&quot;, 0.017453292519943295], 
  AXIS[&quot;Geodetic longitude&quot;, EAST], 
  AXIS[&quot;Geodetic latitude&quot;, NORTH]]</crs>
  </latLonBoundingBox>
  <projectionPolicy>FORCE_DECLARED</projectionPolicy>
  <enabled>true</enabled>
  <metadata>
    <entry key="cachingEnabled">false</entry>
    <entry key="dirName">gebco_08_gebco_08</entry>
  </metadata>
  <nativeFormat>GeoTIFF</nativeFormat>
  <grid dimension="2">
    <range>
      <low>0 0</low>
      <high>43200 21600</high>
    </range>
    <transform>
      <scaleX>0.008333333333333333</scaleX>
      <scaleY>-0.008333333333333333</scaleY>
      <shearX>0.0</shearX>
      <shearY>0.0</shearY>
      <translateX>-179.99583333333334</translateX>
      <translateY>89.99583333333334</translateY>
    </transform>
    <crs>EPSG:4326</crs>
  </grid>
  <supportedFormats>
    <string>GIF</string>
    <string>PNG</string>
    <string>JPEG</string>
    <string>TIFF</string>
    <string>GEOTIFF</string>
  </supportedFormats>
  <interpolationMethods>
    <string>bilinear</string>
    <string>bicubic</string>
  </interpolationMethods>
  <dimensions>
    <coverageDimension>
      <name>GRAY_INDEX</name>
      <description>GridSampleDimension[-Infinity,Infinity]</description>
    </coverageDimension>
  </dimensions>
  <parameters>
    <entry>
      <string>InputTransparentColor</string>
      <string></string>
    </entry>
    <entry>
      <string>SUGGESTED_TILE_SIZE</string>
      <string>512,512</string>
    </entry>
  </parameters>
</coverage>