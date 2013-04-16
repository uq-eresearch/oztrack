<?xml version="1.0" encoding="UTF-8"?>
<coverage>
  <name>fire-frequency-avhrr-1997-2009</name>
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
    <minx>112.0</minx>
    <maxx>154.0</maxx>
    <miny>-44.0</miny>
    <maxy>-10.0</maxy>
    <crs>EPSG:4326</crs>
  </nativeBoundingBox>
  <latLonBoundingBox>
    <minx>112.0</minx>
    <maxx>154.0</maxx>
    <miny>-44.0</miny>
    <maxy>-10.0</maxy>
    <crs>EPSG:4326</crs>
  </latLonBoundingBox>
  <projectionPolicy>REPROJECT_TO_DECLARED</projectionPolicy>
  <enabled>true</enabled>
  <metadata>
    <entry key="cachingEnabled">false</entry>
    <entry key="dirName">fire-frequency-avhrr-1997-2009_fire-frequency-avhrr-1997-2009</entry>
  </metadata>
  <nativeFormat>GeoTIFF</nativeFormat>
  <grid dimension="2">
    <range>
      <low>0 0</low>
      <high>4200 3400</high>
    </range>
    <transform>
      <scaleX>0.01</scaleX>
      <scaleY>-0.01</scaleY>
      <shearX>0.0</shearX>
      <shearY>0.0</shearY>
      <translateX>112.005</translateX>
      <translateY>-10.005</translateY>
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
      <description>GridSampleDimension[0.0,0.0]</description>
      <range>
        <min>0.0</min>
        <max>0.0</max>
      </range>
      <nullValues>
        <double>0.0</double>
      </nullValues>
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