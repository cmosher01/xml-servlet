<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<xsl:stylesheet
    version="3.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
>
    <!--
    Prepends absolute a/href and link/href attributes with the proxy path prefix.
    -->
    <xsl:output method="xml" version="1.1" encoding="UTF-8"/>

    <xsl:param name="nu.mine.mosher.xml.pathPrefix"/>

    <xsl:mode on-no-match="shallow-copy"/>

    <xsl:template match="xhtml:a/@href[fn:starts-with(.,'/') and not(fn:starts-with(.,'//'))]">
        <xsl:attribute name='href'>
            <xsl:value-of select="fn:concat($nu.mine.mosher.xml.pathPrefix,.)"/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="xhtml:link/@href[fn:starts-with(.,'/') and not(fn:starts-with(.,'//'))]">
        <xsl:attribute name='href'>
            <xsl:value-of select="fn:concat($nu.mine.mosher.xml.pathPrefix,.)"/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="xhtml:script/@src[fn:starts-with(.,'/') and not(fn:starts-with(.,'//'))]">
        <xsl:attribute name='src'>
            <xsl:value-of select="fn:concat($nu.mine.mosher.xml.pathPrefix,.)"/>
        </xsl:attribute>
    </xsl:template>
</xsl:stylesheet>
