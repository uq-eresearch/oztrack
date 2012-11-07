<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>positionfixlayer</sld:Name>
    <sld:UserStyle>
      <sld:Name>positionfixlayer</sld:Name>
      <sld:Title/>
      <sld:FeatureTypeStyle>
        <sld:Name>positionfixlayer</sld:Name>
        <sld:Rule>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>deleted</ogc:PropertyName>
              <ogc:Literal>false</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <sld:PointSymbolizer>
            <sld:Graphic>
              <sld:Mark>
                <sld:WellKnownName>cross</sld:WellKnownName>
                <sld:Fill>
                  <sld:CssParameter name="fill">
                    <ogc:PropertyName>colour</ogc:PropertyName>
                  </sld:CssParameter>
                </sld:Fill>
                <sld:Stroke>
                  <sld:CssParameter name="stroke">#888888</sld:CssParameter>
                  <sld:CssParameter name="stroke-opacity">0.6</sld:CssParameter>
                  <sld:CssParameter name="stroke-width">0.5</sld:CssParameter>
                </sld:Stroke>
              </sld:Mark>
              <sld:Size>
                <ogc:Literal>8</ogc:Literal>
              </sld:Size>
            </sld:Graphic>
          </sld:PointSymbolizer>
        </sld:Rule>
        <sld:Rule>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>deleted</ogc:PropertyName>
              <ogc:Literal>true</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <sld:PointSymbolizer>
            <sld:Graphic>
              <sld:Mark>
                <sld:WellKnownName>cross</sld:WellKnownName>
                <sld:Fill>
                  <sld:CssParameter name="fill">#bc0000</sld:CssParameter>
                </sld:Fill>
                <sld:Stroke>
                  <sld:CssParameter name="stroke">#ffffff</sld:CssParameter>
                  <sld:CssParameter name="stroke-opacity">0.8</sld:CssParameter>
                  <sld:CssParameter name="stroke-width">0.2</sld:CssParameter>
                </sld:Stroke>
              </sld:Mark>
              <sld:Size>
                <ogc:Literal>8</ogc:Literal>
              </sld:Size>
            </sld:Graphic>
          </sld:PointSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>