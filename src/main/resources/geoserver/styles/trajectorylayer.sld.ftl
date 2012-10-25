<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>trajectorylayer</sld:Name>
    <sld:UserStyle>
      <sld:Name>trajectorylayer</sld:Name>
      <sld:Title/>
      <sld:FeatureTypeStyle>
        <sld:Name>trajectorylayer</sld:Name>
        <#list colours as colour>
        <sld:Rule>
          <ogc:Filter>
            <ogc:PropertyIsEqualTo>
              <ogc:Function name="IEEERemainder">
                <ogc:PropertyName>animal_id</ogc:PropertyName>
                <ogc:Literal>12</ogc:Literal>
              </ogc:Function>
              <ogc:Literal>${colour_index}</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:Filter>
          <sld:LineSymbolizer>
            <sld:Stroke>
              <sld:CssParameter name="stroke">${colour}</sld:CssParameter>
              <sld:CssParameter name="stroke-width">2.0</sld:CssParameter>
              <sld:CssParameter name="stroke-opacity">0.8</sld:CssParameter>
            </sld:Stroke>
          </sld:LineSymbolizer>
        </sld:Rule>
        </#list>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>