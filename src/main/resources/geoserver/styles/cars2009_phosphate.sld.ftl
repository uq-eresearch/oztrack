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
              <!-- scale_factor=0.0001602246196572719; add_offset=5.2 -->
              <!-- quantity = band * 0.0001602246196572719 + 5.2 -->
              <!-- band = (quantity - 5.2) / 0.0001602246196572719 -->
              <sld:ColorMapEntry color="#FFFFFF" quantity="-32767" opacity="0"/>
              <sld:ColorMapEntry color="#FEFEFE" quantity="-32454.4380952381" label="0 μmol/L"/>
              <sld:ColorMapEntry color="#0000FE" quantity="-32142.3761904762" label="0.05 μmol/L"/>
              <sld:ColorMapEntry color="#0033FE" quantity="-31830.3142857143" label="0.1 μmol/L"/>
              <sld:ColorMapEntry color="#0059FE" quantity="-31518.2523809524" label="0.15 μmol/L"/>
              <sld:ColorMapEntry color="#0099FF" quantity="-31206.1904761905" label="0.2 μmol/L"/>
              <sld:ColorMapEntry color="#00D8FE" quantity="-30894.1285714286" label="0.25 μmol/L"/>
              <sld:ColorMapEntry color="#238E23" quantity="-30582.0666666667" label="0.3 μmol/L"/>
              <sld:ColorMapEntry color="#00A000" quantity="-30270.0047619048" label="0.35 μmol/L"/>
              <sld:ColorMapEntry color="#4CCC4C" quantity="-29957.9428571429" label="0.4 μmol/L"/>
              <sld:ColorMapEntry color="#66FE66" quantity="-29333.8190476191" label="0.5 μmol/L"/>
              <sld:ColorMapEntry color="#F2FF7E" quantity="-27773.5095238095" label="0.75 μmol/L"/>
              <sld:ColorMapEntry color="#FEFE00" quantity="-26213.2" label="1 μmol/L"/>
              <sld:ColorMapEntry color="#FFB200" quantity="-24652.8904761905" label="1.25 μmol/L"/>
              <sld:ColorMapEntry color="#FE5100" quantity="-23092.580952381" label="1.5 μmol/L"/>
              <sld:ColorMapEntry color="#FE2600" quantity="-21532.2714285714" label="1.75 μmol/L"/>
              <sld:ColorMapEntry color="#B12D60" quantity="-19971.9619047619" label="2 μmol/L"/>
            </sld:ColorMap>
          </sld:RasterSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>