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
              <!-- scale factor: 0.0006485282224222911; add_offset: 21.25 -->
              <!-- PSU = band * 0.0006485282224222911 + 21.25 -->
              <!-- band = (PSU - 21.25) / 0.0006485282224222911 -->
              <sld:ColorMapEntry color="#FFFFFF" quantity="-32767" opacity="0"/>
              <sld:ColorMapEntry color="#F2CCF2" quantity="15034.0411764706" label="31 PSU"/>
              <sld:ColorMapEntry color="#AE73D8" quantity="16575.9941176471" label="32 PSU"/>
              <sld:ColorMapEntry color="#2567F1" quantity="18117.9470588235" label="33 PSU"/>
              <sld:ColorMapEntry color="#5FD19B" quantity="19659.9000000000" label="34 PSU"/>
              <sld:ColorMapEntry color="#2FDA32" quantity="21201.8529411765" label="35 PSU"/>
              <sld:ColorMapEntry color="#D7EB1A" quantity="22743.8058823529" label="36 PSU"/>
              <sld:ColorMapEntry color="#F79709" quantity="24285.7588235294" label="37 PSU"/>
              <sld:ColorMapEntry color="#FA1C2A" quantity="25827.7117647059" label="38 PSU"/>
              <sld:ColorMapEntry color="#FFEEA5" quantity="27369.6647058824" label="39 PSU"/>
            </sld:ColorMap>
          </sld:RasterSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>