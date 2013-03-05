<?xml version="1.0" encoding="UTF-8"?>
<layer>
  <name>${layerName}</name>
  <type>RASTER</type>
  <#if defaultStyle??>
  <defaultStyle>
    <name>${defaultStyle}</name>
  </defaultStyle>
  </#if>
  <#if styles??>
  <styles>
    <#list styles as style>
    <style>
      <name>${style}</name>
    </style>
    </#list>
  </styles>
  </#if>
  <resource class="coverage">
    <name>${coverageName}</name>
  </resource>
  <enabled>true</enabled>
</layer>