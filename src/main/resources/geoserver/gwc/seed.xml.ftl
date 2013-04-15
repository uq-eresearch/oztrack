<?xml version="1.0" encoding="UTF-8"?>
<seedRequest>
  <name>${layerName}</name>
  <!-- Example: seed, reseed, truncate -->
  <type>${requestType}</type>
  <!-- Example: EPSG:4326, EPSG:900913 -->
  <gridSetId>${gridSetId}</gridSetId>
  <#if (minX?? && maxX?? && minY?? && maxY??)>
  <bounds>
    <coords>
      <double>${minX}</double>
      <double>${maxX}</double>
      <double>${minY}</double>
      <double>${maxY}</double>
    </coords>
  </bounds>
  </#if>
  <!-- Example: 00 -->
  <zoomStart>${zoomStart}</zoomStart>
  <!-- Example: 20 -->
  <zoomStop>${zoomStop}</zoomStop>
  <!-- Example: image/png, image/jpeg -->
  <format>${imageFormat}</format>
  <!-- Example: STYLES, CQL_FILTER -->
  <#if (styleName??)>
  <parameters>
    <entry>
      <string>STYLES</string>
      <string>${styleName}</string>
    </entry>
  </parameters>
  </#if>
  <!-- Number of seeding threads to run in parallel. -->
  <!-- If type == truncate only one thread will be used regardless of this parameter. -->
  <threadCount>${threadCount}</threadCount>
</seedRequest>