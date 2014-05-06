<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
<html>
	<head>
		<link href="../css/pattern.css" rel="stylesheet" type="text/css"/>
		<title><xsl:value-of select="pattern/@name"/></title>
	</head>
	<body>
		<h2><xsl:value-of select="pattern/@name"/></h2>
		<div id="priority"><xsl:value-of select="pattern/@priority"/></div>
<!-- 
		<p>Language(s) : <xsl:value-of select="pattern/language"/></p>
 -->
		<div id='description'>
			<xsl:apply-templates/>
		</div>
	</body>
</html>
</xsl:template>

<xsl:template match="/component">
	<xsl:copy-of select="description/*"/>
</xsl:template>

</xsl:stylesheet>
