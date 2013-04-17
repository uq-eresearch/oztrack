<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor
  version="1.0.0"
  xmlns="http://www.opengis.net/sld"
  xmlns:ogc="http://www.opengis.net/ogc"
  xmlns:xlink="http://www.w3.org/1999/xlink"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd">
  <NamedLayer>
    <Name>elevation</Name>
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
              <ColorMapEntry color="#00a900" quantity="-1" label="-1 m" opacity="0"/>
              <ColorMapEntry color="#00a900" quantity="0" label="0 m"/>
              <ColorMapEntry color="#43b518" quantity="250" label="250 m"/>
              <ColorMapEntry color="#87bf31" quantity="500" label="500 m"/>
              <ColorMapEntry color="#cccc4b" quantity="750" label="750 m"/>
              <ColorMapEntry color="#ffff7f" quantity="1000" label="1000 m"/>
              <ColorMapEntry color="#e7df71" quantity="1500" label="1500 m"/>
              <ColorMapEntry color="#cfc066" quantity="2000" label="2000 m"/>
              <ColorMapEntry color="#b7a158" quantity="2500" label="2000 m"/>
              <ColorMapEntry color="#9f824b" quantity="3000" label="3000 m"/>
              <ColorMapEntry color="#87633f" quantity="4000" label="4000 m"/>
              <ColorMapEntry color="#582525" quantity="5000" label="5000 m"/>
            </ColorMap>
          </RasterSymbolizer>
        </Rule>
      </FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>