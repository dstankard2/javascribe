<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:pattern="http://docs.javascribe.com/pattern">

<xsl:template match="/">
<html>
<head>
<title><xsl:value-of select="/pattern:pattern/@name"/></title>

<link href="../css/pattern.css" rel="stylesheet" type="text/css" />

</head>
<body>
<xsl:apply-templates/>
</body>
</html>
</xsl:template>

<xsl:template match="/pattern:pattern">
<div class='pattern-page'>
<h1><xsl:value-of select="./@name"/></h1>
<div id='pattern-description'>
<xsl:value-of select="./pattern:description"/>

</div>
<div id='attribute-display'>
<h3>XML Attributes</h3>
<table border='1'>
<thead>
<tr>
<th>Name</th><th>Required</th><th>Description</th><th>Example Value</th>
</tr>
</thead>
<tbody>
<xsl:for-each select="./pattern:attribute">
<tr>
<td><xsl:value-of select="./@name" /></td>
<td><xsl:value-of select="./@required" /></td>
<td><xsl:value-of select="./pattern:description" /></td>
<td><xsl:value-of select="./@example" /></td>
</tr>
</xsl:for-each>
</tbody>
</table>
</div>

<div id='propertyDisplay'>
<h3>Configuration Properties</h3>

<table border='1'>
<thead>
<tr>
<th>Name</th><th>Required</th><th>Description</th><th>Example Value</th>
</tr>
</thead>
<tbody>
<xsl:for-each select="./pattern:property">
<tr>
<td><xsl:value-of select="./@name" /></td>
<td><xsl:value-of select="./@required" /></td>
<td><xsl:value-of select="./pattern:description" /></td>
<td><xsl:value-of select="./@example" /></td>
</tr>
</xsl:for-each>
</tbody>
</table>

</div>
<div id='long-explanation'>

<xsl:value-of select="./pattern:longExplanation" />

</div>
</div>
</xsl:template>

</xsl:stylesheet>

