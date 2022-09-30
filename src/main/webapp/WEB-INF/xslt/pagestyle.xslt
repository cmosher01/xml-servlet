<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<xsl:stylesheet
        version="3.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xhtml="http://www.w3.org/1999/xhtml"
>
    <!--
        Adds link/stylesheet and class attribute to HTML page
    -->
    <xsl:output method="xml" version="1.1" encoding="UTF-8"/>

    <xsl:param name="nu.mine.mosher.xml.pageclass"/>
    <xsl:param name="nu.mine.mosher.xml.stylesheet"/>
    <xsl:param name="nu.mine.mosher.xml.script"/>

    <xsl:mode on-no-match="shallow-copy"/>

    <xsl:template match="/xhtml:html">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="class">
                <xsl:value-of select="$nu.mine.mosher.xml.pageclass"/>
            </xsl:attribute>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="/xhtml:html/xhtml:head">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates/>
            <xsl:element name="link" namespace="http://www.w3.org/1999/xhtml">
                <xsl:attribute name="rel">
                    <xsl:value-of select="'stylesheet'"/>
                </xsl:attribute>
                <xsl:attribute name="href">
                    <xsl:value-of select="$nu.mine.mosher.xml.stylesheet"/>
                </xsl:attribute>
            </xsl:element>
            <xsl:element name="script" namespace="http://www.w3.org/1999/xhtml">
                <xsl:attribute name="src">
                    <xsl:value-of select="$nu.mine.mosher.xml.script"/>
                </xsl:attribute>
                <xsl:attribute name="async">
                    <xsl:value-of select="'async'"/>
                </xsl:attribute>
            </xsl:element>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
