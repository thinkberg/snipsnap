<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="text" encoding="iso-8859-1" media-type="text/plain"/>

  <xsl:template match="snipspace">
    Found <xsl:value-of select="count(user)"/> users.
    Found <xsl:value-of select="count(snip)"/> snips.
    Checking validity of users:
    <xsl:for-each select="user">
      <xsl:sort select="login"/>
      <xsl:variable name="id"><xsl:value-of select="login"/></xsl:variable>
      <xsl:if test="count(/snipspace/user[login=$id]) &gt; 1">
        <xsl:text>! User</xsl:text> "
        <xsl:value-of select="login"/>" appears <xsl:value-of select="count(/snipspace/user[login=$id])"/> times, check double entries.
      </xsl:if>

    </xsl:for-each>
  </xsl:template>

  <xsl:template match="user">
    <xsl:variable name="id">
      <xsl:value-of select="login"/>
    </xsl:variable>
    <xsl:if test="count(/snipspace/user[login=$id]) &gt; 1">
      <xsl:text>! User</xsl:text> "
      <xsl:value-of select="login"/>" appears <xsl:value-of select="count(/snipspace/user[login=$id])"/> times, check double entries.
    </xsl:if>
  </xsl:template>

  <xsl:template match="snip">
    <xsl:variable name="id">
      <xsl:value-of select="name"/>
    </xsl:variable>
    <xsl:if test="count(/snipspace/snip[name=$id]) &gt; 1">
      <xsl:text>! Snip</xsl:text> "
      <xsl:value-of select="name"/>" appears <xsl:value-of select="count(/snipspace/snip[name=$id])"/> times, check double entries.
    </xsl:if>
    <xsl:if test="starts-with($id, 'comment-')">
      <xsl:if test="string-length(commentSnip) = 0">
        <xsl:text>Snip</xsl:text> "
        <xsl:value-of select="name"/>" needs 'commentSnip' tag!
      </xsl:if>
      <!-- does not work
      <xsl:if test="string-length(commentSnip) &gt; 0 and not(commentSnip = substring-before(substring-after(name, '-'), '-'))">
        <xsl:text>Snip</xsl:text> "<xsl:value-of select="name"/>" has incorrect 'commentSnip' tag <xsl:value-of select="commentSnip"/>~<xsl:value-of select="substring-before(substring-after(name, '-'), '-')"/>!
      </xsl:if>
      -->
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
