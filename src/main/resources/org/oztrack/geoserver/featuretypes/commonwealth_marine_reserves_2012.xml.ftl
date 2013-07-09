<featureType>
  <name>commonwealth_marine_reserves_2012</name>
  <nativeName>commonwealth_marine_reserves_network_2012</nativeName>
  <namespace>
    <name>oztrack</name>
  </namespace>
  <title>commonwealth_marine_reserves_2012</title>
  <description>Contents of file</description>
  <keywords>
    <string>commonwealth_marine_reserves_network_2012</string>
    <string>features</string>
  </keywords>
  <nativeCRS>GEOGCS[&quot;GCS_GDA_1994&quot;, 
  DATUM[&quot;D_GDA_1994&quot;, 
    SPHEROID[&quot;GRS_1980&quot;, 6378137.0, 298.257222101]], 
  PRIMEM[&quot;Greenwich&quot;, 0.0], 
  UNIT[&quot;degree&quot;, 0.017453292519943295], 
  AXIS[&quot;Longitude&quot;, EAST], 
  AXIS[&quot;Latitude&quot;, NORTH]]</nativeCRS>
  <srs>EPSG:4326</srs>
  <nativeBoundingBox>
    <minx>70.90000000000009</minx>
    <maxx>170.3666666700001</maxx>
    <miny>-58.44946994599985</miny>
    <maxy>-8.881889904291256</maxy>
    <crs>GEOGCS[&quot;GCS_GDA_1994&quot;, 
  DATUM[&quot;D_GDA_1994&quot;, 
    SPHEROID[&quot;GRS_1980&quot;, 6378137.0, 298.257222101]], 
  PRIMEM[&quot;Greenwich&quot;, 0.0], 
  UNIT[&quot;degree&quot;, 0.017453292519943295], 
  AXIS[&quot;Longitude&quot;, EAST], 
  AXIS[&quot;Latitude&quot;, NORTH]]</crs>
  </nativeBoundingBox>
  <latLonBoundingBox>
    <minx>70.90000000000009</minx>
    <maxx>170.3666666700001</maxx>
    <miny>-58.44946994599985</miny>
    <maxy>-8.881889904291256</maxy>
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
  <advertised>true</advertised>
  <metadata>
    <entry key="cachingEnabled">false</entry>
  </metadata>
  <store class="dataStore">
    <name>commonwealth_marine_reserves_2012</name>
  </store>
  <maxFeatures>0</maxFeatures>
  <numDecimals>0</numDecimals>
  <attributes>
    <attribute>
      <name>the_geom</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>true</nillable>
      <binding>com.vividsolutions.jts.geom.MultiPolygon</binding>
    </attribute>
    <attribute>
      <name>Network</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>true</nillable>
      <binding>java.lang.String</binding>
      <length>30</length>
    </attribute>
    <attribute>
      <name>MPA_NAME</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>true</nillable>
      <binding>java.lang.String</binding>
      <length>254</length>
    </attribute>
    <attribute>
      <name>ZONE</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>true</nillable>
      <binding>java.lang.String</binding>
      <length>254</length>
    </attribute>
    <attribute>
      <name>IUCN</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>true</nillable>
      <binding>java.lang.String</binding>
      <length>5</length>
    </attribute>
    <attribute>
      <name>Label</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>true</nillable>
      <binding>java.lang.String</binding>
      <length>100</length>
    </attribute>
    <attribute>
      <name>Status</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>true</nillable>
      <binding>java.lang.String</binding>
      <length>254</length>
    </attribute>
    <attribute>
      <name>Area_km2</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>true</nillable>
      <binding>java.lang.Double</binding>
      <length>19</length>
    </attribute>
    <attribute>
      <name>Legend</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>true</nillable>
      <binding>java.lang.String</binding>
      <length>100</length>
    </attribute>
  </attributes>
</featureType>