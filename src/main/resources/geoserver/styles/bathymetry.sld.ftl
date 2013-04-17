<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor
  version="1.0.0"
  xmlns="http://www.opengis.net/sld"
  xmlns:sld="http://www.opengis.net/sld"
  xmlns:ogc="http://www.opengis.net/ogc"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd">
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
            <sld:ColorMap type="ramp">
              <sld:ColorMapEntry color="#181736" quantity="-8000" label="-8000 m"/>
              <sld:ColorMapEntry color="#142d66" quantity="-7000" label="-7000 m"/>
              <sld:ColorMapEntry color="#155787" quantity="-6000" label="-6000 m"/>
              <sld:ColorMapEntry color="#158aab" quantity="-5000" label="-5000 m"/>
              <sld:ColorMapEntry color="#1b9bb8" quantity="-4000" label="-4000 m"/>
              <sld:ColorMapEntry color="#01b0cb" quantity="-3000" label="-3000 m"/>
              <sld:ColorMapEntry color="#37bad8" quantity="-2000" label="-2000 m"/>
              <sld:ColorMapEntry color="#5fc2d9" quantity="-1000" label="-1000 m"/>
              <sld:ColorMapEntry color="#86cbd2" quantity="-500" label="-500 m"/>
              <sld:ColorMapEntry color="#afdad1" quantity="-200" label="-200 m"/>
              <sld:ColorMapEntry color="#afdabc" quantity="-100" label="-100 m"/>
              <sld:ColorMapEntry color="#ccdab4" quantity="-50" label="-50 m"/>
              <sld:ColorMapEntry color="#d0d0ac" quantity="-25" label="-25 m"/>
              <sld:ColorMapEntry color="#e0b0a0" quantity="-10" label="-10 m"/>
              <sld:ColorMapEntry color="#e0b0a0" quantity="-1" label="-1 m"/>
              <sld:ColorMapEntry color="#ffffff" quantity="0" label="0 m" opacity="0"/>
            </sld:ColorMap>
          </sld:RasterSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>
