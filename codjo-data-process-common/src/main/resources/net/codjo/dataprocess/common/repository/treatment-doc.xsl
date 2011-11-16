<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
        <html>
            <body bgcolor="#ffcccc">
                <h2>Liste des traitements/controles</h2>
                <xsl:apply-templates select="//root/treatment"/>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="treatment">
        <table border="1" width="100%" height="0%">
            <tr bgcolor="#99ccff">
                <th width="10%">
                    <xsl:value-of select="@id"/>
                </th>
                <th width="90%" colspan="2" align="left">
                    <xsl:value-of select="title"/>
                </th>
            </tr>
            <tr>
                <td></td>
                <td colspan="2">
                    <b>Type :</b>
                    <xsl:value-of select="@type"/>
                </td>
            </tr>
            <tr>
                <td></td>
                <td colspan="2">
                    <b>Scope :</b>
                    <xsl:value-of select="@scope"/>
                </td>
            </tr>
            <tr>
                <td></td>
                <td colspan="2">
                    <b>Commentaire :</b>
                    <xsl:value-of select="comment"/>
                </td>
            </tr>
            <tr>
                <td></td>
                <td colspan="2">
                    <b>Target :</b>
                    <xsl:value-of select="target"/>
                </td>
            </tr>
            <tr>
                <td></td>
                <td colspan="2">
                    <b>Gui-target :</b>
                    <xsl:value-of select="gui-target"/>
                </td>
            </tr>
            <xsl:if test="arguments/arg">
                <tr>
                    <td></td>
                    <td width="10%" valign="top">
                        <b>Arguments</b>
                    </td>
                    <td width="90%">
                        <table border="1" width="100%" height="0%">
                            <th>Nom</th>
                            <th>Valeur</th>
                            <th>position</th>
                            <th>Type</th>
                            <xsl:for-each select="arguments/arg">
                                <tr>
                                    <td>
                                        <xsl:value-of select="@name"/>
                                    </td>
                                    <td>
                                        <xsl:value-of select="@value"/>
                                    </td>
                                    <td>
                                        <xsl:value-of select="@position"/>
                                    </td>
                                    <td>
                                        <xsl:value-of select="@type"/>
                                    </td>
                                </tr>
                            </xsl:for-each>
                        </table>
                    </td>
                </tr>
            </xsl:if>
        </table>
        <br></br>

        <!--
          <tr>
                <h2>Arguments</h2>
                <table border="1">
                  <tr bgcolor="#9acd32">
                    <th>Nom</th>
                    <th>valeur</th>
                    <th>Position</th>
                    <th>Type</th>
                  </tr>
        <xsl:for-each select="arguments/arg">
          <tr>
          <td><b><xsl:value-of select="@name"/></b></td>
          <td><b><xsl:value-of select="@value"/></b></td>
          <td><b><xsl:value-of select="@position"/></b></td>
          <td><b><xsl:value-of select="@type"/></b></td>
          </tr>
        </xsl:for-each>
         -->

    </xsl:template>

</xsl:stylesheet>


