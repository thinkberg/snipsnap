<?xml version="1.0" encoding="iso-8859-1" ?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

 <!-- including other files -->
 <xsl:template match="include">
  <xsl:apply-templates select="document(concat('../', @file))"/>
 </xsl:template>

 <!-- special textual elements -->
 <xsl:template match="company" name="company">Fraunhofer FIRST</xsl:template>
 <xsl:template match="companylong" name="companylong">Fraunhofer FIRST</xsl:template>
 <xsl:template match="product">\textsf{Snip\textbf{Snap}}</xsl:template>
 <xsl:template match="eur">\EUR\/</xsl:template>
 <xsl:template match="bs">\textbackslash</xsl:template>
 <xsl:template match="dot">\ding{109}</xsl:template>
 <xsl:template match="fulldot">\ding{108}</xsl:template>
 <xsl:template match="halfdot">\ding{119}</xsl:template>
 <xsl:template match="newpage">\pagebreak[4]</xsl:template>

 <!-- text rendering -->
 <xsl:template match="cite">\cite{<xsl:value-of select="."/>}</xsl:template>
 <xsl:template match="bibliography">
 \begin{thebibliography}{99}
   <xsl:apply-templates/>
 \end{thebibliography}</xsl:template>
 <xsl:template match="bibitem">\bibitem{<xsl:value-of select="@ref"/>} <xsl:value-of select="."/>
 \ </xsl:template>
 <xsl:template match="shell">\begin{verbatim}<xsl:value-of select="."/>\end{verbatim}</xsl:template>
 <xsl:template match="source">
\begin{Verbatim}[gobble=<xsl:value-of select="@gobble"/>,frame=single,numbers=left,fontsize=\small]
<xsl:value-of select="."/>
\end{Verbatim}   
 </xsl:template>
 <xsl:template match="important">\textbf{<xsl:value-of select="."/>}</xsl:template>
 <xsl:template match="br">\\</xsl:template>
 <xsl:template match="i">\textit{<xsl:apply-templates/>}</xsl:template>
 <xsl:template match="b">\textbf{<xsl:apply-templates/>}</xsl:template>
 <xsl:template match="sup">\textsuperscript{<xsl:apply-templates/>}</xsl:template>
 <xsl:template match="quote"><xsl:if test="@ref">\textsc{<xsl:value-of select="@ref"/>}: </xsl:if>\glqq<xsl:text> </xsl:text><xsl:value-of select="."/>\grqq\/</xsl:template>

 <xsl:template match="knowledge">\textsc{<xsl:value-of select="."/>}</xsl:template>
 <xsl:template match="footnote">\footnote{<xsl:apply-templates/>}</xsl:template>

 <!-- References -->
 <xsl:template match="ref">\ref{<xsl:value-of select="@name"/>}</xsl:template>

 <!-- list rendering -->
 <xsl:template match="list">
  <xsl:choose>
   <xsl:when test="@type = 'enumerated'">
    \begin{dingautolist}{172}
     <xsl:apply-templates select="item"/>
    \end{dingautolist}
   </xsl:when>
   <xsl:when test="@type = 'description'">
    \begin{description}
     <xsl:apply-templates select="item"/>
    \end{description}
   </xsl:when>
   <xsl:when test="@type = 'ding'">
    \begin{tabbing}
     \quad \= <xsl:value-of select="item[1]/@ding"/>\quad \= <xsl:value-of select="item[1]"/> \kill
     <xsl:for-each select="item">
      \&gt; <xsl:value-of select="@ding"/> \&gt; <xsl:apply-templates select="."/> \\
     </xsl:for-each>
    \end{tabbing}
   </xsl:when>
   <xsl:otherwise>
    \begin{itemize}
     <xsl:apply-templates select="item"/>
    \end{itemize}
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>

 <xsl:template match="item">
  <xsl:choose>
    <xsl:when test="@ding"></xsl:when>
    <xsl:when test="@title">
      \item \textbf{<xsl:value-of select="@title"/>}
    </xsl:when>
    <xsl:when test="title">
      \item[{\bf\sf <xsl:apply-templates select="title"/>}]
    </xsl:when>
    <xsl:otherwise>\item </xsl:otherwise>
  </xsl:choose>
  <xsl:text> </xsl:text>
  <xsl:apply-templates select="child::node()[not(self::title)]"/>
 </xsl:template>

 <!-- END: list rendering -->


 <!-- table rendering -->
 <xsl:template match="table">
  <xsl:if test="string-length(translate(@alignment,'|{}1234567890m','')) != count((head|row)[1]/*)">
   <xsl:message>
    "<xsl:value-of select="@alignment"/>" does not match amount of cells: <xsl:value-of select="count(head/*)"/>
   </xsl:message>
  </xsl:if>
  \begin{center}
  \centering
	<xsl:choose>
	  <xsl:when test="@long">
      \begin{longtable}{<xsl:value-of select="@alignment"/>}
      <xsl:apply-templates select="head"/>
      <xsl:if test="head">
      \hline
      </xsl:if>
      <xsl:apply-templates select="row|line"/>
      \end{longtable}
		</xsl:when>
		<xsl:otherwise>
      \begin{tabular}{<xsl:value-of select="@alignment"/>}
      <xsl:apply-templates select="head"/>
      <xsl:if test="head">
      \hline
      </xsl:if>
      <xsl:apply-templates select="row|line"/>
      \end{tabular}
	  </xsl:otherwise>
	 </xsl:choose>
   \end{center}
 </xsl:template>

 <xsl:template match="head">
  <xsl:for-each select="cell">
   \textbf{<xsl:apply-templates select="."/>} <xsl:if test="position()!=last()"> &amp;</xsl:if>
  </xsl:for-each> 
	<xsl:choose>
	<xsl:when test="../@long"> \endhead </xsl:when>
	<xsl:otherwise> \\ </xsl:otherwise>
	</xsl:choose>
 </xsl:template>

 <xsl:template match="row">
  <xsl:for-each select="cell">
   <xsl:apply-templates select="."/>
   <xsl:if test="position()!=last()"> &amp;</xsl:if>
  </xsl:for-each> \\
 </xsl:template>

 <xsl:template match="line">
  \hline
 </xsl:template>
 <!-- END: table rendering -->

 <!-- image rendering -->

 <xsl:template match="image">
   \begin{figure}[ht]
  \centering
    \includegraphics[keepaspectratio,width=<xsl:value-of select="@width"/>]{images/<xsl:value-of select="@file"/>}
     <xsl:if test="@caption">
       {\centering\small\textsf <xsl:apply-templates select="@caption"/>}
     </xsl:if>
   <xsl:if test="@label">
     \label{<xsl:apply-templates select="@label"/>}
   </xsl:if>
\end{figure}
 </xsl:template>

</xsl:stylesheet>
