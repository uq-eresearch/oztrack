<coverage>
  <name>cars2009a_temperature</name>
  <nativeName>cars2009a_temperature</nativeName>
  <namespace>
    <name>oztrack</name>
  </namespace>
  <title>cars2009a_temperature</title>
  <description>Generated from GeoTIFF</description>
  <keywords>
    <string>WCS</string>
    <string>GeoTIFF</string>
    <string>cars2009a_temperature</string>
  </keywords>
  <nativeCRS class="org.geotools.referencing.crs.DefaultEngineeringCRS">LOCAL_CS[&quot;Wildcard 2D cartesian plane in metric unit&quot;, 
  LOCAL_DATUM[&quot;Unknown&quot;, 0], 
  UNIT[&quot;m&quot;, 1.0], 
  AXIS[&quot;x&quot;, EAST], 
  AXIS[&quot;y&quot;, NORTH], 
  AUTHORITY[&quot;EPSG&quot;,&quot;404000&quot;]]</nativeCRS>
  <srs>EPSG:4326</srs>
  <nativeBoundingBox>
    <minx>-180.25</minx>
    <maxx>180.25</maxx>
    <miny>-75.25</miny>
    <maxy>90.25</maxy>
    <crs class="org.geotools.referencing.crs.DefaultEngineeringCRS">EPSG:404000</crs>
  </nativeBoundingBox>
  <latLonBoundingBox>
    <minx>-180.0</minx>
    <maxx>180.0</maxx>
    <miny>-75.0</miny>
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
    <entry key="dirName">cars2009a_temperature_cars2009a_temperature</entry>
  </metadata>
  <store class="coverageStore">
    <name>cars2009a_temperature</name>
  </store>
  <nativeFormat>GeoTIFF</nativeFormat>
  <grid dimension="2">
    <range>
      <low>0 0</low>
      <high>721 331</high>
    </range>
    <transform>
      <scaleX>0.5</scaleX>
      <scaleY>-0.5453172205438066</scaleY>
      <shearX>0.0</shearX>
      <shearY>0.0</shearY>
      <translateX>-180.0</translateX>
      <translateY>89.9773413897281</translateY>
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
      <range>
        <min>-inf</min>
        <max>inf</max>
      </range>
    </coverageDimension>
  </dimensions>
  <requestSRS>
    <string>EPSG:404000</string>
  </requestSRS>
  <responseSRS>
    <string>EPSG:404000</string>
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