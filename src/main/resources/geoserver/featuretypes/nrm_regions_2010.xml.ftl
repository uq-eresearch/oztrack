<featureType>
  <name>nrm_regions_2010</name>
  <nativeName>NRM_Regions_2010</nativeName>
  <namespace>
    <name>oztrack</name>
    <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="http://localhost/geoserver/rest/namespaces/oztrack.xml" type="application/xml"/>
  </namespace>
  <description>Contents of file</description>
  <keywords>
    <string>NRM_Regions_2010</string>
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
    <minx>72.24619483900005</minx>
    <maxx>168.22617881200006</maxx>
    <miny>-55.173104263999925</miny>
    <maxy>-9.187946932999933</maxy>
    <crs>GEOGCS[&quot;GCS_GDA_1994&quot;, 
  DATUM[&quot;D_GDA_1994&quot;, 
    SPHEROID[&quot;GRS_1980&quot;, 6378137.0, 298.257222101]], 
  PRIMEM[&quot;Greenwich&quot;, 0.0], 
  UNIT[&quot;degree&quot;, 0.017453292519943295], 
  AXIS[&quot;Longitude&quot;, EAST], 
  AXIS[&quot;Latitude&quot;, NORTH]]</crs>
  </nativeBoundingBox>
  <latLonBoundingBox>
    <minx>72.24619483900005</minx>
    <maxx>168.22617881200006</maxx>
    <miny>-55.173104263999925</miny>
    <maxy>-9.187946932999933</maxy>
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
  </metadata>
  <store class="dataStore">
    <name>nrm_regions_2010</name>
    <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="http://localhost/geoserver/rest/workspaces/oztrack/datastores/nrm_regions_2010.xml" type="application/xml"/>
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
      <name>NRM_REGION</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>true</nillable>
      <binding>java.lang.String</binding>
      <length>40</length>
    </attribute>
    <attribute>
      <name>STATE</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>true</nillable>
      <binding>java.lang.String</binding>
      <length>20</length>
    </attribute>
    <attribute>
      <name>NRM_BODY</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>true</nillable>
      <binding>java.lang.String</binding>
      <length>55</length>
    </attribute>
    <attribute>
      <name>AREA_DESC</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>true</nillable>
      <binding>java.lang.String</binding>
      <length>40</length>
    </attribute>
    <attribute>
      <name>SHAPE_Leng</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>true</nillable>
      <binding>java.lang.Double</binding>
      <length>19</length>
    </attribute>
    <attribute>
      <name>SHAPE_Area</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>true</nillable>
      <binding>java.lang.Double</binding>
      <length>19</length>
    </attribute>
  </attributes>
</featureType>