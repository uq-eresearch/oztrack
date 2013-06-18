<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>Default Styler</sld:Name>
    <sld:UserStyle>
      <sld:Name>Default Styler</sld:Name>
      <sld:Title/>
      <sld:FeatureTypeStyle>
        <sld:Name>name</sld:Name>
        <sld:Rule>
          <sld:RasterSymbolizer>
            <sld:ChannelSelection>
              <sld:GrayChannel>
                <sld:SourceChannelName>1</sld:SourceChannelName>
              </sld:GrayChannel>
            </sld:ChannelSelection>
            <sld:ColorMap>
              <!-- scale_factor=0.003662277020737644; add_offset=115 -->
              <!-- quantity = band * 0.003662277020737644 + 115 -->
              <!-- band = (quantity - 115) / 0.003662277020737644 -->
              <sld:ColorMapEntry color="#FFFFFF" quantity="-32767" opacity="0"/>
              <sld:ColorMapEntry color="#0000FE" quantity="-31401.2291666667" label="0 μmol/L"/>
              <sld:ColorMapEntry color="#0099FF" quantity="-30855.1208333333" label="2 μmol/L"/>
              <sld:ColorMapEntry color="#00D8FE" quantity="-30309.0125" label="4 μmol/L"/>
              <sld:ColorMapEntry color="#00A000" quantity="-29762.9041666667" label="6 μmol/L"/>
              <sld:ColorMapEntry color="#66FE66" quantity="-29216.7958333333" label="8 μmol/L"/>
              <sld:ColorMapEntry color="#FEFE00" quantity="-28670.6875" label="10 μmol/L"/>
              <sld:ColorMapEntry color="#FFB200" quantity="-25940.1458333333" label="20 μmol/L"/>
              <sld:ColorMapEntry color="#FE2600" quantity="-20479.0625" label="40 μmol/L"/>
              <sld:ColorMapEntry color="#B12D60" quantity="-9556.89583333333" label="80 μmol/L"/>
            </sld:ColorMap>
          </sld:RasterSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>