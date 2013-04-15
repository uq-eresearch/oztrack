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
            <sld:ColorMap extended="ramp">
              <sld:ColorMapEntry color="#181736" quantity="-8000"/>
              <sld:ColorMapEntry color="#142d66" quantity="-7000"/>
              <sld:ColorMapEntry color="#155787" quantity="-6000"/>
              <sld:ColorMapEntry color="#158aab" quantity="-5000"/>
              <sld:ColorMapEntry color="#1b9bb8" quantity="-4000"/>
              <sld:ColorMapEntry color="#01b0cb" quantity="-3000"/>
              <sld:ColorMapEntry color="#37bad8" quantity="-2000"/>
              <sld:ColorMapEntry color="#5fc2d9" quantity="-1000"/>
              <sld:ColorMapEntry color="#86cbd2" quantity="-500"/>
              <sld:ColorMapEntry color="#afdad1" quantity="-200"/>
              <sld:ColorMapEntry color="#afdabc" quantity="-100"/>
              <sld:ColorMapEntry color="#ccdab4" quantity="-50"/>
              <sld:ColorMapEntry color="#d0d0ac" quantity="-25"/>
              <sld:ColorMapEntry color="#e0b0a0" quantity="-10"/>
              <sld:ColorMapEntry color="#e0b0a0" quantity="-1"/>
              <sld:ColorMapEntry color="#ffffff" opacity="0" quantity="0"/>
            </sld:ColorMap>
          </sld:RasterSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>
