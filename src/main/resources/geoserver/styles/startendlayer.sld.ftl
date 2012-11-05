<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>startendlayer</sld:Name>
    <sld:UserStyle>
      <sld:Name>startendlayer</sld:Name>
      <sld:Title/>
      <sld:FeatureTypeStyle>
        <sld:Name>startendlayer</sld:Name>
        <sld:Rule>
          <sld:PointSymbolizer>
            <sld:Geometry>
              <ogc:PropertyName>startlocationgeometry</ogc:PropertyName>
            </sld:Geometry>
            <sld:Graphic>
              <sld:Mark>
                <sld:WellKnownName>circle</sld:WellKnownName>
                <sld:Stroke>
                  <sld:CssParameter name="stroke">#00CD00</sld:CssParameter>
                  <sld:CssParameter name="stroke-width">1.2</sld:CssParameter>
                  <sld:CssParameter name="stroke-opacity">1.0</sld:CssParameter>
                </sld:Stroke>
              </sld:Mark>
              <sld:Size>5</sld:Size>
            </sld:Graphic>
          </sld:PointSymbolizer>
        </sld:Rule>
        <sld:Rule>
          <sld:PointSymbolizer>
            <sld:Geometry>
              <ogc:PropertyName>endlocationgeometry</ogc:PropertyName>
            </sld:Geometry>
            <sld:Graphic>
              <sld:Mark>
                <sld:WellKnownName>circle</sld:WellKnownName>
                <sld:Stroke>
                  <sld:CssParameter name="stroke">#CD0000</sld:CssParameter>
                  <sld:CssParameter name="stroke-width">1.2</sld:CssParameter>
                  <sld:CssParameter name="stroke-opacity">1.0</sld:CssParameter>
                </sld:Stroke>
              </sld:Mark>
              <sld:Size>5</sld:Size>
            </sld:Graphic>
          </sld:PointSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>