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
              <!-- scale_factor=0.001; add_offset=0 -->
              <!-- quantity = band * 0.001 + 0 -->
              <!-- band = (quantity - 0) / 0.001 -->
              <sld:ColorMapEntry color="#FFFFFF" quantity="-32767" opacity="0"/>
              <sld:ColorMapEntry color="#FEFEFE" quantity="0" label="0 m"/>
              <sld:ColorMapEntry color="#0000FE" quantity="200" label="0.2 m"/>
              <sld:ColorMapEntry color="#0033FE" quantity="400" label="0.4 m"/>
              <sld:ColorMapEntry color="#0059FE" quantity="600" label="0.6 m"/>
              <sld:ColorMapEntry color="#0099FF" quantity="800" label="0.8 m"/>
              <sld:ColorMapEntry color="#00D8FE" quantity="1000" label="1 m"/>
              <sld:ColorMapEntry color="#238E23" quantity="1200" label="1.2 m"/>
              <sld:ColorMapEntry color="#00A000" quantity="1400" label="1.4 m"/>
              <sld:ColorMapEntry color="#4CCC4C" quantity="1600" label="1.6 m"/>
              <sld:ColorMapEntry color="#66FE66" quantity="1800" label="1.8 m"/>
              <sld:ColorMapEntry color="#F2FF7E" quantity="2000" label="2 m"/>
              <sld:ColorMapEntry color="#FEFE00" quantity="2200" label="2.2 m"/>
              <sld:ColorMapEntry color="#FFB200" quantity="2400" label="2.4 m"/>
              <sld:ColorMapEntry color="#FE5100" quantity="2600" label="2.6 m"/>
              <sld:ColorMapEntry color="#FE2600" quantity="2800" label="2.8 m"/>
              <sld:ColorMapEntry color="#B12D60" quantity="3000" label="3 m"/>
            </sld:ColorMap>
          </sld:RasterSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>