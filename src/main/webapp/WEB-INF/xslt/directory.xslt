<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<xsl:stylesheet
    version="3.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>
    <xsl:output method="xml" version="1.1" encoding="UTF-8"/>

    <xsl:param name="dir"/>

    <xsl:mode on-no-match="shallow-copy"/>

    <xsl:template match="ListN">
        <xsl:element name="html" namespace="http://www.w3.org/1999/xhtml">
            <xsl:element name="head" namespace="http://www.w3.org/1999/xhtml">
            </xsl:element>
            <xsl:element name="body" namespace="http://www.w3.org/1999/xhtml">
                <xsl:element name="ul" namespace="http://www.w3.org/1999/xhtml">
                    <xsl:apply-templates select="@* | node()"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>


    <xsl:template match="item">
        <xsl:element name="li" namespace="http://www.w3.org/1999/xhtml">
            <xsl:element name="a" namespace="http://www.w3.org/1999/xhtml">
                <xsl:attribute name="href">
                    <xsl:value-of select="link/text()"/>
                </xsl:attribute>
                <xsl:value-of select="display/text()"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>
