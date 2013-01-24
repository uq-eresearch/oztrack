<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor
  version="1.0.0"
  xmlns="http://www.opengis.net/sld"
  xmlns:ogc="http://www.opengis.net/ogc"
  xmlns:xlink="http://www.w3.org/1999/xlink"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd">
  <NamedLayer>
    <Name>bathymetry</Name>
    <UserStyle>
      <FeatureTypeStyle>
        <Rule>
          <RasterSymbolizer>
            <ChannelSelection>
              <GrayChannel>
                <SourceChannelName>1</SourceChannelName>
              </GrayChannel>
            </ChannelSelection>
            <ColorMap extended="true" type="ramp">
              <ColorMapEntry color="#181736" quantity="-8000"/>
              <ColorMapEntry color="#142d66" quantity="-7000"/>
              <ColorMapEntry color="#155787" quantity="-6000"/>
              <ColorMapEntry color="#158aab" quantity="-5000"/>
              <ColorMapEntry color="#1b9bb8" quantity="-4000"/>
              <ColorMapEntry color="#01b0cb" quantity="-3000"/>
              <ColorMapEntry color="#37bad8" quantity="-2000"/>
              <ColorMapEntry color="#5fc2d9" quantity="-1000"/>
              <ColorMapEntry color="#86cbd2" quantity="-500"/>
              <ColorMapEntry color="#afdad1" quantity="-200"/>
              <ColorMapEntry color="#afdabc" quantity="-100"/>
              <ColorMapEntry color="#afdabc" quantity="-1"/>
              <ColorMapEntry color="#00a900" quantity="0" opacity="0"/>
            </ColorMap>
          </RasterSymbolizer>
        </Rule>
      </FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>