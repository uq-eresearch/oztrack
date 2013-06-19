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
              <!-- scale_factor=0.000153357850243389; add_offset=5 -->
              <!-- quantity = band * 0.000153357850243389 + 5 -->
              <!-- band = (quantity - 5) / 0.000153357850243389 -->
              <sld:ColorMapEntry color="#FFFFFF" quantity="-32767" opacity="0"/>
              <sld:ColorMapEntry color="#FEFEFE" quantity="-32603.4825870647" label="0 mL/L"/>
              <sld:ColorMapEntry color="#0000FE" quantity="-6520.69651741294" label="4 mL/L"/>
              <sld:ColorMapEntry color="#0033FE" quantity="-4890.5223880597" label="4.25 mL/L"/>
              <sld:ColorMapEntry color="#0059FE" quantity="-3260.34825870647" label="4.5 mL/L"/>
              <sld:ColorMapEntry color="#0099FF" quantity="-1630.17412935323" label="4.75 mL/L"/>
              <sld:ColorMapEntry color="#00D8FE" quantity="0" label="5 mL/L"/>
              <sld:ColorMapEntry color="#238E23" quantity="1630.17412935323" label="5.25 mL/L"/>
              <sld:ColorMapEntry color="#00A000" quantity="3260.34825870647" label="5.5 mL/L"/>
              <sld:ColorMapEntry color="#4CCC4C" quantity="4890.5223880597" label="5.75 mL/L"/>
              <sld:ColorMapEntry color="#66FE66" quantity="6520.69651741294" label="6 mL/L"/>
              <sld:ColorMapEntry color="#F2FF7E" quantity="9781.04477611941" label="6.5 mL/L"/>
              <sld:ColorMapEntry color="#FEFE00" quantity="13041.3930348259" label="7 mL/L"/>
              <sld:ColorMapEntry color="#FFB200" quantity="16301.7412935323" label="7.5 mL/L"/>
              <sld:ColorMapEntry color="#FE5100" quantity="19562.0895522388" label="8 mL/L"/>
              <sld:ColorMapEntry color="#FE2600" quantity="22822.4378109453" label="8.5 mL/L"/>
              <sld:ColorMapEntry color="#B12D60" quantity="26082.7860696517" label="9 mL/L"/>
            </sld:ColorMap>
          </sld:RasterSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>