<?xml version="1.0" encoding="iso-8859-1" ?>

<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp "&#160;">
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 <xsl:import href="helper.xsl"/>
 <xsl:output method="text" encoding="iso-8859-1"/>
 <xsl:strip-space elements="*"/>

 <xsl:template match="/">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="address"/>

 <xsl:template match="document">
  \documentclass[a4paper,pdftex]{article}
  <xsl:apply-templates select="header"/>
  \newpage
  \pagestyle{empty}
  \tableofcontents
  \newpage
  \pagestyle{fancy}
  \setcounter{page}{1}  %% Titelseite nicht mitzaehlen
  <xsl:apply-templates select="include|section"/>
  <xsl:apply-templates select="bibliography"/>
  \end{document}
 </xsl:template>

 <!-- document header -->
 <xsl:template match="header">
   \usepackage{titlesec}
   \usepackage{wrapfig}
   \usepackage{fancyvrb}
   \usepackage{amsfonts}
   \usepackage{palatino}
   \usepackage{lastpage}
   \usepackage{color}
   \usepackage{t1enc}
   \usepackage[isolatin]{inputenc}
   \usepackage[pdftex]{graphicx}
   \usepackage{fancyhdr}
   \usepackage{endnotes}
             
  \lhead{\itshape \subsectionmark}
  \chead{} \rhead{\itshape Page \thepage{} of \pageref{LastPage}}
  \renewcommand{\headrulewidth}{0pt}
  \lfoot{\mbox{}\\ \itshape SnipSnap User Guide}
  \cfoot{}
  \rfoot{\mbox{}\\ \itshape <xsl:value-of select="date"/>}

  \begin{document}
  \thispagestyle{empty}

  {\raggedright\vspace{15cm}{
   \begin{figure}[ht]
    \includegraphics[keepaspectratio,width=3cm]{images/SnipSnapLogo.pdf}
   \end{figure}
   \huge\bfseries\sffamily{<xsl:apply-templates select="title"/>}}\\\vspace{0.5cm}
   \normalsize\itshape <xsl:apply-templates select="author"/>\\
   <xsl:value-of select="date"/>\\\vspace{14cm}}
   <xsl:apply-templates select="contact"/>
 </xsl:template>

 <xsl:template match="contact">
   \fbox{\parbox{\textwidth}{
   \textbf{Contact:} \\
   \begin{tabbing}
   Stephan J. Schmidt\= \quad \qquad \qquad \=
   Matthias L. Jugel \\
   stephan@mud.de \> \>
   leo@mud.de\\
   \end{tabbing}
   }}
 </xsl:template>

 <!-- document structure -->
 <xsl:template match="section">
  <xsl:choose>
   <xsl:when test="parent::section">
    \subsection{<xsl:value-of select="@title"/>}
   </xsl:when>
   <xsl:otherwise>
    <xsl:choose>
     <xsl:when test="@title">
      \section{<xsl:value-of select="@title"/>}
     </xsl:when>
     <xsl:otherwise>
      \section{<xsl:apply-templates select="child::title/node()"/>}
     </xsl:otherwise>
    </xsl:choose>
   </xsl:otherwise>
  </xsl:choose>
  <xsl:if test="@name">
   \label{<xsl:value-of select="@name"/>}
  </xsl:if>
  <xsl:apply-templates select="child::node()[not(self::title)]"/>

 </xsl:template>

 <xsl:template match="subsection">
  <xsl:choose>
   <xsl:when test="@title">
    \subsection{<xsl:value-of select="@title"/>}
   </xsl:when>
   <xsl:otherwise>
    \subsection{<xsl:apply-templates select="child::title/node()"/>}
   </xsl:otherwise>
  </xsl:choose>
  <xsl:if test="@name">
   \label{<xsl:value-of select="@name"/>}
  </xsl:if>
  <xsl:apply-templates select="child::node()[not(self::title)]"/>
 </xsl:template>

 <xsl:template match="subsubsection">
  <xsl:choose>
   <xsl:when test="@title">
    \subsubsection{<xsl:value-of select="@title"/>}
   </xsl:when>
   <xsl:otherwise>

    \subsubsection{<xsl:apply-templates select="child::title/node()"/>}
   </xsl:otherwise>
  </xsl:choose>
  <xsl:if test="@name">
   \label{<xsl:value-of select="@name"/>}
  </xsl:if>
  \nopagebreak[4]<xsl:text> 
	
	</xsl:text><xsl:apply-templates select="child::node()[not(self::title)]"/>
 </xsl:template>

 <xsl:template match="usecase">
   <!--
   \begin{figure}
    {\small
     \colorbox{darkgrey}{
      \parbox{\textwidth}{\raggedright
       \color{black}\textbf{Fallbeispiel \theexample: <xsl:apply-templates select="@title"/>}}}

     \colorbox{grey}{
      \parbox{\textwidth}{
   \textbf{Unternehmen} \\ <xsl:apply-templates select="company/child::node()"/> \\[0.3cm]
   \textbf{Motivation} \\ <xsl:apply-templates select="motivation"/> \\[0.3cm]
   \textbf{Vorteil} \\ <xsl:apply-templates select="advantage"/> \\[0.3cm]
     }}
   }
   \addtocounter{example}{1}
   \end{figure}
   -->
   \textbf{Fallbeispiel \theexample: <xsl:apply-templates select="@title"/>}

   \textbf{Unternehmen} \\ <xsl:apply-templates select="company/child::node()"/> \\[0.3cm]
   \textbf{Motivation} \\ <xsl:apply-templates select="motivation"/> \\[0.3cm]
   \textbf{Vorteil} \\ <xsl:apply-templates select="advantage"/> \\[0.3cm]
   \addtocounter{example}{1}
 </xsl:template>

</xsl:stylesheet>
