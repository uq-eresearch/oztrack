<?xml version="1.0" encoding="UTF-8"?>
<coverage>
  <name>dlcd-class</name>
  <nativeCRS>GEOGCS[&quot;WGS 84&quot;, 
  DATUM[&quot;World Geodetic System 1984&quot;, 
    SPHEROID[&quot;WGS 84&quot;, 6378137.0, 298.257223563, AUTHORITY[&quot;EPSG&quot;,&quot;7030&quot;]], 
    AUTHORITY[&quot;EPSG&quot;,&quot;6326&quot;]], 
  PRIMEM[&quot;Greenwich&quot;, 0.0, AUTHORITY[&quot;EPSG&quot;,&quot;8901&quot;]], 
  UNIT[&quot;degree&quot;, 0.017453292519943295], 
  AXIS[&quot;Geodetic longitude&quot;, EAST], 
  AXIS[&quot;Geodetic latitude&quot;, NORTH], 
  AUTHORITY[&quot;EPSG&quot;,&quot;4326&quot;]]</nativeCRS>
  <srs>EPSG:4326</srs>
  <nativeBoundingBox>
    <minx>110.0000005</minx>
    <maxx>155.0091895</maxx>
    <miny>-45.004797499999995</miny>
    <maxy>-9.999999500000008</maxy>
    <crs>EPSG:4326</crs>
  </nativeBoundingBox>
  <latLonBoundingBox>
    <minx>110.0000005</minx>
    <maxx>155.0091895</maxx>
    <miny>-45.004797499999995</miny>
    <maxy>-9.999999500000008</maxy>
    <crs>EPSG:4326</crs>
  </latLonBoundingBox>
  <projectionPolicy>REPROJECT_TO_DECLARED</projectionPolicy>
  <enabled>true</enabled>
  <metadata>
    <entry key="cachingEnabled">false</entry>
    <entry key="dirName">DLCDv1_Class_DLCDv1_Class</entry>
  </metadata>
  <nativeFormat>GeoTIFF</nativeFormat>
  <grid dimension="2">
    <range>
      <low>0 0</low>
      <high>19161 14902</high>
    </range>
    <transform>
      <scaleX>0.0023489999999999995</scaleX>
      <scaleY>-0.002348999999999999</scaleY>
      <shearX>0.0</shearX>
      <shearY>0.0</shearY>
      <translateX>110.001175</translateX>
      <translateY>-10.001174000000008</translateY>
    </transform>
    <crs>EPSG:4326</crs>
  </grid>
  <supportedFormats>
    <string>GEOTIFF</string>
    <string>GIF</string>
    <string>PNG</string>
    <string>JPEG</string>
    <string>TIFF</string>
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