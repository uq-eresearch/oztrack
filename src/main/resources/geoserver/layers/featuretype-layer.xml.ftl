<?xml version="1.0" encoding="UTF-8"?>
<layer>
  <name>${layerName}</name>
  <type>VECTOR</type>
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
  <resource class="featureType">
    <name>${featuretypeName}</name>
  </resource>
  <enabled>true</enabled>
</layer>