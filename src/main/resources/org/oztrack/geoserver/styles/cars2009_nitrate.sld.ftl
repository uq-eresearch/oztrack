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
              <!-- scale_factor=0.000915569255184411; add_offset=28 -->
              <!-- quantity = band * 0.000915569255184411 + 28 -->
              <!-- band = (quantity - 28) / 0.000915569255184411 -->
              <sld:ColorMapEntry color="#FFFFFF" quantity="-32767" opacity="0"/>
              <sld:ColorMapEntry color="#FEFEFE" quantity="-30582.0666666667" label="0 mL/L"/>
              <sld:ColorMapEntry color="#0000FE" quantity="-30527.4558333333" label="0.05 mL/L"/>
              <sld:ColorMapEntry color="#0033FE" quantity="-30472.845" label="0.1 mL/L"/>
              <sld:ColorMapEntry color="#0059FE" quantity="-30363.6233333333" label="0.2 mL/L"/>
              <sld:ColorMapEntry color="#0099FF" quantity="-30145.18" label="0.4 mL/L"/>
              <sld:ColorMapEntry color="#00D8FE" quantity="-29708.2933333333" label="0.8 mL/L"/>
              <sld:ColorMapEntry color="#238E23" quantity="-28834.52" label="1.6 mL/L"/>
              <sld:ColorMapEntry color="#00A000" quantity="-28397.6333333333" label="2 mL/L"/>
              <sld:ColorMapEntry color="#4CCC4C" quantity="-27851.525" label="2.5 mL/L"/>
              <sld:ColorMapEntry color="#66FE66" quantity="-27305.4166666667" label="3 mL/L"/>
              <sld:ColorMapEntry color="#F2FF7E" quantity="-25120.9833333333" label="5 mL/L"/>
              <sld:ColorMapEntry color="#FEFE00" quantity="-19659.9" label="10 mL/L"/>
              <sld:ColorMapEntry color="#FFB200" quantity="-14198.8166666667" label="15 mL/L"/>
              <sld:ColorMapEntry color="#FE5100" quantity="-8737.73333333333" label="20 mL/L"/>
              <sld:ColorMapEntry color="#FE2600" quantity="-3276.65" label="25 mL/L"/>
              <sld:ColorMapEntry color="#B12D60" quantity="2184.43333333333" label="30 mL/L"/>
            </sld:ColorMap>
          </sld:RasterSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>