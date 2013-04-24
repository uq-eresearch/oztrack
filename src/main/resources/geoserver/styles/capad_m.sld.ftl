<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>Default Styler</sld:Name>
    <sld:UserStyle>
      <sld:Name>Default Styler</sld:Name>
      <sld:Title/>
      <sld:FeatureTypeStyle>
        <sld:Name>IUCN Protected Area Category</sld:Name>
        <sld:Rule>
          <Name>IUCN IA</Name>
          <Title>Strict Nature Reserve (IA)</Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>IUCN</ogc:PropertyName>
              <ogc:Literal>IA</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <sld:PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#00325b</CssParameter>
              <CssParameter name="fill-opacity">0.6</CssParameter>
            </Fill>
            <sld:Stroke>
              <sld:CssParameter name="stroke">#333333</sld:CssParameter>
              <sld:CssParameter name="stroke-opacity">0.8</sld:CssParameter>
            </sld:Stroke>
          </sld:PolygonSymbolizer>
        </sld:Rule>
        <sld:Rule>
          <Name>IUCN IB</Name>
          <Title>Wilderness Area (IB)</Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>IUCN</ogc:PropertyName>
              <ogc:Literal>IB</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <sld:PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#003278</CssParameter>
              <CssParameter name="fill-opacity">0.6</CssParameter>
            </Fill>
            <sld:Stroke>
              <sld:CssParameter name="stroke">#333333</sld:CssParameter>
              <sld:CssParameter name="stroke-opacity">0.8</sld:CssParameter>
            </sld:Stroke>
          </sld:PolygonSymbolizer>
        </sld:Rule>
        <sld:Rule>
          <Name>IUCN II</Name>
          <Title>National Park (II)</Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>IUCN</ogc:PropertyName>
              <ogc:Literal>II</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <sld:PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#003296</CssParameter>
              <CssParameter name="fill-opacity">0.6</CssParameter>
            </Fill>
            <sld:Stroke>
              <sld:CssParameter name="stroke">#333333</sld:CssParameter>
              <sld:CssParameter name="stroke-opacity">0.8</sld:CssParameter>
            </sld:Stroke>
          </sld:PolygonSymbolizer>
        </sld:Rule>
        <sld:Rule>
          <Name>IUCN III</Name>
          <Title>Protected Landscape/Seascape (III)</Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>IUCN</ogc:PropertyName>
              <ogc:Literal>III</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <sld:PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#0032b4</CssParameter>
              <CssParameter name="fill-opacity">0.6</CssParameter>
            </Fill>
            <sld:Stroke>
              <sld:CssParameter name="stroke">#333333</sld:CssParameter>
              <sld:CssParameter name="stroke-opacity">0.8</sld:CssParameter>
            </sld:Stroke>
          </sld:PolygonSymbolizer>
        </sld:Rule>
        <sld:Rule>
          <Name>IUCN IV</Name>
          <Title>Habitat/Species Management Area (IV)</Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>IUCN</ogc:PropertyName>
              <ogc:Literal>IV</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <sld:PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#0032d2</CssParameter>
              <CssParameter name="fill-opacity">0.6</CssParameter>
            </Fill>
            <sld:Stroke>
              <sld:CssParameter name="stroke">#333333</sld:CssParameter>
              <sld:CssParameter name="stroke-opacity">0.8</sld:CssParameter>
            </sld:Stroke>
          </sld:PolygonSymbolizer>
        </sld:Rule>
        <sld:Rule>
          <Name>IUCN V</Name>
          <Title>Protected Landscape/Seascape (V)</Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>IUCN</ogc:PropertyName>
              <ogc:Literal>V</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <sld:PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#0064d2</CssParameter>
              <CssParameter name="fill-opacity">0.6</CssParameter>
            </Fill>
            <sld:Stroke>
              <sld:CssParameter name="stroke">#333333</sld:CssParameter>
              <sld:CssParameter name="stroke-opacity">0.8</sld:CssParameter>
            </sld:Stroke>
          </sld:PolygonSymbolizer>
        </sld:Rule>
        <sld:Rule>
          <Name>IUCN VI</Name>
          <Title>Protected Area with Sustainable Use of Natural Resources (VI)</Title>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>IUCN</ogc:PropertyName>
              <ogc:Literal>VI</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <sld:PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#00afd2</CssParameter>
              <CssParameter name="fill-opacity">0.6</CssParameter>
            </Fill>
            <sld:Stroke>
              <sld:CssParameter name="stroke">#333333</sld:CssParameter>
              <sld:CssParameter name="stroke-opacity">0.8</sld:CssParameter>
            </sld:Stroke>
          </sld:PolygonSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>
