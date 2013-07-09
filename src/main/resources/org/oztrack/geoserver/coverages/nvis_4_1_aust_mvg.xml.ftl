<?xml version="1.0" encoding="UTF-8"?>
<coverage>
  <name>nvis_4_1_aust_mvg</name>
  <nativeName>nvis_4_1_aust_mvg</nativeName>
  <namespace>
    <name>oztrack</name>
  </namespace>
  <title>nvis_4_1_aust_mvg</title>
  <description>Generated from GeoTIFF</description>
  <keywords>
    <string>WCS</string>
    <string>GeoTIFF</string>
    <string>nvis_4_1_aust_mvg</string>
  </keywords>
  <nativeCRS class="projected">PROJCS[&quot;GDA94 / Australian Albers&quot;, 
  GEOGCS[&quot;GDA94&quot;, 
    DATUM[&quot;Geocentric Datum of Australia 1994&quot;, 
      SPHEROID[&quot;GRS 1980&quot;, 6378137.0, 298.257222101, AUTHORITY[&quot;EPSG&quot;,&quot;7019&quot;]], 
      TOWGS84[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], 
      AUTHORITY[&quot;EPSG&quot;,&quot;6283&quot;]], 
    PRIMEM[&quot;Greenwich&quot;, 0.0, AUTHORITY[&quot;EPSG&quot;,&quot;8901&quot;]], 
    UNIT[&quot;degree&quot;, 0.017453292519943295], 
    AXIS[&quot;Geodetic longitude&quot;, EAST], 
    AXIS[&quot;Geodetic latitude&quot;, NORTH], 
    AUTHORITY[&quot;EPSG&quot;,&quot;4283&quot;]], 
  PROJECTION[&quot;Albers_Conic_Equal_Area&quot;, AUTHORITY[&quot;EPSG&quot;,&quot;9822&quot;]], 
  PARAMETER[&quot;central_meridian&quot;, 132.0], 
  PARAMETER[&quot;latitude_of_origin&quot;, 0.0], 
  PARAMETER[&quot;standard_parallel_1&quot;, -18.0], 
  PARAMETER[&quot;false_easting&quot;, 0.0], 
  PARAMETER[&quot;false_northing&quot;, 0.0], 
  PARAMETER[&quot;standard_parallel_2&quot;, -36.0], 
  UNIT[&quot;m&quot;, 1.0], 
  AXIS[&quot;Easting&quot;, EAST], 
  AXIS[&quot;Northing&quot;, NORTH], 
  AUTHORITY[&quot;EPSG&quot;,&quot;3577&quot;]]</nativeCRS>
  <srs>EPSG:3577</srs>
  <nativeBoundingBox>
    <minx>-1888000.0</minx>
    <maxx>2122000.0</maxx>
    <miny>-4840999.99999999</miny>
    <maxy>-1003999.9999999898</maxy>
    <crs class="projected">EPSG:3577</crs>
  </nativeBoundingBox>
  <latLonBoundingBox>
    <minx>109.5043559812428</minx>
    <maxx>157.2157367208643</maxx>
    <miny>-44.312602859794254</miny>
    <maxy>-8.139869342336084</maxy>
    <crs>GEOGCS[&quot;WGS84(DD)&quot;, 
  DATUM[&quot;WGS84&quot;, 
    SPHEROID[&quot;WGS84&quot;, 6378137.0, 298.257223563]], 
  PRIMEM[&quot;Greenwich&quot;, 0.0], 
  UNIT[&quot;degree&quot;, 0.017453292519943295], 
  AXIS[&quot;Geodetic longitude&quot;, EAST], 
  AXIS[&quot;Geodetic latitude&quot;, NORTH]]</crs>
  </latLonBoundingBox>
  <projectionPolicy>REPROJECT_TO_DECLARED</projectionPolicy>
  <enabled>true</enabled>
  <metadata>
    <entry key="cachingEnabled">false</entry>
    <entry key="dirName">nvis_4_1_aust_mvg_nvis_4_1_aust_mvg</entry>
  </metadata>
  <store class="coverageStore">
    <name>nvis_4_1_aust_mvg</name>
  </store>
  <nativeFormat>GeoTIFF</nativeFormat>
  <grid dimension="2">
    <range>
      <low>0 0</low>
      <high>40100 38370</high>
    </range>
    <transform>
      <scaleX>100.0</scaleX>
      <scaleY>-100.0</scaleY>
      <shearX>0.0</shearX>
      <shearY>0.0</shearY>
      <translateX>-1887950.0</translateX>
      <translateY>-1004049.9999999898</translateY>
    </transform>
    <crs>EPSG:3577</crs>
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
      <name>PALETTE_INDEX</name>
      <description>GridSampleDimension[255.0,255.0]</description>
      <range>
        <min>255.0</min>
        <max>255.0</max>
      </range>
      <nullValues>
        <double>255.0</double>
      </nullValues>
    </coverageDimension>
  </dimensions>
  <requestSRS>
    <string>EPSG:3577</string>
  </requestSRS>
  <responseSRS>
    <string>EPSG:3577</string>
  </responseSRS>
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