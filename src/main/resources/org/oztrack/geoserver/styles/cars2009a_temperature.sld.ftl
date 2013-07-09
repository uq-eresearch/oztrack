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
              <!-- scale_factor=0.0005798605282834602; add_offset=13 -->
              <!-- quantity = band * 0.0005798605282834602 + 13 -->
              <!-- band = (quantity - 13) / 0.0005798605282834602 -->
              <sld:ColorMapEntry color="#FFFFFF" quantity="-32767" opacity="0"/>
              <sld:ColorMapEntry color="#FFFFFF" quantity="-22419.1842105264" label="- °C"/>
              <sld:ColorMapEntry color="#FEFEFE" quantity="-22419.1842105263" label="0 °C"/>
              <sld:ColorMapEntry color="#0000FE" quantity="-18970.0789473684" label="2 °C"/>
              <sld:ColorMapEntry color="#0033FE" quantity="-15520.9736842105" label="4 °C"/>
              <sld:ColorMapEntry color="#0059FE" quantity="-12071.8684210526" label="6 °C"/>
              <sld:ColorMapEntry color="#0099FF" quantity="-8622.76315789474" label="8 °C"/>
              <sld:ColorMapEntry color="#00D8FE" quantity="-5173.65789473684" label="10 °C"/>
              <sld:ColorMapEntry color="#238E23" quantity="-1724.55263157895" label="12 °C"/>
              <sld:ColorMapEntry color="#00A000" quantity="1724.55263157895" label="14 °C"/>
              <sld:ColorMapEntry color="#4CCC4C" quantity="5173.65789473684" label="16 °C"/>
              <sld:ColorMapEntry color="#66FE66" quantity="8622.76315789474" label="18 °C"/>
              <sld:ColorMapEntry color="#F2FF7E" quantity="12071.8684210526" label="20 °C"/>
              <sld:ColorMapEntry color="#FEFE00" quantity="15520.9736842105" label="22 °C"/>
              <sld:ColorMapEntry color="#FFB200" quantity="18970.0789473684" label="24 °C"/>
              <sld:ColorMapEntry color="#FE5100" quantity="22419.1842105263" label="26 °C"/>
              <sld:ColorMapEntry color="#FE2600" quantity="25868.2894736842" label="28 °C"/>
              <sld:ColorMapEntry color="#B12D60" quantity="29317.3947368421" label="30 °C"/>
            </sld:ColorMap>
          </sld:RasterSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>