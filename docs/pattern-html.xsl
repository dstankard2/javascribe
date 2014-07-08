<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:pattern="http://docs.javascribe.com/pattern"
xsi:schemaLocation="http://docs.javascribe.com/pattern pattern.xsd"
>

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
<br/>
<div class='section-banner'>Description</div>
<br/>
<div id='pattern-description'>

<xsl:copy-of select="./pattern:description" />

<p><b>Priority</b>: <xsl:value-of select="./@priority" /></p>
<p><b>XML Namespace</b>: <xsl:value-of select="./@namespace" /></p>
</div>

<div id='propertyDisplay'>

<br/>
<div class='section-banner'>Configuration Properties</div>
<br/>
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
<td class='text-cell'><xsl:value-of select="./pattern:description" /></td>
<td><xsl:value-of select="./@example" /></td>
</tr>
</xsl:for-each>
</tbody>
</table>

</div>

<div id='attribute-display'>

<br/>
<br/>
<div class='section-banner'>XML Attributes</div>
<br/>

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
<td class='text-cell'><xsl:value-of select="./pattern:description" /></td>
<td><xsl:value-of select="./@example" /></td>
</tr>
</xsl:for-each>
</tbody>
</table>
</div>
<xsl:if test="./pattern:longExplanation">
<br/>
<br/>
<div class='section-banner'>Other Explanation</div>
<div id='long-explanation'>

<xsl:copy-of select="./pattern:longExplanation" />

</div>

</xsl:if>

<xsl:if test="./pattern:examples">
<br/>
<br/>

<div id='examples-space'>

<div class='section-banner'>Examples</div>

<xsl:for-each select="./pattern:examples/pattern:example">

<div class='example'>
<p><xsl:value-of select="./@desc" /></p>

<div class='example-comp'>

<xsl:copy-of select="./pattern:exampleComp/*" />

</div>

<div class='example-explanation'>
<xsl:copy-of select="./pattern:explanation" />
</div>

<xsl:if test="./pattern:systemAttributes/pattern:systemAttribute">

<div class='system-attributes'>

<div class='secondary-banner'>System Attributes</div>
<br/>
<table border='1' class='example-system-attributes-table'>

<thead>
<tr><th>Attribute Name</th><th>Attribute Type</th><th>Description</th>
</tr>
</thead>
<tbody>

<xsl:for-each select="./pattern:systemAttributes/pattern:systemAttribute">

<tr>
<td><xsl:value-of select="./@name" /></td>
<td><xsl:value-of select="./@type" /></td>
<td><xsl:value-of select="./@desc" /></td>
</tr>

</xsl:for-each>

</tbody>
</table>

</div>

</xsl:if>

<xsl:if test="./pattern:variableTypes/pattern:variableType">

<div class='example-variable-types'>

<br/>
<div class='secondary-banner'>Variable Types</div>
<br/>

<table border='1' class='example-variable-types-table'>
<thead>
<tr><th>Type Name</th><th>Explanation</th>
</tr>
<tbody>

<xsl:for-each select="./pattern:variableTypes/pattern:variableType">
<tr>
<td><xsl:value-of select="./@name" /></td>
<td>
<xsl:choose>
	<xsl:when test='./@stereotype="AttributeHolder"'>
An attribute holder has attributes which can be referenced via dot notation ("a.b" references the "b" attribute of object "a")	
	</xsl:when>
	<xsl:when test='./@stereotype="JavaService"'>
A Java service is a class that is instantiated via a default constructor.  
It has one or more business operations that can be invoked by other components.
	</xsl:when>
	<xsl:when test='./@stereotype="LocatedJavaService"'>
A located Java service is a class that is instantiated via a service locator.
It has one or more business operations that can be invoked by other components.
	</xsl:when>
	<xsl:otherwise>
		<b>ERROR: Found an unsupported stereotype "<xsl:value-of select="./@stereotype" />"</b>
	</xsl:otherwise>
</xsl:choose>
</td>
</tr>
</xsl:for-each>

</tbody>

</thead>
</table>


</div>

</xsl:if>

<xsl:if test="./pattern:generatedFiles/pattern:file">
<br/>
<div class='example-generated-files'>

<br/>
<div class='secondary-banner'>Generated Files</div>
<br/>

<xsl:for-each select="./pattern:generatedFiles/pattern:file">

<div class='example-generated-file'>
<div class='name'>
<xsl:value-of select="./@name" />
</div>
<div class='information'>

<xsl:copy-of select="." />

</div>

</div>

</xsl:for-each>

</div>

</xsl:if>

</div>

</xsl:for-each>

</div>

</xsl:if>

</div>
</xsl:template>

</xsl:stylesheet>

